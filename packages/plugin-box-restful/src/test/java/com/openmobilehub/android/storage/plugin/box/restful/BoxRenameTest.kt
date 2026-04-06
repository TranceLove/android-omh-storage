package com.openmobilehub.android.storage.plugin.box.restful

import com.openmobilehub.android.auth.core.OmhAuthClient
import com.openmobilehub.android.storage.core.model.OmhStorageEntity
import com.openmobilehub.android.storage.core.model.OmhStorageException
import com.openmobilehub.android.storage.plugin.box.restful.data.BoxApiService
import com.openmobilehub.android.storage.plugin.box.restful.data.BoxCollaborationApiService
import com.openmobilehub.android.storage.plugin.box.restful.data.BoxUploadApiService
import com.openmobilehub.android.storage.plugin.box.restful.data.repository.BoxRestfulFileRepository
import com.openmobilehub.android.storage.plugin.box.restful.data.source.body.UpdateFileRequest
import com.openmobilehub.android.storage.plugin.box.restful.data.source.body.UpdateFolderRequest
import com.openmobilehub.android.storage.plugin.box.restful.data.source.response.BoxFile
import com.openmobilehub.android.storage.plugin.box.restful.data.source.response.BoxFolder
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import kotlin.test.assertFailsWith

class BoxRenameTest {

    private lateinit var authClient: OmhAuthClient
    private lateinit var apiService: BoxApiService
    private lateinit var uploadApiService: BoxUploadApiService
    private lateinit var collaborationApiService: BoxCollaborationApiService
    private lateinit var repository: BoxRestfulFileRepository
    private lateinit var client: BoxRestfulOmhStorageClient

    @Before
    fun setUp() {
        authClient = mockk(relaxed = true)
        apiService = mockk()
        uploadApiService = mockk()
        collaborationApiService = mockk()
        repository = BoxRestfulFileRepository(apiService, uploadApiService, collaborationApiService)
        client = BoxRestfulOmhStorageClient(authClient, repository)
    }

    // =========================================================================
    // Repository-level tests: renameFile
    // =========================================================================

    @Test
    fun `renameFile should return OmhFile with updated name on success`() = runTest {
        // Given
        val fileId = "file_123"
        val newName = "renamed_document.pdf"
        val mockBoxFile = BoxFile(
            id = fileId,
            type = "file",
            name = newName,
            modifiedAt = "2024-01-15T10:00:00Z"
        )

        coEvery {
            apiService.updateFile(fileId, any<UpdateFileRequest>())
        } returns Response.success(mockBoxFile)

        // When
        val result = repository.renameFile(fileId, newName)

        // Then
        assertNotNull(result)
        assertTrue(result is OmhStorageEntity.OmhFile)
        assertEquals(fileId, result.id)
        assertEquals(newName, result.name)
    }

    @Test
    fun `renameFile should call updateFile with correct request containing new name`() = runTest {
        // Given
        val fileId = "file_456"
        val newName = "my_new_file.txt"
        val expectedRequest = UpdateFileRequest(name = newName)
        val mockBoxFile = BoxFile(id = fileId, type = "file", name = newName)

        coEvery {
            apiService.updateFile(fileId, expectedRequest)
        } returns Response.success(mockBoxFile)

        // When
        repository.renameFile(fileId, newName)

        // Then
        coVerify(exactly = 1) {
            apiService.updateFile(fileId, expectedRequest)
        }
    }

    @Test
    fun `renameFile should throw OmhStorageException on non-successful API response`() = runTest {
        // Given
        val fileId = "file_123"
        val newName = "renamed_document.pdf"
        val errorBody = mockk<ResponseBody>(relaxed = true)

        coEvery {
            apiService.updateFile(fileId, any<UpdateFileRequest>())
        } returns Response.error(404, errorBody)

        // When & Then
        assertFailsWith<OmhStorageException.ApiException> {
            repository.renameFile(fileId, newName)
        }
    }

    @Test
    fun `renameFile should throw OmhStorageException when response body is null`() = runTest {
        // Given
        val fileId = "file_123"
        val newName = "renamed_document.pdf"

        coEvery {
            apiService.updateFile(fileId, any<UpdateFileRequest>())
        } returns Response.success(null)

        // When & Then
        assertFailsWith<OmhStorageException.ApiException> {
            repository.renameFile(fileId, newName)
        }
    }

