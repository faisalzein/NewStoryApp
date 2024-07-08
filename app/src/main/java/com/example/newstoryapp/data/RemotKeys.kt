package com.example.newstoryapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "story_keys")
data class RemotKeys(
    @PrimaryKey val id: String,
    val prevKey: Int?,
    val nextKey: Int?
)