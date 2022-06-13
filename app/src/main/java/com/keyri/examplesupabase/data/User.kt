package com.keyri.examplesupabase.data

import com.google.gson.annotations.SerializedName

data class User(

    @SerializedName("id")
    val id: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("phone")
    val phone: String?
)
