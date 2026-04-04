package com.openmobilehub.android.storage.plugin.box.restful.data

import com.openmobilehub.android.storage.plugin.box.restful.data.source.body.CommitFileUploadRequest
import com.openmobilehub.android.storage.plugin.box.restful.data.source.body.UploadFileRequest
import com.openmobilehub.android.storage.plugin.box.restful.data.source.response.UploadChunkResponse
import com.openmobilehub.android.storage.plugin.box.restful.data.source.response.UploadFileResponse
import com.openmobilehub.android.storage.plugin.box.restful.data.source.response.UploadSessionResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface BoxUploadApiService {

    @Multipart
    @POST("files/content")
    suspend fun uploadFile(
        @Part("attributes") attributes: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<UploadFileResponse>

    // Chunked upload endpoints
    @POST("files/upload_sessions")
    suspend fun createUploadSession(
        @Body uploadFileRequest: UploadFileRequest
    ): Response<UploadSessionResponse>

    @PUT("files/upload_sessions/{upload_session_id}")
    suspend fun uploadFilePart(
        @Path("upload_session_id") uploadSessionId: String,
        @Header("Content-Range") contentRange: String,
        @Header("digest") digest: String,
        @Body filePart: RequestBody
    ): Response<UploadChunkResponse>

    @POST("files/upload_sessions/{upload_session_id}/commit")
    suspend fun commitUploadSession(
        @Path("upload_session_id") uploadSessionId: String,
        @Header("digest") digest: String,
        @Body commitRequest: CommitFileUploadRequest
    ): Response<UploadFileResponse>

    @DELETE("files/upload_sessions/{upload_session_id}")
    suspend fun cancelUploadSession(
        @Path("upload_session_id") uploadSessionId: String
    ): Response<Unit>
}
