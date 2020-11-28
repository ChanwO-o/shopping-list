package com.androiddevs.shoppinglisttestingyt.repositories

import androidx.lifecycle.LiveData
import com.androiddevs.shoppinglisttestingyt.data.local.ShoppingItem
import com.androiddevs.shoppinglisttestingyt.data.remote.responses.ImageResponse
import com.androiddevs.shoppinglisttestingyt.other.Resource
import retrofit2.Response

// repository functions
interface ShoppingRepository {

    suspend fun insertShoppingItem(shoppingItem: ShoppingItem)

    suspend fun deleteShoppingItem(shoppingItem: ShoppingItem)

    suspend fun observeAllShoppingItems(): LiveData<List<ShoppingItem>>

    suspend fun observeTotalPrice(): LiveData<Float>

    suspend fun searchForImage(imageQuery: String): Resource<ImageResponse>
}