package br.edu.bichohub.api

import android.content.Context
import android.content.SharedPreferences

object TokenManager {
    private const val PREF_NAME = "bichohub_prefs"
    private const val KEY_TOKEN = "jwt_token"
    private const val KEY_NOME = "usuario_nome"
    private const val KEY_ADMIN = "eh_admin"
    private const val KEY_COLETOR = "eh_coletor"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun salvarToken(token: String, nome: String) {
        salvarToken(token, nome, false, false)
    }

    fun salvarToken(token: String, nome: String, ehAdmin: Boolean, ehColetor: Boolean) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_NOME, nome)
            .putBoolean(KEY_ADMIN, ehAdmin)
            .putBoolean(KEY_COLETOR, ehColetor)
            .apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun getNome(): String? = prefs.getString(KEY_NOME, null)

    fun isAdmin(): Boolean = prefs.getBoolean(KEY_ADMIN, false)

    fun isColetor(): Boolean = prefs.getBoolean(KEY_COLETOR, false)

    fun isLogado(): Boolean = getToken() != null

    fun logout() {
        prefs.edit()
            .remove(KEY_TOKEN)
            .remove(KEY_NOME)
            .remove(KEY_ADMIN)
            .remove(KEY_COLETOR)
            .apply()
    }
}
