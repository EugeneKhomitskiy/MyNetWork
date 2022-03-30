package com.example.mynetwork.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynetwork.api.ApiService
import com.example.mynetwork.dto.Token
import com.example.mynetwork.error.ApiError
import com.example.mynetwork.error.NetworkError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    val data = MutableLiveData<Token>()

    fun registerUser(name: String, login: String, pass: String) {
        viewModelScope.launch {
            try {
                val response = apiService.registerUser(name, login, pass)
                if (!response.isSuccessful) {
                    throw ApiError(response.message())
                }
                val body = response.body() ?: throw ApiError(response.message())
                data.value = Token(body.id, body.token)
            } catch (e: IOException) {
                throw NetworkError
            } catch (e: Exception) {
                throw UnknownError()
            }
        }
    }
}