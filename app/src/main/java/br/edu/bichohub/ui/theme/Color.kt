package br.edu.bichohub.ui.theme

import androidx.compose.ui.graphics.Color

val VerdeClaro1 = Color(0xFF548E2E)
val VerdeClaro2 = Color(0xFFc7e6b3)
val CinzaClaro = Color(0xFFD1D1D1)
val Branco = Color(0xFFFFFBFE)

val VerdeEscuro1 = Color(0xFF0A0F09)
val VerdeEscuro2 = Color(0xFF3A6B26)
val VerdeEscuro3 = Color(0xFF49593E)
val Cinza = Color(0xFF7C7C7C)
val CinzaEscuro = Color(0xFF111111)
val VermelhoErro = Color(0xFFFF2a2a)
val VermelhoErroDark = Color(0xFF830B0B)

fun corEstado(estado: Int): Color = when (estado) {
    1 -> Color(0xFF1565C0)
    2 -> Color(0xFF2E7D32)
    3 -> Color(0xFFB71C1C)
    4 -> Color(0xFFF57F17)
    else -> Cinza
}