package com.example.mynetwork.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynetwork.api.UserApiService
import com.example.mynetwork.dto.Token
import com.example.mynetwork.error.ApiError
import com.example.mynetwork.model.ModelState
import com.example.mynetwork.repository.SignInRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInRepository: SignInRepository
) : ViewModel() {

    val data = MutableLiveData<Token>()

    private val _dataState = MutableLiveData<ModelState>()
    val dataState: LiveData<ModelState>
        get() = _dataState

    fun updateUser(name: String, pass: String) {
        viewModelScope.launch {
            _dataState.postValue(ModelState(loading = true))
            try {
                data.value = signInRepository.updateUser(name, pass)
                _dataState.postValue(ModelState())
            } catch (e: IOException) {
                _dataState.postValue(ModelState(error = true))
            } catch (e: Exception) {
                _dataState.postValue(ModelState(errorLogin = true))
            }
        }
    }
}