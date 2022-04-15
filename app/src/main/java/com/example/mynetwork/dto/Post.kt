package com.example.mynetwork.dto

data class Post(
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String?,
    val content: String,
    val published: String,
    val coords: Coordinates? = null,
    val link: String? = null,
    val mentionIds: Set<Long> = emptySet(),
    val mentionedMe: Boolean = false,
    val likeOwnerIds: Set<Long> = emptySet(),
    val likedByMe: Boolean = false,
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false
) {
    companion object {
        val empty = Post(
            id = 0,
            authorId = 0,
            author = "",
            authorAvatar = "",
            content = "",
            published = "2021-08-17T16:46:58.887547Z"
        )
    }
}