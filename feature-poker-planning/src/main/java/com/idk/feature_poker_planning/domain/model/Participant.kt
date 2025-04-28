package com.idk.feature_poker_planning.domain.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Participant(
    var userId: String = "",
    var name: String = "",
    var avatar: String = "",
    var vote: Int? = null
)
