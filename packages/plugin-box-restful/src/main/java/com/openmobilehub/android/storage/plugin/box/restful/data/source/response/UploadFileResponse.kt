package com.openmobilehub.android.storage.plugin.box.restful.data.source.response

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class UploadFileResponse(
    @JsonProperty("total_count")
    val totalCount: Int,
    @JsonProperty("entries")
    val entries: List<BoxFile>
)
