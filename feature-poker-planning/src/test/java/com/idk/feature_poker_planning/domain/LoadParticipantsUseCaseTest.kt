package com.idk.feature_poker_planning.domain

import com.idk.feature_poker_planning.domain.repository.RoomRepository
import com.idk.feature_poker_planning.utils.TestDataProvider
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class LoadParticipantsUseCaseTest {
    private val repository: RoomRepository = mockk()
    private val useCase = LoadParticipantsUseCase(repository)

    @Test
    fun invoke_withValidRoomId_emitsParticipants() = runTest {
        coEvery { repository.observeParticipants(TestDataProvider.TEST_ROOM_ID) } returns flowOf(
            TestDataProvider.participants
        )

        val result = useCase(TestDataProvider.TEST_ROOM_ID).first()

        assertEquals(TestDataProvider.participants, result)
    }

    @Test
    fun invoke_repositoryThrows_propagatesException() = runTest {
        coEvery { repository.observeParticipants(TestDataProvider.TEST_ROOM_ID) } returns flow { throw TestDataProvider.testError }

        try {
            useCase(TestDataProvider.TEST_ROOM_ID).first()
            Assert.fail("Expected RuntimeException")
        } catch (e: RuntimeException) {
            assertEquals(TestDataProvider.testError, e)
        }
    }
}
