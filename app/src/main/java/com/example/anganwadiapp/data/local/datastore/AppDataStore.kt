package com.example.anganwadiapp.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "anganwadi_preferences"
)

@Singleton
class AppDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_ROLE = stringPreferencesKey("user_role")
    }

    val isFirstLaunch: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[Keys.IS_FIRST_LAUNCH] ?: true }

    suspend fun setFirstLaunchComplete() {
        context.dataStore.edit { preferences ->
            preferences[Keys.IS_FIRST_LAUNCH] = false
        }
    }

    fun getUserName(): Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[Keys.USER_NAME] }

    suspend fun setUserName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.USER_NAME] = name
        }
    }

    fun getUserRole(): Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[Keys.USER_ROLE] }

    suspend fun setUserRole(role: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.USER_ROLE] = role
        }
    }
}
