package com.example.newstoryapp.User

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class PreferensiUser private constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun saveSession(user: ModelUser) {
        dataStore.edit { preferences ->
            preferences[EMAIL_KEY] = user.email
            preferences[TOKEN_KEY] = user.token
            preferences[IS_LOGIN_KEY] = true
        }
    }

    fun mendapatkanSession(): Flow<ModelUser> {
        return dataStore.data.map { preferences ->
            ModelUser(
                userId = preferences[USERID_KEY] ?: "",
                name = preferences[NAME_KEY] ?: "",
                email = preferences[EMAIL_KEY] ?: "",
                token = preferences[TOKEN_KEY] ?: "",
                isLogin = preferences[IS_LOGIN_KEY] ?: false
            )
        }
    }

    suspend fun keluar() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: PreferensiUser? = null

        private val USERID_KEY = stringPreferencesKey("userid")
        private val NAME_KEY = stringPreferencesKey("name")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")

        fun mendapatkanInstance(dataStore: DataStore<Preferences>): PreferensiUser {
            return INSTANCE ?: synchronized(this) {
                val instance = PreferensiUser(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}
