package br.com.faleingles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.faleingles.presentation.navigation.FaleInglesNavHost
import br.com.faleingles.presentation.navigation.Route
import br.com.faleingles.presentation.navigation.StartDestination
import br.com.faleingles.presentation.navigation.StartDestinationViewModel
import br.com.faleingles.presentation.theme.FaleInglesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val startVm: StartDestinationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FaleInglesTheme {
                val destination by startVm.startDestination.collectAsStateWithLifecycle()

                when (destination) {
                    StartDestination.Loading -> {
                        Surface(modifier = Modifier.fillMaxSize()) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    StartDestination.Onboarding -> {
                        FaleInglesNavHost(startDestination = Route.Welcome)
                    }
                    StartDestination.Auth -> {
                        FaleInglesNavHost(startDestination = Route.Auth)
                    }
                    StartDestination.Home -> {
                        FaleInglesNavHost(startDestination = Route.Home)
                    }
                }
            }
        }
    }
}
