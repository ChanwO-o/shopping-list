package com.androiddevs.shoppinglisttestingyt

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.core.util.Preconditions
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi

// HiltExtension file: utility function for Dagger-Hilt. Can call from anywhere
@ExperimentalCoroutinesApi
// inline: lambda functions in Kotlin made more efficient. Compiler won't make this into an actual function, instead will copy body into places where it is called
// reified keyword: makes type known at compile time so that we can access properties of Fragment class
inline fun <reified T : Fragment> launchFragmentInHiltContainer(
    fragmentArgs: Bundle? = null,
    themeResId: Int = R.style.FragmentScenarioEmptyFragmentActivityTheme,

    // attaching FragmentFactory allows us to use constructor injection into fragments
    fragmentFactory: FragmentFactory? = null,

    // lambda function used to get reference to fragment that we launched in this Hilt container. crossinline: keyword for using lambdas inside inline functions
    crossinline action: T.() -> Unit = {}
) {
    // intent that starts our activity in which to attach our fragment in
    // serves as MainActivity
    val mainActivityIntent = Intent.makeMainActivity(
        ComponentName(
            ApplicationProvider.getApplicationContext(),
            HiltTestActivity::class.java
        )
    ).putExtra(FragmentScenario.EmptyFragmentActivity.THEME_EXTRAS_BUNDLE_KEY, themeResId)

    // create Fragment and attach it
    ActivityScenario.launch<HiltTestActivity>(mainActivityIntent).onActivity { activity -> // activity: reference to Activity that just started
        // attach the FragmentFactory
        fragmentFactory?.let{
            activity.supportFragmentManager.fragmentFactory = it
        }
        val fragment = activity.supportFragmentManager.fragmentFactory.instantiate( // instantiate Fragment
            Preconditions.checkNotNull(T::class.java.classLoader),
            T::class.java.name
        )
        fragment.arguments = fragmentArgs

        // fragment transaction
        activity.supportFragmentManager.beginTransaction()
            .add(android.R.id.content, fragment, "")
            .commitNow()

        // use action lambda function with our fragment
        (fragment as T).action()
    }
}