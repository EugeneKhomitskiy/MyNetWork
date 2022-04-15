package com.example.mynetwork.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.mynetwork.auth.AppAuth
import com.example.mynetwork.dao.WallDao
import com.example.mynetwork.dto.Post
import com.example.mynetwork.model.ModelState
import com.example.mynetwork.repository.WallRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class WallViewModel @Inject constructor(
    private val wallRepository: WallRepository,
    private val wallDao: WallDao,
    appAuth: AppAuth
) : ViewModel() {

    private val cached = wallRepository
        .data
        .cachedIn(viewModelScope)

    val data: Flow<PagingData<Post>> = appAuth.authStateFlow
        .flatMapLatest { (myId, _) ->
            cached.map { pagingData ->
                pagingData.map { post ->
                    post.copy(ownedByMe = post.authorId == myId)
                }
            }
        }

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