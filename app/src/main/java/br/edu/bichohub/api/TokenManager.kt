package br.edu.bichohub.api

import android.content.Context
import android.content.SharedPreferences

object TokenManager {
    private const val PREF_NAME = "bichohub_prefs"
    private const val KEY_TOKEN = "jwt_token"
    private const val KEY_NOME = "usuario_nome"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun salvarToken(token: String, nome: String) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_NOME, nome)
            .apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun getNome(): String? = prefs.getString(KEY_NOME, null)

    fun isLogado(): Boolean = getToken() != null

    fun logout() {
        prefs.edit()
            .remove(KEY_TOKEN)
            .remove(KEY_NOME)
            .apply()
    }
}
