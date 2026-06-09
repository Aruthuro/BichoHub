package br.edu.bichohub.data

import br.edu.bichohub.api.model.ErrorResponse
import com.google.gson.Gson
import retrofit2.HttpException
import retrofit2.Response
import java.net.UnknownHostException
import java.net.SocketTimeoutException

val gson = Gson()

suspend fun <T> chamaApi(chamada: suspend () -> Response<T>): Resposta<T> {
    return try {
        val resposta = chamada()
        val code = resposta.code()
        val corpo = resposta.body()

        if (resposta.isSuccessful && corpo != null) {
            Resposta.Sucesso(code, corpo)
        } else {
            val erro = resposta.errorBody()?.string()
            val msg = try {
                val errorObj = gson.fromJson(erro, ErrorResponse::class.java)
                errorObj.msg
            } catch (erro: Exception) {
                erro.toString()
            }
            Resposta.Erro(code, msg)
        }
    } catch (e: HttpException) {
        val erro = e.response()?.errorBody()?.string()
        val msg = try {
            val errorObj = gson.fromJson(erro, ErrorResponse::class.java)
            errorObj.msg
        } catch (erro: Exception) {
            erro.toString()
        }
        Resposta.Erro(e.code(), msg)
    } catch (_: UnknownHostException) {
        Resposta.Erro(0, "Sem conexão com a internet")
    } catch (_: SocketTimeoutException) {
        Resposta.Erro(-1, "Tempo de conexão esgotado")
    } catch (erro: Exception) {
        Resposta.ErroException(-2, erro)
    }
}