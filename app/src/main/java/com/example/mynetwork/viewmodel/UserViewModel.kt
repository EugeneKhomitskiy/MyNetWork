package com.example.mynetwork.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynetwork.api.UserApiService
import com.example.mynetwork.dto.User
import com.example.mynetwork.model.ModelState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userApiService: UserApiService
) : ViewModel() {
    private val _data = MutableLiveData<List<User>>()
    val data: LiveData<List<User>>
        get() = _data

    private val _dataState = MutableLiveData<ModelState>()
    val dataState: LiveData<ModelState>
        get() = _dataState

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    fun getUsers() = viewModelScope.launch {
        try {
            val response = userApiService.getUsers()
            if (response.isSuccessful) {
                _data.value = response.body()
            }
        } catch (e: IOException) {
            _dataState.postValue(ModelState(error = true))
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
            e.printStackTrace()
        }
    }
}