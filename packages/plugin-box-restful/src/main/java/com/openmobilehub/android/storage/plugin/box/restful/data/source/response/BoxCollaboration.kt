package com.openmobilehub.android.storage.plugin.box.restful.data.source.response

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class BoxCollaboration(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("type")
    val type: String,
    @JsonProperty("accessible_by")
    val accessibleBy: BoxCollaborator?,
    @JsonProperty("acknowledged_at")
    val acknowledgedAt: String?,
    @JsonProperty("created_at")
    val createdAt: String?,
    @JsonProperty("created_by")
    val createdBy: BoxUser?,
    @JsonProperty("expires_at")
    val expiresAt: String?,
    @JsonProperty("invite_email")
    val inviteEmail: String?,
    @JsonProperty("is_access_only")
    val isAccessOnly: Boolean?,
    @JsonProperty("item")
    val item: BoxCollaborationItem?,
    @JsonProperty("modified_at")
    val modifiedAt: String?,
    @JsonProperty("role")
    val role: String,
    @JsonProperty("status")
    val status: String
)

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class BoxCollaborator(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("type")
    val type: String, // "user" or "group"
    @JsonProperty("name")
    val name: String?,
    @JsonProperty("login")
    val login: String?
)

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class BoxCollaborationItem(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("type")
    val type: String, // "file" or "folder"
    @JsonProperty("name")
    val name: String?,
    @JsonProperty("etag")
    val etag: String?
)
