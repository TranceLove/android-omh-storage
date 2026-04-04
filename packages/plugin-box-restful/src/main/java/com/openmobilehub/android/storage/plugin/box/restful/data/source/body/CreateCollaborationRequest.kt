package com.openmobilehub.android.storage.plugin.box.restful.data.source.body

import com.fasterxml.jackson.annotation.JsonProperty

data class CreateCollaborationRequest(
    @JsonProperty("item")
    val item: CollaborationItem,
    @JsonProperty("accessible_by")
    val accessibleBy: CollaborationAccessibleBy,
    @JsonProperty("role")
    val role: String,
    @JsonProperty("can_view_path")
    val canViewPath: Boolean? = null,
    @JsonProperty("expires_at")
    val expiresAt: String? = null,
    @JsonProperty("is_access_only")
    val isAccessOnly: Boolean? = null
)

data class CollaborationItem(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("type")
    val type: String // "file" or "folder"
)

data class CollaborationAccessibleBy(
    @JsonProperty("type")
    val type: String, // "user" or "group"
    @JsonProperty("id")
    val id: String? = null,
    @JsonProperty("login")
    val login: String? = null // Alternative to id for users
)
