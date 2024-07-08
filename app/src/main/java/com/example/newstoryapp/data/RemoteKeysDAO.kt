package com.example.newstoryapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun enterAll(remoteKey: List<RemotKeys>)

    @Query("SELECT * FROM story_keys WHERE id = :id")
    suspend fun getRemoteKeysId(id: String): RemotKeys?


    @Query("DELETE FROM story_keys")
    suspend fun deleteRemoteKeys()
}
