package com.example.mynetwork.viewmodel

import androidx.lifecycle.ViewModel
import androidx.paging.map
import com.example.mynetwork.auth.AppAuth
import com.example.mynetwork.repository.WallRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class WallViewModel @Inject constructor(
    private val wallRepository: WallRepository,
    private val appAuth: AppAuth
) : ViewModel() {

    fun userWall(id: Long) = appAuth.authStateFlow
        .flatMapLatest { (myId, _) ->
            wallRepository.userWall(id).map { pagingData ->
                pagingData.map { post ->
                    post.copy(
                        ownedByMe = post.authorId == myId,
                        likedByMe = post.likeOwnerIds.contains(myId)
                    )
                }
            }
        }
}