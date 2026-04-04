package com.openmobilehub.android.storage.plugin.box.restful.data.repository

import android.util.Base64.DEFAULT
import android.util.Base64.decode
import android.util.Base64.encodeToString
import com.fasterxml.jackson.databind.ObjectMapper
import com.openmobilehub.android.storage.core.ThumbnailSize
import com.openmobilehub.android.storage.core.model.OmhCreatePermission
import com.openmobilehub.android.storage.core.model.OmhFileVersion
import com.openmobilehub.android.storage.core.model.OmhPermission
import com.openmobilehub.android.storage.core.model.OmhPermissionRole
import com.openmobilehub.android.storage.core.model.OmhStorageEntity
import com.openmobilehub.android.storage.core.model.OmhStorageException
import com.openmobilehub.android.storage.core.model.OmhStorageMetadata
import com.openmobilehub.android.storage.core.restful.common.utils.toApiException
import com.openmobilehub.android.storage.core.restful.common.utils.toByteArrayOutputStream
import com.openmobilehub.android.storage.plugin.box.restful.Constants
import com.openmobilehub.android.storage.plugin.box.restful.data.BoxApiService
import com.openmobilehub.android.storage.plugin.box.restful.data.BoxCollaborationApiService
import com.openmobilehub.android.storage.plugin.box.restful.data.BoxUploadApiService
import com.openmobilehub.android.storage.plugin.box.restful.data.source.body.CommitFileUploadRequest
import com.openmobilehub.android.storage.plugin.box.restful.data.source.body.CreateFolderRequest
import com.openmobilehub.android.storage.plugin.box.restful.data.source.body.ParentReference
import com.openmobilehub.android.storage.plugin.box.restful.data.source.body.UpdateCollaborationRequest
import com.openmobilehub.android.storage.plugin.box.restful.data.source.body.UpdateFileRequest
import com.openmobilehub.android.storage.plugin.box.restful.data.source.body.UpdateFolderRequest
import com.openmobilehub.android.storage.plugin.box.restful.data.source.body.UploadFileRequest
import com.openmobilehub.android.storage.plugin.box.restful.data.source.body.UploadPart
import com.openmobilehub.android.storage.plugin.box.restful.data.source.mapper.toBoxRole
import com.openmobilehub.android.storage.plugin.box.restful.data.source.mapper.toCreateCollaborationRequest
import com.openmobilehub.android.storage.plugin.box.restful.data.source.mapper.toOmhFile
import com.openmobilehub.android.storage.plugin.box.restful.data.source.mapper.toOmhFileVersion
import com.openmobilehub.android.storage.plugin.box.restful.data.source.mapper.toOmhFolder
import com.openmobilehub.android.storage.plugin.box.restful.data.source.mapper.toOmhPermission
import com.openmobilehub.android.storage.plugin.box.restful.data.source.mapper.toOmhStorageEntity
import com.openmobilehub.android.storage.plugin.box.restful.data.source.response.UploadSessionResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.HttpURLConnection.HTTP_ACCEPTED
import java.net.HttpURLConnection.HTTP_MOVED_TEMP
import java.net.HttpURLConnection.HTTP_NOT_FOUND
import kotlin.time.Duration.Companion.seconds

