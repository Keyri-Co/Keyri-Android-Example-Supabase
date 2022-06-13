package com.keyri.examplesupabase.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keyri.examplesupabase.data.AuthResponse
import com.keyri.examplesupabase.data.SignupRequestBody
import com.keyri.examplesupabase.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainViewModel(private val api: ApiService) : ViewModel() {

    private val _authResponseFlow = MutableStateFlow<AuthResponse?>(null)

    val authResponseFlow: StateFlow<AuthResponse?>
        get() = _authResponseFlow


    fun authorize(apiKey: String, email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val body = SignupRequestBody(email, password)

            try {
                api.signup(apiKey, body).collectLatest {
                    _authResponseFlow.value = it
                }
            } catch (e: Exception) {
                api.login(apiKey = apiKey, body = body).collectLatest {
                    _authResponseFlow.value = it
                }
            }
        }
    }
}
