package com.openmobilehub.android.storage.plugin.box.restful.data.source.response

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class FileVersionsResponse(
    @JsonProperty("entries")
    val entries: List<BoxFileVersion>,
    @JsonProperty("limit")
    val limit: Int?,
    @JsonProperty("offset")
    val offset: Int?,
    @JsonProperty("total_count")
    val totalCount: Int?,
    @JsonProperty("order")
    val order: List<BoxOrderBy>?
)

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class BoxOrderBy(
    @JsonProperty("by")
    val by: String,
    @JsonProperty("direction")
    val direction: String
)
