package com.example.game.renderer

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.example.game.model.AttackPhase
import com.example.game.model.AttackType
import com.example.game.model.BossAttackVariant
import com.example.game.model.BuggyPartKind
import com.example.game.model.EnemyBehaviorState
import com.example.game.model.EnemyKind
import com.example.game.model.EnemyState
import com.example.game.model.FacingDirection
import com.example.game.model.LootKind
import com.example.game.model.LuffyState
import com.example.game.model.ParticleKind
import com.example.game.model.PlayableHero
import com.example.game.model.PlayerAnimState
import com.example.game.model.PlayerProjectileKind
import com.example.game.model.ProjectileKind
import com.example.game.model.SlashVfxKind
import com.example.game.model.SwordSlashVfx
import com.example.game.model.WorldConstants
import com.example.game.model.WorldState
import com.example.game.sprites.ArlongParkSpriteSheet
import com.example.game.sprites.BuggyCrewSpriteSheet
import com.example.game.sprites.ChopperSpriteSheet
import com.example.game.sprites.EastBlueSprites
import com.example.game.sprites.EnemySpriteSheet
import com.example.game.sprites.LuffySpriteSheet
import com.example.game.sprites.NamiSpriteSheet
import com.example.game.sprites.SanjiSpriteSheet
import com.example.game.sprites.UsoppSpriteSheet
import com.example.game.sprites.ZoroSpriteSheet
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Crisp 16-bit Pixel Art Canvas Renderer for Gomu Arena (Levels 1-3).
 * Renders environment, Luffy/Zoro/Sanji/Nami/Usopp/Chopper, enemies, bosses, projectiles, VFX, and particles.
 */
