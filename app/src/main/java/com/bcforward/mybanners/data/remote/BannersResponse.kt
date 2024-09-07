package com.bcforward.mybanners.data.remote

data class BannersResponse(
    val sectionType: String,
    val items: List<Item>
)

data class Item(
    val title: String,
    val image: String
)

