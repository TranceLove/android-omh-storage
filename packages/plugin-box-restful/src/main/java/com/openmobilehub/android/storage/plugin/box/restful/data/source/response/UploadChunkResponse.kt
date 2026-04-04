package com.openmobilehub.android.storage.plugin.box.restful.data.source.response

import com.fasterxml.jackson.annotation.JsonProperty

data class UploadChunkResponse(
    @JsonProperty("part")
    val part: UploadedPart
)

data class UploadedPart(
    @JsonProperty("part_id")
    val partId: String,
    @JsonProperty("offset")
    val offset: Long,
    @JsonProperty("size")
    val size: Long,
    @JsonProperty("sha1")
    val sha1: String
)
