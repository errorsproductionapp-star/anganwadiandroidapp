package com.example.anganwadiapp.core.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val PARENT_IS_LOGGED_IN = booleanPreferencesKey("parent_is_logged_in")
        val PARENT_CHILD_ID = stringPreferencesKey("parent_child_id")
        val PARENT_CENTER_ID = stringPreferencesKey("parent_center_id")
        val PARENT_CHILD_NAME = stringPreferencesKey("parent_child_name")
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[Keys.IS_LOGGED_IN] ?: false
        }

    suspend fun setLoggedIn(isLoggedIn: Boolean, email: String? = null) {
        context.dataStore.edit { preferences ->
            preferences[Keys.IS_LOGGED_IN] = isLoggedIn
            if (email != null) {
                preferences[Keys.USER_EMAIL] = email
            } else {
                preferences.remove(Keys.USER_EMAIL)
            }
        }
    }

    val isParentLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[Keys.PARENT_IS_LOGGED_IN] ?: false
        }

    suspend fun setParentLoggedIn(isLoggedIn: Boolean, childId: String = "", centerId: String = "", childName: String = "") {
        context.dataStore.edit { preferences ->
            preferences[Keys.PARENT_IS_LOGGED_IN] = isLoggedIn
            if (isLoggedIn) {
                preferences[Keys.PARENT_CHILD_ID] = childId
                preferences[Keys.PARENT_CENTER_ID] = centerId
                preferences[Keys.PARENT_CHILD_NAME] = childName
            } else {
                preferences.remove(Keys.PARENT_CHILD_ID)
                preferences.remove(Keys.PARENT_CENTER_ID)
                preferences.remove(Keys.PARENT_CHILD_NAME)
            }
        }
    }

    suspend fun getParentData(): ParentData {
        val preferences = context.dataStore.data.first()
        val childId = preferences[Keys.PARENT_CHILD_ID] ?: ""
        val centerId = preferences[Keys.PARENT_CENTER_ID] ?: ""
        val childName = preferences[Keys.PARENT_CHILD_NAME] ?: ""
        return ParentData(childId, centerId, childName)
    }

    suspend fun getParentChildId(): String {
        val preferences = context.dataStore.data.first()
        return preferences[Keys.PARENT_CHILD_ID] ?: ""
    }

    suspend fun getParentCenterId(): String {
        val preferences = context.dataStore.data.first()
        return preferences[Keys.PARENT_CENTER_ID] ?: ""
    }
    suspend fun logout() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

data class ParentData(
    val childId: String,
    val centerId: String,
    val childName: String
)



