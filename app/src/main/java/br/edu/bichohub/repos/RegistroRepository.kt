package br.edu.bichohub.repos

import android.content.Context
import android.location.Location
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import br.edu.bichohub.api.model.OcorrenciaRequest
import br.edu.bichohub.data.local.PendingOcorrenciaDao
import br.edu.bichohub.data.remote.Resposta
import br.edu.bichohub.data.remote.UserRemoteDataSource
import br.edu.bichohub.data.worker.UploadWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Repositório para funções de ações do usuário
 * @param remoteDataSource a DataSource consumida por este repositório.
 * @param pendingDao objeto para cuidar das operações com os arquivos temporários de ocorrências.
 * @param context contexto da aplicação, garantido pelo Hilt.
 */
class RegistroRepository @Inject constructor(
    private val remoteDataSource: UserRemoteDataSource,
    private val pendingDao: PendingOcorrenciaDao,
    @param:ApplicationContext private val context: Context
) {
    private fun uploadWorkRequest() {
        val restricoes = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<UploadWorker>()
            .setConstraints(restricoes)
            .build()

        WorkManager
            .getInstance(context)
            .enqueueUniqueWork(
                "EnviaOcorrenciasPendentes",
                ExistingWorkPolicy.KEEP,
                request
            )
    }

    suspend fun registraOcorrencia(
        tipo: Int,
        gpsOrigem: Location,
        descricaoOrigem: String?,
        observacoes: String?,
        risco: String?,
        imagem: String?
    ): Resposta<Unit> {
        val req = OcorrenciaRequest(tipo, gpsOrigem, descricaoOrigem, observacoes, risco, imagem)

        return when (val resultado = remoteDataSource.registraOcorrencia(req)) {
            is Resposta.Sucesso<*> -> resultado
            is Resposta.Erro -> when (resultado.code) {
                401 -> Resposta.Erro(401, "Usuário não autorizado.")
                0 -> {
                    pendingDao.save(req)
                    uploadWorkRequest()
                    Resposta.Sucesso(201, Unit)
                }
                else -> resultado
            }
        }
    }

    suspend fun registraOcorrenciasPendentes(): Boolean {
        val arquivosPendentes = pendingDao.listFiles()
        if (arquivosPendentes.isEmpty()) return true

        for (arquivo in arquivosPendentes) {
            val requisicao = pendingDao.readFile(arquivo)

            if (requisicao != null) {
                val resultado = remoteDataSource.registraOcorrencia(requisicao)

                if (resultado is Resposta.Sucesso) {
                    pendingDao.delete(arquivo)
                } else {
                    return false
                }
            }
        }
        return true
    }
}