package com.example.listazakupow

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(name = "shopping_data")

class ShoppingRepository(private val context: Context) {

    companion object {
        private val PRODUCTS_KEY = stringPreferencesKey("products")
    }

    private val gson = Gson()

    suspend fun saveProducts(products: List<Product>) {
        val json = gson.toJson(products)

        context.dataStore.edit { preferences ->
            preferences[PRODUCTS_KEY] = json
        }
    }

    suspend fun loadProducts(): List<Product> {
        val preferences = context.dataStore.data.first()
        val json = preferences[PRODUCTS_KEY] ?: return emptyList()

        val type = object : TypeToken<List<Product>>() {}.type

        return gson.fromJson(json, type)
    }
}