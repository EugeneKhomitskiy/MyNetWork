package com.example.mynetwork.entity

import com.example.mynetwork.enumeration.EventType

data class EventTypeEmbeddable(
    val eventType: String
) {
    fun toDto() = EventType.valueOf(eventType)

    companion object {
        fun fromDto(dto: EventType) = EventTypeEmbeddable(dto.name)
    }

}