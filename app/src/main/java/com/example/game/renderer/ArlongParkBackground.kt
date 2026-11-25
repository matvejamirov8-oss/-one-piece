package com.example.game.renderer

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import com.example.game.model.ArlongStagePhase
import com.example.game.model.DestructiblePropKind
import com.example.game.model.PlayableHero
import com.example.game.model.WorldConstants
import com.example.game.model.WorldState
import com.example.game.sprites.ArlongParkSpriteSheet
import com.example.game.sprites.EastBlueSprites
import com.example.game.sprites.LuffySpriteSheet
import kotlin.math.floor
import kotlin.math.sin

/**
 * 16-bit Pixel Art environment & interactive setpiece renderer for Level 3: Arlong Park!
 * Renders:
 * - Phase 1: Arlong Park stone gates, courtyard decks, central sparkling deep swimming pool,
 *   elevated Chew Sniper balconies, palm trees, and the towering Arlong Park Pagoda.
 * - Phase 2: Smooth upward camera pan into Nami's Map Room at the top of the tower, with
 *   arched sky windows, tiled stone floor, and destructible pillars, map shelves & desk.
 * - Finale: Slow-mo golden tension framing & multi-story Tower Collapse plunge sequence!
 */
object ArlongParkBackground {

    private val pixelInk = Color(0xFF181425)
    private val skyTop = Color(0xFF1B76D1)
    private val skyMid = Color(0xFF44A5F2)
    private val skyLow = Color(0xFF8CD5FF)
    private val skyHorizon = Color(0xFFC8F0FF)

    private val pagodaWall = Color(0xFFE6DCC8)
    private val pagodaWallShade = Color(0xFFC2B59D)
    private val pagodaRoof = Color(0xFFA8242E)
    private val pagodaRoofHighlight = Color(0xFFD93B47)
    private val pagodaRoofDark = Color(0xFF6B141D)

    private val stoneLight = Color(0xFFD9D1C3)
    private val stoneMid = Color(0xFFB3A896)
    private val stoneDark = Color(0xFF7A7164)
    private val stoneDeep = Color(0xFF4E473E)

    private val poolSurface = Color(0xFF47DBFF)
    private val poolUpper = Color(0xFF1A96DE)
    private val poolMid = Color(0xFF116BB5)
    private val poolDeep = Color(0xFF0A437A)
    private val poolFoam = Color(0xFFE8FDFF)

    private val mapWallDark = Color(0xFF3E2A22)
    private val mapWallMid = Color(0xFF5A3D2E)
    private val mapWoodBeam = Color(0xFF7D502C)
    private val mapFloorLight = Color(0xFFD6CBB8)
    private val mapFloorDark = Color(0xFFB0A38D)

    fun render(
        world: WorldState,
        viewW: Float,
        camX: Float,
        camY: Float,
        shakeY: Float,
        envSprites: EastBlueSprites,
        arlongSprites: ArlongParkSpriteSheet,
        luffySprites: LuffySpriteSheet,
        drawPxRect: (Float, Float, Float, Float, Color) -> Unit,
        drawPxSprite: (ImageBitmap, Float, Float, Float, Float) -> Unit
    ) {
        if (world.isArlongMapRoom) {
            renderMapRoom(world, viewW, camX, camY, arlongSprites, drawPxRect, drawPxSprite)
        } else {
            // Smooth upward pan effect during the first half of TOWER_TRANSITION!
            val panOffsetY = if (world.arlongPhase == ArlongStagePhase.TOWER_TRANSITION) {
                val p = (world.arlongTransitionTimer / 0.72f).coerceIn(0f, 1f)
                p * p * 110f
            } else {
                0f
            }
            renderCourtyard(
                world = world,
                viewW = viewW,
                camX = camX,
                camY = camY - panOffsetY,
                shakeY = shakeY,
                envSprites = envSprites,
                luffySprites = luffySprites,
                drawPxRect = drawPxRect,
                drawPxSprite = drawPxSprite
            )
        }
    }

