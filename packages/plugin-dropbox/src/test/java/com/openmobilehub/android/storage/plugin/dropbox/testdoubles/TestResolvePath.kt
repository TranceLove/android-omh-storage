package com.openmobilehub.android.storage.plugin.dropbox.testdoubles

import com.dropbox.core.v2.files.FileMetadata
import com.dropbox.core.v2.files.FolderMetadata
import com.dropbox.core.v2.files.ListFolderResult
import io.mockk.every
import io.mockk.mockk

val testQueryRootFolder = mockk<ListFolderResult>(relaxed = true).also { result ->
    every { result.entries } returns listOf(
        mockk<FolderMetadata>().also { folder ->
            every { folder.id } returns "id of folder /RSX"
            every { folder.name } returns "RSX"
            every { folder.parentSharedFolderId } returns ""
        }
    )
}
val testQueryFolderRsx = mockk<ListFolderResult>(relaxed = true).also { result ->
    every { result.entries } returns listOf(
        mockk<FolderMetadata>().also { folder ->
            every { folder.id } returns "id of folder /RSX/1"
            every { folder.name } returns "1"
            every { folder.parentSharedFolderId } returns "id of folder /RSX"
        }
    )
}

val testQueryFolder1 = mockk<ListFolderResult>(relaxed = true).also { result ->
    every { result.entries } returns listOf(
        mockk<FolderMetadata>().also { folder ->
            every { folder.id } returns "id of folder /RSX/1/2"
            every { folder.name } returns "2"
            every { folder.parentSharedFolderId } returns "id of folder /RSX/1"
        }
    )
}

val testQueryFolder2 = mockk<ListFolderResult>(relaxed = true).also { result ->
    every { result.entries } returns listOf(
        mockk<FolderMetadata>().also { folder ->
            every { folder.id } returns "id of folder /RSX/1/2/3"
            every { folder.name } returns "3"
            every { folder.parentSharedFolderId } returns "id of folder /RSX/1/2"
        }
    )
}

val testQueryFolder3 = mockk<ListFolderResult>(relaxed = true).also { result ->
    every { result.entries } returns listOf(
        testFileJpg()
    )
}

fun testFileJpg(): FileMetadata = FileMetadata(
    "testfile.jpg",
    "id of file /RSX/1/2/3/testfile.jpg",
    TEST_FILE_MODIFIED_TIME,
    TEST_FILE_MODIFIED_TIME,
    "000000000000000000000",
    12345L
)
