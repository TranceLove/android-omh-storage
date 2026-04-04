package com.openmobilehub.android.storage.plugin.box.restful.data.source.body

import com.fasterxml.jackson.annotation.JsonProperty

data class CommitFileUploadRequest(
    @JsonProperty("parts")
    val parts: List<UploadPart>
)

data class UploadPart(
    @JsonProperty("part_id")
    val partId: String,
    @JsonProperty("offset")
    val offset: Long,
    @JsonProperty("size")
    val size: Long,
    @JsonProperty("sha1")
    val sha1: String
)
