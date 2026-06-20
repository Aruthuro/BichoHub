package br.edu.bichohub.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitObject {
    // Para emulador Android local:      http://10.0.2.2:6969/api/
    // Para ngrok (expor na internet):   https://teu-subdomain.ngrok-free.app/api/
    private const val URL = "http://10.0.2.2:6969/api/"

    private val authInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
        val token = TokenManager.getToken()
        if (token != null) {
            request.addHeader("Authorization", "Bearer $token")
        }
        chain.proceed(request.build())
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: BichoHubService = retrofit.create(BichoHubService::class.java)
}
