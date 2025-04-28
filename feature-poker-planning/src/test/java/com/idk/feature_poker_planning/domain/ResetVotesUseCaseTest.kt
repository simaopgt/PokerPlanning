package com.idk.feature_poker_planning.domain

import com.google.firebase.firestore.util.Assert.fail
import com.idk.feature_poker_planning.domain.repository.RoomRepository
import com.idk.feature_poker_planning.utils.TestDataProvider
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ResetVotesUseCaseTest {

    private val repository: RoomRepository = mockk()
    private val useCase = ResetVotesUseCase(repository)

    @Test
    fun invoke_validRoomId_callsResetVotesOnce() = runTest {
        coJustRun { repository.resetVotes(TestDataProvider.TEST_ROOM_ID) }

        useCase(TestDataProvider.TEST_ROOM_ID)

        coVerify(exactly = 1) {
            repository.resetVotes(TestDataProvider.TEST_ROOM_ID)
        }
    }

    @Test
    fun invoke_repositoryThrows_propagatesException() = runTest {
        coEvery { repository.resetVotes(TestDataProvider.TEST_ROOM_ID) } throws TestDataProvider.testError

        try {
            useCase(TestDataProvider.TEST_ROOM_ID)
            fail("Expected ${TestDataProvider.testError::class.simpleName} to be thrown")
        } catch (e: RuntimeException) {
            assertEquals(TestDataProvider.testError.message, e.message)
        }
    }
}
