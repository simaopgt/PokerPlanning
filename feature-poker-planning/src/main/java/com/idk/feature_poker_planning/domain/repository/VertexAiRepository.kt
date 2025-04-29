package com.idk.feature_poker_planning.domain.repository


interface VertexAiRepository {
    suspend fun getSummary(prompt: String): String
}
