package com.openmobilehub.android.storage.plugin.box.restful

import com.openmobilehub.android.auth.core.OmhAuthClient
import com.openmobilehub.android.storage.plugin.box.restful.data.repository.BoxRestfulFileRepository
import io.mockk.mockk
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class BoxRestfulOmhStorageClientTest {

    private val mockAuthClient = mockk<OmhAuthClient>()
    private val mockRepository = mockk<BoxRestfulFileRepository>()

    @Test
    fun `should create client with correct root folder`() {
        val client = BoxRestfulOmhStorageClient(mockAuthClient, mockRepository)

        assertNotNull(client)
        assertEquals("0", client.rootFolder)
    }
}
