package com.openmobilehub.android.storage.plugin.box.restful.data.source.response

import com.fasterxml.jackson.annotation.JsonProperty

data class BoxFolder(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("type")
    val type: String,
    @JsonProperty("name")
    val name: String,
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
    @JsonProperty("item_collection")
    val itemCollection: BoxItemCollection? = null
)

data class BoxItemCollection(
    @JsonProperty("total_count")
    val totalCount: Int,
    @JsonProperty("entries")
    val entries: List<BoxItem>? = null,
    @JsonProperty("offset")
    val offset: Int,
    @JsonProperty("limit")
    val limit: Int
)

data class BoxItem(
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
    @JsonProperty("etag")
    val etag: String? = null,
    @JsonProperty("sequence_id")
    val sequenceId: String? = null
)
