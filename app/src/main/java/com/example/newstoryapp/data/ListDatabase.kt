package com.example.newstoryapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.newstoryapp.response.ListStoryItem

@Database(
    entities = [ListStoryItem::class, RemotKeys::class],
    version = 2,
    exportSchema = false
)
abstract class ListDatabase: RoomDatabase() {


    abstract fun storyDao(): DAOStory


    abstract fun remotKeysDao(): RemoteKeysDao


    companion object{

        @Volatile
        private var INSTANCE: ListDatabase? = null


        @JvmStatic
        fun getDatabase(context: Context): ListDatabase {

            return INSTANCE ?: synchronized(this){
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ListDatabase::class.java, "story_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
