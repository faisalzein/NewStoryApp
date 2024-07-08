package com.example.newstoryapp.View

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.newstoryapp.Injection.Injection
import com.example.newstoryapp.data.ListStorage
import java.lang.IllegalArgumentException

class ListStoryViewModel(private val listRepository: ListStorage): ViewModel() {
    fun getStory(token: String) = listRepository.getStories(token).cachedIn(viewModelScope)
}

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListStoryViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return ListStoryViewModel(Injection.RepositoryProvider(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}