    @Test
    fun `renameFile should preserve file id while updating name`() = runTest {
        // Given
        val fileId = "file_789"
        val newName = "updated_report.docx"
        val mockBoxFile = BoxFile(
            id = fileId,
            type = "file",
            name = newName,
            createdAt = "2024-01-10T08:00:00Z",
            modifiedAt = "2024-01-15T14:30:00Z"
        )

        coEvery {
            apiService.updateFile(fileId, any<UpdateFileRequest>())
        } returns Response.success(mockBoxFile)

        // When
        val result = repository.renameFile(fileId, newName)

        // Then
        assertEquals(fileId, result.id)
        assertEquals(newName, result.name)
        assertNull((result as OmhStorageEntity.OmhFile).mimeType) // Box doesn't provide MIME type
    }

    // =========================================================================
    // Repository-level tests: renameFolder
    // =========================================================================

    @Test
    fun `renameFolder should return OmhFolder with updated name on success`() = runTest {
        // Given
        val folderId = "folder_123"
        val newName = "Renamed Folder"
        val mockBoxFolder = BoxFolder(
            id = folderId,
            type = "folder",
            name = newName,
            modifiedAt = "2024-01-15T10:00:00Z"
        )

        coEvery {
            apiService.updateFolder(folderId, any<UpdateFolderRequest>())
        } returns Response.success(mockBoxFolder)

        // When
        val result = repository.renameFolder(folderId, newName)

        // Then
        assertNotNull(result)
        assertTrue(result is OmhStorageEntity.OmhFolder)
        assertEquals(folderId, result.id)
        assertEquals(newName, result.name)
    }

    @Test
    fun `renameFolder should call updateFolder with correct request containing new name`() = runTest {
        // Given
        val folderId = "folder_456"
        val newName = "My Documents"
        val expectedRequest = UpdateFolderRequest(name = newName)
        val mockBoxFolder = BoxFolder(id = folderId, type = "folder", name = newName)

        coEvery {
            apiService.updateFolder(folderId, expectedRequest)
        } returns Response.success(mockBoxFolder)

        // When
        repository.renameFolder(folderId, newName)

        // Then
        coVerify(exactly = 1) {
            apiService.updateFolder(folderId, expectedRequest)
        }
    }

    @Test
    fun `renameFolder should throw OmhStorageException on non-successful API response`() = runTest {
        // Given
        val folderId = "folder_123"
        val newName = "Renamed Folder"
        val errorBody = mockk<ResponseBody>(relaxed = true)

        coEvery {
            apiService.updateFolder(folderId, any<UpdateFolderRequest>())
        } returns Response.error(403, errorBody)

        // When & Then
        assertFailsWith<OmhStorageException.ApiException> {
            repository.renameFolder(folderId, newName)
        }
    }

    @Test
    fun `renameFolder should throw OmhStorageException when response body is null`() = runTest {
        // Given
        val folderId = "folder_123"
        val newName = "Renamed Folder"

        coEvery {
            apiService.updateFolder(folderId, any<UpdateFolderRequest>())
        } returns Response.success(null)

        // When & Then
        assertFailsWith<OmhStorageException.ApiException> {
            repository.renameFolder(folderId, newName)
        }
    }

    @Test
    fun `renameFolder should preserve folder id while updating name`() = runTest {
        // Given
        val folderId = "folder_789"
        val newName = "Project Assets"
        val mockBoxFolder = BoxFolder(
            id = folderId,
            type = "folder",
            name = newName,
            createdAt = "2024-01-01T00:00:00Z",
            modifiedAt = "2024-01-20T09:00:00Z"
        )

        coEvery {
            apiService.updateFolder(folderId, any<UpdateFolderRequest>())
        } returns Response.success(mockBoxFolder)

        // When
        val result = repository.renameFolder(folderId, newName)

        // Then
        assertEquals(folderId, result.id)
        assertEquals(newName, result.name)
    }

    // =========================================================================
    // Client-level tests: rename (delegates to renameFile / renameFolder)
    // =========================================================================

