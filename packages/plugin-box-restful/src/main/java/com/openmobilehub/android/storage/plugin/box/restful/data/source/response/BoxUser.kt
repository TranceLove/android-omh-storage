package com.openmobilehub.android.storage.plugin.box.restful.data.source.response

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class BoxUser(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("type")
    val type: String,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("login")
    val login: String,
    @JsonProperty("space_amount")
    val spaceAmount: Long? = null,
    @JsonProperty("space_used")
    val spaceUsed: Long? = null
)
