package br.com.faleingles.presentation.conversation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.faleingles.domain.model.ConversationMessage
import br.com.faleingles.domain.model.GrammarCorrection
import br.com.faleingles.domain.model.CorrectionType
import br.com.faleingles.domain.model.MessageRole
import br.com.faleingles.presentation.theme.FaleInglesTheme

private val previewMessages = listOf(
    ConversationMessage(
        id = "1",
        role = MessageRole.ASSISTANT,
        content = "Hi! I'm your AI tutor. Let's practice a bit. How are you feeling today?",
        correction = null,
        timestampMillis = 0,
    ),
    ConversationMessage(
        id = "2",
        role = MessageRole.USER,
        content = "I am feel good, thank you!",
        correction = null,
        timestampMillis = 0,
    ),
    ConversationMessage(
        id = "3",
        role = MessageRole.ASSISTANT,
        content = "Great to hear that! I'm glad you're doing well.",
        correction = GrammarCorrection(
            original = "I am feel good",
            corrected = "I am feeling good",
            explanation = "Use the gerund (-ing) after 'to be' to form the present continuous.",
            correctionType = CorrectionType.GRAMMAR,
        ),
        timestampMillis = 0,
    ),
    ConversationMessage(
        id = "4",
        role = MessageRole.USER,
        content = "I am feeling good! What we can talk about?",
        correction = null,
        timestampMillis = 0,
    ),
    ConversationMessage(
        id = "5",
        role = MessageRole.ASSISTANT,
        content = "We can talk about anything! How about telling me about your day?",
        correction = GrammarCorrection(
            original = "What we can talk about?",
            corrected = "What can we talk about?",
            explanation = "In questions, the auxiliary verb comes before the subject.",
            correctionType = CorrectionType.GRAMMAR,
        ),
        timestampMillis = 0,
    ),
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Conversation — Light", showBackground = true, showSystemUi = true)
@Composable
private fun ConversationScreenPreview() {
    FaleInglesTheme(darkTheme = false) {
        ConversationContent(messages = previewMessages, isLoading = false, inputText = "My day was very good!")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Conversation — Dark", showBackground = true, showSystemUi = true)
@Composable
private fun ConversationScreenDarkPreview() {
    FaleInglesTheme(darkTheme = true) {
        ConversationContent(messages = previewMessages, isLoading = true, inputText = "")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ConversationContent(
    messages: List<ConversationMessage>,
    isLoading: Boolean,
    inputText: String,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Conversar em inglês") },
                navigationIcon = { IconButton(onClick = {}) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } },
            )
        },
        bottomBar = {
            Surface(tonalElevation = 3.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = {},
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Escreva em inglês…") },
                        singleLine = true,
                    )
                    Spacer(Modifier.width(8.dp))
                    IconButton(onClick = {}, enabled = inputText.isNotBlank() && !isLoading) {
                        Icon(Icons.AutoMirrored.Filled.Send, null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(messages) { message ->
                val isUser = message.role == MessageRole.USER
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start) {
                    Column(horizontalAlignment = if (isUser) Alignment.End else Alignment.Start) {
                        Surface(
                            shape = MaterialTheme.shapes.large,
                            color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.widthIn(max = 280.dp),
                        ) {
                            Text(
                                message.content,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            )
                        }
                        message.correction?.let { correction ->
                            Spacer(Modifier.height(4.dp))
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.errorContainer,
                                modifier = Modifier.widthIn(max = 280.dp),
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Correção: ${correction.corrected}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onErrorContainer)
                                    Text(correction.explanation, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f))
                                }
                            }
                        }
                    }
                }
            }
            if (isLoading) {
                item {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
            }
        }
    }
}
