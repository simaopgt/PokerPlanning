package com.idk.feature_poker_planning.domain

import android.annotation.SuppressLint
import com.idk.feature_poker_planning.domain.model.Participant
import com.idk.feature_poker_planning.domain.repository.VertexAiRepository
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

    private companion object {
        const val ERROR_NO_VOTES = "Nenhum voto disponível para gerar sugestão"
        const val ERROR_CANNOT_DETERMINE_MAX_VOTE = "Não foi possível determinar o maior voto"
        const val ERROR_CANNOT_DETERMINE_MIN_VOTE = "Não foi possível determinar o menor voto"
        const val ERROR_MAX_PARTICIPANT_NOT_FOUND = "Participante com maior voto não encontrado"
        const val ERROR_MIN_PARTICIPANT_NOT_FOUND = "Participante com menor voto não encontrado"

        const val PROMPT_PREFIX = "Este é um app de planning poker."
        const val PROMPT_MAX_VOTE = "O maior voto foi %d pelo usuário %s. "
        const val PROMPT_MIN_VOTE = "O menor voto foi %d pelo usuário %s. "
        const val PROMPT_SUGGESTION =
            "Sugira um único texto, sem mais de uma opção, instigando os usuários a votarem novamente caso haja divergência de valores entre o voto máximo e mínimo "
        const val PROMPT_CONSENSUS =
            "para tentarem chegar em um consenso, afinal esse é o objetivo da planning poker."
        const val PROMPT_STYLE =
            "Utilize uma linguagem amigável e encorajadora, como se você fosse um facilitador."
    }
}
