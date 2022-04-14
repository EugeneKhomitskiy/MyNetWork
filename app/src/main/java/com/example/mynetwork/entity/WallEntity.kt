package com.example.mynetwork.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mynetwork.dto.Post

@Entity
data class WallEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String?,
    val content: String,
    val published: String,
    @Embedded
    val coords: CoordinatesEmbeddable?,
    val link: String? = null,
    val likedByMe: Boolean = false,
    @Embedded
    val attachment: AttachmentEmbeddable?,
    val likeOwnerIds: Set<Long> = emptySet(),
) {
    fun toDto() =
        Post(
            id = id,
            authorId = authorId,
            author = author,
            authorAvatar = authorAvatar,
            content = content,
            published = published,
            coords = coords?.toDto(),
            link = link,
            likedByMe = likedByMe,
            attachment = attachment?.toDto(),
            likeOwnerIds = likeOwnerIds
        )

    companion object {
        fun fromDto(dto: Post) =
            WallEntity(
                dto.id,
                dto.authorId,
                dto.author,
                dto.authorAvatar,
                dto.content,
                dto.published,
                CoordinatesEmbeddable.fromDto(dto.coords),
                dto.link,
                dto.likedByMe,
                AttachmentEmbeddable.fromDto(dto.attachment),
                dto.likeOwnerIds
            )
    }
}

fun List<WallEntity>.toDto() = map(WallEntity::toDto)
fun List<Post>.toWallEntity() = map(WallEntity::fromDto)