package com.androiddevs.shoppinglisttestingyt.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.androiddevs.shoppinglisttestingyt.adapters.ImageAdapter
import com.androiddevs.shoppinglisttestingyt.adapters.ShoppingItemAdapter
import com.androiddevs.shoppinglisttestingyt.repositories.FakeShoppingRepositoryAndroidTest
import com.bumptech.glide.RequestManager
import javax.inject.Inject

/**
 * Test Fragment Factory that uses FakeShoppingRepository. Used for tests only
 */
class TestShoppingFragmentFactory @Inject constructor(
    private val imageAdapter : ImageAdapter,
    private val glide: RequestManager,
    private val shoppingItemAdapter: ShoppingItemAdapter
) : FragmentFactory() {

    // called when fragment is instantiated
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when(className) { // create different fragment depending on className
            ImagePickFragment::class.java.name -> ImagePickFragment(imageAdapter)
            AddShoppingItemFragment::class.java.name -> AddShoppingItemFragment(glide)
            ShoppingFragment::class.java.name -> ShoppingFragment(
                shoppingItemAdapter,
                ShoppingViewModel(FakeShoppingRepositoryAndroidTest())
            )
            else -> super.instantiate(classLoader, className)
        }
    }
}