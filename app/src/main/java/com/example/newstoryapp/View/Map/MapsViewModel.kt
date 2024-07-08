package com.example.newstoryapp.View.Map

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.newstoryapp.Injection.Injection
import com.example.newstoryapp.data.ListStorage
import com.example.newstoryapp.response.ListStoryItem
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class MapViewModel(private val listRepository: ListStorage): ViewModel() {
    private val _listLoc = MutableLiveData<List<ListStoryItem>>()
    var lastLoc: LiveData<List<ListStoryItem>> = _listLoc

    fun getLocation(token: String){
        viewModelScope.launch {
            _listLoc.postValue(listRepository.getLoc(token))
        }
    }
}

class MapsViewModelFactory(private val context: Context): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return MapViewModel(Injection.RepositoryProvider(context)) as T
        }
        throw IllegalArgumentException("Unknown View Model Class: ")
    }
}