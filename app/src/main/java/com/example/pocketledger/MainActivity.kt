package com.example.pocketledger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.pocketledger.ui.theme.PocketLedgerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PocketLedgerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: MainViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                    val isOnboardingCompleted by viewModel.isOnboardingCompleted.collectAsState(initial = true) // Default true to avoid flash if loading

                    if (!isOnboardingCompleted) {
                        com.example.pocketledger.ui.onboarding.OnboardingScreen(
                            onFinish = viewModel::completeOnboarding
                        )
                    } else {
                        com.example.pocketledger.ui.navigation.PocketLedgerNavHost()
                    }
                }
            }
        }
    }
}
