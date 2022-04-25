package com.example.mynetwork.viewmodel

import androidx.lifecycle.*
import com.example.mynetwork.auth.AppAuth
import com.example.mynetwork.dto.Job
import com.example.mynetwork.model.ModelState
import com.example.mynetwork.repository.JobRepository
import com.example.mynetwork.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

private val empty = Job(
    id = 0,
    name = "",
    position = "",
    start = 0L,
    finish = null
)

@ExperimentalCoroutinesApi
@HiltViewModel
class JobViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    appAuth: AppAuth
) : ViewModel() {

    val data: Flow<List<Job>> = appAuth.authStateFlow
        .flatMapLatest { (myId, _) ->
            jobRepository.data.map {
                it.map {
                    it.copy(
                        ownedByMe = userId.value == myId
                    )
                }
            }
        }

    private val userId = MutableLiveData<Long>()

    val edited = MutableLiveData(empty)

    private val _dataState = MutableLiveData<ModelState>()
    val dataState: LiveData<ModelState>
        get() = _dataState

    private val _jobCreated = SingleLiveEvent<Unit>()
    val jobCreated: LiveData<Unit>
        get() = _jobCreated

    fun loadJobs(id: Long) = viewModelScope.launch {
        try {
            _dataState.value = ModelState(loading = true)
            jobRepository.getByUserId(id)
            _dataState.value = ModelState()
        } catch (e: Exception) {
            _dataState.value = ModelState(error = true)
        }
    }

    fun setId(id: Long) {
        userId.value = id
    }

    fun save() {
        edited.value?.let { job ->
            _jobCreated.value = Unit
            viewModelScope.launch {
                _dataState.value = ModelState(loading = true)
                try {
                    jobRepository.save(job)
                    _dataState.value = ModelState()
                } catch (e: Exception) {
                    _dataState.value = ModelState(error = true)
                }
            }
        }
        edited.value = empty
    }

    fun change(name: String, position: String, start: Long, finish: Long?) {
        edited.value?.let {
            val nameText = name.trim()
            val positionText = position.trim()
            if (edited.value?.name != nameText) {
                edited.value = edited.value?.copy(name = nameText)
            }
            if (edited.value?.position != positionText) {
                edited.value = edited.value?.copy(position = positionText)
            }
            if (edited.value?.start != start) {
                edited.value = edited.value?.copy(start = start)
            }
            if (edited.value?.finish != finish) {
                edited.value = edited.value?.copy(finish = finish)
            }
        }
    }

    fun edit(job: Job) {
        edited.value = job
    }

    fun removeById(id: Long) = viewModelScope.launch {
        try {
            jobRepository.removeById(id)
        } catch (e: Exception) {
            _dataState.value =
                ModelState(error = true)
        }
    }
}