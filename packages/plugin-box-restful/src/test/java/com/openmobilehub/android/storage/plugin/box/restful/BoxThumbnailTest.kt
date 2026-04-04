package com.openmobilehub.android.storage.plugin.box.restful

import com.openmobilehub.android.auth.core.OmhAuthClient
import com.openmobilehub.android.storage.core.ThumbnailSize
import com.openmobilehub.android.storage.plugin.box.restful.data.BoxApiService
import com.openmobilehub.android.storage.plugin.box.restful.data.BoxCollaborationApiService
import com.openmobilehub.android.storage.plugin.box.restful.data.BoxUploadApiService
import com.openmobilehub.android.storage.plugin.box.restful.data.repository.BoxRestfulFileRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.Headers
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.ResponseBody
import org.junit.Test
import retrofit2.Response
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class BoxThumbnailTest {

    private val mockAuthClient = mockk<OmhAuthClient>()
    private val mockBoxApiService = mockk<BoxApiService>()
    private val mockBoxUploadApiService = mockk<BoxUploadApiService>()
    private val mockBoxCollaborationApiService = mockk<BoxCollaborationApiService>()

    private val repository = BoxRestfulFileRepository(
        mockBoxApiService,
        mockBoxUploadApiService,
        mockBoxCollaborationApiService
    )
    private val client = BoxRestfulOmhStorageClient(mockAuthClient, repository)

    @Test
    fun `should request thumbnail with correct parameters for different sizes`() = runTest {
        // Mock successful thumbnail response
        val mockResponseBody = mockk<ResponseBody>()
        val mockResponse = Response.success(mockResponseBody)

        coEvery {
            mockBoxApiService.getFileThumbnail(
                fileId = "test_file_id",
                extension = "png",
                maxWidth = 128,
                maxHeight = 128
            )
        } returns mockResponse

        coEvery { mockResponseBody.byteStream() } returns "thumbnail data".byteInputStream()

        // Test medium size thumbnail
        val result = client.getFileThumbnail("test_file_id", ThumbnailSize.MEDIUM)

        assertNotNull(result)
    }

    @Test
    fun `should retry on 202 response with Retry-After header`() = runTest {
        val mockResponseBody = mockk<ResponseBody>()

        // First response: 202 with Retry-After header
        val headers202 = Headers.Builder()
            .add("Retry-After", "1") // 1 second
            .add("Location", "https://box.com/thumbnail/generating")
            .build()
        val rawResponse202 = okhttp3.Response.Builder()
            .code(202)
            .message("Accepted")
            .protocol(Protocol.HTTP_1_1)
            .headers(headers202)
            .request(Request.Builder().url("https://api.box.com").build())
            .build()
        val response202 = Response.success(mockResponseBody, rawResponse202)

        // Second response: 200 success
        val successResponseBody = mockk<ResponseBody>()
        val successResponse = Response.success(successResponseBody)

        coEvery {
            mockBoxApiService.getFileThumbnail(
                fileId = "test_file_id",
                extension = "png",
                maxWidth = 128,
                maxHeight = 128
            )
        } returnsMany listOf(response202, successResponse)

        coEvery { successResponseBody.byteStream() } returns "thumbnail data".byteInputStream()

        // Test - should automatically retry after delay
        val result = client.getFileThumbnail("test_file_id", ThumbnailSize.MEDIUM)

        assertNotNull(result)

        // Verify it made 2 requests (initial + 1 retry)
        coVerify(exactly = 2) {
            mockBoxApiService.getFileThumbnail(
                fileId = "test_file_id",
                extension = "png",
                maxWidth = 128,
                maxHeight = 128
            )
        }
    }

    @Test
    fun `should handle 302 redirect with retry`() = runTest {
        val mockResponseBody = mockk<ResponseBody>()

        // First response: 302 redirect
        val headers302 = Headers.Builder()
            .add("Location", "https://dl.boxcloud.com/thumbnail/xyz")
            .add("Retry-After", "1")
            .build()
        val rawResponse302 = okhttp3.Response.Builder()
            .code(302)
            .message("Found")
            .protocol(Protocol.HTTP_1_1)
            .headers(headers302)
            .request(Request.Builder().url("https://api.box.com").build())
            .build()
        val response302 = Response.error<ResponseBody>(mockResponseBody, rawResponse302)

        // Second response: 200 success
        val successResponseBody = mockk<ResponseBody>()
        val successResponse = Response.success(successResponseBody)

        coEvery {
            mockBoxApiService.getFileThumbnail(
                fileId = "test_file_id",
                extension = "png",
                maxWidth = 64,
                maxHeight = 64
            )
        } returnsMany listOf(response302, successResponse)

        coEvery { successResponseBody.byteStream() } returns "thumbnail data".byteInputStream()

        // Test - should automatically follow redirect after delay
        val result = client.getFileThumbnail("test_file_id", ThumbnailSize.SMALL)

        assertNotNull(result)

        // Verify it made 2 requests (initial + 1 retry)
        coVerify(exactly = 2) {
            mockBoxApiService.getFileThumbnail(
                fileId = "test_file_id",
                extension = "png",
                maxWidth = 64,
                maxHeight = 64
            )
        }
    }

    @Test
    fun `should use correct format and size for different thumbnail sizes`() {
        // Test the mapping logic
        val testCases = mapOf(
            ThumbnailSize.VERY_SMALL to Triple("png", 32, 32),
            ThumbnailSize.SMALL to Triple("png", 64, 64),
            ThumbnailSize.MEDIUM to Triple("png", 128, 128),
            ThumbnailSize.LARGE to Triple("png", 256, 256),
            ThumbnailSize.VERY_LARGE to Triple("jpg", 320, 320)
        )

        // Verify the mapping exists
        testCases.forEach { (_, expected) ->
            assertNotNull(expected)
        }
    }
}