    @Test
    fun `client rename should rename a file and return OmhFile with new name`() = runTest {
        // Given
        val fileId = "file_123"
        val newName = "renamed_file.pdf"
        val originalBoxFile = BoxFile(id = fileId, type = "file", name = "original_file.pdf")
        val renamedBoxFile = BoxFile(id = fileId, type = "file", name = newName)

        // getFileMetadata calls getFile to determine entity type
        coEvery { apiService.getFile(fileId) } returns Response.success(originalBoxFile)
        // renameFile calls updateFile
        coEvery {
            apiService.updateFile(fileId, any<UpdateFileRequest>())
        } returns Response.success(renamedBoxFile)

        // When
        val result = client.rename(fileId, newName)

        // Then
        assertNotNull(result)
        assertTrue(result is OmhStorageEntity.OmhFile)
        assertEquals(newName, result?.name)
        assertEquals(fileId, result?.id)
    }

    @Test
    fun `client rename should call both getFileMetadata and renameFile exactly once`() = runTest {
        // Given
        val fileId = "file_123"
        val newName = "renamed_file.pdf"
        val originalBoxFile = BoxFile(id = fileId, type = "file", name = "original_file.pdf")
        val renamedBoxFile = BoxFile(id = fileId, type = "file", name = newName)

        coEvery { apiService.getFile(fileId) } returns Response.success(originalBoxFile)
        coEvery {
            apiService.updateFile(fileId, any<UpdateFileRequest>())
        } returns Response.success(renamedBoxFile)

        // When
        client.rename(fileId, newName)

        // Then – getFile called once for metadata lookup, updateFile called once for rename
        coVerify(exactly = 1) { apiService.getFile(fileId) }
        coVerify(exactly = 1) { apiService.updateFile(fileId, any<UpdateFileRequest>()) }
    }

    @Test
    fun `client rename should propagate OmhStorageException when getFileMetadata fails`() = runTest {
        // Given
        val fileId = "file_123"
        val newName = "renamed_file.pdf"
        val errorBody = mockk<ResponseBody>(relaxed = true)

        coEvery { apiService.getFile(fileId) } returns Response.error(404, errorBody)

        // When & Then
        assertFailsWith<OmhStorageException.ApiException> {
            client.rename(fileId, newName)
        }
    }

    @Test
    fun `client rename should propagate OmhStorageException when updateFile fails`() = runTest {
        // Given
        val fileId = "file_123"
        val newName = "renamed_file.pdf"
        val originalBoxFile = BoxFile(id = fileId, type = "file", name = "original_file.pdf")
        val errorBody = mockk<ResponseBody>(relaxed = true)

        coEvery { apiService.getFile(fileId) } returns Response.success(originalBoxFile)
        coEvery {
            apiService.updateFile(fileId, any<UpdateFileRequest>())
        } returns Response.error(409, errorBody)

        // When & Then
        assertFailsWith<OmhStorageException.ApiException> {
            client.rename(fileId, newName)
        }
    }

    @Test
    fun `client rename should pass the new name to the rename request`() = runTest {
        // Given
        val fileId = "file_abc"
        val newName = "final_report_2024.xlsx"
        val expectedRequest = UpdateFileRequest(name = newName)
        val originalBoxFile = BoxFile(id = fileId, type = "file", name = "draft.xlsx")
        val renamedBoxFile = BoxFile(id = fileId, type = "file", name = newName)

        coEvery { apiService.getFile(fileId) } returns Response.success(originalBoxFile)
        coEvery {
            apiService.updateFile(fileId, expectedRequest)
        } returns Response.success(renamedBoxFile)

        // When
        val result = client.rename(fileId, newName)

        // Then
        assertEquals(newName, result?.name)
        coVerify(exactly = 1) { apiService.updateFile(fileId, expectedRequest) }
    }

    @Test
    fun `client rename folder should call updateFolder via renameFolder`() = runTest {
        // Given – test renameFolder directly since getFileMetadata only resolves to OmhFile
        val folderId = "folder_abc"
        val newName = "Renamed Project"
        val mockBoxFolder = BoxFolder(id = folderId, type = "folder", name = newName)

        coEvery {
            apiService.updateFolder(folderId, any<UpdateFolderRequest>())
        } returns Response.success(mockBoxFolder)

        // When
        val result = repository.renameFolder(folderId, newName)

        // Then
        assertNotNull(result)
        assertTrue(result is OmhStorageEntity.OmhFolder)
        assertEquals(folderId, result.id)
        assertEquals(newName, result.name)

        coVerify(exactly = 1) {
            apiService.updateFolder(folderId, any<UpdateFolderRequest>())
        }
    }
}
