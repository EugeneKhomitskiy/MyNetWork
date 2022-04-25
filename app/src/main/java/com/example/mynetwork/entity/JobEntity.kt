package com.example.mynetwork.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mynetwork.dto.Job

@Entity
data class JobEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val position: String,
    val start: Long,
    val finish: Long? = null,
    val link: String? = null,
    val ownedByMe: Boolean = false
) {
    fun toDto() =
        Job(
            id = id,
            name = name,
            position = position,
            start = start,
            finish = finish,
            link = link,
            ownedByMe = ownedByMe
        )

    companion object {
        fun fromDto(dto: Job) =
            JobEntity(
                dto.id,
                dto.name,
                dto.position,
                dto.start,
                dto.finish,
                dto.link,
                dto.ownedByMe
            )
    }
}

fun List<JobEntity>.toDto() = map(JobEntity::toDto)
fun List<Job>.toJobEntity() = map(JobEntity::fromDto)