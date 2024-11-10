package com.example.bankapplication.retrofit

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface MainAPI {
    @GET("items/")
    suspend fun getUserById(): RegResponse

    @POST("register/")
    suspend fun register(@Body regRequest: RegPost): RegResponse

    @POST("auth/")
    suspend fun auth(@Body authRequest: AuthPost): Response<AuthResponse>

    @POST("user_search/")
    suspend fun user_search(@Body authRequest: SearchPost): Response<SearchResponse>

    @POST("transfer/")
    suspend fun transfer(@Body authRequest: TransferPost): Response<TransferResponse>

    @POST("support/")
    suspend fun support(@Body request: SupportPost): Response<SupportResponse>
}