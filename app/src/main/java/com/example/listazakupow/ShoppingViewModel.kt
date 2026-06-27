package com.example.listazakupow

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShoppingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ShoppingRepository(application.applicationContext)

    // Lista produktów
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    // Generator kolejnych ID
    private var nextId = 1

    init {
        loadProducts()
    }

    /**
     * Wczytanie listy z DataStore
     */
    private fun loadProducts() {
        viewModelScope.launch {
            val savedProducts = repository.loadProducts()

            _products.value = savedProducts

            // Ustawienie następnego wolnego ID
            nextId = if (savedProducts.isEmpty()) {
                1
            } else {
                savedProducts.maxOf { it.id } + 1
            }
        }
    }

    /**
     * Zapis listy do DataStore
     */
    private fun saveProducts() {
        viewModelScope.launch {
            repository.saveProducts(_products.value)
        }
    }

    /**
     * Dodawanie produktu
     */
    fun addProduct(name: String) {

        if (name.isBlank()) return

        val newProduct = Product(
            id = nextId++,
            name = name
        )

        _products.value = _products.value + newProduct

        saveProducts()
    }

    /**
     * Usuwanie produktu
     */
    fun removeProduct(product: Product) {

        _products.value = _products.value.filter {
            it.id != product.id
        }

        saveProducts()
    }

    /**
     * Edycja nazwy produktu
     */
    fun updateProduct(product: Product, newName: String) {

        if (newName.isBlank()) return

        _products.value = _products.value.map {
            if (it.id == product.id)
                it.copy(name = newName)
            else
                it
        }

        saveProducts()
    }

    /**
     * Oznaczanie produktu jako kupiony
     */
    fun toggleBought(product: Product) {

        _products.value = _products.value.map {
            if (it.id == product.id)
                it.copy(isBought = !it.isBought)
            else
                it
        }

        saveProducts()
    }
}