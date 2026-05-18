package br.edu.bichohub.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    //primary = VerdeClaro1,
    //secondary = VerdeClaro2,
    //tertiary = CinzaClaro,
    background = Branco,
    onBackground = CinzaEscuro,
    surface = VerdeEscuro2,
    onSurface = CinzaClaro
)
private val DarkColorScheme = darkColorScheme(
    //primary = VerdeClaro1,
    //secondary = VerdeEscuro2,
    //tertiary = Cinza,
    background = CinzaEscuro,
    onBackground = CinzaClaro,
    surface = VerdeClaro1,
    onSurface = Branco
)

/**
 * Inicia o tema do aplicativo.
 * @param darkTheme opcinonal, se o sistema está utilziando o tema escuro ou não.
 * @param content o conteúdo a ser inserido na tela.
 */
@Composable
fun BichoHubTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}