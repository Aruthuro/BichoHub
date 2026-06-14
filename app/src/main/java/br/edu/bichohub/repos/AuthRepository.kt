package br.edu.bichohub.repos

import br.edu.bichohub.api.auth.TokenDataStoreManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Repositório para funções relacionadas a autenticação.
 * @param tokenManager o gerenciador de tokens de acesso.
 */
class AuthRepository @Inject constructor(private val tokenManager: TokenDataStoreManager) {
    val tokenFlow: Flow<String?> = tokenManager.tokenFlow

    suspend fun salvaToken(token: String) {
        tokenManager.setToken(token)
    }

    suspend fun limpaSessao() {
        tokenManager.deletaToken()
    }
}