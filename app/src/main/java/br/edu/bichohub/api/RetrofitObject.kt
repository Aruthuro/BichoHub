package br.edu.bichohub.api
import br.edu.bichohub.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitObject {
    // Para emulador Android local:      http://10.0.2.2:6969/api/
    // Produção (Render):                https://bichohub-server.onrender.com/api/
    // private const val URL = "https://bichohub-server.onrender.com/api/"
    private const val URL = BuildConfig.BASE_URL

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
