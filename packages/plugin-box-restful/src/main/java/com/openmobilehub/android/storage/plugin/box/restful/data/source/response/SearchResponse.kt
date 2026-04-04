package com.openmobilehub.android.storage.plugin.box.restful.data.source.response

import com.fasterxml.jackson.annotation.JsonProperty

data class SearchResponse(
    @JsonProperty("total_count")
    val totalCount: Int,
    @JsonProperty("entries")
    val entries: List<BoxItem>,
    @JsonProperty("offset")
    val offset: Int,
    @JsonProperty("limit")
    val limit: Int
)
