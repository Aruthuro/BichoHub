package br.edu.bichohub.api

import android.content.Context
import br.edu.bichohub.api.auth.AuthInterceptor
import br.edu.bichohub.api.auth.TokenDataStoreManager
import br.edu.bichohub.api.service.AuthService
import br.edu.bichohub.api.service.BichoHubService
import br.edu.bichohub.api.service.UserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
    private const val URL = "http://10.0.0.100:6969/api/"

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
    fun provideTokenDataStoreManager(@ApplicationContext context: Context): TokenDataStoreManager {
        return TokenDataStoreManager(context)
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

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }
}