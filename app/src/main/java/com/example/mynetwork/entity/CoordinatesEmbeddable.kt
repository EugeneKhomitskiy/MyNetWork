package com.example.mynetwork.entity

import com.example.mynetwork.dto.Coordinates

data class CoordinatesEmbeddable(
    val lat: Double,
    val longitude: Double,
) {
    fun toDto() = Coordinates(lat, longitude)

    companion object {
        fun fromDto(dto: Coordinates?) = dto?.let {
            CoordinatesEmbeddable(it.lat, it.long)
        }
    }
}