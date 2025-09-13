package com.openmobilehub.android.storage.plugin.dropbox.restful.data.source.body

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonProperty

@Keep
internal data class GetThumbnailRequest(
    @JsonProperty("path")
    val path: String,
    @JsonProperty("format")
    val format: String = "jpeg",
    @JsonProperty("size")
    val size: String,
    @JsonProperty("mode")
    val mode: String = "strict"
)
