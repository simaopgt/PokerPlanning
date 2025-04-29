package com.idk.feature_poker_planning.data.repository


import com.google.firebase.vertexai.FirebaseVertexAI
import com.google.firebase.vertexai.GenerativeModel
import com.google.firebase.vertexai.type.asTextOrNull
import com.idk.feature_poker_planning.domain.repository.VertexAiRepository
import com.idk.feature_poker_planning.utils.FirestoreConstants.ERROR_EMPTY_AI_RESPONSE
import com.idk.feature_poker_planning.utils.FirestoreConstants.GENERATIVE_AI_MODEL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VertexAiRepositoryImpl @Inject constructor() : VertexAiRepository {

    private val vertexAi: FirebaseVertexAI = FirebaseVertexAI.instance
    private val generativeModel: GenerativeModel = vertexAi.generativeModel(GENERATIVE_AI_MODEL)

    override suspend fun getSummary(prompt: String): String {
        val response = generativeModel.generateContent(prompt)

        val firstCandidateText =
            response.candidates.firstOrNull()?.content?.parts?.mapNotNull { part -> part.asTextOrNull() }
                ?.joinToString(separator = "")

        return firstCandidateText ?: response.text
        ?: throw IllegalStateException(ERROR_EMPTY_AI_RESPONSE)
    }
}
