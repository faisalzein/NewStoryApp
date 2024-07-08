package com.example.newstoryapp.response

import com.google.gson.annotations.SerializedName

data class AddStoryrespon(

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)