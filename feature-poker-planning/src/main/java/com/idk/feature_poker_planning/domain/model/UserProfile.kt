package com.idk.feature_poker_planning.domain.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class UserProfile(
    var userId: String,
    var userName: String,
    var avatar: String
)
