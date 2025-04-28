package com.idk.feature_poker_planning.domain

import com.idk.feature_poker_planning.domain.repository.RoomRepository
import com.idk.feature_poker_planning.utils.TestDataProvider
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.Assert.assertSame
import junit.framework.Assert.fail
import kotlinx.coroutines.test.runTest
import org.junit.Test

class JoinRoomUseCaseTest {
    private val getUserProfileUseCase: GetUserProfileUseCase = mockk()
    private val roomRepository: RoomRepository = mockk()
    private val useCase = JoinRoomUseCase(getUserProfileUseCase, roomRepository)

    @Test
    fun invoke_callsAddParticipantWithProfileData_whenInvoked() = runTest {
        val profile = TestDataProvider.defaultUserProfile
        coEvery { getUserProfileUseCase() } returns profile
        coJustRun { roomRepository.addParticipant(any(), any(), any(), any()) }

        useCase(TestDataProvider.TEST_ROOM_ID)

        coVerify(exactly = 1) {
            roomRepository.addParticipant(
                roomId = TestDataProvider.TEST_ROOM_ID,
                userId = profile.userId,
                name = profile.userName,
                avatar = profile.avatar
            )
        }
    }

    @Test
    fun invoke_propagatesException_whenRepositoryThrows() = runTest {
        val profile = TestDataProvider.defaultUserProfile
        coEvery { getUserProfileUseCase() } returns profile
        val error = TestDataProvider.testError
        coEvery { roomRepository.addParticipant(any(), any(), any(), any()) } throws error

        try {
            useCase(TestDataProvider.TEST_ROOM_ID)
            fail("Expected RuntimeException")
        } catch (e: RuntimeException) {
            assertSame(error, e)
        }
    }

    @Test
    fun invoke_propagatesException_whenGetUserProfileThrows() = runTest {
        val error = TestDataProvider.testError
        coEvery { getUserProfileUseCase() } throws error

        try {
            useCase(TestDataProvider.TEST_ROOM_ID)
            fail("Expected RuntimeException")
        } catch (e: RuntimeException) {
            assertSame(error, e)
        }
    }
}
