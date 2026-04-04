package com.openmobilehub.android.storage.plugin.box.restful

internal object Constants {
    const val ROOT_FOLDER = "0"
    const val CHUNKED_UPLOAD_THRESHOLD = 20 * 1024 * 1024 // 20MB (Box requirement)
    const val CHUNK_SIZE = 8 * 1024 * 1024 // 8MB (recommended chunk size)
}
