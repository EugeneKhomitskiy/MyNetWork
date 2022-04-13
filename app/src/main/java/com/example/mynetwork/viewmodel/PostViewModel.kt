package com.example.mynetwork.viewmodel

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.mynetwork.dto.Post
import com.example.mynetwork.model.ModelState
import com.example.mynetwork.repository.PostRepository
import com.example.mynetwork.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
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

@ExperimentalCoroutinesApi
@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    private val cached = postRepository
        .data
        .cachedIn(viewModelScope)

    val data: Flow<PagingData<Post>> = cached

    private val edited = MutableLiveData(empty)

    private val _dataState = MutableLiveData<ModelState>()
    val dataState: LiveData<ModelState>
        get() = _dataState

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    fun save() {
        edited.value?.let { post ->
            viewModelScope.launch {
                try {
                    postRepository.savePost(post)
                    _dataState.value = ModelState()
                    _postCreated.value = Unit
                } catch (e: Exception) {
                    _dataState.value = ModelState(error = true)
                }
            }
        }
        edited.value = empty
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }
}