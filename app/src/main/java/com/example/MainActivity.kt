package com.example

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.HealthCheckScreen
import com.example.ui.HealthCheckViewModel
import com.example.ui.theme.SeniorCareTheme

class MainActivity : ComponentActivity() {

    private val viewModel: HealthCheckViewModel by viewModels {
        HealthCheckViewModel.Factory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SeniorCareTheme {
                LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
                    viewModel.refreshDateIfNeeded()
                }

                val context = LocalContext.current
                DisposableEffect(context) {
                    val dateChangeReceiver = object : BroadcastReceiver() {
                        override fun onReceive(ctx: Context?, intent: Intent?) {
                            viewModel.refreshDateIfNeeded()
                        }
                    }
                    val filter = IntentFilter().apply {
                        addAction(Intent.ACTION_DATE_CHANGED)
                        addAction(Intent.ACTION_TIME_CHANGED)
                        addAction(Intent.ACTION_TIMEZONE_CHANGED)
                    }
                    ContextCompat.registerReceiver(
                        context,
                        dateChangeReceiver,
                        filter,
                        ContextCompat.RECEIVER_NOT_EXPORTED
                    )
                    onDispose {
                        context.unregisterReceiver(dateChangeReceiver)
                    }
                }

                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                HealthCheckScreen(
                    uiState = uiState,
                    onToggleMedication = viewModel::toggleMedication,
                    onAddWater = viewModel::addWaterCup,
                    onRemoveWater = viewModel::removeWaterCup
                )
            }
        }
    }
}
