package com.openmobilehub.android.storage.plugin.box.restful

import com.openmobilehub.android.auth.core.OmhAuthClient
import com.openmobilehub.android.storage.core.OmhStorageClient
import com.openmobilehub.android.storage.core.model.OmhCreatePermission
import com.openmobilehub.android.storage.core.model.OmhFileVersion
import com.openmobilehub.android.storage.core.model.OmhPermission
import com.openmobilehub.android.storage.core.model.OmhPermissionRole
import com.openmobilehub.android.storage.core.model.OmhStorageEntity
import com.openmobilehub.android.storage.core.model.OmhStorageException
import com.openmobilehub.android.storage.core.model.OmhStorageMetadata
import com.openmobilehub.android.storage.plugin.box.restful.data.repository.BoxRestfulFileRepository
import java.io.ByteArrayOutputStream
import java.io.File

@Suppress("TooManyFunctions")
class BoxRestfulOmhStorageClient(
    authClient: OmhAuthClient,
    private val fileRepository: BoxRestfulFileRepository
) : OmhStorageClient(authClient) {

    override val rootFolder: String = Constants.ROOT_FOLDER

    override suspend fun getStorageQuota(): Long = fileRepository.getStorageAmount()

    override suspend fun getStorageUsage(): Long = fileRepository.getStorageUsed()

    override suspend fun getFileMetadata(fileId: String): OmhStorageMetadata? {
        return fileRepository.getFileMetadata(fileId)
    }

    override suspend fun getWebUrl(fileId: String): String? {
        return fileRepository.getWebUrl(fileId)
    }

    override suspend fun resolvePath(path: String): OmhStorageEntity? {
        return fileRepository.resolvePath(path)
    }

    override fun getProviderSdk(): Any {
        return "Box API v2.0"
    }

    override suspend fun listFiles(parentId: String): List<OmhStorageEntity> {
        return fileRepository.getFilesList(parentId)
    }

    override suspend fun search(query: String): List<OmhStorageEntity> {
        return fileRepository.search(query)
    }

    override suspend fun createFileWithMimeType(
        name: String,
        mimeType: String,
        parentId: String
    ): OmhStorageEntity? {
        throw UnsupportedOperationException(
            "Box does not support creating files with mime types. Use uploadFile instead."
        )
    }

    override suspend fun createFileWithExtension(
        name: String,
        extension: String,
        parentId: String
    ): OmhStorageEntity? {
        throw UnsupportedOperationException(
            "Box does not support creating files with extensions. Use uploadFile instead."
        )
    }

    override suspend fun createFolder(name: String, parentId: String): OmhStorageEntity {
        return fileRepository.createFolder(name, parentId)
    }

    override suspend fun deleteFile(fileId: String) {
        fileRepository.deleteFile(fileId)
    }

    suspend fun permanentlyDeleteFolder(folderId: String) {
        fileRepository.deleteFolder(folderId)
    }

    override suspend fun permanentlyDeleteFile(fileId: String) {
        fileRepository.deleteFile(fileId)
    }

    override suspend fun uploadFile(
        localFileToUpload: File,
        parentId: String?
    ): OmhStorageEntity? {
        return fileRepository.uploadFile(localFileToUpload, localFileToUpload.name, parentId ?: rootFolder)
    }

    override suspend fun downloadFile(fileId: String): ByteArrayOutputStream {
        return fileRepository.downloadFile(fileId)
    }

    override suspend fun exportFile(fileId: String, exportedMimeType: String): ByteArrayOutputStream {
        return fileRepository.exportFile(fileId, exportedMimeType)
    }

    override suspend fun updateFile(localFileToUpload: File, fileId: String): OmhStorageEntity? {
        // For Box, we'll implement this as upload to replace the existing file
        // This is a simplified implementation
        throw UnsupportedOperationException("File update with local file is not implemented for Box plugin")
    }

    override suspend fun getFileVersions(fileId: String): List<OmhFileVersion> {
        return fileRepository.getFileVersions(fileId)
    }

    override suspend fun downloadFileVersion(
        fileId: String,
        versionId: String
    ): ByteArrayOutputStream {
        return fileRepository.downloadFileVersion(fileId, versionId)
    }

    override suspend fun getFilePermissions(fileId: String): List<OmhPermission> {
        return fileRepository.getFilePermissions(fileId)
    }

    override suspend fun deletePermission(
        fileId: String,
        permissionId: String
    ) {
        try {
            fileRepository.deleteFilePermission(fileId, permissionId)
        } catch (e: OmhStorageException.ApiException) {
            // Box API doesn't return the deleted collaboration, so we expect this
            // If the message indicates success, we don't throw
            if (!e.message?.contains("deleted successfully", ignoreCase = true)!!) {
                throw e
            }
        }
    }

    override suspend fun updatePermission(
        fileId: String,
        permissionId: String,
        role: OmhPermissionRole
    ): OmhPermission? {
        return fileRepository.updateFilePermission(fileId, permissionId, role)
    }

    override suspend fun createPermission(
        fileId: String,
        permission: OmhCreatePermission,
        sendNotificationEmail: Boolean,
        emailMessage: String?
    ): OmhPermission? {
        // Note: Box API doesn't support sendNotificationEmail and emailMessage parameters
        // in the same way as other providers, but we'll ignore them for now
        return fileRepository.createFilePermission(fileId, permission)
    }
}
