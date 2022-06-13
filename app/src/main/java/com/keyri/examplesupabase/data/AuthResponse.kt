package com.keyri.examplesupabase.data

import com.google.gson.annotations.SerializedName

data class AuthResponse(

    @SerializedName("access_token")
    val accessToken: String,

    @SerializedName("token_type")
    val tokenType: String,

    @SerializedName("expires_in")
    val expiresIn: Long,

    @SerializedName("refresh_token")
    val refreshToken: String,

    @SerializedName("user")
    val user: User
)
