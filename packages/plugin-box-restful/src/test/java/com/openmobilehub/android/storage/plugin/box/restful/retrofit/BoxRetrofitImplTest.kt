package com.openmobilehub.android.storage.plugin.box.restful.retrofit

import com.openmobilehub.android.auth.core.OmhAuthClient
import com.openmobilehub.android.storage.plugin.box.restful.data.BoxApiService
import com.openmobilehub.android.storage.plugin.box.restful.data.BoxCollaborationApiService
import com.openmobilehub.android.storage.plugin.box.restful.data.BoxUploadApiService
import com.openmobilehub.android.storage.plugin.box.restful.data.retrofit.BoxRetrofitImpl
import io.mockk.mockk
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class BoxRetrofitImplTest {

    private val mockAuthClient = mockk<OmhAuthClient>()
    private lateinit var boxRetrofitImpl: BoxRetrofitImpl

    @Before
    fun setUp() {
        boxRetrofitImpl = BoxRetrofitImpl(mockAuthClient)
    }

    @Test
    fun `boxApiService is not null after initialization`() {
        assertNotNull(boxRetrofitImpl.boxApiService)
    }

    @Test
    fun `boxUploadApiService is not null after initialization`() {
        assertNotNull(boxRetrofitImpl.boxUploadApiService)
    }

    @Test
    fun `boxCollaborationApiService is not null after initialization`() {
        assertNotNull(boxRetrofitImpl.boxCollaborationApiService)
    }

    @Test
    fun `boxApiService implements BoxApiService interface`() {
        assert(boxRetrofitImpl.boxApiService is BoxApiService)
    }

    @Test
    fun `boxUploadApiService implements BoxUploadApiService interface`() {
        assert(boxRetrofitImpl.boxUploadApiService is BoxUploadApiService)
    }

    @Test
    fun `boxCollaborationApiService implements BoxCollaborationApiService interface`() {
        assert(boxRetrofitImpl.boxCollaborationApiService is BoxCollaborationApiService)
    }

    @Test
    fun `companion objectMapper is not null`() {
        assertNotNull(BoxRetrofitImpl.objectMapper)
    }
}
