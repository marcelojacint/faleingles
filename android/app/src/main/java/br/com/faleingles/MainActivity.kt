package br.com.faleingles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import br.com.faleingles.presentation.navigation.FaleInglesNavHost
import br.com.faleingles.presentation.theme.FaleInglesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FaleInglesTheme {
                FaleInglesNavHost()
            }
        }
    }
}
