package br.com.faleingles.presentation.onboarding.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.com.faleingles.presentation.theme.FaleInglesTheme

@Preview(name = "Welcome — Light", showBackground = true, showSystemUi = true)
@Composable
private fun WelcomeScreenPreview() {
    FaleInglesTheme(darkTheme = false) { WelcomeScreen(onContinue = {}) }
}

@Preview(name = "Welcome — Dark", showBackground = true, showSystemUi = true)
@Composable
private fun WelcomeScreenDarkPreview() {
    FaleInglesTheme(darkTheme = true) { WelcomeScreen(onContinue = {}) }
}

@Preview(name = "Manifesto — Page 1", showBackground = true, showSystemUi = true)
@Composable
private fun ManifestoScreenPreview() {
    FaleInglesTheme { ManifestoScreen(onContinue = {}) }
}

@Preview(name = "Diagnostic", showBackground = true, showSystemUi = true)
@Composable
private fun DiagnosticScreenPreview() {
    FaleInglesTheme { DiagnosticScreen(onContinue = {}) }
}

@Preview(name = "Goal", showBackground = true, showSystemUi = true)
@Composable
private fun GoalScreenPreview() {
    FaleInglesTheme { GoalScreen(onContinue = {}) }
}
