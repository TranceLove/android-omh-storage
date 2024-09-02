package com.openmobilehub.android.storage.core.utils

import com.openmobilehub.android.storage.core.OmhStorageClient
import com.openmobilehub.android.storage.core.model.OmhStorageEntity

suspend fun OmhStorageClient.folderSize(folderId: String): Long {
    var retval = 0L
    val entries = listFiles(folderId)
    for (entry in entries) {
        if (entry is OmhStorageEntity.OmhFile) {
            retval += entry.size ?: 0
        } else if (entry is OmhStorageEntity.OmhFolder) {
            retval += folderSize(entry.id)
        }
    }
    return retval
}
