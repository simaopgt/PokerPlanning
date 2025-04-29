package com.idk.feature_poker_planning.presentation.rooms

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.idk.feature_poker_planning.domain.GenerateConsensusSuggestionUseCase
import com.idk.feature_poker_planning.domain.GetUserProfileUseCase
import com.idk.feature_poker_planning.domain.JoinRoomUseCase
import com.idk.feature_poker_planning.domain.LoadParticipantsUseCase
import com.idk.feature_poker_planning.domain.ResetVotesUseCase
import com.idk.feature_poker_planning.domain.SubmitVoteUseCase
import com.idk.feature_poker_planning.domain.model.Participant
import com.idk.feature_poker_planning.domain.model.Room
import com.idk.feature_poker_planning.domain.repository.RoomRepository
import com.idk.feature_poker_planning.navigation.PokerPlanningDestinations
import com.idk.feature_poker_planning.utils.UiTestsDataProvider
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class RoomScreenAndroidTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val testRoomId = UiTestsDataProvider.TEST_ROOM_ID
    private val testRoomName = UiTestsDataProvider.TEST_ROOM_NAME

    private lateinit var fakeRepository: FakeRoomRepository
    private val mockProfileUseCase = mockk<GetUserProfileUseCase>()
    private lateinit var loadParticipantsUseCase: LoadParticipantsUseCase
    private lateinit var joinRoomUseCase: JoinRoomUseCase
    private lateinit var submitVoteUseCase: SubmitVoteUseCase
    private lateinit var resetVotesUseCase: ResetVotesUseCase
    private lateinit var generateConsensusUseCase: GenerateConsensusSuggestionUseCase

    @Before
    fun setUp() {
        fakeRepository = FakeRoomRepository(UiTestsDataProvider.participantsFlow(emptyList()))
        loadParticipantsUseCase = LoadParticipantsUseCase(fakeRepository)
        joinRoomUseCase = JoinRoomUseCase(mockProfileUseCase, fakeRepository)
        submitVoteUseCase = SubmitVoteUseCase(fakeRepository, mockProfileUseCase)
        resetVotesUseCase = ResetVotesUseCase(fakeRepository)
        generateConsensusUseCase = mockk(relaxed = true)

        coEvery { mockProfileUseCase() } returns UiTestsDataProvider.DEFAULT_USER_PROFILE
        coEvery { generateConsensusUseCase(any()) } returns ""
    }

    private fun setContentWith(participants: List<Participant>) {
        fakeRepository = FakeRoomRepository(UiTestsDataProvider.participantsFlow(participants))
        val load = LoadParticipantsUseCase(fakeRepository)
        val join = JoinRoomUseCase(mockProfileUseCase, fakeRepository)
        val submit = SubmitVoteUseCase(fakeRepository, mockProfileUseCase)
        val reset = ResetVotesUseCase(fakeRepository)
        val generate = generateConsensusUseCase

        val viewModel = RoomViewModel(
            savedStateHandle = SavedStateHandle(
                mapOf(
                    PokerPlanningDestinations.ARG_ROOM_ID to testRoomId,
                    PokerPlanningDestinations.ARG_ROOM_NAME to testRoomName
                )
            ),
            loadParticipantsUseCase = load,
            submitVoteUseCase = submit,
            joinRoomUseCase = join,
            resetVotesUseCase = reset,
            generateConsensusUseCase = generate
        )

        composeTestRule.setContent {
            RoomRoute(
                onBack = { }, viewModel = viewModel
            )
        }
    }

    @Test
    fun titleAndTeamLabel_areDisplayed() {
        setContentWith(listOf(UiTestsDataProvider.PARTICIPANT_NOT_VOTED))

        composeTestRule.onNodeWithText(testRoomName).assertIsDisplayed()
        composeTestRule.onNodeWithText("Team").assertIsDisplayed()
    }

    @Test
    fun checkIcon_showsForVotedParticipant_beforeReveal() {
        setContentWith(listOf(UiTestsDataProvider.PARTICIPANT_VOTED_3))

        composeTestRule.onNodeWithContentDescription("Já votou").assertIsDisplayed()
    }

    @Test
    fun voteButton_enabledBeforeVoting() {
        setContentWith(listOf(UiTestsDataProvider.PARTICIPANT_NOT_VOTED))

        composeTestRule.onNodeWithText("Votar").assertIsEnabled()
    }

    @Test
    fun voteButton_disabledAfterRevealingVotes() {
        setContentWith(listOf(UiTestsDataProvider.PARTICIPANT_VOTED_5))
        composeTestRule.onNodeWithText("Revelar Votos").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Votar").assertIsNotEnabled()
    }

    class FakeRoomRepository(
        private val participantsFlow: Flow<List<Participant>>
    ) : RoomRepository {
        override suspend fun createRoom(room: Room) = Unit
        override fun observeRooms() = flowOf<List<Room>>()
        override suspend fun addParticipant(
            roomId: String, userId: String, name: String, avatar: String
        ) = Unit

        override fun observeParticipants(roomId: String): Flow<List<Participant>> = participantsFlow

        override suspend fun submitVote(
            roomId: String, userId: String, name: String, avatar: String, vote: Int
        ) = Unit

        override suspend fun resetVotes(roomId: String) = Unit
    }
}
