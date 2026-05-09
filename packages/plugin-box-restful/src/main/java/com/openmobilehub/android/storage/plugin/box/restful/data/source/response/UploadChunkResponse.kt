package com.openmobilehub.android.storage.plugin.box.restful.data.source.response

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class UploadChunkResponse(
    @JsonProperty("part")
    val part: UploadedPart
)

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
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
