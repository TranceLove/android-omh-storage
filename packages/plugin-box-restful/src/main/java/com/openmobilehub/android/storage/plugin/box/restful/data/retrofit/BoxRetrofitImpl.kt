package com.openmobilehub.android.storage.plugin.box.restful.data.retrofit

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.openmobilehub.android.auth.core.OmhAuthClient
import com.openmobilehub.android.storage.core.restful.common.data.repository.StorageAuthenticator
import com.openmobilehub.android.storage.core.restful.common.utils.accessToken
import com.openmobilehub.android.storage.plugin.box.restful.BuildConfig
import com.openmobilehub.android.storage.plugin.box.restful.data.BoxApiService
import com.openmobilehub.android.storage.plugin.box.restful.data.BoxCollaborationApiService
import com.openmobilehub.android.storage.plugin.box.restful.data.BoxUploadApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

internal class BoxRetrofitImpl(private val omhAuthClient: OmhAuthClient) {

    companion object {
        val objectMapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())
    }

    val boxApiService: BoxApiService = Retrofit.Builder()
        .client(createOkHttpClient())
        .addConverterFactory(createConverterFactory())
        .baseUrl(BuildConfig.BOX_API_URL)
        .build().create(BoxApiService::class.java)

    val boxUploadApiService: BoxUploadApiService = Retrofit.Builder()
        .client(createOkHttpClient())
        .addConverterFactory(createConverterFactory())
        .baseUrl(BuildConfig.BOX_UPLOAD_API_URL)
        .build().create(BoxUploadApiService::class.java)

    val boxCollaborationApiService: BoxCollaborationApiService = Retrofit.Builder()
        .client(createOkHttpClient())
        .addConverterFactory(createConverterFactory())
        .baseUrl(BuildConfig.BOX_API_URL)
        .build().create(BoxCollaborationApiService::class.java)

    private fun createOkHttpClient(): OkHttpClient {
        val authenticator = StorageAuthenticator(omhAuthClient)
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = setupRequestInterceptor(chain)
                chain.proceed(request)
            }
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    if (BuildConfig.DEBUG) setLevel(HttpLoggingInterceptor.Level.BODY)
                },
            )
            .authenticator(authenticator)
            .build()
    }

    private fun setupRequestInterceptor(chain: Interceptor.Chain) = chain
        .request()
        .newBuilder()
        .addHeader(
            StorageAuthenticator.HEADER_AUTHORIZATION_NAME,
            StorageAuthenticator.BEARER.format(omhAuthClient.accessToken.orEmpty()),
        )
        .build()

    private fun createConverterFactory() = JacksonConverterFactory.create(
        ObjectMapper()
            .registerModule(KotlinModule.Builder().build())
    )
}
