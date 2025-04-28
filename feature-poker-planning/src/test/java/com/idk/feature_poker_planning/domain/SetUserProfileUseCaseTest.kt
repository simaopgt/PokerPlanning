package com.idk.feature_poker_planning.domain

import com.idk.feature_poker_planning.domain.model.UserProfile
import com.idk.feature_poker_planning.domain.repository.UserProfileRepository
import com.idk.feature_poker_planning.utils.TestDataProvider
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertSame
import junit.framework.Assert.assertTrue
import junit.framework.Assert.fail
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SetUserProfileUseCaseTest {
    private val repository: UserProfileRepository = mockk()
    private val useCase = SetUserProfileUseCase(repository)

    @Test
    fun invoke_savesProfileWithGeneratedUserId_whenUserIdIsBlank() = runTest {
        val slot = slot<UserProfile>()
        coJustRun { repository.saveProfile(capture(slot)) }

        useCase(TestDataProvider.defaultUserProfile)

        coVerify(exactly = 1) { repository.saveProfile(any()) }
        val saved = slot.captured
        assertTrue(saved.userId.isNotBlank())
        assertEquals(TestDataProvider.defaultUserProfile.userName, saved.userName)
        assertEquals(TestDataProvider.defaultUserProfile.avatar, saved.avatar)
    }

    @Test
    fun invoke_propagatesException_whenRepositoryThrows() = runTest {
        val exception = TestDataProvider.testError
        coEvery { repository.saveProfile(any()) } throws exception

        try {
            useCase(TestDataProvider.defaultUserProfile)
            fail("Expected RuntimeException")
        } catch (e: RuntimeException) {
            assertSame(exception, e)
        }
    }
}
