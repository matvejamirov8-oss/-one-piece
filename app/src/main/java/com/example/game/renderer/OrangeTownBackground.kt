package com.example.game.renderer

import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import com.example.game.model.WorldConstants
import com.example.game.model.WorldState
import com.example.game.sprites.BuggyCrewSpriteSheet
import com.example.game.sprites.EastBlueSprites
import kotlin.math.sin

/**
 * 16-bit Pixel Art Sunset Environment Renderer for Level 2 — Orange Town (Орандж-Таун).
 * Features a twilight-to-crimson sunset sky, low glowing sun, dusk harbor with Buggy's clown ship,
 * ruined port town houses, bright red-and-white striped circus tent, wooden crates, and cobblestone plaza.
 */
object OrangeTownBackground {

    private val duskSky0 = Color(0xFF28194A)
    private val duskSky1 = Color(0xFF5A245E)
    private val duskSky2 = Color(0xFFA63452)
    private val duskSky3 = Color(0xFFE0663A)
    private val duskHorizon = Color(0xFFFFB454)

    private val sunsetSunCore = Color(0xFFFFF3B0)
    private val sunsetSunGlow = Color(0xFFFF9838)
    private val sunsetSunHalo = Color(0x55FF6B2B)

    private val duskSeaDeep = Color(0xFF2C1E4A)
    private val duskSeaMid = Color(0xFF542854)
    private val duskSeaWarm = Color(0xFF8C3B4A)
    private val sunsetShimmer = Color(0xAAFFBE66)

    private val cobbleTopLight = Color(0xFFB5A092)
    private val cobbleBase = Color(0xFF8C786C)
    private val cobbleMid = Color(0xFF6B5952)
    private val cobbleDark = Color(0xFF473A38)
    private val cobbleGrout = Color(0xFF2B2129)
    private val curbHighlight = Color(0xFFD9B896)
    private val pixelInk = Color(0xFF181425)

