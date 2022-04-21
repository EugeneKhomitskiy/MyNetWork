package com.example.mynetwork.viewmodel

import android.net.Uri
import androidx.core.net.toFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.mynetwork.auth.AppAuth
import com.example.mynetwork.dto.Event
import com.example.mynetwork.dto.MediaUpload
import com.example.mynetwork.enumeration.EventType
import com.example.mynetwork.model.ModelState
import com.example.mynetwork.model.PhotoModel
import com.example.mynetwork.repository.EventRepository
import com.example.mynetwork.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

private val empty = Event(
    id = 0,
    authorId = 0,
    author = "",
    authorAvatar = "",
    content = "",
    published = "2021-08-17T16:46:58.887547Z",
    datetime = "2021-08-17T16:46:58.887547Z",
    type = EventType.ONLINE,
    speakerIds = emptySet()
)

private val noPhoto = PhotoModel()

@ExperimentalCoroutinesApi
@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    appAuth: AppAuth
) : ViewModel() {
    private val cached = eventRepository
        .data
        .cachedIn(viewModelScope)

    val data: Flow<PagingData<Event>> = appAuth.authStateFlow
        .flatMapLatest { (myId, _) ->
            cached.map { pagingData ->
                pagingData.map { event ->
                    event.copy(
                        ownedByMe = event.authorId == myId,
                        likedByMe = event.likeOwnerIds.contains(myId),
                        participatedByMe = event.participantsIds.contains(myId)
                    )
                }
            }
        }

    val edited = MutableLiveData(empty)

    private val _dataState = MutableLiveData<ModelState>()
    val dataState: LiveData<ModelState>
        get() = _dataState

    private val _eventCreated = SingleLiveEvent<Unit>()
    val eventCreated: LiveData<Unit>
        get() = _eventCreated

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

    fun save() {
        edited.value?.let { event ->
            viewModelScope.launch {
                try {
                    when (_photo.value) {
                        noPhoto -> eventRepository.save(event)
                        else -> _photo.value?.uri?.let { MediaUpload(it.toFile()) }
                            ?.let { eventRepository.saveWithAttachment(event, it) }
                    }
                    _dataState.value = ModelState()
                    _eventCreated.value = Unit
                } catch (e: Exception) {
                    _dataState.value = ModelState(error = true)
                }
            }
        }
        edited.value = empty
        _photo.value = noPhoto
    }

    fun change(content: String, date: String) {
        edited.value?.let {
            val text = content.trim()
            if (edited.value?.content != text) {
                edited.value = edited.value?.copy(content = text)
            }
            if (edited.value?.datetime != date) {
                edited.value = edited.value?.copy(datetime = date)
            }
        }
    }

    fun setSpeaker(id: Long) {
        if (edited.value?.speakerIds?.contains(id) == false) {
            edited.value = edited.value?.speakerIds?.plus(id)
                ?.let { edited.value?.copy(speakerIds = it) }
        }
    }

    fun changePhoto(uri: Uri?) {
        _photo.value = PhotoModel(uri)
    }

    fun removeById(id: Long) = viewModelScope.launch {
        try {
            eventRepository.removeById(id)
        } catch (e: Exception) {
            _dataState.value =
                ModelState(error = true)
        }
    }

    fun edit(event: Event) {
        edited.value = event
    }

    fun likeById(id: Long) = viewModelScope.launch {
        try {
            eventRepository.likeById(id)
        } catch (e: Exception) {
            _dataState.value =
                ModelState(error = true)
        }
    }

    fun dislikeById(id: Long) = viewModelScope.launch {
        try {
            eventRepository.dislikeById(id)
        } catch (e: Exception) {
            _dataState.value =
                ModelState(error = true)
        }
    }

    fun participate(id: Long) = viewModelScope.launch {
        try {
            eventRepository.participate(id)
        } catch (e: Exception) {
            _dataState.value =
                ModelState(error = true)
        }
    }

    fun notParticipate(id: Long) = viewModelScope.launch {
        try {
            eventRepository.notParticipate(id)
        } catch (e: Exception) {
            _dataState.value =
                ModelState(error = true)
        }
    }
}