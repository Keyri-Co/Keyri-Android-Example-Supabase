package com.keyri.examplesupabase.data

import com.google.gson.annotations.SerializedName

data class SignupRequestBody(

    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String
)
