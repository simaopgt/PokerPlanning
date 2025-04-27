package com.idk.feature_poker_planning.domain

import com.idk.feature_poker_planning.domain.repository.UserProfileRepository
import com.idk.feature_poker_planning.utils.TestDataProvider
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class HasUserProfileUseCaseTest {
    private val repository: UserProfileRepository = mockk()
    private val useCase = HasUserProfileUseCase(repository)

    @Test
    fun invoke_repositoryHasProfile_returnsTrue() = runTest {
        coEvery { repository.hasProfile() } returns TestDataProvider.HAS_PROFILE

        val result = useCase()

        assertTrue(result)
    }

    @Test
    fun invoke_repositoryNoProfile_returnsFalse() = runTest {
        coEvery { repository.hasProfile() } returns TestDataProvider.NO_PROFILE

        val result = useCase()

        assertFalse(result)
    }

    @Test
    fun invoke_repositoryThrows_propagatesException() = runTest {
        coEvery { repository.hasProfile() } throws TestDataProvider.testError

        try {
            useCase()
            Assert.fail("Expected RuntimeException")
        } catch (e: RuntimeException) {
            assertTrue(e === TestDataProvider.testError)
        }
    }
}
