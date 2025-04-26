package com.idk.feature_poker_planning.presentation.home

import com.idk.feature_poker_planning.domain.CreateRoomUseCase
import com.idk.feature_poker_planning.domain.LoadRoomsUseCase
import com.idk.feature_poker_planning.domain.model.Room
import com.idk.feature_poker_planning.utils.TestDataProvider
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val createRoomUseCase = mockk<CreateRoomUseCase>()
    private val loadRoomsUseCase = mockk<LoadRoomsUseCase>()

    @Before
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_loadsRoomsAndUpdatesUiState() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        val expectedRooms = listOf(TestDataProvider.defaultRoom)
        every { loadRoomsUseCase() } returns flowOf(expectedRooms)
        coJustRun { createRoomUseCase(any()) }

        val viewModel = HomeViewModel(createRoomUseCase, loadRoomsUseCase)

        testScheduler.advanceUntilIdle()

        assertEquals(expectedRooms, viewModel.uiState.value.rooms)
    }

    @Test
    fun createRoom_invokesUseCaseAndReloadsRooms() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        val initialRooms = emptyList<Room>()
        val newRooms = listOf(TestDataProvider.defaultRoom)
        every { loadRoomsUseCase.invoke() } returnsMany listOf(
            flowOf(initialRooms), flowOf(newRooms)
        )
        coJustRun { createRoomUseCase.invoke(any()) }

        val viewModel = HomeViewModel(createRoomUseCase, loadRoomsUseCase)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(initialRooms, viewModel.uiState.value.rooms)

        viewModel.createRoom(TestDataProvider.defaultRoom.name)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { createRoomUseCase.invoke(TestDataProvider.defaultRoom.name) }
        assertEquals(newRooms, viewModel.uiState.value.rooms)
    }
}
