package com.openmobilehub.android.storage.plugin.box.restful.data.source.response

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class ListFolderResponse(
    @JsonProperty("total_count")
    val totalCount: Int,
    @JsonProperty("entries")
    val entries: List<NodeMetadata>,
    @JsonProperty("offset")
    val offset: Int,
    @JsonProperty("limit")
    val limit: Int,
    @JsonProperty("next_marker")
    val nextMarker: String?
)