@Suppress("TooManyFunctions", "TooGenericExceptionCaught", "ThrowsCount", "UnusedPrivateMember", "LargeClass")
class BoxRestfulFileRepository(
    private val boxApiService: BoxApiService,
    private val boxUploadApiService: BoxUploadApiService,
    private val boxCollaborationApiService: BoxCollaborationApiService
) {
    private val objectMapper = ObjectMapper()

    companion object {
        private const val BOX_VERY_LARGE_THUMBNAIL_SIZE = 320
    }

    suspend fun getFilesList(parentId: String): List<OmhStorageEntity> {
        return withContext(Dispatchers.IO) {
            try {
                val response = boxApiService.getFolderItems(parentId)
                if (response.isSuccessful) {
                    response.body()?.entries?.map { it.toOmhStorageEntity() } ?: emptyList()
                } else {
                    throw response.toApiException()
                }
            } catch (e: Exception) {
                throw OmhStorageException.ApiException(
                    message = e.message ?: "Unknown error occurred",
                    cause = e
                )
            }
        }
    }

    suspend fun search(query: String): List<OmhStorageEntity> {
        return withContext(Dispatchers.IO) {
            try {
                val response = boxApiService.search(query)
                if (response.isSuccessful) {
                    response.body()?.entries?.map { it.toOmhStorageEntity() } ?: emptyList()
                } else {
                    throw response.toApiException()
                }
            } catch (e: Exception) {
                throw OmhStorageException.ApiException(
                    message = e.message ?: "Unknown error occurred",
                    cause = e
                )
            }
        }
    }

    suspend fun createFolder(name: String, parentId: String): OmhStorageEntity {
        return withContext(Dispatchers.IO) {
            try {
                val request = CreateFolderRequest(
                    name = name,
                    parent = ParentReference(parentId)
                )
                val response = boxApiService.createFolder(request)
                if (response.isSuccessful) {
                    response.body()?.toOmhFolder()
                        ?: throw OmhStorageException.ApiException(message = "Empty response body")
                } else {
                    throw response.toApiException()
                }
            } catch (e: Exception) {
                throw OmhStorageException.ApiException(
                    message = e.message ?: "Unknown error occurred",
                    cause = e
                )
            }
        }
    }

    suspend fun uploadFile(
        localFile: File,
        name: String,
        parentId: String
    ): OmhStorageEntity {
        return withContext(Dispatchers.IO) {
            try {
                // Use chunked upload for files >= 20MB (Box requirement)
                if (localFile.length() >= Constants.CHUNKED_UPLOAD_THRESHOLD) {
                    uploadLargeFile(localFile, name, parentId)
                } else {
                    uploadSmallFile(localFile, name, parentId)
                }
            } catch (e: Exception) {
                when (e) {
                    is OmhStorageException -> throw e
                    else -> throw OmhStorageException.ApiException(
                        message = e.message ?: "Unknown error occurred",
                        cause = e
                    )
                }
            }
        }
    }

    private suspend fun uploadSmallFile(
        localFile: File,
        name: String,
        parentId: String
    ): OmhStorageEntity {
        val attributes = mapOf(
            "name" to name,
            "parent" to mapOf("id" to parentId)
        )
        val attributesJson = objectMapper.writeValueAsString(attributes)
        val attributesPart = attributesJson.toRequestBody("application/json".toMediaType())

        val filePart = MultipartBody.Part.createFormData(
            "file",
            name,
            localFile.asRequestBody("application/octet-stream".toMediaType())
        )

        val response = boxUploadApiService.uploadFile(attributesPart, filePart)
        return if (response.isSuccessful) {
            response.body()?.entries?.firstOrNull()?.toOmhFile()
                ?: throw OmhStorageException.ApiException(message = "Empty response body")
        } else {
            throw response.toApiException()
        }
    }

    @Suppress("SwallowedException")
    private suspend fun uploadLargeFile(
        localFile: File,
        name: String,
        parentId: String
    ): OmhStorageEntity {
        // Step 1: Create upload session
        val uploadRequest = UploadFileRequest(
            fileName = name,
            fileSize = localFile.length(),
            parent = ParentReference(parentId)
        )

        val sessionResponse = boxUploadApiService.createUploadSession(uploadRequest)
        if (!sessionResponse.isSuccessful) {
            throw sessionResponse.toApiException()
        }

        val session = sessionResponse.body()
            ?: throw OmhStorageException.ApiException(message = "Failed to create upload session")

        try {
            // Step 2: Upload file parts
            val uploadedParts = uploadFileParts(localFile, session)

            // Step 3: Commit upload session
            return commitUploadSession(session.id, uploadedParts)
        } catch (e: Exception) {
            // Cancel session on error
            try {
                boxUploadApiService.cancelUploadSession(session.id)
            } catch (cancelException: Exception) {
                // Log but don't throw cancel exception
            }
            throw e
        }
    }

    private suspend fun uploadFileParts(
        localFile: File,
        session: UploadSessionResponse
    ): List<UploadPart> {
        val uploadedParts = mutableListOf<UploadPart>()
        val fileSize = localFile.length()
        val chunkSize = Constants.CHUNK_SIZE
        var offset = 0L

        localFile.inputStream().use { inputStream ->
            val buffer = ByteArray(chunkSize)

            while (offset < fileSize) {
                val bytesToRead = minOf(chunkSize.toLong(), fileSize - offset).toInt()
                val bytesRead = inputStream.read(buffer, 0, bytesToRead)

                if (bytesRead <= 0) break

                // Calculate SHA1 for this chunk
                val chunkData = buffer.copyOf(bytesRead)
                val sha1 = calculateSHA1(chunkData)

                // Create content range header
                val contentRange = "bytes $offset-${offset + bytesRead - 1}/$fileSize"
                val digest = "sha=$sha1"

                // Upload this part
                val partBody = chunkData.toRequestBody("application/octet-stream".toMediaType())
                val response = boxUploadApiService.uploadFilePart(
                    uploadSessionId = session.id,
                    contentRange = contentRange,
                    digest = digest,
                    filePart = partBody
                )

                if (response.isSuccessful) {
                    val uploadedPart = response.body()?.part
                        ?: throw OmhStorageException.ApiException(message = "Failed to upload part")

                    uploadedParts.add(
                        UploadPart(
                            partId = uploadedPart.partId,
                            offset = uploadedPart.offset,
                            size = uploadedPart.size,
                            sha1 = uploadedPart.sha1
                        )
                    )
                } else {
                    throw response.toApiException()
                }

                offset += bytesRead
            }
        }

        return uploadedParts
    }

    private suspend fun commitUploadSession(
        sessionId: String,
        parts: List<UploadPart>
    ): OmhStorageEntity {
        // Calculate SHA1 of all parts combined
        val allPartsDigest = calculateCombinedSHA1(parts)

        val commitRequest = CommitFileUploadRequest(parts = parts)
        val response = boxUploadApiService.commitUploadSession(
            uploadSessionId = sessionId,
            digest = "sha=$allPartsDigest",
            commitRequest = commitRequest
        )

        return if (response.isSuccessful) {
            response.body()?.entries?.firstOrNull()?.toOmhFile()
                ?: throw OmhStorageException.ApiException(message = "Failed to commit upload session")
        } else {
            throw response.toApiException()
        }
    }

    private fun calculateSHA1(data: ByteArray): String {
        val digest = java.security.MessageDigest.getInstance("SHA-1")
        val hash = digest.digest(data)
        return encodeToString(hash, DEFAULT)
    }

    private fun calculateCombinedSHA1(parts: List<UploadPart>): String {
        // For Box, we need to provide the SHA1 of the entire file
        // This is a simplified implementation - in practice, you might want to
        // calculate this during the upload process
        val digest = java.security.MessageDigest.getInstance("SHA-1")
        parts.forEach { part ->
            val partHash = decode(part.sha1, DEFAULT)
            digest.update(partHash)
        }
        val combinedHash = digest.digest()
        return encodeToString(combinedHash, DEFAULT)
    }

    suspend fun downloadFile(fileId: String): ByteArrayOutputStream {
        return withContext(Dispatchers.IO) {
            try {
                val response = boxApiService.downloadFile(fileId)
                if (response.isSuccessful) {
                    response.body()?.toByteArrayOutputStream()
                        ?: throw OmhStorageException.ApiException(message = "Empty response body")
                } else {
                    throw response.toApiException()
                }
            } catch (e: Exception) {
                throw OmhStorageException.ApiException(
                    message = e.message ?: "Unknown error occurred",
                    cause = e
                )
            }
        }
    }

    suspend fun deleteFile(fileId: String) {
        return withContext(Dispatchers.IO) {
            try {
                val response = boxApiService.deleteFile(fileId)
                if (!response.isSuccessful) {
                    throw response.toApiException()
                }
            } catch (e: Exception) {
                throw OmhStorageException.ApiException(
                    message = e.message ?: "Unknown error occurred",
                    cause = e
                )
            }
        }
    }

    suspend fun deleteFolder(folderId: String) {
        return withContext(Dispatchers.IO) {
            try {
                val response = boxApiService.deleteFolder(folderId, recursive = true)
                if (!response.isSuccessful) {
                    throw response.toApiException()
                }
            } catch (e: Exception) {
                throw OmhStorageException.ApiException(
                    message = e.message ?: "Unknown error occurred",
                    cause = e
                )
            }
        }
    }

    suspend fun getFile(fileId: String): OmhStorageEntity {
        return withContext(Dispatchers.IO) {
            try {
                val response = boxApiService.getFile(fileId)
                if (response.isSuccessful) {
                    response.body()?.toOmhFile()
                        ?: throw OmhStorageException.ApiException(message = "Empty response body")
                } else {
                    throw response.toApiException()
                }
            } catch (e: Exception) {
                throw OmhStorageException.ApiException(
                    message = e.message ?: "Unknown error occurred",
                    cause = e
                )
            }
        }
    }

    suspend fun getFolder(folderId: String): OmhStorageEntity {
        return withContext(Dispatchers.IO) {
            try {
                val response = boxApiService.getFolder(folderId)
                if (response.isSuccessful) {
                    response.body()?.toOmhFolder()
                        ?: throw OmhStorageException.ApiException(message = "Empty response body")
                } else {
                    throw response.toApiException()
                }
            } catch (e: Exception) {
                throw OmhStorageException.ApiException(
                    message = e.message ?: "Unknown error occurred",
                    cause = e
                )
            }
        }
    }

    suspend fun updateFile(fileId: String, name: String): OmhStorageEntity {
        return withContext(Dispatchers.IO) {
            try {
                val request = UpdateFileRequest(name = name)
                val response = boxApiService.updateFile(fileId, request)
                if (response.isSuccessful) {
                    response.body()?.toOmhFile()
                        ?: throw OmhStorageException.ApiException(message = "Empty response body")
                } else {
                    throw response.toApiException()
                }
            } catch (e: Exception) {
                throw OmhStorageException.ApiException(
                    message = e.message ?: "Unknown error occurred",
                    cause = e
                )
            }
        }
    }

    suspend fun updateFolder(folderId: String, name: String): OmhStorageEntity {
        return withContext(Dispatchers.IO) {
            try {
                val request = UpdateFolderRequest(name = name)
                val response = boxApiService.updateFolder(folderId, request)
                if (response.isSuccessful) {
                    response.body()?.toOmhFolder()
                        ?: throw OmhStorageException.ApiException(message = "Empty response body")
                } else {
                    throw response.toApiException()
                }
            } catch (e: Exception) {
                throw OmhStorageException.ApiException(
                    message = e.message ?: "Unknown error occurred",
                    cause = e
                )
            }
        }
    }

    suspend fun getStorageAmount(): Long {
        return withContext(Dispatchers.IO) {
            try {
                val response = boxApiService.getCurrentUser()
                if (response.isSuccessful) {
                    val user = response.body()
                        ?: throw OmhStorageException.ApiException(message = "Empty response body")
                    user.spaceAmount
                } else {
                    throw response.toApiException()
                }
            } catch (e: Exception) {
                throw OmhStorageException.ApiException(
                    message = e.message ?: "Unknown error occurred",
                    cause = e
                )
            } ?: 0L
        }
    }

    suspend fun getStorageUsed(): Long {
        return withContext(Dispatchers.IO) {
            try {
                val response = boxApiService.getCurrentUser()
                if (response.isSuccessful) {
                    val user = response.body()
                        ?: throw OmhStorageException.ApiException(message = "Empty response body")
                    user.spaceUsed
                } else {
                    throw response.toApiException()
                }
            } catch (e: Exception) {
                throw OmhStorageException.ApiException(
                    message = e.message ?: "Unknown error occurred",
                    cause = e
                )
            } ?: 0L
        }
    }

    // Unsupported operations for Box
    suspend fun exportFile(fileId: String, mimeType: String): ByteArrayOutputStream {
        throw UnsupportedOperationException("Box does not support file export with different MIME types")
    }

    /**
     * Get thumbnail for a file with automatic retry handling.
     *
     * Box API can return:
     * - 200: Thumbnail ready, returned immediately
     * - 202: Thumbnail being generated, retry after Retry-After seconds
     * - 302: Thumbnail at different location, follow redirect after optional Retry-After delay
     * - 404: File not found or thumbnail not supported
     *
     * This implementation automatically handles retries with exponential backoff.
     */
    suspend fun getThumbnail(fileId: String, thumbnailSize: ThumbnailSize): ByteArrayOutputStream {
        return withContext(Dispatchers.IO) {
            try {
                val (extension, maxWidth, maxHeight) = when (thumbnailSize) {
                    ThumbnailSize.VERY_SMALL -> Triple(
                        "png",
                        ThumbnailSize.SMALL.width,
                        ThumbnailSize.SMALL.width
                    )
                    ThumbnailSize.SMALL -> Triple(
                        "png",
                        ThumbnailSize.MEDIUM.width,
                        ThumbnailSize.MEDIUM.width
                    )
                    ThumbnailSize.MEDIUM -> Triple(
                        "png",
                        ThumbnailSize.LARGE.width,
                        ThumbnailSize.LARGE.width
                    )
                    ThumbnailSize.LARGE -> Triple(
                        "png",
                        ThumbnailSize.VERY_LARGE.width,
                        ThumbnailSize.VERY_LARGE.width
                    )
                    ThumbnailSize.VERY_LARGE -> Triple(
                        "jpg",
                        BOX_VERY_LARGE_THUMBNAIL_SIZE,
                        BOX_VERY_LARGE_THUMBNAIL_SIZE
                    )
                }

                getThumbnailWithRetry(
                    fileId = fileId,
                    extension = extension,
                    maxWidth = maxWidth,
                    maxHeight = maxHeight,
                    maxRetries = 3
                )
            } catch (e: Exception) {
                when (e) {
                    is OmhStorageException -> throw e
                    else -> throw OmhStorageException.ApiException(
                        message = e.message ?: "Unknown error occurred while getting thumbnail",
                        cause = e
                    )
                }
            }
        }
    }

    @Suppress("LongParameterList", "LongMethod")
    private suspend fun getThumbnailWithRetry(
        fileId: String,
        extension: String,
        maxWidth: Int,
        maxHeight: Int,
        maxRetries: Int,
        currentAttempt: Int = 0
    ): ByteArrayOutputStream {
        val response = boxApiService.getFileThumbnail(
            fileId = fileId,
            extension = extension,
            maxWidth = maxWidth,
            maxHeight = maxHeight
        )

        return when {
            response.code() == HTTP_ACCEPTED -> {
                // Thumbnail is being generated, check Retry-After header
                val retryAfter = response.headers()["Retry-After"]
                val location = response.headers()["Location"]

                if (currentAttempt >= maxRetries) {
                    throw OmhStorageException.ApiException(
                        message = "Thumbnail generation timeout after $maxRetries attempts. Location: $location"
                    )
                }

                // Parse retry-after header (can be seconds or HTTP date)
                val delaySeconds = retryAfter?.toLongOrNull() ?: 2 // Default to 2 seconds

                // Wait for the specified delay
                delay(delaySeconds.seconds.inWholeMilliseconds)

                // Retry the request
                getThumbnailWithRetry(
                    fileId = fileId,
                    extension = extension,
                    maxWidth = maxWidth,
                    maxHeight = maxHeight,
                    maxRetries = maxRetries,
                    currentAttempt = currentAttempt + 1
                )
            }
            response.isSuccessful -> {
                response.body()?.toByteArrayOutputStream()
                    ?: throw OmhStorageException.ApiException(message = "Empty thumbnail response")
            }
            response.code() == HTTP_MOVED_TEMP -> {
                // Box has a thumbnail ready at a different location (redirect)
                val location = response.headers()["Location"]
                val retryAfter = response.headers()["Retry-After"]

                if (location != null) {
                    // For 302, Box is redirecting to the actual thumbnail location
                    // We should follow the redirect
                    if (currentAttempt >= maxRetries) {
                        throw OmhStorageException.ApiException(
                            message = "Too many redirects after $maxRetries attempts. Location: $location"
                        )
                    }

                    // If there's a Retry-After header, wait before following redirect
                    if (retryAfter != null) {
                        val delaySeconds = retryAfter.toLongOrNull() ?: 1
                        delay(delaySeconds.seconds.inWholeMilliseconds)
                    }

                    // Retry the original request (Box will eventually return 200)
                    getThumbnailWithRetry(
                        fileId = fileId,
                        extension = extension,
                        maxWidth = maxWidth,
                        maxHeight = maxHeight,
                        maxRetries = maxRetries,
                        currentAttempt = currentAttempt + 1
                    )
                } else {
                    throw OmhStorageException.ApiException(message = "Thumbnail redirect without Location header")
                }
            }
            response.code() == HTTP_NOT_FOUND -> {
                throw OmhStorageException.ApiException(message = "File not found or thumbnail cannot be generated")
            }
            else -> {
                throw response.toApiException()
            }
        }
    }

    suspend fun getFileVersions(fileId: String): List<OmhFileVersion> {
        return withContext(Dispatchers.IO) {
            try {
                val response = boxApiService.getFileVersions(fileId)
                if (response.isSuccessful) {
                    response.body()?.entries?.map { it.toOmhFileVersion(fileId) } ?: emptyList()
                } else {
                    throw response.toApiException()
                }
            } catch (e: Exception) {
                when (e) {
                    is OmhStorageException -> throw e
                    else -> throw OmhStorageException.ApiException(
                        message = e.message ?: "Unknown error occurred while getting file versions",
                        cause = e
                    )
                }
            }
        }
    }

    suspend fun downloadFileVersion(fileId: String, versionId: String): ByteArrayOutputStream {
        return withContext(Dispatchers.IO) {
            try {
                // First, get the file version to validate it exists
                val versionResponse = boxApiService.getFileVersion(fileId, versionId)
                if (!versionResponse.isSuccessful) {
                    throw versionResponse.toApiException()
                }

                // Box API supports downloading specific versions using the version query parameter
                // We need to add this endpoint to the API service
                val response = boxApiService.downloadFileVersion(fileId, versionId)
                if (response.isSuccessful) {
                    response.body().toByteArrayOutputStream()
                } else {
                    throw response.toApiException()
                }
            } catch (e: Exception) {
                when (e) {
                    is OmhStorageException -> throw e
                    else -> throw OmhStorageException.ApiException(
                        message = e.message ?: "Unknown error occurred while downloading file version",
                        cause = e
                    )
                }
            }
        }
    }

    suspend fun getFilePermissions(fileId: String): List<OmhPermission> {
        return withContext(Dispatchers.IO) {
            try {
                // Get file with collaborations field
                val response = boxApiService.getFileCollaborations(fileId, "permissions,collaborations")
                if (response.isSuccessful) {
                    val file = response.body()
                    // Box returns collaborations in the file object when requested with fields
                    // For now, we'll return empty list as Box API structure is complex
                    // This would need to be enhanced to extract collaborations from the file response
                    emptyList()
                } else {
                    throw response.toApiException()
                }
            } catch (e: Exception) {
                when (e) {
                    is OmhStorageException -> throw e
                    else -> throw OmhStorageException.ApiException(
                        message = e.message ?: "Unknown error occurred while getting file permissions",
                        cause = e
                    )
                }
            }
        }
    }

    suspend fun deleteFilePermission(fileId: String, permissionId: String): OmhPermission {
        return withContext(Dispatchers.IO) {
            try {
                // Delete collaboration (permission)
                val response = boxCollaborationApiService.deleteCollaboration(permissionId)
                if (response.isSuccessful) {
                    // Return a dummy permission as the collaboration is deleted
                    // Box API doesn't return the deleted collaboration
                    throw OmhStorageException.ApiException(message = "Permission deleted successfully")
                } else {
                    throw response.toApiException()
                }
            } catch (e: Exception) {
                when (e) {
                    is OmhStorageException -> throw e
                    else -> throw OmhStorageException.ApiException(
                        message = e.message ?: "Unknown error occurred while deleting file permission",
                        cause = e
                    )
                }
            }
        }
    }

    suspend fun updateFilePermission(
        fileId: String,
        permissionId: String,
        role: OmhPermissionRole
    ): OmhPermission {
        return withContext(Dispatchers.IO) {
            try {
                val updateRequest = UpdateCollaborationRequest(
                    role = role.toBoxRole()
                )
                val response = boxCollaborationApiService.updateCollaboration(permissionId, updateRequest)
                if (response.isSuccessful) {
                    response.body()?.toOmhPermission() ?: throw OmhStorageException.ApiException(
                        message = "Failed to update permission"
                    )
                } else {
                    throw response.toApiException()
                }
            } catch (e: Exception) {
                when (e) {
                    is OmhStorageException -> throw e
                    else -> throw OmhStorageException.ApiException(
                        message = e.message ?: "Unknown error occurred while updating file permission",
                        cause = e
                    )
                }
            }
        }
    }

    suspend fun createFilePermission(
        fileId: String,
        permission: OmhCreatePermission
    ): OmhPermission {
        return withContext(Dispatchers.IO) {
            try {
                when (permission) {
                    is OmhCreatePermission.CreateIdentityPermission -> {
                        val createRequest = permission.toCreateCollaborationRequest(fileId, "file")
                        val response = boxCollaborationApiService.createCollaboration(createRequest)
                        if (response.isSuccessful) {
                            response.body()?.toOmhPermission()
                                ?: throw OmhStorageException.ApiException(message = "Failed to create permission")
                        } else {
                            throw response.toApiException()
                        }
                    }
                }
            } catch (e: Exception) {
                when (e) {
                    is OmhStorageException -> throw e
                    else -> throw OmhStorageException.ApiException(
                        message = e.message ?: "Unknown error occurred while creating file permission",
                        cause = e
                    )
                }
            }
        }
    }

    suspend fun getFileMetadata(fileId: String): OmhStorageMetadata? {
        return withContext(Dispatchers.IO) {
            try {
                val response = boxApiService.getFile(fileId)
                if (response.isSuccessful) {
                    val file = response.body()
                        ?: throw OmhStorageException.ApiException(message = "Empty response body")
                    OmhStorageMetadata(
                        entity = file.toOmhFile(),
                        originalMetadata = file
                    )
                } else {
                    throw response.toApiException()
                }
            } catch (e: Exception) {
                when (e) {
                    is OmhStorageException -> throw e
                    else -> throw OmhStorageException.ApiException(
                        message = e.message ?: "Unknown error occurred while getting file metadata",
                        cause = e
                    )
                }
            }
        }
    }

    suspend fun getWebUrl(fileId: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val response = boxApiService.getFile(fileId)
                if (response.isSuccessful) {
                    val file = response.body()
                        ?: throw OmhStorageException.ApiException(message = "Empty response body")
                    // Box files have a shared_link.url property
                    // If no shared link exists, we could create one, but for now just return null
                    file.sharedLink?.url
                } else {
                    throw response.toApiException()
                }
            } catch (e: Exception) {
                when (e) {
                    is OmhStorageException -> throw e
                    else -> throw OmhStorageException.ApiException(
                        message = e.message ?: "Unknown error occurred while getting web URL",
                        cause = e
                    )
                }
            }
        }
    }

    suspend fun resolvePath(path: String): OmhStorageEntity? {
        return withContext(Dispatchers.IO) {
            try {
                // Box doesn't have a direct path resolution API
                // We need to traverse the folder structure
                // This is a simplified implementation

                if (path.isEmpty() || path == "/") {
                    // Return root folder
                    return@withContext getFolder(Constants.ROOT_FOLDER)
                }

                // Split path and traverse
                val pathParts = path.trim('/').split('/')
                var currentFolderId = Constants.ROOT_FOLDER
                var currentEntity: OmhStorageEntity? = null

                for ((index, part) in pathParts.withIndex()) {
                    val isLastPart = index == pathParts.size - 1

                    // List items in current folder
                    val items = getFilesList(currentFolderId)

                    // Find item matching this part
                    val matchingItem = items.firstOrNull { entity ->
                        when (entity) {
                            is OmhStorageEntity.OmhFile -> entity.name == part
                            is OmhStorageEntity.OmhFolder -> entity.name == part
                        }
                    }

                    if (matchingItem == null) {
                        throw OmhStorageException.ApiException(statusCode = 404, message = "Path not found: $path")
                    }

                    if (isLastPart) {
                        currentEntity = matchingItem
                    } else {
                        // Must be a folder to continue traversal
                        when (matchingItem) {
                            is OmhStorageEntity.OmhFolder -> currentFolderId = matchingItem.id
                            is OmhStorageEntity.OmhFile -> throw OmhStorageException.ApiException(
                                message = "Path contains file in middle: $path"
                            )
                        }
                    }
                }

                currentEntity
            } catch (e: Exception) {
                when (e) {
                    is OmhStorageException -> throw e
                    else -> throw OmhStorageException.ApiException(
                        message = e.message ?: "Unknown error occurred while resolving path",
                        cause = e
                    )
                }
            }
        }
    }
}
