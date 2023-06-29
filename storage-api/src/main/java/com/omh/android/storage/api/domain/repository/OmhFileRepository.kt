package com.omh.android.storage.api.domain.repository

import com.omh.android.storage.api.domain.model.OmhFile
import java.io.ByteArrayOutputStream
import java.io.File

interface OmhFileRepository {

    fun getFilesList(parentId: String = "root"): List<OmhFile>

    fun createFile(name: String, mimeType: String, parentId: String?): OmhFile?

    fun deleteFile(fileId: String): Boolean

    fun uploadFile(localFileToUpload: File, parentId: String?): OmhFile?

    fun downloadFile(fileId: String, mimeType: String?): ByteArrayOutputStream

    fun open()

    fun update()
}
