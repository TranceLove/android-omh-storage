package com.openmobilehub.android.storage.plugin.box.restful.data.source.response

import com.fasterxml.jackson.annotation.JsonProperty

data class UploadFileResponse(
    @JsonProperty("total_count")
    val totalCount: Int,
    @JsonProperty("entries")
    val entries: List<BoxFile>
)
