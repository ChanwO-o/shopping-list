package com.androiddevs.shoppinglisttestingyt.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.androiddevs.shoppinglisttestingyt.getOrAwaitValue
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi // add this to remove warning in test cases below
@RunWith(AndroidJUnit4::class) // tell JUnit these are instrmented tests (requiring emulator/device)
@SmallTest // unit test category (small, medium, large) not necessary annotation
class ShoppingDaoTest { // test Room database

    // LiveData is asynchronous by default, even though we use runBlockingTest{} JUnit won't allow it.
    // This rule tells JUnit to run tests in this class one after another (basically on the same thread)
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: ShoppingItemDatabase
    private lateinit var dao: ShoppingDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder( // inMemoryDatabaseBuilder(): creates fake database in memory rather than persistent storage
            ApplicationProvider.getApplicationContext(),
            ShoppingItemDatabase::class.java
        )
            .allowMainThreadQueries() // in tests we don't want multiple threads manipulating each other, so allow main thread only
            .build()
        dao = database.shoppingDao()
    }

    @After
    fun teardown() {
        database.close()
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