package com.example.newstoryapp.data

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newstoryapp.response.ListStoryItem

@Dao
interface DAOStory {

    @Insert(onConflict = OnConflictStrategy.REPLACE)

    suspend fun addStory(story: List<ListStoryItem>)


    @Query("SELECT * FROM story")
    fun getAllStory(): PagingSource<Int, ListStoryItem>


    @Query("DELETE FROM story")
    suspend fun deleteAll()
}