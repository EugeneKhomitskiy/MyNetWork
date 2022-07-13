package com.example.mynetwork.viewmodel

import androidx.lifecycle.*
import com.example.mynetwork.dto.User
import com.example.mynetwork.model.ModelState
import com.example.mynetwork.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
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
        _dataState.postValue(ModelState(loading = true))
        try {
            userRepository.getAll()
            _dataState.postValue(ModelState())
        } catch (e: Exception) {
            _dataState.value = ModelState(error = true)
        }
    }

    fun getUserById(id: Long) = viewModelScope.launch {
        _dataState.postValue(ModelState(loading = true))
        try {
            _user.value = userRepository.getUserById(id)
            _dataState.postValue(ModelState())
        } catch (e: Exception) {
            _dataState.postValue(ModelState(error = true))
        }
    }

    fun getUsersIds(set: Set<Long>) = viewModelScope.launch {
        _usersIds.value = set
    }
}