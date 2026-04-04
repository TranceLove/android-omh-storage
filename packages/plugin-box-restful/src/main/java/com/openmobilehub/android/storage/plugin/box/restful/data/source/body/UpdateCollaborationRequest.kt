package com.openmobilehub.android.storage.plugin.box.restful.data.source.body

import com.fasterxml.jackson.annotation.JsonProperty

data class UpdateCollaborationRequest(
    @JsonProperty("role")
    val role: String,
    @JsonProperty("can_view_path")
    val canViewPath: Boolean? = null,
    @JsonProperty("expires_at")
    val expiresAt: String? = null,
    @JsonProperty("status")
    val status: String? = null // "accepted", "rejected"
)
