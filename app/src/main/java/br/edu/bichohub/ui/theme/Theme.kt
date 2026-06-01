package br.edu.bichohub.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = VerdeEscuro2,
    onPrimary = Branco,
    secondaryContainer = VerdeClaro2,
    background = Branco,
    onBackground = CinzaEscuro,
    surface = VerdeClaro1,
    onSurface = CinzaEscuro,
    surfaceVariant = Branco,
    error = VermelhoErro,
    onError = Branco
)
private val DarkColorScheme = darkColorScheme(
    //primary = VerdeClaro1,
    //secondary = VerdeEscuro2,
    //tertiary = Cinza,
    background = CinzaEscuro,
    onBackground = CinzaClaro,
    surface = VerdeClaro1,
    onSurface = Branco,
    error = VermelhoErroDark,
    onError = Branco
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