    private fun renderCourtyard(
        world: WorldState,
        viewW: Float,
        camX: Float,
        camY: Float,
        shakeY: Float,
        envSprites: EastBlueSprites,
        luffySprites: LuffySpriteSheet,
        drawPxRect: (Float, Float, Float, Float, Color) -> Unit,
        drawPxSprite: (ImageBitmap, Float, Float, Float, Float) -> Unit
    ) {
        val time = world.worldTime

        // 1. Tropical East Blue Sky
        drawPxRect(0f, 0f, viewW + 2f, 24f, skyTop)
        drawPxRect(0f, 24f, viewW + 2f, 24f, skyMid)
        drawPxRect(0f, 48f, viewW + 2f, 24f, skyLow)
        drawPxRect(0f, 72f, viewW + 2f, 52f, skyHorizon)

        // Parallax clouds
        for (cloud in world.clouds) {
            val cx = cloud.x - camX * 0.22f
            if (cx > -55f && cx < viewW + 55f) {
                val sprite = if (cloud.variant == 0) envSprites.cloudLarge else envSprites.cloudSmall
                drawPxSprite(sprite, cx, cloud.y * 0.65f - shakeY * 0.2f, 1f, 1f)
            }
        }

        // Distant ocean horizon behind courtyard wall
        val wallTopY = 128f - camY
        drawPxRect(0f, wallTopY - 22f, viewW + 2f, 22f, Color(0xFF1F7BC9))
        drawPxRect(0f, wallTopY - 10f, viewW + 2f, 10f, Color(0xFF33A3E0))

        // 2. Iconic Arlong Park Multi-Story Pagoda Tower (centered at world X = 380)
        val towerCx = 380f - camX * 0.55f
        val towerBaseY = 176f - camY
        for (story in 0..3) {
            val storyW = 104f - story * 14f
            val storyH = 26f
            val sy = towerBaseY - (story + 1) * storyH
            // Limestone pagoda body
            drawPxRect(towerCx - storyW * 0.5f, sy, storyW, storyH, pagodaWall)
            drawPxRect(towerCx + storyW * 0.16f, sy, storyW * 0.34f, storyH, pagodaWallShade)
            // Windows
            val winCount = 3 - (story / 2)
            for (w in 0 until winCount) {
                val wx = towerCx - storyW * 0.32f + w * (storyW * 0.64f / (winCount - 1).coerceAtLeast(1))
                drawPxRect(wx - 4f, sy + 7f, 8f, 12f, Color(0xFF282034))
                drawPxRect(wx - 3f, sy + 8f, 6f, 4f, Color(0xFF66C2FF))
            }
            // Curved Crimson Pagoda Eaves
            val roofW = storyW + 22f
            drawPxRect(towerCx - roofW * 0.5f, sy - 4f, roofW, 6f, pagodaRoof)
            drawPxRect(towerCx - roofW * 0.5f + 4f, sy - 6f, roofW - 8f, 3f, pagodaRoofHighlight)
            drawPxRect(towerCx - roofW * 0.5f - 3f, sy - 6f, 6f, 4f, pagodaRoofDark)
            drawPxRect(towerCx + roofW * 0.5f - 3f, sy - 6f, 6f, 4f, pagodaRoofDark)
            drawPxRect(towerCx - roofW * 0.5f, sy + 2f, roofW, 2f, pixelInk)
        }
        // Top Shark-Saw Emblem Spire on Pagoda Peak
        val spireY = towerBaseY - 4 * 26f - 24f
        drawPxRect(towerCx - 2f, spireY, 4f, 20f, pixelInk)
        // Saw-nose banner flag fluttering in the breeze
        val wave = if (((time * 6f).toInt() % 2) == 0) 1f else 0f
        drawPxRect(towerCx + 2f, spireY + 2f + wave, 18f, 10f, Color(0xFFE53844))
        drawPxRect(towerCx + 5f, spireY + 5f + wave, 11f, 4f, Color(0xFFF7F9FC))

        // 3. Background Courtyard Perimeter Wall & Tropical Palm Trees
        drawPxRect(0f, wallTopY, viewW + 2f, 58f, stoneMid)
        drawPxRect(0f, wallTopY, viewW + 2f, 4f, stoneLight)
        drawPxRect(0f, wallTopY + 4f, viewW + 2f, 2f, stoneDark)
        // Wall battlements & stone masonry joints
        val blockStart = ((camX / 28f).toInt() - 1)
        val blockEnd = blockStart + (viewW / 28f).toInt() + 4
        for (i in blockStart..blockEnd) {
            val bx = i * 28f - camX
            drawPxRect(bx, wallTopY - 6f, 18f, 6f, stoneLight)
            drawPxRect(bx, wallTopY - 6f, 18f, 1.5f, pixelInk)
            drawPxRect(bx, wallTopY + 6f, 1.5f, 44f, stoneDark)
        }

        // Palm trees framing left & right edges of Arlong Park
        val palmXs = floatArrayOf(58f, 134f, 646f, 706f)
        for ((idx, pxWorld) in palmXs.withIndex()) {
            val sx = pxWorld - camX
            if (sx > -45f && sx < viewW + 45f) {
                val palmBmp = if (idx % 2 == 0) envSprites.palmTreeRight else envSprites.palmTreeLeft
                drawPxSprite(palmBmp, sx - 20f, WorldConstants.BEACH_GROUND_Y - camY - 58f, 1.0f, 1f)
            }
        }

        // 4. Elevated Upper Balconies (Left & Right — Chew Snipers perch here!)
        fun drawBalcony(minX: Float, maxX: Float) {
            val bx = minX - camX
            val bw = maxX - minX
            val by = WorldConstants.ARLONG_BALCONY_Y - camY
            val groundScreenY = WorldConstants.BEACH_GROUND_Y - camY
            if (bx + bw < -10f || bx > viewW + 10f) return
            // Supporting stone columns
            drawPxRect(bx + 6f, by + 6f, 10f, groundScreenY - by - 6f, stoneDark)
            drawPxRect(bx + 8f, by + 6f, 5f, groundScreenY - by - 6f, stoneMid)
            drawPxRect(bx + bw - 16f, by + 6f, 10f, groundScreenY - by - 6f, stoneDark)
            drawPxRect(bx + bw - 14f, by + 6f, 5f, groundScreenY - by - 6f, stoneMid)
            // Balcony deck slab
            drawPxRect(bx - 2f, by, bw + 4f, 8f, stoneLight)
            drawPxRect(bx - 2f, by + 6f, bw + 4f, 3f, stoneDeep)
            drawPxRect(bx - 2f, by, bw + 4f, 1.5f, pixelInk)
            // Decorative railing posts
            for (rx in 4 until bw.toInt() - 4 step 14) {
                drawPxRect(bx + rx, by - 7f, 3f, 7f, stoneDark)
            }
            drawPxRect(bx, by - 8f, bw, 2f, stoneLight)
        }
        drawBalcony(WorldConstants.ARLONG_LEFT_BALCONY_MIN_X, WorldConstants.ARLONG_LEFT_BALCONY_MAX_X)
        drawBalcony(WorldConstants.ARLONG_RIGHT_BALCONY_MIN_X, WorldConstants.ARLONG_RIGHT_BALCONY_MAX_X)

        // Intermediate Stone Stepping Platforms leading easily onto both balconies!
        fun drawSteppingPlatform(minX: Float, maxX: Float) {
            val sx = minX - camX
            val sw = maxX - minX
            val sy = WorldConstants.ARLONG_STEP_Y - camY
            val groundScreenY = WorldConstants.BEACH_GROUND_Y - camY
            if (sx + sw < -10f || sx > viewW + 10f) return
            drawPxRect(sx + 4f, sy + 6f, sw - 8f, groundScreenY - sy - 6f, stoneDark)
            drawPxRect(sx + 6f, sy + 6f, sw - 14f, groundScreenY - sy - 6f, stoneMid)
            drawPxRect(sx - 1f, sy, sw + 2f, 7f, stoneLight)
            drawPxRect(sx - 1f, sy + 5f, sw + 2f, 2.5f, stoneDeep)
            drawPxRect(sx - 1f, sy, sw + 2f, 1.5f, pixelInk)
        }
        drawSteppingPlatform(WorldConstants.ARLONG_LEFT_STEP_MIN_X, WorldConstants.ARLONG_LEFT_STEP_MAX_X)
        drawSteppingPlatform(WorldConstants.ARLONG_RIGHT_STEP_MIN_X, WorldConstants.ARLONG_RIGHT_STEP_MAX_X)

        // 5. Left & Right Stone Courtyard Decks + Deep Central Swimming Pool!
        val deckY = WorldConstants.BEACH_GROUND_Y - camY
        val poolLeftSx = WorldConstants.ARLONG_POOL_LEFT_X - camX
        val poolRightSx = WorldConstants.ARLONG_POOL_RIGHT_X - camX
        val poolW = poolRightSx - poolLeftSx

        // Deep Central Swimming Pool Basin
        drawPxRect(poolLeftSx, deckY + 2f, poolW, 64f, poolDeep)
        drawPxRect(poolLeftSx + 3f, deckY + 2f, poolW - 6f, 32f, poolMid)
        drawPxRect(poolLeftSx + 3f, deckY + 2f, poolW - 6f, 14f, poolUpper)
        // Pool tiled rim walls
        drawPxRect(poolLeftSx, deckY, 4f, 56f, stoneDeep)
        drawPxRect(poolRightSx - 4f, deckY, 4f, 56f, stoneDeep)
        // Animated sparkling water surface & shimmer ripples
        val surfY = WorldConstants.ARLONG_POOL_SURFACE_Y - camY
        drawPxRect(poolLeftSx + 3f, surfY, poolW - 6f, 3f, poolSurface)
        val rippleShift = ((time * 18f) % 20f)
        for (rx in 0 until (poolW.toInt() - 12) step 20) {
            val sx = poolLeftSx + 6f + ((rx + rippleShift) % (poolW - 16f).coerceAtLeast(1f))
            drawPxRect(sx, surfY + 1f, 8f, 1.5f, poolFoam)
            drawPxRect(sx - 3f, surfY + 11f, 6f, 1.5f, Color(0x668CE8FF))
        }
        // Pool warning sign painted near edges
        drawPxRect(poolLeftSx - 12f, deckY + 1f, 10f, 3f, Color(0xFFFFD84D))
        drawPxRect(poolRightSx + 2f, deckY + 1f, 10f, 3f, Color(0xFFFFD84D))

        // If Luffy is drowning in the pool, highlight BOTH poolside rescue zones clearly!
        if (world.luffyDrowning) {
            val zonePulse = 0.45f + 0.35f * sin(time * 12f)
            val zoneCol = Color(0xFF47DBFF).copy(alpha = zonePulse.coerceIn(0.2f, 0.85f))
            val rimCol = Color(0xFFFFD84D).copy(alpha = (zonePulse + 0.15f).coerceIn(0.3f, 0.95f))
            // Left pool rim rescue pad
            drawPxRect(poolLeftSx - 38f, deckY - 3f, 36f, 4f, zoneCol)
            drawPxRect(poolLeftSx - 38f, deckY - 4f, 36f, 1.5f, rimCol)
            // Right pool rim rescue pad
            drawPxRect(poolRightSx + 2f, deckY - 3f, 36f, 4f, zoneCol)
            drawPxRect(poolRightSx + 2f, deckY - 4f, 36f, 1.5f, rimCol)
            if (world.luffy.activeHero != PlayableHero.LUFFY) {
                val nearestRimX = if (world.luffyDrownX < 380f) poolLeftSx - 20f else poolRightSx + 20f
                val arrowBob = sin(time * 9f) * 2.5f
                PixelBitmapText.drawCentered(
                    text = "СЮДА!",
                    centerX = nearestRimX,
                    topY = deckY - 18f + arrowBob,
                    pixelSize = 0.95f,
                    color = Color(0xFF47DBFF),
                    alpha = 1f,
                    drawPxRect = drawPxRect
                )
            }
        }

        // If Luffy is drowning in the pool while player switched to a rescuer, render drowning Luffy right inside the pool!
        if (world.luffyDrowning && world.luffy.activeHero != PlayableHero.LUFFY) {
            val lx = world.luffyDrownX - camX
            val ly = WorldConstants.ARLONG_POOL_DROWN_Y - camY + sin(time * 7f) * 2f
            val hurtBitmap = luffySprites.hurtPose.right
            drawPxSprite(hurtBitmap, floor(lx - hurtBitmap.width * 0.5f), floor(ly - hurtBitmap.height + 4f), 1f, 0.92f)
            // Semi-transparent water overlay across lower half of drowning Luffy
            drawPxRect(lx - 14f, surfY + 1f, 28f, 26f, Color(0x771996DE))
            // Pulsing pixel rescue indicator right above Luffy!
            val pulse = if (((time * 10f).toInt() % 2) == 0) Color(0xFFFF4D4D) else Color(0xFFFFD84D)
            PixelBitmapText.drawCentered(
                text = "СПАСИ! ${String.format(java.util.Locale.US, "%.1f", world.luffyDrownTimer)}с",
                centerX = lx,
                topY = surfY - 22f,
                pixelSize = 1.0f,
                color = pulse,
                alpha = 1f,
                drawPxRect = drawPxRect
            )
        }

        // Left Stone Courtyard Deck
        if (poolLeftSx > -10f) {
            val lw = (poolLeftSx + 2f).coerceAtLeast(0f)
            drawPxRect(-2f, deckY, lw, 6f, stoneLight)
            drawPxRect(-2f, deckY + 6f, lw, 24f, stoneMid)
            drawPxRect(-2f, deckY + 30f, lw, 38f, stoneDark)
            drawPxRect(-2f, deckY, lw, 1.5f, pixelInk)
            for (tx in 0 until WorldConstants.ARLONG_POOL_LEFT_X.toInt() step 22) {
                val sx = tx - camX
                if (sx > -22f && sx < poolLeftSx) {
                    drawPxRect(sx, deckY + 2f, 1.5f, 28f, stoneDark)
                }
            }
        }
        // Right Stone Courtyard Deck
        if (poolRightSx < viewW + 10f) {
            val rw = (viewW - poolRightSx + 8f).coerceAtLeast(0f)
            drawPxRect(poolRightSx, deckY, rw, 6f, stoneLight)
            drawPxRect(poolRightSx, deckY + 6f, rw, 24f, stoneMid)
            drawPxRect(poolRightSx, deckY + 30f, rw, 38f, stoneDark)
            drawPxRect(poolRightSx, deckY, rw, 1.5f, pixelInk)
            for (tx in WorldConstants.ARLONG_POOL_RIGHT_X.toInt() until WorldConstants.WORLD_WIDTH.toInt() step 22) {
                val sx = tx - camX
                if (sx > poolRightSx && sx < viewW + 22f) {
                    drawPxRect(sx, deckY + 2f, 1.5f, 28f, stoneDark)
                }
            }
        }

        // 6. Closed Stone Gate of Arlong Park (Phase: GATE_INTRO)
        if (world.arlongPhase == ArlongStagePhase.GATE_INTRO) {
            val gx = WorldConstants.ARLONG_GATE_X - camX
            val gy = deckY - 68f
            // Massive stone pillars & arched lintel
            drawPxRect(gx - 12f, gy - 8f, 28f, 10f, stoneLight)
            drawPxRect(gx - 12f, gy - 8f, 28f, 2f, pixelInk)
            drawPxRect(gx - 10f, gy + 2f, 24f, 66f, Color(0xFF7A4820))
            drawPxRect(gx - 8f, gy + 4f, 20f, 64f, Color(0xFF9E612F))
            // Iron bands
            drawPxRect(gx - 10f, gy + 16f, 24f, 4f, Color(0xFF3E4759))
            drawPxRect(gx - 10f, gy + 48f, 24f, 4f, Color(0xFF3E4759))
            // Crimson Arlong Saw-Shark Emblem on the gate!
            drawPxRect(gx - 5f, gy + 26f, 14f, 14f, Color(0xFFD92B3A))
            drawPxRect(gx - 3f, gy + 30f, 10f, 6f, Color(0xFFF7F9FC))
            drawPxRect(gx - 10f, gy + 2f, 2f, 66f, pixelInk)
            drawPxRect(gx + 12f, gy + 2f, 2f, 66f, pixelInk)

            // Pulsing pixel prompt arrow above gate
            val bob = sin(time * 8f) * 3f
            val blinkColor = if (((time * 6f).toInt() % 2) == 0) Color(0xFFFFD84D) else Color(0xFFFF6B35)
            PixelBitmapText.drawCentered(
                text = "УДАРЬ ВОРОТА!",
                centerX = gx + 2f,
                topY = gy - 24f + bob,
                pixelSize = 1.1f,
                color = blinkColor,
                alpha = 1f,
                drawPxRect = drawPxRect
            )
        }
    }

