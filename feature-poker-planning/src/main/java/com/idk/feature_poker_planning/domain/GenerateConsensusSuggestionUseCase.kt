package com.idk.feature_poker_planning.domain

import android.annotation.SuppressLint
import com.idk.feature_poker_planning.domain.model.Participant
import com.idk.feature_poker_planning.domain.repository.VertexAiRepository
import com.idk.feature_poker_planning.utils.FirestoreConstants.ERROR_CANNOT_DETERMINE_MAX_VOTE
import com.idk.feature_poker_planning.utils.FirestoreConstants.ERROR_CANNOT_DETERMINE_MIN_VOTE
import com.idk.feature_poker_planning.utils.FirestoreConstants.ERROR_MAX_PARTICIPANT_NOT_FOUND
import com.idk.feature_poker_planning.utils.FirestoreConstants.ERROR_MIN_PARTICIPANT_NOT_FOUND
import com.idk.feature_poker_planning.utils.FirestoreConstants.ERROR_NO_VOTES
import com.idk.feature_poker_planning.utils.FirestoreConstants.PROMPT_CONSENSUS
import com.idk.feature_poker_planning.utils.FirestoreConstants.PROMPT_MAX_VOTE
import com.idk.feature_poker_planning.utils.FirestoreConstants.PROMPT_MIN_VOTE
import com.idk.feature_poker_planning.utils.FirestoreConstants.PROMPT_PREFIX
import com.idk.feature_poker_planning.utils.FirestoreConstants.PROMPT_STYLE
import com.idk.feature_poker_planning.utils.FirestoreConstants.PROMPT_SUGGESTION
import javax.inject.Inject

class GenerateConsensusSuggestionUseCase @Inject constructor(
    private val vertexAiRepository: VertexAiRepository
) {

    @SuppressLint("DefaultLocale")
    suspend operator fun invoke(participants: List<Participant>): String {
        val voteValues = participants.mapNotNull { it.vote }
        if (voteValues.isEmpty()) {
            throw IllegalArgumentException(ERROR_NO_VOTES)
        }

        val highestVote =
            voteValues.maxOrNull() ?: throw IllegalStateException(ERROR_CANNOT_DETERMINE_MAX_VOTE)
        val lowestVote =
            voteValues.minOrNull() ?: throw IllegalStateException(ERROR_CANNOT_DETERMINE_MIN_VOTE)

        val participantWithHighestVote =
            participants.firstOrNull { it.vote == highestVote } ?: throw IllegalStateException(
                ERROR_MAX_PARTICIPANT_NOT_FOUND
            )

        val participantWithLowestVote =
            participants.firstOrNull { it.vote == lowestVote } ?: throw IllegalStateException(
                ERROR_MIN_PARTICIPANT_NOT_FOUND
            )

        val promptText = buildString {
            append(PROMPT_PREFIX)
            append(String.format(PROMPT_MAX_VOTE, highestVote, participantWithHighestVote.name))
            append(String.format(PROMPT_MIN_VOTE, lowestVote, participantWithLowestVote.name))
            append(PROMPT_SUGGESTION)
            append(PROMPT_CONSENSUS)
            append(PROMPT_STYLE)
        }

        return vertexAiRepository.getSummary(promptText)
    }
}
