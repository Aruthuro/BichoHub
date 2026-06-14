package br.edu.bichohub.api.auth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenDataStoreManager: TokenDataStoreManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val token = runBlocking(Dispatchers.IO) { tokenDataStoreManager.tokenFlow.first() }
        if (token.isNullOrBlank()) {
            return chain.proceed(originalRequest)
        }

        val req = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        return chain.proceed(req)
    }
}