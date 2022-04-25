package com.example.mynetwork.dto

import com.example.mynetwork.enumeration.EventType

data class Event(
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String?,
    val content: String,
    val datetime: String,
    val published: String,
    val coords: Coordinates? = null,
    val type: EventType,
    val likeOwnerIds: Set<Long> = emptySet(),
    val likedByMe: Boolean = false,
    val speakerIds: Set<Long> = emptySet(),
    val participantsIds: Set<Long> = emptySet(),
    val participatedByMe: Boolean = false,
    val attachment: Attachment? = null,
    val link: String? = null,
    val ownedByMe: Boolean = false
) {
    companion object {
        val empty = Event(
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
    }
}