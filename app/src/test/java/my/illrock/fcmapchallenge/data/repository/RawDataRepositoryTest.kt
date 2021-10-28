package my.illrock.fcmapchallenge.data.repository

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import my.illrock.fcmapchallenge.data.entity.RawData
import my.illrock.fcmapchallenge.data.network.ApiService
import my.illrock.fcmapchallenge.data.network.exception.InternalServerException
import my.illrock.fcmapchallenge.data.network.response.ResultWrapper
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class RawDataRepositoryTest {
    private val testDispatcher = TestCoroutineDispatcher()
    private val apiService = mockk<ApiService> {
        coEvery { getRawData(any(), any(), any(), any()) } returns listOf(
            RawData("2021-10-26", null, 0.1, 2.3)
        )
    }
    private val apiKeyRepository = mockk<ApiKeyRepository> {
        every { get() } returns "ApiKey"
    }

    private lateinit var repository: RawDataRepository

    @Before
    fun setUp() {
        repository = RawDataRepository(apiService, apiKeyRepository, testDispatcher)
    }

    @Test
    fun get_apiSuccess() {
        coEvery { apiService.getRawData(any(), any(), any(), any()) } returns listOf(
            RawData(MOCK_BEGIN, null, 1.1, 2.2),
            RawData(MOCK_END, null, 3.3, 4.4),
        )

        testDispatcher.runBlockingTest {
            val result = repository.get(MOCK_OBJECT_ID, MOCK_BEGIN, MOCK_END)
            assertTrue(result is ResultWrapper.Success)
            val data = (result as ResultWrapper.Success).data
            assertEquals(MOCK_BEGIN, data[0].timestamp)
            assertEquals(1.1, data[0].longitude)
            assertEquals(2.2, data[0].latitude)
            assertEquals(MOCK_END, data[1].timestamp)
            assertEquals(3.3, data[1].longitude)
            assertEquals(4.4, data[1].latitude)
        }
        testDispatcher.cleanupTestCoroutines()

        coVerify(exactly = 1) { apiService.getRawData(MOCK_OBJECT_ID, MOCK_BEGIN, MOCK_END, any()) }
    }

    @Test
    fun get_apiError() {
        val expectedException = InternalServerException("Unknown API user")
        coEvery { apiService.getRawData(any(), any(), any(), any()) } throws expectedException

        testDispatcher.runBlockingTest {
            val result = repository.get(MOCK_OBJECT_ID, MOCK_BEGIN, MOCK_END)

            assertTrue(result is ResultWrapper.Error)
            val actualException = (result as ResultWrapper.Error).exception
            assertTrue(actualException is InternalServerException)
            val actualInternalException = actualException as InternalServerException
            assertTrue(actualInternalException.isUnknownApiKeyException())
        }
        testDispatcher.cleanupTestCoroutines()

        coVerify(exactly = 1) { apiService.getRawData(MOCK_OBJECT_ID, MOCK_BEGIN, MOCK_END, any()) }
    }

    private companion object {
        const val MOCK_OBJECT_ID = 1L
        const val MOCK_BEGIN = "1"
        const val MOCK_END = "2"
    }
}