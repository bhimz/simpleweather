package com.bhimz.simpleweather.util

const val HEADER_VIEW = 0
const val ITEM_VIEW = 1

@Suppress("UNCHECKED_CAST")
    data class ListItemModel(val actualIndex: Int, val viewType: Int, private val data: Any) {
        fun <T> itemData(): T = data as T
    }