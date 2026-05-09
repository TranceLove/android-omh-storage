package com.openmobilehub.android.storage.plugin.box.restful.data.source.response

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class UploadSessionResponse(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("type")
    val type: String,
    @JsonProperty("session_expires_at")
    val sessionExpiresAt: String,
    @JsonProperty("part_size")
    val partSize: Long,
    @JsonProperty("total_parts")
    val totalParts: Int,
    @JsonProperty("num_parts_processed")
    val numPartsProcessed: Int
)
