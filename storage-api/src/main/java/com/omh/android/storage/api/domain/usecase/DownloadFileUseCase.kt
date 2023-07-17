package com.omh.android.storage.api.domain.usecase

import com.omh.android.storage.api.domain.repository.OmhFileRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.io.ByteArrayOutputStream

internal class DownloadFileUseCase(
    private val repository: OmhFileRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.Default
) : OmhSuspendUseCase<DownloadFileUseCaseParams, DownloadFileUseCaseResult>(dispatcher) {

    override suspend fun execute(parameters: DownloadFileUseCaseParams) = DownloadFileUseCaseResult(
        repository.downloadFile(parameters.fileId, parameters.mimeType)
    )
}

data class DownloadFileUseCaseParams(val fileId: String, val mimeType: String?)

data class DownloadFileUseCaseResult(val outputStream: ByteArrayOutputStream)
