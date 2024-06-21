/*
 * Copyright 2023 Open Mobile Hub
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.openmobilehub.android.storage.plugin.googledrive.gms

import com.openmobilehub.android.auth.core.OmhAuthClient
import com.openmobilehub.android.auth.core.models.OmhAuthStatusCodes
import com.openmobilehub.android.auth.plugin.google.gms.GmsCredentials
import com.openmobilehub.android.storage.core.OmhStorageClient
import com.openmobilehub.android.storage.core.model.OmhFilePermission
import com.openmobilehub.android.storage.core.model.OmhFileVersion
import com.openmobilehub.android.storage.core.model.OmhStorageEntity
import com.openmobilehub.android.storage.core.model.OmhStorageException
import com.openmobilehub.android.storage.plugin.googledrive.gms.data.repository.GmsFileRepository
import com.openmobilehub.android.storage.plugin.googledrive.gms.data.service.GoogleDriveApiProvider
import com.openmobilehub.android.storage.plugin.googledrive.gms.data.service.GoogleDriveApiService
import java.io.ByteArrayOutputStream
import java.io.File

internal class GoogleDriveGmsOmhStorageClient private constructor(
    authClient: OmhAuthClient,
    private val fileRepository: GmsFileRepository,
) : OmhStorageClient(authClient) {

    internal class Builder : OmhStorageClient.Builder {

        override fun build(authClient: OmhAuthClient): OmhStorageClient {
            val credentials =
                (authClient.getCredentials() as? GmsCredentials)?.googleAccountCredential
                    ?: throw OmhStorageException.InvalidCredentialsException(OmhAuthStatusCodes.SIGN_IN_FAILED)

            val apiProvider = GoogleDriveApiProvider.getInstance(credentials)
            val apiService = GoogleDriveApiService(apiProvider)
            val repository = GmsFileRepository(apiService)

            return GoogleDriveGmsOmhStorageClient(authClient, repository)
        }
    }

    override val rootFolder: String
        get() = GoogleDriveGmsConstants.ROOT_FOLDER

    override suspend fun listFiles(parentId: String): List<OmhStorageEntity> {
        return fileRepository.getFilesList(parentId)
    }

    override suspend fun search(query: String): List<OmhStorageEntity> {
        return fileRepository.search(query)
    }

    override suspend fun createFile(
        name: String,
        mimeType: String,
        parentId: String
    ): OmhStorageEntity? {
        return fileRepository.createFile(name, mimeType, parentId)
    }

    override suspend fun deleteFile(id: String): Boolean {
        return fileRepository.deleteFile(id)
    }

    override suspend fun uploadFile(localFileToUpload: File, parentId: String?): OmhStorageEntity? {
        return fileRepository.uploadFile(localFileToUpload, parentId)
    }

    override suspend fun downloadFile(fileId: String, mimeType: String?): ByteArrayOutputStream {
        return fileRepository.downloadFile(fileId, mimeType)
    }

    override suspend fun updateFile(localFileToUpload: File, fileId: String): OmhStorageEntity.OmhFile? {
        return fileRepository.updateFile(localFileToUpload, fileId)
    }

    override suspend fun getFileVersions(fileId: String): List<OmhFileVersion> {
        return fileRepository.getFileVersions(fileId)
    }

    override suspend fun downloadFileVersion(fileId: String, versionId: String): ByteArrayOutputStream {
        return fileRepository.downloadFileVersion(fileId, versionId)
    }

    override suspend fun getFilePermissions(fileId: String): List<OmhFilePermission> {
        // To be implemented
        return emptyList()
    }
}