    fun render(
        world: WorldState,
        viewW: Float,
        camX: Float,
        camY: Float,
        shakeY: Float,
        envSprites: EastBlueSprites,
        buggySprites: BuggyCrewSpriteSheet,
        drawPxRect: (Float, Float, Float, Float, Color) -> Unit,
        drawPxSprite: (ImageBitmap, Float, Float, Float, Float, ColorFilter?) -> Unit
    ) {
        val time = world.worldTime

        // === 1. SUNSET SKY BANDS + 16-BIT DITHERING ===
        drawPxRect(0f, 0f, viewW + 2f, 18f, duskSky0)
        drawPxRect(0f, 18f, viewW + 2f, 16f, duskSky1)
        drawPxRect(0f, 34f, viewW + 2f, 16f, duskSky2)
        drawPxRect(0f, 50f, viewW + 2f, 15f, duskSky3)
        drawPxRect(0f, 65f, viewW + 2f, 15f, duskHorizon)

        for (i in 0..(viewW.toInt() / 4 + 2)) {
            val dx = i * 4f
            drawPxRect(dx, 17f, 2f, 1f, duskSky1)
            drawPxRect(dx + 2f, 33f, 2f, 1f, duskSky2)
            drawPxRect(dx, 49f, 2f, 1f, duskSky3)
            drawPxRect(dx + 2f, 64f, 2f, 1f, duskHorizon)
        }

        // Low glowing sunset sun over Orange Town harbor
        val sunX = (viewW * 0.68f) - camX * 0.04f
        val sunY = 58f - shakeY * 0.2f
        val pulse = if (sin(time * 2.0f) > 0f) 1f else 0f
        drawPxRect(sunX - 18f - pulse, sunY - 18f - pulse, 36f + pulse * 2f, 36f + pulse * 2f, sunsetSunHalo)
        drawPxRect(sunX - 13f, sunY - 13f, 26f, 26f, sunsetSunGlow)
        drawPxRect(sunX - 9f, sunY - 9f, 18f, 18f, sunsetSunCore)

        // Sunset-tinted clouds & distant birds
        val cloudTint = ColorFilter.tint(Color(0x88FF9E6B), BlendMode.SrcAtop)
        for (cloud in world.clouds) {
            val cx = cloud.x - camX * 0.22f
            if (cx > -55f && cx < viewW + 55f) {
                val sprite = if (cloud.variant == 0) envSprites.cloudLarge else envSprites.cloudSmall
                drawPxSprite(sprite, cx, cloud.y * 0.58f - shakeY * 0.2f, 1f, 0.92f, cloudTint)
            }
        }
        val gullTint = ColorFilter.tint(Color(0xFF331B36), BlendMode.SrcAtop)
        for (gull in world.seagulls) {
            val gx = gull.x - camX * 0.34f
            if (gx > -20f && gx < viewW + 20f) {
                val sprite = if (sin(gull.wingPhase) >= 0f) envSprites.seagullUp else envSprites.seagullDown
                val bobY = sin(gull.wingPhase) * 2.2f
                drawPxSprite(sprite, gx, gull.y * 0.66f + bobY - shakeY * 0.22f, 1f, 0.9f, gullTint)
            }
        }

        // === 2. DUSK HARBOR SEA & BUGGY'S CLOWN PIRATE SHIP ===
        val seaTopY = 112f - camY
        drawPxRect(0f, seaTopY, viewW + 2f, 18f, duskSeaDeep)
        drawPxRect(0f, seaTopY + 18f, viewW + 2f, 22f, duskSeaMid)
        drawPxRect(0f, seaTopY + 40f, viewW + 2f, 24f, duskSeaWarm)

        // Golden sun reflection shimmering across the water
        for (row in 0..4) {
            val wy = seaTopY + 4f + row * 9f
            val wobble = sin(time * 3.1f + row * 1.4f) * 3.5f
            val reflW = (30f - row * 4f).coerceAtLeast(10f)
            drawPxRect(sunX - reflW * 0.5f + wobble, wy, reflW, 2f, sunsetShimmer)
        }

        // Buggy's Big Top Pirate Ship with Clown Figurehead on the horizon!
        val shipX = 438f - camX * 0.44f
        val shipBob = sin(time * 2.0f) * 1.8f
        val shipY = seaTopY - 12f + shipBob
        if (shipX > -75f && shipX < viewW + 75f) {
            drawPxSprite(buggySprites.buggyShip, shipX, shipY, 1.35f, 1f, null)
            val wakeW = 42f + sin(time * 3.2f) * 4f
            drawPxRect(shipX + 8f, shipY + 27f, wakeW, 2f, sunsetShimmer)
        }

        // Distant harbor seawall separating ocean & town plaza
        val streetTopY = 166f - camY
        drawPxRect(-2f, streetTopY - 12f, viewW + 4f, 12f, Color(0xFF42313F))
        drawPxRect(-2f, streetTopY - 13f, viewW + 4f, 2f, Color(0xFF8C5D5E))
        for (px in 0..760 step 32) {
            val sx = px - camX * 0.65f
            if (sx > -16f && sx < viewW + 16f) {
                drawPxRect(sx, streetTopY - 18f, 6f, 7f, pixelInk)
                drawPxRect(sx + 1f, streetTopY - 17f, 4f, 5f, Color(0xFF70585C))
            }
        }

        // === 3. ORANGE TOWN RUINED HOUSES & BRIGHT CIRCUS TENT ===
        val housePositions = listOf(
            Triple(28f, true, 1.65f),
            Triple(142f, false, 1.55f),
            Triple(436f, true, 1.60f),
            Triple(572f, false, 1.65f)
        )
        for ((hxWorld, isTypeA, scale) in housePositions) {
            val sx = hxWorld - camX * 0.78f
            val sprite = if (isTypeA) buggySprites.ruinedHouseA else buggySprites.ruinedHouseB
            if (sx > -90f && sx < viewW + 90f) {
                val topY = streetTopY + 2f - sprite.height * scale
                drawPxSprite(sprite, sx, topY, scale, 1f, null)
            }
        }

        // Iconic Red-and-White Striped Buggy Circus Tent in the town center!
        val tentX = 274f - camX * 0.80f
        val tentScale = 1.85f
        if (tentX > -120f && tentX < viewW + 120f) {
            val tentTopY = streetTopY + 3f - buggySprites.circusTent.height * tentScale
            drawPxSprite(buggySprites.circusTent, tentX, tentTopY, tentScale, 1f, null)
        }

        // === 4. COBBLESTONE ORANGE TOWN STREET (КАМЕННАЯ МОСТОВАЯ) ===
        drawPxRect(-2f, streetTopY - 2f, viewW + 4f, 2f, pixelInk)
        drawPxRect(-2f, streetTopY, viewW + 4f, 3f, curbHighlight)
        drawPxRect(-2f, streetTopY + 3f, viewW + 4f, 18f, cobbleTopLight)
        drawPxRect(-2f, streetTopY + 21f, viewW + 4f, 22f, cobbleBase)
        drawPxRect(-2f, streetTopY + 43f, viewW + 4f, 28f, cobbleMid)

        // Crisp 16-bit staggered cobblestone pavers
        val stoneW = 16f
        val stoneH = 7f
        for (row in 0..7) {
            val sy = streetTopY + 4f + row * stoneH
            val rowOffset = if (row % 2 == 0) 0f else stoneW * 0.5f
            val startCol = ((camX - rowOffset) / stoneW).toInt() - 1
            val endCol = startCol + (viewW / stoneW).toInt() + 3
            drawPxRect(-2f, sy, viewW + 4f, 1f, cobbleGrout)
            for (col in startCol..endCol) {
                val sx = col * stoneW + rowOffset - camX
                drawPxRect(sx, sy, 1f, stoneH, cobbleGrout)
                val shade = when ((col * 7 + row * 13) and 3) {
                    0 -> cobbleTopLight
                    1 -> cobbleBase
                    2 -> cobbleMid
                    else -> cobbleDark
                }
                drawPxRect(sx + 1f, sy + 1f, stoneW - 2f, stoneH - 2f, shade)
                if (row < 3) {
                    drawPxRect(sx + 2f, sy + 1f, stoneW - 5f, 1f, curbHighlight.copy(alpha = 0.55f))
                }
            }
        }

        // === 5. FOREGROUND RUINED CRATES, BARRELS & RUBBLE ===
        val propPositions = listOf(
            Triple(64f, true, 0f),
            Triple(196f, false, 1f),
            Triple(368f, true, -1f),
            Triple(518f, false, 0f),
            Triple(654f, true, 0f)
        )
        for ((wx, isCrate, yOff) in propPositions) {
            val sx = wx - camX
            if (sx > -28f && sx < viewW + 28f) {
                val sprite = if (isCrate) envSprites.crate else envSprites.barrel
                drawPxSprite(sprite, sx, streetTopY - 12f + yOff, 1f, 1f, null)
            }
        }

        // Small scattered stone debris chunks along the curb
        for (rx in 24..720 step 58) {
            val sx = rx - camX
            if (sx > -12f && sx < viewW + 12f) {
                drawPxRect(sx, streetTopY - 2f, 5f, 3f, pixelInk)
                drawPxRect(sx + 1f, streetTopY - 1f, 3f, 2f, cobbleTopLight)
                drawPxRect(sx + 7f, streetTopY, 3f, 2f, cobbleDark)
            }
        }
    }
}
