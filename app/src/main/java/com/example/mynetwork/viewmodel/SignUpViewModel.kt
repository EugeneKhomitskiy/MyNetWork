package com.example.mynetwork.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynetwork.dto.Token
import com.example.mynetwork.model.ModelState
import com.example.mynetwork.model.PhotoModel
import com.example.mynetwork.repository.SignUpRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

private val noPhoto = PhotoModel()

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpRepository: SignUpRepository
) : ViewModel() {

    val data = MutableLiveData<Token>()

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

    private val _dataState = MutableLiveData<ModelState>()
    val dataState: LiveData<ModelState>
        get() = _dataState

    fun registerUser(name: String, login: String, pass: String) {
        viewModelScope.launch {
            _dataState.postValue(ModelState(loading = true))
            try {
                val uri = photo.value?.uri
                val token = signUpRepository.registerUser(name, login, pass, uri!!)
                data.value = Token(token.id, token.token)
                _dataState.postValue(ModelState())
            } catch (e: IOException) {
                _dataState.postValue(ModelState(error = true))
            } catch (e: Exception) {
                throw UnknownError()
            }
        }
        _photo.value = noPhoto
    }

    fun changePhoto(uri: Uri?) {
        _photo.value = PhotoModel(uri)
    }
}