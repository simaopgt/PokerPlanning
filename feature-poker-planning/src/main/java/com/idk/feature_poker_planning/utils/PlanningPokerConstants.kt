package com.idk.feature_poker_planning.utils

object FirestoreConstants {
//    Firestore
    const val ROOMS_COLLECTION = "rooms"
    const val ROOM_ID_FIELD = "id"
    const val ROOM_NAME_FIELD = "name"
    const val CREATED_AT_FIELD = "createdAt"
    const val PARTICIPANTS_FIELD = "participants"
    const val USER_ID_FIELD = "userId"
    const val USER_NAME_FIELD = "name"
    const val VOTE_FIELD = "vote"
    const val USER_AVATAR_FIELD = "avatar"

//    UI
    const val SPLASH_SCREEN_DELAY = 2500L
    const val AVATAR_ID_01 = "avatar_1"
    const val AVATAR_ID_02 = "avatar_2"
    const val AVATAR_ID_03 = "avatar_3"
    const val AVATAR_ID_04 = "avatar_4"
    const val AVATAR_ID_05 = "avatar_5"
    const val AVATAR_ID_06 = "avatar_6"

//    AI
    const val GENERATIVE_AI_MODEL = "gemini-2.0-flash"
    const val PROMPT_PREFIX = "Este é um app de planning poker."
    const val PROMPT_MAX_VOTE = "O maior voto foi %d pelo usuário %s. "
    const val PROMPT_MIN_VOTE = "O menor voto foi %d pelo usuário %s. "
    const val PROMPT_SUGGESTION =
        "Sugira um único texto, sem mais de uma opção, instigando os usuários a votarem novamente caso haja divergência de valores entre o voto máximo e mínimo "
    const val PROMPT_CONSENSUS =
        "para tentarem chegar em um consenso, afinal esse é o objetivo da planning poker."
    const val PROMPT_STYLE =
        "Utilize uma linguagem amigável e encorajadora, como se você fosse um facilitador."

//    ERRORS
    const val ERROR_NO_VOTES = "Nenhum voto disponível para gerar sugestão"
    const val ERROR_CANNOT_DETERMINE_MAX_VOTE = "Não foi possível determinar o maior voto"
    const val ERROR_CANNOT_DETERMINE_MIN_VOTE = "Não foi possível determinar o menor voto"
    const val ERROR_MAX_PARTICIPANT_NOT_FOUND = "Participante com maior voto não encontrado"
    const val ERROR_MIN_PARTICIPANT_NOT_FOUND = "Participante com menor voto não encontrado"
    const val ERROR_EMPTY_AI_RESPONSE = "Resposta AI vazia"
}
