package br.com.faleingles.presentation.onboarding.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private data class ManifestoPage(val title: String, val body: String)

private val pages = listOf(
    ManifestoPage(
        title = "Você não aprendeu português decorando palavras.",
        body = "Aprendeu ouvindo frases inteiras, em situações reais, repetidas até virarem som natural.",
    ),
    ManifestoPage(
        title = "Os outros apps ensinam do jeito errado.",
        body = "Apple = maçã. Cat = gato. Palavras sem contexto, sem som, sem uso real. Não é assim que o cérebro aprende.",
    ),
    ManifestoPage(
        title = "FaleInglês te ensina do jeito certo.",
        body = "Frases reais. Sons naturais. Situações do dia a dia. Gramática como consequência, não como pré-requisito.",
    ),
)

@Composable
fun ManifestoScreen(onContinue: () -> Unit) {
    var currentPage by remember { mutableIntStateOf(0) }
    val page = pages[currentPage]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Spacer(Modifier.height(48.dp))

        AnimatedContent(targetState = currentPage, label = "manifesto") {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = pages[it].title,
                    style = MaterialTheme.typography.displayMedium,
                    textAlign = TextAlign.Center,
                    color = androidx.compose.ui.graphics.Color(0xFF111111),
                )
                Spacer(Modifier.height(20.dp))
                Text(
                    text = pages[it].body,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = androidx.compose.ui.graphics.Color(0xFF444444),
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                pages.indices.forEach { index ->
                    val isSelected = index == currentPage
                    Surface(
                        modifier = Modifier.size(if (isSelected) 24.dp else 8.dp, 8.dp),
                        shape = MaterialTheme.shapes.extraLarge,
                        color = if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        content = {}
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    if (currentPage < pages.lastIndex) currentPage++ else onContinue()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium,
            ) {
                Text(
                    if (currentPage < pages.lastIndex) "Próximo" else "Entendi, quero começar",
                    style = MaterialTheme.typography.labelLarge,
                )
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}
