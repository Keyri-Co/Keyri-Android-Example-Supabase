package com.keyri.examplesupabase.network

import com.keyri.examplesupabase.data.AuthResponse
import com.keyri.examplesupabase.data.SignupRequestBody
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("auth/v1/token")
    fun login(
        @Header("apiKey") apiKey: String,
        @Query("grant_type") grantType: String = "password",
        @Body body: SignupRequestBody
    ): Flow<AuthResponse>

    @POST("auth/v1/signup")
    fun signup(
        @Header("apiKey") apiKey: String,
        @Body body: SignupRequestBody
    ): Flow<AuthResponse>
}
