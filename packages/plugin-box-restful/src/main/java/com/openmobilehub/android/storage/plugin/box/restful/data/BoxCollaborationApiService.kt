package com.openmobilehub.android.storage.plugin.box.restful.data

import com.openmobilehub.android.storage.plugin.box.restful.data.source.body.CreateCollaborationRequest
import com.openmobilehub.android.storage.plugin.box.restful.data.source.body.UpdateCollaborationRequest
import com.openmobilehub.android.storage.plugin.box.restful.data.source.response.BoxCollaboration
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface BoxCollaborationApiService {

    @GET("collaborations/{collaboration_id}")
    suspend fun getCollaboration(
        @Path("collaboration_id") collaborationId: String,
        @Query("fields") fields: String? = null
    ): Response<BoxCollaboration>

    @POST("collaborations")
    suspend fun createCollaboration(
        @Body createCollaborationRequest: CreateCollaborationRequest,
        @Query("notify") notify: Boolean? = null,
        @Query("fields") fields: String? = null
    ): Response<BoxCollaboration>

    @PUT("collaborations/{collaboration_id}")
    suspend fun updateCollaboration(
        @Path("collaboration_id") collaborationId: String,
        @Body updateCollaborationRequest: UpdateCollaborationRequest
    ): Response<BoxCollaboration>

    @DELETE("collaborations/{collaboration_id}")
    suspend fun deleteCollaboration(
        @Path("collaboration_id") collaborationId: String
    ): Response<Unit>
}
