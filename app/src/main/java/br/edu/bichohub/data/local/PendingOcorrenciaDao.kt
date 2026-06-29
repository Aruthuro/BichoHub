package br.edu.bichohub.data.local

import android.content.Context
import br.edu.bichohub.api.model.OcorrenciaRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Classe que cuida das operações com os arquivos temporários de ocorrências.
 * @param context contexto da aplicação.
 */
@Singleton
class PendingOcorrenciaDao @Inject constructor(@param:ApplicationContext private val context: Context) {
    private val json = Json { ignoreUnknownKeys = true }

    private val dir: File
        get() = File(context.filesDir, "pending_ocorrencias").also { it.mkdirs() }

    fun save(data: OcorrenciaRequest) {
        val file = File(dir, "${System.currentTimeMillis()}_${data.hashCode()}.json")
        file.writeText(json.encodeToString(data))
    }

    fun listFiles(): List<File> {
        return dir.listFiles { f -> f.extension == "json" }
            ?.sortedBy { it.name }
            ?: emptyList()
    }

    fun readFile(file: File): OcorrenciaRequest? {
        return try {
            json.decodeFromString<OcorrenciaRequest>(file.readText())
        } catch (_: Exception) {
            file.delete()
            null
        }
    }

    fun delete(file: File) {
        file.delete()
    }
}
