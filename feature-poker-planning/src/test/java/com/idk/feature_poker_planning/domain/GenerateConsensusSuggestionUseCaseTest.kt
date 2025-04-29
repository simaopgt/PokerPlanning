package com.idk.feature_poker_planning.domain

import com.idk.feature_poker_planning.domain.model.Participant
import com.idk.feature_poker_planning.domain.repository.VertexAiRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class GenerateConsensusSuggestionUseCaseTest {

    private val vertexAiRepository: VertexAiRepository = mockk()
    private val useCase = GenerateConsensusSuggestionUseCase(vertexAiRepository)

    @Test
    fun `invoke throws IllegalArgumentException when no votes available`() = runTest {
        val participants = listOf(
            Participant(userId = "1", name = "Alice", avatar = "", vote = null),
            Participant(userId = "2", name = "Bob", avatar = "", vote = null)
        )

        try {
            useCase(participants)
            fail("Expected IllegalArgumentException when no votes are present")
        } catch (e: IllegalArgumentException) {
            assertEquals("Nenhum voto disponível para gerar sugestão", e.message)
        }
    }

    @Test
    fun `invoke calls repository with correct prompt and returns suggestion`() = runTest {
        val participants = listOf(
            Participant(userId = "1", name = "Alice", avatar = "", vote = 3),
            Participant(userId = "2", name = "Bob", avatar = "", vote = 8),
            Participant(userId = "3", name = "Charlie", avatar = "", vote = 5)
        )

        val expectedPrompt = buildString {
            append("Este é um app de planning poker.")
            append("O maior voto foi 8 pelo usuário Bob. ")
            append("O menor voto foi 3 pelo usuário Alice. ")
            append("Sugira um único texto, sem mais de uma opção, instigando os usuários a votarem novamente caso haja divergência de valores entre o voto máximo e mínimo ")
            append("para tentarem chegar em um consenso, afinal esse é o objetivo da planning poker.")
            append("Utilize uma linguagem amigável e encorajadora, como se você fosse um facilitador.")
        }

        val fakeSuggestion = "Claro! Vamos discutir as diferenças..."
        coEvery { vertexAiRepository.getSummary(expectedPrompt) } returns fakeSuggestion

        val result = useCase(participants)

        assertEquals(fakeSuggestion, result)
        coVerify(exactly = 1) { vertexAiRepository.getSummary(expectedPrompt) }
    }
}
