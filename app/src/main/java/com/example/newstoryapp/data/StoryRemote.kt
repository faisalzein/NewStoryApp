package com.example.newstoryapp.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.newstoryapp.Api.ServiceAPI
import com.example.newstoryapp.response.ListStoryItem
import java.lang.Exception


@OptIn(ExperimentalPagingApi::class)

class StoryRemote(

    private val database: ListDatabase,
    private val apiService: ServiceAPI,
    private val token: String
) : RemoteMediator<Int, ListStoryItem>() {


    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }


    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ListStoryItem>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosesToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {

            val responseData = apiService.getStory("Bearer $token", page, state.config.pageSize, 1 and 0).listStory
            val endOfPaginationReached = responseData.isEmpty()


            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.remotKeysDao().deleteRemoteKeys()
                    database.storyDao().deleteAll()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = responseData.map {
                    RemotKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                database.remotKeysDao().enterAll(keys)
                database.storyDao().addStory(responseData)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }


    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ListStoryItem>): RemotKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.remotKeysDao().getRemoteKeysId(data.id)
        }
    }

    // Fungsi untuk mendapatkan remote key dari item pertama di state
    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ListStoryItem>): RemotKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.remotKeysDao().getRemoteKeysId(data.id)
        }
    }

    // Fungsi untuk mendapatkan remote key yang paling dekat dengan posisi saat ini di state
    private suspend fun getRemoteKeyClosesToCurrentPosition(state: PagingState<Int, ListStoryItem>): RemotKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.remotKeysDao().getRemoteKeysId(id)
            }
        }
    }
}
