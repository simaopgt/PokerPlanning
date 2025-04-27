package com.idk.feature_poker_planning.presentation.home

import com.idk.feature_poker_planning.domain.CreateRoomUseCase
import com.idk.feature_poker_planning.domain.GetUserProfileUseCase
import com.idk.feature_poker_planning.domain.LoadRoomsUseCase
import com.idk.feature_poker_planning.domain.model.Room
import com.idk.feature_poker_planning.utils.TestDataProvider
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val testScheduler = TestCoroutineScheduler()
    private val dispatcher = StandardTestDispatcher(testScheduler)
    private lateinit var createRoomUseCase: CreateRoomUseCase
    private lateinit var loadRoomsUseCase: LoadRoomsUseCase
    private lateinit var getUserProfileUseCase: GetUserProfileUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        createRoomUseCase = mockk()
        loadRoomsUseCase = mockk()
        getUserProfileUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_loadsProfileAndRooms_updatesUiState() = runTest(testScheduler) {
        coEvery { getUserProfileUseCase() } returns TestDataProvider.defaultUserProfile
        val rooms = listOf(TestDataProvider.defaultRoom)
        every { loadRoomsUseCase() } returns flowOf(rooms)
        coJustRun { createRoomUseCase(any()) }

        val viewModel = HomeViewModel(
            createRoomUseCase, loadRoomsUseCase, getUserProfileUseCase
        )
        testScheduler.advanceUntilIdle()

        val ui = viewModel.uiState.value
        assertEquals(TestDataProvider.defaultUserProfile.userName, ui.userName)
        assertEquals(TestDataProvider.defaultUserProfile.avatar, ui.userAvatar)
        assertEquals(rooms, ui.rooms)
    }

    @Test
    fun createRoom_withName_invokesUseCaseAndReloadsRooms() = runTest(testScheduler) {
        coEvery { getUserProfileUseCase() } returns TestDataProvider.defaultUserProfile
        val initial = emptyList<Room>()
        val updated = listOf(TestDataProvider.defaultRoom)
        every { loadRoomsUseCase() } returnsMany listOf(
            flowOf(initial), flowOf(updated)
        )
        coJustRun { createRoomUseCase(any()) }

        val viewModel = HomeViewModel(
            createRoomUseCase, loadRoomsUseCase, getUserProfileUseCase
        )
        testScheduler.advanceUntilIdle()
        assertEquals(initial, viewModel.uiState.value.rooms)

        viewModel.createRoom(TestDataProvider.defaultRoom.name)
        testScheduler.advanceUntilIdle()

        coVerify { createRoomUseCase.invoke(TestDataProvider.defaultRoom.name) }
        assertEquals(updated, viewModel.uiState.value.rooms)
    }

    @Test
    fun createRoom_withoutName_invokesUseCaseWithNullAndReloadsRooms() = runTest(testScheduler) {
        coEvery { getUserProfileUseCase() } returns TestDataProvider.defaultUserProfile
        val initial = emptyList<Room>()
        val updated = listOf(TestDataProvider.defaultRoom)
        every { loadRoomsUseCase() } returnsMany listOf(
            flowOf(initial), flowOf(updated)
        )
        coJustRun { createRoomUseCase(any()) }

        val viewModel = HomeViewModel(
            createRoomUseCase, loadRoomsUseCase, getUserProfileUseCase
        )
        testScheduler.advanceUntilIdle()

        viewModel.createRoom()
        testScheduler.advanceUntilIdle()

        coVerify { createRoomUseCase.invoke(null) }
        assertEquals(updated, viewModel.uiState.value.rooms)
    }
}
