package com.openmobilehub.android.storage.plugin.box.restful.data.source.body

import com.fasterxml.jackson.annotation.JsonProperty

data class CreateFolderRequest(
    @JsonProperty("name")
    val name: String,
    @JsonProperty("parent")
    val parent: ParentReference
)

data class ParentReference(
    @JsonProperty("id")
    val id: String
)
