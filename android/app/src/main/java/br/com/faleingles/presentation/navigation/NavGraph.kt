package br.com.faleingles.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import br.com.faleingles.presentation.home.screen.HomeScreen
import br.com.faleingles.presentation.lesson.screen.LessonScreen
import br.com.faleingles.presentation.onboarding.screen.DiagnosticScreen
import br.com.faleingles.presentation.onboarding.screen.GoalScreen
import br.com.faleingles.presentation.onboarding.screen.ManifestoScreen
import br.com.faleingles.presentation.onboarding.screen.WelcomeScreen
import br.com.faleingles.presentation.practice.screen.PracticeScreen
import br.com.faleingles.presentation.audio.screen.AudioScreen
import br.com.faleingles.presentation.auth.screen.AuthScreen
import br.com.faleingles.presentation.conversation.screen.ConversationScreen
import br.com.faleingles.presentation.review.screen.ReviewScreen
import br.com.faleingles.presentation.progress.screen.ProgressScreen
import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable data object Welcome : Route
    @Serializable data object Manifesto : Route
    @Serializable data object Diagnostic : Route
    @Serializable data object Goal : Route
    @Serializable data object Auth : Route
    @Serializable data object Home : Route
    @Serializable data class Lesson(val lessonId: String) : Route
    @Serializable data class Practice(val lessonId: String) : Route
    @Serializable data class Audio(val lessonId: String) : Route
    @Serializable data object Conversation : Route
    @Serializable data object Review : Route
    @Serializable data object Progress : Route
}

@Composable
fun FaleInglesNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: Route = Route.Welcome
) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable<Route.Welcome> {
            WelcomeScreen(
                onContinue = { navController.navigate(Route.Manifesto) }
            )
        }

        composable<Route.Manifesto> {
            ManifestoScreen(
                onContinue = { navController.navigate(Route.Diagnostic) }
            )
        }

        composable<Route.Diagnostic> {
            DiagnosticScreen(
                onContinue = { navController.navigate(Route.Goal) }
            )
        }

        composable<Route.Goal> {
            GoalScreen(
                onContinue = { navController.navigate(Route.Auth) }
            )
        }

        composable<Route.Auth> {
            AuthScreen(
                onAuthSuccess = {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Welcome) { inclusive = true }
                    }
                }
            )
        }

        composable<Route.Home> {
            HomeScreen(
                onLessonClick = { lessonId -> navController.navigate(Route.Lesson(lessonId)) },
                onConversationClick = { navController.navigate(Route.Conversation) },
                onReviewClick = { navController.navigate(Route.Review) },
                onProgressClick = { navController.navigate(Route.Progress) },
            )
        }

        composable<Route.Lesson> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.Lesson>()
            LessonScreen(
                lessonId = route.lessonId,
                onPracticeClick = { navController.navigate(Route.Practice(route.lessonId)) },
                onAudioClick = { navController.navigate(Route.Audio(route.lessonId)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable<Route.Practice> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.Practice>()
            PracticeScreen(
                lessonId = route.lessonId,
                onFinish = { navController.popBackStack(Route.Home, inclusive = false) }
            )
        }

        composable<Route.Audio> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.Audio>()
            AudioScreen(
                lessonId = route.lessonId,
                onBack = { navController.popBackStack() }
            )
        }

        composable<Route.Conversation> {
            ConversationScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<Route.Review> {
            ReviewScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<Route.Progress> {
            ProgressScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
