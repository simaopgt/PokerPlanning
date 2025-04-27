package com.idk.feature_poker_planning.domain

import com.idk.feature_poker_planning.domain.repository.UserProfileRepository
import com.idk.feature_poker_planning.utils.TestDataProvider
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class GetUserProfileUseCaseTest {
    private val repository: UserProfileRepository = mockk()
    private val useCase = GetUserProfileUseCase(repository)

    @Test
    fun invoke_repositoryReturnsProfile_returnsProfile() = runTest {
        coEvery { repository.getProfile() } returns TestDataProvider.defaultUserProfile

        val result = useCase()

        assertEquals(TestDataProvider.defaultUserProfile, result)
    }

    @Test
    fun invoke_repositoryThrowsPropagatesException() = runTest {
        coEvery { repository.getProfile() } throws TestDataProvider.testError

        try {
            useCase()
            Assert.fail("Expected RuntimeException")
        } catch (e: RuntimeException) {
            assertEquals(TestDataProvider.testError, e)
        }
    }
}
