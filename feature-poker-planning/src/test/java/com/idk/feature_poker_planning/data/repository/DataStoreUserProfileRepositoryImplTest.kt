package com.idk.feature_poker_planning.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import com.idk.feature_poker_planning.domain.model.UserProfile
import com.idk.feature_poker_planning.utils.TestDataProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DataStoreUserProfileRepositoryImplTest {
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var repository: DataStoreUserProfileRepositoryImpl

    private val keyUserId = stringPreferencesKey("user_id")
    private val keyUserName = stringPreferencesKey("user_name")
    private val keyUserAvatar = stringPreferencesKey("user_avatar")

    @Before
    fun setup() {
        dataStore = mockk(relaxed = true)
        repository = DataStoreUserProfileRepositoryImpl(dataStore)
    }

    @Test
    fun getProfile_prefsEmpty_generatesUuidAndSavesAndReturnsProfile() = runTest {
        val initialPrefs = preferencesOf()
        coEvery { dataStore.data } returns flowOf(initialPrefs)
        val updateSlot = slot<suspend (Preferences) -> Preferences>()
        coEvery { dataStore.updateData(capture(updateSlot)) } coAnswers {
            updateSlot.captured.invoke(initialPrefs)
        }

        val profile = repository.getProfile()

        assertTrue(profile.userId.isNotBlank())
        assertEquals("", profile.userName)
        assertEquals("", profile.avatar)
        coVerify(exactly = 1) { dataStore.updateData(any()) }
    }

    @Test
    fun getProfile_prefsPopulated_returnsWithoutSaving() = runTest {
        val existing = UserProfile(
            TestDataProvider.TEST_USER_ID,
            TestDataProvider.VALID_NAME,
            TestDataProvider.VALID_AVATAR
        )
        val prefs = preferencesOf(
            keyUserId to existing.userId,
            keyUserName to existing.userName,
            keyUserAvatar to existing.avatar
        )
        coEvery { dataStore.data } returns flowOf(prefs)

        val profile = repository.getProfile()

        assertEquals(existing, profile)
        coVerify(exactly = 0) { dataStore.updateData(any()) }
    }

    @Test
    fun saveProfile_writesAllFields() = runTest {
        val profile = UserProfile(
            TestDataProvider.TEST_USER_ID,
            TestDataProvider.VALID_NAME,
            TestDataProvider.VALID_AVATAR
        )
        val updateSlot = slot<suspend (Preferences) -> Preferences>()
        coEvery { dataStore.updateData(capture(updateSlot)) } coAnswers {
            val resultPrefs = updateSlot.captured.invoke(preferencesOf())
            resultPrefs
        }

        repository.saveProfile(profile)

        coVerify(exactly = 1) { dataStore.updateData(any()) }
    }

    @Test
    fun hasProfile_returnsTrueIfKeyExists() = runTest {
        val prefs = preferencesOf(keyUserId to TestDataProvider.SOME_USER_ID)
        coEvery { dataStore.data } returns flowOf(prefs)

        val result = repository.hasProfile()

        assertTrue(result)
    }

    @Test
    fun hasProfile_returnsFalseIfKeyMissing() = runTest {
        coEvery { dataStore.data } returns flowOf(preferencesOf())

        val result = repository.hasProfile()

        assertFalse(result)
    }
}
