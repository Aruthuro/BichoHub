package br.edu.bichohub

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Classe chamada ao inicializar o aplicativo.
 * Roda antes de qualquer coisa, logo, é usada para disparar serviços essenciais.
 */
@HiltAndroidApp
class App: Application(), Configuration.Provider {
    @Inject lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}