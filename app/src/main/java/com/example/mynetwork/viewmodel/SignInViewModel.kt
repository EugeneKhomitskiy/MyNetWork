package com.example.mynetwork.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynetwork.api.UserApiService
import com.example.mynetwork.dto.Token
import com.example.mynetwork.error.ApiError
import com.example.mynetwork.model.ModelState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val userApiService: UserApiService
) : ViewModel() {

    val data = MutableLiveData<Token>()

    private val _dataState = MutableLiveData<ModelState>()
    val dataState: LiveData<ModelState>
        get() = _dataState

    fun updateUser(name: String, pass: String) {
        viewModelScope.launch {
            _dataState.postValue(ModelState(loading = true))
            try {
                val response = userApiService.updateUser(name, pass)
                if (!response.isSuccessful) {
                    throw ApiError(response.message())
                }
                _dataState.postValue(ModelState())
                val body = response.body() ?: throw ApiError(response.message())
                data.value = Token(body.id, body.token)
            } catch (e: IOException) {
                _dataState.postValue(ModelState(error = true))
            } catch (e: Exception) {
                _dataState.postValue(ModelState(errorLogin = true))
            }
        }
    }
}