package br.edu.bichohub.api.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Gerenciador da DataStore que armazena os tokens de acesso.
 * @param context o contexo do aplicativo.
 */
class TokenDataStoreManager @Inject constructor(@param:ApplicationContext private val context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")

    internal companion object {
        val JWT_TOKEN = stringPreferencesKey("CHAVE")
    }

    val tokenFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[JWT_TOKEN]
    }

    suspend fun setToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[JWT_TOKEN] = token
        }
    }

    suspend fun deletaToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(JWT_TOKEN)
        }
    }
}