package com.example.mynetwork.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.mynetwork.dao.WallDao
import com.example.mynetwork.dto.Post
import com.example.mynetwork.model.ModelState
import com.example.mynetwork.repository.WallRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class WallViewModel @Inject constructor(
    private val wallRepository: WallRepository,
    private val wallDao: WallDao
) : ViewModel() {

    private val cached = wallRepository
        .data
        .cachedIn(viewModelScope)

    val data: Flow<PagingData<Post>> = cached

    private val _dataState = MutableLiveData<ModelState>()
    val dataState: LiveData<ModelState>
        get() = _dataState

    fun load(id: Long) = viewModelScope.launch {
        try {
            wallRepository.load(id)
        } catch (e: Exception) {
            _dataState.value = ModelState(error = true)
        }
    }

    fun clearWall() = viewModelScope.launch {
        wallDao.removeAll()
    }
}