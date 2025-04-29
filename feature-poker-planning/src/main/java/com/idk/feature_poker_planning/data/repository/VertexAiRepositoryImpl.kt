package com.idk.feature_poker_planning.data.repository


import com.google.firebase.vertexai.FirebaseVertexAI
import com.google.firebase.vertexai.GenerativeModel
import com.google.firebase.vertexai.type.asTextOrNull
import com.idk.feature_poker_planning.domain.repository.VertexAiRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VertexAiRepositoryImpl @Inject constructor() : VertexAiRepository {

    private val vertexAi: FirebaseVertexAI = FirebaseVertexAI.instance
    private val generativeModel: GenerativeModel = vertexAi.generativeModel("gemini-2.0-flash")

    override suspend fun getSummary(prompt: String): String {
        val response = generativeModel.generateContent(prompt)

        val firstCandidateText =
            response.candidates.firstOrNull()?.content?.parts?.mapNotNull { part -> part.asTextOrNull() }
                ?.joinToString(separator = "")

        return firstCandidateText ?: response.text
        ?: throw IllegalStateException("Resposta AI vazia")
    }
}
