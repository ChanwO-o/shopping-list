package com.androiddevs.shoppinglisttestingyt.ui

import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import com.androiddevs.shoppinglisttestingyt.R
import com.androiddevs.shoppinglisttestingyt.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@MediumTest // integration tests are medium tests
@HiltAndroidTest
@ExperimentalCoroutinesApi
class ShoppingFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setup() {
        hiltRule.inject()
    }

    // test doubles (fakes, mocks)
    // Mock: version of class that doesn't do anything. Has function signatures but no implementation
    // tracks how often what is called, can define what values it should return in each case

    @Test
    fun clickAddShoppingItemButton_navigateToAddShoppingItemFragment() {
        val navController = mock(NavController::class.java) // mock NavController obj
        // launch instance of a fragment, replace its NavController with our mock NavController
        launchFragmentInHiltContainer<ShoppingFragment> {
            Navigation.setViewNavController(requireView(), navController)
        }

        // simulate click on floating action button
        Espresso.onView(withId(R.id.fabAddShoppingItem)).perform(click())

        // verify that navigate function of NavController obj was called with right params
        verify(navController).navigate(
            ShoppingFragmentDirections.actionShoppingFragmentToAddShoppingItemFragment()
        )
    }
}