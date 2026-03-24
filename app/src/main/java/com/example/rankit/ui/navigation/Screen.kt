package com.example.rankit.ui.navigation

import kotlinx.serialization.Serializable

// Each object/class here is a route. The @Serializable annotation lets
// Navigation Compose encode/decode route arguments automatically —
// no manual string building like "listDetail/{listId}" needed.

@Serializable
object Home

@Serializable
data class ListDetail(val listId: String)

@Serializable
object CreateList

@Serializable
data class AddItem(val listId: String)

@Serializable
data class ItemDetail(val listId: String, val itemId: String)
