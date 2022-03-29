package com.example.mynetwork.dto

import com.example.mynetwork.enumeration.AttachmentType

data class Attachment(
    val url: String,
    val type: AttachmentType,
)