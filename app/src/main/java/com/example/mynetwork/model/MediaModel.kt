package com.example.mynetwork.model

import android.net.Uri
import com.example.mynetwork.enumeration.AttachmentType
import java.io.InputStream

data class MediaModel(
    val uri: Uri? = null,
    val inputStream: InputStream? = null,
    val type: AttachmentType? = null
)
