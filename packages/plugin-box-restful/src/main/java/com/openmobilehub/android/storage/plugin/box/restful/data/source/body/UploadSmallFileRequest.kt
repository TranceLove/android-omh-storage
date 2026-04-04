package com.openmobilehub.android.storage.plugin.box.restful.data.source.body

import com.fasterxml.jackson.annotation.JsonProperty

data class UploadSmallFileRequest(
    @JsonProperty("name")
    val name: String,
    @JsonProperty("parent")
    val parent: ParentReference
)
