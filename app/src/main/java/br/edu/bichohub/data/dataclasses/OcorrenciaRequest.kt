package br.edu.bichohub.data.dataclasses

import android.location.Location
import com.google.gson.annotations.SerializedName

data class OcorrenciaRequest(
    @SerializedName("usuario_id")
    val id: Int,
    @SerializedName("origem_gps")
    val origemGPS: Location,
    val descricao: String?,
    val imagem: String?,
    val tipo: Int
)
