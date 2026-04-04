package com.openmobilehub.android.storage.plugin.box.restful.data.source.mapper

import android.webkit.MimeTypeMap
import com.openmobilehub.android.storage.core.model.OmhStorageEntity
import com.openmobilehub.android.storage.core.utils.fromRFC3339StringToDate
import com.openmobilehub.android.storage.plugin.box.restful.data.source.response.NodeMetadata
import com.openmobilehub.android.storage.plugin.box.restful.data.source.response.NodeMetadata.Companion.TYPE_FILE
import com.openmobilehub.android.storage.plugin.box.restful.data.source.response.NodeMetadata.Companion.TYPE_FOLDER
import java.util.Date

const val APPLICATION_OCTET_STREAM = "application/octet-stream"

fun NodeMetadata.toOmhStorageEntity(): OmhStorageEntity {
    return when (type) {
        TYPE_FILE -> OmhStorageEntity.OmhFile(
            mimeType = APPLICATION_OCTET_STREAM,
            id = this.id,
            name = this.name,
            modifiedTime = this.updatedAt.fromRFC3339StringToDate() ?: Date(0L),
            createdTime = this.updatedAt.fromRFC3339StringToDate() ?: Date(0L),
            parentId = this.parent.id,
            size = this.size.toInt(),
            extension = MimeTypeMap.getFileExtensionFromUrl(this.name),
        )
        TYPE_FOLDER -> OmhStorageEntity.OmhFolder(
            id = this.id,
            name = this.name,
            parentId = this.parent.id,
            createdTime = this.createdAt.fromRFC3339StringToDate() ?: Date(0L),
            modifiedTime = this.updatedAt.fromRFC3339StringToDate() ?: Date(0L)
        )
        else -> throw IllegalArgumentException("Unknown type: ${this.type}")
    }
}
