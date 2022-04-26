package com.example.mynetwork.viewmodel

import android.net.Uri
import androidx.core.net.toFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynetwork.api.UserApiService
import com.example.mynetwork.dto.PhotoUpload
import com.example.mynetwork.dto.Token
import com.example.mynetwork.error.ApiError
import com.example.mynetwork.model.ModelState
import com.example.mynetwork.model.PhotoModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import javax.inject.Inject

private val noPhoto = PhotoModel()

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val userApiService: UserApiService
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
                val response = userApiService.registerUser(
                    name.toRequestBody("text/plain".toMediaType()),
                    login.toRequestBody("text/plain".toMediaType()),
                    pass.toRequestBody("text/plain".toMediaType()),
                    photo.value?.uri?.toFile()?.let {
                        val upload = PhotoUpload(it)
                        MultipartBody.Part.createFormData(
                            "file", upload.file.name, upload.file.asRequestBody()
                        )
                    }
                )
                val body = response.body() ?: throw ApiError(response.message())
                data.value = Token(body.id, body.token)
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