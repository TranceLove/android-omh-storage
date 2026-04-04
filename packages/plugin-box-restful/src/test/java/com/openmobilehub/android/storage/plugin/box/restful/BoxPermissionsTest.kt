package com.openmobilehub.android.storage.plugin.box.restful

import com.openmobilehub.android.auth.core.OmhAuthClient
import com.openmobilehub.android.storage.core.model.OmhCreatePermission
import com.openmobilehub.android.storage.core.model.OmhPermission
import com.openmobilehub.android.storage.core.model.OmhPermissionRecipient
import com.openmobilehub.android.storage.core.model.OmhPermissionRole
import com.openmobilehub.android.storage.plugin.box.restful.data.BoxApiService
import com.openmobilehub.android.storage.plugin.box.restful.data.BoxCollaborationApiService
import com.openmobilehub.android.storage.plugin.box.restful.data.BoxUploadApiService
import com.openmobilehub.android.storage.plugin.box.restful.data.repository.BoxRestfulFileRepository
import com.openmobilehub.android.storage.plugin.box.restful.data.source.body.CreateCollaborationRequest
import com.openmobilehub.android.storage.plugin.box.restful.data.source.response.BoxCollaboration
import com.openmobilehub.android.storage.plugin.box.restful.data.source.response.BoxCollaborator
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class BoxPermissionsTest {

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

    @Test
    fun `createPermission should create Box collaboration successfully`() = runTest {
        // Given
        val fileId = "test_file_id"
        val createPermission = OmhCreatePermission.CreateIdentityPermission(
            role = OmhPermissionRole.WRITER,
            recipient = OmhPermissionRecipient.User("user@example.com")
        )

        val mockCollaboration = BoxCollaboration(
            id = "collab_123",
            type = "collaboration",
            accessibleBy = BoxCollaborator(
                id = "user_456",
                type = "user",
                name = "Test User",
                login = "user@example.com"
            ),
            acknowledgedAt = null,
            createdAt = "2023-01-01T00:00:00Z",
            createdBy = null,
            expiresAt = null,
            inviteEmail = "user@example.com",
            isAccessOnly = false,
            item = null,
            modifiedAt = "2023-01-01T00:00:00Z",
            role = "editor",
            status = "accepted"
        )

        coEvery {
            collaborationApiService.createCollaboration(any<CreateCollaborationRequest>())
        } returns Response.success(mockCollaboration)

        // When
        val result = client.createPermission(fileId, createPermission, false, null)

        // Then
        assertNotNull(result)
        assertEquals("collab_123", result?.id)
        assertEquals(OmhPermissionRole.WRITER, result?.role)

        coVerify(exactly = 1) {
            collaborationApiService.createCollaboration(any<CreateCollaborationRequest>())
        }
    }

    @Test
    fun `updatePermission should update collaboration role successfully`() = runTest {
        // Given
        val fileId = "test_file_id"
        val permissionId = "collab_123"
        val newRole = OmhPermissionRole.READER

        val mockUpdatedCollaboration = BoxCollaboration(
            id = permissionId,
            type = "collaboration",
            accessibleBy = BoxCollaborator(
                id = "user_456",
                type = "user",
                name = "Test User",
                login = "user@example.com"
            ),
            acknowledgedAt = null,
            createdAt = "2023-01-01T00:00:00Z",
            createdBy = null,
            expiresAt = null,
            inviteEmail = "user@example.com",
            isAccessOnly = false,
            item = null,
            modifiedAt = "2023-01-01T00:00:00Z",
            role = "viewer", // Updated role
            status = "accepted"
        )

        coEvery {
            collaborationApiService.updateCollaboration(permissionId, any())
        } returns Response.success(mockUpdatedCollaboration)

        // When
        val result = client.updatePermission(fileId, permissionId, newRole)

        // Then
        assertNotNull(result)
        assertEquals(permissionId, result?.id)
        assertEquals(OmhPermissionRole.READER, result?.role)

        coVerify(exactly = 1) {
            collaborationApiService.updateCollaboration(permissionId, any())
        }
    }

    @Test
    fun `deletePermission should delete collaboration successfully`() = runTest {
        // Given
        val fileId = "test_file_id"
        val permissionId = "collab_123"

        coEvery {
            collaborationApiService.deleteCollaboration(permissionId)
        } returns Response.success(Unit)

        // When & Then (should not throw exception)
        client.deletePermission(fileId, permissionId)

        coVerify(exactly = 1) {
            collaborationApiService.deleteCollaboration(permissionId)
        }
    }

    @Test
    fun `getFilePermissions should return empty list for now`() = runTest {
        // Given
        val fileId = "test_file_id"

        coEvery {
            apiService.getFileCollaborations(fileId, any())
        } returns Response.success(mockk(relaxed = true))

        // When
        val result = client.getFilePermissions(fileId)

        // Then
        // Currently returns empty list as Box API structure is complex
        assertEquals(emptyList<OmhPermission>(), result)

        coVerify(exactly = 1) {
            apiService.getFileCollaborations(fileId, any())
        }
    }
}
