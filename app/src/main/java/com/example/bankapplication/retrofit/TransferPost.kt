package com.example.bankapplication.retrofit

data class TransferPost(
    val phone_number: String,
    val balance: Int,
    val sender_phone: String
)
