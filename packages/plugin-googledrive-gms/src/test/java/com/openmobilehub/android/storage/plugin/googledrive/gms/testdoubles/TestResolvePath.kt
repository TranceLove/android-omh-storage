package com.openmobilehub.android.storage.plugin.googledrive.gms.testdoubles

import com.google.api.client.util.DateTime
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import com.openmobilehub.android.storage.plugin.googledrive.gms.GoogleDriveGmsConstants
import io.mockk.every
import io.mockk.mockk

val testQueryRootFolder = mockk<FileList>(relaxed = true).also { result ->
    every { result.files } returns listOf(
        mockk<File>().also { file ->
            every { file.id } returns "id of folder /RSX"
            every { file.name } returns "RSX"
            every { file.createdTime } returns DateTime(TEST_FILE_CREATED_TIME)
            every { file.modifiedTime } returns DateTime(TEST_FILE_MODIFIED_TIME)
            every { file.parents } returns listOf(GoogleDriveGmsConstants.ROOT_FOLDER)
            every { file.mimeType } returns GoogleDriveGmsConstants.FOLDER_MIME_TYPE
            every { file.size } returns 0
        }
    )
}
val testQueryFolderRsx = mockk<FileList>(relaxed = true).also { result ->
    every { result.files } returns listOf(
        mockk<File>().also { file ->
            every { file.id } returns "id of folder /RSX/1"
            every { file.name } returns "1"
            every { file.createdTime } returns DateTime(TEST_FILE_CREATED_TIME)
            every { file.modifiedTime } returns DateTime(TEST_FILE_MODIFIED_TIME)
            every { file.parents } returns listOf("id of folder /RSX")
            every { file.mimeType } returns GoogleDriveGmsConstants.FOLDER_MIME_TYPE
            every { file.size } returns 0
        }
    )
}

val testQueryFolder1 = mockk<FileList>(relaxed = true).also { result ->
    every { result.files } returns listOf(
        mockk<File>().also { file ->
            every { file.id } returns "id of folder /RSX/1/2"
            every { file.name } returns "2"
            every { file.createdTime } returns DateTime(TEST_FILE_CREATED_TIME)
            every { file.modifiedTime } returns DateTime(TEST_FILE_MODIFIED_TIME)
            every { file.parents } returns listOf("id of folder /RSX/1")
            every { file.mimeType } returns GoogleDriveGmsConstants.FOLDER_MIME_TYPE
            every { file.size } returns 0
        }
    )
}

val testQueryFolder2 = mockk<FileList>(relaxed = true).also { result ->
    every { result.files } returns listOf(
        mockk<File>().also { file ->
            every { file.id } returns "id of folder /RSX/1/2/3"
            every { file.name } returns "3"
            every { file.createdTime } returns DateTime(TEST_FILE_CREATED_TIME)
            every { file.modifiedTime } returns DateTime(TEST_FILE_MODIFIED_TIME)
            every { file.parents } returns listOf("id of folder /RSX/1/2")
            every { file.mimeType } returns GoogleDriveGmsConstants.FOLDER_MIME_TYPE
            every { file.size } returns 0
        }
    )
}

val testQueryFolder3 = mockk<FileList>(relaxed = true).also { result ->
    every { result.files } returns listOf(
        testFileJpg()
    )
}

fun testFileJpg(): File = mockk<File>(relaxed = true).also { file ->
    every { file.id } returns "id of file /RSX/1/2/3/testfile.jpg"
    every { file.name } returns "testfile.jpg"
    every { file.createdTime } returns DateTime(TEST_FILE_CREATED_TIME)
    every { file.modifiedTime } returns DateTime(TEST_FILE_MODIFIED_TIME)
    every { file.parents } returns listOf("id of folder /RSX/1/2/3")
    every { file.mimeType } returns "image/jpeg"
    every { file.size } returns 12345
}
