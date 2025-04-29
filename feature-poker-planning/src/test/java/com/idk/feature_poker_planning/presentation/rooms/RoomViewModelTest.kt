package com.idk.feature_poker_planning.presentation.rooms

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import com.idk.feature_poker_planning.domain.GenerateConsensusSuggestionUseCase
import com.idk.feature_poker_planning.domain.JoinRoomUseCase
import com.idk.feature_poker_planning.domain.LoadParticipantsUseCase
import com.idk.feature_poker_planning.domain.ResetVotesUseCase
import com.idk.feature_poker_planning.domain.SubmitVoteUseCase
import com.idk.feature_poker_planning.domain.model.Participant
import com.idk.feature_poker_planning.navigation.PokerPlanningDestinations
import com.idk.feature_poker_planning.utils.TestDataProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertNull
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
    private val testRoomName = TestDataProvider.defaultRoom.name
    private val participants = listOf(
        Participant(userId = "u1", name = "Alice", avatar = "", vote = null),
        Participant(userId = "u2", name = "Bob", avatar = "", vote = null)
    )

    private lateinit var savedStateHandle: SavedStateHandle
    private val loadParticipantsUseCase: LoadParticipantsUseCase = mockk()
    private val submitVoteUseCase: SubmitVoteUseCase = mockk(relaxed = true)
    private val joinRoomUseCase: JoinRoomUseCase = mockk(relaxed = true)
    private val resetVotesUseCase: ResetVotesUseCase = mockk(relaxed = true)
    private val generateConsensusUseCase: GenerateConsensusSuggestionUseCase = mockk(relaxed = true)
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        mockkStatic(Uri::class)
        every { Uri.decode(any()) } answers { it.invocation.args[0] as String }
        Dispatchers.setMain(dispatcher)
        savedStateHandle = SavedStateHandle(
            mapOf(
                PokerPlanningDestinations.ARG_ROOM_ID to testRoomId,
                PokerPlanningDestinations.ARG_ROOM_NAME to testRoomName
            )
        )
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
            savedStateHandle,
            loadParticipantsUseCase,
            submitVoteUseCase,
            joinRoomUseCase,
            resetVotesUseCase,
            generateConsensusUseCase
        )
        dispatcher.scheduler.advanceUntilIdle()

        val ui = viewModel.uiState.value
        assertEquals(testRoomName, ui.roomName)
        assertEquals(participants, ui.participants)
    }

    @Test
    fun invoke_updatesCurrentVoteInput_whenOnVoteInputChangeCalled() = runTest {
        coEvery { joinRoomUseCase(testRoomId) } returns Unit
        coEvery { loadParticipantsUseCase(testRoomId) } returns flowOf(emptyList())

        val viewModel = RoomViewModel(
            savedStateHandle,
            loadParticipantsUseCase,
            submitVoteUseCase,
            joinRoomUseCase,
            resetVotesUseCase,
            generateConsensusUseCase
        )
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.onVoteInputChange("5")
        assertEquals("5", viewModel.uiState.value.currentVoteInput)
    }

    @Test
    fun invoke_setsVotesRevealedTrueAndLoading_whenRevealVotesCalled() = runTest {
        coEvery { joinRoomUseCase(testRoomId) } returns Unit
        coEvery { loadParticipantsUseCase(testRoomId) } returns flowOf(emptyList())

        val viewModel = RoomViewModel(
            savedStateHandle,
            loadParticipantsUseCase,
            submitVoteUseCase,
            joinRoomUseCase,
            resetVotesUseCase,
            generateConsensusUseCase
        )
        dispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.votesRevealed)
        assertFalse(viewModel.uiState.value.isLoadingAi)

        viewModel.revealVotes()
        val loadingState = viewModel.uiState.value
        assertTrue(loadingState.votesRevealed)
        assertTrue(loadingState.isLoadingAi)
        assertNull(loadingState.aiError)

        dispatcher.scheduler.advanceUntilIdle()
        val loadedState = viewModel.uiState.value
        assertFalse(loadedState.isLoadingAi)
        assertEquals("", loadedState.aiSummary)
    }

    @Test
    fun invoke_resetsVotesRevealedAndClearsInput_whenStartNewSessionCalled() = runTest {
        coEvery { joinRoomUseCase(testRoomId) } returns Unit
        coEvery { loadParticipantsUseCase(testRoomId) } returns flowOf(emptyList())

        val viewModel = RoomViewModel(
            savedStateHandle,
            loadParticipantsUseCase,
            submitVoteUseCase,
            joinRoomUseCase,
            resetVotesUseCase,
            generateConsensusUseCase
        )
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.onVoteInputChange("3")
        viewModel.revealVotes()
        dispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.uiState.value.votesRevealed)
        assertEquals("3", viewModel.uiState.value.currentVoteInput)

        viewModel.startNewSession()
        dispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.votesRevealed)
        assertEquals("", viewModel.uiState.value.currentVoteInput)
    }

    @Test
    fun invoke_callsSubmitVoteUseCase_whenCurrentVoteInputIsValid() = runTest {
        coEvery { joinRoomUseCase(testRoomId) } returns Unit
        coEvery { loadParticipantsUseCase(testRoomId) } returns flowOf(emptyList())
        coEvery { submitVoteUseCase(testRoomId, 7) } returns Unit

        val viewModel = RoomViewModel(
            savedStateHandle,
            loadParticipantsUseCase,
            submitVoteUseCase,
            joinRoomUseCase,
            resetVotesUseCase,
            generateConsensusUseCase
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
            savedStateHandle,
            loadParticipantsUseCase,
            submitVoteUseCase,
            joinRoomUseCase,
            resetVotesUseCase,
            generateConsensusUseCase
        )
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.onVoteInputChange("abc")
        viewModel.submitVote()
        dispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 0) { submitVoteUseCase(any(), any()) }
    }

    @Test
    fun invoke_callsResetVotesUseCase_whenStartNewSessionCalled() = runTest {
        coEvery { joinRoomUseCase(testRoomId) } returns Unit
        coEvery { loadParticipantsUseCase(testRoomId) } returns flowOf(emptyList())
        coEvery { resetVotesUseCase(testRoomId) } returns Unit

        val viewModel = RoomViewModel(
            savedStateHandle,
            loadParticipantsUseCase,
            submitVoteUseCase,
            joinRoomUseCase,
            resetVotesUseCase,
            generateConsensusUseCase
        )
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.startNewSession()
        dispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { resetVotesUseCase(testRoomId) }
    }
}
