package com.openmobilehub.android.storage.plugin.box.restful.data.source.mapper

import com.openmobilehub.android.storage.core.model.OmhFileVersion
import com.openmobilehub.android.storage.core.model.OmhStorageEntity
import com.openmobilehub.android.storage.plugin.box.restful.data.source.response.BoxFile
import com.openmobilehub.android.storage.plugin.box.restful.data.source.response.BoxFileVersion
import com.openmobilehub.android.storage.plugin.box.restful.data.source.response.BoxFolder
import com.openmobilehub.android.storage.plugin.box.restful.data.source.response.BoxItem
import java.time.ZonedDateTime
import java.util.Date

fun BoxFile.toOmhFile(): OmhStorageEntity.OmhFile {
    return OmhStorageEntity.OmhFile(
        id = id,
        name = name,
        createdTime = createdAt?.let { parseBoxDateTimeToDate(it) },
        modifiedTime = modifiedAt?.let { parseBoxDateTimeToDate(it) },
        parentId = parent?.id,
        mimeType = null, // Box doesn't provide MIME type in basic file info
        extension = name.substringAfterLast('.', "").takeIf { it != name },
        size = size?.toInt()
    )
}

fun BoxFolder.toOmhFolder(): OmhStorageEntity.OmhFolder {
    return OmhStorageEntity.OmhFolder(
        id = id,
        name = name,
        createdTime = createdAt?.let { parseBoxDateTimeToDate(it) },
        modifiedTime = modifiedAt?.let { parseBoxDateTimeToDate(it) },
        parentId = parent?.id
    )
}

fun BoxItem.toOmhStorageEntity(): OmhStorageEntity {
    return when (type) {
        "file" -> OmhStorageEntity.OmhFile(
            id = id,
            name = name ?: "",
            createdTime = createdAt?.let { parseBoxDateTimeToDate(it) },
            modifiedTime = modifiedAt?.let { parseBoxDateTimeToDate(it) },
            parentId = parent?.id,
            mimeType = null,
            extension = name?.substringAfterLast('.', "")?.takeIf { it != name },
            size = size?.toInt()
        )
        "folder" -> OmhStorageEntity.OmhFolder(
            id = id,
            name = name ?: "",
            createdTime = createdAt?.let { parseBoxDateTimeToDate(it) },
            modifiedTime = modifiedAt?.let { parseBoxDateTimeToDate(it) },
            parentId = parent?.id
        )
        else -> throw IllegalArgumentException("Unknown Box item type: $type")
    }
}

fun BoxFileVersion.toOmhFileVersion(fileId: String): OmhFileVersion {
    return OmhFileVersion(
        fileId = fileId,
        versionId = id,
        lastModified = modifiedAt?.let { parseBoxDateTimeToDate(it) } ?: Date()
    )
}

private fun parseBoxDateTimeToDate(dateTimeString: String): Date {
    val zonedDateTime = ZonedDateTime.parse(dateTimeString)
    return Date.from(zonedDateTime.toInstant())
}
