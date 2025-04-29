package com.idk.feature_poker_planning.domain

import com.idk.feature_poker_planning.domain.model.Participant
import com.idk.feature_poker_planning.domain.repository.VertexAiRepository
import javax.inject.Inject

class GenerateConsensusSuggestionUseCase @Inject constructor(
    private val vertexAiRepository: VertexAiRepository
) {
    suspend operator fun invoke(participants: List<Participant>): String {
        val votes = participants.mapNotNull { it.vote }
        if (votes.isEmpty()) {
            throw IllegalArgumentException("Nenhum voto disponível para gerar sugestão")
        }

        val maxVote = votes.maxOrNull()
            ?: throw IllegalStateException("Não foi possível determinar o maior voto")
        val minVote = votes.minOrNull()
            ?: throw IllegalStateException("Não foi possível determinar o menor voto")

        val maxP = participants.firstOrNull { it.vote == maxVote }
            ?: throw IllegalStateException("Participante com maior voto não encontrado")
        val minP = participants.firstOrNull { it.vote == minVote }
            ?: throw IllegalStateException("Participante com menor voto não encontrado")

        val prompt = buildString {
            append("Este é um app de planning poker.")
            append("O maior voto foi $maxVote pelo usuário ${maxP.name}. ")
            append("O menor voto foi $minVote pelo usuário ${minP.name}. ")
            append("Sugira um único texto, sem mais de uma opção, instigando os usuários a votarem novamente caso haja divergencia de valores entre o voto máximo e mínimo ")
            append("para tentarem chegar em um consenso, afinal esse é o objetivo da planning poker.")
            append("Utilize uma linguagem amigável e encorajadora, como se você fosse um facilitador.")
        }

        return vertexAiRepository.getSummary(prompt)
    }
}
