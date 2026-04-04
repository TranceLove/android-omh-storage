package com.openmobilehub.android.storage.plugin.box.restful.data.source.response

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class CurrentUserResponse(
    @JsonProperty("space_amount")
    val quotaAllocated: Long,
    @JsonProperty("space_used")
    val quotaUsed: Long,
)
