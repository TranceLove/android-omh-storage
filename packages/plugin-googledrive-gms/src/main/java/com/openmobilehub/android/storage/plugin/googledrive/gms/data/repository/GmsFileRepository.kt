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

package com.openmobilehub.android.storage.plugin.googledrive.gms.data.repository

import android.webkit.MimeTypeMap
import com.google.api.client.http.FileContent
import com.google.api.client.http.HttpResponseException
import com.openmobilehub.android.storage.core.model.OmhFile
import com.openmobilehub.android.storage.core.model.OmhFileVersion
import com.openmobilehub.android.storage.core.model.OmhStorageException
import com.openmobilehub.android.storage.core.model.OmhStorageStatusCodes
import com.openmobilehub.android.storage.plugin.googledrive.gms.data.mapper.toOmhFile
import com.openmobilehub.android.storage.plugin.googledrive.gms.data.mapper.toOmhFileVersions
import com.openmobilehub.android.storage.plugin.googledrive.gms.data.mapper.toOmhFiles
import com.openmobilehub.android.storage.plugin.googledrive.gms.data.service.GoogleDriveApiService
import java.io.ByteArrayOutputStream
import java.io.File
import com.google.api.services.drive.model.File as GoogleDriveFile

internal class GmsFileRepository(
    private val apiService: GoogleDriveApiService
) {
    companion object {
        private const val ANY_MIME_TYPE = "*/*"
    }

    fun getFilesList(parentId: String): List<OmhFile> {
        return apiService.getFilesList(parentId).execute().toOmhFiles()
    }

    fun search(query: String): List<OmhFile> {
        return apiService.search(query).execute().toOmhFiles()
    }

    fun createFile(name: String, mimeType: String, parentId: String?): OmhFile? {
        val fileToBeCreated = GoogleDriveFile().apply {
            this.name = name
            this.mimeType = mimeType
            if (parentId != null) {
                this.parents = listOf(parentId)
            }
        }

        val responseFile: GoogleDriveFile = apiService.createFile(fileToBeCreated).execute()

        return responseFile.toOmhFile()
    }

    @SuppressWarnings("TooGenericExceptionCaught", "SwallowedException")
    fun deleteFile(fileId: String): Boolean {
        return try {
            apiService.deleteFile(fileId).execute()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun uploadFile(localFileToUpload: File, parentId: String?): OmhFile? {
        val localMimeType = getStringMimeTypeFromLocalFile(localFileToUpload)

        val file = GoogleDriveFile().apply {
            name = localFileToUpload.name
            mimeType = localMimeType
            parents = if (parentId.isNullOrBlank()) {
                emptyList()
            } else {
                listOf(parentId)
            }
        }

        val mediaContent = FileContent(localMimeType, localFileToUpload)

        val response: GoogleDriveFile = apiService.uploadFile(file, mediaContent).execute()

        return response.toOmhFile()
    }

    private fun getStringMimeTypeFromLocalFile(file: File) = MimeTypeMap
        .getSingleton()
        .getMimeTypeFromExtension(file.extension)
        ?: ANY_MIME_TYPE

    @SuppressWarnings("SwallowedException")
    fun downloadFile(fileId: String, mimeType: String?): ByteArrayOutputStream {
        val outputStream = ByteArrayOutputStream()

        try {
            apiService.downloadFile(fileId).executeMediaAndDownloadTo(outputStream)
        } catch (exception: HttpResponseException) {
            with(outputStream) {
                flush()
                reset()
            }

            if (mimeType.isNullOrBlank()) {
                throw OmhStorageException.DownloadException(
                    OmhStorageStatusCodes.DOWNLOAD_ERROR,
                    exception
                )
            }

            apiService
                .downloadGoogleDoc(fileId, mimeType)
                .executeMediaAndDownloadTo(outputStream)
        }

        return outputStream
    }

    fun updateFile(localFileToUpload: File, fileId: String): OmhFile? {
        val localMimeType = getStringMimeTypeFromLocalFile(localFileToUpload)

        val file = GoogleDriveFile().apply {
            name = localFileToUpload.name
            mimeType = localMimeType
        }

        val mediaContent = FileContent(localMimeType, localFileToUpload)

        val response: GoogleDriveFile =
            apiService.updateFile(fileId, file, mediaContent).execute()

        return response.toOmhFile()
    }

    fun getFileVersions(fileId: String): List<OmhFileVersion> {
        return apiService.getFileRevisions(fileId).execute().toOmhFileVersions(fileId).reversed()
    }

    fun downloadFileVersion(fileId: String, versionId: String): ByteArrayOutputStream {
        val outputStream = ByteArrayOutputStream()
        apiService.downloadFileRevision(fileId, versionId).executeMediaAndDownloadTo(outputStream)

        return outputStream
    }
}
