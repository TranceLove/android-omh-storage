package com.openmobilehub.android.storage.plugin.box.restful.data

import com.openmobilehub.android.storage.plugin.box.restful.data.source.body.CreateFolderRequest
import com.openmobilehub.android.storage.plugin.box.restful.data.source.body.UpdateFileRequest
import com.openmobilehub.android.storage.plugin.box.restful.data.source.body.UpdateFolderRequest
import com.openmobilehub.android.storage.plugin.box.restful.data.source.response.BoxFile
import com.openmobilehub.android.storage.plugin.box.restful.data.source.response.BoxFileVersion
import com.openmobilehub.android.storage.plugin.box.restful.data.source.response.BoxFolder
import com.openmobilehub.android.storage.plugin.box.restful.data.source.response.BoxUser
import com.openmobilehub.android.storage.plugin.box.restful.data.source.response.FileVersionsResponse
import com.openmobilehub.android.storage.plugin.box.restful.data.source.response.FolderItemsResponse
import com.openmobilehub.android.storage.plugin.box.restful.data.source.response.SearchResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

@Suppress("TooManyFunctions")
interface BoxApiService {

    @GET("users/me")
    suspend fun getCurrentUser(): Response<BoxUser>

    @GET("folders/{folder_id}")
    suspend fun getFolder(
        @Path("folder_id") folderId: String
    ): Response<BoxFolder>

    @GET("folders/{folder_id}/items")
    suspend fun getFolderItems(
        @Path("folder_id") folderId: String,
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null,
        @Query("fields") fields: String = "id,type,name,created_at,modified_at,parent,size",
    ): Response<FolderItemsResponse>

    @POST("folders")
    suspend fun createFolder(
        @Body body: CreateFolderRequest
    ): Response<BoxFolder>

    @PUT("folders/{folder_id}")
    suspend fun updateFolder(
        @Path("folder_id") folderId: String,
        @Body body: UpdateFolderRequest
    ): Response<BoxFolder>

    @DELETE("folders/{folder_id}")
    suspend fun deleteFolder(
        @Path("folder_id") folderId: String,
        @Query("recursive") recursive: Boolean = false
    ): Response<Unit>

    @GET("files/{file_id}")
    suspend fun getFile(
        @Path("file_id") fileId: String,
        @Query("fields") fields: String? = null
    ): Response<BoxFile>

    @PUT("files/{file_id}")
    suspend fun updateFile(
        @Path("file_id") fileId: String,
        @Body body: UpdateFileRequest
    ): Response<BoxFile>

    @DELETE("files/{file_id}")
    suspend fun deleteFile(
        @Path("file_id") fileId: String
    ): Response<Unit>

    @GET("files/{file_id}/content")
    suspend fun downloadFile(
        @Path("file_id") fileId: String
    ): Response<ResponseBody>

    @GET("search")
    suspend fun search(
        @Query("query") query: String,
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null,
        @Query("type") type: String? = null
    ): Response<SearchResponse>

    @GET("files/{file_id}/thumbnail.{extension}")
    @Suppress("LongParameterList")
    suspend fun getFileThumbnail(
        @Path("file_id") fileId: String,
        @Path("extension") extension: String,
        @Query("min_width") minWidth: Int? = null,
        @Query("max_width") maxWidth: Int? = null,
        @Query("min_height") minHeight: Int? = null,
        @Query("max_height") maxHeight: Int? = null
    ): Response<ResponseBody>

    // File versions endpoints
    @GET("files/{file_id}/versions")
    suspend fun getFileVersions(
        @Path("file_id") fileId: String,
        @Query("fields") fields: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null
    ): Response<FileVersionsResponse>

    @GET("files/{file_id}/versions/{file_version_id}")
    suspend fun getFileVersion(
        @Path("file_id") fileId: String,
        @Path("file_version_id") fileVersionId: String,
        @Query("fields") fields: String? = null
    ): Response<BoxFileVersion>

    @GET("files/{file_id}/content")
    suspend fun downloadFileVersion(
        @Path("file_id") fileId: String,
        @Query("version") versionId: String
    ): Response<ResponseBody>

    // Collaboration endpoints for permissions
    @GET("files/{file_id}")
    suspend fun getFileCollaborations(
        @Path("file_id") fileId: String,
        @Query("fields") fields: String? = null
    ): Response<BoxFile>

    @GET("folders/{folder_id}")
    suspend fun getFolderCollaborations(
        @Path("folder_id") folderId: String,
        @Query("fields") fields: String? = null
    ): Response<BoxFolder>
}
