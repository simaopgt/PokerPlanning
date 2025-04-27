package com.idk.feature_poker_planning.domain

import com.idk.feature_poker_planning.domain.repository.UserProfileRepository
import com.idk.feature_poker_planning.utils.TestDataProvider
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class SetUserProfileUseCaseTest {
    private val repository: UserProfileRepository = mockk()
    private val useCase = SetUserProfileUseCase(repository)

    @Test
    fun invoke_withValidProfile_callsRepository() = runTest {
        coJustRun { repository.saveProfile(TestDataProvider.defaultUserProfile) }

        useCase(TestDataProvider.defaultUserProfile)

        coVerify(exactly = 1) { repository.saveProfile(TestDataProvider.defaultUserProfile) }
    }

    @Test
    fun invoke_repositoryThrows_propagatesException() = runTest {
        coEvery { repository.saveProfile(TestDataProvider.defaultUserProfile) } throws TestDataProvider.testError

        try {
            useCase(TestDataProvider.defaultUserProfile)
            Assert.fail("Expected RuntimeException")
        } catch (e: RuntimeException) {
            Assert.assertEquals(TestDataProvider.testError, e)
        }
    }
}