package com.example.newstoryapp.User

data class ModelUser(
    val userId: String,
    val name: String,
    val email: String,
    val token: String,
    val isLogin: Boolean
)