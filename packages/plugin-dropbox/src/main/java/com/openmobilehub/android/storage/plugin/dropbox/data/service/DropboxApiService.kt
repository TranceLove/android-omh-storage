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

package com.openmobilehub.android.storage.plugin.dropbox.data.service

import com.dropbox.core.v2.files.CreateFolderResult
import com.dropbox.core.v2.files.DeleteResult
import com.dropbox.core.v2.files.FileMetadata
import com.dropbox.core.v2.files.ListFolderResult
import com.dropbox.core.v2.files.ListRevisionsResult
import com.dropbox.core.v2.files.Metadata
import com.dropbox.core.v2.files.RelocationResult
import com.dropbox.core.v2.files.SearchV2Result
import com.dropbox.core.v2.files.WriteMode
import java.io.ByteArrayOutputStream
import java.io.InputStream

internal class DropboxApiService(private val apiClient: DropboxApiClient) {

    fun getFilesList(parentId: String): ListFolderResult {
        return apiClient.dropboxApiService.files().listFolder(parentId)
    }

    fun uploadFile(
        inputStream: InputStream,
        path: String,
        withAutorename: Boolean = true,
        writeMode: WriteMode = WriteMode.ADD
    ): FileMetadata {
        // withAutorename(true) is used to avoid conflicts with existing files
        // by renaming the uploaded file. It matches the Google Drive API behavior.

        return apiClient.dropboxApiService.files().uploadBuilder(path)
            .withAutorename(withAutorename)
            .withMode(writeMode)
            .uploadAndFinish(inputStream)
    }

    fun downloadFile(fileId: String, outputStream: ByteArrayOutputStream): FileMetadata {
        return apiClient.dropboxApiService.files().download(fileId).download(outputStream)
    }

    fun getFileRevisions(fileId: String): ListRevisionsResult {
        return apiClient.dropboxApiService.files().listRevisions(fileId)
    }

    fun downloadFileRevision(
        revisionId: String,
        outputStream: ByteArrayOutputStream
    ): FileMetadata {
        val path = "rev:$revisionId"

        return apiClient.dropboxApiService.files().download(path)
            .download(outputStream)
    }

    fun deleteFile(fileId: String): DeleteResult {
        return apiClient.dropboxApiService.files().deleteV2(fileId)
    }

    fun search(query: String): SearchV2Result {
        return apiClient.dropboxApiService.files().searchV2(query)
    }

    fun getFile(fileId: String): Metadata {
        return apiClient.dropboxApiService.files().getMetadata(fileId)
    }

    fun createFolder(path: String): CreateFolderResult {
        return apiClient.dropboxApiService.files().createFolderV2(path)
    }

    fun moveFile(fromPath: String, toPath: String): RelocationResult {
        return apiClient.dropboxApiService.files().moveV2Builder(fromPath, toPath).withAutorename(true).start()
    }
}
