package br.edu.bichohub.repos

import android.content.Context
import android.net.Uri
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
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
        gpsWkt: String,
        dataCaptura: String,
        descricaoOrigem: String?,
        observacoes: String?,
        risco: String?
    ): Resposta<Int> {
        val req = OcorrenciaRequest(tipo, gpsWkt, dataCaptura, descricaoOrigem, observacoes, risco, null)

        return when (val resultado = remoteDataSource.registraOcorrencia(req)) {
            is Resposta.Sucesso -> Resposta.Sucesso(resultado.code, resultado.corpo.id)
            is Resposta.Erro -> when (resultado.code) {
                401 -> Resposta.Erro(401, "Usuário não autorizado.")
                0 -> {
                    pendingDao.save(req)
                    uploadWorkRequest()
                    Resposta.Erro(0, "Sem conexão")
                }
                else -> resultado
            }
        }
    }

    suspend fun uploadImagem(id: Int, imagemUri: Uri): Resposta<Unit> {
        return try {
            val file = uriToFile(imagemUri) ?: return Resposta.Erro(-1, "Não foi possível ler a imagem")
            val mediaType = context.contentResolver.getType(imagemUri) ?: "image/jpeg"
            val requestBody = file.readBytes().toRequestBody(mediaType.toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("imagem", file.name, requestBody)
            remoteDataSource.uploadImagem(id, part)
        } catch (e: Exception) {
            Resposta.Erro(-2, e.message ?: "Erro ao fazer upload da imagem")
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

    private fun uriToFile(uri: Uri): File? {
        return try {
            // Check if it's a FileProvider URI or a content URI
            val caminho = uri.path ?: return null
            // For content://br.edu.bichohub.provider/foto/IMG_xxx.jpg, extract filename
            val nome = caminho.substringAfterLast("/")
            // Copy content to a temp file
            val tempFile = File(context.cacheDir, "upload_$nome")
            context.contentResolver.openInputStream(uri)?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            if (tempFile.exists()) tempFile else null
        } catch (e: Exception) {
            null
        }
    }
}
