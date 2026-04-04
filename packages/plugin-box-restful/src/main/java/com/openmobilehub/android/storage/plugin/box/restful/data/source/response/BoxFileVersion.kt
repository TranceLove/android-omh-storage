package com.openmobilehub.android.storage.plugin.box.restful.data.source.response

import com.fasterxml.jackson.annotation.JsonProperty

data class BoxFileVersion(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("type")
    val type: String,
    @JsonProperty("created_at")
    val createdAt: String?,
    @JsonProperty("modified_at")
    val modifiedAt: String?,
    @JsonProperty("modified_by")
    val modifiedBy: BoxUser?,
    @JsonProperty("name")
    val name: String?,
    @JsonProperty("purged_at")
    val purgedAt: String?,
    @JsonProperty("restored_at")
    val restoredAt: String?,
    @JsonProperty("restored_by")
    val restoredBy: BoxUser?,
    @JsonProperty("sha1")
    val sha1: String?,
    @JsonProperty("size")
    val size: Long?,
    @JsonProperty("trashed_at")
    val trashedAt: String?,
    @JsonProperty("trashed_by")
    val trashedBy: BoxUser?,
    @JsonProperty("uploader_display_name")
    val uploaderDisplayName: String?,
    @JsonProperty("version_number")
    val versionNumber: String?
)
