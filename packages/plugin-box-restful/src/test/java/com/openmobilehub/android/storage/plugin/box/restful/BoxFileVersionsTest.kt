package com.openmobilehub.android.storage.plugin.box.restful

import com.openmobilehub.android.auth.core.OmhAuthClient
import com.openmobilehub.android.storage.plugin.box.restful.data.BoxApiService
import com.openmobilehub.android.storage.plugin.box.restful.data.BoxCollaborationApiService
import com.openmobilehub.android.storage.plugin.box.restful.data.BoxUploadApiService
import com.openmobilehub.android.storage.plugin.box.restful.data.repository.BoxRestfulFileRepository
import com.openmobilehub.android.storage.plugin.box.restful.data.source.response.BoxFileVersion
import com.openmobilehub.android.storage.plugin.box.restful.data.source.response.BoxOrderBy
import com.openmobilehub.android.storage.plugin.box.restful.data.source.response.BoxUser
import com.openmobilehub.android.storage.plugin.box.restful.data.source.response.FileVersionsResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.junit.Test
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BoxFileVersionsTest {

    private val mockAuthClient = mockk<OmhAuthClient>()
    private val mockBoxApiService = mockk<BoxApiService>()
    private val mockBoxUploadApiService = mockk<BoxUploadApiService>()
    private val mockBoxCollaborationApiService = mockk<BoxCollaborationApiService>()

    private val repository = BoxRestfulFileRepository(
        mockBoxApiService,
        mockBoxUploadApiService,
        mockBoxCollaborationApiService
    )
    private val client = BoxRestfulOmhStorageClient(mockAuthClient, repository)

    @Test
    @Suppress("LongMethod")
    fun `should successfully get file versions`() = runTest {
        // Mock file versions response
        val mockUser = BoxUser(
            id = "user123",
            type = "user",
            name = "Test User",
            login = "test@example.com"
        )

        val mockVersions = listOf(
            BoxFileVersion(
                id = "version1",
                type = "file_version",
                createdAt = "2023-01-01T10:00:00Z",
                modifiedAt = "2023-01-01T10:00:00Z",
                modifiedBy = mockUser,
                name = "document_v1.pdf",
                purgedAt = null,
                restoredAt = null,
                restoredBy = null,
                sha1 = "abc123",
                size = 1024L,
                trashedAt = null,
                trashedBy = null,
                uploaderDisplayName = "Test User",
                versionNumber = "1"
            ),
            BoxFileVersion(
                id = "version2",
                type = "file_version",
                createdAt = "2023-01-02T10:00:00Z",
                modifiedAt = "2023-01-02T10:00:00Z",
                modifiedBy = mockUser,
                name = "document_v2.pdf",
                purgedAt = null,
                restoredAt = null,
                restoredBy = null,
                sha1 = "def456",
                size = 2048L,
                trashedAt = null,
                trashedBy = null,
                uploaderDisplayName = "Test User",
                versionNumber = "2"
            )
        )

        val mockResponse = FileVersionsResponse(
            entries = mockVersions,
            limit = 1000,
            offset = 0,
            totalCount = 2,
            order = listOf(BoxOrderBy("created_at", "ASC"))
        )

        coEvery {
            mockBoxApiService.getFileVersions("file123")
        } returns Response.success(mockResponse)

        // Test
        val result = client.getFileVersions("file123")

        // Verify
        assertEquals(2, result.size)

        val firstVersion = result[0]
        assertEquals("version1", firstVersion.versionId)
        assertNotNull(firstVersion.lastModified)

        val secondVersion = result[1]
        assertEquals("version2", secondVersion.versionId)
    }

    @Test
    fun `should successfully download file version`() = runTest {
        val fileId = "file123"
        val versionId = "version1"
        val mockFileContent = "Mock file content".toByteArray()

        // Mock getting file version info (validation)
        val mockVersion = BoxFileVersion(
            id = versionId,
            type = "file_version",
            createdAt = "2023-01-01T10:00:00Z",
            modifiedAt = "2023-01-01T10:00:00Z",
            modifiedBy = null,
            name = "document.pdf",
            purgedAt = null,
            restoredAt = null,
            restoredBy = null,
            sha1 = "abc123",
            size = 1024L,
            trashedAt = null,
            trashedBy = null,
            uploaderDisplayName = "Test User",
            versionNumber = "1"
        )

        coEvery {
            mockBoxApiService.getFileVersion(fileId, versionId)
        } returns Response.success(mockVersion)

        // Mock downloading file version content
        val mockResponseBody = mockk<ResponseBody>()
        coEvery {
            mockResponseBody.byteStream()
        } returns mockFileContent.inputStream()

        coEvery {
            mockBoxApiService.downloadFileVersion(fileId, versionId)
        } returns Response.success(mockResponseBody)

        // Test
        val result = client.downloadFileVersion(fileId, versionId)

        // Verify
        assertTrue(result.size() > 0)
        val downloadedContent = result.toByteArray()
        assertEquals(mockFileContent.size, downloadedContent.size)
    }

    @Test
    fun `should handle empty file versions list`() = runTest {
        val mockResponse = FileVersionsResponse(
            entries = emptyList(),
            limit = 1000,
            offset = 0,
            totalCount = 0,
            order = null
        )

        coEvery {
            mockBoxApiService.getFileVersions("file123")
        } returns Response.success(mockResponse)

        // Test
        val result = client.getFileVersions("file123")

        // Verify
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should map Box file version to OMH file version correctly`() = runTest {
        val mockUser = BoxUser(
            id = "user123",
            type = "user",
            name = "Test User",
            login = "test@example.com"
        )

        val boxVersion = BoxFileVersion(
            id = "version123",
            type = "file_version",
            createdAt = "2023-01-01T10:00:00Z",
            modifiedAt = "2023-01-02T15:30:00Z",
            modifiedBy = mockUser,
            name = "test_document.pdf",
            purgedAt = null,
            restoredAt = null,
            restoredBy = null,
            sha1 = "abcdef123456",
            size = 5120L,
            trashedAt = null,
            trashedBy = null,
            uploaderDisplayName = "Test User",
            versionNumber = "3"
        )

        val mockResponse = FileVersionsResponse(
            entries = listOf(boxVersion),
            limit = 1000,
            offset = 0,
            totalCount = 1,
            order = null
        )

        coEvery {
            mockBoxApiService.getFileVersions("file123")
        } returns Response.success(mockResponse)

        // Test
        val result = client.getFileVersions("file123")

        // Verify mapping
        assertEquals(1, result.size)
        val omhVersion = result[0]

        assertEquals("version123", omhVersion.versionId)
        assertNotNull(omhVersion.lastModified)
    }
}
