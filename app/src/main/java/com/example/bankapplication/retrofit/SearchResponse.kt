package com.example.bankapplication.retrofit

data class SearchResponse(
    val last_name: String,
    val first_name: String,
    val phone_number: String,
    val password: String,
    val balance: Int
)
