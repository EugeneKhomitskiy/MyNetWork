package com.example.mynetwork.viewmodel

import android.net.Uri
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.mynetwork.auth.AppAuth
import com.example.mynetwork.dto.Coordinates
import com.example.mynetwork.dto.MediaUpload
import com.example.mynetwork.dto.Post
import com.example.mynetwork.enumeration.AttachmentType
import com.example.mynetwork.model.MediaModel
import com.example.mynetwork.model.ModelState
import com.example.mynetwork.repository.PostRepository
import com.example.mynetwork.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

private val empty = Post(
    id = 0,
    authorId = 0,
    author = "",
    authorAvatar = "",
    content = "",
    published = "2021-08-17T16:46:58.887547Z"
)

private val noMedia = MediaModel()

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
                    post.copy(
                        ownedByMe = post.authorId == myId,
                        likedByMe = post.likeOwnerIds.contains(myId)
                    )
                }
            }
        }

    val edited = MutableLiveData(empty)

    private val _dataState = MutableLiveData<ModelState>()
    val dataState: LiveData<ModelState>
        get() = _dataState

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _media = MutableLiveData(noMedia)
    val media: LiveData<MediaModel>
        get() = _media

    fun save() {
        edited.value?.let { post ->
            viewModelScope.launch {
                _dataState.postValue(ModelState(loading = true))
                try {
                    when (_media.value) {
                        noMedia -> postRepository.savePost(post)
                        else -> _media.value?.inputStream?.let { MediaUpload(it) }
                            ?.let { postRepository.saveWithAttachment(post, it, _media.value?.type!!) }
                    }
                    _dataState.postValue(ModelState())
                    _postCreated.value = Unit
                } catch (e: IOException) {
                    _dataState.postValue(ModelState(error = true))
                } catch (e: Exception) {
                    throw UnknownError()
                }
            }
        }
        edited.value = empty
        _media.value = noMedia
    }

    fun change(content: String, coords: Coordinates?) {
        edited.value?.let {
            val text = content.trim()
            if (edited.value?.content != text) {
                edited.value = edited.value?.copy(content = text)
            }
            if (edited.value?.coords != coords) {
                edited.value = edited.value?.copy(coords = coords)
            }
        }
    }

    fun changeMedia(uri: Uri?, inputStream: InputStream?, type: AttachmentType?) {
        _media.value = MediaModel(uri, inputStream, type)
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

    fun likeById(id: Long) = viewModelScope.launch {
        try {
            postRepository.likeById(id)
        } catch (e: Exception) {
            _dataState.value =
                ModelState(error = true)
        }
    }

    fun dislikeById(id: Long) = viewModelScope.launch {
        try {
            postRepository.dislikeById(id)
        } catch (e: Exception) {
            _dataState.value =
                ModelState(error = true)
        }
    }

    fun changeMentionIds(id: Long) {
        edited.value?.let {
            if (edited.value?.mentionIds?.contains(id) == false) {
                edited.value = edited.value?.copy(mentionIds = it.mentionIds.plus(id))
            }
        }
    }
}