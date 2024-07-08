package com.example.newstoryapp.Injection

import android.content.Context
import com.example.newstoryapp.Api.APISettings
import com.example.newstoryapp.data.ListDatabase
import com.example.newstoryapp.data.ListStorage

object Injection {


    fun RepositoryProvider(context: Context): ListStorage{

        val database = ListDatabase.getDatabase(context)


        val apiService = APISettings.getApiService()


        return ListStorage(database, apiService)
    }
}