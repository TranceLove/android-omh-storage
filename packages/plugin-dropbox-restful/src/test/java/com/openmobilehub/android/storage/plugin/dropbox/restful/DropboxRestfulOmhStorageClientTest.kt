package com.openmobilehub.android.storage.plugin.dropbox.restful

import com.openmobilehub.android.storage.core.model.OmhStorageEntity
import com.openmobilehub.android.storage.plugin.dropbox.restful.data.repository.DropboxRestfulFileRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DropboxRestfulOmhStorageClientTest {

    @Test
    fun `test rootFolder`() {
        val client = DropboxRestfulOmhStorageClient(mockk(), mockk())
        assertEquals("", client.rootFolder)
    }

    @Test
    fun `given valid id and newName, rename delegates to repository and returns result`() = runTest {
        val fileId = "id:file1"
        val newName = "renamed.txt"
        val expected = mockk<OmhStorageEntity.OmhFile>()
        val fileRepository = mockk<DropboxRestfulFileRepository>()
        coEvery { fileRepository.rename(fileId, newName) } returns expected

        val client = DropboxRestfulOmhStorageClient(mockk(), fileRepository)
        val result = client.rename(fileId, newName)

        coVerify(exactly = 1) { fileRepository.rename(fileId, newName) }
        assertEquals(expected, result)
    }

    @Test
    fun `given valid id and newName for folder, rename delegates to repository and returns OmhFolder`() = runTest {
        val folderId = "id:folder1"
        val newName = "Renamed Folder"
        val expected = mockk<OmhStorageEntity.OmhFolder>()
        val fileRepository = mockk<DropboxRestfulFileRepository>()
        coEvery { fileRepository.rename(folderId, newName) } returns expected

        val client = DropboxRestfulOmhStorageClient(mockk(), fileRepository)
        val result = client.rename(folderId, newName)

        coVerify(exactly = 1) { fileRepository.rename(folderId, newName) }
        assertEquals(expected, result)
    }
}