    private fun renderMapRoom(
        world: WorldState,
        viewW: Float,
        camX: Float,
        camY: Float,
        arlongSprites: ArlongParkSpriteSheet,
        drawPxRect: (Float, Float, Float, Float, Color) -> Unit,
        drawPxSprite: (ImageBitmap, Float, Float, Float, Float) -> Unit
    ) {
        val time = world.worldTime
        val floorY = WorldConstants.BEACH_GROUND_Y - camY

        // 1. Warm Timber & Stone Tower-Top Map Room Walls
        drawPxRect(0f, 0f, viewW + 2f, floorY, mapWallDark)
        drawPxRect(0f, 18f, viewW + 2f, floorY - 24f, mapWallMid)
        // Ceiling beams
        drawPxRect(0f, 0f, viewW + 2f, 14f, Color(0xFF2B1A14))
        drawPxRect(0f, 14f, viewW + 2f, 4f, mapWoodBeam)

        // 2. Tall Arched Panoramic Windows overlooking the sky (showing height of the tower!)
        val windowXs = floatArrayOf(110f, 290f, 470f, 650f)
        for (wxWorld in windowXs) {
            val wx = wxWorld - camX
            if (wx > -70f && wx < viewW + 70f) {
                val wy = 24f
                val ww = 54f
                val wh = floorY - 36f
                drawPxRect(wx - 3f, wy - 3f, ww + 6f, wh + 6f, pixelInk)
                drawPxRect(wx, wy, ww, wh * 0.45f, skyTop)
                drawPxRect(wx, wy + wh * 0.45f, ww, wh * 0.35f, skyMid)
                drawPxRect(wx, wy + wh * 0.80f, ww, wh * 0.20f, skyHorizon)
                // Window mullions
                drawPxRect(wx + ww * 0.5f - 1.5f, wy, 3f, wh, mapWoodBeam)
                drawPxRect(wx, wy + wh * 0.45f, ww, 3f, mapWoodBeam)
            }
        }

        // Wall-mounted rolled nautical charts & Nami's map boards in the background
        for (bxWorld in 180..620 step 110) {
            val bx = bxWorld - camX
            if (bx > -40f && bx < viewW + 40f) {
                drawPxRect(bx, floorY - 72f, 34f, 24f, Color(0xFF3A2318))
                drawPxRect(bx + 2f, floorY - 70f, 30f, 20f, Color(0xFFFFF2C8))
                drawPxRect(bx + 6f, floorY - 65f, 18f, 2f, Color(0xFF4A90C8))
                drawPxRect(bx + 10f, floorY - 58f, 14f, 2f, Color(0xFFE53844))
            }
        }

        // 3. Checkered Limestone Map Room Floor
        drawPxRect(0f, floorY, viewW + 2f, 60f, mapFloorDark)
        drawPxRect(0f, floorY, viewW + 2f, 6f, mapFloorLight)
        drawPxRect(0f, floorY, viewW + 2f, 1.5f, pixelInk)
        val tileStart = ((camX / 24f).toInt() - 1)
        val tileEnd = tileStart + (viewW / 24f).toInt() + 4
        for (i in tileStart..tileEnd) {
            val tx = i * 24f - camX
            val color = if (i % 2 == 0) mapFloorLight else mapFloorDark
            drawPxRect(tx, floorY + 6f, 24f, 16f, color)
            drawPxRect(tx, floorY + 6f, 1.5f, 30f, stoneDark)
        }

        // 4. Interactive Destructible Props (Pillars, Map Shelves, Nami's Desk)
        for (prop in world.arlongProps) {
            val sx = prop.x - camX
            if (sx < -50f || sx > viewW + 50f) continue
            val bitmap = when (prop.kind) {
                DestructiblePropKind.MAP_SHELF ->
                    if (prop.isDestroyed) arlongSprites.mapShelfBroken else arlongSprites.mapShelfIntact
                DestructiblePropKind.STONE_PILLAR ->
                    if (prop.isDestroyed) arlongSprites.stonePillarBroken else arlongSprites.stonePillarIntact
                DestructiblePropKind.NAMI_DESK ->
                    if (prop.isDestroyed) arlongSprites.namiDeskBroken else arlongSprites.namiDeskIntact
            }
            val scale = when (prop.kind) {
                DestructiblePropKind.STONE_PILLAR -> 2.0f
                DestructiblePropKind.MAP_SHELF -> 1.8f
                DestructiblePropKind.NAMI_DESK -> 1.6f
            }
            val jitter = if (prop.flashTimer > 0f) (if (((time * 60f).toInt() % 2) == 0) 2f else -2f) else 0f
            val drawW = bitmap.width * scale
            val drawH = bitmap.height * scale
            drawPxSprite(bitmap, floor(sx - drawW * 0.5f + jitter), floor(floorY - drawH + 2f), scale, 1f)
            // Crack cracks on damaged-but-standing props
            if (!prop.isDestroyed && prop.hp < prop.maxHp) {
                drawPxRect(sx - 4f, floorY - drawH * 0.6f, 2f, 10f, pixelInk)
                drawPxRect(sx - 2f, floorY - drawH * 0.45f, 6f, 2f, pixelInk)
            }
        }

        // 5. Slow-Mo Finisher Dramatic Golden Frame Vignette!
        if (world.arlongPhase == ArlongStagePhase.FINISHER_SLOWMO) {
            val pulseAlpha = 0.16f + 0.10f * sin(time * 18f)
            drawPxRect(0f, 0f, viewW + 2f, 12f, Color(0xFFFFD84D).copy(alpha = pulseAlpha.coerceIn(0.05f, 0.35f)))
            drawPxRect(0f, floorY + 26f, viewW + 2f, 18f, Color(0xFFFFD84D).copy(alpha = pulseAlpha.coerceIn(0.05f, 0.35f)))
        }

        // 6. EPIC TOWER COLLAPSE MULTI-FLOOR CRASH SEQUENCE!
        if (world.arlongPhase == ArlongStagePhase.TOWER_COLLAPSE) {
            val ct = world.arlongCollapseTimer
            val cx = world.arlongCollapseX - camX
            // Dark smoky dramatic sky overlay as the tower disintegrates
            val dustAlpha = (ct / 2.65f).coerceIn(0f, 0.72f)
            drawPxRect(0f, 0f, viewW + 2f, 200f, Color(0xFF1A1424).copy(alpha = dustAlpha * 0.55f))

            // 5 Pagoda Floor slabs rushing UPWARD past the plunging clash!
            val scrollY = (ct * 190f)
            for (f in 0..4) {
                val fy = ((f * 46f - scrollY) % 210f + 210f) % 210f
                // Cracked & splitting left/right floor halves flying apart from the impact column!
                val splitGap = 24f + ((ct * 28f) % 36f)
                drawPxRect(0f, fy, (cx - splitGap).coerceAtLeast(0f), 8f, pagodaRoof)
                drawPxRect(0f, fy + 8f, (cx - splitGap).coerceAtLeast(0f), 4f, stoneMid)
                drawPxRect(cx + splitGap, fy, (viewW - cx - splitGap + 10f).coerceAtLeast(0f), 8f, pagodaRoof)
                drawPxRect(cx + splitGap, fy + 8f, (viewW - cx - splitGap + 10f).coerceAtLeast(0f), 4f, stoneMid)
            }

            // Blazing vertical Battle Axe impact column straight through Arlong Park!
            val colPulse = if (((ct * 28f).toInt() % 2) == 0) 4f else 0f
            drawPxRect(cx - 18f - colPulse, 0f, 36f + colPulse * 2f, 190f, Color(0x66FFD84D))
            drawPxRect(cx - 8f, 0f, 16f, 190f, Color(0xAAFFFBE0))
        }
    }
}
