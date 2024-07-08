package com.example.newstoryapp.data


import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.newstoryapp.Api.ServiceAPI
import com.example.newstoryapp.response.ListStoryItem


class ListStorage(private val listDatabase: ListDatabase, private val apiService: ServiceAPI) {


    @OptIn(ExperimentalPagingApi::class)

    fun getStories(token: String): LiveData<PagingData<ListStoryItem>> {

        return Pager(
            config = PagingConfig(

                pageSize = 5
            ),

            remoteMediator = StoryRemote(listDatabase, apiService, token),
            pagingSourceFactory = {

                listDatabase.storyDao().getAllStory()
            }
        ).liveData
    }


    suspend fun getLoc(token: String): List<ListStoryItem> {
        return apiService.getLocation("Bearer $token").listStory
    }
}
