package com.example

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.game.ui.GameScreen
import com.example.game.ui.GameViewModel
import com.example.ui.theme.GomuArenaTheme

class MainActivity : ComponentActivity() {

    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        WindowCompat.getInsetsController(window, window.decorView).apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.systemBars())
        }
        setContent {
            GomuArenaTheme {
                GameScreen(viewModel = gameViewModel)
            }
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        val isDown = event.action == KeyEvent.ACTION_DOWN
        val isUp = event.action == KeyEvent.ACTION_UP
        if (isDown || isUp) {
            val handled = gameViewModel.onHardwareKeyEvent(
                keyCode = event.keyCode,
                isDown = isDown,
                repeatCount = event.repeatCount
            )
            if (handled) return true
        }
        return super.dispatchKeyEvent(event)
    }
}
