package my.illrock.fcmapchallenge.presentation.trip

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.Assert.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import my.illrock.fcmapchallenge.data.database.lastdata.LastDataEntity
import my.illrock.fcmapchallenge.data.entity.RawData
import my.illrock.fcmapchallenge.data.network.exception.InternalServerException
import my.illrock.fcmapchallenge.data.network.response.ResultWrapper
import my.illrock.fcmapchallenge.data.repository.LastDataRepository
import my.illrock.fcmapchallenge.data.repository.RawDataRepository
import my.illrock.fcmapchallenge.util.getOrAwaitValue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*


@ExperimentalCoroutinesApi
class TripViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val rawDataRepository = mockk<RawDataRepository> {
        coEvery { get(any(), any(), any()) } returns ResultWrapper.Success(listOf(
            mockRawData()
        ))
    }
    private val lastDataRepository = mockk<LastDataRepository> {
        coEvery { getById(any()) } returns mockLastDataEntity()
    }
    private val testDispatcher = TestCoroutineDispatcher()

    private lateinit var vm: TripViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        vm = TripViewModel(rawDataRepository, lastDataRepository, testDispatcher)
    }

    @After
    fun cleanUp() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun init_success() {
        testDispatcher.runBlockingTest {
            val expectedDate = Date(MOCK_BEGIN_TIMESTAMP)
            vm.init(MOCK_OBJECT_ID, expectedDate)

            assertEquals(MOCK_PLATE, vm.plate.getOrAwaitValue())
            assertEquals(expectedDate, vm.chosenDate.getOrAwaitValue())
            assertFalse(vm.isLoading.getOrAwaitValue())

            val expectedRawData = listOf(mockRawData())
            assertEquals(expectedRawData, vm.tripData.getOrAwaitValue())

            assertFalse(vm.isEmptyData.getOrAwaitValue())
        }

        coVerify(exactly = 1) { lastDataRepository.getById(MOCK_OBJECT_ID) }
        coVerify(exactly = 1) { rawDataRepository.get(MOCK_OBJECT_ID, MOCK_BEGIN, MOCK_END) }
    }

    @Test
    fun init_error() {
        val expectedErrorMessage = "Random exception"
        val expectedError = InternalServerException(expectedErrorMessage)
        coEvery { rawDataRepository.get(any(), any(), any()) } returns ResultWrapper.Error(expectedError)

        testDispatcher.runBlockingTest {
            val expectedDate = Date(MOCK_BEGIN_TIMESTAMP)
            vm.init(MOCK_OBJECT_ID, expectedDate)

            assertEquals(MOCK_PLATE, vm.plate.getOrAwaitValue())
            assertEquals(expectedDate, vm.chosenDate.getOrAwaitValue())
            assertFalse(vm.isLoading.getOrAwaitValue())

            assertEquals(listOf<RawData>(), vm.tripData.getOrAwaitValue())

            assertFalse(vm.isEmptyData.getOrAwaitValue())
            assertEquals(expectedErrorMessage, vm.errorString.getOrAwaitValue().peekContent())
        }

        coVerify(exactly = 1) { lastDataRepository.getById(MOCK_OBJECT_ID) }
        coVerify(exactly = 1) { rawDataRepository.get(MOCK_OBJECT_ID, MOCK_BEGIN, MOCK_END) }
    }

    private fun mockLastDataEntity() = LastDataEntity(
        MOCK_OBJECT_ID,
        "",
        1.1,
        2.2,
        "Address",
        "Driver Name",
        "PL4T8",
        null,
        null
    )

    private fun mockRawData() = RawData(
        "",
        null,
        3.3,
        4.4
    )

    private companion object {
        const val MOCK_OBJECT_ID = 1L
        const val MOCK_BEGIN_TIMESTAMP = 1635379200000L
        const val MOCK_BEGIN = "2021-10-28"
        const val MOCK_END = "2021-10-29"
        const val MOCK_PLATE = "PL4T8"
    }
}