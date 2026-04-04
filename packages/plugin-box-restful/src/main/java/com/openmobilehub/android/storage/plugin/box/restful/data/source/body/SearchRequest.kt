package com.openmobilehub.android.storage.plugin.box.restful.data.source.body

import com.fasterxml.jackson.annotation.JsonProperty

data class SearchRequest(
    @JsonProperty("query")
    val query: String,
    @JsonProperty("limit")
    val limit: Int? = null,
    @JsonProperty("offset")
    val offset: Int? = null,
    @JsonProperty("type")
    val type: String? = null
)
