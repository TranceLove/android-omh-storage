package com.openmobilehub.android.storage.plugin.dropbox.restful.data.source.body

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
internal data class MoveNodeRequest(
    @JsonProperty("from_path")
    val fromPath: String,
    @JsonProperty("to_path")
    val toPath: String,
    @JsonProperty("autorename")
    val autoRename: Boolean = false
)
