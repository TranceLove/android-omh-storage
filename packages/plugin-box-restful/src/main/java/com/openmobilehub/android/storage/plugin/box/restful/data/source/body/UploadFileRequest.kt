package com.openmobilehub.android.storage.plugin.box.restful.data.source.body

import com.fasterxml.jackson.annotation.JsonProperty

data class UploadFileRequest(
    @JsonProperty("file_name")
    val fileName: String,
    @JsonProperty("file_size")
    val fileSize: Long,
    @JsonProperty("parent")
    val parent: ParentReference
)
