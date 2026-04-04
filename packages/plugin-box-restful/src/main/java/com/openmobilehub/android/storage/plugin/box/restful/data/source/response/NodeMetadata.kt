package com.openmobilehub.android.storage.plugin.box.restful.data.source.response

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class NodeMetadata(
    @JsonProperty("type")
    val type: String,
    @JsonProperty("id")
    val id: String,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("created_at")
    val createdAt: String,
    @JsonProperty("modified_at")
    val updatedAt: String,
    @JsonProperty("parent")
    val parent: Parent,
    @JsonProperty("size")
    val size: Long = -1L,
) {
    fun isFolder(): Boolean = type == TYPE_FOLDER

    @Keep
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Parent(
        @JsonProperty("type")
        val type: String,
        @JsonProperty("id")
        val id: String,
        @JsonProperty("name")
        val name: String,
    )

    companion object {
        const val TYPE_FILE = "file"
        const val TYPE_FOLDER = "folder"
    }
}
