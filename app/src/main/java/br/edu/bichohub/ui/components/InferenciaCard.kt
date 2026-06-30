package br.edu.bichohub.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun InferenciaCard(
    classificacao: String?,
    confiancaClassificacao: Int?,
    classificacaoColetor: String? = null,
    classificacaoConfirmada: Boolean = false,
    modifier: Modifier = Modifier
) {
    val temColetor = !classificacaoColetor.isNullOrBlank()
    val temModelo = !classificacao.isNullOrBlank()
    val corFundo = when {
        temColetor || classificacaoConfirmada -> Color(0xFFE8F5E9)
        temModelo -> Color(0xFFE8F5E9)
        else -> Color(0xFFFFF8E1)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = corFundo)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Classificação")

            when {
                temColetor -> {
                    Text(
                        text = "$classificacaoColetor",
                        color = Color(0xFF1565C0),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = "Classificação feita pelo coletor",
                        color = Color(0xFF558B2F),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                classificacaoConfirmada -> {
                    Text(
                        text = "$classificacao ${if (confiancaClassificacao != null) "(${confiancaClassificacao}%) " else ""}✅",
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = "Classificação confirmada pelo coletor",
                        color = Color(0xFF558B2F),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                temModelo -> {
                    Text(
                        text = "$classificacao ${if (confiancaClassificacao != null) "(${confiancaClassificacao}%)" else ""}",
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                else -> {
                    Text(
                        text = "🔍 Modulo de inferencia em desenvolvimento",
                        color = Color(0xFFF57F17),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
