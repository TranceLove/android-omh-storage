package com.openmobilehub.android.storage.plugin.box.restful.data.source.response

import com.fasterxml.jackson.annotation.JsonProperty

data class BoxFile(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("type")
    val type: String,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("size")
    val size: Long? = null,
    @JsonProperty("created_at")
    val createdAt: String? = null,
    @JsonProperty("modified_at")
    val modifiedAt: String? = null,
    @JsonProperty("description")
    val description: String? = null,
    @JsonProperty("parent")
    val parent: BoxParent? = null,
    @JsonProperty("path_collection")
    val pathCollection: BoxPathCollection? = null,
    @JsonProperty("created_by")
    val createdBy: BoxUser? = null,
    @JsonProperty("modified_by")
    val modifiedBy: BoxUser? = null,
    @JsonProperty("owned_by")
    val ownedBy: BoxUser? = null,
    @JsonProperty("shared_link")
    val sharedLink: BoxSharedLink? = null,
    @JsonProperty("etag")
    val etag: String? = null,
    @JsonProperty("sequence_id")
    val sequenceId: String? = null,
    @JsonProperty("sha1")
    val sha1: String? = null
)

data class BoxParent(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("type")
    val type: String,
    @JsonProperty("name")
    val name: String? = null
)

data class BoxPathCollection(
    @JsonProperty("total_count")
    val totalCount: Int,
    @JsonProperty("entries")
    val entries: List<BoxParent>
)

data class BoxSharedLink(
    @JsonProperty("url")
    val url: String,
    @JsonProperty("download_url")
    val downloadUrl: String? = null,
    @JsonProperty("vanity_url")
    val vanityUrl: String? = null,
    @JsonProperty("vanity_name")
    val vanityName: String? = null,
    @JsonProperty("access")
    val access: String,
    @JsonProperty("effective_access")
    val effectiveAccess: String,
    @JsonProperty("effective_permission")
    val effectivePermission: String,
    @JsonProperty("unshared_at")
    val unsharedAt: String? = null,
    @JsonProperty("is_password_enabled")
    val isPasswordEnabled: Boolean,
    @JsonProperty("permissions")
    val permissions: BoxPermissions? = null,
    @JsonProperty("download_count")
    val downloadCount: Int,
    @JsonProperty("preview_count")
    val previewCount: Int
)

data class BoxPermissions(
    @JsonProperty("can_download")
    val canDownload: Boolean,
    @JsonProperty("can_preview")
    val canPreview: Boolean
)
