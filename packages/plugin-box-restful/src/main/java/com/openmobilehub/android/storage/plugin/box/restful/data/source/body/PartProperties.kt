package com.openmobilehub.android.storage.plugin.box.restful.data.source.body

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonProperty

@Keep
internal data class PartProperties(
    @JsonProperty("offset")
    val offset: Long,
    @JsonProperty("part_id")
    val partId: String,
    @JsonProperty("sha1")
    val sha1: String,
    @JsonProperty("size")
    val size: Long,
)
