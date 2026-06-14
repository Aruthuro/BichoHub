package br.edu.bichohub.api

import br.edu.bichohub.api.auth.AuthInterceptor
import br.edu.bichohub.api.auth.TokenDataStoreManager
import br.edu.bichohub.api.service.BichoHubService
import br.edu.bichohub.api.service.UserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Objeto único que providencia uma instância do Retrofit para os serviços.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val URL = "https://127.0.0.1:6969/api/"

    @Provides
    @Singleton
    fun retrofitClient(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenDataStoreManager: TokenDataStoreManager): AuthInterceptor {
        return AuthInterceptor(tokenDataStoreManager)
    }

    @Provides
    @Singleton
    fun okClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideUserService(retrofit: Retrofit): UserService {
        return retrofit.create(UserService::class.java)
    }

    @Provides
    @Singleton
    fun provideBichoHubService(retrofit: Retrofit): BichoHubService {
        return retrofit.create(BichoHubService::class.java)
    }
}