package com.idk.feature_poker_planning.domain

import com.google.firebase.firestore.util.Assert.fail
import com.idk.feature_poker_planning.domain.repository.RoomRepository
import com.idk.feature_poker_planning.utils.TestDataProvider
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SubmitVoteUseCaseTest {
    private val repository: RoomRepository = mockk()
    private val getUserProfileUseCase: GetUserProfileUseCase = mockk()
    private val useCase = SubmitVoteUseCase(repository, getUserProfileUseCase)

    @Test
    fun invoke_callsRepositorySubmitVote_whenParametersAreValid() = runTest {
        coEvery { getUserProfileUseCase() } returns TestDataProvider.defaultUserProfile
        coJustRun {
            repository.submitVote(
                TestDataProvider.TEST_ROOM_ID,
                TestDataProvider.defaultUserProfile.userId,
                TestDataProvider.defaultUserProfile.userName,
                TestDataProvider.defaultUserProfile.avatar,
                TestDataProvider.TEST_VOTE
            )
        }

        useCase(TestDataProvider.TEST_ROOM_ID, TestDataProvider.TEST_VOTE)

        coVerify(exactly = 1) {
            repository.submitVote(
                TestDataProvider.TEST_ROOM_ID,
                TestDataProvider.defaultUserProfile.userId,
                TestDataProvider.defaultUserProfile.userName,
                TestDataProvider.defaultUserProfile.avatar,
                TestDataProvider.TEST_VOTE
            )
        }
    }

    @Test
    fun invoke_propagatesException_whenRepositoryThrows() = runTest {
        coEvery { getUserProfileUseCase() } returns TestDataProvider.defaultUserProfile
        coEvery {
            repository.submitVote(
                TestDataProvider.TEST_ROOM_ID,
                TestDataProvider.defaultUserProfile.userId,
                TestDataProvider.defaultUserProfile.userName,
                TestDataProvider.defaultUserProfile.avatar,
                TestDataProvider.TEST_VOTE
            )
        } throws TestDataProvider.testError

        try {
            useCase(TestDataProvider.TEST_ROOM_ID, TestDataProvider.TEST_VOTE)
            fail("Expected RuntimeException")
        } catch (e: RuntimeException) {
            assertEquals(TestDataProvider.testError, e)
        }
    }
}
