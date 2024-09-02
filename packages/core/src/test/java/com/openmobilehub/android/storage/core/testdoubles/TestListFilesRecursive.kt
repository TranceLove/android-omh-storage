package com.openmobilehub.android.storage.core.testdoubles

import com.openmobilehub.android.storage.core.model.OmhStorageEntity
import java.util.Date

val rootFolderList = listOf(
    OmhStorageEntity.OmhFile(
        id = "file1",
        name = "file1.txt",
        createdTime = Date(),
        modifiedTime = Date(),
        parentId = null,
        mimeType = "text/plain",
        extension = "txt",
        size = 1
    ),
    OmhStorageEntity.OmhFile(
        id = "file2",
        name = "file2.txt",
        createdTime = Date(),
        modifiedTime = Date(),
        parentId = null,
        mimeType = "text/plain",
        extension = "txt",
        size = 2
    ),
    OmhStorageEntity.OmhFile(
        id = "file3",
        name = "file3.txt",
        createdTime = Date(),
        modifiedTime = Date(),
        parentId = null,
        mimeType = "text/plain",
        extension = "txt",
        size = 3
    ),
    OmhStorageEntity.OmhFolder(
        id = "folder1",
        name = "folder1",
        createdTime = Date(),
        modifiedTime = Date(),
        parentId = null,
    ),
    OmhStorageEntity.OmhFolder(
        id = "folder2",
        name = "folder2",
        createdTime = Date(),
        modifiedTime = Date(),
        parentId = null,
    ),
)
