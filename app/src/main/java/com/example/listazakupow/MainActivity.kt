package com.example.listazakupow

import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.text.style.TextDecoration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.filled.Check

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ShoppingScreen()
        }
    }
}

@Composable
fun ShoppingScreen(viewModel: ShoppingViewModel = viewModel()) {

    val products by viewModel.products.collectAsState()
    var text by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {

        // Licznik produktów
        Text(
            text = "Liczba produktów: ${products.size}",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Pole dodawania produktu
        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Nazwa produktu") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (text.isNotBlank()) {
                    viewModel.addProduct(text)
                    text = ""
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Dodaj produkt")
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(products) { product ->

                var editMode by remember { mutableStateOf(false) }
                var tempName by remember { mutableStateOf(product.name) }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {

                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        if (editMode) {

                            TextField(
                                value = tempName,
                                onValueChange = { tempName = it },
                                modifier = Modifier.weight(1f)
                            )

                            IconButton(
                                onClick = {
                                    viewModel.updateProduct(product, tempName)
                                    editMode = false
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Zapisz"
                                )
                            }

                        } else {

                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Checkbox(
                                    checked = product.isBought,
                                    onCheckedChange = {
                                        viewModel.toggleBought(product)
                                    }
                                )

                                Text(
                                    text = product.name,
                                    textDecoration =
                                        if (product.isBought)
                                            TextDecoration.LineThrough
                                        else
                                            TextDecoration.None,
                                    modifier = Modifier.clickable {
                                        editMode = true
                                    }
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                viewModel.removeProduct(product)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Usuń"
                            )
                        }
                    }
                }
            }
        }
    }
}