package br.com.faleingles.presentation.onboarding.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.faleingles.presentation.onboarding.viewmodel.OnboardingViewModel

private data class DiagnosticQuestion(val question: String, val options: List<String>)

private val questions = listOf(
    DiagnosticQuestion(
        "Qual dessas frases você entende sem tradução?",
        listOf(
            "Nenhuma delas",
            "\"Hello, how are you?\"",
            "\"I'm going to the store later\"",
            "\"Why is everybody so upset about this?\"",
        ),
    ),
    DiagnosticQuestion(
        "Você consegue responder se alguém te perguntar \"Where are you from?\"?",
        listOf(
            "Não entendo a pergunta",
            "Entendo, mas não sei responder",
            "Consigo responder com dificuldade",
            "Respondo naturalmente",
        ),
    ),
    DiagnosticQuestion(
        "Qual é o seu objetivo com inglês?",
        listOf(
            "Entender filmes e séries",
            "Trabalhar com empresa estrangeira",
            "Viajar ao exterior",
            "Me comunicar no dia a dia",
        ),
    ),
)

@Composable
fun DiagnosticScreen(
    onContinue: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var currentQuestion by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }

    val question = questions[currentQuestion]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(48.dp))

        Text(
            text = "Pergunta ${currentQuestion + 1} de ${questions.size}",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(16.dp))

        LinearProgressIndicator(
            progress = { (currentQuestion + 1f) / questions.size },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(32.dp))

        Text(
            text = question.question,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(32.dp))

        question.options.forEachIndexed { index, option ->
            val isSelected = selectedOption == index
            OutlinedButton(
                onClick = {
                    selectedOption = index
                    viewModel.selectLevel(currentQuestion * 4 + index)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                border = BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outline,
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isSelected)
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                    else androidx.compose.ui.graphics.Color.White,
                ),
            ) {
                Text(
                    text = option,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSelected) MaterialTheme.colorScheme.primary
                    else androidx.compose.ui.graphics.Color(0xFF111111),
                    modifier = Modifier.padding(vertical = 4.dp),
                )
            }
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = {
                if (currentQuestion < questions.lastIndex) {
                    currentQuestion++
                    selectedOption = null
                } else {
                    onContinue()
                }
            },
            enabled = selectedOption != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = MaterialTheme.shapes.medium,
        ) {
            Text(
                if (currentQuestion < questions.lastIndex) "Próxima" else "Ver meu nível",
                style = MaterialTheme.typography.labelLarge,
            )
        }
        Spacer(Modifier.height(32.dp))
    }
}
