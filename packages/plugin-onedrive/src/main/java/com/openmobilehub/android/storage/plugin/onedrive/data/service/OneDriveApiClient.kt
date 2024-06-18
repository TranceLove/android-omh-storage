package com.openmobilehub.android.storage.plugin.onedrive.data.service

import com.microsoft.graph.core.models.UploadResult
import com.microsoft.graph.core.tasks.LargeFileUploadTask
import com.microsoft.graph.models.DriveItem
import com.microsoft.graph.models.UploadSession
import com.microsoft.graph.serviceclient.GraphServiceClient
import com.microsoft.kiota.serialization.ParseNode
import java.io.FileInputStream

class OneDriveApiClient(private val authProvider: OneDriveAuthProvider) {
    companion object {
        private var instance: OneDriveApiClient? = null

        internal fun getInstance(newAuthProvider: OneDriveAuthProvider): OneDriveApiClient {
            val oldAuthProvider = instance?.authProvider
            val isDifferentAccount = oldAuthProvider?.accessToken != newAuthProvider.accessToken

            if (instance == null || isDifferentAccount) {
                instance = OneDriveApiClient(newAuthProvider)
            }

            return instance!!
        }

        private const val CHUNK_SIZE_IN_BYTES = 1024 * 1024 // 1MB
        private const val MAX_ATTEMPTS = 5
    }

    internal val graphServiceClient = GraphServiceClient(authProvider)

    internal fun uploadFile(
        uploadSession: UploadSession,
        fileInputStream: FileInputStream,
        streamSize: Long,
    ): UploadResult<DriveItem> {
        val largeFileUploadTask = LargeFileUploadTask(
            graphServiceClient.requestAdapter,
            uploadSession,
            fileInputStream,
            streamSize,
            CHUNK_SIZE_IN_BYTES.toLong()
        ) { parseNode: ParseNode? ->
            DriveItem.createFromDiscriminatorValue(
                parseNode
            )
        }

        return largeFileUploadTask.upload(MAX_ATTEMPTS, null)
    }
}
