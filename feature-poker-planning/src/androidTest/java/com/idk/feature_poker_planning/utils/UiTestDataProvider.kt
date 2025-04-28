package com.idk.feature_poker_planning.utils

import com.idk.feature_poker_planning.domain.model.Participant
import com.idk.feature_poker_planning.domain.model.UserProfile
import kotlinx.coroutines.flow.flowOf

object UiTestsDataProvider {
    const val TEST_ROOM_ID = "room123"
    const val TEST_ROOM_NAME = "Test Room"
    const val TEST_USER_ID = "user1"

    val DEFAULT_USER_PROFILE = UserProfile(
        userId = TEST_USER_ID,
        userName = "Alice",
        avatar = "avatar"
    )

    val PARTICIPANT_NOT_VOTED = Participant(
        userId = TEST_USER_ID,
        name = "Alice",
        avatar = "avatar",
        vote = null
    )

    val PARTICIPANT_VOTED_3 = Participant(
        userId = TEST_USER_ID,
        name = "Alice",
        avatar = "avatar",
        vote = 3
    )

    val PARTICIPANT_VOTED_5 = Participant(
        userId = TEST_USER_ID,
        name = "Alice",
        avatar = "avatar",
        vote = 5
    )

    fun participantsFlow(participants: List<Participant>) = flowOf(participants)
}
