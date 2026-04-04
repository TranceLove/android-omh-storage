package com.openmobilehub.android.storage.plugin.box.restful.data.source.body

import com.fasterxml.jackson.annotation.JsonProperty

data class UpdateFolderRequest(
    @JsonProperty("name")
    val name: String? = null,
    @JsonProperty("description")
    val description: String? = null,
    @JsonProperty("parent")
    val parent: ParentReference? = null
)