class EastBlueRenderer(
    val luffySprites: LuffySpriteSheet,
    val zoroSprites: ZoroSpriteSheet,
    val sanjiSprites: SanjiSpriteSheet,
    val namiSprites: NamiSpriteSheet,
    val usoppSprites: UsoppSpriteSheet,
    val chopperSprites: ChopperSpriteSheet,
    val envSprites: EastBlueSprites,
    val enemySprites: EnemySpriteSheet,
    val buggySprites: BuggyCrewSpriteSheet,
    val arlongSprites: ArlongParkSpriteSheet
) {
    // East Blue 16-bit Environment Palette
    private val skyBand0 = Color(0xFF2882DB)
    private val skyBand1 = Color(0xFF47A4EE)
    private val skyBand2 = Color(0xFF73C4FA)
    private val skyBand3 = Color(0xFFA8E2FF)
    private val skyHorizon = Color(0xFFD6F4FF)

    private val sunCore = Color(0xFFFFF8B8)
    private val sunGlow = Color(0xFFFFE066)
    private val sunHalo = Color(0x55FFF099)

    private val distantIsland = Color(0xFF5D9CC4)
    private val distantIslandShade = Color(0xFF477FA8)

    private val seaDeep = Color(0xFF10589C)
    private val seaMid = Color(0xFF1C75C4)
    private val seaBright = Color(0xFF2A97E0)
    private val seaShallow = Color(0xFF3CC2DE)
    private val seaFoam = Color(0xFFEBFBFF)

    private val sandHighlight = Color(0xFFFFE7A0)
    private val sandBase = Color(0xFFF2CC72)
    private val sandMid = Color(0xFFDCA84F)
    private val sandDeep = Color(0xFFB87F32)
    private val sandWet = Color(0xFFC9994D)

    private val grassBright = Color(0xFF56C952)
    private val grassMid = Color(0xFF32993D)
    private val grassDark = Color(0xFF1F6E32)

    private val woodLight = Color(0xFFC98F50)
    private val woodBase = Color(0xFF9C6631)
    private val woodDark = Color(0xFF633B1A)
    private val pixelInk = Color(0xFF181425)

    private val rubberSkin = Color(0xFFF2C37E)
    private val rubberShade = Color(0xFFCC8D43)
    private var currentLevelNumber: Int = 1

    fun DrawScope.renderWorld(world: WorldState) {
        currentLevelNumber = world.levelNumber
        val px = size.height / WorldConstants.CAMERA_VIEW_HEIGHT
        val viewW = size.width / px
        val camX = world.cameraX + world.shakeOffsetX
        val camY = WorldConstants.CAMERA_BASE_Y + world.shakeOffsetY
        val shakeY = world.shakeOffsetY
        val time = world.worldTime

        fun drawPxRect(vx: Float, vy: Float, vw: Float, vh: Float, color: Color) {
            val sx = floor(vx) * px
            val sy = floor(vy) * px
            val sw = (floor(vx + vw) - floor(vx)).coerceAtLeast(1f) * px
            val sh = (floor(vy + vh) - floor(vy)).coerceAtLeast(1f) * px
            drawRect(
                color = color,
                topLeft = Offset(sx, sy),
                size = Size(sw + 0.5f, sh + 0.5f)
            )
        }

        fun drawPxSprite(
            bitmap: ImageBitmap,
            vx: Float,
            vy: Float,
            scale: Float = 1f,
            alpha: Float = 1f,
            colorFilter: ColorFilter? = null
        ) {
            val dx = (floor(vx) * px).roundToInt()
            val dy = (floor(vy) * px).roundToInt()
            val dw = (bitmap.width * scale * px).roundToInt().coerceAtLeast(1)
            val dh = (bitmap.height * scale * px).roundToInt().coerceAtLeast(1)
            drawImage(
                image = bitmap,
                dstOffset = IntOffset(dx, dy),
                dstSize = IntSize(dw, dh),
                alpha = alpha,
                colorFilter = colorFilter,
                filterQuality = FilterQuality.None
            )
        }

        // === 1-7. STAGE ENVIRONMENT (LEVEL 1 EAST BLUE / LEVEL 2 ORANGE TOWN / LEVEL 3 ARLONG PARK) ===
        if (world.levelNumber == 3) {
            ArlongParkBackground.render(
                world = world,
                viewW = viewW,
                camX = camX,
                camY = camY,
                shakeY = shakeY,
                envSprites = envSprites,
                arlongSprites = arlongSprites,
                luffySprites = luffySprites,
                drawPxRect = ::drawPxRect,
                drawPxSprite = { bmp, vx, vy, sc, al -> drawPxSprite(bmp, vx, vy, sc, al) }
            )
        } else if (world.levelNumber == 2) {
            OrangeTownBackground.render(
                world = world,
                viewW = viewW,
                camX = camX,
                camY = camY,
                shakeY = shakeY,
                envSprites = envSprites,
                buggySprites = buggySprites,
                drawPxRect = ::drawPxRect,
                drawPxSprite = ::drawPxSprite
            )
        } else {
        // === 1. SUNNY EAST BLUE SKY ===
        drawPxRect(0f, 0f, viewW + 2f, 18f, skyBand0)
        drawPxRect(0f, 18f, viewW + 2f, 16f, skyBand1)
        drawPxRect(0f, 34f, viewW + 2f, 16f, skyBand2)
        drawPxRect(0f, 50f, viewW + 2f, 14f, skyBand3)
        drawPxRect(0f, 64f, viewW + 2f, 12f, skyHorizon)

        for (i in 0..(viewW.toInt() / 4 + 2)) {
            val dx = i * 4f
            drawPxRect(dx, 17f, 2f, 1f, skyBand1)
            drawPxRect(dx + 2f, 33f, 2f, 1f, skyBand2)
            drawPxRect(dx, 49f, 2f, 1f, skyBand3)
            drawPxRect(dx + 2f, 63f, 2f, 1f, skyHorizon)
        }

        val sunX = (viewW * 0.78f) - camX * 0.04f
        val sunY = 17f - shakeY * 0.2f
        val pulse = if (sin(time * 2.2f) > 0f) 1f else 0f
        drawPxRect(sunX - 11f - pulse, sunY - 11f - pulse, 22f + pulse * 2f, 22f + pulse * 2f, sunHalo)
        drawPxRect(sunX - 8f, sunY - 8f, 16f, 16f, sunGlow)
        drawPxRect(sunX - 6f, sunY - 6f, 12f, 12f, sunCore)

        // === 2. PARALLAX CLOUDS & SEAGULLS ===
        for (cloud in world.clouds) {
            val cx = cloud.x - camX * 0.25f
            if (cx > -55f && cx < viewW + 55f) {
                val sprite = if (cloud.variant == 0) envSprites.cloudLarge else envSprites.cloudSmall
                drawPxSprite(sprite, cx, cloud.y * 0.65f - shakeY * 0.2f)
            }
        }

        for (gull in world.seagulls) {
            val gx = gull.x - camX * 0.35f
            if (gx > -20f && gx < viewW + 20f) {
                val sprite = if (sin(gull.wingPhase) >= 0f) envSprites.seagullUp else envSprites.seagullDown
                val bobY = sin(gull.wingPhase) * 2.5f
                drawPxSprite(sprite, gx, gull.y * 0.72f + bobY - shakeY * 0.25f)
            }
        }

        // === 3. DISTANT ISLANDS ===
        val horizonY = 114f - camY
        val isl1X = 110f - camX * 0.22f
        drawPxRect(isl1X, horizonY - 9f, 44f, 9f, distantIsland)
        drawPxRect(isl1X + 8f, horizonY - 15f, 26f, 6f, distantIsland)
        drawPxRect(isl1X + 14f, horizonY - 19f, 12f, 4f, distantIslandShade)

        val isl2X = 360f - camX * 0.22f
        drawPxRect(isl2X, horizonY - 6f, 34f, 6f, distantIsland)
        drawPxRect(isl2X + 10f, horizonY - 10f, 14f, 4f, distantIslandShade)

        // === 4. ANIMATED SPARKLING SEA & PIRATE SHIP ===
        val seaTopY = 114f - camY
        drawPxRect(0f, seaTopY, viewW + 2f, 18f, seaDeep)
        drawPxRect(0f, seaTopY + 18f, viewW + 2f, 20f, seaMid)
        drawPxRect(0f, seaTopY + 38f, viewW + 2f, 20f, seaBright)
        drawPxRect(0f, seaTopY + 58f, viewW + 2f, 48f, seaShallow)

        val waveShiftSlow = ((time * 9f) % 24f)
        val waveShiftFast = ((time * 16f) % 18f)
        for (row in 0..4) {
            val wy = seaTopY + 5f + row * 12f
            val spacing = if (row < 2) 24f else 18f
            val shift = if (row % 2 == 0) waveShiftSlow else -waveShiftFast
            val parallax = 0.35f + row * 0.08f
            val startIdx = ((camX * parallax) / spacing).toInt() - 2
            val endIdx = startIdx + (viewW / spacing).toInt() + 5
            for (i in startIdx..endIdx) {
                val wx = i * spacing - camX * parallax + shift
                val shimmerLen = if ((i + row) % 3 == 0) 8f else 5f
                val color = if (row >= 3) seaFoam else Color(0x88CCF5FF)
                drawPxRect(wx, wy, shimmerLen, 1.5f, color)
            }
        }

        val shipWorldX = 435f - camX * 0.48f
        val shipBobY = sin(time * 2.1f) * 2.4f
        val shipY = seaTopY - 12f + shipBobY
        if (shipWorldX > -70f && shipWorldX < viewW + 70f) {
            drawPxSprite(envSprites.pirateShip, shipWorldX, shipY, scale = 1.15f)
            val wakeW = 36f + sin(time * 3.5f) * 4f
            drawPxRect(shipWorldX + 10f, shipY + 42f, wakeW, 2f, seaFoam)
            drawPxRect(shipWorldX + 16f, shipY + 45f, wakeW * 0.65f, 1.5f, Color(0x99EBFBFF))
        }

        // === 5. SUN-DRENCHED SANDY BEACH & SURF ===
        val beachEndScreenX = WorldConstants.PIER_START_X + 26f - camX
        val beachTopY = 166f - camY

        val surfSlide = sin(time * 2.8f) * 3.5f
        val surfX = beachEndScreenX - 4f + surfSlide
        drawPxRect(surfX - 4f, beachTopY + 2f, 18f, 60f, sandWet)
        drawPxRect(surfX + 8f, beachTopY + 1f, 5f, 60f, seaFoam)

        if (beachEndScreenX > -10f) {
            val beachW = (beachEndScreenX + 8f).coerceAtLeast(0f)
            drawPxRect(-2f, beachTopY, beachW, 6f, sandHighlight)
            drawPxRect(-2f, beachTopY + 6f, beachW, 24f, sandBase)
            drawPxRect(-2f, beachTopY + 30f, beachW, 38f, sandMid)
            drawPxRect(-2f, beachTopY + 46f, beachW, 24f, sandDeep)

            for (gx in 12..490 step 19) {
                val sx = gx - camX
                if (sx > -10f && sx < viewW + 10f && sx < beachEndScreenX - 4f) {
                    val py1 = beachTopY + 10f + ((gx * 7) % 14)
                    val py2 = beachTopY + 28f + ((gx * 13) % 18)
                    drawPxRect(sx, py1, 2f, 1f, sandMid)
                    drawPxRect(sx + 5f, py1 + 4f, 1.5f, 1f, sandHighlight)
                    drawPxRect(sx + 2f, py2, 3f, 1.5f, sandDeep)
                }
            }
        }

        val grassEndX = 104f - camX
        if (grassEndX > -10f) {
            drawPxRect(-2f, beachTopY - 3f, grassEndX, 6f, grassBright)
            drawPxRect(-2f, beachTopY + 1f, grassEndX - 6f, 5f, grassMid)
            drawPxRect(-2f, beachTopY + 5f, grassEndX - 14f, 3f, grassDark)
            for (bx in 8..96 step 12) {
                val sx = bx - camX
                if (sx > -8f && sx < viewW + 8f) {
                    drawPxRect(sx, beachTopY - 6f, 2f, 3f, grassBright)
                    drawPxRect(sx + 3f, beachTopY - 5f, 2f, 2f, grassMid)
                }
            }
        }

        // === 6. PALM TREES & BEACH PROPS ===
        val palmConfigs = listOf(
            Triple(34f, true, 1.15f),
            Triple(122f, false, 1.0f),
            Triple(286f, true, 1.08f),
            Triple(426f, false, 1.02f)
        )
        for ((pxWorld, faceRight, scale) in palmConfigs) {
            val sx = pxWorld - camX
            if (sx > -60f && sx < viewW + 60f) {
                val sway = sin(time * 1.9f + pxWorld * 0.05f) * 1.2f
                val sprite = if (faceRight) envSprites.palmTreeRight else envSprites.palmTreeLeft
                val topY = beachTopY + 6f - (sprite.height * scale)
                drawPxSprite(sprite, sx + sway * 0.5f, topY, scale = scale)
            }
        }

        val rock1X = 76f - camX
        if (rock1X > -30f && rock1X < viewW + 30f) drawPxSprite(envSprites.rock, rock1X, beachTopY + 1f)
        val barrelBeachX = 178f - camX
        if (barrelBeachX > -25f && barrelBeachX < viewW + 25f) drawPxSprite(envSprites.barrel, barrelBeachX, beachTopY - 5f)
        val starX = 354f - camX
        if (starX > -20f && starX < viewW + 20f) drawPxSprite(envSprites.starfish, starX, beachTopY + 26f)
        val rock2X = 456f - camX
        if (rock2X > -30f && rock2X < viewW + 30f) drawPxSprite(envSprites.rock, rock2X, beachTopY + 2f)

        // === 7. WOODEN EAST BLUE PIER ===
        val pierStartScreen = WorldConstants.PIER_START_X - camX
        val pierEndScreen = WorldConstants.PIER_END_X - camX
        val pierDeckY = WorldConstants.PIER_GROUND_Y - camY

        if (pierEndScreen > -20f && pierStartScreen < viewW + 20f) {
            val pierW = pierEndScreen - pierStartScreen

            var pilingX = WorldConstants.PIER_START_X + 18f
            while (pilingX <= WorldConstants.PIER_END_X - 8f) {
                val sx = pilingX - camX
                drawPxRect(sx - 1f, pierDeckY + 5f, 10f, 44f, pixelInk)
                drawPxRect(sx, pierDeckY + 5f, 8f, 44f, woodDark)
                drawPxRect(sx + 1f, pierDeckY + 5f, 4f, 44f, woodBase)
                drawPxRect(sx + 1f, pierDeckY + 5f, 2f, 40f, woodLight)
                val waterLineY = pierDeckY + 24f + sin(time * 3f + pilingX * 0.1f) * 1.2f
                drawPxRect(sx - 3f, waterLineY, 14f, 2f, seaFoam)
                drawPxRect(sx - 1f, pierDeckY - 7f, 10f, 8f, pixelInk)
                drawPxRect(sx, pierDeckY - 6f, 8f, 6f, woodBase)
                drawPxRect(sx, pierDeckY - 6f, 6f, 2f, woodLight)
                pilingX += 46f
            }

            drawPxRect(pierStartScreen, pierDeckY + 5f, pierW, 6f, pixelInk)
            drawPxRect(pierStartScreen + 1f, pierDeckY + 6f, pierW - 2f, 4f, woodDark)

            drawPxRect(pierStartScreen - 1f, pierDeckY - 1f, pierW + 2f, 8f, pixelInk)
            drawPxRect(pierStartScreen, pierDeckY, pierW, 2f, woodLight)
            drawPxRect(pierStartScreen, pierDeckY + 2f, pierW, 4f, woodBase)

            var plankX = WorldConstants.PIER_START_X + 8f
            while (plankX < WorldConstants.PIER_END_X - 4f) {
                val sx = plankX - camX
                drawPxRect(sx, pierDeckY, 1f, 6f, woodDark)
                plankX += 10f
            }

            drawPxRect(pierStartScreen - 10f, pierDeckY + 2f, 12f, 5f, pixelInk)
            drawPxRect(pierStartScreen - 9f, pierDeckY + 3f, 10f, 3f, woodLight)

            drawPxSprite(envSprites.crate, 668f - camX, pierDeckY - 15f)
            drawPxSprite(envSprites.barrel, 690f - camX, pierDeckY - 14f)
        }
        } // end Level 1 East Blue environment

        // === 8. BATTLE AXE / BUGGY BALL GROUND CRATERS ===
        for (crater in world.craters) {
            val alpha = (1f - (crater.age / crater.maxAge)).coerceIn(0f, 1f)
            val cx = crater.x - camX
            val cy = crater.y - camY
            val crackColor = Color(0xFF3A2414).copy(alpha = alpha * 0.85f)
            val highlightCrack = Color(0xFF6B4226).copy(alpha = alpha * 0.7f)
            drawPxRect(cx - 16f, cy - 1f, 32f, 3f, crackColor)
            drawPxRect(cx - 12f, cy + 2f, 24f, 2f, crackColor)
            drawPxRect(cx - 22f, cy, 7f, 1.5f, highlightCrack)
            drawPxRect(cx + 15f, cy, 7f, 1.5f, highlightCrack)
            drawPxRect(cx - 8f, cy + 4f, 4f, 2f, crackColor)
            drawPxRect(cx + 5f, cy + 3f, 5f, 2f, crackColor)
        }

        // === 9. LOOT ITEMS (MEAT ON THE BONE & GOLDEN BERRY COINS) ===
        for (item in world.lootItems) {
            val sx = item.x - camX
            val sy = item.y - camY
            if (sx < -24f || sx > viewW + 24f) continue
            val bob = sin(time * 5.2f + item.id) * 1.6f
            val blinkAlpha = if (item.age > item.maxAge - 3f && ((time * 12f).toInt() % 2 == 0)) 0.35f else 1f
            drawPxRect(sx - 5f, sy - 1f, 10f, 2f, Color(0x55181425))
            val sprite = when (item.kind) {
                LootKind.MEAT_ON_BONE -> enemySprites.meatOnBone
                LootKind.BERRY_COIN -> enemySprites.berryCoin
            }
            drawPxSprite(
                sprite,
                sx - sprite.width * 0.5f,
                sy - sprite.height - 1f + bob,
                1f,
                blinkAlpha
            )
        }

        // === 10. ENEMIES & BOSSES (LEVEL 1 MARINES, LEVEL 2 BUGGY PIRATES, LEVEL 3 ARLONG FISHMEN) ===
        for (enemy in world.enemies) {
            val sx = enemy.x - camX
            if (sx < -55f || sx > viewW + 55f) continue
            renderEnemy(
                enemy = enemy,
                levelNumber = world.levelNumber,
                isArlongMapRoom = world.isArlongMapRoom,
                camX = camX,
                camY = camY,
                worldTime = time,
                drawPxRect = ::drawPxRect,
                drawPxSprite = { bmp, vx, vy, sc, al -> drawPxSprite(bmp, vx, vy, sc, al) }
            )
        }

        // === 10.2. BUGGY BARA-BARA FESTIVAL FLOATING BODY PARTS ===
        for (part in world.buggyParts) {
            val sx = part.x - camX
            val sy = part.y - camY
            if (sx < -32f || sx > viewW + 32f) continue
            val isRight = part.facing == FacingDirection.RIGHT
            val sprite = when (part.kind) {
                BuggyPartKind.HEAD -> buggySprites.buggyPartHead.forFacing(isRight)
                BuggyPartKind.TORSO -> buggySprites.buggyPartTorso.forFacing(isRight)
                BuggyPartKind.HAND_LEFT, BuggyPartKind.HAND_RIGHT -> buggySprites.buggyPartHand.forFacing(isRight)
            }
            // Subtle cyan Bara-Bara glow behind floating body part
            drawPxRect(sx - 6f, sy - 4f, 12f, 8f, Color(0x445CE1FF))
            drawPxSprite(sprite, sx - sprite.width * 0.5f, sy - sprite.height * 0.5f, 1f, 1f)
        }

        // === 10.5. ZORO ONIGIRI & PURGATORY AFTERIMAGES ===
        val zoroGhostTint = ColorFilter.tint(Color(0xFF2EE66B), BlendMode.SrcAtop)
        val zoroHellGhostTint = ColorFilter.tint(Color(0xFF143D26), BlendMode.SrcAtop)
        for (ghost in world.zoroAfterimages) {
            val t = (ghost.age / ghost.maxAge).coerceIn(0f, 1f)
            val alpha = ((1f - t) * (if (ghost.isKingOfHell) 0.70f else 0.56f)).coerceIn(0f, 1f)
            val sprite = zoroSprites.onigiriDash.forFacing(ghost.facing == FacingDirection.RIGHT)
            val gx = floor(ghost.x - camX - sprite.width * 0.5f)
            val gy = floor(ghost.y - camY - sprite.height + 2f)
            if (ghost.isKingOfHell) {
                drawPxRect(gx - 2f, gy + 4f, sprite.width + 4f, sprite.height - 6f, Color(0x4439FF88).copy(alpha = alpha * 0.65f))
            }
            drawPxSprite(sprite, gx, gy, 1f, alpha, if (ghost.isKingOfHell) zoroHellGhostTint else zoroGhostTint)
        }

        // === 10.6. SANJI SKY WALK (GEPPO) WHITE PIXEL AIR RINGS ===
        for (ring in world.skyWalkRings) {
            val t = (ring.age / ring.maxAge).coerceIn(0f, 1f)
            val alpha = (1f - t * t).coerceIn(0f, 1f)
            val rx = ring.x - camX
            val ry = ring.y - camY + t * 4f
            val halfW = 8f + t * 14f
            val halfH = 2.5f + t * 3.5f
            val outerColor = Color(0xFFA6F0FF).copy(alpha = alpha * 0.85f)
            val coreColor = Color(0xFFFFFFFF).copy(alpha = alpha)
            // Pixel oval ring (top/bottom edges + side caps)
            drawPxRect(rx - halfW, ry - halfH, halfW * 2f, 2f, outerColor)
            drawPxRect(rx - halfW, ry + halfH - 1f, halfW * 2f, 2f, outerColor)
            drawPxRect(rx - halfW - 2f, ry - halfH + 1f, 3f, halfH * 2f - 1f, coreColor)
            drawPxRect(rx + halfW - 1f, ry - halfH + 1f, 3f, halfH * 2f - 1f, coreColor)
            drawPxRect(rx - halfW * 0.7f, ry - halfH + 1f, halfW * 1.4f, 1.5f, coreColor)
        }

        // === 11. ACTIVE HERO (LUFFY, ZORO, SANJI, NAMI, OR USOPP) ===
        when (world.luffy.activeHero) {
            PlayableHero.ZORO -> {
                renderZoroAndSantoryu(
                    player = world.luffy,
                    camX = camX,
                    camY = camY,
                    worldTime = time,
                    drawPxRect = ::drawPxRect,
                    drawPxSprite = { bmp, vx, vy, sc, al -> drawPxSprite(bmp, vx, vy, sc, al) }
                )
            }
            PlayableHero.SANJI -> {
                renderSanjiAndBlackLeg(
                    player = world.luffy,
                    camX = camX,
                    camY = camY,
                    worldTime = time,
                    drawPxRect = ::drawPxRect,
                    drawPxSprite = { bmp, vx, vy, sc, al -> drawPxSprite(bmp, vx, vy, sc, al) }
                )
            }
            PlayableHero.NAMI -> {
                renderNamiAndClimaTact(
                    player = world.luffy,
                    camX = camX,
                    camY = camY,
                    worldTime = time,
                    drawPxRect = ::drawPxRect,
                    drawPxSprite = { bmp, vx, vy, sc, al -> drawPxSprite(bmp, vx, vy, sc, al) }
                )
            }
            PlayableHero.USOPP -> {
                renderUsoppAndKabuto(
                    player = world.luffy,
                    camX = camX,
                    camY = camY,
                    worldTime = time,
                    drawPxRect = ::drawPxRect,
                    drawPxSprite = { bmp, vx, vy, sc, al -> drawPxSprite(bmp, vx, vy, sc, al) }
                )
            }
            PlayableHero.CHOPPER -> {
                renderChopperAndRumble(
                    player = world.luffy,
                    camX = camX,
                    camY = camY,
                    worldTime = time,
                    drawPxRect = ::drawPxRect,
                    drawPxSprite = { bmp, vx, vy, sc, al -> drawPxSprite(bmp, vx, vy, sc, al) }
                )
            }
            PlayableHero.LUFFY -> {
                renderLuffyAndGomuLimbs(
                    luffy = world.luffy,
                    camX = camX,
                    camY = camY,
                    worldTime = time,
                    drawPxRect = ::drawPxRect,
                    drawPxSprite = { bmp, vx, vy, sc, al -> drawPxSprite(bmp, vx, vy, sc, al) }
                )
            }
        }

        // Translucent pool water surface overlay in front of active hero when inside Arlong Park pool
        if (world.levelNumber == 3 && !world.isArlongMapRoom &&
            world.luffy.x in WorldConstants.ARLONG_POOL_LEFT_X..WorldConstants.ARLONG_POOL_RIGHT_X &&
            world.luffy.y > WorldConstants.BEACH_GROUND_Y - 1f
        ) {
            val pxHero = world.luffy.x - camX
            val waterTop = WorldConstants.BEACH_GROUND_Y - camY + 3f
            val submergeH = (world.luffy.y - WorldConstants.BEACH_GROUND_Y).coerceIn(2f, 22f)
            drawPxRect(pxHero - 12f, waterTop, 24f, submergeH + 4f, Color(0x771A96DE))
            drawPxRect(pxHero - 13f, waterTop, 26f, 2f, Color(0xCCE8FDFF))
        }

        // === 11.5. CHARACTER TAG-IN FLASH & COMBAT SLASH / FLAME VFX ===
        if (world.luffy.switchFlashTimer > 0f) {
            val flashT = (world.luffy.switchFlashTimer / 0.24f).coerceIn(0f, 1f)
            val fx = world.luffy.x - camX
            val fy = world.luffy.y - camY - 15f
            val flashColor = when (world.luffy.activeHero) {
                PlayableHero.LUFFY -> Color(0xFFFFE566)
                PlayableHero.ZORO -> Color(0xFF5CF289)
                PlayableHero.SANJI -> Color(0xFFFF8A1F)
                PlayableHero.NAMI -> Color(0xFF5CE1FF)
                PlayableHero.USOPP -> Color(0xFFFFD84D)
                PlayableHero.CHOPPER -> Color(0xFFFF78B4)
            }
            val r = 10f + (1f - flashT) * 18f
            drawPxRect(fx - r, fy - 1.5f, r * 2f, 3f, flashColor.copy(alpha = flashT * 0.85f))
            drawPxRect(fx - 1.5f, fy - r, 3f, r * 2f, flashColor.copy(alpha = flashT * 0.85f))
            val inner = r * 0.55f
            drawPxRect(fx - inner, fy - inner, inner * 2f, inner * 2f, Color.White.copy(alpha = flashT * 0.72f))
        }

        for (slash in world.slashVfxs) {
            renderSwordSlashVfx(
                slash = slash,
                camX = camX,
                camY = camY,
                drawPxRect = ::drawPxRect
            )
        }

        // === 11.7. PLAYER PROJECTILES (NAMI SPARK/CYCLONE & USOPP PELLET/EXPLODING STAR/FIREBIRD PHOENIX!) ===
        for (pProj in world.playerProjectiles) {
            val sx = pProj.x - camX
            val sy = pProj.y - camY
            if (sx < -52f || sx > viewW + 52f) continue
            val isRight = pProj.facing == FacingDirection.RIGHT
            when (pProj.kind) {
                PlayerProjectileKind.NAMI_ELECTRIC_BOLT -> {
                    val bmp = namiSprites.electricBolt.forFacing(isRight)
                    drawPxRect(sx - 8f, sy - 4f, 16f, 8f, Color(0x557BE8FF))
                    drawPxSprite(bmp, sx - bmp.width * 0.5f, sy - bmp.height * 0.5f, 1f, 1f)
                }
                PlayerProjectileKind.NAMI_CYCLONE -> {
                    val idx = ((time * 18f).toInt()) % namiSprites.cycloneFrames.size
                    val bmp = namiSprites.cycloneFrames[idx]
                    val bob = sin(time * 24f) * 1.5f
                    drawPxRect(sx - 13f, sy - 11f, 26f, 22f, Color(0x447BE8FF))
                    drawPxSprite(bmp, sx - bmp.width * 0.5f, sy - bmp.height * 0.5f + bob, 1.15f, 0.96f)
                }
                PlayerProjectileKind.USOPP_PELLET -> {
                    val bmp = usoppSprites.metalPellet.forFacing(isRight)
                    drawPxSprite(bmp, sx - bmp.width * 0.5f, sy - bmp.height * 0.5f, 1f, 1f)
                }
                PlayerProjectileKind.USOPP_EXPLODING_STAR -> {
                    val pulse = if (((time * 20f).toInt() % 2) == 0) 2f else 0f
                    drawPxRect(sx - 7f - pulse, sy - 7f - pulse, 14f + pulse * 2f, 14f + pulse * 2f, Color(0x66FF6B1A))
                    val bmp = usoppSprites.explodingStar
                    drawPxSprite(bmp, sx - bmp.width * 0.5f, sy - bmp.height * 0.5f, 1f, 1f)
                }
                PlayerProjectileKind.USOPP_FIREBIRD_STAR -> {
                    // Giant blazing pixel phoenix soaring across the screen!
                    val idx = ((time * 16f).toInt()) % usoppSprites.firebirdFrames.size
                    val bmp = usoppSprites.firebirdFrames[idx].forFacing(isRight)
                    val pulse = sin(time * 28f) * 2.5f
                    drawPxRect(sx - 20f - pulse, sy - 12f, 40f + pulse * 2f, 24f, Color(0x44FF6B1A))
                    drawPxRect(sx - 14f, sy - 8f, 28f, 16f, Color(0x55FFE45E))
                    drawPxSprite(bmp, sx - bmp.width * 0.5f, sy - bmp.height * 0.5f, 1.15f, 1f)
                }
                PlayerProjectileKind.ZORO_360_POUND_CANNON -> {
                    // Sharp emerald crescent air-pressure wave ("360 Pound Cannon")
                    val dir = if (isRight) 1f else -1f
                    val outer = Color(0xCC22C55E)
                    val bright = Color(0xEE66FFAA)
                    val core = Color(0xFFFFFFFF)
                    val steps = 10
                    for (i in 0..steps) {
                        val u = i.toFloat() / steps
                        val deg = -68f + 136f * u
                        val rad = deg * (PI.toFloat() / 180f)
                        val ax = sx + dir * (cos(rad) * 11f - 3f)
                        val ay = sy + sin(rad) * 14f
                        val taper = sin(u * PI.toFloat()).coerceIn(0.3f, 1f)
                        val oSz = 5.5f * taper
                        val mSz = 3.2f * taper
                        drawPxRect(ax - oSz * 0.5f, ay - oSz * 0.5f, oSz, oSz, outer)
                        drawPxRect(ax - mSz * 0.5f, ay - mSz * 0.5f, mSz, mSz, bright)
                        drawPxRect(ax - 1f, ay - 1f, 2f, 2f, core)
                    }
                    // Trailing air-blade streaks
                    drawPxRect(sx - dir * 12f - 4f, sy - 7f, 8f, 2f, bright)
                    drawPxRect(sx - dir * 15f - 5f, sy - 1f, 10f, 2f, core)
                    drawPxRect(sx - dir * 12f - 4f, sy + 5f, 8f, 2f, bright)
                }
                PlayerProjectileKind.ZORO_HELL_WAVE -> {
                    // Massive King of Hell black-and-emerald Conqueror Haki crescent wave!
                    val dir = if (isRight) 1f else -1f
                    val obsidian = Color(0xEE141821)
                    val hellGreen = Color(0xFF39FF88)
                    val neonMint = Color(0xFFE8FFF2)
                    val steps = 14
                    for (i in 0..steps) {
                        val u = i.toFloat() / steps
                        val deg = -74f + 148f * u
                        val rad = deg * (PI.toFloat() / 180f)
                        val ax = sx + dir * (cos(rad) * 15f - 4f)
                        val ay = sy + sin(rad) * 19f
                        val taper = sin(u * PI.toFloat()).coerceIn(0.32f, 1f)
                        val oSz = 8.0f * taper
                        val mSz = 4.8f * taper
                        val cSz = 2.2f * taper
                        drawPxRect(ax - oSz * 0.5f, ay - oSz * 0.5f, oSz, oSz, obsidian)
                        drawPxRect(ax - mSz * 0.5f, ay - mSz * 0.5f, mSz, mSz, hellGreen)
                        drawPxRect(ax - cSz * 0.5f, ay - cSz * 0.5f, cSz, cSz, neonMint)
                    }
                    // Jagged black-green Haki trailing wisps
                    val jitter = if (((time * 36f).toInt() % 2) == 0) 2f else -2f
                    drawPxRect(sx - dir * 16f - 6f, sy - 10f + jitter, 12f, 2.5f, obsidian)
                    drawPxRect(sx - dir * 18f - 7f, sy - 1f, 14f, 3f, hellGreen)
                    drawPxRect(sx - dir * 16f - 6f, sy + 8f - jitter, 12f, 2.5f, obsidian)
                }
            }
        }

        // === 11.75. ASURA DELAYED SLASH LOCK-ON CUT LINES ===
        for (ds in world.delayedSlashes) {
            val dx = ds.x - camX
            val dy = ds.y - camY
            val charge = (ds.age / ds.triggerTime).coerceIn(0f, 1f)
            val pulse = if (((time * 40f).toInt() % 2) == 0) 1f else 0.65f
            val r = 10f + charge * 10f
            val violet = Color(0xFFB55CFF).copy(alpha = 0.85f * pulse)
            val white = Color(0xFFFFFFFF).copy(alpha = pulse)
            for (s in -4..4) {
                val u = s / 4f
                drawPxRect(dx + u * r - 1.5f, dy + u * r * 0.75f - 1.5f, 3f, 3f, violet)
                drawPxRect(dx + u * r - 1.5f, dy - u * r * 0.75f - 1.5f, 3f, 3f, violet)
            }
            drawPxRect(dx - 3f, dy - 1f, 6f, 2f, white)
            drawPxRect(dx - 1f, dy - 3f, 2f, 6f, white)
        }

        // === 11.8. NAMI THUNDERBOLT TEMPO STORM CLOUDS & MASSIVE JAGGED PIXEL LIGHTNING BOLT ===
        for (cloud in world.thunderClouds) {
            val cx = cloud.x - camX
            val cy = cloud.cloudY - camY
            val gy = cloud.groundY - camY
            if (cx < -64f || cx > viewW + 64f) continue
            val fadeOut = if (cloud.age > cloud.maxAge - 0.24f) {
                ((cloud.maxAge - cloud.age) / 0.24f).coerceIn(0f, 1f)
            } else {
                1f
            }

            // Target warning ring on ground before lightning crashes down
            if (!cloud.strikeTriggered) {
                val chargeT = (cloud.age / cloud.strikeTime).coerceIn(0f, 1f)
                val pulse = if (((time * 28f).toInt() % 2) == 0) 1f else 0.45f
                drawPxRect(cx - 28f * chargeT, gy - 2f, 56f * chargeT, 3f, Color(0xFF7BE8FF).copy(alpha = 0.75f * pulse))
            }

            // Massive jagged multi-segment lightning bolt right after strikeTime!
            if (cloud.strikeTriggered) {
                val boltAge = cloud.age - cloud.strikeTime
                if (boltAge in 0f..0.42f) {
                    val boltAlpha = (1f - boltAge / 0.42f).coerceIn(0f, 1f)
                    val cyanGlow = Color(0xFF38BDF8).copy(alpha = boltAlpha * 0.65f)
                    val goldEdge = Color(0xFFFFF066).copy(alpha = boltAlpha * 0.92f)
                    val whiteCore = Color(0xFFFFFFFF).copy(alpha = boltAlpha)
                    val segCount = 8
                    val topY = cy + 12f
                    val stepY = ((gy - topY) / segCount).coerceAtLeast(4f)
                    val jitterSeed = ((time * 40f).toInt())
                    var prevX = cx
                    for (s in 0 until segCount) {
                        val y0 = topY + s * stepY
                        val jag = if (s == segCount - 1) 0f else (if ((s + jitterSeed) % 2 == 0) 7f else -7f)
                        val curX = cx + jag
                        val minX = minOf(prevX, curX)
                        val spanW = kotlin.math.abs(curX - prevX) + 6f
                        // Wide cyan electric aura
                        drawPxRect(minX - 6f, y0 - 1f, spanW + 12f, stepY + 2f, cyanGlow)
                        // Golden outer bolt
                        drawPxRect(minX - 3f, y0, spanW + 6f, stepY + 1f, goldEdge)
                        // White-hot lightning core
                        drawPxRect(minX - 1f, y0, spanW + 2f, stepY + 1f, whiteCore)
                        prevX = curX
                    }
                    // Electric blast ring at ground impact
                    val blastR = 14f + (1f - boltAlpha) * 28f
                    drawPxRect(cx - blastR, gy - 5f, blastR * 2f, 6f, cyanGlow)
                    drawPxRect(cx - blastR * 0.6f, gy - 3f, blastR * 1.2f, 4f, whiteCore)
                }
            }

            // Draw dark pixel storm cloud hovering overhead
            val cloudSprite = namiSprites.thunderCloud
            val bob = sin(time * 8f + cloud.id) * 1.5f
            drawPxSprite(
                cloudSprite,
                cx - cloudSprite.width * 0.55f,
                cy - cloudSprite.height * 0.5f + bob,
                1.1f,
                fadeOut
            )
        }

        // === 12. ENEMY PROJECTILES (BULLETS, SHOCKWAVES, KNIVES, FLAMES, BUGGY FIST, BUGGY BALL & WATER BULLET) ===
        for (proj in world.projectiles) {
            val sx = proj.x - camX
            val sy = proj.y - camY
            if (sx < -36f || sx > viewW + 36f) continue
            val isRight = proj.facing == FacingDirection.RIGHT
            val sprite = when (proj.kind) {
                ProjectileKind.RIFLE_BULLET -> enemySprites.rifleBullet.forFacing(isRight)
                ProjectileKind.MORGAN_SHOCKWAVE -> enemySprites.morganShockwave.forFacing(isRight)
                ProjectileKind.THROWING_KNIFE -> buggySprites.throwingKnife.forFacing(isRight)
                ProjectileKind.CABAJI_FLAME -> buggySprites.cabajiFlame.forFacing(isRight)
                ProjectileKind.BUGGY_FIST -> buggySprites.buggyFist.forFacing(isRight)
                ProjectileKind.BUGGY_BALL -> buggySprites.buggyBall
                ProjectileKind.WATER_BULLET -> arlongSprites.waterBullet.forFacing(isRight)
            }
            if (proj.kind == ProjectileKind.BUGGY_BALL) {
                // Pulsing danger halo around the crimson Buggy Ball
                val pulse = if (((time * 18f).toInt() % 2) == 0) 2f else 0f
                drawPxRect(sx - 9f - pulse, sy - 9f - pulse, 18f + pulse * 2f, 18f + pulse * 2f, Color(0x55FF3B30))
            } else if (proj.kind == ProjectileKind.WATER_BULLET) {
                drawPxRect(sx - 7f, sy - 4f, 14f, 8f, Color(0x5547DBFF))
            }
            drawPxSprite(
                sprite,
                sx - sprite.width * 0.5f,
                sy - sprite.height * 0.5f,
                1f,
                1f
            )
        }

        // === 13. PIXEL PARTICLES & SHOCKWAVE RINGS ===
        for (p in world.particles) {
            val sx = p.x - camX
            val sy = p.y - camY
            val t = (p.age / p.maxAge).coerceIn(0f, 1f)
            val alpha = (1f - t).coerceIn(0f, 1f)
            val color = p.color.copy(alpha = alpha)

            when (p.kind) {
                ParticleKind.SHOCKWAVE_RING -> {
                    val h = p.size * (1.25f + t * 0.8f)
                    val w = 4f + (1f - t) * 4f
                    drawPxRect(sx - w * 0.5f, sy - h, w, h, color)
                    drawPxRect(sx - w * 0.5f + 2f, sy - h * 0.75f, 2f, h * 0.75f, Color.White.copy(alpha = alpha))
                }
                ParticleKind.WIND_STREAK -> {
                    val w = p.size * p.stretchX
                    val h = if (p.stretchX < 1f) p.size * 4.5f else p.size
                    drawPxRect(sx - w * 0.5f, sy - h * 0.5f, w, h, color)
                }
                ParticleKind.IMPACT_SPARK -> {
                    drawPxRect(sx - p.size * 0.5f, sy - p.size * 0.5f, p.size, p.size, color)
                    drawPxRect(sx - 1f, sy - p.size, 2f, p.size * 2f, color)
                    drawPxRect(sx - p.size, sy - 1f, p.size * 2f, 2f, color)
                }
                ParticleKind.MAP_PAPER -> {
                    // Fluttering nautical chart parchment sheet from Nami's Map Room!
                    val tilt = if (sin(p.age * 11f + p.x * 0.1f) > 0f) 1f else -1f
                    val pw = (p.size * 1.4f).coerceAtLeast(4f)
                    val ph = (p.size * 0.95f).coerceAtLeast(3f)
                    drawPxRect(sx - pw * 0.5f - 0.5f, sy - ph * 0.5f - 0.5f, pw + 1f, ph + 1f, pixelInk.copy(alpha = alpha * 0.7f))
                    drawPxRect(sx - pw * 0.5f, sy - ph * 0.5f, pw, ph, color)
                    drawPxRect(sx - pw * 0.25f + tilt * 0.5f, sy - 0.5f, pw * 0.5f, 1f, Color(0xFF9C7A4C).copy(alpha = alpha))
                }
                ParticleKind.BUBBLE -> {
                    // Hollow cyan pixel air bubble rising from the Arlong Park pool
                    val s = p.size.coerceAtLeast(3f)
                    drawPxRect(sx - s * 0.5f, sy - s * 0.5f, s, 1f, color)
                    drawPxRect(sx - s * 0.5f, sy + s * 0.5f - 1f, s, 1f, color)
                    drawPxRect(sx - s * 0.5f, sy - s * 0.5f, 1f, s, color)
                    drawPxRect(sx + s * 0.5f - 1f, sy - s * 0.5f, 1f, s, color)
                    drawPxRect(sx - s * 0.5f + 1f, sy - s * 0.5f + 1f, 1f, 1f, Color.White.copy(alpha = alpha))
                }
                ParticleKind.HEAL_CROSS -> {
                    // Crisp green pixel medical cross (+) floating up from Chopper's Heal!
                    val arm = p.size.coerceAtLeast(4f)
                    val thick = (arm * 0.42f).coerceAtLeast(2f)
                    val outline = Color(0xFF14422A).copy(alpha = alpha * 0.85f)
                    drawPxRect(sx - arm * 0.5f - 0.5f, sy - thick * 0.5f - 0.5f, arm + 1f, thick + 1f, outline)
                    drawPxRect(sx - thick * 0.5f - 0.5f, sy - arm * 0.5f - 0.5f, thick + 1f, arm + 1f, outline)
                    drawPxRect(sx - arm * 0.5f, sy - thick * 0.5f, arm, thick, color)
                    drawPxRect(sx - thick * 0.5f, sy - arm * 0.5f, thick, arm, color)
                    drawPxRect(sx - 0.8f, sy - 0.8f, 1.6f, 1.6f, Color.White.copy(alpha = alpha))
                }
                else -> {
                    val s = p.size * (if (p.kind == ParticleKind.DUST) 1f + t * 0.45f else 1f)
                    drawPxRect(sx - s * 0.5f, sy - s * 0.5f, s, s, color)
                }
            }
        }

        // === 14. FLOATING PIXEL DAMAGE / HEAL / BERRY NUMBERS ===
        for (ft in world.floatingTexts) {
            val t = (ft.age / ft.maxAge).coerceIn(0f, 1f)
            val alpha = if (t < 0.65f) 1f else (1f - (t - 0.65f) / 0.35f).coerceIn(0f, 1f)
            val pixelSize = if (ft.isLarge) 1.5f else 1.0f
            PixelBitmapText.drawCentered(
                text = ft.text,
                centerX = ft.x - camX,
                topY = ft.y - camY,
                pixelSize = pixelSize,
                color = ft.color,
                alpha = alpha,
                drawPxRect = ::drawPxRect
            )
        }

        // === 15. OPTIONAL HITBOX / HURTBOX DEBUG OVERLAY ===
        if (world.showHitboxes) {
            val hb = world.luffy.hurtbox
            drawPxRect(hb.x - camX, hb.y - camY, hb.width, hb.height, Color(0x4438D966))
            drawPxRect(hb.x - camX, hb.y - camY, hb.width, 1f, Color(0xFF38D966))
            drawPxRect(hb.x - camX, hb.bottom - camY - 1f, hb.width, 1f, Color(0xFF38D966))

            for (enemy in world.enemies) {
                val eb = enemy.hurtbox
                drawPxRect(eb.x - camX, eb.y - camY, eb.width, eb.height, Color(0x44FFAA22))
                drawPxRect(eb.x - camX, eb.y - camY, eb.width, 1f, Color(0xFFFFAA22))
                drawPxRect(eb.x - camX, eb.bottom - camY - 1f, eb.width, 1f, Color(0xFFFFAA22))
            }

            for (proj in world.projectiles) {
                val pb = proj.bounds
                drawPxRect(pb.x - camX, pb.y - camY, pb.width, pb.height, Color(0x55FF3B30))
            }

            for (pProj in world.playerProjectiles) {
                val pb = pProj.bounds
                drawPxRect(pb.x - camX, pb.y - camY, pb.width, pb.height, Color(0x665CE1FF))
            }

            for (box in world.activeHitboxes) {
                val b = box.bounds
                drawPxRect(b.x - camX, b.y - camY, b.width, b.height, Color(0x66FF3B30))
                drawPxRect(b.x - camX, b.y - camY, b.width, 1.5f, Color(0xFFFF3B30))
                drawPxRect(b.x - camX, b.bottom - camY - 1.5f, b.width, 1.5f, Color(0xFFFF3B30))
                drawPxRect(b.x - camX, b.y - camY, 1.5f, b.height, Color(0xFFFF3B30))
                drawPxRect(b.right - camX - 1.5f, b.y - camY, 1.5f, b.height, Color(0xFFFF3B30))
            }
        }

        // === 16. FULL-SCREEN EXPLOSION / IMPACT FLASH OVERLAY ===
        if (world.screenFlashTimer > 0f) {
            val flashAlpha = (world.screenFlashTimer / 0.24f).coerceIn(0f, 1f) * 0.62f
            drawRect(
                color = world.screenFlashColor.copy(alpha = flashAlpha),
                topLeft = Offset.Zero,
                size = size
            )
        }
    }

    private fun renderEnemy(
        enemy: EnemyState,
        levelNumber: Int,
        isArlongMapRoom: Boolean,
        camX: Float,
        camY: Float,
        worldTime: Float,
        drawPxRect: (Float, Float, Float, Float, Color) -> Unit,
        drawPxSprite: (ImageBitmap, Float, Float, Float, Float) -> Unit
    ) {
        val isRight = enemy.facing == FacingDirection.RIGHT
        val screenX = enemy.x - camX
        val screenY = enemy.y - camY

        // Ground shadow
        val groundY = when {
            enemy.kind == EnemyKind.FISHMAN_CHEW && !enemy.droppedFromBalcony -> WorldConstants.ARLONG_BALCONY_Y
            levelNumber == 1 && enemy.x >= WorldConstants.PIER_START_X + 4f -> WorldConstants.PIER_GROUND_Y
            else -> WorldConstants.BEACH_GROUND_Y
        }
        val heightAboveGround = (groundY - enemy.y).coerceAtLeast(0f)
        val shadowScale = (1f - heightAboveGround / 120f).coerceIn(0.35f, 1f)
        val baseShadowW = if (enemy.kind.isBoss) 26f else 15f
        val shadowW = baseShadowW * shadowScale
        drawPxRect(screenX - shadowW * 0.5f, groundY - camY - 1.5f, shadowW, 3f, Color(0x55181425))

        // Select directional + white-flash sprite frame
        val flashSprite = when (enemy.kind) {
            EnemyKind.MARINE_SOLDIER -> when (enemy.behavior) {
                EnemyBehaviorState.WINDUP -> enemySprites.marineWindupSprite
                EnemyBehaviorState.ATTACK -> enemySprites.marineSlashSprite
                else -> {
                    val idx = ((enemy.animTime * 6f).toInt()) % enemySprites.marineWalk.size
                    enemySprites.marineWalk[idx]
                }
            }
            EnemyKind.MARINE_RIFLEMAN -> when (enemy.behavior) {
                EnemyBehaviorState.WINDUP, EnemyBehaviorState.ATTACK -> enemySprites.rifleAimSprite
                else -> {
                    val idx = ((enemy.animTime * 5.5f).toInt()) % enemySprites.rifleWalk.size
                    enemySprites.rifleWalk[idx]
                }
            }
            EnemyKind.PIRATE_BANDIT -> when (enemy.behavior) {
                EnemyBehaviorState.ATTACK -> enemySprites.banditSlashSprite
                else -> {
                    val idx = ((enemy.animTime * 11f).toInt()) % enemySprites.banditRun.size
                    enemySprites.banditRun[idx]
                }
            }
            EnemyKind.BOSS_MORGAN -> when (enemy.behavior) {
                EnemyBehaviorState.WINDUP -> enemySprites.morganWindupSprite
                EnemyBehaviorState.ATTACK -> enemySprites.morganSlashSprite
                else -> {
                    val idx = ((enemy.animTime * 5f).toInt()) % enemySprites.morganWalk.size
                    enemySprites.morganWalk[idx]
                }
            }
            EnemyKind.BUGGY_ACROBAT -> when {
                enemy.behavior == EnemyBehaviorState.ATTACK -> buggySprites.acrobatDiveSprite
                else -> {
                    val idx = ((enemy.animTime * 11f).toInt()) % buggySprites.acrobatRun.size
                    buggySprites.acrobatRun[idx]
                }
            }
            EnemyKind.KNIFE_THROWER -> when (enemy.behavior) {
                EnemyBehaviorState.WINDUP, EnemyBehaviorState.ATTACK -> buggySprites.throwerThrowSprite
                else -> {
                    val idx = ((enemy.animTime * 6f).toInt()) % buggySprites.throwerWalk.size
                    buggySprites.throwerWalk[idx]
                }
            }
            EnemyKind.CABAJI -> when {
                enemy.behavior == EnemyBehaviorState.ATTACK && enemy.bossAttack == BossAttackVariant.CABAJI_FIRE ->
                    buggySprites.cabajiFireSprite
                enemy.behavior == EnemyBehaviorState.ATTACK && enemy.bossAttack == BossAttackVariant.CABAJI_DASH ->
                    buggySprites.cabajiDashSprite
                else -> {
                    val idx = ((enemy.animTime * 10f).toInt()) % buggySprites.cabajiRide.size
                    buggySprites.cabajiRide[idx]
                }
            }
            EnemyKind.BOSS_BUGGY -> when {
                enemy.behavior == EnemyBehaviorState.ATTACK && enemy.bossAttack == BossAttackVariant.BUGGY_FESTIVAL ->
                    buggySprites.buggySplitFeetSprite
                enemy.behavior == EnemyBehaviorState.ATTACK && enemy.bossAttack == BossAttackVariant.BUGGY_CANNON ->
                    buggySprites.buggyCannonSprite
                enemy.behavior == EnemyBehaviorState.WINDUP -> buggySprites.buggyWindupSprite
                else -> {
                    val idx = ((enemy.animTime * 5.5f).toInt()) % buggySprites.buggyWalk.size
                    buggySprites.buggyWalk[idx]
                }
            }
            EnemyKind.FISHMAN_BOUNCER -> when (enemy.behavior) {
                EnemyBehaviorState.WINDUP, EnemyBehaviorState.ATTACK -> arlongSprites.bouncerShoveSprite
                else -> {
                    val idx = ((enemy.animTime * 6.5f).toInt()) % arlongSprites.bouncerWalk.size
                    arlongSprites.bouncerWalk[idx]
                }
            }
            EnemyKind.FISHMAN_CHEW -> when (enemy.behavior) {
                EnemyBehaviorState.WINDUP, EnemyBehaviorState.ATTACK -> arlongSprites.chewShootSprite
                else -> arlongSprites.chewIdleSprite
            }
            EnemyKind.HATCHAN -> when {
                enemy.behavior == EnemyBehaviorState.ATTACK && enemy.bossAttack == BossAttackVariant.HATCHAN_SPIN -> {
                    val idx = ((worldTime * 18f).toInt()) % arlongSprites.hatchanSpin.size
                    arlongSprites.hatchanSpin[idx]
                }
                enemy.behavior == EnemyBehaviorState.ATTACK -> arlongSprites.hatchanSlashSprite
                else -> {
                    val idx = ((enemy.animTime * 6.5f).toInt()) % arlongSprites.hatchanWalk.size
                    arlongSprites.hatchanWalk[idx]
                }
            }
            EnemyKind.BOSS_ARLONG -> when {
                isArlongMapRoom -> when (enemy.behavior) {
                    EnemyBehaviorState.ATTACK -> arlongSprites.arlongKiribatiCleaveSprite
                    else -> arlongSprites.arlongKiribatiIdleSprite
                }
                enemy.behavior == EnemyBehaviorState.ATTACK && enemy.bossAttack == BossAttackVariant.ARLONG_SHARK_DART ->
                    arlongSprites.arlongSharkDartSprite
                else -> {
                    val idx = ((enemy.animTime * 6f).toInt()) % arlongSprites.arlongWalk.size
                    arlongSprites.arlongWalk[idx]
                }
            }
        }

        val isFlashingWhite = enemy.flashTimer > 0f
        val bitmap = flashSprite.forFacing(isRight, isFlashing = isFlashingWhite)

        // Subtle walk bob for rifleman & hit-shake jitter on impact
        val walkBob = if (enemy.kind == EnemyKind.MARINE_RIFLEMAN &&
            enemy.behavior == EnemyBehaviorState.CHASE &&
            kotlin.math.abs(enemy.vx) > 8f
        ) {
            if (((enemy.animTime * 8f).toInt() % 2) == 0) -1f else 0f
        } else {
            0f
        }
        val hurtJitterX = if (isFlashingWhite) (if (((worldTime * 60f).toInt() % 2) == 0) 1f else -1f) else 0f

        val spriteLeft = floor(screenX - bitmap.width * 0.5f + hurtJitterX)
        val spriteTop = floor(screenY - bitmap.height + 2f + walkBob)
        drawPxSprite(bitmap, spriteLeft, spriteTop, 1f, 1f)

        // Crisp danger glint during WINDUP so attacks are readable & fun to dodge!
        if (enemy.behavior == EnemyBehaviorState.WINDUP) {
            val blinkOn = ((worldTime * 16f).toInt() % 2) == 0
            if (blinkOn) {
                val glintX = screenX + enemy.facing.sign * (if (enemy.kind.isBoss) 10f else 6f)
                val glintY = spriteTop + (if (enemy.kind.isBoss) 4f else 2f)
                drawPxRect(glintX - 1f, glintY - 3f, 2f, 6f, Color(0xFFFF3B30))
                drawPxRect(glintX - 3f, glintY - 1f, 6f, 2f, Color(0xFFFF3B30))
                drawPxRect(glintX - 1f, glintY - 1f, 2f, 2f, Color(0xFFFFF59D))
            }
        }

        // Swirling pixel stun stars when launched/stunned by Battle Axe
        if (enemy.behavior == EnemyBehaviorState.STUNNED && enemy.stunTimer > 0f) {
            for (s in 0..2) {
                val angle = worldTime * 9f + s * (2f * PI.toFloat() / 3f)
                val starX = screenX + cos(angle) * 8f
                val starY = spriteTop - 2f + sin(angle) * 2.5f
                drawPxRect(starX - 1f, starY - 1f, 2f, 2f, Color(0xFFFFEB3B))
            }
        }

        // Crimson Bleed droplets indicator when cut by Zoro's Onigiri / Purgatory Onigiri / Asura!
        if (enemy.bleedTimer > 0f) {
            for (d in 0..2) {
                val phase = ((worldTime * 4.5f + d * 0.33f) % 1f)
                val dropX = screenX + (d - 1) * 6f
                val dropY = spriteTop + 6f + phase * 14f
                drawPxRect(dropX - 1f, dropY - 1f, 2f, 3f, Color(0xFFE62E3D))
                drawPxRect(dropX - 0.5f, dropY - 1f, 1f, 1f, Color(0xFFFF8FA3))
            }
        }

        // Overhead Pixel HP Bar for regular enemies (Boss Morgan uses big bottom-screen HUD bar)
        if (!enemy.kind.isBoss) {
            val barW = 20f
            val barH = 3f
            val barX = floor(screenX - barW * 0.5f)
            val barY = floor(screenY - enemy.kind.hitboxHeight - 7f)
            val ratio = (enemy.currentHp.toFloat() / enemy.maxHp.coerceAtLeast(1)).coerceIn(0f, 1f)
            drawPxRect(barX - 1f, barY - 1f, barW + 2f, barH + 2f, pixelInk)
            drawPxRect(barX, barY, barW, barH, Color(0xFF3A1C24))
            val fillColor = when {
                ratio > 0.5f -> Color(0xFF4CD964)
                ratio > 0.25f -> Color(0xFFFFC107)
                else -> Color(0xFFE63946)
            }
            if (ratio > 0f) {
                drawPxRect(barX, barY, (barW * ratio).coerceAtLeast(1f), barH, fillColor)
            }
        }
    }

    private fun renderLuffyAndGomuLimbs(
        luffy: LuffyState,
        camX: Float,
        camY: Float,
        worldTime: Float,
        drawPxRect: (Float, Float, Float, Float, Color) -> Unit,
        drawPxSprite: (ImageBitmap, Float, Float, Float, Float) -> Unit
    ) {
        val isRight = luffy.facing == FacingDirection.RIGHT
        val dir = luffy.facing.sign.toFloat()
        val screenX = luffy.x - camX
        val screenY = luffy.y - camY

        // Invulnerability blink after taking damage
        val spriteAlpha = if (luffy.invulnTimer > 0f && ((worldTime * 20f).toInt() % 2 == 0)) {
            0.30f
        } else {
            1f
        }

        // Ground shadow
        val groundY = if (currentLevelNumber == 1 && luffy.x >= WorldConstants.PIER_START_X + 4f) {
            WorldConstants.PIER_GROUND_Y
        } else {
            WorldConstants.BEACH_GROUND_Y
        }
        val heightAboveGround = (groundY - luffy.y).coerceAtLeast(0f)
        val shadowScale = (1f - heightAboveGround / 120f).coerceIn(0.35f, 1f)
        val shadowW = (if (luffy.gear4Active) 24f else 16f) * shadowScale
        drawPxRect(
            screenX - shadowW * 0.5f,
            groundY - camY - 1.5f,
            shadowW,
            3f,
            Color(0x55181425)
        )

        if (luffy.gear4Active) {
            renderBoundmanAndHakiLimbs(
                luffy = luffy,
                camX = camX,
                camY = camY,
                screenX = screenX,
                screenY = screenY,
                worldTime = worldTime,
                spriteAlpha = spriteAlpha,
                drawPxRect = drawPxRect,
                drawPxSprite = drawPxSprite
            )
            return
        }

        val bodySprite = when (luffy.animState) {
            PlayerAnimState.IDLE -> {
                val idx = ((luffy.animTime * 4.5f).toInt()) % luffySprites.idleFrames.size
                luffySprites.idleFrames[idx].forFacing(isRight)
            }
            PlayerAnimState.RUN -> {
                val idx = ((luffy.animTime * 11f).toInt()) % luffySprites.runFrames.size
                luffySprites.runFrames[idx].forFacing(isRight)
            }
            PlayerAnimState.JUMP_RISE -> luffySprites.jumpRise.forFacing(isRight)
            PlayerAnimState.JUMP_FALL -> luffySprites.jumpFall.forFacing(isRight)
            PlayerAnimState.LAND -> luffySprites.landSquash.forFacing(isRight)
            PlayerAnimState.HURT -> luffySprites.hurtPose.forFacing(isRight)
            PlayerAnimState.ATTACK_PISTOL -> luffySprites.pistolPose.forFacing(isRight)
            PlayerAnimState.ATTACK_GATLING -> {
                val idx = ((luffy.animTime * 24f).toInt()) % luffySprites.gatlingFrames.size
                luffySprites.gatlingFrames[idx].forFacing(isRight)
            }
            PlayerAnimState.ATTACK_AXE -> {
                if (luffy.attackPhase == AttackPhase.WINDUP) {
                    luffySprites.axeWindup.forFacing(isRight)
                } else {
                    luffySprites.axeSlam.forFacing(isRight)
                }
            }
            PlayerAnimState.ATTACK_ZORO_SLASH,
            PlayerAnimState.ATTACK_ZORO_ONIGIRI,
            PlayerAnimState.ATTACK_ZORO_TATSUMAKI,
            PlayerAnimState.ATTACK_ZORO_HELL_SLASH,
            PlayerAnimState.ATTACK_ZORO_PURGATORY_ONIGIRI,
            PlayerAnimState.ATTACK_ZORO_ASURA,
            PlayerAnimState.ATTACK_SANJI_KICK,
            PlayerAnimState.ATTACK_SANJI_PARTY_TABLE,
            PlayerAnimState.ATTACK_SANJI_DIABLE_JAMBE,
            PlayerAnimState.ATTACK_NAMI_SPARK,
            PlayerAnimState.ATTACK_NAMI_CYCLONE,
            PlayerAnimState.ATTACK_NAMI_THUNDERBOLT,
            PlayerAnimState.ATTACK_USOPP_SHOT,
            PlayerAnimState.ATTACK_USOPP_EXPLODING_STAR,
            PlayerAnimState.ATTACK_USOPP_FIREBIRD_STAR,
            PlayerAnimState.ATTACK_CHOPPER_KUNGFU,
            PlayerAnimState.ATTACK_CHOPPER_GUARD_HEAL,
            PlayerAnimState.ATTACK_CHOPPER_MONSTER_POINT,
            PlayerAnimState.ATTACK_GEAR4_KONG_GUN,
            PlayerAnimState.ATTACK_GEAR4_KONG_ORGAN,
            PlayerAnimState.ATTACK_GEAR4_KING_KONG_GUN -> luffySprites.idleFrames[0].forFacing(isRight)
        }

        val spriteLeft = floor(screenX - bodySprite.width * 0.5f)
        val spriteTop = floor(screenY - bodySprite.height + 2f)

        // Sky-High Rubber Leg welded seamlessly onto axeWindupFrame (28x34)
        if (luffy.activeAttack == AttackType.AXE && luffy.attackPhase == AttackPhase.WINDUP) {
            val legLength = WorldConstants.AXE_SKY_HEIGHT * luffy.stretchFactor
            if (legLength > 2f) {
                val legLeftX = if (isRight) spriteLeft + 16f else spriteLeft + 8f
                val legBaseY = spriteTop + 4f
                val footBottomY = floor(legBaseY - legLength)
                val segHeight = (legBaseY - footBottomY + 2f).coerceAtLeast(2f)

                drawPxRect(legLeftX, footBottomY, 4f, segHeight, pixelInk)
                drawPxRect(legLeftX + 1f, footBottomY, 1f, segHeight, rubberSkin)
                drawPxRect(legLeftX + 2f, footBottomY, 1f, segHeight, rubberShade)

                val footSprite = luffySprites.axeFootUp.forFacing(isRight)
                val footDrawX = legLeftX - 5f
                val footDrawY = footBottomY - footSprite.height + 3f
                drawPxSprite(footSprite, footDrawX, footDrawY, 1f, spriteAlpha)
            }
        }

        // Draw Luffy sprite anchored at bottom-center
        drawPxSprite(bodySprite, spriteLeft, spriteTop, 1f, spriteAlpha)

        // Active Gomu-Gomu Stretched Limbs
        when (luffy.activeAttack) {
            AttackType.PISTOL -> {
                val reach = WorldConstants.PISTOL_MAX_REACH * luffy.stretchFactor
                val shoulderX = if (isRight) spriteLeft + 18f else spriteLeft + 10f
                val armTopY = spriteTop + 17f
                val fistX = floor(shoulderX + dir * (4f + reach))
                val armLeft = minOf(shoulderX, fistX)
                val armWidth = kotlin.math.abs(fistX - shoulderX).coerceAtLeast(2f)

                drawPxRect(armLeft, armTopY, armWidth, 4f, pixelInk)
                drawPxRect(armLeft, armTopY + 1f, armWidth, 1f, rubberSkin)
                drawPxRect(armLeft, armTopY + 2f, armWidth, 1f, rubberShade)

                if (luffy.attackPhase == AttackPhase.ACTIVE && reach > 28f) {
                    val armCenterY = armTopY + 2f
                    val ring1X = shoulderX + dir * (reach * 0.45f)
                    val ring2X = shoulderX + dir * (reach * 0.76f)
                    drawPxRect(ring1X - 1f, armCenterY - 6f, 2f, 12f, Color(0xCCFFFFFF))
                    drawPxRect(ring2X - 1f, armCenterY - 8f, 2f, 16f, Color(0xBB7CE8FF))
                }

                val fistSprite = luffySprites.rubberFist.forFacing(isRight)
                val fistDrawX = if (isRight) fistX - 1f else fistX - fistSprite.width + 1f
                val fistDrawY = armTopY - 4f
                drawPxSprite(fistSprite, fistDrawX, fistDrawY, 1f, spriteAlpha)
            }

            AttackType.GATLING -> {
                if (luffy.attackPhase == AttackPhase.ACTIVE || luffy.gatlingGhosts.isNotEmpty()) {
                    val shoulderX = if (isRight) spriteLeft + 17f else spriteLeft + 11f
                    val baseArmTopY = spriteTop + 17f

                    for (ghost in luffy.gatlingGhosts) {
                        val lifeT = (ghost.age / ghost.maxAge).coerceIn(0f, 1f)
                        val launchT = (lifeT * 3.4f).coerceIn(0f, 1f)
                        val easedLaunch = 1f - (1f - launchT) * (1f - launchT)
                        val fadeAlpha = (if (lifeT > 0.65f) {
                            (1f - (lifeT - 0.65f) / 0.35f).coerceIn(0.25f, 1f)
                        } else {
                            1f
                        }) * spriteAlpha

                        val armTopY = floor(baseArmTopY + ghost.offsetY)
                        val curReach = (ghost.reach + ghost.offsetX) * easedLaunch
                        val fistX = floor(shoulderX + dir * (4f + curReach))
                        val armLeft = minOf(shoulderX, fistX)
                        val armWidth = kotlin.math.abs(fistX - shoulderX).coerceAtLeast(2f)

                        drawPxRect(armLeft, armTopY, armWidth, 4f, pixelInk.copy(alpha = fadeAlpha))
                        drawPxRect(armLeft, armTopY + 1f, armWidth, 1f, rubberSkin.copy(alpha = fadeAlpha))
                        drawPxRect(armLeft, armTopY + 2f, armWidth, 1f, rubberShade.copy(alpha = fadeAlpha))

                        if (launchT > 0.6f && curReach > 30f) {
                            val ringX = shoulderX + dir * (curReach * 0.72f)
                            drawPxRect(ringX - 1f, armTopY - 3f, 2f, 10f, Color.White.copy(alpha = fadeAlpha * 0.7f))
                        }

                        val fist = luffySprites.rubberFist.forFacing(isRight)
                        val fx = if (isRight) fistX - 1f else fistX - fist.width + 1f
                        val fy = armTopY - 4f
                        drawPxSprite(fist, fx, fy, 1f, fadeAlpha)
                    }
                }
            }

            AttackType.AXE -> {
                if (luffy.attackPhase == AttackPhase.ACTIVE || luffy.attackPhase == AttackPhase.RETRACT) {
                    val impactWorldX = (luffy.x + dir * WorldConstants.AXE_SLAM_FORWARD)
                        .coerceIn(WorldConstants.LEFT_BOUNDARY_X, WorldConstants.RIGHT_BOUNDARY_X)
                    val impactScreenX = floor(impactWorldX - camX)
                    val impactGroundY = floor(
                        if (currentLevelNumber == 1 && impactWorldX >= WorldConstants.PIER_START_X + 4f) {
                            WorldConstants.PIER_GROUND_Y - camY
                        } else {
                            WorldConstants.BEACH_GROUND_Y - camY
                        }
                    )

                    val hipX = if (isRight) spriteLeft + 19f else spriteLeft + 9f
                    val hipY = spriteTop + 24f

                    if (!luffy.axeImpactTriggered) {
                        val t = luffy.stretchFactor.coerceIn(0f, 1f)
                        val eased = t * t
                        val angle = (PI.toFloat() * 0.5f) * (1f - eased)
                        val arcRadius = 118f * (1f - eased) + kotlin.math.abs(impactScreenX - hipX) * eased
                        val footAnkleX = floor(hipX + dir * cos(angle) * arcRadius)
                        val footAnkleY = floor(
                            (hipY - sin(angle) * arcRadius) * (1f - eased) +
                                (impactGroundY - 6f) * eased
                        )

                        for (trail in 1..3) {
                            val prevAngle = (angle + trail * 0.14f).coerceAtMost(PI.toFloat() * 0.5f)
                            val tx = floor(hipX + dir * cos(prevAngle) * arcRadius)
                            val ty = floor(hipY - sin(prevAngle) * arcRadius)
                            drawGatlingStreakLine(
                                x0 = hipX,
                                y0 = hipY,
                                x1 = tx,
                                y1 = ty,
                                color = Color(0xFFCCF5FF).copy(alpha = 0.45f - trail * 0.11f),
                                drawPxRect = drawPxRect
                            )
                        }

                        drawRubberSegment(hipX, hipY, footAnkleX, footAnkleY, spriteAlpha, drawPxRect)

                        val slamFoot = luffySprites.axeFootSlam.forFacing(isRight)
                        drawPxSprite(
                            slamFoot,
                            footAnkleX - slamFoot.width * 0.5f,
                            footAnkleY - 2f,
                            1f,
                            spriteAlpha
                        )
                    } else {
                        val retractScale = luffy.stretchFactor.coerceIn(0f, 1f)
                        val footAnkleX = floor(hipX + (impactScreenX - hipX) * retractScale)
                        val footAnkleY = floor(hipY + (impactGroundY - 6f - hipY) * retractScale)

                        if (retractScale > 0.08f) {
                            drawRubberSegment(hipX, hipY, footAnkleX, footAnkleY, spriteAlpha, drawPxRect)
                            val slamFoot = luffySprites.axeFootSlam.forFacing(isRight)
                            drawPxSprite(
                                slamFoot,
                                footAnkleX - slamFoot.width * 0.5f,
                                footAnkleY - 2f,
                                1f,
                                spriteAlpha
                            )
                        }
                    }
                }
            }

            AttackType.ZORO_SLASH,
            AttackType.ZORO_ONIGIRI,
            AttackType.ZORO_TATSUMAKI,
            AttackType.ZORO_HELL_SLASH,
            AttackType.ZORO_PURGATORY_ONIGIRI,
            AttackType.ZORO_ASURA,
            AttackType.SANJI_KICK,
            AttackType.SANJI_PARTY_TABLE,
            AttackType.SANJI_DIABLE_JAMBE,
            AttackType.NAMI_SPARK,
            AttackType.NAMI_CYCLONE,
            AttackType.NAMI_THUNDERBOLT,
            AttackType.USOPP_SHOT,
            AttackType.USOPP_EXPLODING_STAR,
            AttackType.USOPP_FIREBIRD_STAR,
            AttackType.CHOPPER_KUNGFU,
            AttackType.CHOPPER_GUARD_HEAL,
            AttackType.CHOPPER_MONSTER_POINT,
            AttackType.GEAR4_KONG_GUN,
            AttackType.GEAR4_KONG_ORGAN,
            AttackType.GEAR4_KING_KONG_GUN,
            null -> Unit
        }
    }

    /**
     * Dedicated 16-bit renderer for Luffy's Gear 4: Boundman form —
     * muscular obsidian-crimson Busoshoku Haki body, pulsing crimson Haki aura,
     * swirling steam puffs, Kong Gun cannon punch, Kong Organ multi-arm barrage,
     * and colossal descending King Kong Gun fist!
     */
    private fun renderBoundmanAndHakiLimbs(
        luffy: LuffyState,
        camX: Float,
        camY: Float,
        screenX: Float,
        screenY: Float,
        worldTime: Float,
        spriteAlpha: Float,
        drawPxRect: (Float, Float, Float, Float, Color) -> Unit,
        drawPxSprite: (ImageBitmap, Float, Float, Float, Float) -> Unit
    ) {
        val isRight = luffy.facing == FacingDirection.RIGHT
        val dir = luffy.facing.sign.toFloat()

        // Subtle pulsing crimson-obsidian Busoshoku Haki aura behind Boundman
        val auraPulse = 0.5f + 0.5f * sin(worldTime * 11f)
        val auraW = 36f + auraPulse * 4f
        val auraH = 38f + auraPulse * 3f
        drawPxRect(
            floor(screenX - auraW * 0.5f),
            floor(screenY - auraH + 2f),
            auraW,
            auraH,
            Color(0xFFB81D46).copy(alpha = (0.12f + auraPulse * 0.08f) * spriteAlpha)
        )

        // Back-layer 16-bit pixel Hagoromo steam vapor plumes curling behind Boundman's shoulders
        val steamPuffs = luffySprites.boundmanSteamPuffs
        if (steamPuffs.isNotEmpty()) {
            for (i in 0 until 4) {
                val phase = (worldTime * 1.65f + i * 0.25f) % 1f
                val side = if (i % 2 == 0) -1f else 1f
                val driftX = side * (11f + phase * 6f) - dir * (if (luffy.animState == PlayerAnimState.RUN) phase * 8f else 0f)
                val riseY = 26f + phase * 14f
                val frameIdx = (phase * steamPuffs.size).toInt().coerceIn(0, steamPuffs.lastIndex)
                val puffAlpha = ((1f - phase) * 0.85f).coerceIn(0f, 0.85f) * spriteAlpha
                drawPxSprite(
                    steamPuffs[frameIdx],
                    floor(screenX + driftX - 5f),
                    floor(screenY - riseY - 4f),
                    1f,
                    puffAlpha
                )
            }
        }

        val idleIdx = ((worldTime * 7.2f).toInt()) % luffySprites.boundmanIdleFrames.size
        val runIdx = ((luffy.animTime * 11f).toInt()) % luffySprites.boundmanRunFrames.size
        val bodySprite = when (luffy.animState) {
            PlayerAnimState.HURT -> luffySprites.boundmanHurt.forFacing(isRight)
            PlayerAnimState.ATTACK_GEAR4_KONG_GUN -> luffySprites.boundmanKongGun.forFacing(isRight)
            PlayerAnimState.ATTACK_GEAR4_KONG_ORGAN -> {
                val idx = ((luffy.animTime * 20f).toInt()) % luffySprites.boundmanKongOrganFrames.size
                luffySprites.boundmanKongOrganFrames[idx].forFacing(isRight)
            }
            PlayerAnimState.ATTACK_GEAR4_KING_KONG_GUN -> luffySprites.boundmanKingKong.forFacing(isRight)
            PlayerAnimState.JUMP_RISE -> luffySprites.boundmanJumpRise.forFacing(isRight)
            PlayerAnimState.JUMP_FALL -> luffySprites.boundmanJumpFall.forFacing(isRight)
            PlayerAnimState.LAND -> luffySprites.boundmanLand.forFacing(isRight)
            PlayerAnimState.RUN -> luffySprites.boundmanRunFrames[runIdx].forFacing(isRight)
            else -> luffySprites.boundmanIdleFrames[idleIdx].forFacing(isRight)
        }

        val spriteLeft = floor(screenX - bodySprite.width * 0.5f)
        val spriteTop = floor(screenY - bodySprite.height + 2f)

        // Draw colossal inflated King Kong Gun Haki fist overhead during WINDUP before drawing body
        if (luffy.activeAttack == AttackType.GEAR4_KING_KONG_GUN && luffy.attackPhase == AttackPhase.WINDUP) {
            val inflateScale = 0.72f + 0.65f * luffy.stretchFactor
            val kingFist = luffySprites.hakiKingFist.forFacing(isRight)
            val fw = kingFist.width * inflateScale
            val fh = kingFist.height * inflateScale
            val fx = floor(screenX + dir * 10f - fw * 0.5f)
            val fy = floor(spriteTop - fh + 8f)
            drawPxRect(fx - 3f, fy - 3f, fw + 6f, fh + 6f, Color(0x55FF4D7D))
            drawPxSprite(kingFist, fx, fy, inflateScale, spriteAlpha)
        }

        drawPxSprite(bodySprite, spriteLeft, spriteTop, 1f, spriteAlpha)

        // Foreground elastic bounce steam rings at Haki tiptoes on squash/land/push-off frames
        if (steamPuffs.isNotEmpty() && luffy.isGrounded) {
            val isBounceCompress = luffy.animState == PlayerAnimState.LAND ||
                (luffy.animState == PlayerAnimState.IDLE && idleIdx == 1) ||
                (luffy.animState == PlayerAnimState.RUN && runIdx == 0)
            if (isBounceCompress) {
                drawPxSprite(steamPuffs[1], floor(screenX - 16f), floor(screenY - 6f), 1f, 0.72f * spriteAlpha)
                drawPxSprite(steamPuffs[1], floor(screenX + 6f), floor(screenY - 6f), 1f, 0.72f * spriteAlpha)
            }
        }

        val hakiObsidian = Color(0xFF240C18)
        val hakiWine = Color(0xFF5E1028)
        val hakiCrimson = Color(0xFFB81D46)
        val hakiMagenta = Color(0xFFFF4D7D)

        when (luffy.activeAttack) {
            AttackType.GEAR4_KONG_GUN -> {
                val reach = WorldConstants.KONG_GUN_MAX_REACH * luffy.stretchFactor
                val shoulderX = if (isRight) spriteLeft + 30f else spriteLeft + 18f
                val armTopY = spriteTop + 16f
                val fistX = floor(shoulderX + dir * (4f + reach))
                val armLeft = minOf(shoulderX, fistX)
                val armWidth = kotlin.math.abs(fistX - shoulderX).coerceAtLeast(3f)

                if (luffy.attackPhase == AttackPhase.WINDUP) {
                    val compressX = if (isRight) shoulderX - 6f else shoulderX - 10f
                    drawPxRect(compressX, armTopY - 2f, 14f, 10f, pixelInk)
                    drawPxRect(compressX + 1f, armTopY - 1f, 12f, 8f, hakiObsidian)
                    drawPxRect(compressX + 2f, armTopY, 10f, 3f, hakiCrimson)
                    drawPxRect(compressX + 4f, armTopY + 1f, 6f, 2f, hakiMagenta)
                } else {
                    drawPxRect(armLeft, armTopY - 1f, armWidth, 8f, pixelInk)
                    drawPxRect(armLeft, armTopY, armWidth, 1f, hakiMagenta)
                    drawPxRect(armLeft, armTopY + 1f, armWidth, 2f, hakiCrimson)
                    drawPxRect(armLeft, armTopY + 3f, armWidth, 2f, hakiWine)
                    drawPxRect(armLeft, armTopY + 5f, armWidth, 1f, hakiObsidian)

                    if (luffy.attackPhase == AttackPhase.ACTIVE && reach > 26f) {
                        val ring1X = shoulderX + dir * (reach * 0.42f)
                        val ring2X = shoulderX + dir * (reach * 0.74f)
                        drawPxRect(ring1X - 1.5f, armTopY - 7f, 3f, 20f, Color(0xDDFF4D7D))
                        drawPxRect(ring2X - 1.5f, armTopY - 9f, 3f, 24f, Color(0xCCFFFFFF))
                    }

                    val fistSprite = luffySprites.hakiFist.forFacing(isRight)
                    val fistDrawX = if (isRight) fistX - 2f else fistX - fistSprite.width + 2f
                    val fistDrawY = armTopY - 5f
                    drawPxSprite(fistSprite, fistDrawX, fistDrawY, 1f, spriteAlpha)
                }
            }

            AttackType.GEAR4_KONG_ORGAN -> {
                if (luffy.attackPhase == AttackPhase.ACTIVE || luffy.gatlingGhosts.isNotEmpty()) {
                    val shoulderX = if (isRight) spriteLeft + 29f else spriteLeft + 19f
                    val baseArmTopY = spriteTop + 16f

                    for (ghost in luffy.gatlingGhosts) {
                        val lifeT = (ghost.age / ghost.maxAge).coerceIn(0f, 1f)
                        val launchT = (lifeT * 3.2f).coerceIn(0f, 1f)
                        val easedLaunch = 1f - (1f - launchT) * (1f - launchT)
                        val fadeAlpha = (if (lifeT > 0.62f) {
                            (1f - (lifeT - 0.62f) / 0.38f).coerceIn(0.22f, 1f)
                        } else {
                            1f
                        }) * spriteAlpha

                        val armTopY = floor(baseArmTopY + ghost.offsetY)
                        val curReach = (ghost.reach + ghost.offsetX) * easedLaunch
                        val fistX = floor(shoulderX + dir * (4f + curReach))
                        val armLeft = minOf(shoulderX, fistX)
                        val armWidth = kotlin.math.abs(fistX - shoulderX).coerceAtLeast(3f)

                        drawPxRect(armLeft, armTopY - 1f, armWidth, 7f, pixelInk.copy(alpha = fadeAlpha))
                        drawPxRect(armLeft, armTopY, armWidth, 1f, hakiMagenta.copy(alpha = fadeAlpha))
                        drawPxRect(armLeft, armTopY + 1f, armWidth, 2f, hakiCrimson.copy(alpha = fadeAlpha))
                        drawPxRect(armLeft, armTopY + 3f, armWidth, 2f, hakiObsidian.copy(alpha = fadeAlpha))

                        val fist = luffySprites.hakiFist.forFacing(isRight)
                        val fx = if (isRight) fistX - 2f else fistX - fist.width + 2f
                        val fy = armTopY - 5f
                        drawPxSprite(fist, fx, fy, 1f, fadeAlpha)
                    }
                }
            }

            AttackType.GEAR4_KING_KONG_GUN -> {
                if (luffy.attackPhase == AttackPhase.ACTIVE || luffy.attackPhase == AttackPhase.RETRACT) {
                    val impactWorldX = (luffy.x + dir * 24f)
                        .coerceIn(WorldConstants.LEFT_BOUNDARY_X, WorldConstants.RIGHT_BOUNDARY_X)
                    val impactScreenX = floor(impactWorldX - camX)
                    val impactGroundY = floor(
                        if (currentLevelNumber == 1 && impactWorldX >= WorldConstants.PIER_START_X + 4f) {
                            WorldConstants.PIER_GROUND_Y - camY
                        } else {
                            WorldConstants.BEACH_GROUND_Y - camY
                        }
                    )
                    val scale = if (luffy.attackPhase == AttackPhase.RETRACT) {
                        (0.45f + 0.90f * luffy.stretchFactor).coerceAtLeast(0.35f)
                    } else {
                        1.35f
                    }
                    val kingFist = luffySprites.hakiKingFist.forFacing(isRight)
                    val fw = kingFist.width * scale
                    val fh = kingFist.height * scale
                    val fistCenterX = if (luffy.kingKongImpactTriggered) impactScreenX else screenX + dir * 16f
                    val fistBottomY = if (luffy.kingKongImpactTriggered) {
                        impactGroundY + 2f
                    } else {
                        screenY + 6f
                    }
                    val armX = floor(fistCenterX - 5f)
                    val armTop = floor(spriteTop + 14f)
                    val armH = (fistBottomY - fh * 0.6f - armTop).coerceAtLeast(4f)
                    drawPxRect(armX, armTop, 10f, armH, pixelInk.copy(alpha = spriteAlpha))
                    drawPxRect(armX + 1f, armTop, 2f, armH, hakiMagenta.copy(alpha = spriteAlpha))
                    drawPxRect(armX + 3f, armTop, 3f, armH, hakiCrimson.copy(alpha = spriteAlpha))
                    drawPxRect(armX + 6f, armTop, 3f, armH, hakiObsidian.copy(alpha = spriteAlpha))

                    drawPxSprite(
                        kingFist,
                        floor(fistCenterX - fw * 0.5f),
                        floor(fistBottomY - fh),
                        scale,
                        spriteAlpha
                    )
                }
            }

            else -> Unit
        }
    }

    private fun renderZoroAndSantoryu(
        player: LuffyState,
        camX: Float,
        camY: Float,
        worldTime: Float,
        drawPxRect: (Float, Float, Float, Float, Color) -> Unit,
        drawPxSprite: (ImageBitmap, Float, Float, Float, Float) -> Unit
    ) {
        val isRight = player.facing == FacingDirection.RIGHT
        val dir = player.facing.sign.toFloat()
        val screenX = player.x - camX
        val screenY = player.y - camY

        // Invulnerability blink (only on damage invuln, not Onigiri / Purgatory / Asura dash frames)
        val isOnigiriDashing = (player.activeAttack == AttackType.ZORO_ONIGIRI ||
            player.activeAttack == AttackType.ZORO_PURGATORY_ONIGIRI) &&
            player.attackPhase == AttackPhase.ACTIVE
        val isAsuraActive = player.activeAttack == AttackType.ZORO_ASURA &&
            player.attackPhase != AttackPhase.NONE
        val isKingOfHell = player.zoroHellActive
        val spriteAlpha = if (player.invulnTimer > 0f && !isOnigiriDashing && !isAsuraActive &&
            ((worldTime * 20f).toInt() % 2 == 0)
        ) {
            0.30f
        } else {
            1f
        }

        // Ground shadow
        val groundY = if (currentLevelNumber == 1 && player.x >= WorldConstants.PIER_START_X + 4f) {
            WorldConstants.PIER_GROUND_Y
        } else {
            WorldConstants.BEACH_GROUND_Y
        }
        val heightAboveGround = (groundY - player.y).coerceAtLeast(0f)
        val shadowScale = (1f - heightAboveGround / 120f).coerceIn(0.35f, 1f)
        val shadowW = 16f * shadowScale
        drawPxRect(
            screenX - shadowW * 0.5f,
            groundY - camY - 1.5f,
            shadowW,
            3f,
            if (isKingOfHell) Color(0x88112E1E) else Color(0x55181425)
        )

        // King of Hell Conqueror Haki ground ring & rising black-emerald flame aura behind Zoro
        if (isKingOfHell) {
            val auraPulse = sin(worldTime * 16f) * 2.5f
            drawPxRect(screenX - 14f - auraPulse * 0.5f, groundY - camY - 2.5f, 28f + auraPulse, 3.5f, Color(0x8839FF88))
            drawPxRect(screenX - 10f, groundY - camY - 1.5f, 20f, 2f, Color(0xAA10141D))
            for (f in 0..5) {
                val phase = ((worldTime * 5.2f + f * 0.19f) % 1f)
                val fx = screenX + sin(worldTime * 9f + f * 1.3f) * (10f + (f % 2) * 4f)
                val fy = screenY - 3f - phase * 30f
                val sz = (4.5f * (1f - phase * 0.65f)).coerceAtLeast(1.5f)
                drawPxRect(fx - sz * 0.5f, fy - sz * 0.5f, sz, sz, if (f % 2 == 0) Color(0xCC12161F) else Color(0xCC39FF88))
                drawPxRect(fx - sz * 0.25f, fy - sz * 0.25f, sz * 0.5f, sz * 0.5f, Color(0xDD9EFFC4))
            }
        }

        // Kyutoryu: Asura (Nine-Sword Style) spectral purple-emerald 3-headed, 6-armed demon aura silhouette behind Zoro!
        if (isAsuraActive) {
            val asuraAlpha = when (player.attackPhase) {
                AttackPhase.WINDUP -> (player.stretchFactor * 0.85f).coerceIn(0.25f, 0.85f)
                AttackPhase.ACTIVE -> 0.92f
                else -> (player.stretchFactor * 0.7f).coerceIn(0f, 0.7f)
            }
            val auraViolet = Color(0xFF9333EA).copy(alpha = asuraAlpha * 0.78f)
            val auraEmerald = Color(0xFF39FF88).copy(alpha = asuraAlpha * 0.82f)
            val auraCore = Color(0xFFE9D5FF).copy(alpha = asuraAlpha * 0.90f)
            val auraDark = Color(0xFF1A102B).copy(alpha = asuraAlpha * 0.72f)
            val bob = sin(worldTime * 22f) * 1.5f
            // Towering demonic torso & 3 spectral heads
            drawPxRect(screenX - 14f, screenY - 46f + bob, 28f, 34f, auraDark)
            drawPxRect(screenX - 12f, screenY - 44f + bob, 24f, 30f, auraViolet)
            // Left, Center & Right Asura spectral heads with glowing eyes
            for (hx in listOf(-12f, 0f, 12f)) {
                val hy = screenY - (if (hx == 0f) 56f else 51f) + bob
                drawPxRect(screenX + hx - 5f, hy, 10f, 10f, auraDark)
                drawPxRect(screenX + hx - 4f, hy + 1f, 8f, 8f, auraViolet)
                drawPxRect(screenX + hx - 2.5f, hy + 4f, 2f, 2f, auraEmerald)
                drawPxRect(screenX + hx + 0.5f, hy + 4f, 2f, 2f, auraEmerald)
            }
            // Six fanned spectral arms holding 9 glowing blades radiating outward!
            for (side in listOf(-1f, 1f)) {
                for (armIdx in 0..2) {
                    val ay = screenY - 38f + armIdx * 9f + bob
                    val reach = 20f + armIdx * 5f
                    drawPxRect(screenX + side * 8f - (if (side < 0) reach else 0f), ay, reach, 3f, auraViolet)
                    // Glowing spectral katana blade extending from each hand
                    val bladeLen = 18f + (if (player.attackPhase == AttackPhase.ACTIVE) 12f else 0f)
                    val bx = screenX + side * (8f + reach)
                    val slopeY = (armIdx - 1) * 5f
                    for (seg in 0..5) {
                        val u = seg / 5f
                        val px = bx + side * bladeLen * u
                        val py = ay + slopeY * u
                        drawPxRect(px - 2f, py - 2f, 4f, 4f, auraEmerald)
                        drawPxRect(px - 1f, py - 1f, 2f, 2f, auraCore)
                    }
                }
            }
        }

        val isTatsumakiActive = player.activeAttack == AttackType.ZORO_TATSUMAKI &&
            player.attackPhase == AttackPhase.ACTIVE

        // Back layer of Dragon Twister whirlwind behind Zoro
        if (isTatsumakiActive) {
            renderTatsumakiVortex(
                centerX = screenX,
                baseY = screenY,
                worldTime = worldTime,
                frontLayer = false,
                drawPxRect = drawPxRect
            )
        }

        val bodySprite = when (player.animState) {
            PlayerAnimState.IDLE -> {
                val idx = ((player.animTime * 4.5f).toInt()) % zoroSprites.idleFrames.size
                zoroSprites.idleFrames[idx].forFacing(isRight)
            }
            PlayerAnimState.RUN -> {
                val idx = ((player.animTime * 12f).toInt()) % zoroSprites.runFrames.size
                zoroSprites.runFrames[idx].forFacing(isRight)
            }
            PlayerAnimState.JUMP_RISE -> zoroSprites.jumpRise.forFacing(isRight)
            PlayerAnimState.JUMP_FALL -> zoroSprites.jumpFall.forFacing(isRight)
            PlayerAnimState.LAND -> zoroSprites.landSquash.forFacing(isRight)
            PlayerAnimState.HURT -> zoroSprites.hurtPose.forFacing(isRight)
            PlayerAnimState.ATTACK_ZORO_SLASH,
            PlayerAnimState.ATTACK_ZORO_HELL_SLASH -> {
                val idx = player.zoroComboStep.coerceIn(0, zoroSprites.slashFrames.lastIndex)
                zoroSprites.slashFrames[idx].forFacing(isRight)
            }
            PlayerAnimState.ATTACK_ZORO_ONIGIRI,
            PlayerAnimState.ATTACK_ZORO_PURGATORY_ONIGIRI -> {
                if (player.attackPhase == AttackPhase.WINDUP) {
                    zoroSprites.onigiriWindup.forFacing(isRight)
                } else {
                    zoroSprites.onigiriDash.forFacing(isRight)
                }
            }
            PlayerAnimState.ATTACK_ZORO_TATSUMAKI -> {
                val idx = ((player.animTime * 24f).toInt()) % zoroSprites.tatsumakiFrames.size
                zoroSprites.tatsumakiFrames[idx].forFacing(isRight)
            }
            PlayerAnimState.ATTACK_ZORO_ASURA -> {
                if (player.attackPhase == AttackPhase.ACTIVE) {
                    zoroSprites.slashFrames[2].forFacing(isRight)
                } else {
                    zoroSprites.onigiriWindup.forFacing(isRight)
                }
            }
            else -> zoroSprites.idleFrames[0].forFacing(isRight)
        }

        val spriteLeft = floor(screenX - bodySprite.width * 0.5f)
        val spriteTop = floor(screenY - bodySprite.height + 2f)

        // Draw Zoro body sprite intact
        drawPxSprite(bodySprite, spriteLeft, spriteTop, 1f, spriteAlpha)

        // King of Hell overlays ON TOP of Zoro's sprite (Black Bandana + fluttering tail + black-green Haki sword flames!)
        if (isKingOfHell) {
            val headCenterX = screenX + dir * 0.5f
            val headTopY = spriteTop + 4f
            // Crisp black pixel bandana wrapped tightly over Zoro's forehead
            drawPxRect(headCenterX - 5f, headTopY, 10f, 3.5f, Color(0xFF10131A))
            drawPxRect(headCenterX - 4f, headTopY + 0.5f, 8f, 1.5f, Color(0xFF242B38))
            // Fluttering bandana tails streaming behind his head
            val flutter = if (((worldTime * 14f).toInt() % 2) == 0) -1f else 1f
            drawPxRect(headCenterX - dir * 7f - 1.5f, headTopY + 1f, 4f, 2f, Color(0xFF10131A))
            drawPxRect(headCenterX - dir * 10f - 1.5f, headTopY + 2f + flutter * 0.8f, 4f, 1.8f, Color(0xFF1B202C))
            // Glowing emerald Conqueror Haki eye glint
            drawPxRect(headCenterX + dir * 2.5f, headTopY + 4f, 2f, 1.5f, Color(0xFF5CF289))

            // Black-and-green Haki flames licking around Zoro's blades (front & back swords)
            val swordOffsets = listOf(
                Pair(dir * 12f, -15f),
                Pair(dir * 8f, -21f),
                Pair(-dir * 10f, -13f)
            )
            for ((idx, off) in swordOffsets.withIndex()) {
                val flick = sin(worldTime * 24f + idx * 2.1f) * 2.2f
                val fx = screenX + off.first
                val fy = screenY + off.second + flick
                drawPxRect(fx - 3f, fy - 4f, 6f, 7f, Color(0xCC10141D))
                drawPxRect(fx - 2f, fy - 3f, 4f, 5f, Color(0xEE39FF88))
                drawPxRect(fx - 1f, fy - 2f, 2f, 2.5f, Color(0xFFFFFFFF))
            }
            // Occasional jagged green-black Conqueror lightning crackle around Zoro
            if (((worldTime * 18f).toInt() % 3) == 0) {
                val lx = screenX + (if (((worldTime * 12f).toInt() % 2) == 0) 11f else -11f)
                val ly = screenY - 22f
                drawPxRect(lx - 1f, ly, 2f, 6f, Color(0xFF141821))
                drawPxRect(lx, ly + 1f, 1.5f, 4f, Color(0xFF39FF88))
                drawPxRect(lx + 1.5f, ly + 4f, 2f, 5f, Color(0xFF39FF88))
            }
        }

        // Steel Parry 100% Crit Ready — bright white-gold razor glint pulsing on Zoro's sword!
        if (player.zoroParryCritReady) {
            val glintX = screenX + dir * 10f
            val glintY = screenY - 19f
            val pulse = if (((worldTime * 26f).toInt() % 2) == 0) 6.5f else 4.5f
            drawPxRect(glintX - pulse, glintY - 1f, pulse * 2f, 2f, Color(0xFFFFF066))
            drawPxRect(glintX - 1f, glintY - pulse, 2f, pulse * 2f, Color(0xFFFFF066))
            drawPxRect(glintX - 2f, glintY - 2f, 4f, 4f, Color.White)
        }

        // Onigiri / Purgatory Onigiri windup katana steel glint
        if ((player.activeAttack == AttackType.ZORO_ONIGIRI || player.activeAttack == AttackType.ZORO_PURGATORY_ONIGIRI) &&
            player.attackPhase == AttackPhase.WINDUP
        ) {
            val glintX = screenX + dir * 7f
            val glintY = screenY - 15f
            val pulse = if (((worldTime * 30f).toInt() % 2) == 0) 7f else 4.5f
            val glintColor = if (player.activeAttack == AttackType.ZORO_PURGATORY_ONIGIRI) Color(0xFF39FF88) else Color(0xFF7CF5A0)
            drawPxRect(glintX - pulse, glintY - 1f, pulse * 2f, 2f, glintColor)
            drawPxRect(glintX - 1f, glintY - pulse, 2f, pulse * 2f, glintColor)
            drawPxRect(glintX - 2f, glintY - 2f, 4f, 4f, Color.White)
        }

        // Onigiri / Purgatory Onigiri dash speed lines
        if (isOnigiriDashing) {
            val tailDir = -dir
            val isPurgatory = player.activeAttack == AttackType.ZORO_PURGATORY_ONIGIRI
            drawPxRect(screenX + tailDir * 10f - 10f, screenY - 22f, 24f, 2.5f, if (isPurgatory) Color(0xEE141821) else Color(0xCC4ADE80))
            drawPxRect(screenX + tailDir * 14f - 12f, screenY - 14f, 28f, 2.5f, if (isPurgatory) Color(0xFF39FF88) else Color(0xEEFFFFFF))
            drawPxRect(screenX + tailDir * 9f - 9f, screenY - 7f, 22f, 2.5f, if (isPurgatory) Color(0xEEFFFFFF) else Color(0xBB22C55E))
        }

        // Front layer of Dragon Twister whirlwind (whipping emerald wind & razor blade arcs)
        if (isTatsumakiActive) {
            renderTatsumakiVortex(
                centerX = screenX,
                baseY = screenY,
                worldTime = worldTime,
                frontLayer = true,
                drawPxRect = drawPxRect
            )
        }
    }

    private fun renderSanjiAndBlackLeg(
        player: LuffyState,
        camX: Float,
        camY: Float,
        worldTime: Float,
        drawPxRect: (Float, Float, Float, Float, Color) -> Unit,
        drawPxSprite: (ImageBitmap, Float, Float, Float, Float) -> Unit
    ) {
        val isRight = player.facing == FacingDirection.RIGHT
        val dir = player.facing.sign.toFloat()
        val screenX = player.x - camX
        val screenY = player.y - camY

        val isDiableDashing = player.activeAttack == AttackType.SANJI_DIABLE_JAMBE &&
            player.attackPhase == AttackPhase.ACTIVE && !player.diableExplosionTriggered
        val spriteAlpha = if (player.invulnTimer > 0f && !isDiableDashing &&
            ((worldTime * 20f).toInt() % 2 == 0)
        ) {
            0.30f
        } else {
            1f
        }

        // Ground shadow
        val groundY = if (currentLevelNumber == 1 && player.x >= WorldConstants.PIER_START_X + 4f) {
            WorldConstants.PIER_GROUND_Y
        } else {
            WorldConstants.BEACH_GROUND_Y
        }
        val heightAboveGround = (groundY - player.y).coerceAtLeast(0f)
        val shadowScale = (1f - heightAboveGround / 120f).coerceIn(0.35f, 1f)
        val shadowW = 16f * shadowScale
        drawPxRect(
            screenX - shadowW * 0.5f,
            groundY - camY - 1.5f,
            shadowW,
            3f,
            Color(0x55181425)
        )

        val isPartySpinning = player.activeAttack == AttackType.SANJI_PARTY_TABLE &&
            player.attackPhase == AttackPhase.ACTIVE
        val isDiableActive = player.activeAttack == AttackType.SANJI_DIABLE_JAMBE &&
            (player.attackPhase == AttackPhase.WINDUP || player.attackPhase == AttackPhase.ACTIVE)

        // Back blur bands for Party Table Kick Course
        if (isPartySpinning) {
            val spinPhase = worldTime * 28f
            val r = 34f + sin(spinPhase) * 8f
            drawPxRect(screenX - r, screenY - 21f, r * 2f, 4f, Color(0x883B82F6))
            drawPxRect(screenX - r * 0.85f, screenY - 16f, r * 1.7f, 3f, Color(0xAAFFFFFF))
        }

        val bodySprite = when (player.animState) {
            PlayerAnimState.IDLE -> {
                val idx = ((player.animTime * 4.5f).toInt()) % sanjiSprites.idleFrames.size
                sanjiSprites.idleFrames[idx].forFacing(isRight)
            }
            PlayerAnimState.RUN -> {
                val idx = ((player.animTime * 13f).toInt()) % sanjiSprites.runFrames.size
                sanjiSprites.runFrames[idx].forFacing(isRight)
            }
            PlayerAnimState.JUMP_RISE -> sanjiSprites.jumpRise.forFacing(isRight)
            PlayerAnimState.JUMP_FALL -> sanjiSprites.jumpFall.forFacing(isRight)
            PlayerAnimState.LAND -> sanjiSprites.landSquash.forFacing(isRight)
            PlayerAnimState.HURT -> sanjiSprites.hurtPose.forFacing(isRight)
            PlayerAnimState.ATTACK_SANJI_KICK -> {
                val idx = player.sanjiComboStep.coerceIn(0, sanjiSprites.kickFrames.lastIndex)
                sanjiSprites.kickFrames[idx].forFacing(isRight)
            }
            PlayerAnimState.ATTACK_SANJI_PARTY_TABLE -> {
                val idx = ((player.animTime * 24f).toInt()) % sanjiSprites.partyTableFrames.size
                sanjiSprites.partyTableFrames[idx].forFacing(isRight)
            }
            PlayerAnimState.ATTACK_SANJI_DIABLE_JAMBE -> {
                if (player.attackPhase == AttackPhase.WINDUP) {
                    sanjiSprites.diableWindup.forFacing(isRight)
                } else {
                    sanjiSprites.diableDash.forFacing(isRight)
                }
            }
            else -> sanjiSprites.idleFrames[0].forFacing(isRight)
        }

        val spriteLeft = floor(screenX - bodySprite.width * 0.5f)
        val spriteTop = floor(screenY - bodySprite.height + 2f)

        // Draw Sanji sprite
        drawPxSprite(bodySprite, spriteLeft, spriteTop, 1f, spriteAlpha)

        // Front whirlwind kick streaks for Party Table Kick Course
        if (isPartySpinning) {
            val phase = worldTime * 32f
            for (k in 0..3) {
                val ang = phase + k * (PI.toFloat() * 0.5f)
                val kx = screenX + cos(ang) * 34f
                val ky = screenY - 20f + sin(ang) * 4f
                drawPxRect(kx - 7f, ky - 2f, 14f, 3f, Color(0xDDFFFFFF))
                drawPxRect(kx - 5f, ky - 1f, 10f, 1.5f, Color(0xEEFFD84D))
            }
        }

        // Animated blazing pixel flames wrapping Sanji's leg during Diable Jambe!
        if (isDiableActive) {
            val legX = if (player.attackPhase == AttackPhase.WINDUP) {
                screenX + dir * 7f
            } else {
                screenX + dir * 13f
            }
            val legY = if (player.attackPhase == AttackPhase.WINDUP) screenY - 9f else screenY - 15f
            val flicker = sin(worldTime * 38f) * 2.5f
            val red = Color(0xDDE62E2D)
            val orange = Color(0xEEFF8A1F)
            val yellow = Color(0xFFFFE45E)
            val core = Color(0xFFFFFBE6)

            // Layered jagged pixel flame tongues
            drawPxRect(legX - 7f, legY - 7f - flicker * 0.5f, 14f, 12f, red)
            drawPxRect(legX - 5f, legY - 10f + flicker * 0.4f, 10f, 12f, orange)
            drawPxRect(legX - 3f, legY - 6f, 6f, 8f, yellow)
            drawPxRect(legX - 2f, legY - 4f, 4f, 4f, core)

            // Fiery comet trail behind Sanji during dash
            if (isDiableDashing) {
                val tailDir = -dir
                drawPxRect(screenX + tailDir * 12f - 11f, screenY - 20f, 22f, 4f, Color(0xCCE62E2D))
                drawPxRect(screenX + tailDir * 15f - 12f, screenY - 16f, 24f, 3f, Color(0xEEFF8A1F))
                drawPxRect(screenX + tailDir * 9f - 8f, screenY - 13f, 16f, 2f, Color(0xFFFFFBE6))
            }
        }
    }

    private fun renderNamiAndClimaTact(
        player: LuffyState,
        camX: Float,
        camY: Float,
        worldTime: Float,
        drawPxRect: (Float, Float, Float, Float, Color) -> Unit,
        drawPxSprite: (ImageBitmap, Float, Float, Float, Float) -> Unit
    ) {
        val isRight = player.facing == FacingDirection.RIGHT
        val dir = player.facing.sign.toFloat()
        val screenX = player.x - camX
        val screenY = player.y - camY

        val spriteAlpha = if (player.invulnTimer > 0f && ((worldTime * 20f).toInt() % 2 == 0)) 0.30f else 1f

        val groundY = if (currentLevelNumber == 1 && player.x >= WorldConstants.PIER_START_X + 4f) {
            WorldConstants.PIER_GROUND_Y
        } else {
            WorldConstants.BEACH_GROUND_Y
        }
        val heightAboveGround = (groundY - player.y).coerceAtLeast(0f)
        val shadowScale = (1f - heightAboveGround / 120f).coerceIn(0.35f, 1f)
        val shadowW = 15f * shadowScale
        drawPxRect(screenX - shadowW * 0.5f, groundY - camY - 1.5f, shadowW, 3f, Color(0x55181425))

        val bodySprite = when (player.animState) {
            PlayerAnimState.IDLE -> {
                val idx = ((player.animTime * 4.5f).toInt()) % namiSprites.idleFrames.size
                namiSprites.idleFrames[idx].forFacing(isRight)
            }
            PlayerAnimState.RUN -> {
                val idx = ((player.animTime * 12f).toInt()) % namiSprites.runFrames.size
                namiSprites.runFrames[idx].forFacing(isRight)
            }
            PlayerAnimState.JUMP_RISE -> namiSprites.jumpRise.forFacing(isRight)
            PlayerAnimState.JUMP_FALL -> namiSprites.jumpFall.forFacing(isRight)
            PlayerAnimState.LAND -> namiSprites.landSquash.forFacing(isRight)
            PlayerAnimState.HURT -> namiSprites.hurtPose.forFacing(isRight)
            PlayerAnimState.ATTACK_NAMI_SPARK -> namiSprites.sparkPose.forFacing(isRight)
            PlayerAnimState.ATTACK_NAMI_CYCLONE -> namiSprites.cyclonePose.forFacing(isRight)
            PlayerAnimState.ATTACK_NAMI_THUNDERBOLT -> namiSprites.thunderPose.forFacing(isRight)
            else -> namiSprites.idleFrames[0].forFacing(isRight)
        }

        val spriteLeft = floor(screenX - bodySprite.width * 0.5f)
        val spriteTop = floor(screenY - bodySprite.height + 2f)
        drawPxSprite(bodySprite, spriteLeft, spriteTop, 1f, spriteAlpha)

        // Crackling electric spark flare strictly in front of the Clima-Tact tip during Spark thrust
        if (player.activeAttack == AttackType.NAMI_SPARK && player.attackPhase == AttackPhase.ACTIVE) {
            val tipX = screenX + dir * 16f
            val tipY = screenY - 14f
            val pulse = if (((worldTime * 36f).toInt() % 2) == 0) 1f else 0.72f
            drawPxRect(tipX - 4f, tipY - 4f, 8f, 8f, Color(0x667BE8FF).copy(alpha = 0.55f * pulse * spriteAlpha))
            drawPxRect(tipX - 2.5f, tipY - 2.5f, 5f, 5f, Color(0xFFFFE854).copy(alpha = 0.90f * spriteAlpha))
            drawPxRect(tipX - 1f, tipY - 1f, 2f, 2f, Color.White.copy(alpha = spriteAlpha))
        }

        // Weather orbs rendered strictly in front of (Cyclone) or above (Thunderbolt) her Clima-Tact tip
        if (player.activeAttack == AttackType.NAMI_CYCLONE || player.activeAttack == AttackType.NAMI_THUNDERBOLT) {
            val isThunder = player.activeAttack == AttackType.NAMI_THUNDERBOLT
            val orbCenterX = screenX + (if (isThunder) dir * 2f else dir * 16f)
            val orbCenterY = screenY - (if (isThunder) 34f else 17f)
            val orbColors = if (isThunder) {
                listOf(Color(0xFFFFE854), Color(0xFF7BE8FF), Color(0xFFFFFFFF))
            } else {
                listOf(Color(0xFF7BE8FF), Color(0xFF3284EE), Color(0xFFFFFFFF))
            }
            for (k in 0..2) {
                val ang = worldTime * 15f + k * (2f * PI.toFloat() / 3f)
                val ox = orbCenterX + cos(ang) * 7f
                val oy = orbCenterY + sin(ang) * 4f
                drawPxRect(ox - 2f, oy - 2f, 4f, 4f, orbColors[k])
                drawPxRect(ox - 1f, oy - 1f, 2f, 2f, Color.White)
            }
        }
    }

    private fun renderUsoppAndKabuto(
        player: LuffyState,
        camX: Float,
        camY: Float,
        worldTime: Float,
        drawPxRect: (Float, Float, Float, Float, Color) -> Unit,
        drawPxSprite: (ImageBitmap, Float, Float, Float, Float) -> Unit
    ) {
        val isRight = player.facing == FacingDirection.RIGHT
        val dir = player.facing.sign.toFloat()
        val screenX = player.x - camX
        val screenY = player.y - camY

        val spriteAlpha = if (player.invulnTimer > 0f && ((worldTime * 20f).toInt() % 2 == 0)) 0.30f else 1f

        val groundY = if (currentLevelNumber == 1 && player.x >= WorldConstants.PIER_START_X + 4f) {
            WorldConstants.PIER_GROUND_Y
        } else {
            WorldConstants.BEACH_GROUND_Y
        }
        val heightAboveGround = (groundY - player.y).coerceAtLeast(0f)
        val shadowScale = (1f - heightAboveGround / 120f).coerceIn(0.35f, 1f)
        val shadowW = 16f * shadowScale
        drawPxRect(screenX - shadowW * 0.5f, groundY - camY - 1.5f, shadowW, 3f, Color(0x55181425))

        val bodySprite = when (player.animState) {
            PlayerAnimState.IDLE -> {
                val idx = ((player.animTime * 4.5f).toInt()) % usoppSprites.idleFrames.size
                usoppSprites.idleFrames[idx].forFacing(isRight)
            }
            PlayerAnimState.RUN -> {
                val idx = ((player.animTime * 11.5f).toInt()) % usoppSprites.runFrames.size
                usoppSprites.runFrames[idx].forFacing(isRight)
            }
            PlayerAnimState.JUMP_RISE -> usoppSprites.jumpRise.forFacing(isRight)
            PlayerAnimState.JUMP_FALL -> usoppSprites.jumpFall.forFacing(isRight)
            PlayerAnimState.LAND -> usoppSprites.landSquash.forFacing(isRight)
            PlayerAnimState.HURT -> usoppSprites.hurtPose.forFacing(isRight)
            PlayerAnimState.ATTACK_USOPP_SHOT -> {
                if (player.attackPhase == AttackPhase.WINDUP) {
                    usoppSprites.aimPullPose.forFacing(isRight)
                } else {
                    usoppSprites.fireReleasePose.forFacing(isRight)
                }
            }
            PlayerAnimState.ATTACK_USOPP_EXPLODING_STAR,
            PlayerAnimState.ATTACK_USOPP_FIREBIRD_STAR -> {
                if (player.attackPhase == AttackPhase.WINDUP) {
                    usoppSprites.specialChargePose.forFacing(isRight)
                } else {
                    usoppSprites.fireReleasePose.forFacing(isRight)
                }
            }
            else -> usoppSprites.idleFrames[0].forFacing(isRight)
        }

        val spriteLeft = floor(screenX - bodySprite.width * 0.5f)
        val spriteTop = floor(screenY - bodySprite.height + 2f)
        drawPxSprite(bodySprite, spriteLeft, spriteTop, 1f, spriteAlpha)

        // Glowing Kabuto charge flare during Exploding Star / Firebird Star pullback!
        if ((player.activeAttack == AttackType.USOPP_EXPLODING_STAR || player.activeAttack == AttackType.USOPP_FIREBIRD_STAR) &&
            player.attackPhase == AttackPhase.WINDUP
        ) {
            val gx = screenX + dir * 8f
            val gy = screenY - 17f
            val r = if (player.activeAttack == AttackType.USOPP_FIREBIRD_STAR) 6f else 4f
            val pulse = if (((worldTime * 30f).toInt() % 2) == 0) 2f else 0f
            drawPxRect(gx - r - pulse, gy - r - pulse, (r + pulse) * 2f, (r + pulse) * 2f, Color(0x88FF6B1A))
            drawPxRect(gx - r * 0.6f, gy - r * 0.6f, r * 1.2f, r * 1.2f, Color(0xFFFFE45E))
            drawPxRect(gx - 1.5f, gy - 1.5f, 3f, 3f, Color.White)
        }
    }

    private fun renderChopperAndRumble(
        player: LuffyState,
        camX: Float,
        camY: Float,
        worldTime: Float,
        drawPxRect: (Float, Float, Float, Float, Color) -> Unit,
        drawPxSprite: (ImageBitmap, Float, Float, Float, Float) -> Unit
    ) {
        val isRight = player.facing == FacingDirection.RIGHT
        val screenX = player.x - camX
        val screenY = player.y - camY

        val spriteAlpha = if (player.invulnTimer > 0f && ((worldTime * 20f).toInt() % 2 == 0)) 0.30f else 1f

        val groundY = if (currentLevelNumber == 1 && player.x >= WorldConstants.PIER_START_X + 4f) {
            WorldConstants.PIER_GROUND_Y
        } else {
            WorldConstants.BEACH_GROUND_Y
        }
        val heightAboveGround = (groundY - player.y).coerceAtLeast(0f)
        val shadowScale = (1f - heightAboveGround / 120f).coerceIn(0.35f, 1f)

        val isGuardBall = player.activeAttack == AttackType.CHOPPER_GUARD_HEAL &&
            player.attackPhase != AttackPhase.WINDUP
        val isMonsterGiant = player.activeAttack == AttackType.CHOPPER_MONSTER_POINT &&
            player.attackPhase == AttackPhase.ACTIVE

        val baseShadowW = when {
            isMonsterGiant -> 40f
            isGuardBall -> 26f
            else -> 13f
        }
        val shadowW = baseShadowW * shadowScale
        drawPxRect(screenX - shadowW * 0.5f, groundY - camY - 1.5f, shadowW, 3f, Color(0x55181425))

        // Monster Point golden-crimson feral aura behind giant Chopper!
        if (isMonsterGiant) {
            val pulse = sin(worldTime * 24f) * 3f
            drawPxRect(screenX - 24f - pulse, screenY - 58f, 48f + pulse * 2f, 58f, Color(0x44FF8A1F))
            drawPxRect(screenX - 18f, screenY - 52f - pulse * 0.5f, 36f, 50f, Color(0x38FFE14D))
        }

        // Guard Point translucent emerald/pink protective shield halo behind giant fluff sphere
        if (isGuardBall) {
            val pulse = sin(worldTime * 14f) * 2f
            drawPxRect(screenX - 21f - pulse, screenY - 33f - pulse, 42f + pulse * 2f, 32f + pulse * 2f, Color(0x334CF57E))
            drawPxRect(screenX - 19f, screenY - 31f, 38f, 2f, Color(0xAA9EFFB8))
            drawPxRect(screenX - 19f, screenY - 2f, 38f, 2f, Color(0xAA9EFFB8))
        }

        val bodySprite: ImageBitmap
        val spriteScale: Float
        when (player.animState) {
            PlayerAnimState.IDLE -> {
                val idx = ((player.animTime * 5.0f).toInt()) % chopperSprites.idleFrames.size
                bodySprite = chopperSprites.idleFrames[idx].forFacing(isRight)
                spriteScale = 1f
            }
            PlayerAnimState.RUN -> {
                val idx = ((player.animTime * 14f).toInt()) % chopperSprites.runFrames.size
                bodySprite = chopperSprites.runFrames[idx].forFacing(isRight)
                spriteScale = 1f
            }
            PlayerAnimState.JUMP_RISE -> {
                bodySprite = chopperSprites.jumpRise.forFacing(isRight)
                spriteScale = 1f
            }
            PlayerAnimState.JUMP_FALL -> {
                bodySprite = chopperSprites.jumpFall.forFacing(isRight)
                spriteScale = 1f
            }
            PlayerAnimState.LAND -> {
                bodySprite = chopperSprites.landSquash.forFacing(isRight)
                spriteScale = 1f
            }
            PlayerAnimState.HURT -> {
                bodySprite = chopperSprites.hurtPose.forFacing(isRight)
                spriteScale = 1f
            }
            PlayerAnimState.ATTACK_CHOPPER_KUNGFU -> {
                bodySprite = if (player.chopperComboStep % 2 == 0) {
                    chopperSprites.kungFuJab.forFacing(isRight)
                } else {
                    chopperSprites.kungFuPalm.forFacing(isRight)
                }
                spriteScale = 1f
            }
            PlayerAnimState.ATTACK_CHOPPER_GUARD_HEAL -> {
                if (player.attackPhase == AttackPhase.WINDUP) {
                    bodySprite = chopperSprites.landSquash.forFacing(isRight)
                    spriteScale = 1f
                } else {
                    val idx = ((worldTime * 9f).toInt()) % chopperSprites.guardPointFrames.size
                    bodySprite = chopperSprites.guardPointFrames[idx].forFacing(isRight)
                    spriteScale = 1.05f
                }
            }
            PlayerAnimState.ATTACK_CHOPPER_MONSTER_POINT -> {
                when {
                    player.attackPhase == AttackPhase.WINDUP -> {
                        bodySprite = chopperSprites.rumbleEatPose.forFacing(isRight)
                        spriteScale = 1f
                    }
                    player.attackPhase == AttackPhase.ACTIVE -> {
                        bodySprite = if (player.monsterSlamTriggered) {
                            chopperSprites.monsterSlam.forFacing(isRight)
                        } else {
                            chopperSprites.monsterRoar.forFacing(isRight)
                        }
                        spriteScale = 1.18f
                    }
                    else -> {
                        bodySprite = chopperSprites.idleFrames[0].forFacing(isRight)
                        spriteScale = 1f
                    }
                }
            }
            else -> {
                bodySprite = chopperSprites.idleFrames[0].forFacing(isRight)
                spriteScale = 1f
            }
        }

        val drawW = bodySprite.width * spriteScale
        val drawH = bodySprite.height * spriteScale
        val spriteLeft = floor(screenX - drawW * 0.5f)
        val spriteTop = floor(screenY - drawH + 2f * spriteScale)
        drawPxSprite(bodySprite, spriteLeft, spriteTop, spriteScale, spriteAlpha)

        // Crisp white pixel impact streaks for Chopper's Kung Fu Point martial-arts combo!
        if (player.activeAttack == AttackType.CHOPPER_KUNGFU && player.attackPhase == AttackPhase.ACTIVE) {
            val dir = player.facing.sign.toFloat()
            val hitX = screenX + dir * 15f
            val hitY = screenY - 12f
            val flicker = if (((worldTime * 34f).toInt() % 2) == 0) 0f else 2f
            drawPxRect(hitX - 3f, hitY - 4f + flicker, 9f, 2f, Color(0xEEFFFFFF))
            drawPxRect(hitX - 1f, hitY + 1f - flicker, 11f, 2f, Color(0xCCD4DFF0))
            drawPxRect(hitX + dir * 3f - 1.5f, hitY - 2f, 3f, 3f, Color(0xFFFFE14D))
        }

        // Ground-shattering shockwave glow under Monster Point's colossal slamming paw!
        if (player.activeAttack == AttackType.CHOPPER_MONSTER_POINT &&
            player.attackPhase == AttackPhase.ACTIVE &&
            player.monsterSlamTriggered
        ) {
            val dir = player.facing.sign.toFloat()
            val slamX = screenX + dir * 18f
            val pulse = 0.65f + 0.35f * sin(worldTime * 30f)
            drawPxRect(slamX - 16f, screenY - 4f, 32f, 4f, Color(0xFFFF8A1F).copy(alpha = 0.65f * pulse))
            drawPxRect(slamX - 11f, screenY - 3f, 22f, 2.5f, Color(0xFFFFE14D).copy(alpha = 0.90f * pulse))
            drawPxRect(slamX - 6f, screenY - 2f, 12f, 1.5f, Color.White.copy(alpha = 0.95f * pulse))
        }

        // Glowing yellow Rumble Ball pill tossed into Chopper's mouth during Monster Point windup!
        if (player.activeAttack == AttackType.CHOPPER_MONSTER_POINT && player.attackPhase == AttackPhase.WINDUP) {
            val t = (player.attackTimer / 0.34f).coerceIn(0f, 1f)
            val arcY = -sin(t * PI.toFloat()) * 20f
            val pillX = screenX + (1f - t) * player.facing.sign * 6f
            val pillY = screenY - 16f + arcY
            drawPxRect(pillX - 4f, pillY - 4f, 8f, 8f, Color(0x77FFE14D))
            drawPxRect(pillX - 2.5f, pillY - 2.5f, 5f, 5f, pixelInk)
            drawPxRect(pillX - 1.5f, pillY - 1.5f, 3f, 3f, Color(0xFFFFE14D))
            drawPxRect(pillX - 1f, pillY - 1f, 1.5f, 1.5f, Color.White)
        }
    }

    private fun renderTatsumakiVortex(
        centerX: Float,
        baseY: Float,
        worldTime: Float,
        frontLayer: Boolean,
        drawPxRect: (Float, Float, Float, Float, Color) -> Unit
    ) {
        val levels = 11
        val darkGreen = Color(0xAA15803D)
        val emerald = Color(0xCC2EE66B)
        val mint = Color(0xDD9EF7BA)
        val white = Color(0xEEFFFFFF)

        for (i in 0 until levels) {
            val ratio = i.toFloat() / (levels - 1)
            val y = baseY - 4f - ratio * 84f
            // Funnel silhouette: tight at Zoro's feet, flaring wide overhead
            val radius = 14f + ratio * 36f
            val spinPhase = worldTime * 19f + i * 0.72f
            val sway = sin(spinPhase) * (3f + ratio * 4f)
            val bandW = radius * 2f

            if (!frontLayer) {
                // Back column swirl
                drawPxRect(centerX - radius + sway, y, bandW, 4f, darkGreen)
                val innerW = bandW * 0.65f
                drawPxRect(centerX - innerW * 0.5f - sway * 0.6f, y + 1f, innerW, 2f, emerald)
            } else {
                // Front high-speed slicing wind bands
                val offset = cos(spinPhase) * radius * 0.65f
                val slashW = (10f + ratio * 16f)
                drawPxRect(centerX + offset - slashW * 0.5f, y, slashW, 3f, mint)
                drawPxRect(centerX + offset - slashW * 0.35f, y + 1f, slashW * 0.7f, 1.5f, white)

                // Diagonal pixel blade-cut accents orbiting the vortex
                if (i % 2 == 0) {
                    val bladeX = centerX - offset * 0.85f
                    drawPxRect(bladeX - 6f, y - 2f, 5f, 2f, white)
                    drawPxRect(bladeX - 2f, y, 5f, 2f, emerald)
                    drawPxRect(bladeX + 2f, y + 2f, 5f, 2f, white)
                }
            }
        }
    }

    private fun renderSwordSlashVfx(
        slash: SwordSlashVfx,
        camX: Float,
        camY: Float,
        drawPxRect: (Float, Float, Float, Float, Color) -> Unit
    ) {
        val t = (slash.age / slash.maxAge).coerceIn(0f, 1f)
        val alpha = (1f - t * t).coerceIn(0f, 1f)
        if (alpha <= 0.02f) return

        val dir = slash.facing.sign.toFloat()
        val cx = slash.x - camX
        val cy = slash.y - camY

        val outerGreen = Color(0xFF16A34A).copy(alpha = alpha * 0.85f)
        val brightGreen = Color(0xFF4ADE80).copy(alpha = alpha)
        val razorWhite = Color(0xFFF0FFF4).copy(alpha = alpha)

        fun drawPixelDiagonal(x0: Float, y0: Float, x1: Float, y1: Float, thickness: Float) {
            val dx = x1 - x0
            val dy = y1 - y0
            val steps = (kotlin.math.hypot(dx.toDouble(), dy.toDouble()) / 2.2f).toInt().coerceIn(4, 34)
            for (i in 0..steps) {
                val u = i.toFloat() / steps
                val px = floor(x0 + dx * u)
                val py = floor(y0 + dy * u)
                // Taper ends to razor points
                val taper = sin(u * PI.toFloat()).coerceIn(0.25f, 1f)
                val outer = (thickness * 2.1f * taper).coerceAtLeast(2f)
                val inner = (thickness * 1.0f * taper).coerceAtLeast(1f)
                drawPxRect(px - outer * 0.5f, py - outer * 0.5f, outer, outer, outerGreen)
                drawPxRect(px - inner * 0.7f, py - inner * 0.7f, inner * 1.4f, inner * 1.4f, brightGreen)
                drawPxRect(px - inner * 0.4f, py - inner * 0.4f, inner * 0.8f, inner * 0.8f, razorWhite)
            }
        }

        fun drawPixelArc(startDeg: Float, endDeg: Float, rx: Float, ry: Float, thick: Float) {
            val steps = 16
            for (i in 0..steps) {
                val u = i.toFloat() / steps
                val deg = startDeg + (endDeg - startDeg) * u
                val rad = deg * (PI.toFloat() / 180f)
                val ax = cx + dir * (cos(rad) * rx)
                val ay = cy + sin(rad) * ry
                val taper = sin(u * PI.toFloat()).coerceIn(0.22f, 1f)
                val oSize = (thick * 2.1f * taper).coerceAtLeast(2f)
                val mSize = (thick * 1.35f * taper).coerceAtLeast(1.5f)
                val iSize = (thick * 0.7f * taper).coerceAtLeast(1f)
                drawPxRect(ax - oSize * 0.5f, ay - oSize * 0.5f, oSize, oSize, outerGreen)
                drawPxRect(ax - mSize * 0.5f, ay - mSize * 0.5f, mSize, mSize, brightGreen)
                drawPxRect(ax - iSize * 0.5f, ay - iSize * 0.5f, iSize, iSize, razorWhite)
            }
        }

        when (slash.kind) {
            SlashVfxKind.DOWN_ARC -> {
                val grow = 1f + t * 0.16f
                drawPixelArc(-78f, 68f, 20f * grow, 16f * grow, 2.8f * (1f - t * 0.4f))
            }
            SlashVfxKind.UP_ARC -> {
                val grow = 1f + t * 0.16f
                drawPixelArc(75f, -72f, 21f * grow, 16f * grow, 2.8f * (1f - t * 0.4f))
            }
            SlashVfxKind.CROSS_X -> {
                val grow = 1f + t * 0.14f
                val rx = 20f * grow
                val ry = 16f * grow
                val th = 2.6f * (1f - t * 0.45f)
                drawPixelDiagonal(cx - dir * rx * 0.7f, cy - ry, cx + dir * rx, cy + ry, th)
                drawPixelDiagonal(cx - dir * rx * 0.7f, cy + ry, cx + dir * rx, cy - ry, th)
                drawPixelArc(-60f, 60f, 23f * grow, 15f * grow, 1.8f * (1f - t * 0.5f))
            }
            SlashVfxKind.ONIGIRI_X -> {
                val grow = 1f + t * 0.22f
                val rx = 28f * grow
                val ry = 22f * grow
                val th = 3.6f * (1f - t * 0.5f)
                // Twin razor Santoryu Demon Slash X + horizontal third-blade cut
                drawPixelDiagonal(cx - rx, cy - ry, cx + rx, cy + ry, th)
                drawPixelDiagonal(cx - rx, cy + ry, cx + rx, cy - ry, th)
                drawPixelDiagonal(cx - rx * 1.15f, cy, cx + rx * 1.15f, cy, th * 0.75f)
                // Bright center flash diamond
                val core = (10f * (1f - t)).coerceAtLeast(2f)
                drawPxRect(cx - core, cy - 2f, core * 2f, 4f, razorWhite)
                drawPxRect(cx - 2f, cy - core, 4f, core * 2f, razorWhite)
            }
            SlashVfxKind.SANJI_KICK_ARC -> {
                val grow = 1f + t * 0.20f
                val kickBlue = Color(0xFF3B82F6).copy(alpha = alpha * 0.80f)
                val kickGold = Color(0xFFFFE45E).copy(alpha = alpha * 0.90f)
                val kickWhite = Color(0xFFFFFFFF).copy(alpha = alpha)
                val steps = 14
                for (i in 0..steps) {
                    val u = i.toFloat() / steps
                    val deg = -72f + 144f * u
                    val rad = deg * (PI.toFloat() / 180f)
                    val ax = cx + dir * (cos(rad) * 19f * grow)
                    val ay = cy + sin(rad) * 14f * grow
                    val taper = sin(u * PI.toFloat()).coerceIn(0.25f, 1f)
                    val oSize = (5.0f * taper).coerceAtLeast(2f)
                    val mSize = (3.2f * taper).coerceAtLeast(1.5f)
                    drawPxRect(ax - oSize * 0.5f, ay - oSize * 0.5f, oSize, oSize, kickBlue)
                    drawPxRect(ax - mSize * 0.5f, ay - mSize * 0.5f, mSize, mSize, kickGold)
                    drawPxRect(ax - 1f, ay - 1f, 2f, 2f, kickWhite)
                }
            }
            SlashVfxKind.DIABLE_EXPLOSION -> {
                // Explosive 16-bit fiery pixel nova for Diable Jambe!
                val outerR = (16f + t * 34f)
                val midR = (12f + t * 24f)
                val coreR = (10f * (1f - t * 0.7f)).coerceAtLeast(3f)
                val red = Color(0xFFE62E2D).copy(alpha = alpha * 0.85f)
                val orange = Color(0xFFFF8A1F).copy(alpha = alpha * 0.92f)
                val yellow = Color(0xFFFFE45E).copy(alpha = alpha)
                val white = Color(0xFFFFFBE6).copy(alpha = alpha)

                // Expanding flame ring of pixel bursts
                val lobes = 12
                for (i in 0 until lobes) {
                    val ang = (i.toFloat() / lobes) * (2f * PI.toFloat()) + t * 1.4f
                    val lx = cx + cos(ang) * outerR
                    val ly = cy + sin(ang) * (outerR * 0.72f)
                    val sz = (10f * (1f - t * 0.45f)).coerceAtLeast(3f)
                    drawPxRect(lx - sz * 0.5f, ly - sz * 0.5f, sz, sz, red)
                    drawPxRect(lx - sz * 0.35f, ly - sz * 0.35f, sz * 0.7f, sz * 0.7f, orange)
                }
                // Inner fiery blast core & cross flare
                drawPxRect(cx - midR, cy - midR * 0.7f, midR * 2f, midR * 1.4f, orange.copy(alpha = alpha * 0.65f))
                drawPxRect(cx - coreR * 1.4f, cy - coreR * 0.9f, coreR * 2.8f, coreR * 1.8f, yellow)
                drawPxRect(cx - coreR * 1.8f, cy - 2f, coreR * 3.6f, 4f, white)
                drawPxRect(cx - 2f, cy - coreR * 1.8f, 4f, coreR * 3.6f, white)
            }
            SlashVfxKind.BUGGY_BALL_EXPLOSION -> {
                // Massive multi-ring pixel Buggy Ball cannon detonation!
                val outerR = 20f + t * 48f
                val midR = 14f + t * 34f
                val coreR = (16f * (1f - t * 0.65f)).coerceAtLeast(4f)
                val darkRed = Color(0xFFB31926).copy(alpha = alpha * 0.88f)
                val orange = Color(0xFFFF6B1A).copy(alpha = alpha * 0.94f)
                val yellow = Color(0xFFFFE45E).copy(alpha = alpha)
                val white = Color(0xFFFFFFFF).copy(alpha = alpha)

                // Expanding shockwave smoke & fire ring
                val lobes = 16
                for (i in 0 until lobes) {
                    val ang = (i.toFloat() / lobes) * (2f * PI.toFloat()) - t * 0.8f
                    val lx = cx + cos(ang) * outerR
                    val ly = cy + sin(ang) * (outerR * 0.78f)
                    val sz = (14f * (1f - t * 0.42f)).coerceAtLeast(4f)
                    drawPxRect(lx - sz * 0.5f, ly - sz * 0.5f, sz, sz, darkRed)
                    drawPxRect(lx - sz * 0.36f, ly - sz * 0.36f, sz * 0.72f, sz * 0.72f, orange)
                }
                // Blazing fireball center + cross flash
                drawPxRect(cx - midR, cy - midR * 0.82f, midR * 2f, midR * 1.64f, orange.copy(alpha = alpha * 0.78f))
                drawPxRect(cx - coreR * 1.5f, cy - coreR * 1.15f, coreR * 3.0f, coreR * 2.3f, yellow)
                drawPxRect(cx - coreR * 2.4f, cy - 3f, coreR * 4.8f, 6f, white)
                drawPxRect(cx - 3f, cy - coreR * 2.2f, 6f, coreR * 4.4f, white)
            }
            SlashVfxKind.NAMI_SPARK_ARC -> {
                val grow = 1f + t * 0.22f
                val elecCyan = Color(0xFF38BDF8).copy(alpha = alpha * 0.85f)
                val elecGold = Color(0xFFFFF066).copy(alpha = alpha * 0.95f)
                val elecWhite = Color(0xFFFFFFFF).copy(alpha = alpha)
                val steps = 12
                for (i in 0..steps) {
                    val u = i.toFloat() / steps
                    val deg = -68f + 136f * u
                    val rad = deg * (PI.toFloat() / 180f)
                    val jag = if (i % 2 == 0) 2f else -2f
                    val ax = cx + dir * (cos(rad) * 18f * grow + jag)
                    val ay = cy + sin(rad) * 14f * grow
                    drawPxRect(ax - 2.5f, ay - 2.5f, 5f, 5f, elecCyan)
                    drawPxRect(ax - 1.5f, ay - 1.5f, 3f, 3f, elecGold)
                    drawPxRect(ax - 1f, ay - 1f, 2f, 2f, elecWhite)
                }
            }
            SlashVfxKind.EXPLODING_STAR_BLAST -> {
                // Crisp 16-bit starburst explosion for Usopp's Exploding Star!
                val outerR = 14f + t * 34f
                val midR = 10f + t * 22f
                val coreR = (12f * (1f - t * 0.65f)).coerceAtLeast(3f)
                val crimson = Color(0xFFE62E2D).copy(alpha = alpha * 0.85f)
                val orange = Color(0xFFFF7A1A).copy(alpha = alpha * 0.94f)
                val yellow = Color(0xFFFFE854).copy(alpha = alpha)
                val white = Color(0xFFFFFFFF).copy(alpha = alpha)

                // 8-point starburst spikes
                for (i in 0 until 8) {
                    val ang = (i.toFloat() / 8f) * (2f * PI.toFloat()) + t * 0.6f
                    val lx = cx + cos(ang) * outerR
                    val ly = cy + sin(ang) * outerR * 0.82f
                    val sz = (10f * (1f - t * 0.4f)).coerceAtLeast(3f)
                    drawPxRect(lx - sz * 0.5f, ly - sz * 0.5f, sz, sz, crimson)
                    drawPxRect(lx - sz * 0.35f, ly - sz * 0.35f, sz * 0.7f, sz * 0.7f, orange)
                }
                drawPxRect(cx - midR, cy - midR * 0.8f, midR * 2f, midR * 1.6f, orange.copy(alpha = alpha * 0.72f))
                drawPxRect(cx - coreR * 1.4f, cy - coreR * 1.1f, coreR * 2.8f, coreR * 2.2f, yellow)
                drawPxRect(cx - coreR * 2.1f, cy - 2.5f, coreR * 4.2f, 5f, white)
                drawPxRect(cx - 2.5f, cy - coreR * 2.0f, 5f, coreR * 4.0f, white)
            }
            SlashVfxKind.KIRIBATI_SLASH -> {
                // Massive jagged saw-tooth crimson/steel cleave arc from Arlong's Kiribati!
                val grow = 1f + t * 0.28f
                val crimson = Color(0xFFD92638).copy(alpha = alpha * 0.85f)
                val steel = Color(0xFFD8E8F8).copy(alpha = alpha * 0.94f)
                val white = Color(0xFFFFFFFF).copy(alpha = alpha)
                val steps = 16
                for (i in 0..steps) {
                    val u = i.toFloat() / steps
                    val deg = -82f + 164f * u
                    val rad = deg * (PI.toFloat() / 180f)
                    val sawTooth = if (i % 2 == 0) 4f else -2f
                    val ax = cx + dir * (cos(rad) * (32f + sawTooth) * grow)
                    val ay = cy + sin(rad) * 26f * grow
                    drawPxRect(ax - 4f, ay - 4f, 8f, 8f, crimson)
                    drawPxRect(ax - 2.5f, ay - 2.5f, 5f, 5f, steel)
                    drawPxRect(ax - 1f, ay - 1f, 2f, 2f, white)
                }
            }
            SlashVfxKind.CHOPPER_KUNGFU_ARC -> {
                val grow = 1f + t * 0.20f
                val pink = Color(0xFFFF78B4).copy(alpha = alpha * 0.85f)
                val gold = Color(0xFFFFE14D).copy(alpha = alpha * 0.94f)
                val white = Color(0xFFFFFFFF).copy(alpha = alpha)
                val steps = 11
                for (i in 0..steps) {
                    val u = i.toFloat() / steps
                    val deg = -64f + 128f * u
                    val rad = deg * (PI.toFloat() / 180f)
                    val ax = cx + dir * (cos(rad) * 16f * grow)
                    val ay = cy + sin(rad) * 12f * grow
                    val taper = sin(u * PI.toFloat()).coerceIn(0.25f, 1f)
                    val oSize = (4.6f * taper).coerceAtLeast(2f)
                    drawPxRect(ax - oSize * 0.5f, ay - oSize * 0.5f, oSize, oSize, pink)
                    drawPxRect(ax - 1.5f, ay - 1.5f, 3f, 3f, gold)
                    drawPxRect(ax - 0.8f, ay - 0.8f, 1.6f, 1.6f, white)
                }
            }
            SlashVfxKind.MONSTER_SHOCKWAVE -> {
                // Colossal Monster Point paw slam shockwave dome & seismic ground pillars!
                val spreadX = 18f + t * 68f
                val pillarH = (32f * (1f - t * 0.55f)).coerceAtLeast(6f)
                val brown = Color(0xFF9C5E2E).copy(alpha = alpha * 0.85f)
                val gold = Color(0xFFFFD84D).copy(alpha = alpha * 0.92f)
                val white = Color(0xFFFFFFFF).copy(alpha = alpha)

                // Twin expanding shockwave walls racing left & right
                for (side in listOf(-1f, 1f)) {
                    val wx = cx + side * spreadX
                    drawPxRect(wx - 7f, cy - pillarH, 14f, pillarH + 6f, brown)
                    drawPxRect(wx - 4.5f, cy - pillarH * 0.85f, 9f, pillarH * 0.85f + 4f, gold)
                    drawPxRect(wx - 2f, cy - pillarH * 0.65f, 4f, pillarH * 0.65f + 2f, white)
                }
                // Ground earthquake blast flash across impact zone
                drawPxRect(cx - spreadX, cy - 4f, spreadX * 2f, 8f, gold.copy(alpha = alpha * 0.65f))
                drawPxRect(cx - spreadX * 0.72f, cy - 2f, spreadX * 1.44f, 4f, white)
            }
            SlashVfxKind.GEAR4_SHOCKWAVE -> {
                // Expanding Busoshoku Haki crimson-black Conqueror/Gear 4 activation ring!
                val outerR = 16f + t * 88f
                val innerR = 10f + t * 62f
                val crimson = Color(0xFFE62E4D).copy(alpha = alpha * 0.92f)
                val obsidian = Color(0xFF181425).copy(alpha = alpha * 0.85f)
                val white = Color(0xFFFFFFFF).copy(alpha = alpha)
                val lobes = 16
                for (i in 0 until lobes) {
                    val ang = (i.toFloat() / lobes) * (2f * PI.toFloat()) + t * 0.8f
                    val ox = cx + cos(ang) * outerR
                    val oy = cy + sin(ang) * (outerR * 0.58f)
                    val sz = (10f * (1f - t * 0.45f)).coerceAtLeast(3f)
                    drawPxRect(ox - sz * 0.5f, oy - sz * 0.5f, sz, sz, obsidian)
                    drawPxRect(ox - sz * 0.35f, oy - sz * 0.35f, sz * 0.7f, sz * 0.7f, crimson)
                }
                drawPxRect(cx - innerR, cy - 3f, innerR * 2f, 6f, crimson.copy(alpha = alpha * 0.7f))
                drawPxRect(cx - innerR * 0.7f, cy - 1.5f, innerR * 1.4f, 3f, white)
            }
            SlashVfxKind.KING_KONG_BLAST -> {
                // Cataclysmic King Kong Gun ground shockwave & twin crimson-gold Haki pillars!
                val spreadX = 24f + t * 112f
                val pillarH = (46f * (1f - t * 0.52f)).coerceAtLeast(8f)
                val obsidian = Color(0xFF181425).copy(alpha = alpha * 0.90f)
                val crimson = Color(0xFFE62E4D).copy(alpha = alpha * 0.95f)
                val gold = Color(0xFFFFE45E).copy(alpha = alpha)
                val white = Color(0xFFFFFFFF).copy(alpha = alpha)
                for (side in listOf(-1f, 1f)) {
                    val wx = cx + side * spreadX
                    drawPxRect(wx - 9f, cy - pillarH, 18f, pillarH + 6f, obsidian)
                    drawPxRect(wx - 6f, cy - pillarH * 0.88f, 12f, pillarH * 0.88f + 4f, crimson)
                    drawPxRect(wx - 3f, cy - pillarH * 0.72f, 6f, pillarH * 0.72f + 2f, gold)
                    drawPxRect(wx - 1.5f, cy - pillarH * 0.5f, 3f, pillarH * 0.5f, white)
                }
                drawPxRect(cx - spreadX, cy - 6f, spreadX * 2f, 10f, crimson.copy(alpha = alpha * 0.78f))
                drawPxRect(cx - spreadX * 0.82f, cy - 3f, spreadX * 1.64f, 5f, gold)
                drawPxRect(cx - spreadX * 0.55f, cy - 1.5f, spreadX * 1.1f, 3f, white)
            }
            SlashVfxKind.PARRY_FLASH -> {
                // Dazzling Steel Parry starburst + expanding steel-gold clash ring!
                val ray = 14f + t * 34f
                val gold = Color(0xFFFFF066).copy(alpha = alpha * 0.92f)
                val white = Color(0xFFFFFFFF).copy(alpha = alpha)
                drawPxRect(cx - ray, cy - 2f, ray * 2f, 4f, gold)
                drawPxRect(cx - 2f, cy - ray, 4f, ray * 2f, gold)
                drawPxRect(cx - ray * 0.75f, cy - 1f, ray * 1.5f, 2f, white)
                drawPxRect(cx - 1f, cy - ray * 0.75f, 2f, ray * 1.5f, white)
                val diag = ray * 0.62f
                for (i in -5..5) {
                    val u = i / 5f
                    drawPxRect(cx + u * diag - 1.5f, cy + u * diag - 1.5f, 3f, 3f, white)
                    drawPxRect(cx + u * diag - 1.5f, cy - u * diag - 1.5f, 3f, 3f, white)
                }
            }
            SlashVfxKind.HELL_DOWN_ARC,
            SlashVfxKind.HELL_UP_ARC -> {
                val grow = 1f + t * 0.20f
                val startDeg = if (slash.kind == SlashVfxKind.HELL_DOWN_ARC) -84f else 82f
                val endDeg = if (slash.kind == SlashVfxKind.HELL_DOWN_ARC) 76f else -80f
                val hellDark = Color(0xFF10141D).copy(alpha = alpha * 0.94f)
                val hellGreen = Color(0xFF39FF88).copy(alpha = alpha * 0.96f)
                val hellWhite = Color(0xFFE8FFF2).copy(alpha = alpha)
                val steps = 16
                for (i in 0..steps) {
                    val u = i.toFloat() / steps
                    val deg = startDeg + (endDeg - startDeg) * u
                    val rad = deg * (PI.toFloat() / 180f)
                    val ax = cx + dir * (cos(rad) * 28f * grow)
                    val ay = cy + sin(rad) * 21f * grow
                    val taper = sin(u * PI.toFloat()).coerceIn(0.25f, 1f)
                    val oSz = (7.2f * taper).coerceAtLeast(2.5f)
                    val mSz = (4.4f * taper).coerceAtLeast(1.8f)
                    drawPxRect(ax - oSz * 0.5f, ay - oSz * 0.5f, oSz, oSz, hellDark)
                    drawPxRect(ax - mSz * 0.5f, ay - mSz * 0.5f, mSz, mSz, hellGreen)
                    drawPxRect(ax - 1f, ay - 1f, 2f, 2f, hellWhite)
                }
            }
            SlashVfxKind.HELL_CROSS_X,
            SlashVfxKind.PURGATORY_ONIGIRI_X -> {
                val isPurgatory = slash.kind == SlashVfxKind.PURGATORY_ONIGIRI_X
                val grow = 1f + t * (if (isPurgatory) 0.30f else 0.18f)
                val rx = (if (isPurgatory) 40f else 28f) * grow
                val ry = (if (isPurgatory) 30f else 22f) * grow
                val hellDark = Color(0xFF10141D).copy(alpha = alpha * 0.94f)
                val hellGreen = Color(0xFF39FF88).copy(alpha = alpha * 0.98f)
                val hellWhite = Color(0xFFFFFFFF).copy(alpha = alpha)
                val steps = 16
                for (i in 0..steps) {
                    val u = i.toFloat() / steps
                    val x1 = (cx - rx) + 2f * rx * u
                    val y1 = (cy - ry) + 2f * ry * u
                    val y2 = (cy + ry) - 2f * ry * u
                    val taper = sin(u * PI.toFloat()).coerceIn(0.25f, 1f)
                    val oSz = (if (isPurgatory) 8.5f else 6.5f) * taper
                    val mSz = oSz * 0.6f
                    drawPxRect(x1 - oSz * 0.5f, y1 - oSz * 0.5f, oSz, oSz, hellDark)
                    drawPxRect(x1 - mSz * 0.5f, y1 - mSz * 0.5f, mSz, mSz, hellGreen)
                    drawPxRect(x1 - 1f, y1 - 1f, 2f, 2f, hellWhite)
                    drawPxRect(x1 - oSz * 0.5f, y2 - oSz * 0.5f, oSz, oSz, hellDark)
                    drawPxRect(x1 - mSz * 0.5f, y2 - mSz * 0.5f, mSz, mSz, hellGreen)
                    drawPxRect(x1 - 1f, y2 - 1f, 2f, 2f, hellWhite)
                }
                if (isPurgatory) {
                    drawPxRect(cx - rx * 1.2f, cy - 2.5f, rx * 2.4f, 5f, hellDark)
                    drawPxRect(cx - rx * 1.1f, cy - 1.5f, rx * 2.2f, 3f, hellGreen)
                    drawPxRect(cx - rx * 0.85f, cy - 0.8f, rx * 1.7f, 1.6f, hellWhite)
                }
            }
            SlashVfxKind.ZORO_HELL_SHOCKWAVE -> {
                // Expanding King of Hell obsidian-emerald Conqueror Haki ring!
                val outerR = 18f + t * 98f
                val innerR = 12f + t * 68f
                val obsidian = Color(0xFF10141D).copy(alpha = alpha * 0.92f)
                val hellGreen = Color(0xFF39FF88).copy(alpha = alpha * 0.95f)
                val white = Color(0xFFFFFFFF).copy(alpha = alpha)
                val lobes = 16
                for (i in 0 until lobes) {
                    val ang = (i.toFloat() / lobes) * (2f * PI.toFloat()) - t * 0.9f
                    val ox = cx + cos(ang) * outerR
                    val oy = cy + sin(ang) * (outerR * 0.58f)
                    val sz = (11f * (1f - t * 0.45f)).coerceAtLeast(3f)
                    drawPxRect(ox - sz * 0.5f, oy - sz * 0.5f, sz, sz, obsidian)
                    drawPxRect(ox - sz * 0.36f, oy - sz * 0.36f, sz * 0.72f, sz * 0.72f, hellGreen)
                }
                drawPxRect(cx - innerR, cy - 3.5f, innerR * 2f, 7f, hellGreen.copy(alpha = alpha * 0.75f))
                drawPxRect(cx - innerR * 0.7f, cy - 1.5f, innerR * 1.4f, 3f, white)
            }
            SlashVfxKind.ASURA_NINE_BLADES -> {
                // Nine colossal intersecting purple-emerald blades tearing across the entire screen!
                val spanX = 110f + t * 185f
                val spanY = 54f + t * 44f
                val dark = Color(0xFF1A0F2E).copy(alpha = alpha * 0.90f)
                val violet = Color(0xFFA84BFF).copy(alpha = alpha * 0.94f)
                val emerald = Color(0xFF39FF88).copy(alpha = alpha * 0.96f)
                val white = Color(0xFFFFFFFF).copy(alpha = alpha)
                val offsets = listOf(-42f, -26f, -12f, 0f, 12f, 26f, 42f)
                for ((idx, offY) in offsets.withIndex()) {
                    val tilt = ((idx % 3) - 1) * 18f
                    val steps = 16
                    val accent = if (idx % 2 == 0) violet else emerald
                    for (s in 0..steps) {
                        val u = s.toFloat() / steps
                        val px = (cx - spanX) + 2f * spanX * u
                        val py = cy + offY + (u - 0.5f) * tilt
                        val oSz = (5.5f * (1f - t * 0.45f)).coerceAtLeast(2f)
                        drawPxRect(px - oSz * 0.5f, py - oSz * 0.5f, oSz, oSz, dark)
                        drawPxRect(px - oSz * 0.32f, py - oSz * 0.32f, oSz * 0.64f, oSz * 0.64f, accent)
                        if (idx == 3) {
                            drawPxRect(px - 1f, py - 1f, 2f, 2f, white)
                        }
                    }
                }
                // Central Asura X-cross flash
                drawPxRect(cx - spanX * 0.7f, cy - 2f, spanX * 1.4f, 4f, white)
                drawPxRect(cx - 3f, cy - spanY, 6f, spanY * 2f, violet)
            }
            SlashVfxKind.ASURA_DELAYED_CUT -> {
                // Dramatic anime delayed slash detonation bursting in violet, emerald & blinding white!
                val len = 24f + t * 38f
                val violet = Color(0xFFB55CFF).copy(alpha = alpha * 0.94f)
                val emerald = Color(0xFF39FF88).copy(alpha = alpha * 0.96f)
                val white = Color(0xFFFFFFFF).copy(alpha = alpha)
                for (s in -8..8) {
                    val u = s / 8f
                    drawPxRect(cx + u * len - 3f, cy + u * len * 0.72f - 3f, 6f, 6f, violet)
                    drawPxRect(cx + u * len - 1.5f, cy + u * len * 0.72f - 1.5f, 3f, 3f, white)
                    drawPxRect(cx + u * len - 3f, cy - u * len * 0.72f - 3f, 6f, 6f, emerald)
                    drawPxRect(cx + u * len - 1.5f, cy - u * len * 0.72f - 1.5f, 3f, 3f, white)
                }
                val core = (14f * (1f - t * 0.6f)).coerceAtLeast(3f)
                drawPxRect(cx - core, cy - 2f, core * 2f, 4f, white)
                drawPxRect(cx - 2f, cy - core, 4f, core * 2f, white)
            }
        }
    }

    private fun drawRubberSegment(
        x0: Float,
        y0: Float,
        x1: Float,
        y1: Float,
        alpha: Float = 1f,
        drawPxRect: (Float, Float, Float, Float, Color) -> Unit
    ) {
        val dx = x1 - x0
        val dy = y1 - y0
        val dist = kotlin.math.hypot(dx.toDouble(), dy.toDouble()).toFloat()
        val count = (dist / 1.5f).toInt().coerceIn(2, 160)
        val ink = pixelInk.copy(alpha = alpha)
        val skin = rubberSkin.copy(alpha = alpha)
        val shade = rubberShade.copy(alpha = alpha)

        for (i in 0..count) {
            val t = i.toFloat() / count
            val px = floor(x0 + dx * t)
            val py = floor(y0 + dy * t)
            drawPxRect(px - 2f, py - 2f, 4f, 4f, ink)
        }
        for (i in 0..count) {
            val t = i.toFloat() / count
            val px = floor(x0 + dx * t)
            val py = floor(y0 + dy * t)
            drawPxRect(px - 1f, py - 1f, 2f, 1f, skin)
            drawPxRect(px - 1f, py, 2f, 1f, shade)
        }
    }

    private fun drawGatlingStreakLine(
        x0: Float,
        y0: Float,
        x1: Float,
        y1: Float,
        color: Color,
        drawPxRect: (Float, Float, Float, Float, Color) -> Unit
    ) {
        val dx = x1 - x0
        val dy = y1 - y0
        val dist = kotlin.math.hypot(dx.toDouble(), dy.toDouble()).toFloat()
        val count = (dist / 2.5f).toInt().coerceIn(2, 100)
        for (i in 0..count) {
            val t = i.toFloat() / count
            val px = floor(x0 + dx * t)
            val py = floor(y0 + dy * t)
            drawPxRect(px - 1f, py - 1f, 2f, 2f, color)
        }
    }
}
