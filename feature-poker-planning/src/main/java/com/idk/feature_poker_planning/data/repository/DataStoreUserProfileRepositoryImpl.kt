package com.idk.feature_poker_planning.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.idk.feature_poker_planning.domain.model.UserProfile
import com.idk.feature_poker_planning.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreUserProfileRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserProfileRepository {

    companion object {
        private val KEY_USER_ID = stringPreferencesKey("user_id")
        private val KEY_USER_NAME = stringPreferencesKey("user_name")
        private val KEY_USER_AVATAR = stringPreferencesKey("user_avatar")
    }

    override suspend fun getProfile(): UserProfile {
        val prefs = dataStore.data.first()
        val userId = prefs[KEY_USER_ID] ?: UUID.randomUUID().toString()
        val userName = prefs[KEY_USER_NAME] ?: ""
        val avatar = prefs[KEY_USER_AVATAR] ?: ""
        if (prefs[KEY_USER_ID] == null) {
            dataStore.edit { s ->
                s[KEY_USER_ID] = userId
                s[KEY_USER_NAME] = userName
                s[KEY_USER_AVATAR] = avatar
            }
        }
        return UserProfile(userId, userName, avatar)
    }

    override suspend fun saveProfile(profile: UserProfile) {
        dataStore.edit { s ->
            s[KEY_USER_ID] = profile.userId
            s[KEY_USER_NAME] = profile.userName
            s[KEY_USER_AVATAR] = profile.avatar
        }
    }

    override suspend fun hasProfile(): Boolean {
        val prefs = dataStore.data.first()
        return prefs.contains(KEY_USER_ID)
    }
}
