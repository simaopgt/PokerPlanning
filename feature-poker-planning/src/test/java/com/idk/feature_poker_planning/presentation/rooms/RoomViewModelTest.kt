package com.idk.feature_poker_planning.presentation.rooms

import androidx.lifecycle.SavedStateHandle
import com.idk.feature_poker_planning.domain.JoinRoomUseCase
import com.idk.feature_poker_planning.domain.LoadParticipantsUseCase
import com.idk.feature_poker_planning.domain.SubmitVoteUseCase
import com.idk.feature_poker_planning.domain.model.Participant
import com.idk.feature_poker_planning.utils.RoomNavArgs
import com.idk.feature_poker_planning.utils.TestDataProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RoomViewModelTest {
    private val testRoomId = TestDataProvider.defaultRoom.id
    private val participants = listOf(
        Participant(userId = "u1", name = "Alice", vote = null),
        Participant(userId = "u2", name = "Bob", vote = null)
    )

    private lateinit var savedStateHandle: SavedStateHandle
    private val loadParticipantsUseCase: LoadParticipantsUseCase = mockk()
    private val submitVoteUseCase: SubmitVoteUseCase = mockk(relaxed = true)
    private val joinRoomUseCase: JoinRoomUseCase = mockk(relaxed = true)
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        savedStateHandle = SavedStateHandle(mapOf(RoomNavArgs.ARG_ROOM_ID to testRoomId))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun invoke_updatesUiStateWithDistinctParticipants_whenLoadParticipantsEmits() = runTest {
        coEvery { joinRoomUseCase(testRoomId) } returns Unit
        coEvery { loadParticipantsUseCase(testRoomId) } returns flowOf(participants + participants)

        val viewModel = RoomViewModel(
            savedStateHandle, loadParticipantsUseCase, submitVoteUseCase, joinRoomUseCase
        )
        dispatcher.scheduler.advanceUntilIdle()

        val ui = viewModel.uiState.value
        assertEquals(testRoomId, ui.roomName)
        assertEquals(participants, ui.participants)
    }

    @Test
    fun invoke_updatesCurrentVoteInput_whenOnVoteInputChangeCalled() = runTest {
        coEvery { joinRoomUseCase(testRoomId) } returns Unit
        coEvery { loadParticipantsUseCase(testRoomId) } returns flowOf(emptyList())

        val viewModel = RoomViewModel(
            savedStateHandle, loadParticipantsUseCase, submitVoteUseCase, joinRoomUseCase
        )
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.onVoteInputChange("5")

        assertEquals("5", viewModel.uiState.value.currentVoteInput)
    }

    @Test
    fun invoke_setsVotesRevealedTrue_whenRevealVotesCalled() = runTest {
        coEvery { joinRoomUseCase(testRoomId) } returns Unit
        coEvery { loadParticipantsUseCase(testRoomId) } returns flowOf(emptyList())

        val viewModel = RoomViewModel(
            savedStateHandle, loadParticipantsUseCase, submitVoteUseCase, joinRoomUseCase
        )
        dispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.votesRevealed)
        viewModel.revealVotes()

        assertTrue(viewModel.uiState.value.votesRevealed)
    }

    @Test
    fun invoke_resetsVotesRevealedAndClearsInput_whenStartNewSessionCalled() = runTest {
        coEvery { joinRoomUseCase(testRoomId) } returns Unit
        coEvery { loadParticipantsUseCase(testRoomId) } returns flowOf(emptyList())

        val viewModel = RoomViewModel(
            savedStateHandle, loadParticipantsUseCase, submitVoteUseCase, joinRoomUseCase
        )
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.onVoteInputChange("3")
        viewModel.revealVotes()
        assertTrue(viewModel.uiState.value.votesRevealed)
        assertEquals("3", viewModel.uiState.value.currentVoteInput)

        viewModel.startNewSession()

        assertFalse(viewModel.uiState.value.votesRevealed)
        assertEquals("", viewModel.uiState.value.currentVoteInput)
    }

    @Test
    fun invoke_callsSubmitVoteUseCase_whenCurrentVoteInputIsValid() = runTest {
        coEvery { joinRoomUseCase(testRoomId) } returns Unit
        coEvery { loadParticipantsUseCase(testRoomId) } returns flowOf(emptyList())
        coEvery { submitVoteUseCase(testRoomId, 7) } returns Unit

        val viewModel = RoomViewModel(
            savedStateHandle, loadParticipantsUseCase, submitVoteUseCase, joinRoomUseCase
        )
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.onVoteInputChange("7")
        viewModel.submitVote()
        dispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { submitVoteUseCase(testRoomId, 7) }
    }

    @Test
    fun invoke_doesNotCallSubmitVoteUseCase_whenCurrentVoteInputIsInvalid() = runTest {
        coEvery { joinRoomUseCase(testRoomId) } returns Unit
        coEvery { loadParticipantsUseCase(testRoomId) } returns flowOf(emptyList())

        val viewModel = RoomViewModel(
            savedStateHandle, loadParticipantsUseCase, submitVoteUseCase, joinRoomUseCase
        )
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.onVoteInputChange("abc")
        viewModel.submitVote()
        dispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 0) { submitVoteUseCase(any(), any()) }
    }
}
