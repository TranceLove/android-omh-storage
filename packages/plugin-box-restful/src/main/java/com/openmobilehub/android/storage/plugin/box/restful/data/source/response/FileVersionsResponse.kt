package com.openmobilehub.android.storage.plugin.box.restful.data.source.response

import com.fasterxml.jackson.annotation.JsonProperty

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

data class BoxOrderBy(
    @JsonProperty("by")
    val by: String,
    @JsonProperty("direction")
    val direction: String
)
