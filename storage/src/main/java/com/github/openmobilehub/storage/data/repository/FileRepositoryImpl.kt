package com.github.openmobilehub.storage.data.repository

import com.github.openmobilehub.storage.data.datasource.network.FileNetworkDataSource
import com.github.openmobilehub.storage.domain.abstraction.FileRepository

class FileRepositoryImpl(
    private val networkDataSource: FileNetworkDataSource
) : FileRepository {
    override fun create() {
        networkDataSource.create()
    }

    override fun read() {
        networkDataSource.read()
    }

    override fun update() {
        networkDataSource.update()
    }

    override fun delete() {
        networkDataSource.delete()
    }

    override fun upload() {
        networkDataSource.upload()
    }

    override fun download() {
        networkDataSource.download()
    }
}
