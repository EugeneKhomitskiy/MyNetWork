package com.example.mynetwork.viewmodel

import androidx.lifecycle.*
import com.example.mynetwork.api.UserApiService
import com.example.mynetwork.dto.Post
import com.example.mynetwork.dto.User
import com.example.mynetwork.model.ModelState
import com.example.mynetwork.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userApiService: UserApiService
) : ViewModel() {

    val data: LiveData<List<User>> = userRepository.data
        .asLiveData(Dispatchers.Default)

    private val _dataState = MutableLiveData<ModelState>()
    val dataState: LiveData<ModelState>
        get() = _dataState

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _usersIds = MutableLiveData<Set<Long>>()
    val userIds: LiveData<Set<Long>>
        get() = _usersIds

    init {
        getUsers()
    }

    private fun getUsers() = viewModelScope.launch {
        try {
            userRepository.getAll()
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    fun getUserById(id: Long) = viewModelScope.launch {
        try {
            val response = userApiService.getUserById(id)
            if (response.isSuccessful) {
                _user.value = response.body()
            }
        } catch (e: IOException) {
            _dataState.postValue(ModelState(error = true))
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    fun getMentionsIds(post: Post) = viewModelScope.launch {
        _usersIds.value = post.mentionIds
    }

    fun getLikeOwnersIds(post: Post) = viewModelScope.launch {
        _usersIds.value = post.likeOwnerIds
    }
}