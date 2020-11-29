package com.androiddevs.shoppinglisttestingyt.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.androiddevs.shoppinglisttestingyt.getOrAwaitValue
import com.androiddevs.shoppinglisttestingyt.launchFragmentInHiltContainer
import com.androiddevs.shoppinglisttestingyt.ui.ShoppingFragment
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi // add this to remove warning in test cases below
//@RunWith(AndroidJUnit4::class) // tell JUnit these are instrmented tests (requiring emulator/device) (removed cuz we now use our own Test Runner
@SmallTest // unit test category (small, medium, large) not necessary annotation
@HiltAndroidTest // specify to Hilt we want to inject dependencies into this test class
class ShoppingDaoTest { // test Room database

    // rule that allows Hilt to inject stuff into test class. make sure this is the first rule if there's multiple rules in a class
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    // LiveData is asynchronous by default, even though we use runBlockingTest{} JUnit won't allow it.
    // This rule tells JUnit to run tests in this class one after another (basically on the same thread)
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("test_db") // must specify this because it doesn't know whether ShoppingItemDatabase is from app module or test app module
    lateinit var database: ShoppingItemDatabase

    private lateinit var dao: ShoppingDao

    @Before
    fun setup() {
        // instead of initializing database every time like in this commented code block, using Hilt to inject it everywhere is more efficient
        /*
        database = Room.inMemoryDatabaseBuilder( // inMemoryDatabaseBuilder(): creates fake database in memory rather than persistent storage
            ApplicationProvider.getApplicationContext(),
            ShoppingItemDatabase::class.java
        )
            .allowMainThreadQueries() // in tests we don't want multiple threads manipulating each other, so allow main thread only
            .build()
         */
        hiltRule.inject() // injects dependencies under @Inject annotation above instead of creating new database manually here
        dao = database.shoppingDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun testLaunchFragmentInHiltContainer() {
        launchFragmentInHiltContainer<ShoppingFragment> {
            // thanks to HiltExt: we have reference to the ShoppingFragment here!
        }
    }

    @Test
    fun insertShoppingItem() = runBlockingTest { // runBlockingTest(): allows to run coroutine in main thread, for testing only
        // insert a fake item
        val shoppingItem = ShoppingItem("name", 1, 1f, "url", id = 1)
        dao.insertShoppingItem(shoppingItem)

        // LiveData.getOrAwaitValue() is a test function created & used by Google to test coroutines. Copied into LiveDataUtilAndroidTest.kt in androidTest package
        val allShoppingItems = dao.observeAllShoppingItems().getOrAwaitValue()

        assertThat(allShoppingItems.contains(shoppingItem))
    }

    @Test
    fun deleteShoppingItem() = runBlockingTest {
        val shoppingItem = ShoppingItem("name", 1, 1f, "url", id = 1)
        dao.insertShoppingItem(shoppingItem)
        dao.deleteShoppingItem(shoppingItem)

        val allShoppingItems = dao.observeAllShoppingItems().getOrAwaitValue()

        assertThat(allShoppingItems).doesNotContain(shoppingItem)
    }

    @Test
    fun observeTotalPriceSum() = runBlockingTest {
        val shoppingItem1 = ShoppingItem("name", 2, 10f, "url", id = 1)
        val shoppingItem2 = ShoppingItem("name", 4, 5.5f, "url", id = 2)
        val shoppingItem3 = ShoppingItem("name", 0, 100f, "url", id = 3)
        dao.insertShoppingItem(shoppingItem1)
        dao.insertShoppingItem(shoppingItem2)
        dao.insertShoppingItem(shoppingItem3)

        val totalPriceSum = dao.observeTotalPrice().getOrAwaitValue()

        assertThat(totalPriceSum).isEqualTo(2 * 10f + 4 * 5.5f)
    }
}