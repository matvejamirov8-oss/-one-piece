package com.example.game.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.game.model.LevelPhase
import com.example.game.model.WorldConstants
import com.example.ui.theme.PixelInk

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val hudState by viewModel.hudState.collectAsStateWithLifecycle()
    var viewportVirtualWidth by remember { mutableFloatStateOf(360f) }

    BackHandler(enabled = hudState.levelPhase != LevelPhase.MAIN_MENU) {
        viewModel.openMainMenu()
    }

    // 60fps vsync-locked simulation loop
    LaunchedEffect(viewModel) {
        var lastNanos = 0L
        while (true) {
            withFrameNanos { frameNanos ->
                if (lastNanos != 0L) {
                    val dt = ((frameNanos - lastNanos) / 1_000_000_000f).coerceIn(0.001f, 0.05f)
                    viewModel.stepFrame(dt, viewportVirtualWidth)
                }
                lastNanos = frameNanos
            }
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(PixelInk)
    ) {
        val isCompactWidth = maxWidth < 600.dp
        val world = viewModel.worldState

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("east_blue_game_canvas")
                .onSizeChanged { intSize ->
                    if (intSize.height > 0) {
                        val aspect = intSize.width.toFloat() / intSize.height.toFloat()
                        viewportVirtualWidth = WorldConstants.CAMERA_VIEW_HEIGHT * aspect
                    }
                }
        ) {
            with(viewModel.renderer) {
                renderWorld(world)
            }
        }

        PixelHudOverlay(
            hud = hudState,
            sprites = viewModel.luffySprites,
            zoroSprites = viewModel.zoroSprites,
            sanjiSprites = viewModel.sanjiSprites,
            namiSprites = viewModel.namiSprites,
            usoppSprites = viewModel.usoppSprites,
            chopperSprites = viewModel.chopperSprites,
            isCompactWidth = isCompactWidth,
            onToggleMute = viewModel::toggleMute,
            onToggleHitboxes = viewModel::toggleHitboxes,
            onToggleTouchControls = viewModel::toggleTouchControls,
            onLeftChanged = viewModel::setTouchLeft,
            onRightChanged = viewModel::setTouchRight,
            onJumpChanged = viewModel::setTouchJump,
            onPistolClick = viewModel::triggerPistol,
            onGatlingClick = viewModel::triggerGatling,
            onAxeClick = viewModel::triggerAxe,
            onGear4Click = viewModel::triggerGear4,
            onSwitchHero = viewModel::switchHero,
            onSelectHero = viewModel::selectHero,
            onRestartLevel = viewModel::restartLevel,
            onStartLevel = viewModel::startLevel,
            onNextLevel = viewModel::nextLevel,
            onOpenMainMenu = viewModel::openMainMenu
        )
    }
}
