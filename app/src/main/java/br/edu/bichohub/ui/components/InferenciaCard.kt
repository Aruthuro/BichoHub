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
    modifier: Modifier = Modifier
) {
    if (classificacao.isNullOrBlank()) return

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8F5E9)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Classificação do modelo")
            Row(modifier = Modifier.padding(top = 4.dp)) {
                Text(
                    text = classificacao,
                    color = Color(0xFF2E7D32)
                )
                if (confiancaClassificacao != null) {
                    Text(
                        text = " (${confiancaClassificacao}%)",
                        color = Color(0xFF558B2F),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}
