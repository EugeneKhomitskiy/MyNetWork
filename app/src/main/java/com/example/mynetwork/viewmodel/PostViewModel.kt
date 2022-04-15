package com.example.mynetwork.viewmodel

import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.mynetwork.auth.AppAuth
import com.example.mynetwork.dto.MediaUpload
import com.example.mynetwork.dto.Post
import com.example.mynetwork.model.ModelState
import com.example.mynetwork.model.PhotoModel
import com.example.mynetwork.repository.PostRepository
import com.example.mynetwork.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

private val empty = Post(
    id = 0,
    authorId = 0,
    author = "",
    authorAvatar = "",
    content = "",
    published = "2021-08-17T16:46:58.887547Z"
)

private val noPhoto = PhotoModel()

@ExperimentalCoroutinesApi
@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepository: PostRepository,
    appAuth: AppAuth
) : ViewModel() {

    private val cached = postRepository
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

    private val edited = MutableLiveData(empty)

    private val _dataState = MutableLiveData<ModelState>()
    val dataState: LiveData<ModelState>
        get() = _dataState

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

    fun save() {
        edited.value?.let { post ->
            viewModelScope.launch {
                try {
                    when (_photo.value) {
                        noPhoto -> postRepository.savePost(post)
                        else -> _photo.value?.uri?.let { MediaUpload(it.toFile()) }
                            ?.let { postRepository.saveWithAttachment(post, it) }
                    }
                    _dataState.value = ModelState()
                    _postCreated.value = Unit
                } catch (e: Exception) {
                    _dataState.value = ModelState(error = true)
                }
            }
        }
        edited.value = empty
        _photo.value = noPhoto
    }

    fun changeContent(content: String) {
        Log.d("aaa", edited.value?.id.toString())
        edited.value?.let {
            val text = content.trim()
            if (edited.value?.content != text) {
                edited.value = edited.value?.copy(content = text)
            }
        }

    }

    fun changePhoto(uri: Uri?) {
        _photo.value = PhotoModel(uri)
    }

    fun removeById(id: Long) = viewModelScope.launch {
        try {
            postRepository.removeById(id)
        } catch (e: Exception) {
            _dataState.value =
                ModelState(error = true)
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }
}