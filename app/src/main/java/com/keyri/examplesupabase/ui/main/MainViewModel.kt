package com.keyri.examplesupabase.ui.main

import androidx.lifecycle.ViewModel
import com.keyri.examplesupabase.data.AuthResponse
import com.keyri.examplesupabase.data.SignupRequestBody
import com.keyri.examplesupabase.network.ApiService
import kotlinx.coroutines.flow.Flow

class MainViewModel(private val api: ApiService) : ViewModel() {

    fun signup(apiKey: String, email: String, password: String): Flow<AuthResponse> {
        return api.signup(apiKey, SignupRequestBody(email, password))
    }

    fun login(apiKey: String, email: String, password: String): Flow<AuthResponse> {
        return api.login(apiKey, body = SignupRequestBody(email, password))
    }
}
