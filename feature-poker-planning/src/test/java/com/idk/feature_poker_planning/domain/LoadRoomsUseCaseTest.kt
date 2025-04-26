package com.idk.feature_poker_planning.domain

import app.cash.turbine.test
import com.idk.feature_poker_planning.domain.repository.RoomRepository
import com.idk.feature_poker_planning.utils.TestDataProvider
import io.mockk.every
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class LoadRoomsUseCaseTest {
    private val repository = mockk<RoomRepository>()
    private val useCase = LoadRoomsUseCase(repository)

    @Test
    fun invoke_emitsListOfRooms_whenRepositoryEmits() = runTest {
        val expectedRooms = listOf(TestDataProvider.defaultRoom)
        every { repository.observeRooms() } returns flowOf(expectedRooms)

        useCase().test {
            val rooms = awaitItem()
            assertEquals(expectedRooms, rooms)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun invoke_propagatesError_whenRepositoryEmitsError() = runTest {
        val error = RuntimeException(TestDataProvider.testError)
        every { repository.observeRooms() } returns flow { throw error }

        useCase().test {
            val thrown = awaitError()
            assertTrue(thrown is RuntimeException)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
