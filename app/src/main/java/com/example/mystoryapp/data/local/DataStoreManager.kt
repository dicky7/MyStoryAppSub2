package com.example.mystoryapp.data.local


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreManager constructor(private val context: Context) {
    /**
     * get data user isLoggedIn
     */
    fun isLoggedIn(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[LOGGED_IN_KEY] ?: false
        }
    }

    /**
     * SetLogin
     *
     * * using suspend because edit is suspend function, so it's must running at coruntine or suspend function to
     */
    suspend fun setLoggedIn(isLogin: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[LOGGED_IN_KEY] = isLogin
        }
    }

    /**
     * get token login from user
     */
    fun getAuthToken(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[TOKEN_KEY] ?: ""
        }
    }

    /**
     * Save token login from user
     *
     * using suspend because edit is suspend function, so it's must running at coruntine or suspend function to
     */
    suspend fun saveAuthToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
        setLoggedIn(true)
    }


    companion object {
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore("datastore")

        //  Flag for use has login or not
        private val LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in_key")

        //  DataLogin
        private val TOKEN_KEY = stringPreferencesKey("token_key")
    }
}