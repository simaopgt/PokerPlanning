package com.idk.feature_poker_planning.domain

import com.idk.feature_poker_planning.domain.repository.RoomRepository
import com.idk.feature_poker_planning.utils.TestDataProvider
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class SubmitVoteUseCaseTest {
    private val repository: RoomRepository = mockk()
    private val useCase = SubmitVoteUseCase(repository)

    @Test
    fun invoke_validParams_callsRepository() = runTest {
        coJustRun {
            repository.submitVote(
                TestDataProvider.TEST_ROOM_ID,
                TestDataProvider.TEST_USER_ID,
                TestDataProvider.TEST_VOTE
            )
        }

        useCase(
            TestDataProvider.TEST_ROOM_ID, TestDataProvider.TEST_USER_ID, TestDataProvider.TEST_VOTE
        )

        coVerify(exactly = 1) {
            repository.submitVote(
                TestDataProvider.TEST_ROOM_ID,
                TestDataProvider.TEST_USER_ID,
                TestDataProvider.TEST_VOTE
            )
        }
    }

    @Test
    fun invoke_repositoryThrows_propagatesException() = runTest {
        coEvery {
            repository.submitVote(
                TestDataProvider.TEST_ROOM_ID,
                TestDataProvider.TEST_USER_ID,
                TestDataProvider.TEST_VOTE
            )
        } throws TestDataProvider.testError

        try {
            useCase(
                TestDataProvider.TEST_ROOM_ID,
                TestDataProvider.TEST_USER_ID,
                TestDataProvider.TEST_VOTE
            )
            Assert.fail("Expected RuntimeException")
        } catch (e: RuntimeException) {
            Assert.assertEquals(TestDataProvider.testError, e)
        }
    }
}
