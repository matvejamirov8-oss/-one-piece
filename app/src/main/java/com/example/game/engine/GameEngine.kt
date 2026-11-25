package com.example.game.engine

import androidx.compose.ui.graphics.Color
import com.example.game.audio.RetroSfx
import com.example.game.audio.RetroSoundEngine
import com.example.game.model.ArlongStagePhase
import com.example.game.model.AttackBannerState
import com.example.game.model.AttackHitbox
import com.example.game.model.AttackPhase
import com.example.game.model.AttackType
import com.example.game.model.BossAttackVariant
import com.example.game.model.BuggyPartEntity
import com.example.game.model.CloudEntity
import com.example.game.model.CooldownState
import com.example.game.model.DelayedSlashEffect
import com.example.game.model.DestructibleProp
import com.example.game.model.DestructiblePropKind
import com.example.game.model.EnemyBehaviorState
import com.example.game.model.EnemyKind
import com.example.game.model.EnemyState
import com.example.game.model.FacingDirection
import com.example.game.model.FloatingDamageText
import com.example.game.model.GatlingPunchGhost
import com.example.game.model.GroundCrater
import com.example.game.model.InputSnapshot
import com.example.game.model.LevelPhase
import com.example.game.model.LootItem
import com.example.game.model.LootKind
import com.example.game.model.LuffyState
import com.example.game.model.Particle
import com.example.game.model.ParticleKind
import com.example.game.model.PlayableHero
import com.example.game.model.PlayerAnimState
import com.example.game.model.PlayerProjectile
import com.example.game.model.PlayerProjectileKind
import com.example.game.model.Projectile
import com.example.game.model.ProjectileKind
import com.example.game.model.Rect2D
import com.example.game.model.SeagullEntity
import com.example.game.model.SkyWalkRingVfx
import com.example.game.model.SlashVfxKind
import com.example.game.model.SwordSlashVfx
import com.example.game.model.ThunderCloudEntity
import com.example.game.model.WaveBannerState
import com.example.game.model.WorldConstants
import com.example.game.model.WorldState
import com.example.game.model.ZoroAfterimage
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.random.Random

/**
 * Pure Kotlin 2D Side-Scrolling Action Game Engine for Gomu Arena (Level 1 East Blue & Level 2 Orange Town).
 * Manages responsive physics, Luffy/Zoro/Sanji combat, enemy waves, Captain Morgan & Captain Buggy bosses,
 * hitstop freeze frames, screen shake & flash, loot drops, floating pixel damage numbers, and 1:1 camera.
 */
class GameEngine(
    private val soundEngine: RetroSoundEngine,
    private val combatSystem: CombatSystem = CombatSystem()
) {
    private val rng = Random(2026)
    private val enemyAi = EnemyAiSystem(rng)

    private var nextEntityId: Long = 1L

    var state: WorldState = createInitialWorld(levelNumber = 1, initialPhase = LevelPhase.MAIN_MENU)
        private set

    fun startLevel(levelNumber: Int) {
        val keepHitboxes = state.showHitboxes
        state = createInitialWorld(levelNumber = levelNumber, initialPhase = LevelPhase.PLAYING)
            .copy(showHitboxes = keepHitboxes)
    }

    fun openMainMenu() {
        val keepHitboxes = state.showHitboxes
        state = createInitialWorld(levelNumber = state.levelNumber, initialPhase = LevelPhase.MAIN_MENU)
            .copy(showHitboxes = keepHitboxes)
    }

    fun restartLevel(levelNumber: Int = state.levelNumber) {
        startLevel(levelNumber)
    }

    private fun createInitialWorld(
        levelNumber: Int,
        initialPhase: LevelPhase = LevelPhase.PLAYING
    ): WorldState {
        val levelDef = LevelCatalog.getLevel(levelNumber)
        val isLevel3 = levelDef.levelNumber == 3
        val initialLuffy = LuffyState(
            x = if (isLevel3) 118f else 240f,
            y = WorldConstants.BEACH_GROUND_Y,
            facing = FacingDirection.RIGHT
        )
        val firstWave = levelDef.waves.firstOrNull()
        val initialEnemies = if (initialPhase == LevelPhase.PLAYING && firstWave != null && !isLevel3) {
            spawnWaveEnemies(firstWave.spawns, initialLuffy.x, levelDef.levelNumber)
        } else {
            emptyList()
        }
        val regularWaveCount = levelDef.waves.count { !it.isBossWave }.coerceAtLeast(1)

        return WorldState(
            luffy = initialLuffy,
            levelNumber = levelDef.levelNumber,
            levelTitle = levelDef.title,
            waveIndex = 0,
            totalRegularWaves = regularWaveCount,
            waveClearedDelay = 0f,
            levelPhase = initialPhase,
            waveBanner = if (initialPhase == LevelPhase.PLAYING) {
                firstWave?.let {
                    WaveBannerState(text = it.bannerTitle, isBoss = it.isBossWave, remainingSeconds = 2.4f)
                }
            } else {
                null
            },
            enemies = initialEnemies,
            clouds = listOf(
                CloudEntity(x = 40f, y = 20f, speed = 5.5f, variant = 0),
                CloudEntity(x = 180f, y = 34f, speed = 3.8f, variant = 1),
                CloudEntity(x = 340f, y = 16f, speed = 6.2f, variant = 0),
                CloudEntity(x = 510f, y = 28f, speed = 4.4f, variant = 1),
                CloudEntity(x = 650f, y = 18f, speed = 5.0f, variant = 0)
            ),
            seagulls = listOf(
                SeagullEntity(x = 120f, y = 52f, speed = 18f, wingPhase = 0f),
                SeagullEntity(x = 420f, y = 44f, speed = -15f, wingPhase = 1.6f),
                SeagullEntity(x = 610f, y = 58f, speed = 21f, wingPhase = 3.1f)
            ),
            cameraX = if (isLevel3) 0f else 80f,
            arlongPhase = if (isLevel3) ArlongStagePhase.GATE_INTRO else ArlongStagePhase.NONE,
            arlongGateHp = if (isLevel3) 1 else 0,
            arlongProps = if (isLevel3) buildArlongMapRoomProps() else emptyList()
        )
    }

    private fun buildArlongMapRoomProps(): List<DestructibleProp> = listOf(
        DestructibleProp("prop_pillar_1", DestructiblePropKind.STONE_PILLAR, 148f, WorldConstants.BEACH_GROUND_Y, 22f, 44f, 40, 40),
        DestructibleProp("prop_shelf_1", DestructiblePropKind.MAP_SHELF, 232f, WorldConstants.BEACH_GROUND_Y, 30f, 36f, 36, 36),
        DestructibleProp("prop_desk", DestructiblePropKind.NAMI_DESK, 378f, WorldConstants.BEACH_GROUND_Y, 36f, 24f, 34, 34),
        DestructibleProp("prop_shelf_2", DestructiblePropKind.MAP_SHELF, 514f, WorldConstants.BEACH_GROUND_Y, 30f, 36f, 36, 36),
        DestructibleProp("prop_pillar_2", DestructiblePropKind.STONE_PILLAR, 608f, WorldConstants.BEACH_GROUND_Y, 22f, 44f, 40, 40)
    )

    private fun spawnWaveEnemies(
        specs: List<com.example.game.model.EnemySpawnSpec>,
        playerX: Float,
        levelNumber: Int = state.levelNumber
    ): List<EnemyState> {
        return specs.map { spec ->
            var spawnX = (playerX + spec.offsetFromPlayer).coerceIn(
                WorldConstants.LEFT_BOUNDARY_X + 18f,
                WorldConstants.RIGHT_BOUNDARY_X - 18f
            )
            // Never spawn directly on top of hero
            if (abs(spawnX - playerX) < 68f) {
                val fallbackSign = if (playerX > WorldConstants.WORLD_WIDTH * 0.5f) -1f else 1f
                spawnX = (playerX + fallbackSign * 110f).coerceIn(
                    WorldConstants.LEFT_BOUNDARY_X + 18f,
                    WorldConstants.RIGHT_BOUNDARY_X - 18f
                )
            }
            val groundY = groundYAtForLevel(spawnX, levelNumber)
            val facing = if (playerX < spawnX) FacingDirection.LEFT else FacingDirection.RIGHT
            EnemyState(
                id = "enemy_${nextEntityId++}",
                kind = spec.kind,
                x = spawnX,
                y = groundY,
                facing = facing,
                behavior = if (spec.kind == EnemyKind.MARINE_SOLDIER) {
                    EnemyBehaviorState.PATROL
                } else {
                    EnemyBehaviorState.CHASE
                },
                attackCooldown = 0.45f + rng.nextFloat() * 0.45f,
                patrolOriginX = spawnX,
                patrolDir = facing.sign
            )
        }
    }

    fun toggleHitboxes() {
        state = state.copy(showHitboxes = !state.showHitboxes)
    }

    private fun groundYAtForLevel(x: Float, levelNumber: Int): Float {
        if (levelNumber != 1) return WorldConstants.BEACH_GROUND_Y
        return when {
            x < WorldConstants.PIER_START_X -> WorldConstants.BEACH_GROUND_Y
            x < WorldConstants.PIER_START_X + 12f -> {
                val t = (x - WorldConstants.PIER_START_X) / 12f
                WorldConstants.BEACH_GROUND_Y + (WorldConstants.PIER_GROUND_Y - WorldConstants.BEACH_GROUND_Y) * t
            }
            else -> WorldConstants.PIER_GROUND_Y
        }
    }

    fun groundYAt(x: Float): Float = groundYAtForLevel(x, state.levelNumber)

    fun isOnPier(x: Float): Boolean = state.levelNumber == 1 && x >= WorldConstants.PIER_START_X + 4f

    fun step(rawDt: Float, input: InputSnapshot, viewportVirtualWidth: Float) {
        val realDt = rawDt.coerceIn(0.001f, 0.05f)
        val current = state
        val isSlowMoFinisher = current.levelNumber == 3 &&
            current.levelPhase == LevelPhase.PLAYING &&
            current.arlongPhase == ArlongStagePhase.FINISHER_SLOWMO
        val dt = if (isSlowMoFinisher) realDt * 0.28f else realDt
        val worldTime = current.worldTime + dt

        var shakeIntensity = current.shakeIntensity
        var shakeDuration = current.shakeDuration
        var shakeElapsed = current.shakeElapsed
        var screenFlashTimer = (current.screenFlashTimer - realDt).coerceAtLeast(0f)
        var screenFlashColor = current.screenFlashColor

        fun triggerShake(intensity: Float, duration: Float) {
            if (intensity >= shakeIntensity || shakeElapsed >= shakeDuration) {
                shakeIntensity = intensity
                shakeDuration = duration
                shakeElapsed = 0f
            }
        }

        fun triggerFlash(duration: Float, color: Color) {
            screenFlashTimer = maxOf(screenFlashTimer, duration)
            screenFlashColor = color
        }

        // === HITSTOP MICRO-FREEZE FOR IMPACTFUL HITS ===
        if (current.hitStopTimer > 0f) {
            val nextHitStop = (current.hitStopTimer - dt).coerceAtLeast(0f)
            var shakeOffX = 0f
            var shakeOffY = 0f
            if (shakeElapsed < shakeDuration && shakeDuration > 0f) {
                shakeElapsed += dt
                val decay = (1f - (shakeElapsed / shakeDuration)).coerceIn(0f, 1f)
                val mag = shakeIntensity * decay
                shakeOffX = (rng.nextFloat() * 2f - 1f) * mag
                shakeOffY = (rng.nextFloat() * 2f - 1f) * mag
            }
            state = current.copy(
                worldTime = worldTime,
                hitStopTimer = nextHitStop,
                shakeElapsed = shakeElapsed,
                shakeOffsetX = shakeOffX,
                shakeOffsetY = shakeOffY,
                screenFlashTimer = screenFlashTimer,
                screenFlashColor = screenFlashColor
            )
            return
        }

        val particles = current.particles.toMutableList()
        val slashVfxs = current.slashVfxs.mapNotNull { s ->
            val nextAge = s.age + dt
            if (nextAge < s.maxAge) s.copy(age = nextAge) else null
        }.toMutableList()
        val delayedSlashes = current.delayedSlashes.toMutableList()
        val skyWalkRings = current.skyWalkRings.mapNotNull { r ->
            val nextAge = r.age + dt
            if (nextAge < r.maxAge) r.copy(age = nextAge) else null
        }.toMutableList()
        val zoroAfterimages = current.zoroAfterimages.mapNotNull { g ->
            val nextAge = g.age + dt
            if (nextAge < g.maxAge) g.copy(age = nextAge) else null
        }.toMutableList()
        val craters = current.craters.toMutableList()
        val projectiles = current.projectiles.toMutableList()
        val playerProjectiles = current.playerProjectiles.toMutableList()
        val thunderClouds = current.thunderClouds.toMutableList()
        val buggyParts = current.buggyParts.toMutableList()
        val lootItems = current.lootItems.toMutableList()
        val floatingTexts = current.floatingTexts.toMutableList()
        val activeHitboxes = mutableListOf<AttackHitbox>()
        var hitStopTimer = 0f

        var banner = current.banner?.let {
            val rem = it.remainingSeconds - dt
            if (rem > 0f) it.copy(remainingSeconds = rem) else null
        }
        var waveBanner = current.waveBanner?.let {
            val rem = it.remainingSeconds - dt
            if (rem > 0f) it.copy(remainingSeconds = rem) else null
        }

        // If Main Menu, Level Complete, or Game Over, gently animate ambient sky/particles/texts only
        if (current.levelPhase != LevelPhase.PLAYING) {
            val aliveParticles = updateParticles(particles, dt)
            val aliveTexts = updateFloatingTexts(floatingTexts, dt)
            val updatedClouds = updateClouds(current.clouds, dt)
            val updatedSeagulls = updateSeagulls(current.seagulls, dt)
            val (sOffX, sOffY, nextSI, nextSD, nextSE) = updateShake(
                shakeIntensity, shakeDuration, shakeElapsed, dt
            )
            state = current.copy(
                particles = aliveParticles,
                slashVfxs = slashVfxs,
                floatingTexts = aliveTexts,
                clouds = updatedClouds,
                seagulls = updatedSeagulls,
                worldTime = worldTime,
                shakeIntensity = nextSI,
                shakeDuration = nextSD,
                shakeElapsed = nextSE,
                shakeOffsetX = sOffX,
                shakeOffsetY = sOffY,
                screenFlashTimer = screenFlashTimer,
                screenFlashColor = screenFlashColor,
                banner = banner,
                waveBanner = waveBanner,
                activeHitboxes = emptyList()
            )
            return
        }

        var luffy = current.luffy
        var levelPhase = current.levelPhase
        var waveIndex = current.waveIndex
        var waveClearedDelay = current.waveClearedDelay

        // Level 3 Arlong Park mutable state for this tick
        val isLevel3 = current.levelNumber == 3
        var arlongPhase = current.arlongPhase
        var arlongGateHp = current.arlongGateHp
        var arlongGateFlash = (current.arlongGateFlash - realDt).coerceAtLeast(0f)
        var luffyDrowning = current.luffyDrowning
        var luffyDrownTimer = current.luffyDrownTimer
        var luffyDrownX = current.luffyDrownX
        var towerDestruction = current.towerDestruction
        val arlongProps = current.arlongProps.map { p ->
            if (p.flashTimer > 0f) p.copy(flashTimer = (p.flashTimer - realDt).coerceAtLeast(0f)) else p
        }.toMutableList()
        var arlongTransitionTimer = current.arlongTransitionTimer
        var arlongCollapseTimer = current.arlongCollapseTimer
        var arlongCollapseX = current.arlongCollapseX

        // === LEVEL 3 CINEMATIC: TOWER TRANSITION (Courtyard -> Nami's Map Room) ===
        if (isLevel3 && arlongPhase == ArlongStagePhase.TOWER_TRANSITION) {
            val prevT = arlongTransitionTimer
            arlongTransitionTimer += realDt
            val nextEnemies = current.enemies.map { e ->
                if (e.kind == EnemyKind.BOSS_ARLONG) {
                    if (arlongTransitionTimer < 0.72f) {
                        e.copy(y = (e.y - 300f * realDt).coerceAtLeast(48f), isGrounded = false, vx = 0f, vy = -260f)
                    } else {
                        e.copy(
                            x = 492f.coerceIn(WorldConstants.ARLONG_MAP_ROOM_MIN_X + 20f, WorldConstants.ARLONG_MAP_ROOM_MAX_X - 20f),
                            y = WorldConstants.BEACH_GROUND_Y,
                            vx = 0f,
                            vy = 0f,
                            isGrounded = true,
                            facing = FacingDirection.LEFT,
                            behavior = EnemyBehaviorState.CHASE,
                            stunTimer = 0f,
                            attackCooldown = 0.85f,
                            stateTimer = 0f,
                            attackHitApplied = false
                        )
                    }
                } else e
            }
            if (prevT < 0.72f && arlongTransitionTimer >= 0.72f) {
                triggerFlash(0.26f, Color(0xFFFFFFFF))
                triggerShake(7.0f, 0.35f)
                soundEngine.play(RetroSfx.KIRIBATI_SLASH, 0.96f)
                luffyDrowning = false
                luffyDrownTimer = 5.0f
                luffy = luffy.copy(
                    x = 264f,
                    y = WorldConstants.BEACH_GROUND_Y,
                    vx = 0f,
                    vy = 0f,
                    isGrounded = true,
                    facing = FacingDirection.RIGHT,
                    hurtTimer = 0f,
                    invulnTimer = maxOf(luffy.invulnTimer, 0.75f),
                    activeAttack = null,
                    attackPhase = AttackPhase.NONE,
                    attackTimer = 0f,
                    stretchFactor = 0f,
                    gatlingGhosts = emptyList()
                )
                spawnMapPaperBurst(particles, 380f, 130f, count = 20)
            }
            if (arlongTransitionTimer >= 1.28f) {
                arlongPhase = ArlongStagePhase.ARLONG_PHASE2
                luffyDrowning = false
                luffyDrownTimer = 5.0f
                luffy = luffy.copy(
                    x = luffy.x.coerceIn(WorldConstants.ARLONG_MAP_ROOM_MIN_X + 16f, WorldConstants.ARLONG_MAP_ROOM_MAX_X - 16f),
                    y = WorldConstants.BEACH_GROUND_Y,
                    vx = 0f,
                    vy = 0f,
                    isGrounded = true,
                    hurtTimer = 0f
                )
                waveBanner = WaveBannerState("ФАЗА 2: КОМНАТА КАРТ • КИРИБАТИ!", isBoss = true, remainingSeconds = 2.6f)
            }
            val aliveParticles = updateParticles(particles, realDt)
            val aliveTexts = updateFloatingTexts(floatingTexts, realDt)
            val effectiveViewW = viewportVirtualWidth.coerceIn(60f, WorldConstants.WORLD_WIDTH)
            val smoothCamX = (luffy.x - effectiveViewW * 0.5f)
                .coerceIn(0f, (WorldConstants.WORLD_WIDTH - effectiveViewW).coerceAtLeast(0f))
            val (sOffX, sOffY, fSI, fSD, fSE) = updateShake(shakeIntensity, shakeDuration, shakeElapsed, realDt)
            state = current.copy(
                luffy = luffy,
                enemies = nextEnemies,
                projectiles = emptyList(),
                playerProjectiles = emptyList(),
                thunderClouds = emptyList(),
                craters = emptyList(),
                buggyParts = emptyList(),
                particles = aliveParticles,
                floatingTexts = aliveTexts,
                waveBanner = waveBanner,
                cameraX = smoothCamX,
                worldTime = worldTime,
                shakeIntensity = fSI,
                shakeDuration = fSD,
                shakeElapsed = fSE,
                shakeOffsetX = sOffX,
                shakeOffsetY = sOffY,
                screenFlashTimer = screenFlashTimer,
                screenFlashColor = screenFlashColor,
                arlongPhase = arlongPhase,
                luffyDrowning = luffyDrowning,
                luffyDrownTimer = luffyDrownTimer,
                arlongTransitionTimer = arlongTransitionTimer
            )
            return
        }

        // === LEVEL 3 CINEMATIC: EPIC TOWER COLLAPSE FINALE ===
        if (isLevel3 && arlongPhase == ArlongStagePhase.TOWER_COLLAPSE) {
            val prevC = arlongCollapseTimer
            arlongCollapseTimer += realDt
            towerDestruction = 1f
            // Periodic booming floor smashes as Arlong crashes down every floor of Arlong Park!
            val prevFloor = (prevC / 0.48f).toInt()
            val curFloor = (arlongCollapseTimer / 0.48f).toInt()
            if (curFloor > prevFloor && curFloor <= 4) {
                soundEngine.play(if (curFloor % 2 == 0) RetroSfx.TOWER_COLLAPSE else RetroSfx.GATE_SMASH, 0.96f)
                triggerShake(10.5f, 0.42f)
                triggerFlash(0.14f, Color(0xFFFFF3C4))
                spawnMapPaperBurst(particles, arlongCollapseX, 120f + rng.nextFloat() * 45f, count = 16)
                spawnStoneRubbleBurst(particles, arlongCollapseX, 150f, count = 14)
            }
            if (rng.nextFloat() < 0.78f) {
                spawnMapPaperBurst(particles, arlongCollapseX + (rng.nextFloat() * 140f - 70f), 90f + rng.nextFloat() * 75f, count = 2)
                spawnStoneRubbleBurst(particles, arlongCollapseX + (rng.nextFloat() * 110f - 55f), 140f + rng.nextFloat() * 35f, count = 2)
            }
            if (arlongCollapseTimer >= 2.65f) {
                levelPhase = LevelPhase.LEVEL_COMPLETE
                soundEngine.play(RetroSfx.VICTORY_FANFARE, 0.98f)
                triggerShake(8.0f, 0.45f)
                triggerFlash(0.22f, Color(0xFFFFF4C2))
                waveBanner = WaveBannerState(
                    text = "АРЛОНГ-ПАРК ПОВЕРЖЕН!",
                    isBoss = true,
                    remainingSeconds = 3.2f
                )
            }
            val aliveParticles = updateParticles(particles, realDt)
            val aliveTexts = updateFloatingTexts(floatingTexts, realDt)
            val (sOffX, sOffY, fSI, fSD, fSE) = updateShake(shakeIntensity, shakeDuration, shakeElapsed, realDt)
            state = current.copy(
                luffy = luffy.copy(defeatedCount = if (levelPhase == LevelPhase.LEVEL_COMPLETE) luffy.defeatedCount + 1 else luffy.defeatedCount),
                enemies = if (levelPhase == LevelPhase.LEVEL_COMPLETE) emptyList() else current.enemies,
                projectiles = emptyList(),
                playerProjectiles = emptyList(),
                particles = aliveParticles,
                slashVfxs = slashVfxs,
                floatingTexts = aliveTexts,
                levelPhase = levelPhase,
                waveBanner = waveBanner,
                worldTime = worldTime,
                shakeIntensity = fSI,
                shakeDuration = fSD,
                shakeElapsed = fSE,
                shakeOffsetX = sOffX,
                shakeOffsetY = sOffY,
                screenFlashTimer = screenFlashTimer,
                screenFlashColor = screenFlashColor,
                towerDestruction = towerDestruction,
                arlongCollapseTimer = arlongCollapseTimer
            )
            return
        }

        fun spawnSwitchBurst(x: Float, y: Float, targetHero: PlayableHero) {
            val accent = when (targetHero) {
                PlayableHero.LUFFY -> Color(0xFFFFD84D)
                PlayableHero.ZORO -> Color(0xFF4CE87A)
                PlayableHero.SANJI -> Color(0xFFFF8A1F)
                PlayableHero.NAMI -> Color(0xFF5CE1FF)
                PlayableHero.USOPP -> Color(0xFFE8C547)
                PlayableHero.CHOPPER -> Color(0xFFFF78B4)
            }
            repeat(12) { i ->
                val angle = (i.toFloat() / 12f) * (2f * PI.toFloat())
                val spd = 34f + rng.nextFloat() * 44f
                particles.add(
                    Particle(
                        x = x + cos(angle) * 4f,
                        y = y - 14f + sin(angle) * 6f,
                        vx = cos(angle) * spd,
                        vy = sin(angle) * spd - 14f,
                        size = 4f + rng.nextFloat() * 3f,
                        color = if (i % 3 == 0) accent else Color(0xFFF5F8FC),
                        age = 0f,
                        maxAge = 0.28f + rng.nextFloat() * 0.12f,
                        kind = ParticleKind.DUST
                    )
                )
            }
            spawnImpactSparkBurst(particles, x, y - 15f, count = 6)
        }

        fun switchToHero(targetHero: PlayableHero, forcedByKo: Boolean = false) {
            if (luffy.activeHero == targetHero) return
            val targetHp = luffy.heroHp[targetHero] ?: targetHero.maxHp
            if (targetHp <= 0) return
            val updatedHpMap = luffy.heroHp.toMutableMap().apply {
                this[luffy.activeHero] = luffy.currentHp
            }
            // If switching OFF drowning Luffy, place the rescuer cleanly beside the glowing Rescue Zone!
            val switchingFromDrowningLuffy = isLevel3 && luffyDrowning &&
                luffy.activeHero == PlayableHero.LUFFY && targetHero != PlayableHero.LUFFY
            val nextHeroX = if (switchingFromDrowningLuffy) {
                if (luffyDrownX < 380f) WorldConstants.ARLONG_POOL_LEFT_X - 48f
                else WorldConstants.ARLONG_POOL_RIGHT_X + 48f
            } else {
                luffy.x
            }
            val nextHeroY = if (switchingFromDrowningLuffy) WorldConstants.BEACH_GROUND_Y else luffy.y

            luffy = luffy.copy(
                activeHero = targetHero,
                heroHp = updatedHpMap,
                currentHp = targetHp,
                maxHp = targetHero.maxHp,
                x = nextHeroX,
                y = nextHeroY,
                vx = if (switchingFromDrowningLuffy) 0f else luffy.vx,
                vy = if (switchingFromDrowningLuffy) 0f else luffy.vy,
                isGrounded = if (switchingFromDrowningLuffy) true else luffy.isGrounded,
                activeAttack = null,
                attackPhase = AttackPhase.NONE,
                attackTimer = 0f,
                attackHitTargets = emptySet(),
                stretchFactor = 0f,
                gatlingGhosts = emptyList(),
                axeImpactTriggered = false,
                onigiriSlashTriggered = false,
                diableExplosionTriggered = false,
                guardHealTriggered = false,
                monsterRoarTriggered = false,
                monsterSlamTriggered = false,
                kingKongImpactTriggered = false,
                purgatoryDashStage = 0,
                asuraSlashesTriggered = false,
                attackFired = false,
                hurtTimer = 0f,
                invulnTimer = if (forcedByKo) 1.25f else maxOf(luffy.invulnTimer, 0.28f),
                switchCooldown = 0.24f,
                switchFlashTimer = 0.22f
            )
            spawnSwitchBurst(luffy.x, luffy.y, targetHero)
            when (targetHero) {
                PlayableHero.ZORO -> soundEngine.play(RetroSfx.ZORO_DRAW, 0.90f)
                PlayableHero.SANJI -> soundEngine.play(RetroSfx.SANJI_SKYWALK, 0.84f)
                PlayableHero.NAMI -> soundEngine.play(RetroSfx.NAMI_SPARK, 0.85f)
                PlayableHero.USOPP -> soundEngine.play(RetroSfx.USOPP_SLING_DRAW, 0.88f)
                PlayableHero.CHOPPER -> soundEngine.play(RetroSfx.CHOPPER_GUARD_PUFF, 0.86f)
                PlayableHero.LUFFY -> soundEngine.play(RetroSfx.CHAR_SWITCH, 0.88f)
            }
            val tagColor = when (targetHero) {
                PlayableHero.LUFFY -> Color(0xFFFFD84D)
                PlayableHero.ZORO -> Color(0xFF5CF288)
                PlayableHero.SANJI -> Color(0xFFFF9E3D)
                PlayableHero.NAMI -> Color(0xFF5CE1FF)
                PlayableHero.USOPP -> Color(0xFFFFE066)
                PlayableHero.CHOPPER -> Color(0xFFFF78B4)
            }
            floatingTexts.add(
                FloatingDamageText(
                    id = nextEntityId++,
                    x = luffy.x,
                    y = luffy.y - 36f,
                    text = "${targetHero.displayName}!",
                    color = tagColor,
                    isLarge = forcedByKo
                )
            )
        }

        fun applyDamageToLuffy(
            damage: Int,
            knockbackDir: Float,
            shake: Float,
            hitStop: Float,
            knockbackSpeed: Float = 112f
        ) {
            // Invulnerable while dashing through enemies during Onigiri / Purgatory Onigiri / Asura / Diable Jambe, Guard Point fur ball, Monster Point, or Finisher Slow-Mo
            val isOnigiriDash = (luffy.activeAttack == AttackType.ZORO_ONIGIRI ||
                luffy.activeAttack == AttackType.ZORO_PURGATORY_ONIGIRI ||
                luffy.activeAttack == AttackType.ZORO_ASURA) &&
                luffy.attackPhase == AttackPhase.ACTIVE
            val isDiableDash = luffy.activeAttack == AttackType.SANJI_DIABLE_JAMBE &&
                luffy.attackPhase == AttackPhase.ACTIVE && !luffy.diableExplosionTriggered
            val isGuardPoint = luffy.activeAttack == AttackType.CHOPPER_GUARD_HEAL &&
                (luffy.attackPhase == AttackPhase.WINDUP || luffy.attackPhase == AttackPhase.ACTIVE)
            val isMonsterPointArmor = luffy.activeAttack == AttackType.CHOPPER_MONSTER_POINT &&
                luffy.attackPhase == AttackPhase.ACTIVE
            // Zoro Passive: Steel Parry deflects incoming attack if Zoro is actively swinging his katanas!
            val isZoroSteelParryWindow = luffy.activeHero == PlayableHero.ZORO &&
                (luffy.activeAttack == AttackType.ZORO_SLASH ||
                    luffy.activeAttack == AttackType.ZORO_HELL_SLASH ||
                    luffy.activeAttack == AttackType.ZORO_TATSUMAKI) &&
                (luffy.attackPhase == AttackPhase.WINDUP || luffy.attackPhase == AttackPhase.ACTIVE)
            if (isZoroSteelParryWindow && luffy.isAlive && levelPhase == LevelPhase.PLAYING) {
                val meterBoost = if (!luffy.zoroHellActive) {
                    (luffy.zoroHellMeter + 22f).coerceAtMost(WorldConstants.ZORO_HELL_MAX_METER)
                } else {
                    luffy.zoroHellMeter
                }
                luffy = luffy.copy(
                    invulnTimer = maxOf(luffy.invulnTimer, 0.45f),
                    zoroParryCritReady = true,
                    zoroHellMeter = meterBoost
                )
                soundEngine.play(RetroSfx.ZORO_PARRY_CLANG, 1.0f)
                triggerShake(5.5f, 0.24f)
                triggerFlash(0.15f, Color.White)
                hitStopTimer = maxOf(hitStopTimer, 0.065f)
                slashVfxs.add(
                    SwordSlashVfx(
                        id = nextEntityId++,
                        x = luffy.x + luffy.facing.sign * 14f,
                        y = luffy.y - 16f,
                        facing = luffy.facing,
                        kind = SlashVfxKind.PARRY_FLASH,
                        maxAge = 0.26f
                    )
                )
                spawnImpactSparkBurst(particles, luffy.x + luffy.facing.sign * 14f, luffy.y - 16f, count = 14)
                floatingTexts.add(
                    FloatingDamageText(
                        id = nextEntityId++,
                        x = luffy.x,
                        y = luffy.y - 38f,
                        text = "ПАРИРОВАНИЕ! 100% КРИТ!",
                        color = Color(0xFFFFFFFF),
                        isLarge = true
                    )
                )
                return
            }
            if (isGuardPoint && luffy.isAlive && levelPhase == LevelPhase.PLAYING) {
                soundEngine.play(RetroSfx.CHOPPER_GUARD_PUFF, 0.90f)
                triggerShake(2.2f, 0.14f)
                spawnDustBurst(particles, luffy.x, luffy.y - 12f, count = 6, isPier = false)
                floatingTexts.add(
                    FloatingDamageText(
                        id = nextEntityId++,
                        x = luffy.x,
                        y = luffy.y - 34f,
                        text = "БЛОК!",
                        color = Color(0xFFFF9FD0),
                        isLarge = false
                    )
                )
                return
            }
            if (luffy.invulnTimer > 0f || isOnigiriDash || isDiableDash || isMonsterPointArmor || isSlowMoFinisher || !luffy.isAlive || levelPhase != LevelPhase.PLAYING) return

            // Gear 4: Boundman Busoshoku Haki Armor — 45% damage reduction + knockback/flinch immunity on light hits!
            val isGear4Armored = luffy.activeHero == PlayableHero.LUFFY && luffy.gear4Active
            val isHeavyEnemyBlow = damage >= 22 || shake >= 6.0f
            val finalDamage = if (isGear4Armored) {
                (damage * 0.55f).toInt().coerceAtLeast(1)
            } else {
                damage
            }
            val nextHp = (luffy.currentHp - finalDamage).coerceAtLeast(0)
            val updatedHeroHp = luffy.heroHp.toMutableMap().apply {
                this[luffy.activeHero] = nextHp
            }

            if (isGear4Armored && !isHeavyEnemyBlow && nextHp > 0) {
                // Haki Armor absorbs knockback & flinch completely on light enemy hits!
                luffy = luffy.copy(
                    currentHp = nextHp,
                    heroHp = updatedHeroHp,
                    invulnTimer = 0.45f
                )
                soundEngine.play(RetroSfx.GEAR4_HAKI_HIT, 0.65f)
                triggerShake(2.2f, 0.14f)
                repeat(7) {
                    val ang = rng.nextFloat() * 2f * PI.toFloat()
                    val spd = 45f + rng.nextFloat() * 65f
                    particles.add(
                        Particle(
                            x = luffy.x + cos(ang) * 4f,
                            y = luffy.y - 16f + sin(ang) * 4f,
                            vx = cos(ang) * spd,
                            vy = sin(ang) * spd - 12f,
                            size = 2.6f,
                            color = if (rng.nextBoolean()) Color(0xFFFF4D6D) else Color(0xFF1F1629),
                            age = 0f,
                            maxAge = 0.22f,
                            kind = ParticleKind.IMPACT_SPARK
                        )
                    )
                }
                floatingTexts.add(
                    FloatingDamageText(
                        id = nextEntityId++,
                        x = luffy.x,
                        y = luffy.y - 36f,
                        text = "-$finalDamage ХАКИ",
                        color = Color(0xFFFF7A90),
                        isLarge = false
                    )
                )
                return
            }

            val appliedKnockback = if (isGear4Armored) knockbackSpeed * 0.45f else knockbackSpeed
            luffy = luffy.copy(
                currentHp = nextHp,
                heroHp = updatedHeroHp,
                hurtTimer = if (isGear4Armored) 0.15f else 0.26f,
                invulnTimer = 1.25f,
                activeAttack = null,
                attackPhase = AttackPhase.NONE,
                attackTimer = 0f,
                stretchFactor = 0f,
                gatlingGhosts = emptyList(),
                diableExplosionTriggered = false,
                kingKongImpactTriggered = false,
                vx = knockbackDir * appliedKnockback,
                vy = if (appliedKnockback > 180f) -132f else -96f,
                isGrounded = false
            )
            soundEngine.play(RetroSfx.PLAYER_HURT, 0.90f)
            triggerShake(shake.coerceAtLeast(4.8f), 0.28f)
            hitStopTimer = maxOf(hitStopTimer, hitStop)
            spawnImpactSparkBurst(particles, luffy.x, luffy.y - 14f, count = 7)
            floatingTexts.add(
                FloatingDamageText(
                    id = nextEntityId++,
                    x = luffy.x,
                    y = luffy.y - 32f,
                    text = if (isGear4Armored) "-$finalDamage ХАКИ" else "-$finalDamage",
                    color = Color(0xFFFF4D4D),
                    isLarge = finalDamage >= 18
                )
            )
            if (nextHp <= 0) {
                if (luffy.activeHero == PlayableHero.LUFFY && luffy.gear4Active) {
                    luffy = luffy.copy(gear4Active = false, gear4Timer = 0f, gear4Meter = 0f)
                }
                if (luffy.activeHero == PlayableHero.ZORO && luffy.zoroHellActive) {
                    luffy = luffy.copy(zoroHellActive = false, zoroHellTimer = 0f, zoroHellMeter = 0f, zoroParryCritReady = false)
                }
                val allHeroes = PlayableHero.entries
                val nextAlive = (1 until allHeroes.size)
                    .map { allHeroes[(luffy.activeHero.ordinal + it) % allHeroes.size] }
                    .firstOrNull { (updatedHeroHp[it] ?: 0) > 0 }
                if (nextAlive != null) {
                    switchToHero(nextAlive, forcedByKo = true)
                    triggerShake(5.5f, 0.30f)
                } else {
                    levelPhase = LevelPhase.GAME_OVER
                    soundEngine.play(RetroSfx.GAME_OVER, 0.92f)
                    triggerShake(7.2f, 0.40f)
                }
            }
        }

        // 1. Update Cooldowns (all 9 Luffy, Zoro & Sanji skills tick independently) & Timers
        val updatedCooldowns = luffy.cooldowns.mapValues { (_, cd) ->
            cd.copy(remaining = (cd.remaining - dt).coerceAtLeast(0f))
        }
        val nextInvuln = (luffy.invulnTimer - dt).coerceAtLeast(0f)
        val nextHurt = (luffy.hurtTimer - dt).coerceAtLeast(0f)
        val nextSwitchCd = (luffy.switchCooldown - dt).coerceAtLeast(0f)
        val nextSwitchFlash = (luffy.switchFlashTimer - dt).coerceAtLeast(0f)
        val nextZoroComboReset = (luffy.zoroComboResetTimer - dt).coerceAtLeast(0f)
        val nextZoroComboStep = if (nextZoroComboReset <= 0f) 0 else luffy.zoroComboStep
        val nextSanjiComboReset = (luffy.sanjiComboResetTimer - dt).coerceAtLeast(0f)
        val nextSanjiComboStep = if (nextSanjiComboReset <= 0f) 0 else luffy.sanjiComboStep
        val nextChopperComboReset = (luffy.chopperComboResetTimer - dt).coerceAtLeast(0f)
        val nextChopperComboStep = if (nextChopperComboReset <= 0f) 0 else luffy.chopperComboStep
        val newCoyote = if (luffy.isGrounded) WorldConstants.COYOTE_TIME else (luffy.coyoteTimer - dt).coerceAtLeast(0f)
        val newJumpBuffer = if (input.jumpJustPressed) WorldConstants.JUMP_BUFFER_TIME else (luffy.jumpBufferTimer - dt).coerceAtLeast(0f)
        val newLandTimer = (luffy.landTimer - dt).coerceAtLeast(0f)

        // Gear 4: Boundman duration countdown & clean reversion
        var nextGear4Active = luffy.gear4Active
        var nextGear4Timer = luffy.gear4Timer
        var nextGear4Meter = luffy.gear4Meter
        var revertedFromGear4 = false
        if (nextGear4Active) {
            nextGear4Timer = (nextGear4Timer - dt).coerceAtLeast(0f)
            nextGear4Meter = (nextGear4Timer / WorldConstants.GEAR4_DURATION) * WorldConstants.GEAR4_MAX_METER
            if (luffy.activeHero == PlayableHero.LUFFY && rng.nextFloat() < 0.35f) {
                particles.add(
                    Particle(
                        x = luffy.x + (rng.nextFloat() * 22f - 11f),
                        y = luffy.y - 16f - rng.nextFloat() * 14f,
                        vx = (rng.nextFloat() * 18f - 9f),
                        vy = -22f - rng.nextFloat() * 16f,
                        size = 3.5f + rng.nextFloat() * 2.5f,
                        color = if (rng.nextFloat() < 0.72f) Color(0xFFF4F8FF) else Color(0xFFFF5978),
                        age = 0f,
                        maxAge = 0.34f + rng.nextFloat() * 0.14f,
                        kind = ParticleKind.DUST
                    )
                )
            }
            if (nextGear4Timer <= 0f) {
                nextGear4Active = false
                nextGear4Timer = 0f
                nextGear4Meter = 0f
                revertedFromGear4 = true
            }
        }

        // Zoro King of Hell (Black Bandana) duration countdown & clean reversion
        var nextZoroHellActive = luffy.zoroHellActive
        var nextZoroHellTimer = luffy.zoroHellTimer
        var nextZoroHellMeter = luffy.zoroHellMeter
        var revertedFromZoroHell = false
        if (nextZoroHellActive) {
            nextZoroHellTimer = (nextZoroHellTimer - dt).coerceAtLeast(0f)
            nextZoroHellMeter = (nextZoroHellTimer / WorldConstants.ZORO_HELL_DURATION) * WorldConstants.ZORO_HELL_MAX_METER
            if (luffy.activeHero == PlayableHero.ZORO && rng.nextFloat() < 0.55f) {
                particles.add(
                    Particle(
                        x = luffy.x + (rng.nextFloat() * 20f - 10f),
                        y = luffy.y - 10f - rng.nextFloat() * 18f,
                        vx = (rng.nextFloat() * 22f - 11f),
                        vy = -26f - rng.nextFloat() * 22f,
                        size = 2.8f + rng.nextFloat() * 2.2f,
                        color = when {
                            rng.nextFloat() < 0.45f -> Color(0xFF14101C)
                            rng.nextFloat() < 0.85f -> Color(0xFF28F57A)
                            else -> Color(0xFFB44CFF)
                        },
                        age = 0f,
                        maxAge = 0.28f + rng.nextFloat() * 0.14f,
                        kind = ParticleKind.DUST
                    )
                )
            }
            if (nextZoroHellTimer <= 0f) {
                nextZoroHellActive = false
                nextZoroHellTimer = 0f
                nextZoroHellMeter = 0f
                revertedFromZoroHell = true
            }
        }

        val isGear4Attack = luffy.activeAttack == AttackType.GEAR4_KONG_GUN ||
            luffy.activeAttack == AttackType.GEAR4_KONG_ORGAN ||
            luffy.activeAttack == AttackType.GEAR4_KING_KONG_GUN
        val isZoroHellAttack = luffy.activeAttack == AttackType.ZORO_HELL_SLASH ||
            luffy.activeAttack == AttackType.ZORO_PURGATORY_ONIGIRI ||
            luffy.activeAttack == AttackType.ZORO_ASURA
        val cancelAwakenedAttack = (revertedFromGear4 && isGear4Attack) || (revertedFromZoroHell && isZoroHellAttack)

        luffy = luffy.copy(
            cooldowns = updatedCooldowns,
            invulnTimer = nextInvuln,
            hurtTimer = nextHurt,
            switchCooldown = nextSwitchCd,
            switchFlashTimer = nextSwitchFlash,
            zoroComboResetTimer = nextZoroComboReset,
            zoroComboStep = nextZoroComboStep,
            sanjiComboResetTimer = nextSanjiComboReset,
            sanjiComboStep = nextSanjiComboStep,
            chopperComboResetTimer = nextChopperComboReset,
            chopperComboStep = nextChopperComboStep,
            coyoteTimer = newCoyote,
            jumpBufferTimer = newJumpBuffer,
            landTimer = newLandTimer,
            gear4Active = nextGear4Active,
            gear4Timer = nextGear4Timer,
            gear4Meter = nextGear4Meter,
            zoroHellActive = nextZoroHellActive,
            zoroHellTimer = nextZoroHellTimer,
            zoroHellMeter = nextZoroHellMeter,
            activeAttack = if (cancelAwakenedAttack) null else luffy.activeAttack,
            attackPhase = if (cancelAwakenedAttack) AttackPhase.NONE else luffy.attackPhase,
            attackTimer = if (cancelAwakenedAttack) 0f else luffy.attackTimer,
            stretchFactor = if (cancelAwakenedAttack) 0f else luffy.stretchFactor,
            gatlingGhosts = if (cancelAwakenedAttack) emptyList() else luffy.gatlingGhosts,
            kingKongImpactTriggered = if (cancelAwakenedAttack) false else luffy.kingKongImpactTriggered,
            purgatoryDashStage = if (cancelAwakenedAttack) 0 else luffy.purgatoryDashStage,
            asuraSlashesTriggered = if (cancelAwakenedAttack) false else luffy.asuraSlashesTriggered
        )
        if (revertedFromGear4) {
            soundEngine.play(RetroSfx.CHOPPER_GUARD_PUFF, 0.72f)
            spawnDustBurst(particles, luffy.x, luffy.y - 12f, count = 12, isPier = false)
            floatingTexts.add(
                FloatingDamageText(
                    id = nextEntityId++,
                    x = luffy.x,
                    y = luffy.y - 38f,
                    text = "ГИР 4 ЗАВЕРШЁН",
                    color = Color(0xFFFFD84D),
                    isLarge = false
                )
            )
        }
        if (revertedFromZoroHell) {
            soundEngine.play(RetroSfx.ZORO_DRAW, 0.75f)
            spawnDustBurst(particles, luffy.x, luffy.y - 12f, count = 12, isPier = false)
            floatingTexts.add(
                FloatingDamageText(
                    id = nextEntityId++,
                    x = luffy.x,
                    y = luffy.y - 38f,
                    text = "КОРОЛЬ АДА ЗАВЕРШЁН",
                    color = Color(0xFF4CF57E),
                    isLarge = false
                )
            )
        }

        // 1b. Handle Live Character Switching (Tab / 1 / 2 / 3 / 4 / 5 / 6 / Touch Switch & Roster)
        if (luffy.switchCooldown <= 0f) {
            val desiredHero = when {
                input.selectLuffyJustPressed -> PlayableHero.LUFFY
                input.selectZoroJustPressed -> PlayableHero.ZORO
                input.selectSanjiJustPressed -> PlayableHero.SANJI
                input.selectNamiJustPressed -> PlayableHero.NAMI
                input.selectUsoppJustPressed -> PlayableHero.USOPP
                input.selectChopperJustPressed -> PlayableHero.CHOPPER
                input.switchHeroJustPressed -> luffy.standbyHero
                else -> null
            }
            if (desiredHero != null && desiredHero != luffy.activeHero) {
                // Prevent switching BACK to Luffy while Luffy is still drowning in the pool!
                if (!(desiredHero == PlayableHero.LUFFY && luffyDrowning)) {
                    switchToHero(desiredHero)
                }
            }
        }

        // In Level 3 Slow-Mo Finisher, always keep Luffy's Battle Axe ready & guarantee instant tag + snap on C/L!
        if (isSlowMoFinisher) {
            val readyMap = luffy.cooldowns.toMutableMap().apply {
                this[AttackType.AXE] = CooldownState(0f, AttackType.AXE.cooldownSeconds)
            }
            luffy = luffy.copy(cooldowns = readyMap)
            if (input.axeJustPressed) {
                val revivedMap = luffy.heroHp.toMutableMap().apply {
                    if ((this[PlayableHero.LUFFY] ?: 0) <= 0) this[PlayableHero.LUFFY] = 35
                }
                luffy = luffy.copy(heroHp = revivedMap, switchCooldown = 0f, hurtTimer = 0f)
                if (luffy.activeHero != PlayableHero.LUFFY) {
                    switchToHero(PlayableHero.LUFFY)
                }
                val arlong = current.enemies.firstOrNull { it.kind == EnemyKind.BOSS_ARLONG }
                if (arlong != null) {
                    val dirToArlong = if (arlong.x >= luffy.x) FacingDirection.RIGHT else FacingDirection.LEFT
                    val snapX = (arlong.x - dirToArlong.sign * 34f).coerceIn(
                        WorldConstants.ARLONG_MAP_ROOM_MIN_X + 16f,
                        WorldConstants.ARLONG_MAP_ROOM_MAX_X - 16f
                    )
                    luffy = luffy.copy(
                        x = snapX,
                        y = WorldConstants.BEACH_GROUND_Y,
                        vx = 0f,
                        vy = 0f,
                        isGrounded = true,
                        facing = dirToArlong,
                        hurtTimer = 0f,
                        activeAttack = null,
                        attackPhase = AttackPhase.NONE,
                        attackTimer = 0f,
                        stretchFactor = 0f,
                        axeImpactTriggered = false
                    )
                }
            }
        }

        val isLuffyTrappedDrowning = isLevel3 && luffyDrowning && luffy.activeHero == PlayableHero.LUFFY
        val isHurtLocked = luffy.hurtTimer > 0f || isLuffyTrappedDrowning

        // 1c. Gear 4: Boundman Activation (G / V or Touch G4 button when GEAR 4 / HAKI bar is 100%!)
        var gear4ActivatedNow = false
        if (input.gear4JustPressed &&
            !isHurtLocked &&
            luffy.activeHero == PlayableHero.LUFFY &&
            !luffy.gear4Active &&
            luffy.gear4Meter >= WorldConstants.GEAR4_MAX_METER - 0.1f
        ) {
            val resetG4Cooldowns = luffy.cooldowns.toMutableMap().apply {
                this[AttackType.GEAR4_KONG_GUN] = CooldownState(0f, AttackType.GEAR4_KONG_GUN.cooldownSeconds)
                this[AttackType.GEAR4_KONG_ORGAN] = CooldownState(0f, AttackType.GEAR4_KONG_ORGAN.cooldownSeconds)
                this[AttackType.GEAR4_KING_KONG_GUN] = CooldownState(0f, AttackType.GEAR4_KING_KONG_GUN.cooldownSeconds)
            }
            luffy = luffy.copy(
                gear4Active = true,
                gear4Timer = WorldConstants.GEAR4_DURATION,
                gear4Meter = WorldConstants.GEAR4_MAX_METER,
                gear4BounceTimer = 0f,
                airJumpsRemaining = WorldConstants.GEAR4_AIR_JUMPS,
                invulnTimer = maxOf(luffy.invulnTimer, 0.55f),
                activeAttack = null,
                attackPhase = AttackPhase.NONE,
                attackTimer = 0f,
                stretchFactor = 0f,
                gatlingGhosts = emptyList(),
                axeImpactTriggered = false,
                kingKongImpactTriggered = false,
                cooldowns = resetG4Cooldowns
            )
            gear4ActivatedNow = true
            soundEngine.play(RetroSfx.GEAR4_ACTIVATE, 0.98f)
            triggerShake(7.5f, 0.38f)
            triggerFlash(0.18f, Color(0xFFFF4D6D))
            waveBanner = WaveBannerState("ГИР 4: БАУНДСМЕН!", isBoss = true, remainingSeconds = 1.65f)
            slashVfxs.add(
                SwordSlashVfx(
                    id = nextEntityId++,
                    x = luffy.x,
                    y = luffy.y - 16f,
                    facing = luffy.facing,
                    kind = SlashVfxKind.GEAR4_SHOCKWAVE,
                    maxAge = 0.42f
                )
            )
            repeat(24) { i ->
                val ang = (i.toFloat() / 24f) * (2f * PI.toFloat())
                val spd = 60f + rng.nextFloat() * 95f
                particles.add(
                    Particle(
                        x = luffy.x + cos(ang) * 6f,
                        y = luffy.y - 16f + sin(ang) * 6f,
                        vx = cos(ang) * spd,
                        vy = sin(ang) * spd - 24f,
                        size = 4f + rng.nextFloat() * 3.5f,
                        color = when {
                            i % 3 == 0 -> Color(0xFFFF385C)
                            i % 3 == 1 -> Color(0xFFFFFFFF)
                            else -> Color(0xFF1F1629)
                        },
                        age = 0f,
                        maxAge = 0.38f + rng.nextFloat() * 0.16f,
                        kind = ParticleKind.DUST
                    )
                )
            }
            floatingTexts.add(
                FloatingDamageText(
                    id = nextEntityId++,
                    x = luffy.x,
                    y = luffy.y - 44f,
                    text = "ГИР 4: БАУНДСМЕН!",
                    color = Color(0xFFFF4D6D),
                    isLarge = true
                )
            )
        }

        // 1d. Zoro King of Hell (Black Bandana) Awakening Activation (G / V or Touch Awakening button when 100%!)
        var zoroHellActivatedNow = false
        if (input.gear4JustPressed &&
            !isHurtLocked &&
            luffy.activeHero == PlayableHero.ZORO &&
            !luffy.zoroHellActive &&
            luffy.zoroHellMeter >= WorldConstants.ZORO_HELL_MAX_METER - 0.1f
        ) {
            val resetHellCooldowns = luffy.cooldowns.toMutableMap().apply {
                this[AttackType.ZORO_HELL_SLASH] = CooldownState(0f, AttackType.ZORO_HELL_SLASH.cooldownSeconds)
                this[AttackType.ZORO_PURGATORY_ONIGIRI] = CooldownState(0f, AttackType.ZORO_PURGATORY_ONIGIRI.cooldownSeconds)
                this[AttackType.ZORO_ASURA] = CooldownState(0f, AttackType.ZORO_ASURA.cooldownSeconds)
            }
            luffy = luffy.copy(
                zoroHellActive = true,
                zoroHellTimer = WorldConstants.ZORO_HELL_DURATION,
                zoroHellMeter = WorldConstants.ZORO_HELL_MAX_METER,
                invulnTimer = maxOf(luffy.invulnTimer, 0.55f),
                activeAttack = null,
                attackPhase = AttackPhase.NONE,
                attackTimer = 0f,
                stretchFactor = 0f,
                onigiriSlashTriggered = false,
                purgatoryDashStage = 0,
                asuraSlashesTriggered = false,
                cooldowns = resetHellCooldowns
            )
            zoroHellActivatedNow = true
            soundEngine.play(RetroSfx.ZORO_HELL_ACTIVATE, 1.0f)
            triggerShake(7.8f, 0.40f)
            triggerFlash(0.18f, Color(0xFF2EE66B))
            waveBanner = WaveBannerState("КОРОЛЬ АДА: ЧЁРНАЯ БАНДАНА!", isBoss = true, remainingSeconds = 1.75f)
            slashVfxs.add(
                SwordSlashVfx(
                    id = nextEntityId++,
                    x = luffy.x,
                    y = luffy.y - 16f,
                    facing = luffy.facing,
                    kind = SlashVfxKind.ZORO_HELL_SHOCKWAVE,
                    maxAge = 0.44f
                )
            )
            repeat(28) { i ->
                val ang = (i.toFloat() / 28f) * (2f * PI.toFloat())
                val spd = 65f + rng.nextFloat() * 105f
                particles.add(
                    Particle(
                        x = luffy.x + cos(ang) * 6f,
                        y = luffy.y - 16f + sin(ang) * 6f,
                        vx = cos(ang) * spd,
                        vy = sin(ang) * spd - 26f,
                        size = 3.5f + rng.nextFloat() * 3.5f,
                        color = when {
                            i % 3 == 0 -> Color(0xFF14101C)
                            i % 3 == 1 -> Color(0xFF28F57A)
                            else -> Color(0xFFB44CFF)
                        },
                        age = 0f,
                        maxAge = 0.40f + rng.nextFloat() * 0.16f,
                        kind = ParticleKind.DUST
                    )
                )
            }
            floatingTexts.add(
                FloatingDamageText(
                    id = nextEntityId++,
                    x = luffy.x,
                    y = luffy.y - 44f,
                    text = "КОРОЛЬ АДА!",
                    color = Color(0xFF38FF88),
                    isLarge = true
                )
            )
        }

        // 2. Check Attack Triggers (mapped to active hero: Luffy / Gear 4 Boundman, Zoro / King of Hell, Sanji, Nami, Usopp, Chopper)
        if (!isHurtLocked && luffy.activeAttack == null) {
            val requestedAttack = when (luffy.activeHero) {
                PlayableHero.LUFFY -> if (luffy.gear4Active && !isSlowMoFinisher) {
                    when {
                        input.axeJustPressed && luffy.cooldowns[AttackType.GEAR4_KING_KONG_GUN]?.isReady == true -> AttackType.GEAR4_KING_KONG_GUN
                        input.gatlingJustPressed && luffy.cooldowns[AttackType.GEAR4_KONG_ORGAN]?.isReady == true -> AttackType.GEAR4_KONG_ORGAN
                        input.pistolJustPressed && luffy.cooldowns[AttackType.GEAR4_KONG_GUN]?.isReady == true -> AttackType.GEAR4_KONG_GUN
                        else -> null
                    }
                } else {
                    when {
                        input.axeJustPressed && luffy.cooldowns[AttackType.AXE]?.isReady == true -> AttackType.AXE
                        input.gatlingJustPressed && luffy.cooldowns[AttackType.GATLING]?.isReady == true -> AttackType.GATLING
                        input.pistolJustPressed && luffy.cooldowns[AttackType.PISTOL]?.isReady == true -> AttackType.PISTOL
                        else -> null
                    }
                }
                PlayableHero.ZORO -> if (luffy.zoroHellActive) {
                    when {
                        input.axeJustPressed && luffy.cooldowns[AttackType.ZORO_ASURA]?.isReady == true -> AttackType.ZORO_ASURA
                        input.gatlingJustPressed && luffy.cooldowns[AttackType.ZORO_PURGATORY_ONIGIRI]?.isReady == true -> AttackType.ZORO_PURGATORY_ONIGIRI
                        input.pistolJustPressed && luffy.cooldowns[AttackType.ZORO_HELL_SLASH]?.isReady == true -> AttackType.ZORO_HELL_SLASH
                        else -> null
                    }
                } else {
                    when {
                        input.axeJustPressed && luffy.cooldowns[AttackType.ZORO_TATSUMAKI]?.isReady == true -> AttackType.ZORO_TATSUMAKI
                        input.gatlingJustPressed && luffy.cooldowns[AttackType.ZORO_ONIGIRI]?.isReady == true -> AttackType.ZORO_ONIGIRI
                        input.pistolJustPressed && luffy.cooldowns[AttackType.ZORO_SLASH]?.isReady == true -> AttackType.ZORO_SLASH
                        else -> null
                    }
                }
                PlayableHero.SANJI -> when {
                    input.axeJustPressed && luffy.cooldowns[AttackType.SANJI_DIABLE_JAMBE]?.isReady == true -> AttackType.SANJI_DIABLE_JAMBE
                    input.gatlingJustPressed && luffy.cooldowns[AttackType.SANJI_PARTY_TABLE]?.isReady == true -> AttackType.SANJI_PARTY_TABLE
                    input.pistolJustPressed && luffy.cooldowns[AttackType.SANJI_KICK]?.isReady == true -> AttackType.SANJI_KICK
                    else -> null
                }
                PlayableHero.NAMI -> when {
                    input.axeJustPressed && luffy.cooldowns[AttackType.NAMI_THUNDERBOLT]?.isReady == true -> AttackType.NAMI_THUNDERBOLT
                    input.gatlingJustPressed && luffy.cooldowns[AttackType.NAMI_CYCLONE]?.isReady == true -> AttackType.NAMI_CYCLONE
                    input.pistolJustPressed && luffy.cooldowns[AttackType.NAMI_SPARK]?.isReady == true -> AttackType.NAMI_SPARK
                    else -> null
                }
                PlayableHero.USOPP -> when {
                    input.axeJustPressed && luffy.cooldowns[AttackType.USOPP_FIREBIRD_STAR]?.isReady == true -> AttackType.USOPP_FIREBIRD_STAR
                    input.gatlingJustPressed && luffy.cooldowns[AttackType.USOPP_EXPLODING_STAR]?.isReady == true -> AttackType.USOPP_EXPLODING_STAR
                    input.pistolJustPressed && luffy.cooldowns[AttackType.USOPP_SHOT]?.isReady == true -> AttackType.USOPP_SHOT
                    else -> null
                }
                PlayableHero.CHOPPER -> when {
                    input.axeJustPressed && luffy.cooldowns[AttackType.CHOPPER_MONSTER_POINT]?.isReady == true -> AttackType.CHOPPER_MONSTER_POINT
                    input.gatlingJustPressed && luffy.cooldowns[AttackType.CHOPPER_GUARD_HEAL]?.isReady == true -> AttackType.CHOPPER_GUARD_HEAL
                    input.pistolJustPressed && luffy.cooldowns[AttackType.CHOPPER_KUNGFU]?.isReady == true -> AttackType.CHOPPER_KUNGFU
                    else -> null
                }
            }

            if (requestedAttack != null) {
                val startFacing = when {
                    isSlowMoFinisher && requestedAttack == AttackType.AXE -> luffy.facing
                    input.moveRight && !input.moveLeft -> FacingDirection.RIGHT
                    input.moveLeft && !input.moveRight -> FacingDirection.LEFT
                    else -> luffy.facing
                }
                val nextCooldowns = luffy.cooldowns.toMutableMap()
                val effectiveCooldown = if (luffy.activeHero == PlayableHero.ZORO && luffy.zoroHellActive) {
                    requestedAttack.cooldownSeconds / WorldConstants.ZORO_HELL_ATTACK_SPEED_MULT
                } else {
                    requestedAttack.cooldownSeconds
                }
                nextCooldowns[requestedAttack] = CooldownState(
                    remaining = effectiveCooldown,
                    total = effectiveCooldown
                )
                luffy = luffy.copy(
                    facing = startFacing,
                    activeAttack = requestedAttack,
                    attackPhase = AttackPhase.WINDUP,
                    attackTimer = 0f,
                    attackHitTargets = emptySet(),
                    gatlingHitPulseTimer = 0f,
                    stretchFactor = 0f,
                    axeImpactTriggered = false,
                    onigiriSlashTriggered = false,
                    onigiriGhostAccum = 0f,
                    purgatoryDashStage = 0,
                    asuraSlashesTriggered = false,
                    diableExplosionTriggered = false,
                    guardHealTriggered = false,
                    monsterRoarTriggered = false,
                    monsterSlamTriggered = false,
                    kingKongImpactTriggered = false,
                    attackFired = false,
                    gatlingSpawnAccum = 0f,
                    gatlingGhosts = emptyList(),
                    cooldowns = nextCooldowns,
                    vx = if (luffy.isGrounded) luffy.vx * 0.25f else luffy.vx * 0.6f,
                    vy = if (requestedAttack == AttackType.GEAR4_KING_KONG_GUN) -255f else luffy.vy,
                    isGrounded = if (requestedAttack == AttackType.GEAR4_KING_KONG_GUN) false else luffy.isGrounded
                )
                banner = AttackBannerState(requestedAttack, remainingSeconds = 1.15f)

                when (requestedAttack) {
                    AttackType.PISTOL -> soundEngine.play(RetroSfx.RUBBER_STRETCH, 0.85f)
                    AttackType.GATLING -> soundEngine.play(RetroSfx.RUBBER_STRETCH, 0.85f)
                    AttackType.AXE -> soundEngine.play(RetroSfx.AXE_STRETCH, 0.90f)
                    AttackType.GEAR4_KONG_GUN -> soundEngine.play(RetroSfx.RUBBER_STRETCH, 0.95f)
                    AttackType.GEAR4_KONG_ORGAN -> soundEngine.play(RetroSfx.GEAR4_BOUNCE, 0.92f)
                    AttackType.GEAR4_KING_KONG_GUN -> {
                        soundEngine.play(RetroSfx.GEAR4_BOUNCE, 0.96f)
                        soundEngine.play(RetroSfx.AXE_STRETCH, 0.90f)
                    }
                    AttackType.ZORO_SLASH -> soundEngine.play(RetroSfx.ZORO_SLASH, 0.88f)
                    AttackType.ZORO_ONIGIRI -> soundEngine.play(RetroSfx.ZORO_DRAW, 0.92f)
                    AttackType.ZORO_TATSUMAKI -> soundEngine.play(RetroSfx.ZORO_TATSUMAKI, 0.95f)
                    AttackType.ZORO_HELL_SLASH -> soundEngine.play(RetroSfx.ZORO_HELL_SLASH, 0.94f)
                    AttackType.ZORO_PURGATORY_ONIGIRI -> soundEngine.play(RetroSfx.ZORO_DRAW, 0.98f)
                    AttackType.ZORO_ASURA -> soundEngine.play(RetroSfx.ZORO_ASURA_AURA, 1.0f)
                    AttackType.SANJI_KICK -> soundEngine.play(RetroSfx.SANJI_KICK, 0.86f)
                    AttackType.SANJI_PARTY_TABLE -> soundEngine.play(RetroSfx.SANJI_PARTY_TABLE, 0.92f)
                    AttackType.SANJI_DIABLE_JAMBE -> soundEngine.play(RetroSfx.SANJI_DIABLE_FIRE, 0.96f)
                    AttackType.NAMI_SPARK -> soundEngine.play(RetroSfx.NAMI_SPARK, 0.88f)
                    AttackType.NAMI_CYCLONE -> soundEngine.play(RetroSfx.NAMI_CYCLONE, 0.92f)
                    AttackType.NAMI_THUNDERBOLT -> soundEngine.play(RetroSfx.NAMI_SPARK, 0.90f)
                    AttackType.USOPP_SHOT -> soundEngine.play(RetroSfx.USOPP_SLING_DRAW, 0.82f)
                    AttackType.USOPP_EXPLODING_STAR -> soundEngine.play(RetroSfx.USOPP_SLING_DRAW, 0.90f)
                    AttackType.USOPP_FIREBIRD_STAR -> soundEngine.play(RetroSfx.USOPP_SLING_DRAW, 0.94f)
                    AttackType.CHOPPER_KUNGFU -> soundEngine.play(RetroSfx.CHOPPER_KUNGFU, 0.88f)
                    AttackType.CHOPPER_GUARD_HEAL -> soundEngine.play(RetroSfx.CHOPPER_GUARD_PUFF, 0.94f)
                    AttackType.CHOPPER_MONSTER_POINT -> soundEngine.play(RetroSfx.PICKUP_BERRY, 0.82f)
                }
            }
        }

        // 3. Horizontal Movement & Facing
        val moveAxis = if (isHurtLocked) 0f else (if (input.moveRight) 1f else 0f) - (if (input.moveLeft) 1f else 0f)
        val isAttackLocked = luffy.activeAttack != null || isHurtLocked
        val isOnigiriDashing = luffy.activeAttack == AttackType.ZORO_ONIGIRI && luffy.attackPhase == AttackPhase.ACTIVE
        val isPurgatoryDashing = luffy.activeAttack == AttackType.ZORO_PURGATORY_ONIGIRI &&
            luffy.attackPhase == AttackPhase.ACTIVE &&
            (luffy.purgatoryDashStage == 1 || luffy.purgatoryDashStage == 3)
        val isDiableDashing = luffy.activeAttack == AttackType.SANJI_DIABLE_JAMBE &&
            luffy.attackPhase == AttackPhase.ACTIVE && !luffy.diableExplosionTriggered
        val movementMultiplier = when {
            isHurtLocked -> 0f
            luffy.activeAttack == null -> 1f
            luffy.activeAttack == AttackType.PISTOL -> if (luffy.isGrounded) 0.12f else 0.45f
            luffy.activeAttack == AttackType.GATLING -> 0.06f
            luffy.activeAttack == AttackType.AXE -> 0.0f
            luffy.activeAttack == AttackType.GEAR4_KONG_GUN -> if (luffy.isGrounded) 0.10f else 0.40f
            luffy.activeAttack == AttackType.GEAR4_KONG_ORGAN -> 0.10f
            luffy.activeAttack == AttackType.GEAR4_KING_KONG_GUN -> 0.14f
            luffy.activeAttack == AttackType.ZORO_SLASH -> if (luffy.isGrounded) 0.30f else 0.62f
            luffy.activeAttack == AttackType.ZORO_HELL_SLASH -> if (luffy.isGrounded) 0.36f else 0.68f
            luffy.activeAttack == AttackType.ZORO_ONIGIRI -> 0.0f
            luffy.activeAttack == AttackType.ZORO_PURGATORY_ONIGIRI -> 0.0f
            luffy.activeAttack == AttackType.ZORO_TATSUMAKI -> 0.24f
            luffy.activeAttack == AttackType.ZORO_ASURA -> 0.0f
            luffy.activeAttack == AttackType.SANJI_KICK -> if (luffy.isGrounded) 0.30f else 0.65f
            luffy.activeAttack == AttackType.SANJI_PARTY_TABLE -> 0.38f
            luffy.activeAttack == AttackType.SANJI_DIABLE_JAMBE -> 0.0f
            luffy.activeAttack == AttackType.NAMI_SPARK -> if (luffy.isGrounded) 0.25f else 0.60f
            luffy.activeAttack == AttackType.NAMI_CYCLONE -> if (luffy.isGrounded) 0.15f else 0.50f
            luffy.activeAttack == AttackType.NAMI_THUNDERBOLT -> 0.08f
            luffy.activeAttack == AttackType.USOPP_SHOT -> if (luffy.isGrounded) 0.20f else 0.58f
            luffy.activeAttack == AttackType.USOPP_EXPLODING_STAR -> if (luffy.isGrounded) 0.12f else 0.48f
            luffy.activeAttack == AttackType.USOPP_FIREBIRD_STAR -> 0.05f
            luffy.activeAttack == AttackType.CHOPPER_KUNGFU -> if (luffy.isGrounded) 0.34f else 0.70f
            luffy.activeAttack == AttackType.CHOPPER_GUARD_HEAL -> 0.28f
            luffy.activeAttack == AttackType.CHOPPER_MONSTER_POINT -> 0.0f
            else -> 1f
        }

        var vx = luffy.vx
        var facing = luffy.facing
        if (!isAttackLocked && moveAxis != 0f) {
            val newFacing = if (moveAxis > 0f) FacingDirection.RIGHT else FacingDirection.LEFT
            if (luffy.isGrounded && newFacing != facing && abs(vx) > 55f) {
                spawnDustBurst(particles, luffy.x, luffy.y, count = 4, isPier = isOnPier(luffy.x))
            }
            facing = newFacing
        }

        val isGear4Luffy = luffy.activeHero == PlayableHero.LUFFY && luffy.gear4Active
        val isZoroHell = luffy.activeHero == PlayableHero.ZORO && luffy.zoroHellActive
        val speedMult = when {
            isGear4Luffy -> WorldConstants.GEAR4_SPEED_MULT
            isZoroHell -> WorldConstants.ZORO_HELL_SPEED_MULT
            else -> 1f
        }
        val maxRunSpeed = luffy.activeHero.moveSpeed * speedMult
        val targetSpeed = moveAxis * maxRunSpeed * movementMultiplier
        val accel = if (luffy.isGrounded) WorldConstants.GROUND_ACCEL else WorldConstants.AIR_ACCEL
        val friction = when {
            isHurtLocked -> 320f
            luffy.isGrounded -> WorldConstants.GROUND_FRICTION
            else -> WorldConstants.AIR_FRICTION
        }

        vx = if (isPurgatoryDashing) {
            luffy.facing.sign * 690f
        } else if (isOnigiriDashing) {
            luffy.facing.sign * 520f
        } else if (isDiableDashing) {
            luffy.facing.sign * 395f
        } else if (abs(targetSpeed) > 0.1f) {
            approach(vx, targetSpeed, accel * dt)
        } else {
            approach(vx, 0f, friction * dt)
        }

        // 4. Jump, Sanji Sky Walk, Boundman Elastic Flight & Gravity Physics
        var vy = luffy.vy
        var isGrounded = luffy.isGrounded
        var coyote = luffy.coyoteTimer
        var jumpBuffer = luffy.jumpBufferTimer
        val maxAirJumps = if (isGear4Luffy) WorldConstants.GEAR4_AIR_JUMPS else 1
        var airJumps = if (isGrounded) maxAirJumps else luffy.airJumpsRemaining
        var gear4BounceTimer = luffy.gear4BounceTimer

        // Boundman rhythmic ground bounce (boing! while standing or moving on ground)
        if (isGear4Luffy && isGrounded && !isAttackLocked && !isHurtLocked) {
            gear4BounceTimer += dt
            val bounceInterval = if (abs(moveAxis) > 0.1f) 0.28f else 0.42f
            if (gear4BounceTimer >= bounceInterval) {
                gear4BounceTimer = 0f
                vy = if (abs(moveAxis) > 0.1f) -106f else -78f
                isGrounded = false
                soundEngine.play(RetroSfx.GEAR4_BOUNCE, 0.48f)
                spawnDustBurst(particles, luffy.x, luffy.y, count = 4, isPier = isOnPier(luffy.x))
            }
        } else if (!isGear4Luffy) {
            gear4BounceTimer = 0f
        }

        if (!isAttackLocked && jumpBuffer > 0f && coyote > 0f) {
            vy = when {
                isGear4Luffy -> WorldConstants.JUMP_VELOCITY * 1.08f
                luffy.activeHero == PlayableHero.CHOPPER -> WorldConstants.JUMP_VELOCITY * 1.11f
                else -> WorldConstants.JUMP_VELOCITY
            }
            isGrounded = false
            coyote = 0f
            jumpBuffer = 0f
            airJumps = maxAirJumps
            gear4BounceTimer = 0f
            soundEngine.play(if (isGear4Luffy) RetroSfx.GEAR4_BOUNCE else RetroSfx.JUMP, 0.85f)
            spawnDustBurst(particles, luffy.x, luffy.y, count = 6, isPier = isOnPier(luffy.x))
        } else if (!isAttackLocked && input.jumpJustPressed && coyote <= 0f &&
            (luffy.activeHero == PlayableHero.SANJI || isGear4Luffy) && airJumps > 0
        ) {
            // SANJI SKY WALK / BOUNDMAN ELASTIC FLIGHT (mid-air jet-kick hover with steam rings!)
            vy = if (isGear4Luffy) WorldConstants.JUMP_VELOCITY * 0.90f else WorldConstants.JUMP_VELOCITY * 0.95f
            isGrounded = false
            airJumps -= 1
            jumpBuffer = 0f
            soundEngine.play(if (isGear4Luffy) RetroSfx.GEAR4_BOUNCE else RetroSfx.SANJI_SKYWALK, 0.90f)
            skyWalkRings.add(
                SkyWalkRingVfx(
                    id = nextEntityId++,
                    x = luffy.x,
                    y = luffy.y,
                    maxAge = 0.30f
                )
            )
            repeat(8) { idx ->
                val spread = (idx / 7f) * 2f - 1f
                particles.add(
                    Particle(
                        x = luffy.x + spread * 7f,
                        y = luffy.y + 1f,
                        vx = spread * (38f + rng.nextFloat() * 24f),
                        vy = 26f + rng.nextFloat() * 34f,
                        size = 2.5f + rng.nextFloat() * 1.5f,
                        color = if (isGear4Luffy && idx % 3 == 0) Color(0xFFFF5978) else if (idx % 2 == 0) Color(0xFFFFFFFF) else Color(0xFFCFF6FF),
                        age = 0f,
                        maxAge = 0.24f + rng.nextFloat() * 0.10f,
                        kind = ParticleKind.DUST
                    )
                )
            }
        }

        if (!input.jumpHeld && vy < -85f && luffy.activeAttack == null && !isHurtLocked) {
            vy *= 0.62f
        }

        val gravityScale = when {
            luffy.activeAttack == AttackType.GATLING -> 0.25f
            luffy.activeAttack == AttackType.GEAR4_KONG_ORGAN -> 0.18f
            luffy.activeAttack == AttackType.GEAR4_KING_KONG_GUN && luffy.attackPhase == AttackPhase.WINDUP -> 0.45f
            luffy.activeAttack == AttackType.GEAR4_KING_KONG_GUN && luffy.attackPhase == AttackPhase.ACTIVE && !luffy.kingKongImpactTriggered -> 2.4f
            luffy.activeAttack == AttackType.ZORO_ONIGIRI -> 0.10f
            luffy.activeAttack == AttackType.ZORO_PURGATORY_ONIGIRI -> 0.04f
            luffy.activeAttack == AttackType.ZORO_TATSUMAKI -> 0.22f
            luffy.activeAttack == AttackType.ZORO_ASURA -> 0.08f
            luffy.activeAttack == AttackType.SANJI_DIABLE_JAMBE -> 0.12f
            luffy.activeAttack == AttackType.SANJI_PARTY_TABLE -> 0.30f
            luffy.activeAttack == AttackType.CHOPPER_MONSTER_POINT -> 0.20f
            luffy.activeAttack == AttackType.AXE && luffy.attackPhase == AttackPhase.WINDUP -> 0.2f
            vy > 0f -> WorldConstants.FALL_GRAVITY_MULT
            else -> 1f
        }
        if (!isGrounded) {
            vy = (vy + WorldConstants.GRAVITY * gravityScale * dt).coerceAtMost(WorldConstants.MAX_FALL_SPEED)
        }

        val minLeftBoundary = when {
            isLevel3 && current.isArlongMapRoom -> WorldConstants.ARLONG_MAP_ROOM_MIN_X
            else -> WorldConstants.LEFT_BOUNDARY_X
        }
        val maxRightBoundary = when {
            isLevel3 && arlongPhase == ArlongStagePhase.GATE_INTRO -> WorldConstants.ARLONG_GATE_X - 18f
            isLevel3 && current.isArlongMapRoom -> WorldConstants.ARLONG_MAP_ROOM_MAX_X
            else -> WorldConstants.RIGHT_BOUNDARY_X
        }
        var nextX = (luffy.x + vx * dt).coerceIn(minLeftBoundary, maxRightBoundary)
        var nextY = luffy.y + vy * dt

        val inCourtyardPool = isLevel3 && !current.isArlongMapRoom &&
            arlongPhase != ArlongStagePhase.GATE_INTRO &&
            nextX > WorldConstants.ARLONG_POOL_LEFT_X + 6f &&
            nextX < WorldConstants.ARLONG_POOL_RIGHT_X - 6f

        val onBalconyRange = isLevel3 && !current.isArlongMapRoom &&
            arlongPhase != ArlongStagePhase.GATE_INTRO && (
            (nextX >= WorldConstants.ARLONG_LEFT_BALCONY_MIN_X && nextX <= WorldConstants.ARLONG_LEFT_BALCONY_MAX_X) ||
                (nextX >= WorldConstants.ARLONG_RIGHT_BALCONY_MIN_X && nextX <= WorldConstants.ARLONG_RIGHT_BALCONY_MAX_X)
            )
        val onStepRange = isLevel3 && !current.isArlongMapRoom &&
            arlongPhase != ArlongStagePhase.GATE_INTRO && (
            (nextX >= WorldConstants.ARLONG_LEFT_STEP_MIN_X && nextX <= WorldConstants.ARLONG_LEFT_STEP_MAX_X) ||
                (nextX >= WorldConstants.ARLONG_RIGHT_STEP_MIN_X && nextX <= WorldConstants.ARLONG_RIGHT_STEP_MAX_X)
            )

        val groundY = when {
            onBalconyRange && vy >= 0f && luffy.y <= WorldConstants.ARLONG_BALCONY_Y + 6f && nextY >= WorldConstants.ARLONG_BALCONY_Y ->
                WorldConstants.ARLONG_BALCONY_Y
            onStepRange && vy >= 0f && luffy.y <= WorldConstants.ARLONG_STEP_Y + 6f && nextY >= WorldConstants.ARLONG_STEP_Y ->
                WorldConstants.ARLONG_STEP_Y
            inCourtyardPool && luffy.activeHero == PlayableHero.LUFFY ->
                WorldConstants.ARLONG_POOL_DROWN_Y
            inCourtyardPool ->
                WorldConstants.ARLONG_POOL_SWIM_Y
            else -> groundYAt(nextX)
        }

        var landTimer = luffy.landTimer
        val wasGrounded = isGrounded
        if (nextY >= groundY) {
            nextY = groundY
            if (!wasGrounded && vy > 45f) {
                landTimer = 0.095f
                if (inCourtyardPool) {
                    soundEngine.play(RetroSfx.WATER_SPLASH, 0.88f)
                    spawnWaterSplashBurst(particles, nextX, WorldConstants.ARLONG_POOL_SURFACE_Y, count = 14)
                } else {
                    soundEngine.play(RetroSfx.LAND, 0.72f)
                    spawnDustBurst(particles, nextX, groundY, count = 7, isPier = isOnPier(nextX))
                }
            }
            vy = 0f
            isGrounded = true
            airJumps = maxAirJumps
        } else if (wasGrounded && abs(nextY - groundY) <= 5f && vy >= 0f) {
            nextY = groundY
            vy = 0f
            isGrounded = true
            airJumps = maxAirJumps
        } else {
            isGrounded = false
        }

        // === LEVEL 3 WATER WEAKNESS (LUFFY DEVIL FRUIT DROWNING & TEAMMATE RESCUE) ===
        if (isLevel3 && !current.isArlongMapRoom && arlongPhase != ArlongStagePhase.GATE_INTRO) {
            if (inCourtyardPool && luffy.activeHero == PlayableHero.LUFFY && nextY >= WorldConstants.ARLONG_POOL_SURFACE_Y + 2f) {
                if (!luffyDrowning) {
                    luffyDrowning = true
                    luffyDrownTimer = 5.0f
                    luffyDrownX = nextX.coerceIn(
                        WorldConstants.ARLONG_POOL_LEFT_X + 18f,
                        WorldConstants.ARLONG_POOL_RIGHT_X - 18f
                    )
                    soundEngine.play(RetroSfx.WATER_SPLASH, 0.94f)
                    spawnWaterSplashBurst(particles, luffyDrownX, WorldConstants.ARLONG_POOL_SURFACE_Y, count = 18)
                    floatingTexts.add(
                        FloatingDamageText(
                            id = nextEntityId++,
                            x = luffyDrownX,
                            y = WorldConstants.ARLONG_POOL_SURFACE_Y - 26f,
                            text = "ТОНУ! СМЕНИ ГЕРОЯ!",
                            color = Color(0xFF5CE1FF),
                            isLarge = true
                        )
                    )
                }
                vx = 0f
                nextY = WorldConstants.ARLONG_POOL_DROWN_Y + sin(worldTime * 6f) * 2f
            } else if (inCourtyardPool && luffy.activeHero != PlayableHero.LUFFY && nextY >= WorldConstants.ARLONG_POOL_SURFACE_Y) {
                // Non-Devil Fruit teammates swim safely and ripple the water
                if (rng.nextFloat() < 0.25f) {
                    particles.add(
                        Particle(
                            x = nextX + (rng.nextFloat() * 14f - 7f),
                            y = WorldConstants.ARLONG_POOL_SURFACE_Y,
                            vx = (rng.nextFloat() * 24f - 12f),
                            vy = -18f - rng.nextFloat() * 18f,
                            size = 2.2f,
                            color = Color(0xFFA6F2FF),
                            age = 0f,
                            maxAge = 0.25f,
                            kind = ParticleKind.WATER_SPRAY,
                            gravityScale = 0.5f
                        )
                    )
                }
            }

            if (luffyDrowning) {
                val prevWhole = luffyDrownTimer.toInt()
                luffyDrownTimer = (luffyDrownTimer - realDt).coerceAtLeast(0f)
                if (luffyDrownTimer.toInt() < prevWhole && luffyDrownTimer > 0f) {
                    soundEngine.play(RetroSfx.WATER_BUBBLE, 0.72f)
                }
                if (rng.nextFloat() < 0.42f) {
                    particles.add(
                        Particle(
                            x = luffyDrownX + (rng.nextFloat() * 14f - 7f),
                            y = WorldConstants.ARLONG_POOL_DROWN_Y - 12f,
                            vx = (rng.nextFloat() * 12f - 6f),
                            vy = -26f - rng.nextFloat() * 18f,
                            size = 2.5f + rng.nextFloat() * 1.5f,
                            color = Color(0xFFE0FAFF),
                            age = 0f,
                            maxAge = 0.55f,
                            kind = ParticleKind.BUBBLE
                        )
                    )
                }

                // Clear, generous Rescue Zones at BOTH pool edges + direct grab in water!
                if (luffy.activeHero != PlayableHero.LUFFY) {
                    val inLeftRescueZone = nextX in (WorldConstants.ARLONG_POOL_LEFT_X - 42f)..(WorldConstants.ARLONG_POOL_LEFT_X + 18f)
                    val inRightRescueZone = nextX in (WorldConstants.ARLONG_POOL_RIGHT_X - 18f)..(WorldConstants.ARLONG_POOL_RIGHT_X + 42f)
                    val directGrab = abs(nextX - luffyDrownX) <= 56f
                    if (inLeftRescueZone || inRightRescueZone || directGrab) {
                        luffyDrowning = false
                        luffyDrownTimer = 5.0f
                        soundEngine.play(RetroSfx.WATER_SPLASH, 0.92f)
                        soundEngine.play(RetroSfx.PICKUP_MEAT, 0.84f)
                        spawnWaterSplashBurst(particles, luffyDrownX, WorldConstants.ARLONG_POOL_SURFACE_Y, count = 20)
                        floatingTexts.add(
                            FloatingDamageText(
                                id = nextEntityId++,
                                x = nextX,
                                y = nextY - 38f,
                                text = "ЛУФФИ СПАСЁН!",
                                color = Color(0xFFFFD84D),
                                isLarge = true
                            )
                        )
                    }
                }

                // Air timer ran out -> deal minor damage (-15 HP, min 1 HP) & cleanly eject Luffy back onto dry land!
                if (luffyDrowning && luffyDrownTimer <= 0f) {
                    luffyDrowning = false
                    luffyDrownTimer = 5.0f
                    val currentLuffyHp = if (luffy.activeHero == PlayableHero.LUFFY) {
                        luffy.currentHp
                    } else {
                        luffy.heroHp[PlayableHero.LUFFY] ?: PlayableHero.LUFFY.maxHp
                    }
                    val nextLuffyHp = (currentLuffyHp - 15).coerceAtLeast(1)
                    val updatedHpMap = luffy.heroHp.toMutableMap().apply {
                        this[PlayableHero.LUFFY] = nextLuffyHp
                    }
                    val onLeftHalf = luffyDrownX < 380f
                    val safeRimX = if (onLeftHalf) WorldConstants.ARLONG_POOL_LEFT_X - 30f else WorldConstants.ARLONG_POOL_RIGHT_X + 30f
                    val ejectDir = if (onLeftHalf) -1f else 1f
                    soundEngine.play(RetroSfx.WATER_SPLASH, 0.94f)
                    soundEngine.play(RetroSfx.PLAYER_HURT, 0.85f)
                    triggerShake(5.0f, 0.25f)
                    spawnWaterSplashBurst(particles, luffyDrownX, WorldConstants.ARLONG_POOL_SURFACE_Y, count = 22)
                    floatingTexts.add(
                        FloatingDamageText(
                            id = nextEntityId++,
                            x = safeRimX,
                            y = WorldConstants.BEACH_GROUND_Y - 36f,
                            text = "-15 • ВЫБРОШЕН НА СУШУ!",
                            color = Color(0xFFFF9E3D),
                            isLarge = true
                        )
                    )
                    if (luffy.activeHero == PlayableHero.LUFFY) {
                        nextX = safeRimX
                        nextY = WorldConstants.BEACH_GROUND_Y - 6f
                        vx = ejectDir * 90f
                        vy = -165f
                        isGrounded = false
                        luffy = luffy.copy(
                            heroHp = updatedHpMap,
                            currentHp = nextLuffyHp,
                            invulnTimer = 1.4f,
                            hurtTimer = 0.22f,
                            activeAttack = null,
                            attackPhase = AttackPhase.NONE
                        )
                    } else {
                        luffy = luffy.copy(heroHp = updatedHpMap)
                    }
                }
            }
        }

        var stepTimer = luffy.stepSoundTimer
        if (isGrounded && abs(vx) > 48f && luffy.activeAttack == null && !isHurtLocked) {
            stepTimer += dt
            if (stepTimer >= 0.21f) {
                stepTimer = 0f
                soundEngine.play(RetroSfx.STEP, 0.45f)
                spawnFootstepPuff(particles, nextX - facing.sign * 4f, nextY, isOnPier(nextX))
            }
        } else {
            stepTimer = 0.14f
        }

        luffy = luffy.copy(
            x = nextX,
            y = nextY,
            vx = vx,
            vy = vy,
            facing = facing,
            isGrounded = isGrounded,
            airJumpsRemaining = airJumps,
            coyoteTimer = coyote,
            jumpBufferTimer = jumpBuffer,
            landTimer = landTimer,
            stepSoundTimer = stepTimer,
            gear4BounceTimer = gear4BounceTimer
        )

        // 5. Update Active Straw Hat Crew Attacks
        if (luffy.activeAttack != null) {
            luffy = updateActiveAttack(
                luffy = luffy,
                dt = dt,
                enemies = current.enemies,
                particles = particles,
                slashVfxs = slashVfxs,
                zoroAfterimages = zoroAfterimages,
                craters = craters,
                playerProjectiles = playerProjectiles,
                thunderClouds = thunderClouds,
                floatingTexts = floatingTexts,
                activeHitboxes = activeHitboxes,
                triggerShake = ::triggerShake,
                triggerFlash = ::triggerFlash
            )
        }

        // 6. Determine Active Hero Animation State
        val nextAnimState = when {
            luffy.hurtTimer > 0f -> PlayerAnimState.HURT
            luffy.activeAttack == AttackType.PISTOL -> PlayerAnimState.ATTACK_PISTOL
            luffy.activeAttack == AttackType.GATLING -> PlayerAnimState.ATTACK_GATLING
            luffy.activeAttack == AttackType.AXE -> PlayerAnimState.ATTACK_AXE
            luffy.activeAttack == AttackType.GEAR4_KONG_GUN -> PlayerAnimState.ATTACK_GEAR4_KONG_GUN
            luffy.activeAttack == AttackType.GEAR4_KONG_ORGAN -> PlayerAnimState.ATTACK_GEAR4_KONG_ORGAN
            luffy.activeAttack == AttackType.GEAR4_KING_KONG_GUN -> PlayerAnimState.ATTACK_GEAR4_KING_KONG_GUN
            luffy.activeAttack == AttackType.ZORO_SLASH -> PlayerAnimState.ATTACK_ZORO_SLASH
            luffy.activeAttack == AttackType.ZORO_ONIGIRI -> PlayerAnimState.ATTACK_ZORO_ONIGIRI
            luffy.activeAttack == AttackType.ZORO_TATSUMAKI -> PlayerAnimState.ATTACK_ZORO_TATSUMAKI
            luffy.activeAttack == AttackType.ZORO_HELL_SLASH -> PlayerAnimState.ATTACK_ZORO_HELL_SLASH
            luffy.activeAttack == AttackType.ZORO_PURGATORY_ONIGIRI -> PlayerAnimState.ATTACK_ZORO_PURGATORY_ONIGIRI
            luffy.activeAttack == AttackType.ZORO_ASURA -> PlayerAnimState.ATTACK_ZORO_ASURA
            luffy.activeAttack == AttackType.SANJI_KICK -> PlayerAnimState.ATTACK_SANJI_KICK
            luffy.activeAttack == AttackType.SANJI_PARTY_TABLE -> PlayerAnimState.ATTACK_SANJI_PARTY_TABLE
            luffy.activeAttack == AttackType.SANJI_DIABLE_JAMBE -> PlayerAnimState.ATTACK_SANJI_DIABLE_JAMBE
            luffy.activeAttack == AttackType.NAMI_SPARK -> PlayerAnimState.ATTACK_NAMI_SPARK
            luffy.activeAttack == AttackType.NAMI_CYCLONE -> PlayerAnimState.ATTACK_NAMI_CYCLONE
            luffy.activeAttack == AttackType.NAMI_THUNDERBOLT -> PlayerAnimState.ATTACK_NAMI_THUNDERBOLT
            luffy.activeAttack == AttackType.USOPP_SHOT -> PlayerAnimState.ATTACK_USOPP_SHOT
            luffy.activeAttack == AttackType.USOPP_EXPLODING_STAR -> PlayerAnimState.ATTACK_USOPP_EXPLODING_STAR
            luffy.activeAttack == AttackType.USOPP_FIREBIRD_STAR -> PlayerAnimState.ATTACK_USOPP_FIREBIRD_STAR
            luffy.activeAttack == AttackType.CHOPPER_KUNGFU -> PlayerAnimState.ATTACK_CHOPPER_KUNGFU
            luffy.activeAttack == AttackType.CHOPPER_GUARD_HEAL -> PlayerAnimState.ATTACK_CHOPPER_GUARD_HEAL
            luffy.activeAttack == AttackType.CHOPPER_MONSTER_POINT -> PlayerAnimState.ATTACK_CHOPPER_MONSTER_POINT
            !luffy.isGrounded && luffy.vy < 0f -> PlayerAnimState.JUMP_RISE
            !luffy.isGrounded -> PlayerAnimState.JUMP_FALL
            luffy.landTimer > 0f -> PlayerAnimState.LAND
            abs(luffy.vx) > 12f -> PlayerAnimState.RUN
            else -> PlayerAnimState.IDLE
        }
        val nextAnimTime = if (nextAnimState == luffy.animState) luffy.animTime + dt else 0f
        luffy = luffy.copy(animState = nextAnimState, animTime = nextAnimTime)

        // 7. Resolve Player Melee Hitboxes, Ranged Projectiles (Nami/Usopp) & Thunderbolt Clouds vs Enemies
        val updatedEnemies = current.enemies.toMutableList()
        val enemyMap = updatedEnemies.associateBy { it.id }.toMutableMap()
        var playedLightSfx = false
        var playedHeavySfx = false

        // Helper for Level 3 Map Room destructible pillars, shelves & Nami's desk!
        fun damageArlongMapRoomProps(epicenterX: Float, radius: Float, power: Int) {
            if (!isLevel3 || !current.isArlongMapRoom) return
            var anyBroken = false
            for (idx in arlongProps.indices) {
                val prop = arlongProps[idx]
                if (prop.isDestroyed) continue
                if (abs(prop.x - epicenterX) <= radius + prop.width * 0.5f) {
                    val nextPropHp = (prop.hp - power).coerceAtLeast(0)
                    arlongProps[idx] = prop.copy(hp = nextPropHp, flashTimer = 0.14f)
                    spawnMapPaperBurst(particles, prop.x, prop.y - prop.height * 0.5f, count = if (nextPropHp <= 0) 14 else 4)
                    if (nextPropHp <= 0) {
                        anyBroken = true
                        spawnStoneRubbleBurst(particles, prop.x, prop.y - 12f, count = 12)
                        towerDestruction = (towerDestruction + 0.17f).coerceAtMost(0.95f)
                    } else {
                        towerDestruction = (towerDestruction + 0.03f).coerceAtMost(0.92f)
                    }
                }
            }
            if (anyBroken) {
                soundEngine.play(RetroSfx.GATE_SMASH, 0.76f)
                triggerShake(6.5f, 0.28f)
            }
        }

        // Helper for Level 3 Gate Smash!
        fun smashArlongGate() {
            if (!isLevel3 || arlongPhase != ArlongStagePhase.GATE_INTRO) return
            arlongGateHp = 0
            arlongPhase = ArlongStagePhase.COURTYARD_FISHMEN
            waveIndex = 1
            soundEngine.play(RetroSfx.GATE_SMASH, 1.0f)
            soundEngine.play(RetroSfx.AXE_SLAM, 0.88f)
            triggerShake(9.8f, 0.46f)
            triggerFlash(0.20f, Color(0xFFFFF4C2))
            spawnStoneRubbleBurst(particles, WorldConstants.ARLONG_GATE_X, WorldConstants.BEACH_GROUND_Y - 26f, count = 28)
            floatingTexts.add(
                FloatingDamageText(
                    id = nextEntityId++,
                    x = WorldConstants.ARLONG_GATE_X + 10f,
                    y = WorldConstants.BEACH_GROUND_Y - 56f,
                    text = "ВОРОТА РАЗНЕСЕНЫ!",
                    color = Color(0xFFFFD84D),
                    isLarge = true
                )
            )
            waveBanner = WaveBannerState(
                text = "ДВОР АРЛОНГ-ПАРКА • ОСТОРОЖНО, БАССЕЙН!",
                isBoss = false,
                remainingSeconds = 2.6f
            )
            // Spawn Courtyard Tactical Wave: 2 Fishman Bouncers + 2 Balcony Chew Snipers!
            val courtyardWave = listOf(
                EnemyState(
                    id = "enemy_${nextEntityId++}",
                    kind = EnemyKind.FISHMAN_BOUNCER,
                    x = 274f,
                    y = WorldConstants.BEACH_GROUND_Y,
                    facing = FacingDirection.LEFT,
                    behavior = EnemyBehaviorState.CHASE,
                    attackCooldown = 0.65f,
                    patrolOriginX = 274f
                ),
                EnemyState(
                    id = "enemy_${nextEntityId++}",
                    kind = EnemyKind.FISHMAN_BOUNCER,
                    x = 508f,
                    y = WorldConstants.BEACH_GROUND_Y,
                    facing = FacingDirection.LEFT,
                    behavior = EnemyBehaviorState.CHASE,
                    attackCooldown = 0.85f,
                    patrolOriginX = 508f
                ),
                EnemyState(
                    id = "enemy_${nextEntityId++}",
                    kind = EnemyKind.FISHMAN_CHEW,
                    x = 236f,
                    y = WorldConstants.ARLONG_BALCONY_Y,
                    facing = FacingDirection.RIGHT,
                    behavior = EnemyBehaviorState.CHASE,
                    attackCooldown = 1.10f,
                    patrolOriginX = 236f
                ),
                EnemyState(
                    id = "enemy_${nextEntityId++}",
                    kind = EnemyKind.FISHMAN_CHEW,
                    x = 524f,
                    y = WorldConstants.ARLONG_BALCONY_Y,
                    facing = FacingDirection.LEFT,
                    behavior = EnemyBehaviorState.CHASE,
                    attackCooldown = 1.35f,
                    patrolOriginX = 524f
                )
            )
            for (e in courtyardWave) {
                enemyMap[e.id] = e
            }
        }

        // Check if active melee hitboxes smash the closed Arlong Park gate OR damage Map Room props!
        if (isLevel3 && activeHitboxes.isNotEmpty()) {
            if (arlongPhase == ArlongStagePhase.GATE_INTRO) {
                val gateBox = Rect2D(WorldConstants.ARLONG_GATE_X - 14f, WorldConstants.BEACH_GROUND_Y - 64f, 30f, 64f)
                if (activeHitboxes.any { it.bounds.intersects(gateBox) }) {
                    smashArlongGate()
                }
            } else if (current.isArlongMapRoom) {
                for (box in activeHitboxes) {
                    val isHeavy = box.attackType == AttackType.AXE ||
                        box.attackType == AttackType.GEAR4_KONG_GUN ||
                        box.attackType == AttackType.GEAR4_KING_KONG_GUN ||
                        box.attackType == AttackType.ZORO_ONIGIRI ||
                        box.attackType == AttackType.ZORO_TATSUMAKI ||
                        box.attackType == AttackType.ZORO_HELL_SLASH ||
                        box.attackType == AttackType.ZORO_PURGATORY_ONIGIRI ||
                        box.attackType == AttackType.ZORO_ASURA ||
                        box.attackType == AttackType.SANJI_DIABLE_JAMBE ||
                        box.attackType == AttackType.CHOPPER_MONSTER_POINT ||
                        box.attackType == AttackType.PISTOL
                    damageArlongMapRoomProps(box.bounds.centerX, box.bounds.width * 0.5f + 12f, if (isHeavy) 26 else 8)
                }
            }
        }

        // Gear 4 activation Haki shockwave pushes back & stuns nearby enemies!
        if (gear4ActivatedNow) {
            for (enemy in enemyMap.values.toList()) {
                val dist = abs(enemy.x - luffy.x)
                if (dist <= 118f) {
                    val pushDir = if (enemy.x >= luffy.x) 1f else -1f
                    val kbScale = if (enemy.kind.isBoss) 0.55f else 1f
                    val shockHp = if (enemy.kind == EnemyKind.BOSS_ARLONG && arlongPhase == ArlongStagePhase.FINISHER_SLOWMO) {
                        enemy.currentHp
                    } else {
                        (enemy.currentHp - 10).coerceAtLeast(1)
                    }
                    enemyMap[enemy.id] = enemy.copy(
                        currentHp = shockHp,
                        vx = pushDir * 245f * kbScale,
                        vy = -120f * kbScale,
                        isGrounded = false,
                        stunTimer = maxOf(enemy.stunTimer, 0.65f * kbScale),
                        flashTimer = 0.16f,
                        behavior = EnemyBehaviorState.HURT
                    )
                }
            }
        }

        // Zoro King of Hell Conqueror Haki burst pushes back & stuns nearby enemies!
        if (zoroHellActivatedNow) {
            for (enemy in enemyMap.values.toList()) {
                val dist = abs(enemy.x - luffy.x)
                if (dist <= 132f) {
                    val pushDir = if (enemy.x >= luffy.x) 1f else -1f
                    val kbScale = if (enemy.kind.isBoss) 0.55f else 1f
                    val shockHp = if (enemy.kind == EnemyKind.BOSS_ARLONG && arlongPhase == ArlongStagePhase.FINISHER_SLOWMO) {
                        enemy.currentHp
                    } else {
                        (enemy.currentHp - 16).coerceAtLeast(1)
                    }
                    enemyMap[enemy.id] = enemy.copy(
                        currentHp = shockHp,
                        vx = pushDir * 265f * kbScale,
                        vy = -135f * kbScale,
                        isGrounded = false,
                        stunTimer = maxOf(enemy.stunTimer, 0.78f * kbScale),
                        flashTimer = 0.18f,
                        behavior = EnemyBehaviorState.HURT
                    )
                }
            }
        }

        fun applyPlayerStrikeToEnemy(
            targetId: String,
            attackType: AttackType,
            damageDealt: Int,
            knockbackX: Float,
            knockbackY: Float,
            stunSeconds: Float,
            hitStopSeconds: Float,
            shakeIntensity: Float,
            isCrit: Boolean = false,
            isParry: Boolean = false
        ) {
            val target = enemyMap[targetId] ?: return
            var nextHp = (target.currentHp - damageDealt).coerceAtLeast(0)
            val isBoss = target.kind.isBoss

            // Level 3 Arlong Two-Phase & Slow-Mo Battle Axe Finisher Hooks!
            if (isLevel3 && target.kind == EnemyKind.BOSS_ARLONG) {
                if (arlongPhase == ArlongStagePhase.ARLONG_PHASE1 && nextHp <= target.maxHp / 2) {
                    nextHp = target.maxHp / 2
                    arlongPhase = ArlongStagePhase.TOWER_TRANSITION
                    arlongTransitionTimer = 0f
                    soundEngine.play(RetroSfx.SHARK_DART, 0.96f)
                    triggerShake(8.0f, 0.42f)
                    waveBanner = WaveBannerState("АРЛОНГ ВЗЛЕТАЕТ В КОМНАТУ КАРТ!", isBoss = true, remainingSeconds = 2.0f)
                } else if (arlongPhase == ArlongStagePhase.ARLONG_PHASE2) {
                    // Clashes in Map Room steadily raise Tower Destruction and scatter map papers!
                    val isHeavyHit = attackType == AttackType.AXE ||
                        attackType == AttackType.GEAR4_KONG_GUN ||
                        attackType == AttackType.GEAR4_KING_KONG_GUN ||
                        attackType == AttackType.ZORO_ONIGIRI ||
                        attackType == AttackType.ZORO_TATSUMAKI ||
                        attackType == AttackType.ZORO_HELL_SLASH ||
                        attackType == AttackType.ZORO_PURGATORY_ONIGIRI ||
                        attackType == AttackType.ZORO_ASURA ||
                        attackType == AttackType.SANJI_DIABLE_JAMBE ||
                        attackType == AttackType.NAMI_THUNDERBOLT ||
                        attackType == AttackType.USOPP_EXPLODING_STAR ||
                        attackType == AttackType.USOPP_FIREBIRD_STAR ||
                        attackType == AttackType.CHOPPER_MONSTER_POINT
                    damageArlongMapRoomProps(target.x, if (isHeavyHit) 78f else 42f, if (isHeavyHit) 26 else 10)
                    towerDestruction = (towerDestruction + if (isHeavyHit) 0.06f else 0.02f).coerceAtMost(0.95f)
                    if (nextHp <= 1) {
                        nextHp = 1
                        arlongPhase = ArlongStagePhase.FINISHER_SLOWMO
                        soundEngine.play(RetroSfx.ENEMY_HIT_HEAVY, 1.0f)
                        triggerShake(7.5f, 0.40f)
                        triggerFlash(0.18f, Color(0xFFFFE87C))
                        waveBanner = WaveBannerState("ДОБЕЙ РЕЗИНОВЫМ ТОПОРОМ!", isBoss = true, remainingSeconds = 4.0f)
                    }
                } else if (arlongPhase == ArlongStagePhase.FINISHER_SLOWMO) {
                    if (attackType == AttackType.AXE) {
                        // EPIC GUM-GUM BATTLE AXE FINISHER -> TOWER COLLAPSE!
                        nextHp = 0
                        arlongPhase = ArlongStagePhase.TOWER_COLLAPSE
                        arlongCollapseTimer = 0f
                        arlongCollapseX = target.x
                        towerDestruction = 1f
                        soundEngine.play(RetroSfx.AXE_SLAM, 1.0f)
                        soundEngine.play(RetroSfx.TOWER_COLLAPSE, 1.0f)
                        triggerShake(12.0f, 0.65f)
                        triggerFlash(0.28f, Color(0xFFFFF4C2))
                        spawnMapPaperBurst(particles, target.x, target.y - 24f, count = 34)
                        spawnStoneRubbleBurst(particles, target.x, target.y - 10f, count = 26)
                        enemyMap[targetId] = target.copy(currentHp = 1, flashTimer = 0.25f)
                        floatingTexts.add(
                            FloatingDamageText(
                                id = nextEntityId++,
                                x = target.x,
                                y = target.y - 46f,
                                text = "ГОМУ-ГОМУ НО ТОПОР!!",
                                color = Color(0xFFFFD84D),
                                isLarge = true
                            )
                        )
                        return
                    } else {
                        // Keep Arlong at 1 HP until Luffy finishes him with Battle Axe!
                        nextHp = 1
                    }
                }
            }

            val isHeavyStrike = attackType == AttackType.AXE ||
                attackType == AttackType.GEAR4_KONG_GUN ||
                attackType == AttackType.GEAR4_KING_KONG_GUN ||
                attackType == AttackType.ZORO_ONIGIRI ||
                attackType == AttackType.ZORO_TATSUMAKI ||
                attackType == AttackType.ZORO_PURGATORY_ONIGIRI ||
                attackType == AttackType.ZORO_ASURA ||
                attackType == AttackType.SANJI_DIABLE_JAMBE ||
                attackType == AttackType.NAMI_THUNDERBOLT ||
                attackType == AttackType.USOPP_EXPLODING_STAR ||
                attackType == AttackType.USOPP_FIREBIRD_STAR ||
                attackType == AttackType.CHOPPER_MONSTER_POINT ||
                isParry
            val isLauncher = attackType == AttackType.AXE ||
                attackType == AttackType.GEAR4_KONG_GUN ||
                attackType == AttackType.GEAR4_KING_KONG_GUN ||
                attackType == AttackType.ZORO_TATSUMAKI ||
                attackType == AttackType.ZORO_PURGATORY_ONIGIRI ||
                attackType == AttackType.ZORO_ASURA ||
                attackType == AttackType.SANJI_DIABLE_JAMBE ||
                attackType == AttackType.NAMI_THUNDERBOLT ||
                attackType == AttackType.USOPP_EXPLODING_STAR ||
                attackType == AttackType.CHOPPER_MONSTER_POINT ||
                isParry

            // Charge Gear 4 / Haki gauge when base Luffy lands attacks on enemies!
            if (luffy.activeHero == PlayableHero.LUFFY && !luffy.gear4Active) {
                val meterGain = when (attackType) {
                    AttackType.PISTOL -> 16f
                    AttackType.GATLING -> 5.5f
                    AttackType.AXE -> 24f
                    else -> 0f
                }
                if (meterGain > 0f && luffy.gear4Meter < WorldConstants.GEAR4_MAX_METER) {
                    val prevMeter = luffy.gear4Meter
                    val updatedMeter = (prevMeter + meterGain).coerceAtMost(WorldConstants.GEAR4_MAX_METER)
                    luffy = luffy.copy(gear4Meter = updatedMeter)
                    if (prevMeter < WorldConstants.GEAR4_MAX_METER && updatedMeter >= WorldConstants.GEAR4_MAX_METER) {
                        soundEngine.play(RetroSfx.PICKUP_BERRY, 0.92f)
                        floatingTexts.add(
                            FloatingDamageText(
                                id = nextEntityId++,
                                x = luffy.x,
                                y = luffy.y - 42f,
                                text = "ГИР 4 ГОТОВ! [G/V]",
                                color = Color(0xFFFF4D6D),
                                isLarge = true
                            )
                        )
                    }
                }
            }

            // Charge Zoro's King of Hell / Conqueror Haki gauge when base Zoro lands sword strikes or Steel Parries!
            if (luffy.activeHero == PlayableHero.ZORO && !luffy.zoroHellActive) {
                val baseGain = when (attackType) {
                    AttackType.ZORO_SLASH -> 12f
                    AttackType.ZORO_ONIGIRI -> 22f
                    AttackType.ZORO_TATSUMAKI -> 7f
                    else -> 0f
                }
                val meterGain = baseGain + if (isParry) 20f else 0f
                if (meterGain > 0f && luffy.zoroHellMeter < WorldConstants.ZORO_HELL_MAX_METER) {
                    val prevMeter = luffy.zoroHellMeter
                    val updatedMeter = (prevMeter + meterGain).coerceAtMost(WorldConstants.ZORO_HELL_MAX_METER)
                    luffy = luffy.copy(zoroHellMeter = updatedMeter)
                    if (prevMeter < WorldConstants.ZORO_HELL_MAX_METER && updatedMeter >= WorldConstants.ZORO_HELL_MAX_METER) {
                        soundEngine.play(RetroSfx.PICKUP_BERRY, 0.94f)
                        floatingTexts.add(
                            FloatingDamageText(
                                id = nextEntityId++,
                                x = luffy.x,
                                y = luffy.y - 44f,
                                text = "КОРОЛЬ АДА ГОТОВ! [G/V]",
                                color = Color(0xFF4CF57E),
                                isLarge = true
                            )
                        )
                    }
                }
            }

            // Steel Parry passive payoff: clang SFX, white flash, knock enemy down, grant 100% crit on next strike!
            if (isParry) {
                soundEngine.play(RetroSfx.ZORO_PARRY_CLANG, 1.0f)
                triggerFlash(0.15f, Color(0xFFFFFFFF))
                slashVfxs.add(
                    SwordSlashVfx(
                        id = nextEntityId++,
                        x = (luffy.x + target.x) * 0.5f,
                        y = target.y - target.kind.hitboxHeight * 0.55f,
                        facing = luffy.facing,
                        kind = SlashVfxKind.PARRY_FLASH,
                        maxAge = 0.24f
                    )
                )
                luffy = luffy.copy(
                    zoroParryCritReady = true,
                    invulnTimer = maxOf(luffy.invulnTimer, 0.45f)
                )
                floatingTexts.add(
                    FloatingDamageText(
                        id = nextEntityId++,
                        x = luffy.x,
                        y = luffy.y - 48f,
                        text = "ПАРИРОВАНИЕ! 100% КРИТ!",
                        color = Color(0xFFFFFFFF),
                        isLarge = true
                    )
                )
            }

            val kbScaleX = if (isBoss) {
                if (isHeavyStrike) 0.50f else 0.25f
            } else {
                1f
            }
            val kbScaleY = if (isBoss) {
                if (isLauncher) 0.42f else 0f
            } else {
                1f
            }
            val stunScale = if (isBoss) {
                if (isHeavyStrike) 0.55f else 0.30f
            } else {
                1f
            }

            val isGatling = attackType == AttackType.GATLING || attackType == AttackType.GEAR4_KONG_ORGAN
            val rawKbX = if (attackType == AttackType.SANJI_PARTY_TABLE ||
                attackType == AttackType.CHOPPER_GUARD_HEAL ||
                attackType == AttackType.GEAR4_KING_KONG_GUN ||
                attackType == AttackType.ZORO_ASURA
            ) {
                val pushDir = if (target.x >= luffy.x) 1f else -1f
                pushDir * abs(knockbackX)
            } else {
                knockbackX
            }
            val appliedVx = if (isGatling) rawKbX * 0.4f else rawKbX * kbScaleX
            val appliedVy = if (isGatling) 0f else knockbackY * kbScaleY
            val appliedStun = (stunSeconds * stunScale).coerceAtLeast(0.10f)

            // Apply 3-second Bleed DOT from Onigiri, Purgatory Onigiri & Asura!
            val appliedBleedDps = when (attackType) {
                AttackType.ZORO_ONIGIRI -> 12
                AttackType.ZORO_PURGATORY_ONIGIRI, AttackType.ZORO_ASURA -> 18
                else -> 0
            }
            val nextBleedTimer = if (appliedBleedDps > 0) WorldConstants.ZORO_BLEED_DURATION else target.bleedTimer
            val nextBleedDps = if (appliedBleedDps > 0) maxOf(target.bleedDps, appliedBleedDps) else target.bleedDps
            val nextBleedTick = if (appliedBleedDps > 0 && target.bleedTimer <= 0f) 0.5f else target.bleedTickTimer

            val hurtEnemy = target.copy(
                currentHp = nextHp,
                flashTimer = 0.14f,
                stunTimer = maxOf(target.stunTimer, appliedStun),
                behavior = if ((isLauncher || attackType == AttackType.NAMI_THUNDERBOLT) && !isBoss) {
                    EnemyBehaviorState.STUNNED
                } else {
                    EnemyBehaviorState.HURT
                },
                vx = appliedVx,
                vy = if (appliedVy < -10f) appliedVy else target.vy,
                isGrounded = if (appliedVy < -10f) false else target.isGrounded,
                attackHitApplied = false,
                bleedTimer = nextBleedTimer,
                bleedTickTimer = nextBleedTick,
                bleedDps = nextBleedDps
            )

            // Enqueue anime-style delayed slash bursts when Asura's 9 blades pass through an enemy!
            if (attackType == AttackType.ZORO_ASURA && nextHp > 0) {
                val delayedDmg = if (isCrit) (52f * WorldConstants.ZORO_HELL_CRIT_MULT).toInt() else 52
                delayedSlashes.add(
                    DelayedSlashEffect(
                        id = nextEntityId++,
                        targetId = target.id,
                        x = target.x,
                        y = target.y - target.kind.hitboxHeight * 0.52f,
                        damage = delayedDmg,
                        triggerTime = 0.25f
                    )
                )
                if (isBoss) {
                    delayedSlashes.add(
                        DelayedSlashEffect(
                            id = nextEntityId++,
                            targetId = target.id,
                            x = target.x + 8f,
                            y = target.y - target.kind.hitboxHeight * 0.65f,
                            damage = (delayedDmg * 0.85f).toInt(),
                            triggerTime = 0.44f
                        )
                    )
                }
            }

            val dmgColor = when {
                isCrit -> Color(0xFFFFF066)
                else -> when (attackType) {
                    AttackType.AXE -> Color(0xFFFFD84D)
                    AttackType.PISTOL -> Color(0xFFFFFFFF)
                    AttackType.GATLING -> Color(0xFF9BF0FF)
                    AttackType.GEAR4_KONG_GUN -> Color(0xFFFF4D6D)
                    AttackType.GEAR4_KONG_ORGAN -> Color(0xFFFF8FA3)
                    AttackType.GEAR4_KING_KONG_GUN -> Color(0xFFFFD84D)
                    AttackType.ZORO_SLASH -> Color(0xFF9EFFB8)
                    AttackType.ZORO_ONIGIRI -> Color(0xFF4CF57E)
                    AttackType.ZORO_TATSUMAKI -> Color(0xFF7CFFD0)
                    AttackType.ZORO_HELL_SLASH -> Color(0xFF4CF57E)
                    AttackType.ZORO_PURGATORY_ONIGIRI -> Color(0xFF66FFAA)
                    AttackType.ZORO_ASURA -> Color(0xFFD88CFF)
                    AttackType.SANJI_KICK -> Color(0xFFB8E6FF)
                    AttackType.SANJI_PARTY_TABLE -> Color(0xFFFFE57F)
                    AttackType.SANJI_DIABLE_JAMBE -> Color(0xFFFF8A1F)
                    AttackType.NAMI_SPARK -> Color(0xFF8EF4FF)
                    AttackType.NAMI_CYCLONE -> Color(0xFF7BE8FF)
                    AttackType.NAMI_THUNDERBOLT -> Color(0xFFFFF066)
                    AttackType.USOPP_SHOT -> Color(0xFFE6ECF8)
                    AttackType.USOPP_EXPLODING_STAR -> Color(0xFFFF9A3C)
                    AttackType.USOPP_FIREBIRD_STAR -> Color(0xFFFF6B1A)
                    AttackType.CHOPPER_KUNGFU -> Color(0xFFFFA8D5)
                    AttackType.CHOPPER_GUARD_HEAL -> Color(0xFF6EF598)
                    AttackType.CHOPPER_MONSTER_POINT -> Color(0xFFFFD84D)
                }
            }
            val isBigHit = isCrit ||
                attackType == AttackType.AXE ||
                attackType == AttackType.GEAR4_KONG_GUN ||
                attackType == AttackType.GEAR4_KING_KONG_GUN ||
                attackType == AttackType.ZORO_ONIGIRI ||
                attackType == AttackType.ZORO_PURGATORY_ONIGIRI ||
                attackType == AttackType.ZORO_ASURA ||
                attackType == AttackType.SANJI_DIABLE_JAMBE ||
                attackType == AttackType.NAMI_THUNDERBOLT ||
                attackType == AttackType.USOPP_EXPLODING_STAR ||
                attackType == AttackType.USOPP_FIREBIRD_STAR ||
                attackType == AttackType.CHOPPER_MONSTER_POINT
            floatingTexts.add(
                FloatingDamageText(
                    id = nextEntityId++,
                    x = target.x + (rng.nextFloat() * 10f - 5f),
                    y = target.y - target.kind.hitboxHeight - 4f,
                    text = when {
                        isCrit -> "КРИТ ${damageDealt}!"
                        isBigHit -> "${damageDealt}!"
                        else -> "$damageDealt"
                    },
                    color = dmgColor,
                    isLarge = isBigHit
                )
            )

            when (attackType) {
                AttackType.SANJI_DIABLE_JAMBE,
                AttackType.USOPP_EXPLODING_STAR,
                AttackType.USOPP_FIREBIRD_STAR -> {
                    spawnFireSparks(particles, target.x, target.y - target.kind.hitboxHeight * 0.55f, count = 14)
                }
                AttackType.NAMI_SPARK,
                AttackType.NAMI_THUNDERBOLT -> {
                    spawnElectricSparks(
                        particles,
                        target.x,
                        target.y - target.kind.hitboxHeight * 0.55f,
                        count = if (attackType == AttackType.NAMI_THUNDERBOLT) 16 else 7
                    )
                }
                AttackType.ZORO_HELL_SLASH,
                AttackType.ZORO_PURGATORY_ONIGIRI,
                AttackType.ZORO_ASURA -> {
                    val sparkCount = if (attackType == AttackType.ZORO_ASURA) 18 else 11
                    repeat(sparkCount) { idx ->
                        val ang = rng.nextFloat() * (2f * PI.toFloat())
                        val spd = 65f + rng.nextFloat() * 120f
                        particles.add(
                            Particle(
                                x = target.x,
                                y = target.y - target.kind.hitboxHeight * 0.55f,
                                vx = cos(ang) * spd,
                                vy = sin(ang) * spd,
                                size = if (idx % 3 == 0) 3.2f else 2.2f,
                                color = when {
                                    attackType == AttackType.ZORO_ASURA && idx % 3 == 0 -> Color(0xFFB55CFF)
                                    idx % 2 == 0 -> Color(0xFF4CF57E)
                                    else -> Color(0xFF151822)
                                },
                                age = 0f,
                                maxAge = 0.24f + rng.nextFloat() * 0.14f,
                                kind = ParticleKind.IMPACT_SPARK
                            )
                        )
                    }
                }
                else -> {
                    spawnImpactSparkBurst(
                        particles = particles,
                        x = target.x,
                        y = target.y - target.kind.hitboxHeight * 0.55f,
                        count = if (isHeavyStrike) 12 else 5
                    )
                }
            }

            val bossBonusHitStop = if (isBoss) 0.016f else 0f
            val isGear4Strike = attackType == AttackType.GEAR4_KONG_GUN ||
                attackType == AttackType.GEAR4_KONG_ORGAN ||
                attackType == AttackType.GEAR4_KING_KONG_GUN
            if (isHeavyStrike) {
                if (!playedHeavySfx) {
                    val heavySfx = when (attackType) {
                        AttackType.GEAR4_KONG_GUN, AttackType.GEAR4_KING_KONG_GUN -> RetroSfx.GEAR4_KONG_GUN_IMPACT
                        AttackType.ZORO_ASURA -> RetroSfx.ZORO_ASURA_BURST
                        AttackType.ZORO_PURGATORY_ONIGIRI -> RetroSfx.ZORO_HELL_SLASH
                        else -> if (isGear4Strike) RetroSfx.GEAR4_HAKI_HIT else RetroSfx.ENEMY_HIT_HEAVY
                    }
                    soundEngine.play(heavySfx, if (attackType == AttackType.GEAR4_KONG_GUN || attackType == AttackType.ZORO_ASURA) 1.0f else 0.96f)
                    playedHeavySfx = true
                }
                triggerShake(
                    shakeIntensity + (if (isBoss) 1.0f else 0f),
                    if (attackType == AttackType.AXE ||
                        attackType == AttackType.GEAR4_KING_KONG_GUN ||
                        attackType == AttackType.ZORO_ASURA ||
                        attackType == AttackType.SANJI_DIABLE_JAMBE ||
                        attackType == AttackType.NAMI_THUNDERBOLT ||
                        attackType == AttackType.CHOPPER_MONSTER_POINT
                    ) 0.42f else 0.24f
                )
                hitStopTimer = maxOf(hitStopTimer, hitStopSeconds + bossBonusHitStop)
            } else {
                if (!playedLightSfx) {
                    soundEngine.play(
                        when {
                            isGear4Strike -> RetroSfx.GEAR4_HAKI_HIT
                            attackType == AttackType.ZORO_HELL_SLASH -> RetroSfx.ZORO_HELL_SLASH
                            else -> RetroSfx.ENEMY_HIT_LIGHT
                        },
                        if (isGatling) 0.62f else 0.80f
                    )
                    playedLightSfx = true
                }
                triggerShake(shakeIntensity, 0.14f)
                hitStopTimer = maxOf(hitStopTimer, hitStopSeconds + bossBonusHitStop * 0.6f)
            }

            if (nextHp <= 0) {
                enemyMap.remove(targetId)
                luffy = luffy.copy(defeatedCount = luffy.defeatedCount + 1)
                soundEngine.play(RetroSfx.ENEMY_DEFEAT, 0.85f)
                spawnDustBurst(particles, target.x, target.y, count = if (isBoss) 20 else 10, isPier = isOnPier(target.x))
                spawnEnemyLoot(lootItems, target, luffy)

                if (isBoss && target.kind != EnemyKind.BOSS_ARLONG) {
                    buggyParts.clear()
                    levelPhase = LevelPhase.LEVEL_COMPLETE
                    soundEngine.play(RetroSfx.VICTORY_FANFARE, 0.95f)
                    triggerShake(8.0f, 0.48f)
                    triggerFlash(0.18f, Color(0xFFFFF4C2))
                    val defeatText = if (target.kind == EnemyKind.BOSS_BUGGY) {
                        "КАПИТАН БАГГИ ПОВЕРЖЕН!"
                    } else {
                        "КАПИТАН МОРГАН ПОВЕРЖЕН!"
                    }
                    waveBanner = WaveBannerState(
                        text = defeatText,
                        isBoss = true,
                        remainingSeconds = 2.6f
                    )
                }
            } else {
                enemyMap[targetId] = hurtEnemy
            }
        }

        // 7a. Resolve Active Melee Hitboxes (with Zoro Steel Parry + King of Hell Crit Multiplier!)
        val isZoroActive = luffy.activeHero == PlayableHero.ZORO
        val hadParryCritReady = isZoroActive && luffy.zoroParryCritReady
        val hitResults = combatSystem.resolveHits(
            hitboxes = activeHitboxes,
            targets = enemyMap.values.toList(),
            alreadyHitTargetIds = luffy.attackHitTargets,
            canParry = isZoroActive,
            guaranteedCrit = hadParryCritReady,
            passiveCritChance = if (isZoroActive && luffy.zoroHellActive) 0.35f else 0f,
            critDamageMultiplier = if (luffy.zoroHellActive) WorldConstants.ZORO_HELL_CRIT_MULT else WorldConstants.ZORO_BASE_CRIT_MULT,
            critRoll = { rng.nextFloat() }
        )
        if (hitResults.isNotEmpty()) {
            val newHitSet = luffy.attackHitTargets.toMutableSet()
            var triggeredParryThisFrame = false
            for (hit in hitResults) {
                newHitSet.add(hit.targetId)
                if (hit.isParry) triggeredParryThisFrame = true
                applyPlayerStrikeToEnemy(
                    targetId = hit.targetId,
                    attackType = hit.attackType,
                    damageDealt = hit.damageDealt,
                    knockbackX = hit.knockbackX,
                    knockbackY = hit.knockbackY,
                    stunSeconds = hit.stunSeconds,
                    hitStopSeconds = hit.hitStopSeconds,
                    shakeIntensity = hit.shakeIntensity,
                    isCrit = hit.isCrit,
                    isParry = hit.isParry
                )
            }
            luffy = luffy.copy(
                attackHitTargets = newHitSet,
                zoroParryCritReady = if (hadParryCritReady && !triggeredParryThisFrame) false else luffy.zoroParryCritReady
            )
        }

        // Guarantee Level 3 Slow-Mo Battle Axe slam ALWAYS lands and triggers Tower Collapse even if slightly offset!
        if (isSlowMoFinisher &&
            luffy.activeAttack == AttackType.AXE &&
            luffy.axeImpactTriggered &&
            arlongPhase == ArlongStagePhase.FINISHER_SLOWMO
        ) {
            val arlongTarget = enemyMap.values.firstOrNull { it.kind == EnemyKind.BOSS_ARLONG }
            if (arlongTarget != null) {
                applyPlayerStrikeToEnemy(
                    targetId = arlongTarget.id,
                    attackType = AttackType.AXE,
                    damageDealt = 99,
                    knockbackX = 0f,
                    knockbackY = 0f,
                    stunSeconds = 1.0f,
                    hitStopSeconds = 0.06f,
                    shakeIntensity = 12.0f
                )
            }
        }

        // 7b. Update Player Projectiles (Nami Spark Bolt, Cyclone Tempo; Usopp Pellet, Exploding Star, Firebird Star)
        fun detonateExplodingStar(bx: Float, by: Float, damage: Int) {
            val gY = groundYAt(bx)
            val blastY = by.coerceAtMost(gY - 4f)
            soundEngine.play(RetroSfx.USOPP_EXPLOSION, 0.96f)
            triggerShake(6.4f, 0.34f)
            triggerFlash(0.14f, Color(0xFFFFE5A0))
            slashVfxs.add(
                SwordSlashVfx(
                    id = nextEntityId++,
                    x = bx,
                    y = blastY - 4f,
                    facing = FacingDirection.RIGHT,
                    kind = SlashVfxKind.EXPLODING_STAR_BLAST,
                    maxAge = 0.34f
                )
            )
            craters.add(GroundCrater(x = bx, y = gY, age = 0f, maxAge = 2.5f))
            spawnFireSparks(particles, bx, blastY - 4f, count = 20)
            spawnDustBurst(particles, bx, gY, count = 10, isPier = isOnPier(bx))

            val snapshotEnemies = enemyMap.values.toList()
            for (enemy in snapshotEnemies) {
                val dist = hypot((enemy.x - bx).toDouble(), ((enemy.y - enemy.kind.hitboxHeight * 0.5f) - blastY).toDouble()).toFloat()
                if (dist <= 48f) {
                    val pushDir = if (enemy.x >= bx) 1f else -1f
                    applyPlayerStrikeToEnemy(
                        targetId = enemy.id,
                        attackType = AttackType.USOPP_EXPLODING_STAR,
                        damageDealt = damage,
                        knockbackX = pushDir * 170f,
                        knockbackY = -135f,
                        stunSeconds = 0.65f,
                        hitStopSeconds = 0.048f,
                        shakeIntensity = 6.2f
                    )
                }
            }
        }

        val alivePlayerProjectiles = mutableListOf<PlayerProjectile>()
        for (proj in playerProjectiles) {
            val nextAge = proj.age + dt
            val nextVy = proj.vy + WorldConstants.GRAVITY * proj.gravityScale * dt
            val px = proj.x + proj.vx * dt
            val py = if (proj.kind == PlayerProjectileKind.NAMI_CYCLONE) {
                groundYAt(px) - 14f
            } else {
                proj.y + nextVy * dt
            }
            if (nextAge >= proj.maxAge || px < -48f || px > WorldConstants.WORLD_WIDTH + 48f) {
                continue
            }

            // Projectile particle trails
            when (proj.kind) {
                PlayerProjectileKind.NAMI_ELECTRIC_BOLT -> {
                    if (rng.nextFloat() < 0.65f) {
                        spawnElectricSparks(particles, px, py, count = 1)
                    }
                }
                PlayerProjectileKind.NAMI_CYCLONE -> {
                    if (rng.nextFloat() < 0.80f) {
                        particles.add(
                            Particle(
                                x = px + (rng.nextFloat() * 14f - 7f),
                                y = py + (rng.nextFloat() * 18f - 9f),
                                vx = -proj.facing.sign * (30f + rng.nextFloat() * 45f),
                                vy = -40f - rng.nextFloat() * 40f,
                                size = 2f,
                                color = if (rng.nextBoolean()) Color(0xFFFFFFFF) else Color(0xFF7BE8FF),
                                age = 0f,
                                maxAge = 0.18f,
                                kind = ParticleKind.WIND_STREAK,
                                stretchX = 3.2f
                            )
                        )
                    }
                }
                PlayerProjectileKind.USOPP_EXPLODING_STAR -> {
                    if (rng.nextFloat() < 0.72f) {
                        particles.add(
                            Particle(
                                x = px - proj.facing.sign * 4f,
                                y = py,
                                vx = -proj.facing.sign * 20f,
                                vy = -16f + rng.nextFloat() * 16f,
                                size = 2.2f,
                                color = if (rng.nextBoolean()) Color(0xFFFFD84D) else Color(0xFFFF6B1A),
                                age = 0f,
                                maxAge = 0.16f,
                                kind = ParticleKind.IMPACT_SPARK
                            )
                        )
                    }
                }
                PlayerProjectileKind.USOPP_FIREBIRD_STAR -> {
                    // Rich fiery trail streaming behind the blazing pixel phoenix!
                    if (rng.nextFloat() < 0.92f) {
                        spawnFireSparks(particles, px - proj.facing.sign * 14f, py + (rng.nextFloat() * 14f - 7f), count = 2)
                    }
                }
                PlayerProjectileKind.USOPP_PELLET -> {
                    if (rng.nextFloat() < 0.45f) {
                        particles.add(
                            Particle(
                                x = px - proj.facing.sign * 4f,
                                y = py,
                                vx = -proj.facing.sign * 24f,
                                vy = 0f,
                                size = 1.8f,
                                color = Color(0xFFE6ECF8),
                                age = 0f,
                                maxAge = 0.09f,
                                kind = ParticleKind.WIND_STREAK,
                                stretchX = 2.6f
                            )
                        )
                    }
                }
                PlayerProjectileKind.ZORO_360_POUND_CANNON -> {
                    if (rng.nextFloat() < 0.85f) {
                        particles.add(
                            Particle(
                                x = px - proj.facing.sign * 6f,
                                y = py + (rng.nextFloat() * 18f - 9f),
                                vx = -proj.facing.sign * (40f + rng.nextFloat() * 30f),
                                vy = (rng.nextFloat() * 20f - 10f),
                                size = 2.2f,
                                color = if (rng.nextBoolean()) Color(0xFF4CF57E) else Color(0xFFE8FFF2),
                                age = 0f,
                                maxAge = 0.14f,
                                kind = ParticleKind.WIND_STREAK,
                                stretchX = 3.8f
                            )
                        )
                    }
                }
                PlayerProjectileKind.ZORO_HELL_WAVE -> {
                    if (rng.nextFloat() < 0.94f) {
                        particles.add(
                            Particle(
                                x = px - proj.facing.sign * 8f,
                                y = py + (rng.nextFloat() * 26f - 13f),
                                vx = -proj.facing.sign * (55f + rng.nextFloat() * 45f),
                                vy = -12f + rng.nextFloat() * 24f,
                                size = 2.8f,
                                color = when (rng.nextInt(3)) {
                                    0 -> Color(0xFF141821)
                                    1 -> Color(0xFF39FF88)
                                    else -> Color(0xFFE6FFF2)
                                },
                                age = 0f,
                                maxAge = 0.17f,
                                kind = ParticleKind.WIND_STREAK,
                                stretchX = 4.4f
                            )
                        )
                    }
                }
            }

            // Ground detonation for Exploding Star
            if (proj.kind == PlayerProjectileKind.USOPP_EXPLODING_STAR) {
                val gY = groundYAt(px)
                if (py + proj.height * 0.5f >= gY) {
                    detonateExplodingStar(px, gY, proj.damage)
                    continue
                }
            }

            // Reset multi-hit set periodically for Cyclone Tempo so it pushes & multi-hits foes!
            var hitIds = proj.hitTargetIds
            var multiTimer = proj.multiHitTimer + dt
            if (proj.kind == PlayerProjectileKind.NAMI_CYCLONE && multiTimer >= 0.22f) {
                multiTimer -= 0.22f
                hitIds = emptySet()
            }

            val updatedProj = proj.copy(
                x = px,
                y = py,
                vy = nextVy,
                age = nextAge,
                hitTargetIds = hitIds,
                multiHitTimer = multiTimer
            )

            var consumed = false
            val newHitIds = hitIds.toMutableSet()
            val pBounds = updatedProj.bounds

            // Level 3 Gate Smash via Nami/Usopp projectiles!
            if (isLevel3 && arlongPhase == ArlongStagePhase.GATE_INTRO) {
                val gateBox = Rect2D(WorldConstants.ARLONG_GATE_X - 14f, WorldConstants.BEACH_GROUND_Y - 64f, 30f, 64f)
                if (pBounds.intersects(gateBox)) {
                    smashArlongGate()
                    consumed = true
                }
            }
            if (!consumed && isLevel3 && current.isArlongMapRoom) {
                damageArlongMapRoomProps(px, 24f, if (proj.kind == PlayerProjectileKind.USOPP_FIREBIRD_STAR) 26 else 10)
            }

            val targetsSnapshot = enemyMap.values.toList()
            if (!consumed) {
                for (enemy in targetsSnapshot) {
                    if (enemy.id in newHitIds) continue
                    if (!pBounds.intersects(enemy.hurtbox)) continue

                    // HATCHAN 6-SWORD WHIRLWIND DEFLECTS RANGED PROJECTILES!
                    if (enemy.kind == EnemyKind.HATCHAN &&
                        enemy.behavior == EnemyBehaviorState.ATTACK &&
                        enemy.bossAttack == BossAttackVariant.HATCHAN_SPIN
                    ) {
                        soundEngine.play(RetroSfx.DEFLECT_CLANG, 0.92f)
                        spawnImpactSparkBurst(particles, px, py, count = 10)
                        floatingTexts.add(
                            FloatingDamageText(
                                id = nextEntityId++,
                                x = enemy.x,
                                y = enemy.y - 40f,
                                text = "ОТБИТО!",
                                color = Color(0xFFFFF59D),
                                isLarge = false
                            )
                        )
                        consumed = true
                        break
                    }

                    if (proj.kind == PlayerProjectileKind.USOPP_EXPLODING_STAR) {
                        detonateExplodingStar(px, py, proj.damage)
                        consumed = true
                        break
                    }

                    newHitIds.add(enemy.id)
                    val isZoroWave = proj.kind == PlayerProjectileKind.ZORO_360_POUND_CANNON ||
                        proj.kind == PlayerProjectileKind.ZORO_HELL_WAVE
                    val projParryCrit = isZoroWave && luffy.activeHero == PlayableHero.ZORO && luffy.zoroParryCritReady
                    val projPassiveCrit = proj.kind == PlayerProjectileKind.ZORO_HELL_WAVE && rng.nextFloat() < 0.35f
                    val projIsCrit = projParryCrit || projPassiveCrit
                    val projDamage = if (projIsCrit) {
                        val mult = if (proj.kind == PlayerProjectileKind.ZORO_HELL_WAVE || luffy.zoroHellActive) {
                            WorldConstants.ZORO_HELL_CRIT_MULT
                        } else {
                            WorldConstants.ZORO_BASE_CRIT_MULT
                        }
                        (proj.damage * mult).toInt().coerceAtLeast(proj.damage + 1)
                    } else {
                        proj.damage
                    }
                    if (projParryCrit) {
                        luffy = luffy.copy(zoroParryCritReady = false)
                    }
                    applyPlayerStrikeToEnemy(
                        targetId = enemy.id,
                        attackType = proj.attackType,
                        damageDealt = projDamage,
                        knockbackX = proj.knockbackX,
                        knockbackY = proj.knockbackY,
                        stunSeconds = proj.stunSeconds,
                        hitStopSeconds = when (proj.kind) {
                            PlayerProjectileKind.USOPP_FIREBIRD_STAR -> 0.044f
                            PlayerProjectileKind.ZORO_HELL_WAVE -> 0.036f
                            PlayerProjectileKind.ZORO_360_POUND_CANNON -> 0.028f
                            PlayerProjectileKind.NAMI_CYCLONE -> 0.022f
                            else -> 0.018f
                        },
                        shakeIntensity = when (proj.kind) {
                            PlayerProjectileKind.USOPP_FIREBIRD_STAR -> 5.6f
                            PlayerProjectileKind.ZORO_HELL_WAVE -> 4.6f
                            PlayerProjectileKind.ZORO_360_POUND_CANNON -> 3.4f
                            PlayerProjectileKind.NAMI_CYCLONE -> 3.0f
                            else -> 2.0f
                        },
                        isCrit = projIsCrit
                    )
                    if (!proj.piercing) {
                        consumed = true
                        break
                    }
                }
            }

            if (!consumed) {
                alivePlayerProjectiles.add(updatedProj.copy(hitTargetIds = newHitIds))
            }
        }

        // 7c. Update Nami's Thunderbolt Tempo Storm Clouds (charge -> massive lightning bolt AoE stun!)
        val aliveThunderClouds = mutableListOf<ThunderCloudEntity>()
        for (cloud in thunderClouds) {
            val nextAge = cloud.age + dt
            if (nextAge >= cloud.maxAge) continue

            var triggered = cloud.strikeTriggered
            if (!triggered && nextAge < cloud.strikeTime && rng.nextFloat() < 0.65f) {
                spawnElectricSparks(particles, cloud.x + (rng.nextFloat() * 24f - 12f), cloud.cloudY + 10f, count = 1)
            }
            if (!triggered && nextAge >= cloud.strikeTime) {
                triggered = true
                soundEngine.play(RetroSfx.NAMI_THUNDER, 1.0f)
                triggerShake(8.4f, 0.42f)
                triggerFlash(0.20f, Color(0xFFD8FAFF))
                craters.add(GroundCrater(x = cloud.x, y = cloud.groundY, age = 0f, maxAge = 2.8f))
                spawnElectricSparks(particles, cloud.x, cloud.groundY - 12f, count = 24)
                spawnDustBurst(particles, cloud.x, cloud.groundY, count = 12, isPier = isOnPier(cloud.x))

                val strikeBounds = Rect2D(
                    x = cloud.x - cloud.radiusX,
                    y = cloud.cloudY,
                    width = cloud.radiusX * 2f,
                    height = (cloud.groundY - cloud.cloudY + 10f).coerceAtLeast(60f)
                )
                val snapshotEnemies = enemyMap.values.toList()
                for (enemy in snapshotEnemies) {
                    if (strikeBounds.intersects(enemy.hurtbox)) {
                        val pushDir = if (enemy.x >= cloud.x) 1f else -1f
                        applyPlayerStrikeToEnemy(
                            targetId = enemy.id,
                            attackType = AttackType.NAMI_THUNDERBOLT,
                            damageDealt = cloud.damage,
                            knockbackX = pushDir * 115f,
                            knockbackY = -145f,
                            stunSeconds = 1.25f,
                            hitStopSeconds = 0.064f,
                            shakeIntensity = 7.8f
                        )
                    }
                }
            }
            aliveThunderClouds.add(cloud.copy(age = nextAge, strikeTriggered = triggered))
        }

        // 7d. Process Zoro Asura Anime Delayed Slash Detonations!
        val aliveDelayedSlashes = mutableListOf<DelayedSlashEffect>()
        for (ds in delayedSlashes) {
            val nextAge = ds.age + dt
            val tracked = enemyMap[ds.targetId]
            val sx = tracked?.x ?: ds.x
            val sy = if (tracked != null) tracked.y - tracked.kind.hitboxHeight * 0.52f else ds.y
            if (nextAge >= ds.triggerTime) {
                soundEngine.play(RetroSfx.ZORO_ASURA_BURST, 0.98f)
                triggerShake(8.4f, 0.34f)
                triggerFlash(0.14f, Color(0xFFE6C2FF))
                slashVfxs.add(
                    SwordSlashVfx(
                        id = nextEntityId++,
                        x = sx,
                        y = sy,
                        facing = luffy.facing,
                        kind = SlashVfxKind.ASURA_DELAYED_CUT,
                        maxAge = 0.36f
                    )
                )
                if (tracked != null) {
                    val pushDir = if (tracked.x >= luffy.x) 1f else -1f
                    applyPlayerStrikeToEnemy(
                        targetId = tracked.id,
                        attackType = AttackType.ZORO_PURGATORY_ONIGIRI,
                        damageDealt = ds.damage,
                        knockbackX = pushDir * 195f,
                        knockbackY = -145f,
                        stunSeconds = 0.85f,
                        hitStopSeconds = 0.065f,
                        shakeIntensity = 8.4f,
                        isCrit = true
                    )
                }
            } else {
                aliveDelayedSlashes.add(ds.copy(age = nextAge, x = sx, y = sy))
            }
        }

        // 7e. Process Zoro Onigiri / Purgatory / Asura Bleed DOT ticks on enemies!
        for (enemy in enemyMap.values.toList()) {
            if (enemy.bleedTimer <= 0f) continue
            val nextBleedTimer = (enemy.bleedTimer - dt).coerceAtLeast(0f)
            var nextTick = enemy.bleedTickTimer - dt
            var updatedEnemy = enemy.copy(
                bleedTimer = nextBleedTimer,
                bleedTickTimer = nextTick,
                bleedDps = if (nextBleedTimer <= 0f) 0 else enemy.bleedDps
            )
            if (rng.nextFloat() < 0.38f) {
                particles.add(
                    Particle(
                        x = enemy.x + (rng.nextFloat() * 12f - 6f),
                        y = enemy.y - enemy.kind.hitboxHeight * 0.55f + (rng.nextFloat() * 10f - 5f),
                        vx = (rng.nextFloat() * 24f - 12f),
                        vy = -12f - rng.nextFloat() * 18f,
                        size = 2.2f,
                        color = Color(0xFFE62E3D),
                        age = 0f,
                        maxAge = 0.24f,
                        kind = ParticleKind.DEBRIS,
                        gravityScale = 0.55f
                    )
                )
            }
            if (nextTick <= 0f && enemy.bleedDps > 0) {
                nextTick += 0.5f
                val tickDamage = (enemy.bleedDps / 2).coerceAtLeast(4)
                var nextHp = (updatedEnemy.currentHp - tickDamage).coerceAtLeast(0)
                if (isLevel3 && enemy.kind == EnemyKind.BOSS_ARLONG) {
                    if (arlongPhase == ArlongStagePhase.ARLONG_PHASE1 && nextHp < enemy.maxHp / 2) {
                        nextHp = enemy.maxHp / 2
                    } else if (arlongPhase == ArlongStagePhase.ARLONG_PHASE2 || arlongPhase == ArlongStagePhase.FINISHER_SLOWMO) {
                        nextHp = nextHp.coerceAtLeast(1)
                    }
                }
                floatingTexts.add(
                    FloatingDamageText(
                        id = nextEntityId++,
                        x = enemy.x + (rng.nextFloat() * 10f - 5f),
                        y = enemy.y - enemy.kind.hitboxHeight - 2f,
                        text = "-$tickDamage",
                        color = Color(0xFFFF385C),
                        isLarge = false
                    )
                )
                if (nextHp <= 0) {
                    enemyMap.remove(enemy.id)
                    luffy = luffy.copy(defeatedCount = luffy.defeatedCount + 1)
                    soundEngine.play(RetroSfx.ENEMY_DEFEAT, 0.85f)
                    spawnDustBurst(particles, enemy.x, enemy.y, count = if (enemy.kind.isBoss) 20 else 10, isPier = isOnPier(enemy.x))
                    spawnEnemyLoot(lootItems, enemy, luffy)
                    if (enemy.kind.isBoss && enemy.kind != EnemyKind.BOSS_ARLONG) {
                        buggyParts.clear()
                        levelPhase = LevelPhase.LEVEL_COMPLETE
                        soundEngine.play(RetroSfx.VICTORY_FANFARE, 0.95f)
                        triggerShake(8.0f, 0.48f)
                        triggerFlash(0.18f, Color(0xFFFFF4C2))
                        waveBanner = WaveBannerState(
                            text = if (enemy.kind == EnemyKind.BOSS_BUGGY) "КАПИТАН БАГГИ ПОВЕРЖЕН!" else "КАПИТАН МОРГАН ПОВЕРЖЕН!",
                            isBoss = true,
                            remainingSeconds = 2.6f
                        )
                    }
                    continue
                } else {
                    updatedEnemy = updatedEnemy.copy(currentHp = nextHp, bleedTickTimer = nextTick)
                }
            }
            enemyMap[enemy.id] = updatedEnemy
        }

        updatedEnemies.clear()
        updatedEnemies.addAll(enemyMap.values)

        // 8. Update Enemy AI & Combat Actions
        val aiCallbacks = EnemyStepCallbacks(
            groundYAt = ::groundYAt,
            playSfx = { sfx, vol -> soundEngine.play(sfx, vol) },
            triggerShake = ::triggerShake,
            spawnProjectile = { proj -> projectiles.add(proj) },
            spawnBuggyParts = { parts -> buggyParts.addAll(parts) },
            nextProjectileId = { nextEntityId++ },
            addParticle = { p -> particles.add(p) },
            onPlayerHit = { meleeHit ->
                applyDamageToLuffy(
                    damage = meleeHit.damage,
                    knockbackDir = meleeHit.knockbackDir,
                    shake = meleeHit.shakeIntensity,
                    hitStop = meleeHit.hitStopSeconds,
                    knockbackSpeed = meleeHit.knockbackSpeed
                )
            },
            isArlongMapRoom = current.isArlongMapRoom,
            isLevel3 = isLevel3,
            onlyChewsLeft = updatedEnemies.isNotEmpty() && updatedEnemies.all { it.kind == EnemyKind.FISHMAN_CHEW },
            isFinisherSlowMo = isSlowMoFinisher,
            addSlashVfx = { vfx -> slashVfxs.add(vfx) },
            onMapRoomShock = { shockX, radius -> damageArlongMapRoomProps(shockX, radius, 28) }
        )

        val steppedEnemies = updatedEnemies.map { enemy ->
            enemyAi.updateEnemy(enemy, luffy, dt, aiCallbacks)
        }.toMutableList()

        // 8.5. Update Buggy's Bara-Bara Festival Floating Body Parts
        val aliveBuggyParts = mutableListOf<BuggyPartEntity>()
        val bossBuggy = steppedEnemies.firstOrNull { it.kind == EnemyKind.BOSS_BUGGY && it.isAlive }
        if (bossBuggy != null) {
            for (part in buggyParts) {
                val nextAge = part.age + dt
                if (nextAge >= part.maxAge) continue
                val nextCd = (part.hitCooldown - dt).coerceAtLeast(0f)
                val progress = (nextAge / part.maxAge).coerceIn(0f, 1f)
                val nextAngle = part.orbitAngle + dt * 5.8f
                val targetX: Float
                val targetY: Float
                if (progress < 0.78f) {
                    // Swoop rapidly around the player from multiple angles!
                    val swoop = sin(nextAge * 4.2f + part.orbitAngle) * 14f
                    targetX = (luffy.x + cos(nextAngle) * (part.orbitRadiusX + swoop))
                        .coerceIn(WorldConstants.LEFT_BOUNDARY_X, WorldConstants.RIGHT_BOUNDARY_X)
                    targetY = (luffy.y - 18f + sin(nextAngle * 1.45f) * part.orbitRadiusY)
                        .coerceIn(72f, groundYAt(luffy.x) - 8f)
                } else {
                    // Zip cleanly back to Buggy's feet to reassemble
                    targetX = bossBuggy.x
                    targetY = bossBuggy.y - 20f
                }
                val followRate = if (progress < 0.78f) 6.5f else 11.5f
                val nx = part.x + (targetX - part.x) * (followRate * dt).coerceAtMost(1f)
                val ny = part.y + (targetY - part.y) * (followRate * dt).coerceAtMost(1f)
                val facing = if (nx >= part.x) FacingDirection.RIGHT else FacingDirection.LEFT

                if (rng.nextFloat() < 0.35f) {
                    particles.add(
                        Particle(
                            x = nx,
                            y = ny,
                            vx = (part.x - nx) * 2f,
                            vy = (part.y - ny) * 2f,
                            size = 2f,
                            color = Color(0xFF5CE1FF),
                            age = 0f,
                            maxAge = 0.16f,
                            kind = ParticleKind.DUST
                        )
                    )
                }

                var updatedPart = part.copy(
                    x = nx,
                    y = ny,
                    orbitAngle = nextAngle,
                    age = nextAge,
                    hitCooldown = nextCd,
                    facing = facing
                )
                if (levelPhase == LevelPhase.PLAYING && progress < 0.80f && nextCd <= 0f &&
                    updatedPart.bounds.intersects(luffy.hurtbox)
                ) {
                    val kbDir = if (luffy.x >= nx) 1f else -1f
                    applyDamageToLuffy(
                        damage = part.damage,
                        knockbackDir = kbDir,
                        shake = 4.6f,
                        hitStop = 0.045f
                    )
                    updatedPart = updatedPart.copy(hitCooldown = 0.55f)
                }
                aliveBuggyParts.add(updatedPart)
            }
        }

        // Helper for massive Buggy Ball detonation (AoE + Screen Shake + Bright Flash + Explosion VFX!)
        fun detonateBuggyBall(bx: Float, by: Float, damage: Int) {
            val groundY = groundYAt(bx)
            val explodeY = by.coerceAtMost(groundY - 4f)
            soundEngine.play(RetroSfx.BUGGY_EXPLOSION, 1.0f)
            triggerShake(10.5f, 0.54f)
            triggerFlash(0.24f, Color(0xFFFFE5A0))
            slashVfxs.add(
                SwordSlashVfx(
                    id = nextEntityId++,
                    x = bx,
                    y = explodeY - 8f,
                    facing = FacingDirection.RIGHT,
                    kind = SlashVfxKind.BUGGY_BALL_EXPLOSION,
                    maxAge = 0.44f
                )
            )
            craters.add(GroundCrater(x = bx, y = groundY, age = 0f, maxAge = 6.0f))
            spawnFireSparks(particles, bx, explodeY - 6f, count = 22)
            spawnDustBurst(particles, bx, groundY, count = 14, isPier = false)

            if (levelPhase == LevelPhase.PLAYING) {
                val dist = hypot((luffy.x - bx).toDouble(), ((luffy.y - 14f) - explodeY).toDouble()).toFloat()
                if (dist <= 54f) {
                    val kbDir = if (luffy.x >= bx) 1f else -1f
                    applyDamageToLuffy(
                        damage = damage,
                        knockbackDir = kbDir,
                        shake = 10.5f,
                        hitStop = 0.075f
                    )
                }
            }
        }

        // 9. Update Projectiles (Bullets, Shockwaves, Knives, Cabaji Flames, Buggy Fist & Buggy Ball)
        val aliveProjectiles = mutableListOf<Projectile>()
        for (proj in projectiles) {
            val nextAge = proj.age + dt
            val nextVy = proj.vy + WorldConstants.GRAVITY * proj.gravityScale * dt
            val px = proj.x + proj.vx * dt
            val py = if (proj.kind == ProjectileKind.MORGAN_SHOCKWAVE) {
                groundYAt(px) - 7f
            } else {
                proj.y + nextVy * dt
            }
            if (nextAge >= proj.maxAge || px < -24f || px > WorldConstants.WORLD_WIDTH + 24f) {
                continue
            }
            val updatedProj = proj.copy(x = px, y = py, vy = nextVy, age = nextAge)

            // Buggy Ball explodes on hitting the cobblestone ground!
            if (proj.kind == ProjectileKind.BUGGY_BALL) {
                if (rng.nextFloat() < 0.75f) {
                    particles.add(
                        Particle(
                            x = px - proj.facing.sign * 4f,
                            y = py - 5f,
                            vx = (rng.nextFloat() * 24f - 12f),
                            vy = -22f - rng.nextFloat() * 18f,
                            size = 2.5f,
                            color = if (rng.nextBoolean()) Color(0xFFFFD84D) else Color(0xFFFF6B2B),
                            age = 0f,
                            maxAge = 0.18f,
                            kind = ParticleKind.IMPACT_SPARK
                        )
                    )
                }
                val gY = groundYAt(px)
                if (py + proj.height * 0.5f >= gY) {
                    detonateBuggyBall(px, gY, proj.damage)
                    continue
                }
            }

            if (proj.kind == ProjectileKind.MORGAN_SHOCKWAVE && rng.nextFloat() < 0.65f) {
                particles.add(
                    Particle(
                        x = px - proj.facing.sign * 6f,
                        y = groundYAt(px) - 2f,
                        vx = -proj.facing.sign * (15f + rng.nextFloat() * 25f),
                        vy = -12f - rng.nextFloat() * 18f,
                        size = 2.5f,
                        color = if (rng.nextBoolean()) Color(0xFF7CE8FF) else Color(0xFFFFEAB3),
                        age = 0f,
                        maxAge = 0.22f,
                        kind = ParticleKind.DUST
                    )
                )
            }

            if (proj.kind == ProjectileKind.BUGGY_FIST && rng.nextFloat() < 0.6f) {
                particles.add(
                    Particle(
                        x = px - proj.facing.sign * 6f,
                        y = py,
                        vx = -proj.facing.sign * 25f,
                        vy = (rng.nextFloat() * 16f - 8f),
                        size = 2.5f,
                        color = Color(0xFF5CE1FF),
                        age = 0f,
                        maxAge = 0.18f,
                        kind = ParticleKind.DUST
                    )
                )
            }

            if (levelPhase == LevelPhase.PLAYING && updatedProj.bounds.intersects(luffy.hurtbox)) {
                val zoroSlicingBullets = luffy.activeHero == PlayableHero.ZORO && (
                    ((luffy.activeAttack == AttackType.ZORO_ONIGIRI ||
                        luffy.activeAttack == AttackType.ZORO_PURGATORY_ONIGIRI) &&
                        luffy.attackPhase == AttackPhase.ACTIVE) ||
                        (luffy.activeAttack == AttackType.ZORO_ASURA && luffy.attackPhase != AttackPhase.NONE)
                    )
                if (zoroSlicingBullets) {
                    soundEngine.play(RetroSfx.DEFLECT_CLANG, 0.92f)
                    spawnImpactSparkBurst(particles, px, py, count = 10)
                    floatingTexts.add(
                        FloatingDamageText(
                            id = nextEntityId++,
                            x = px,
                            y = py - 14f,
                            text = "РАЗРЕЗАНО!",
                            color = Color(0xFF66FFAA),
                            isLarge = false
                        )
                    )
                    continue
                }
                if (proj.kind == ProjectileKind.BUGGY_BALL) {
                    detonateBuggyBall(px, py, proj.damage)
                    continue
                }
                val isHeavyProj = proj.kind == ProjectileKind.MORGAN_SHOCKWAVE || proj.kind == ProjectileKind.BUGGY_FIST
                applyDamageToLuffy(
                    damage = proj.damage,
                    knockbackDir = proj.facing.sign.toFloat(),
                    shake = if (isHeavyProj) 6.2f else 4.2f,
                    hitStop = if (isHeavyProj) 0.055f else 0.035f
                )
                continue
            }
            aliveProjectiles.add(updatedProj)
        }

        // 10. Update & Collect Loot Items (Meat on the Bone & Berry Coins)
        val aliveLoot = mutableListOf<LootItem>()
        for (item in lootItems) {
            val nextAge = item.age + dt
            if (nextAge >= item.maxAge) continue
            val gY = groundYAt(item.x)
            var lvy = item.vy + WorldConstants.GRAVITY * 0.78f * dt
            var lvx = item.vx
            val lx = (item.x + lvx * dt).coerceIn(WorldConstants.LEFT_BOUNDARY_X, WorldConstants.RIGHT_BOUNDARY_X)
            var ly = item.y + lvy * dt
            if (ly >= gY) {
                ly = gY
                lvy = if (abs(lvy) > 45f) -lvy * 0.35f else 0f
                lvx = approach(lvx, 0f, 260f * dt)
            }
            val updatedItem = item.copy(x = lx, y = ly, vx = lvx, vy = lvy, age = nextAge)
            if (nextAge > 0.14f && updatedItem.bounds.intersects(luffy.hurtbox)) {
                when (item.kind) {
                    LootKind.MEAT_ON_BONE -> {
                        val healAmount = 30
                        val healedHp = (luffy.currentHp + healAmount).coerceAtMost(luffy.maxHp)
                        val updatedMap = luffy.heroHp.toMutableMap().apply {
                            this[luffy.activeHero] = healedHp
                            for (hero in PlayableHero.entries) {
                                if (hero != luffy.activeHero) {
                                    val mateHp = this[hero] ?: 0
                                    if (mateHp > 0) {
                                        this[hero] = (mateHp + 10).coerceAtMost(hero.maxHp)
                                    }
                                }
                            }
                        }
                        luffy = luffy.copy(currentHp = healedHp, heroHp = updatedMap)
                        soundEngine.play(RetroSfx.PICKUP_MEAT, 0.88f)
                        floatingTexts.add(
                            FloatingDamageText(
                                id = nextEntityId++,
                                x = luffy.x,
                                y = luffy.y - 34f,
                                text = "+30HP",
                                color = Color(0xFF4CD964),
                                isLarge = false
                            )
                        )
                    }
                    LootKind.BERRY_COIN -> {
                        val bonus = 100
                        luffy = luffy.copy(berries = luffy.berries + bonus)
                        soundEngine.play(RetroSfx.PICKUP_BERRY, 0.82f)
                        floatingTexts.add(
                            FloatingDamageText(
                                id = nextEntityId++,
                                x = luffy.x,
                                y = luffy.y - 34f,
                                text = "+100B",
                                color = Color(0xFFFFD84D),
                                isLarge = false
                            )
                        )
                    }
                }
                continue
            }
            aliveLoot.add(updatedItem)
        }

        // 11. Wave Progression (Levels 1-2 standard waves; Level 3 Arlong Park interactive phase progression)
        if (levelPhase == LevelPhase.PLAYING && steppedEnemies.isEmpty()) {
            if (isLevel3) {
                when (arlongPhase) {
                    ArlongStagePhase.COURTYARD_FISHMEN -> {
                        waveClearedDelay += dt
                        if (waveClearedDelay >= 1.05f) {
                            waveClearedDelay = 0f
                            arlongPhase = ArlongStagePhase.COURTYARD_HATCHAN
                            waveIndex = 2
                            soundEngine.play(RetroSfx.ZORO_DRAW, 0.92f)
                            triggerShake(4.8f, 0.28f)
                            waveBanner = WaveBannerState("МИНИ-БОСС: ХАТТЯН (6 МЕЧЕЙ)!", isBoss = false, remainingSeconds = 2.6f)
                            steppedEnemies.add(
                                EnemyState(
                                    id = "enemy_${nextEntityId++}",
                                    kind = EnemyKind.HATCHAN,
                                    x = 532f,
                                    y = WorldConstants.BEACH_GROUND_Y,
                                    facing = FacingDirection.LEFT,
                                    behavior = EnemyBehaviorState.CHASE,
                                    attackCooldown = 0.7f,
                                    patrolOriginX = 532f
                                )
                            )
                            steppedEnemies.add(
                                EnemyState(
                                    id = "enemy_${nextEntityId++}",
                                    kind = EnemyKind.FISHMAN_BOUNCER,
                                    x = 248f,
                                    y = WorldConstants.BEACH_GROUND_Y,
                                    facing = FacingDirection.RIGHT,
                                    behavior = EnemyBehaviorState.CHASE,
                                    attackCooldown = 0.9f,
                                    patrolOriginX = 248f
                                )
                            )
                            steppedEnemies.add(
                                EnemyState(
                                    id = "enemy_${nextEntityId++}",
                                    kind = EnemyKind.FISHMAN_CHEW,
                                    x = 524f,
                                    y = WorldConstants.ARLONG_BALCONY_Y,
                                    facing = FacingDirection.LEFT,
                                    behavior = EnemyBehaviorState.CHASE,
                                    attackCooldown = 1.2f,
                                    patrolOriginX = 524f
                                )
                            )
                        }
                    }
                    ArlongStagePhase.COURTYARD_HATCHAN -> {
                        waveClearedDelay += dt
                        if (waveClearedDelay >= 1.15f) {
                            waveClearedDelay = 0f
                            arlongPhase = ArlongStagePhase.ARLONG_PHASE1
                            waveIndex = 3
                            // ARLONG LEAPS DRAMATICALLY OUT OF THE CENTRAL POOL!
                            val poolCenterX = (WorldConstants.ARLONG_POOL_LEFT_X + WorldConstants.ARLONG_POOL_RIGHT_X) * 0.5f
                            soundEngine.play(RetroSfx.WATER_SPLASH, 1.0f)
                            soundEngine.play(RetroSfx.SHARK_DART, 0.94f)
                            triggerShake(7.8f, 0.42f)
                            spawnWaterSplashBurst(particles, poolCenterX, WorldConstants.ARLONG_POOL_SURFACE_Y, count = 28)
                            waveBanner = WaveBannerState("БОСС: АРЛОНГ ПИЛА!", isBoss = true, remainingSeconds = 2.8f)
                            steppedEnemies.add(
                                EnemyState(
                                    id = "enemy_${nextEntityId++}",
                                    kind = EnemyKind.BOSS_ARLONG,
                                    x = poolCenterX,
                                    y = WorldConstants.ARLONG_POOL_SURFACE_Y,
                                    vx = if (luffy.x < poolCenterX) 70f else -70f,
                                    vy = -245f,
                                    isGrounded = false,
                                    facing = if (luffy.x < poolCenterX) FacingDirection.LEFT else FacingDirection.RIGHT,
                                    behavior = EnemyBehaviorState.CHASE,
                                    attackCooldown = 0.95f,
                                    patrolOriginX = poolCenterX
                                )
                            )
                        }
                    }
                    else -> waveClearedDelay = 0f
                }
            } else {
                val levelDef = LevelCatalog.getLevel(current.levelNumber)
                val nextWaveIdx = waveIndex + 1
                if (nextWaveIdx < levelDef.waves.size) {
                    waveClearedDelay += dt
                    if (waveClearedDelay >= 1.15f) {
                        waveIndex = nextWaveIdx
                        waveClearedDelay = 0f
                        val nextWave = levelDef.waves[nextWaveIdx]
                        steppedEnemies.addAll(spawnWaveEnemies(nextWave.spawns, luffy.x, current.levelNumber))
                        waveBanner = WaveBannerState(
                            text = nextWave.bannerTitle,
                            isBoss = nextWave.isBossWave,
                            remainingSeconds = if (nextWave.isBossWave) 2.8f else 2.0f
                        )
                        if (nextWave.isBossWave) {
                            val bossIntroSfx = if (current.levelNumber == 2) RetroSfx.BUGGY_SPLIT else RetroSfx.MORGAN_SWING
                            soundEngine.play(bossIntroSfx, 0.92f)
                            triggerShake(5.5f, 0.35f)
                        }
                    }
                }
            }
        } else {
            waveClearedDelay = 0f
        }

        // 12. Update Particles, Craters, Floating Texts, Ambient Sky
        val aliveParticles = updateParticles(particles, dt)
        val aliveCraters = craters.mapNotNull { c ->
            val age = c.age + dt
            if (age < c.maxAge) c.copy(age = age) else null
        }
        val aliveTexts = updateFloatingTexts(floatingTexts, dt)
        val updatedClouds = updateClouds(current.clouds, dt)
        val updatedSeagulls = updateSeagulls(current.seagulls, dt)

        // 13. Even 1:1 Camera Tracking centered on Luffy
        val effectiveViewW = viewportVirtualWidth.coerceIn(60f, WorldConstants.WORLD_WIDTH)
        val smoothCamX = (luffy.x - effectiveViewW * 0.5f)
            .coerceIn(0f, (WorldConstants.WORLD_WIDTH - effectiveViewW).coerceAtLeast(0f))

        // 14. Update Screen Shake
        val (shakeOffX, shakeOffY, finalSI, finalSD, finalSE) = updateShake(
            shakeIntensity, shakeDuration, shakeElapsed, realDt
        )

        state = current.copy(
            luffy = luffy,
            waveIndex = waveIndex,
            waveClearedDelay = waveClearedDelay,
            levelPhase = levelPhase,
            waveBanner = waveBanner,
            activeHitboxes = activeHitboxes,
            enemies = steppedEnemies,
            buggyParts = aliveBuggyParts,
            projectiles = aliveProjectiles,
            playerProjectiles = alivePlayerProjectiles,
            thunderClouds = aliveThunderClouds,
            lootItems = aliveLoot,
            floatingTexts = aliveTexts,
            particles = aliveParticles,
            slashVfxs = slashVfxs,
            delayedSlashes = aliveDelayedSlashes,
            skyWalkRings = skyWalkRings,
            zoroAfterimages = zoroAfterimages,
            craters = aliveCraters,
            clouds = updatedClouds,
            seagulls = updatedSeagulls,
            cameraX = smoothCamX,
            worldTime = worldTime,
            hitStopTimer = hitStopTimer,
            shakeIntensity = finalSI,
            shakeDuration = finalSD,
            shakeElapsed = finalSE,
            shakeOffsetX = shakeOffX,
            shakeOffsetY = shakeOffY,
            screenFlashTimer = screenFlashTimer,
            screenFlashColor = screenFlashColor,
            banner = banner,
            arlongPhase = arlongPhase,
            arlongGateHp = arlongGateHp,
            arlongGateFlash = arlongGateFlash,
            luffyDrowning = luffyDrowning,
            luffyDrownTimer = luffyDrownTimer,
            luffyDrownX = luffyDrownX,
            towerDestruction = towerDestruction,
            arlongProps = arlongProps,
            arlongTransitionTimer = arlongTransitionTimer,
            arlongCollapseTimer = arlongCollapseTimer,
            arlongCollapseX = arlongCollapseX
        )
    }

    private fun spawnEnemyLoot(
        lootItems: MutableList<LootItem>,
        enemy: EnemyState,
        luffy: LuffyState
    ) {
        val coinCount = if (enemy.kind.isBoss) 6 else (if (rng.nextFloat() < 0.45f) 2 else 1)
        repeat(coinCount) { i ->
            val spread = if (coinCount == 1) 0f else (i - (coinCount - 1) * 0.5f) * 26f
            lootItems.add(
                LootItem(
                    id = nextEntityId++,
                    kind = LootKind.BERRY_COIN,
                    x = enemy.x,
                    y = enemy.y - 12f,
                    vx = spread + (rng.nextFloat() * 24f - 12f),
                    vy = -120f - rng.nextFloat() * 55f
                )
            )
        }

        // Meat on the Bone drop chance (higher when Luffy needs health!)
        val meatChance = if (luffy.currentHp < luffy.maxHp * 0.6f) 0.55f else 0.30f
        if (!enemy.kind.isBoss && rng.nextFloat() < meatChance) {
            lootItems.add(
                LootItem(
                    id = nextEntityId++,
                    kind = LootKind.MEAT_ON_BONE,
                    x = enemy.x,
                    y = enemy.y - 14f,
                    vx = (rng.nextFloat() * 56f - 28f),
                    vy = -145f - rng.nextFloat() * 35f
                )
            )
        }
    }

    private fun updateActiveAttack(
        luffy: LuffyState,
        dt: Float,
        enemies: List<EnemyState>,
        particles: MutableList<Particle>,
        slashVfxs: MutableList<SwordSlashVfx>,
        zoroAfterimages: MutableList<ZoroAfterimage>,
        craters: MutableList<GroundCrater>,
        playerProjectiles: MutableList<PlayerProjectile>,
        thunderClouds: MutableList<ThunderCloudEntity>,
        floatingTexts: MutableList<FloatingDamageText>,
        activeHitboxes: MutableList<AttackHitbox>,
        triggerShake: (Float, Float) -> Unit,
        triggerFlash: (Float, Color) -> Unit
    ): LuffyState {
        val nextTime = luffy.attackTimer + dt
        val dir = luffy.facing.sign.toFloat()

        return when (luffy.activeAttack) {
            AttackType.PISTOL -> {
                val windupEnd = 0.06f
                val extendEnd = 0.17f
                val peakHoldEnd = 0.22f
                val totalEnd = 0.37f

                when {
                    nextTime < windupEnd -> {
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.WINDUP,
                            stretchFactor = 0f
                        )
                    }
                    nextTime < extendEnd -> {
                        val wasWindup = luffy.attackPhase == AttackPhase.WINDUP
                        if (wasWindup) {
                            soundEngine.play(RetroSfx.PUNCH_WHOOSH, 0.85f)
                            spawnWindStreaks(particles, luffy.x + dir * 16f, luffy.y - 16f, dir, count = 4)
                        }
                        val t = ((nextTime - windupEnd) / (extendEnd - windupEnd)).coerceIn(0f, 1f)
                        val eased = 1f - (1f - t) * (1f - t)
                        val reach = WorldConstants.PISTOL_MAX_REACH * eased
                        val armStartX = luffy.x + dir * 8f
                        val fistCenterX = luffy.x + dir * (10f + reach)
                        val boxLeft = minOf(armStartX, fistCenterX - 9f)
                        val boxRight = maxOf(armStartX, fistCenterX + 9f)
                        val fistCenterY = luffy.y - 16f
                        val elevatedTarget = enemies.filter {
                            it.isAlive && (it.x - luffy.x) * dir > 0f && abs(it.x - luffy.x) <= reach + 26f && it.y < luffy.y - 10f
                        }.minByOrNull { abs(it.x - luffy.x) }
                        val boxTopY = if (elevatedTarget != null) {
                            minOf(fistCenterY - 9f, (elevatedTarget.y - elevatedTarget.kind.hitboxHeight * 0.5f) - 10f)
                                .coerceAtLeast(fistCenterY - 52f)
                        } else {
                            fistCenterY - 9f
                        }
                        val boxHeight = ((fistCenterY + 9f) - boxTopY).coerceAtLeast(18f)

                        activeHitboxes.add(
                            combatSystem.createHitbox(
                                sourceId = luffy.id,
                                attackType = AttackType.PISTOL,
                                bounds = Rect2D(boxLeft, boxTopY, (boxRight - boxLeft).coerceAtLeast(18f), boxHeight),
                                knockbackX = dir * 140f,
                                knockbackY = -50f,
                                stunSeconds = 0.26f,
                                hitStopSeconds = 0.028f,
                                shakeIntensity = 2.4f
                            )
                        )
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.ACTIVE,
                            stretchFactor = eased
                        )
                    }
                    nextTime < peakHoldEnd -> {
                        val reach = WorldConstants.PISTOL_MAX_REACH
                        val armStartX = luffy.x + dir * 8f
                        val fistCenterX = luffy.x + dir * (10f + reach)
                        val boxLeft = minOf(armStartX, fistCenterX - 10f)
                        val boxRight = maxOf(armStartX, fistCenterX + 10f)
                        val fistCenterY = luffy.y - 16f
                        val elevatedTarget = enemies.filter {
                            it.isAlive && (it.x - luffy.x) * dir > 0f && abs(it.x - luffy.x) <= reach + 26f && it.y < luffy.y - 10f
                        }.minByOrNull { abs(it.x - luffy.x) }
                        val boxTopY = if (elevatedTarget != null) {
                            minOf(fistCenterY - 9f, (elevatedTarget.y - elevatedTarget.kind.hitboxHeight * 0.5f) - 10f)
                                .coerceAtLeast(fistCenterY - 52f)
                        } else {
                            fistCenterY - 9f
                        }
                        val boxHeight = ((fistCenterY + 9f) - boxTopY).coerceAtLeast(18f)
                        if (luffy.attackTimer < extendEnd) {
                            spawnImpactSparkBurst(particles, fistCenterX + dir * 4f, fistCenterY, count = 6)
                        }
                        activeHitboxes.add(
                            combatSystem.createHitbox(
                                sourceId = luffy.id,
                                attackType = AttackType.PISTOL,
                                bounds = Rect2D(boxLeft, boxTopY, (boxRight - boxLeft).coerceAtLeast(20f), boxHeight),
                                knockbackX = dir * 155f,
                                knockbackY = -58f,
                                stunSeconds = 0.28f,
                                hitStopSeconds = 0.032f,
                                shakeIntensity = 2.6f
                            )
                        )
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.ACTIVE,
                            stretchFactor = 1f
                        )
                    }
                    nextTime < totalEnd -> {
                        val t = ((nextTime - peakHoldEnd) / (totalEnd - peakHoldEnd)).coerceIn(0f, 1f)
                        val elastic = ((1f - t) * (1f - t) * cos(t * PI * 0.5f).toFloat()).coerceAtLeast(0f)
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.RETRACT,
                            stretchFactor = elastic
                        )
                    }
                    else -> {
                        luffy.copy(
                            activeAttack = null,
                            attackPhase = AttackPhase.NONE,
                            attackTimer = 0f,
                            attackHitTargets = emptySet(),
                            stretchFactor = 0f
                        )
                    }
                }
            }

            AttackType.GATLING -> {
                val windupEnd = 0.08f
                val flurryEnd = 0.74f
                val totalEnd = 0.86f

                val agedGhosts = luffy.gatlingGhosts.mapNotNull { g ->
                    val a = g.age + dt
                    if (a < g.maxAge) g.copy(age = a) else null
                }.toMutableList()

                when {
                    nextTime < windupEnd -> {
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.WINDUP,
                            gatlingGhosts = agedGhosts
                        )
                    }
                    nextTime < flurryEnd -> {
                        var accum = luffy.gatlingSpawnAccum + dt
                        val interval = 0.044f
                        while (accum >= interval) {
                            accum -= interval
                            repeat(2) { idx ->
                                val yOff = if (idx == 0) {
                                    -2f + rng.nextFloat() * 2.5f
                                } else {
                                    1f + rng.nextFloat() * 3f
                                }
                                val reach = 52f + rng.nextFloat() * (WorldConstants.GATLING_MAX_REACH - 52f)
                                agedGhosts.add(
                                    GatlingPunchGhost(
                                        offsetX = rng.nextFloat() * 10f,
                                        offsetY = yOff,
                                        reach = reach,
                                        age = 0f,
                                        maxAge = 0.115f,
                                        tintCyan = false
                                    )
                                )
                            }
                            soundEngine.play(RetroSfx.GATLING_POP, 0.58f)
                            triggerShake(2.1f, 0.10f)
                            spawnWindStreaks(
                                particles = particles,
                                x = luffy.x + dir * (28f + rng.nextFloat() * 68f),
                                y = luffy.y - 16f + (rng.nextFloat() * 22f - 11f),
                                dir = dir,
                                count = 2
                            )
                            if (luffy.isGrounded) {
                                spawnFootstepPuff(particles, luffy.x - dir * 8f, luffy.y, isOnPier(luffy.x))
                            }
                        }

                        // Pulse multi-hit tracker every 0.085s to stunlock enemies in place!
                        var pulseTimer = luffy.gatlingHitPulseTimer + dt
                        var hitTargets = luffy.attackHitTargets
                        if (pulseTimer >= 0.085f) {
                            pulseTimer -= 0.085f
                            hitTargets = emptySet()
                        }

                        val boxX = if (dir > 0f) luffy.x + 8f else luffy.x - 8f - WorldConstants.GATLING_MAX_REACH
                        val hasElevatedTarget = enemies.any {
                            it.isAlive && (it.x - luffy.x) * dir > 0f &&
                                abs(it.x - luffy.x) <= WorldConstants.GATLING_MAX_REACH + 24f &&
                                it.y < luffy.y - 10f
                        }
                        val gatlingTopY = if (hasElevatedTarget) luffy.y - 68f else luffy.y - 30f
                        val gatlingH = if (hasElevatedTarget) 66f else 28f
                        activeHitboxes.add(
                            combatSystem.createHitbox(
                                sourceId = luffy.id,
                                attackType = AttackType.GATLING,
                                bounds = Rect2D(
                                    x = boxX,
                                    y = gatlingTopY,
                                    width = WorldConstants.GATLING_MAX_REACH,
                                    height = gatlingH
                                ),
                                knockbackX = dir * 24f,
                                knockbackY = 0f,
                                stunSeconds = 0.26f,
                                hitStopSeconds = 0.012f,
                                shakeIntensity = 2.2f
                            )
                        )

                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.ACTIVE,
                            stretchFactor = 1f,
                            attackHitTargets = hitTargets,
                            gatlingHitPulseTimer = pulseTimer,
                            gatlingSpawnAccum = accum,
                            gatlingGhosts = agedGhosts
                        )
                    }
                    nextTime < totalEnd -> {
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.RETRACT,
                            stretchFactor = 0f,
                            gatlingGhosts = agedGhosts
                        )
                    }
                    else -> {
                        luffy.copy(
                            activeAttack = null,
                            attackPhase = AttackPhase.NONE,
                            attackTimer = 0f,
                            attackHitTargets = emptySet(),
                            stretchFactor = 0f,
                            gatlingGhosts = emptyList()
                        )
                    }
                }
            }

            AttackType.AXE -> {
                val skyStretchEnd = 0.34f
                val slamImpactTime = 0.45f
                val holdInGroundEnd = 0.72f
                val totalEnd = 0.94f

                when {
                    nextTime < skyStretchEnd -> {
                        val t = (nextTime / skyStretchEnd).coerceIn(0f, 1f)
                        val eased = 1f - (1f - t) * (1f - t)
                        if (rng.nextFloat() < 0.45f) {
                            particles.add(
                                Particle(
                                    x = luffy.x + dir * 2f + (rng.nextFloat() * 14f - 7f),
                                    y = luffy.y - 24f - eased * WorldConstants.AXE_SKY_HEIGHT * 0.7f,
                                    vx = 0f,
                                    vy = -120f,
                                    size = 2f,
                                    color = Color(0xFFCCF5FF),
                                    age = 0f,
                                    maxAge = 0.16f,
                                    kind = ParticleKind.WIND_STREAK,
                                    stretchX = 0.4f
                                )
                            )
                        }
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.WINDUP,
                            stretchFactor = eased
                        )
                    }
                    nextTime < slamImpactTime -> {
                        val t = ((nextTime - skyStretchEnd) / (slamImpactTime - skyStretchEnd)).coerceIn(0f, 1f)
                        val impactX = (luffy.x + dir * WorldConstants.AXE_SLAM_FORWARD)
                            .coerceIn(WorldConstants.LEFT_BOUNDARY_X, WorldConstants.RIGHT_BOUNDARY_X)
                        particles.add(
                            Particle(
                                x = impactX + (rng.nextFloat() * 12f - 6f),
                                y = luffy.y - 120f * (1f - t),
                                vx = 0f,
                                vy = 260f,
                                size = 3f,
                                color = Color(0xFFFFFFFF),
                                age = 0f,
                                maxAge = 0.10f,
                                kind = ParticleKind.WIND_STREAK,
                                stretchX = 0.35f
                            )
                        )
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.ACTIVE,
                            stretchFactor = t
                        )
                    }
                    nextTime < holdInGroundEnd -> {
                        val impactX = (luffy.x + dir * WorldConstants.AXE_SLAM_FORWARD)
                            .coerceIn(WorldConstants.LEFT_BOUNDARY_X, WorldConstants.RIGHT_BOUNDARY_X)
                        val impactY = groundYAt(impactX)
                        var impacted = luffy.axeImpactTriggered
                        if (!impacted) {
                            impacted = true
                            soundEngine.play(RetroSfx.AXE_SLAM, 1.0f)
                            triggerShake(6.8f, 0.38f)
                            craters.add(GroundCrater(x = impactX, y = impactY, age = 0f, maxAge = 2.6f))
                            spawnAxeImpactEffects(particles, impactX, impactY, isOnPier(impactX))
                        }

                        // Massive AoE ground slam + shockwave hitbox launching & stunning enemies!
                        if (nextTime < slamImpactTime + 0.16f) {
                            activeHitboxes.add(
                                combatSystem.createHitbox(
                                    sourceId = luffy.id,
                                    attackType = AttackType.AXE,
                                    bounds = Rect2D(
                                        x = impactX - 48f,
                                        y = impactY - 40f,
                                        width = 96f,
                                        height = 42f
                                    ),
                                    knockbackX = dir * 145f,
                                    knockbackY = -195f,
                                    stunSeconds = 1.15f,
                                    hitStopSeconds = 0.065f,
                                    shakeIntensity = 7.0f
                                )
                            )
                        }

                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.ACTIVE,
                            stretchFactor = 1f,
                            axeImpactTriggered = impacted
                        )
                    }
                    nextTime < totalEnd -> {
                        val t = ((nextTime - holdInGroundEnd) / (totalEnd - holdInGroundEnd)).coerceIn(0f, 1f)
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.RETRACT,
                            stretchFactor = (1f - t).coerceAtLeast(0f)
                        )
                    }
                    else -> {
                        luffy.copy(
                            activeAttack = null,
                            attackPhase = AttackPhase.NONE,
                            attackTimer = 0f,
                            attackHitTargets = emptySet(),
                            stretchFactor = 0f,
                            axeImpactTriggered = false
                        )
                    }
                }
            }

            AttackType.GEAR4_KONG_GUN -> {
                // Spring-compressed Busoshoku Haki cannon punch with massive horizontal knockback!
                val windupEnd = 0.14f
                val extendEnd = 0.24f
                val peakHoldEnd = 0.34f
                val totalEnd = 0.48f

                when {
                    nextTime < windupEnd -> {
                        if (rng.nextFloat() < 0.45f) {
                            particles.add(
                                Particle(
                                    x = luffy.x - dir * 8f + (rng.nextFloat() * 10f - 5f),
                                    y = luffy.y - 18f + (rng.nextFloat() * 10f - 5f),
                                    vx = -dir * (30f + rng.nextFloat() * 40f),
                                    vy = -12f + rng.nextFloat() * 24f,
                                    size = 3.5f,
                                    color = if (rng.nextBoolean()) Color(0xFFFFFFFF) else Color(0xFFFF4D6D),
                                    age = 0f,
                                    maxAge = 0.20f,
                                    kind = ParticleKind.DUST
                                )
                            )
                        }
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.WINDUP,
                            stretchFactor = 0f
                        )
                    }
                    nextTime < peakHoldEnd -> {
                        if (luffy.attackPhase == AttackPhase.WINDUP) {
                            soundEngine.play(RetroSfx.GEAR4_KONG_GUN_IMPACT, 0.94f)
                            triggerShake(4.2f, 0.20f)
                            spawnWindStreaks(particles, luffy.x + dir * 20f, luffy.y - 18f, dir, count = 7)
                        }
                        val t = ((nextTime - windupEnd) / (extendEnd - windupEnd)).coerceIn(0f, 1f)
                        val eased = 1f - (1f - t) * (1f - t)
                        val reach = WorldConstants.KONG_GUN_MAX_REACH * eased
                        val armStartX = luffy.x + dir * 8f
                        val fistCenterX = luffy.x + dir * (12f + reach)
                        val boxLeft = minOf(armStartX, fistCenterX - 16f)
                        val boxRight = maxOf(armStartX, fistCenterX + 16f)
                        val fistCenterY = luffy.y - 18f
                        val elevatedTarget = enemies.filter {
                            it.isAlive && (it.x - luffy.x) * dir > 0f && abs(it.x - luffy.x) <= reach + 32f && it.y < luffy.y - 10f
                        }.minByOrNull { abs(it.x - luffy.x) }
                        val boxTopY = if (elevatedTarget != null) {
                            minOf(fistCenterY - 14f, (elevatedTarget.y - elevatedTarget.kind.hitboxHeight * 0.5f) - 12f)
                                .coerceAtLeast(fistCenterY - 58f)
                        } else {
                            fistCenterY - 14f
                        }
                        val boxHeight = ((fistCenterY + 14f) - boxTopY).coerceAtLeast(28f)
                        if (luffy.attackTimer < extendEnd && nextTime >= extendEnd) {
                            spawnImpactSparkBurst(particles, fistCenterX + dir * 6f, fistCenterY, count = 12)
                        }
                        activeHitboxes.add(
                            combatSystem.createHitbox(
                                sourceId = luffy.id,
                                attackType = AttackType.GEAR4_KONG_GUN,
                                bounds = Rect2D(boxLeft, boxTopY, (boxRight - boxLeft).coerceAtLeast(28f), boxHeight),
                                knockbackX = dir * 320f,
                                knockbackY = -115f,
                                stunSeconds = 0.55f,
                                hitStopSeconds = 0.065f,
                                shakeIntensity = 5.8f
                            )
                        )
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.ACTIVE,
                            stretchFactor = eased
                        )
                    }
                    nextTime < totalEnd -> {
                        val t = ((nextTime - peakHoldEnd) / (totalEnd - peakHoldEnd)).coerceIn(0f, 1f)
                        val elastic = ((1f - t) * (1f - t)).coerceAtLeast(0f)
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.RETRACT,
                            stretchFactor = elastic
                        )
                    }
                    else -> {
                        luffy.copy(
                            activeAttack = null,
                            attackPhase = AttackPhase.NONE,
                            attackTimer = 0f,
                            attackHitTargets = emptySet(),
                            stretchFactor = 0f
                        )
                    }
                }
            }

            AttackType.GEAR4_KONG_ORGAN -> {
                // Heavy multi-arm Busoshoku Haki barrage stunlocking enemies in place!
                val windupEnd = 0.08f
                val flurryEnd = 0.92f
                val totalEnd = 1.06f

                val agedGhosts = luffy.gatlingGhosts.mapNotNull { g ->
                    val a = g.age + dt
                    if (a < g.maxAge) g.copy(age = a) else null
                }.toMutableList()

                when {
                    nextTime < windupEnd -> {
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.WINDUP,
                            gatlingGhosts = agedGhosts
                        )
                    }
                    nextTime < flurryEnd -> {
                        var accum = luffy.gatlingSpawnAccum + dt
                        val interval = 0.048f
                        while (accum >= interval) {
                            accum -= interval
                            repeat(2) { idx ->
                                val yOff = if (idx == 0) {
                                    -6f + rng.nextFloat() * 5f
                                } else {
                                    2f + rng.nextFloat() * 6f
                                }
                                val reach = 58f + rng.nextFloat() * (WorldConstants.KONG_ORGAN_MAX_REACH - 58f)
                                agedGhosts.add(
                                    GatlingPunchGhost(
                                        offsetX = rng.nextFloat() * 14f,
                                        offsetY = yOff,
                                        reach = reach,
                                        age = 0f,
                                        maxAge = 0.135f,
                                        tintCyan = true
                                    )
                                )
                            }
                            soundEngine.play(RetroSfx.GEAR4_HAKI_HIT, 0.56f)
                            triggerShake(2.8f, 0.12f)
                            spawnWindStreaks(
                                particles = particles,
                                x = luffy.x + dir * (34f + rng.nextFloat() * 82f),
                                y = luffy.y - 18f + (rng.nextFloat() * 26f - 13f),
                                dir = dir,
                                count = 3
                            )
                        }

                        var pulseTimer = luffy.gatlingHitPulseTimer + dt
                        var hitTargets = luffy.attackHitTargets
                        if (pulseTimer >= 0.095f) {
                            pulseTimer -= 0.095f
                            hitTargets = emptySet()
                        }

                        val boxX = if (dir > 0f) luffy.x + 8f else luffy.x - 8f - WorldConstants.KONG_ORGAN_MAX_REACH
                        val hasElevatedTarget = enemies.any {
                            it.isAlive && (it.x - luffy.x) * dir > 0f &&
                                abs(it.x - luffy.x) <= WorldConstants.KONG_ORGAN_MAX_REACH + 28f &&
                                it.y < luffy.y - 10f
                        }
                        val organTopY = if (hasElevatedTarget) luffy.y - 72f else luffy.y - 36f
                        val organH = if (hasElevatedTarget) 70f else 34f
                        activeHitboxes.add(
                            combatSystem.createHitbox(
                                sourceId = luffy.id,
                                attackType = AttackType.GEAR4_KONG_ORGAN,
                                bounds = Rect2D(
                                    x = boxX,
                                    y = organTopY,
                                    width = WorldConstants.KONG_ORGAN_MAX_REACH,
                                    height = organH
                                ),
                                knockbackX = dir * 16f,
                                knockbackY = 0f,
                                stunSeconds = 0.30f,
                                hitStopSeconds = 0.014f,
                                shakeIntensity = 2.8f
                            )
                        )

                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.ACTIVE,
                            stretchFactor = 1f,
                            attackHitTargets = hitTargets,
                            gatlingHitPulseTimer = pulseTimer,
                            gatlingSpawnAccum = accum,
                            gatlingGhosts = agedGhosts
                        )
                    }
                    nextTime < totalEnd -> {
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.RETRACT,
                            stretchFactor = 0f,
                            gatlingGhosts = agedGhosts
                        )
                    }
                    else -> {
                        luffy.copy(
                            activeAttack = null,
                            attackPhase = AttackPhase.NONE,
                            attackTimer = 0f,
                            attackHitTargets = emptySet(),
                            stretchFactor = 0f,
                            gatlingGhosts = emptyList()
                        )
                    }
                }
            }

            AttackType.GEAR4_KING_KONG_GUN -> {
                // Colossal aerial leap + gigantic inflated Haki fist slam into the ground with massive AoE shockwave!
                val launchEnd = 0.34f
                val slamDescendEnd = 0.48f
                val impactHoldEnd = 0.78f
                val totalEnd = 0.96f

                when {
                    nextTime < launchEnd -> {
                        val t = (nextTime / launchEnd).coerceIn(0f, 1f)
                        val eased = 1f - (1f - t) * (1f - t)
                        if (rng.nextFloat() < 0.65f) {
                            particles.add(
                                Particle(
                                    x = luffy.x + (rng.nextFloat() * 24f - 12f),
                                    y = luffy.y - 26f + (rng.nextFloat() * 14f - 7f),
                                    vx = (rng.nextFloat() * 34f - 17f),
                                    vy = -30f - rng.nextFloat() * 30f,
                                    size = 4f + rng.nextFloat() * 2.5f,
                                    color = if (rng.nextBoolean()) Color(0xFFFF4D6D) else Color(0xFFFFFFFF),
                                    age = 0f,
                                    maxAge = 0.24f,
                                    kind = ParticleKind.DUST
                                )
                            )
                        }
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.WINDUP,
                            stretchFactor = eased
                        )
                    }
                    nextTime < slamDescendEnd && !luffy.isGrounded -> {
                        val t = ((nextTime - launchEnd) / (slamDescendEnd - launchEnd)).coerceIn(0f, 1f)
                        particles.add(
                            Particle(
                                x = luffy.x + dir * 16f + (rng.nextFloat() * 18f - 9f),
                                y = luffy.y - 22f,
                                vx = 0f,
                                vy = 220f,
                                size = 3.5f,
                                color = Color(0xFFFF4D6D),
                                age = 0f,
                                maxAge = 0.12f,
                                kind = ParticleKind.WIND_STREAK,
                                stretchX = 0.4f
                            )
                        )
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.ACTIVE,
                            stretchFactor = t,
                            vy = maxOf(luffy.vy, 340f)
                        )
                    }
                    nextTime < impactHoldEnd -> {
                        val impactX = (luffy.x + dir * 24f)
                            .coerceIn(WorldConstants.LEFT_BOUNDARY_X, WorldConstants.RIGHT_BOUNDARY_X)
                        val impactY = groundYAt(impactX)
                        var impacted = luffy.kingKongImpactTriggered
                        if (!impacted) {
                            impacted = true
                            soundEngine.play(RetroSfx.GEAR4_KING_KONG_SLAM, 1.0f)
                            triggerShake(10.5f, 0.52f)
                            triggerFlash(0.22f, Color(0xFFFFC2CC))
                            craters.add(GroundCrater(x = impactX, y = impactY, age = 0f, maxAge = 3.4f))
                            slashVfxs.add(
                                SwordSlashVfx(
                                    id = nextEntityId++,
                                    x = impactX,
                                    y = impactY - 4f,
                                    facing = luffy.facing,
                                    kind = SlashVfxKind.KING_KONG_BLAST,
                                    maxAge = 0.48f
                                )
                            )
                            spawnAxeImpactEffects(particles, impactX, impactY, isOnPier(impactX))
                            repeat(24) { i ->
                                val side = if (i % 2 == 0) 1f else -1f
                                particles.add(
                                    Particle(
                                        x = impactX + (rng.nextFloat() * 24f - 12f),
                                        y = impactY - 4f,
                                        vx = side * (65f + rng.nextFloat() * 140f),
                                        vy = -65f - rng.nextFloat() * 125f,
                                        size = 4f + rng.nextFloat() * 3f,
                                        color = when (i % 3) {
                                            0 -> Color(0xFFFF385C)
                                            1 -> Color(0xFF1F1629)
                                            else -> Color(0xFFFFD84D)
                                        },
                                        age = 0f,
                                        maxAge = 0.46f + rng.nextFloat() * 0.22f,
                                        kind = ParticleKind.DEBRIS,
                                        gravityScale = 0.72f
                                    )
                                )
                            }
                        }
                        if (nextTime < slamDescendEnd + 0.18f) {
                            activeHitboxes.add(
                                combatSystem.createHitbox(
                                    sourceId = luffy.id,
                                    attackType = AttackType.GEAR4_KING_KONG_GUN,
                                    bounds = Rect2D(
                                        x = impactX - 118f,
                                        y = impactY - 62f,
                                        width = 236f,
                                        height = 66f
                                    ),
                                    knockbackX = dir * 240f,
                                    knockbackY = -185f,
                                    stunSeconds = 1.35f,
                                    hitStopSeconds = 0.14f,
                                    shakeIntensity = 10.5f
                                )
                            )
                        }
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.ACTIVE,
                            stretchFactor = 1f,
                            kingKongImpactTriggered = impacted
                        )
                    }
                    nextTime < totalEnd -> {
                        val t = ((nextTime - impactHoldEnd) / (totalEnd - impactHoldEnd)).coerceIn(0f, 1f)
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.RETRACT,
                            stretchFactor = (1f - t).coerceAtLeast(0f)
                        )
                    }
                    else -> {
                        luffy.copy(
                            activeAttack = null,
                            attackPhase = AttackPhase.NONE,
                            attackTimer = 0f,
                            attackHitTargets = emptySet(),
                            stretchFactor = 0f,
                            kingKongImpactTriggered = false
                        )
                    }
                }
            }

            AttackType.ZORO_SLASH -> {
                val windupEnd = 0.03f
                val activeEnd = 0.125f
                val totalEnd = 0.185f
                val comboStep = luffy.zoroComboStep
                val isFinisher = (comboStep % 3) == 2

                when {
                    nextTime < windupEnd -> {
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.WINDUP,
                            stretchFactor = 0f
                        )
                    }
                    nextTime < activeEnd -> {
                        if (luffy.attackPhase == AttackPhase.WINDUP) {
                            val vfxKind = when (comboStep % 3) {
                                0 -> SlashVfxKind.DOWN_ARC
                                1 -> SlashVfxKind.UP_ARC
                                else -> SlashVfxKind.CROSS_X
                            }
                            slashVfxs.add(
                                SwordSlashVfx(
                                    id = nextEntityId++,
                                    x = luffy.x + dir * 24f,
                                    y = luffy.y - 16f,
                                    facing = luffy.facing,
                                    kind = vfxKind,
                                    maxAge = 0.19f
                                )
                            )
                            spawnZoroWindStreaks(particles, luffy.x + dir * 24f, luffy.y - 16f, dir, count = 5)
                            // 3rd combo hit automatically unleashes the 360 Pound Cannon emerald air wave!
                            if (isFinisher) {
                                soundEngine.play(RetroSfx.ZORO_360_CANNON, 0.96f)
                                triggerShake(3.2f, 0.18f)
                                playerProjectiles.add(
                                    PlayerProjectile(
                                        id = nextEntityId++,
                                        kind = PlayerProjectileKind.ZORO_360_POUND_CANNON,
                                        attackType = AttackType.ZORO_SLASH,
                                        x = luffy.x + dir * 22f,
                                        y = luffy.y - 17f,
                                        vx = dir * 275f,
                                        vy = 0f,
                                        width = 24f,
                                        height = 28f,
                                        damage = 28,
                                        knockbackX = dir * 135f,
                                        knockbackY = -55f,
                                        stunSeconds = 0.34f,
                                        facing = luffy.facing,
                                        maxAge = 0.68f,
                                        piercing = true
                                    )
                                )
                            }
                        }

                        val boxX = if (dir > 0f) luffy.x + 2f else luffy.x - 60f
                        activeHitboxes.add(
                            combatSystem.createHitbox(
                                sourceId = luffy.id,
                                attackType = AttackType.ZORO_SLASH,
                                bounds = Rect2D(boxX, luffy.y - 36f, 58f, 36f),
                                knockbackX = dir * (if (isFinisher) 155f else 85f),
                                knockbackY = if (isFinisher) -78f else -24f,
                                stunSeconds = if (isFinisher) 0.34f else 0.24f,
                                hitStopSeconds = if (isFinisher) 0.036f else 0.020f,
                                shakeIntensity = if (isFinisher) 3.4f else 2.0f
                            ).let { hb ->
                                if (isFinisher) hb.copy(damage = AttackType.ZORO_SLASH.baseDamage + 8) else hb
                            }
                        )
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.ACTIVE,
                            stretchFactor = 1f
                        )
                    }
                    nextTime < totalEnd -> {
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.RETRACT,
                            stretchFactor = 0f
                        )
                    }
                    else -> {
                        luffy.copy(
                            activeAttack = null,
                            attackPhase = AttackPhase.NONE,
                            attackTimer = 0f,
                            attackHitTargets = emptySet(),
                            stretchFactor = 0f,
                            zoroComboStep = (comboStep + 1) % 3,
                            zoroComboResetTimer = 0.72f
                        )
                    }
                }
            }

            AttackType.ZORO_ONIGIRI -> {
                val windupEnd = 0.14f
                val dashEnd = 0.31f
                val totalEnd = 0.44f

                when {
                    nextTime < windupEnd -> {
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.WINDUP,
                            stretchFactor = (nextTime / windupEnd).coerceIn(0f, 1f)
                        )
                    }
                    nextTime < dashEnd -> {
                        var slashDone = luffy.onigiriSlashTriggered
                        if (!slashDone) {
                            slashDone = true
                            soundEngine.play(RetroSfx.ZORO_ONIGIRI, 0.96f)
                            triggerShake(4.2f, 0.22f)
                            spawnDustBurst(particles, luffy.x, luffy.y, count = 8, isPier = isOnPier(luffy.x))
                        }

                        var ghostAccum = luffy.onigiriGhostAccum + dt
                        if (ghostAccum >= 0.032f) {
                            ghostAccum -= 0.032f
                            zoroAfterimages.add(
                                ZoroAfterimage(
                                    x = luffy.x,
                                    y = luffy.y,
                                    facing = luffy.facing,
                                    maxAge = 0.22f
                                )
                            )
                            spawnZoroWindStreaks(particles, luffy.x - dir * 8f, luffy.y - 16f, -dir, count = 2)
                        }

                        // Wide slicing hitbox sweeping through every enemy along the dash path
                        val boxX = if (dir > 0f) luffy.x - 16f else luffy.x - 36f
                        activeHitboxes.add(
                            combatSystem.createHitbox(
                                sourceId = luffy.id,
                                attackType = AttackType.ZORO_ONIGIRI,
                                bounds = Rect2D(boxX, luffy.y - 34f, 52f, 34f),
                                knockbackX = dir * 165f,
                                knockbackY = -85f,
                                stunSeconds = 0.45f,
                                hitStopSeconds = 0.055f,
                                shakeIntensity = 5.0f
                            )
                        )
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.ACTIVE,
                            stretchFactor = 1f,
                            onigiriSlashTriggered = slashDone,
                            onigiriGhostAccum = ghostAccum
                        )
                    }
                    nextTime < totalEnd -> {
                        if (luffy.attackPhase == AttackPhase.ACTIVE) {
                            slashVfxs.add(
                                SwordSlashVfx(
                                    id = nextEntityId++,
                                    x = luffy.x - dir * 24f,
                                    y = luffy.y - 18f,
                                    facing = luffy.facing,
                                    kind = SlashVfxKind.ONIGIRI_X,
                                    maxAge = 0.30f
                                )
                            )
                            spawnDustBurst(particles, luffy.x, luffy.y, count = 6, isPier = isOnPier(luffy.x))
                        }
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.RETRACT,
                            stretchFactor = 0f,
                            vx = luffy.vx * 0.55f
                        )
                    }
                    else -> {
                        luffy.copy(
                            activeAttack = null,
                            attackPhase = AttackPhase.NONE,
                            attackTimer = 0f,
                            attackHitTargets = emptySet(),
                            stretchFactor = 0f,
                            onigiriSlashTriggered = false,
                            onigiriGhostAccum = 0f
                        )
                    }
                }
            }

            AttackType.ZORO_TATSUMAKI -> {
                val windupEnd = 0.10f
                val spinEnd = 0.72f
                val totalEnd = 0.84f

                when {
                    nextTime < windupEnd -> {
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.WINDUP,
                            stretchFactor = (nextTime / windupEnd).coerceIn(0f, 1f)
                        )
                    }
                    nextTime < spinEnd -> {
                        // Spawn swirling emerald tornado particles around Zoro
                        if (rng.nextFloat() < 0.85f) {
                            val angle = rng.nextFloat() * (2f * PI.toFloat())
                            val radius = 12f + rng.nextFloat() * 38f
                            val h = rng.nextFloat() * 76f
                            particles.add(
                                Particle(
                                    x = luffy.x + cos(angle) * radius,
                                    y = luffy.y - 6f - h,
                                    vx = -sin(angle) * 95f,
                                    vy = -85f - rng.nextFloat() * 55f,
                                    size = 2.5f + rng.nextFloat() * 1.5f,
                                    color = if (rng.nextBoolean()) Color(0xFF4CF57E) else Color(0xFFE8FFF0),
                                    age = 0f,
                                    maxAge = 0.22f,
                                    kind = ParticleKind.WIND_STREAK,
                                    stretchX = 3.2f
                                )
                            )
                        }

                        var pulseTimer = luffy.gatlingHitPulseTimer + dt
                        var hitTargets = luffy.attackHitTargets
                        if (pulseTimer >= 0.15f) {
                            pulseTimer -= 0.15f
                            hitTargets = emptySet()
                            triggerShake(4.2f, 0.18f)
                            spawnDustBurst(particles, luffy.x, luffy.y, count = 4, isPier = isOnPier(luffy.x))
                        }

                        // Tall dragon whirlwind AoE around Zoro launching enemies high upward!
                        activeHitboxes.add(
                            combatSystem.createHitbox(
                                sourceId = luffy.id,
                                attackType = AttackType.ZORO_TATSUMAKI,
                                bounds = Rect2D(luffy.x - 54f, luffy.y - 94f, 108f, 96f),
                                knockbackX = dir * 48f,
                                knockbackY = -225f,
                                stunSeconds = 0.85f,
                                hitStopSeconds = 0.038f,
                                shakeIntensity = 5.4f
                            )
                        )
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.ACTIVE,
                            stretchFactor = 1f,
                            attackHitTargets = hitTargets,
                            gatlingHitPulseTimer = pulseTimer
                        )
                    }
                    nextTime < totalEnd -> {
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.RETRACT,
                            stretchFactor = (1f - (nextTime - spinEnd) / (totalEnd - spinEnd)).coerceIn(0f, 1f)
                        )
                    }
                    else -> {
                        luffy.copy(
                            activeAttack = null,
                            attackPhase = AttackPhase.NONE,
                            attackTimer = 0f,
                            attackHitTargets = emptySet(),
                            stretchFactor = 0f
                        )
                    }
                }
            }

            AttackType.ZORO_HELL_SLASH -> {
                // +50% faster King of Hell Haki swings — EVERY strike fires a long-range piercing black-green Haki wave!
                val windupEnd = 0.02f
                val activeEnd = 0.09f
                val totalEnd = 0.135f
                val comboStep = luffy.zoroComboStep
                val isFinisher = (comboStep % 3) == 2

                when {
                    nextTime < windupEnd -> {
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.WINDUP,
                            stretchFactor = 0f
                        )
                    }
                    nextTime < activeEnd -> {
                        if (luffy.attackPhase == AttackPhase.WINDUP) {
                            val vfxKind = when (comboStep % 3) {
                                0 -> SlashVfxKind.HELL_DOWN_ARC
                                1 -> SlashVfxKind.HELL_UP_ARC
                                else -> SlashVfxKind.HELL_CROSS_X
                            }
                            slashVfxs.add(
                                SwordSlashVfx(
                                    id = nextEntityId++,
                                    x = luffy.x + dir * 28f,
                                    y = luffy.y - 17f,
                                    facing = luffy.facing,
                                    kind = vfxKind,
                                    maxAge = 0.19f
                                )
                            )
                            playerProjectiles.add(
                                PlayerProjectile(
                                    id = nextEntityId++,
                                    kind = PlayerProjectileKind.ZORO_HELL_WAVE,
                                    attackType = AttackType.ZORO_HELL_SLASH,
                                    x = luffy.x + dir * 26f,
                                    y = luffy.y - 18f,
                                    vx = dir * 345f,
                                    vy = 0f,
                                    width = 30f,
                                    height = 36f,
                                    damage = if (isFinisher) 40 else 32,
                                    knockbackX = dir * 145f,
                                    knockbackY = if (isFinisher) -70f else -28f,
                                    stunSeconds = 0.36f,
                                    facing = luffy.facing,
                                    maxAge = 1.15f,
                                    piercing = true
                                )
                            )
                            spawnZoroWindStreaks(particles, luffy.x + dir * 26f, luffy.y - 17f, dir, count = 6)
                        }

                        val boxX = if (dir > 0f) luffy.x + 2f else luffy.x - 70f
                        activeHitboxes.add(
                            combatSystem.createHitbox(
                                sourceId = luffy.id,
                                attackType = AttackType.ZORO_HELL_SLASH,
                                bounds = Rect2D(boxX, luffy.y - 38f, 68f, 38f),
                                knockbackX = dir * (if (isFinisher) 175f else 105f),
                                knockbackY = if (isFinisher) -88f else -30f,
                                stunSeconds = if (isFinisher) 0.38f else 0.28f,
                                hitStopSeconds = if (isFinisher) 0.036f else 0.022f,
                                shakeIntensity = if (isFinisher) 4.2f else 2.6f
                            ).let { hb ->
                                if (isFinisher) hb.copy(damage = AttackType.ZORO_HELL_SLASH.baseDamage + 10) else hb
                            }
                        )
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.ACTIVE,
                            stretchFactor = 1f
                        )
                    }
                    nextTime < totalEnd -> {
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.RETRACT,
                            stretchFactor = 0f
                        )
                    }
                    else -> {
                        luffy.copy(
                            activeAttack = null,
                            attackPhase = AttackPhase.NONE,
                            attackTimer = 0f,
                            attackHitTargets = emptySet(),
                            stretchFactor = 0f,
                            zoroComboStep = (comboStep + 1) % 3,
                            zoroComboResetTimer = 0.75f
                        )
                    }
                }
            }

            AttackType.ZORO_PURGATORY_ONIGIRI -> {
                // Double lightning-fast arena-wide dash back and forth with full invulnerability & huge Haki damage!
                val windupEnd = 0.09f
                val dash1End = 0.30f
                val turnEnd = 0.36f
                val dash2End = 0.57f
                val totalEnd = 0.68f

                when {
                    nextTime < windupEnd -> {
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.WINDUP,
                            stretchFactor = (nextTime / windupEnd).coerceIn(0f, 1f),
                            purgatoryDashStage = 0
                        )
                    }
                    nextTime < dash1End -> {
                        var slashDone = luffy.onigiriSlashTriggered
                        if (!slashDone) {
                            slashDone = true
                            soundEngine.play(RetroSfx.ZORO_PURGATORY_ONIGIRI, 1.0f)
                            triggerShake(5.8f, 0.24f)
                            spawnDustBurst(particles, luffy.x, luffy.y, count = 10, isPier = isOnPier(luffy.x))
                        }
                        var ghostAccum = luffy.onigiriGhostAccum + dt
                        if (ghostAccum >= 0.024f) {
                            ghostAccum -= 0.024f
                            zoroAfterimages.add(
                                ZoroAfterimage(
                                    x = luffy.x,
                                    y = luffy.y,
                                    facing = luffy.facing,
                                    maxAge = 0.28f,
                                    isKingOfHell = true
                                )
                            )
                        }
                        val boxX = if (dir > 0f) luffy.x - 18f else luffy.x - 48f
                        activeHitboxes.add(
                            combatSystem.createHitbox(
                                sourceId = luffy.id,
                                attackType = AttackType.ZORO_PURGATORY_ONIGIRI,
                                bounds = Rect2D(boxX, luffy.y - 38f, 66f, 38f),
                                knockbackX = dir * 195f,
                                knockbackY = -105f,
                                stunSeconds = 0.55f,
                                hitStopSeconds = 0.052f,
                                shakeIntensity = 6.2f
                            )
                        )
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.ACTIVE,
                            stretchFactor = 1f,
                            onigiriSlashTriggered = slashDone,
                            onigiriGhostAccum = ghostAccum,
                            purgatoryDashStage = 1
                        )
                    }
                    nextTime < turnEnd -> {
                        if (luffy.purgatoryDashStage == 1) {
                            slashVfxs.add(
                                SwordSlashVfx(
                                    id = nextEntityId++,
                                    x = luffy.x - dir * 18f,
                                    y = luffy.y - 18f,
                                    facing = luffy.facing,
                                    kind = SlashVfxKind.PURGATORY_ONIGIRI_X,
                                    maxAge = 0.34f
                                )
                            )
                            soundEngine.play(RetroSfx.ZORO_PURGATORY_ONIGIRI, 0.96f)
                            triggerShake(6.4f, 0.24f)
                            triggerFlash(0.10f, Color(0xFFB8FFD2))
                            val flipped = if (luffy.facing == FacingDirection.RIGHT) FacingDirection.LEFT else FacingDirection.RIGHT
                            luffy.copy(
                                attackTimer = nextTime,
                                attackPhase = AttackPhase.ACTIVE,
                                facing = flipped,
                                attackHitTargets = emptySet(),
                                purgatoryDashStage = 2
                            )
                        } else {
                            luffy.copy(
                                attackTimer = nextTime,
                                attackPhase = AttackPhase.ACTIVE,
                                purgatoryDashStage = 2
                            )
                        }
                    }
                    nextTime < dash2End -> {
                        var ghostAccum = luffy.onigiriGhostAccum + dt
                        if (ghostAccum >= 0.024f) {
                            ghostAccum -= 0.024f
                            zoroAfterimages.add(
                                ZoroAfterimage(
                                    x = luffy.x,
                                    y = luffy.y,
                                    facing = luffy.facing,
                                    maxAge = 0.28f,
                                    isKingOfHell = true
                                )
                            )
                        }
                        val boxX = if (dir > 0f) luffy.x - 18f else luffy.x - 48f
                        activeHitboxes.add(
                            combatSystem.createHitbox(
                                sourceId = luffy.id,
                                attackType = AttackType.ZORO_PURGATORY_ONIGIRI,
                                bounds = Rect2D(boxX, luffy.y - 38f, 66f, 38f),
                                knockbackX = dir * 215f,
                                knockbackY = -125f,
                                stunSeconds = 0.68f,
                                hitStopSeconds = 0.062f,
                                shakeIntensity = 7.2f
                            )
                        )
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.ACTIVE,
                            stretchFactor = 1f,
                            onigiriGhostAccum = ghostAccum,
                            purgatoryDashStage = 3
                        )
                    }
                    nextTime < totalEnd -> {
                        if (luffy.purgatoryDashStage == 3) {
                            slashVfxs.add(
                                SwordSlashVfx(
                                    id = nextEntityId++,
                                    x = luffy.x - dir * 18f,
                                    y = luffy.y - 18f,
                                    facing = luffy.facing,
                                    kind = SlashVfxKind.PURGATORY_ONIGIRI_X,
                                    maxAge = 0.36f
                                )
                            )
                            spawnDustBurst(particles, luffy.x, luffy.y, count = 10, isPier = isOnPier(luffy.x))
                        }
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.RETRACT,
                            stretchFactor = 0f,
                            vx = luffy.vx * 0.45f,
                            purgatoryDashStage = 4
                        )
                    }
                    else -> {
                        luffy.copy(
                            activeAttack = null,
                            attackPhase = AttackPhase.NONE,
                            attackTimer = 0f,
                            attackHitTargets = emptySet(),
                            stretchFactor = 0f,
                            onigiriSlashTriggered = false,
                            onigiriGhostAccum = 0f,
                            purgatoryDashStage = 0
                        )
                    }
                }
            }

            AttackType.ZORO_ASURA -> {
                // Kyutoryu: Asura (Nine-Sword Style) — Demon aura -> Full-screen devastating cleave -> Anime delayed slash explosions!
                val windupEnd = 0.34f
                val activeEnd = 0.58f
                val totalEnd = 0.82f

                when {
                    nextTime < windupEnd -> {
                        if (rng.nextFloat() < 0.85f) {
                            val ang = rng.nextFloat() * (2f * PI.toFloat())
                            val rad = 16f + rng.nextFloat() * 26f
                            particles.add(
                                Particle(
                                    x = luffy.x + cos(ang) * rad,
                                    y = luffy.y - 20f + sin(ang) * rad * 0.8f,
                                    vx = -cos(ang) * 45f,
                                    vy = -38f - rng.nextFloat() * 35f,
                                    size = 3.0f,
                                    color = when (rng.nextInt(3)) {
                                        0 -> Color(0xFFA84BFF)
                                        1 -> Color(0xFF39FF88)
                                        else -> Color(0xFF141821)
                                    },
                                    age = 0f,
                                    maxAge = 0.24f,
                                    kind = ParticleKind.IMPACT_SPARK
                                )
                            )
                        }
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.WINDUP,
                            stretchFactor = (nextTime / windupEnd).coerceIn(0f, 1f)
                        )
                    }
                    nextTime < activeEnd -> {
                        var triggered = luffy.asuraSlashesTriggered
                        if (!triggered) {
                            triggered = true
                            soundEngine.play(RetroSfx.ZORO_ASURA_BURST, 1.0f)
                            triggerShake(11.8f, 0.54f)
                            triggerFlash(0.24f, Color(0xFFE6C2FF))
                            slashVfxs.add(
                                SwordSlashVfx(
                                    id = nextEntityId++,
                                    x = luffy.x,
                                    y = luffy.y - 24f,
                                    facing = luffy.facing,
                                    kind = SlashVfxKind.ASURA_NINE_BLADES,
                                    maxAge = 0.44f
                                )
                            )
                        }
                        if (nextTime < windupEnd + 0.14f) {
                            // Full-screen-width Asura nine-blade slash sweeping every enemy across the visible arena!
                            activeHitboxes.add(
                                combatSystem.createHitbox(
                                    sourceId = luffy.id,
                                    attackType = AttackType.ZORO_ASURA,
                                    bounds = Rect2D(luffy.x - 320f, luffy.y - 132f, 640f, 165f),
                                    knockbackX = dir * 235f,
                                    knockbackY = -175f,
                                    stunSeconds = 1.25f,
                                    hitStopSeconds = 0.135f,
                                    shakeIntensity = 11.5f
                                )
                            )
                        }
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.ACTIVE,
                            stretchFactor = 1f,
                            asuraSlashesTriggered = triggered
                        )
                    }
                    nextTime < totalEnd -> {
                        val t = ((nextTime - activeEnd) / (totalEnd - activeEnd)).coerceIn(0f, 1f)
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.RETRACT,
                            stretchFactor = (1f - t).coerceAtLeast(0f)
                        )
                    }
                    else -> {
                        luffy.copy(
                            activeAttack = null,
                            attackPhase = AttackPhase.NONE,
                            attackTimer = 0f,
                            attackHitTargets = emptySet(),
                            stretchFactor = 0f,
                            asuraSlashesTriggered = false
                        )
                    }
                }
            }

            AttackType.SANJI_KICK -> {
                val windupEnd = 0.03f
                val activeEnd = 0.12f
                val totalEnd = 0.185f
                val comboStep = luffy.sanjiComboStep

                when {
                    nextTime < windupEnd -> {
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.WINDUP,
                            stretchFactor = 0f
                        )
                    }
                    nextTime < activeEnd -> {
                        if (luffy.attackPhase == AttackPhase.WINDUP) {
                            slashVfxs.add(
                                SwordSlashVfx(
                                    id = nextEntityId++,
                                    x = luffy.x + dir * 16f,
                                    y = luffy.y - 16f,
                                    facing = luffy.facing,
                                    kind = SlashVfxKind.SANJI_KICK_ARC,
                                    maxAge = 0.15f
                                )
                            )
                            spawnWindStreaks(particles, luffy.x + dir * 18f, luffy.y - 16f, dir, count = 3)
                        }

                        val boxX = if (dir > 0f) luffy.x + 2f else luffy.x - 42f
                        val isFinisher = (comboStep % 3) == 2
                        activeHitboxes.add(
                            combatSystem.createHitbox(
                                sourceId = luffy.id,
                                attackType = AttackType.SANJI_KICK,
                                bounds = Rect2D(boxX, luffy.y - 32f, 40f, 30f),
                                knockbackX = dir * (if (isFinisher) 150f else 72f),
                                knockbackY = if (isFinisher) -88f else -24f,
                                stunSeconds = if (isFinisher) 0.30f else 0.20f,
                                hitStopSeconds = if (isFinisher) 0.028f else 0.016f,
                                shakeIntensity = if (isFinisher) 2.8f else 1.7f
                            ).let { hb ->
                                if (isFinisher) hb.copy(damage = AttackType.SANJI_KICK.baseDamage + 7) else hb
                            }
                        )
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.ACTIVE,
                            stretchFactor = 1f
                        )
                    }
                    nextTime < totalEnd -> {
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.RETRACT,
                            stretchFactor = 0f
                        )
                    }
                    else -> {
                        luffy.copy(
                            activeAttack = null,
                            attackPhase = AttackPhase.NONE,
                            attackTimer = 0f,
                            attackHitTargets = emptySet(),
                            stretchFactor = 0f,
                            sanjiComboStep = (comboStep + 1) % 3,
                            sanjiComboResetTimer = 0.62f
                        )
                    }
                }
            }

            AttackType.SANJI_PARTY_TABLE -> {
                val windupEnd = 0.07f
                val spinEnd = 0.66f
                val totalEnd = 0.78f

                when {
                    nextTime < windupEnd -> {
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.WINDUP,
                            stretchFactor = (nextTime / windupEnd).coerceIn(0f, 1f)
                        )
                    }
                    nextTime < spinEnd -> {
                        // Rapid acrobatic wind-kick streaks spraying horizontally from both sides
                        if (rng.nextFloat() < 0.80f) {
                            val side = if (rng.nextBoolean()) 1f else -1f
                            particles.add(
                                Particle(
                                    x = luffy.x + side * (14f + rng.nextFloat() * 24f),
                                    y = luffy.y - 16f + (rng.nextFloat() * 14f - 7f),
                                    vx = side * (120f + rng.nextFloat() * 90f),
                                    vy = -10f + rng.nextFloat() * 20f,
                                    size = 2f,
                                    color = if (rng.nextBoolean()) Color(0xFFFFFFFF) else Color(0xFF93D5FF),
                                    age = 0f,
                                    maxAge = 0.14f,
                                    kind = ParticleKind.WIND_STREAK,
                                    stretchX = 4.2f
                                )
                            )
                        }

                        var pulseTimer = luffy.gatlingHitPulseTimer + dt
                        var hitTargets = luffy.attackHitTargets
                        if (pulseTimer >= 0.135f) {
                            pulseTimer -= 0.135f
                            hitTargets = emptySet()
                            triggerShake(3.4f, 0.15f)
                            if (luffy.isGrounded) {
                                spawnDustBurst(particles, luffy.x, luffy.y, count = 4, isPier = isOnPier(luffy.x))
                            }
                        }

                        // 360-degree radial handstand kick AoE knocking enemies away on BOTH sides
                        activeHitboxes.add(
                            combatSystem.createHitbox(
                                sourceId = luffy.id,
                                attackType = AttackType.SANJI_PARTY_TABLE,
                                bounds = Rect2D(luffy.x - 48f, luffy.y - 34f, 96f, 34f),
                                knockbackX = 160f,
                                knockbackY = -65f,
                                stunSeconds = 0.42f,
                                hitStopSeconds = 0.024f,
                                shakeIntensity = 3.6f
                            )
                        )
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.ACTIVE,
                            stretchFactor = 1f,
                            attackHitTargets = hitTargets,
                            gatlingHitPulseTimer = pulseTimer
                        )
                    }
                    nextTime < totalEnd -> {
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.RETRACT,
                            stretchFactor = (1f - (nextTime - spinEnd) / (totalEnd - spinEnd)).coerceIn(0f, 1f)
                        )
                    }
                    else -> {
                        luffy.copy(
                            activeAttack = null,
                            attackPhase = AttackPhase.NONE,
                            attackTimer = 0f,
                            attackHitTargets = emptySet(),
                            stretchFactor = 0f
                        )
                    }
                }
            }

            AttackType.SANJI_DIABLE_JAMBE -> {
                val windupEnd = 0.22f
                val dashEnd = 0.42f
                val blastEnd = 0.60f
                val totalEnd = 0.74f

                when {
                    nextTime < windupEnd -> {
                        // Blazing pixel embers & flames coiling up Sanji's leg during ignition!
                        if (rng.nextFloat() < 0.85f) {
                            spawnFireSparks(particles, luffy.x + dir * 6f, luffy.y - 10f, count = 2)
                        }
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.WINDUP,
                            stretchFactor = (nextTime / windupEnd).coerceIn(0f, 1f)
                        )
                    }
                    nextTime < dashEnd -> {
                        // Blazing flying kick lunge leaving a stream of fire sparks
                        if (rng.nextFloat() < 0.90f) {
                            spawnFireSparks(particles, luffy.x - dir * 6f, luffy.y - 14f, count = 2)
                        }
                        if (luffy.attackPhase == AttackPhase.WINDUP) {
                            triggerShake(3.8f, 0.20f)
                            spawnDustBurst(particles, luffy.x, luffy.y, count = 6, isPier = isOnPier(luffy.x))
                        }

                        val boxX = if (dir > 0f) luffy.x - 6f else luffy.x - 44f
                        activeHitboxes.add(
                            combatSystem.createHitbox(
                                sourceId = luffy.id,
                                attackType = AttackType.SANJI_DIABLE_JAMBE,
                                bounds = Rect2D(boxX, luffy.y - 34f, 50f, 34f),
                                knockbackX = dir * 185f,
                                knockbackY = -170f,
                                stunSeconds = 0.85f,
                                hitStopSeconds = 0.060f,
                                shakeIntensity = 6.8f
                            )
                        )
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.ACTIVE,
                            stretchFactor = 1f
                        )
                    }
                    nextTime < blastEnd -> {
                        var exploded = luffy.diableExplosionTriggered
                        val blastX = (luffy.x + dir * 18f)
                            .coerceIn(WorldConstants.LEFT_BOUNDARY_X, WorldConstants.RIGHT_BOUNDARY_X)
                        val blastY = luffy.y - 16f
                        if (!exploded) {
                            exploded = true
                            soundEngine.play(RetroSfx.AXE_SLAM, 0.92f)
                            triggerShake(7.0f, 0.36f)
                            slashVfxs.add(
                                SwordSlashVfx(
                                    id = nextEntityId++,
                                    x = blastX,
                                    y = blastY,
                                    facing = luffy.facing,
                                    kind = SlashVfxKind.DIABLE_EXPLOSION,
                                    maxAge = 0.34f
                                )
                            )
                            spawnFireSparks(particles, blastX, blastY, count = 24)
                            craters.add(GroundCrater(x = blastX, y = groundYAt(blastX), age = 0f, maxAge = 2.0f))
                        }

                        // Massive fiery AoE blast hitting all surrounding enemies!
                        activeHitboxes.add(
                            combatSystem.createHitbox(
                                sourceId = luffy.id,
                                attackType = AttackType.SANJI_DIABLE_JAMBE,
                                bounds = Rect2D(blastX - 48f, blastY - 28f, 96f, 54f),
                                knockbackX = dir * 195f,
                                knockbackY = -180f,
                                stunSeconds = 0.95f,
                                hitStopSeconds = 0.065f,
                                shakeIntensity = 7.0f
                            )
                        )
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.ACTIVE,
                            stretchFactor = 1f,
                            diableExplosionTriggered = exploded,
                            vx = luffy.vx * 0.45f
                        )
                    }
                    nextTime < totalEnd -> {
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.RETRACT,
                            stretchFactor = (1f - (nextTime - blastEnd) / (totalEnd - blastEnd)).coerceIn(0f, 1f),
                            vx = luffy.vx * 0.5f
                        )
                    }
                    else -> {
                        luffy.copy(
                            activeAttack = null,
                            attackPhase = AttackPhase.NONE,
                            attackTimer = 0f,
                            attackHitTargets = emptySet(),
                            stretchFactor = 0f,
                            diableExplosionTriggered = false
                        )
                    }
                }
            }

            AttackType.NAMI_SPARK -> {
                val windupEnd = 0.045f
                val activeEnd = 0.16f
                val totalEnd = 0.25f

                when {
                    nextTime < windupEnd -> {
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.WINDUP,
                            stretchFactor = 0f
                        )
                    }
                    nextTime < activeEnd -> {
                        var fired = luffy.attackFired
                        if (!fired) {
                            fired = true
                            slashVfxs.add(
                                SwordSlashVfx(
                                    id = nextEntityId++,
                                    x = luffy.x + dir * 18f,
                                    y = luffy.y - 16f,
                                    facing = luffy.facing,
                                    kind = SlashVfxKind.NAMI_SPARK_ARC,
                                    maxAge = 0.18f
                                )
                            )
                            spawnElectricSparks(particles, luffy.x + dir * 20f, luffy.y - 16f, count = 6)
                            // Short crisp electric discharge auto-angled to hit balcony/elevated foes ahead!
                            val boltX = luffy.x + dir * 22f
                            val boltY = luffy.y - 16f
                            val (boltVx, boltVy) = aimedProjectileVelocity(boltX, boltY, dir, 255f, enemies, maxRange = 195f)
                            playerProjectiles.add(
                                PlayerProjectile(
                                    id = nextEntityId++,
                                    kind = PlayerProjectileKind.NAMI_ELECTRIC_BOLT,
                                    attackType = AttackType.NAMI_SPARK,
                                    x = boltX,
                                    y = boltY,
                                    vx = boltVx,
                                    vy = boltVy,
                                    width = 14f,
                                    height = 10f,
                                    damage = AttackType.NAMI_SPARK.baseDamage,
                                    knockbackX = dir * 95f,
                                    knockbackY = -28f,
                                    stunSeconds = 0.28f,
                                    facing = luffy.facing,
                                    piercing = false,
                                    maxAge = 0.44f
                                )
                            )
                        }
                        // Close-range Clima-Tact staff strike hitbox in front of Nami
                        val boxX = if (dir > 0f) luffy.x + 2f else luffy.x - 36f
                        activeHitboxes.add(
                            combatSystem.createHitbox(
                                sourceId = luffy.id,
                                attackType = AttackType.NAMI_SPARK,
                                bounds = Rect2D(boxX, luffy.y - 30f, 34f, 28f),
                                knockbackX = dir * 85f,
                                knockbackY = -26f,
                                stunSeconds = 0.24f,
                                hitStopSeconds = 0.018f,
                                shakeIntensity = 1.8f
                            )
                        )
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.ACTIVE,
                            stretchFactor = 1f,
                            attackFired = fired
                        )
                    }
                    nextTime < totalEnd -> {
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.RETRACT,
                            stretchFactor = 0f
                        )
                    }
                    else -> {
                        luffy.copy(
                            activeAttack = null,
                            attackPhase = AttackPhase.NONE,
                            attackTimer = 0f,
                            attackHitTargets = emptySet(),
                            stretchFactor = 0f,
                            attackFired = false
                        )
                    }
                }
            }

            AttackType.NAMI_CYCLONE -> {
                val windupEnd = 0.11f
                val activeEnd = 0.27f
                val totalEnd = 0.38f

                when {
                    nextTime < windupEnd -> {
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.WINDUP,
                            stretchFactor = (nextTime / windupEnd).coerceIn(0f, 1f)
                        )
                    }
                    nextTime < activeEnd -> {
                        var fired = luffy.attackFired
                        if (!fired) {
                            fired = true
                            triggerShake(3.2f, 0.20f)
                            spawnWindStreaks(particles, luffy.x + dir * 20f, luffy.y - 16f, dir, count = 6)
                            // Spinning weather cyclone traveling forward, pushing & multi-hitting enemies!
                            playerProjectiles.add(
                                PlayerProjectile(
                                    id = nextEntityId++,
                                    kind = PlayerProjectileKind.NAMI_CYCLONE,
                                    attackType = AttackType.NAMI_CYCLONE,
                                    x = luffy.x + dir * 22f,
                                    y = groundYAt(luffy.x + dir * 22f) - 14f,
                                    vx = dir * 142f,
                                    width = 24f,
                                    height = 30f,
                                    damage = AttackType.NAMI_CYCLONE.baseDamage,
                                    knockbackX = dir * 185f,
                                    knockbackY = -65f,
                                    stunSeconds = 0.36f,
                                    facing = luffy.facing,
                                    piercing = true,
                                    maxAge = 1.15f
                                )
                            )
                        }
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.ACTIVE,
                            stretchFactor = 1f,
                            attackFired = fired
                        )
                    }
                    nextTime < totalEnd -> {
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.RETRACT,
                            stretchFactor = 0f
                        )
                    }
                    else -> {
                        luffy.copy(
                            activeAttack = null,
                            attackPhase = AttackPhase.NONE,
                            attackTimer = 0f,
                            attackHitTargets = emptySet(),
                            stretchFactor = 0f,
                            attackFired = false
                        )
                    }
                }
            }

            AttackType.NAMI_THUNDERBOLT -> {
                val windupEnd = 0.14f
                val activeEnd = 0.34f
                val totalEnd = 0.45f

                when {
                    nextTime < windupEnd -> {
                        if (rng.nextFloat() < 0.75f) {
                            spawnElectricSparks(particles, luffy.x, luffy.y - 34f, count = 1)
                        }
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.WINDUP,
                            stretchFactor = (nextTime / windupEnd).coerceIn(0f, 1f)
                        )
                    }
                    nextTime < activeEnd -> {
                        var fired = luffy.attackFired
                        if (!fired) {
                            fired = true
                            // Smartly summon storm cloud directly above nearest enemy in front of Nami (or 88px ahead)
                            val frontEnemies = enemies.filter { enemy ->
                                enemy.isAlive && (enemy.x - luffy.x) * dir > -16f && abs(enemy.x - luffy.x) <= 240f
                            }
                            val targetEnemy = frontEnemies.minByOrNull { abs(it.x - luffy.x) }
                            val targetX = (targetEnemy?.x ?: (luffy.x + dir * 88f))
                                .coerceIn(WorldConstants.LEFT_BOUNDARY_X + 20f, WorldConstants.RIGHT_BOUNDARY_X - 20f)
                            val gY = groundYAt(targetX)
                            val cloudTopY = (minOf(gY, targetEnemy?.y ?: gY) - 76f).coerceAtLeast(36f)
                            thunderClouds.add(
                                ThunderCloudEntity(
                                    id = nextEntityId++,
                                    x = targetX,
                                    cloudY = cloudTopY,
                                    groundY = gY,
                                    strikeTime = 0.24f,
                                    maxAge = 0.86f,
                                    damage = AttackType.NAMI_THUNDERBOLT.baseDamage,
                                    radiusX = 48f
                                )
                            )
                            spawnElectricSparks(particles, luffy.x, luffy.y - 34f, count = 8)
                        }
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.ACTIVE,
                            stretchFactor = 1f,
                            attackFired = fired
                        )
                    }
                    nextTime < totalEnd -> {
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.RETRACT,
                            stretchFactor = 0f
                        )
                    }
                    else -> {
                        luffy.copy(
                            activeAttack = null,
                            attackPhase = AttackPhase.NONE,
                            attackTimer = 0f,
                            attackHitTargets = emptySet(),
                            stretchFactor = 0f,
                            attackFired = false
                        )
                    }
                }
            }

            AttackType.USOPP_SHOT -> {
                val windupEnd = 0.08f
                val activeEnd = 0.17f
                val totalEnd = 0.24f

                when {
                    nextTime < windupEnd -> {
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.WINDUP,
                            stretchFactor = (nextTime / windupEnd).coerceIn(0f, 1f)
                        )
                    }
                    nextTime < activeEnd -> {
                        var fired = luffy.attackFired
                        if (!fired) {
                            fired = true
                            soundEngine.play(RetroSfx.USOPP_SHOT, 0.86f)
                            spawnWindStreaks(particles, luffy.x + dir * 18f, luffy.y - 16f, dir, count = 3)
                            val shotX = luffy.x + dir * 18f
                            val shotY = luffy.y - 16f
                            val (shotVx, shotVy) = aimedProjectileVelocity(shotX, shotY, dir, 345f, enemies, maxRange = 380f)
                            playerProjectiles.add(
                                PlayerProjectile(
                                    id = nextEntityId++,
                                    kind = PlayerProjectileKind.USOPP_PELLET,
                                    attackType = AttackType.USOPP_SHOT,
                                    x = shotX,
                                    y = shotY,
                                    vx = shotVx,
                                    vy = shotVy,
                                    width = 10f,
                                    height = 8f,
                                    damage = AttackType.USOPP_SHOT.baseDamage,
                                    knockbackX = dir * 82f,
                                    knockbackY = -20f,
                                    stunSeconds = 0.20f,
                                    facing = luffy.facing,
                                    piercing = false,
                                    maxAge = 1.25f
                                )
                            )
                        }
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.ACTIVE,
                            stretchFactor = 1f,
                            attackFired = fired
                        )
                    }
                    nextTime < totalEnd -> {
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.RETRACT,
                            stretchFactor = 0f
                        )
                    }
                    else -> {
                        luffy.copy(
                            activeAttack = null,
                            attackPhase = AttackPhase.NONE,
                            attackTimer = 0f,
                            attackHitTargets = emptySet(),
                            stretchFactor = 0f,
                            attackFired = false
                        )
                    }
                }
            }

            AttackType.USOPP_EXPLODING_STAR -> {
                val windupEnd = 0.14f
                val activeEnd = 0.26f
                val totalEnd = 0.36f

                when {
                    nextTime < windupEnd -> {
                        if (rng.nextFloat() < 0.6f) {
                            spawnFireSparks(particles, luffy.x + dir * 10f, luffy.y - 16f, count = 1)
                        }
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.WINDUP,
                            stretchFactor = (nextTime / windupEnd).coerceIn(0f, 1f)
                        )
                    }
                    nextTime < activeEnd -> {
                        var fired = luffy.attackFired
                        if (!fired) {
                            fired = true
                            soundEngine.play(RetroSfx.USOPP_SHOT, 0.94f)
                            triggerShake(2.8f, 0.16f)
                            val starX = luffy.x + dir * 18f
                            val starY = luffy.y - 17f
                            val elevatedAhead = enemies.any {
                                it.isAlive && (it.x - starX) * dir > 0f && abs(it.x - starX) <= 320f && it.y < luffy.y - 14f
                            }
                            playerProjectiles.add(
                                PlayerProjectile(
                                    id = nextEntityId++,
                                    kind = PlayerProjectileKind.USOPP_EXPLODING_STAR,
                                    attackType = AttackType.USOPP_EXPLODING_STAR,
                                    x = starX,
                                    y = starY,
                                    vx = dir * 235f,
                                    vy = if (elevatedAhead) -122f else -66f,
                                    gravityScale = 0.28f,
                                    width = 12f,
                                    height = 12f,
                                    damage = AttackType.USOPP_EXPLODING_STAR.baseDamage,
                                    knockbackX = dir * 170f,
                                    knockbackY = -135f,
                                    stunSeconds = 0.65f,
                                    facing = luffy.facing,
                                    piercing = false,
                                    maxAge = 1.65f
                                )
                            )
                        }
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.ACTIVE,
                            stretchFactor = 1f,
                            attackFired = fired
                        )
                    }
                    nextTime < totalEnd -> {
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.RETRACT,
                            stretchFactor = 0f
                        )
                    }
                    else -> {
                        luffy.copy(
                            activeAttack = null,
                            attackPhase = AttackPhase.NONE,
                            attackTimer = 0f,
                            attackHitTargets = emptySet(),
                            stretchFactor = 0f,
                            attackFired = false
                        )
                    }
                }
            }

            AttackType.USOPP_FIREBIRD_STAR -> {
                val windupEnd = 0.22f
                val activeEnd = 0.38f
                val totalEnd = 0.48f

                when {
                    nextTime < windupEnd -> {
                        if (rng.nextFloat() < 0.85f) {
                            spawnFireSparks(particles, luffy.x + dir * 10f, luffy.y - 16f, count = 2)
                        }
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.WINDUP,
                            stretchFactor = (nextTime / windupEnd).coerceIn(0f, 1f)
                        )
                    }
                    nextTime < activeEnd -> {
                        var fired = luffy.attackFired
                        if (!fired) {
                            fired = true
                            soundEngine.play(RetroSfx.USOPP_FIREBIRD, 1.0f)
                            triggerShake(6.2f, 0.34f)
                            triggerFlash(0.14f, Color(0xFFFFCF7A))
                            spawnFireSparks(particles, luffy.x + dir * 20f, luffy.y - 16f, count = 16)
                            // Giant blazing pixel phoenix soaring across the screen & angling cleanly toward elevated foes!
                            val fbX = luffy.x + dir * 24f
                            val fbY = luffy.y - 18f
                            val (fbVx, fbVy) = aimedProjectileVelocity(fbX, fbY, dir, 260f, enemies, maxRange = 420f)
                            playerProjectiles.add(
                                PlayerProjectile(
                                    id = nextEntityId++,
                                    kind = PlayerProjectileKind.USOPP_FIREBIRD_STAR,
                                    attackType = AttackType.USOPP_FIREBIRD_STAR,
                                    x = fbX,
                                    y = fbY,
                                    vx = fbVx,
                                    vy = fbVy,
                                    width = 36f,
                                    height = 28f,
                                    damage = AttackType.USOPP_FIREBIRD_STAR.baseDamage,
                                    knockbackX = dir * 195f,
                                    knockbackY = -140f,
                                    stunSeconds = 0.72f,
                                    facing = luffy.facing,
                                    piercing = true,
                                    maxAge = 2.60f
                                )
                            )
                        }
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.ACTIVE,
                            stretchFactor = 1f,
                            attackFired = fired
                        )
                    }
                    nextTime < totalEnd -> {
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.RETRACT,
                            stretchFactor = 0f
                        )
                    }
                    else -> {
                        luffy.copy(
                            activeAttack = null,
                            attackPhase = AttackPhase.NONE,
                            attackTimer = 0f,
                            attackHitTargets = emptySet(),
                            stretchFactor = 0f,
                            attackFired = false
                        )
                    }
                }
            }

            AttackType.CHOPPER_KUNGFU -> {
                val windupEnd = 0.045f
                val activeEnd = 0.145f
                val totalEnd = 0.21f
                val comboStep = luffy.chopperComboStep % 3
                val isFinisher = comboStep == 2

                when {
                    nextTime < windupEnd -> {
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.WINDUP,
                            stretchFactor = 0f
                        )
                    }
                    nextTime < activeEnd -> {
                        var fired = luffy.attackFired
                        if (!fired) {
                            fired = true
                            slashVfxs.add(
                                SwordSlashVfx(
                                    id = nextEntityId++,
                                    x = luffy.x + dir * 20f,
                                    y = luffy.y - 13f,
                                    facing = luffy.facing,
                                    kind = SlashVfxKind.CHOPPER_KUNGFU_ARC,
                                    maxAge = 0.17f
                                )
                            )
                            spawnWindStreaks(particles, luffy.x + dir * 16f, luffy.y - 13f, dir, count = 3)
                        }
                        val boxX = if (dir > 0f) luffy.x + 2f else luffy.x - 42f
                        activeHitboxes.add(
                            combatSystem.createHitbox(
                                sourceId = luffy.id,
                                attackType = AttackType.CHOPPER_KUNGFU,
                                bounds = Rect2D(boxX, luffy.y - 26f, 40f, 24f),
                                damageOverride = AttackType.CHOPPER_KUNGFU.baseDamage + (if (isFinisher) 6 else 0),
                                knockbackX = dir * (if (isFinisher) 155f else 95f),
                                knockbackY = if (isFinisher) -88f else -28f,
                                stunSeconds = if (isFinisher) 0.34f else 0.22f,
                                hitStopSeconds = if (isFinisher) 0.036f else 0.020f,
                                shakeIntensity = if (isFinisher) 3.4f else 2.0f
                            )
                        )
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.ACTIVE,
                            stretchFactor = 1f,
                            vx = if (luffy.isGrounded && !luffy.attackFired) dir * 110f else luffy.vx,
                            attackFired = fired
                        )
                    }
                    nextTime < totalEnd -> {
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.RETRACT,
                            stretchFactor = 0f
                        )
                    }
                    else -> {
                        luffy.copy(
                            activeAttack = null,
                            attackPhase = AttackPhase.NONE,
                            attackTimer = 0f,
                            attackHitTargets = emptySet(),
                            stretchFactor = 0f,
                            attackFired = false,
                            chopperComboStep = (comboStep + 1) % 3,
                            chopperComboResetTimer = 0.62f
                        )
                    }
                }
            }

            AttackType.CHOPPER_GUARD_HEAL -> {
                val windupEnd = 0.08f
                val activeEnd = 1.98f
                val totalEnd = 2.12f

                when {
                    nextTime < windupEnd -> {
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.WINDUP,
                            stretchFactor = (nextTime / windupEnd).coerceIn(0f, 1f)
                        )
                    }
                    nextTime < activeEnd -> {
                        var healed = luffy.guardHealTriggered
                        var nextHp = luffy.currentHp
                        var nextHeroMap = luffy.heroHp
                        if (!healed) {
                            healed = true
                            val selfHeal = 24
                            val crewHeal = 18
                            nextHp = (luffy.currentHp + selfHeal).coerceAtMost(luffy.maxHp)
                            val updatedMap = luffy.heroHp.toMutableMap().apply {
                                this[PlayableHero.CHOPPER] = nextHp
                                for (hero in PlayableHero.entries) {
                                    if (hero != PlayableHero.CHOPPER) {
                                        val hp = this[hero] ?: 0
                                        if (hp > 0) {
                                            this[hero] = (hp + crewHeal).coerceAtMost(hero.maxHp)
                                        }
                                    }
                                }
                            }
                            nextHeroMap = updatedMap
                            soundEngine.play(RetroSfx.CHOPPER_HEAL, 0.92f)
                            triggerShake(3.2f, 0.22f)
                            spawnDustBurst(particles, luffy.x, luffy.y - 6f, count = 12, isPier = false)
                            spawnHealCrossBurst(particles, luffy.x, luffy.y - 16f, count = 12)
                            floatingTexts.add(
                                FloatingDamageText(
                                    id = nextEntityId++,
                                    x = luffy.x,
                                    y = luffy.y - 38f,
                                    text = "+24HP КОМАНДА!",
                                    color = Color(0xFF4CF57E),
                                    isLarge = true
                                )
                            )
                        } else if (rng.nextFloat() < 0.14f) {
                            spawnHealCrossBurst(particles, luffy.x, luffy.y - 14f, count = 1)
                        }

                        // Initial fluff expansion knocks back nearby enemies around Chopper!
                        if (nextTime < 0.44f) {
                            activeHitboxes.add(
                                combatSystem.createHitbox(
                                    sourceId = luffy.id,
                                    attackType = AttackType.CHOPPER_GUARD_HEAL,
                                    bounds = Rect2D(luffy.x - 42f, luffy.y - 38f, 84f, 40f),
                                    knockbackX = 225f,
                                    knockbackY = -108f,
                                    stunSeconds = 0.42f,
                                    hitStopSeconds = 0.025f,
                                    shakeIntensity = 3.2f
                                )
                            )
                        }

                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.ACTIVE,
                            stretchFactor = 1f,
                            guardHealTriggered = healed,
                            currentHp = nextHp,
                            heroHp = nextHeroMap
                        )
                    }
                    nextTime < totalEnd -> {
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.RETRACT,
                            stretchFactor = ((totalEnd - nextTime) / (totalEnd - activeEnd)).coerceIn(0f, 1f)
                        )
                    }
                    else -> {
                        luffy.copy(
                            activeAttack = null,
                            attackPhase = AttackPhase.NONE,
                            attackTimer = 0f,
                            attackHitTargets = emptySet(),
                            stretchFactor = 0f,
                            guardHealTriggered = false
                        )
                    }
                }
            }

            AttackType.CHOPPER_MONSTER_POINT -> {
                val pillEatEnd = 0.34f
                val roarEnd = 0.62f
                val slamEnd = 0.96f
                val totalEnd = 1.18f

                when {
                    nextTime < pillEatEnd -> {
                        // Brain Point tosses and chomps the glowing yellow Rumble Ball!
                        if (rng.nextFloat() < 0.45f) {
                            particles.add(
                                Particle(
                                    x = luffy.x + (rng.nextFloat() * 14f - 7f),
                                    y = luffy.y - 24f,
                                    vx = rng.nextFloat() * 26f - 13f,
                                    vy = -34f - rng.nextFloat() * 24f,
                                    size = 2.5f,
                                    color = if (rng.nextBoolean()) Color(0xFFFFE14D) else Color(0xFFFFFFFF),
                                    age = 0f,
                                    maxAge = 0.22f,
                                    kind = ParticleKind.IMPACT_SPARK
                                )
                            )
                        }
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.WINDUP,
                            stretchFactor = (nextTime / pillEatEnd).coerceIn(0f, 1f)
                        )
                    }
                    nextTime < roarEnd -> {
                        // Colossal Monster Point transformation & feral roar!
                        var roared = luffy.monsterRoarTriggered
                        if (!roared) {
                            roared = true
                            soundEngine.play(RetroSfx.CHOPPER_MONSTER_ROAR, 1.0f)
                            triggerShake(6.2f, 0.36f)
                            triggerFlash(0.16f, Color(0xFFFFE58A))
                            spawnDustBurst(particles, luffy.x, luffy.y, count = 16, isPier = isOnPier(luffy.x))
                            spawnImpactSparkBurst(particles, luffy.x, luffy.y - 28f, count = 12)
                        }
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.ACTIVE,
                            stretchFactor = 0.5f,
                            monsterRoarTriggered = roared
                        )
                    }
                    nextTime < slamEnd -> {
                        // Massive Monster Point paw smash + huge AoE shockwave!
                        var slammed = luffy.monsterSlamTriggered
                        val impactX = (luffy.x + dir * 28f).coerceIn(WorldConstants.LEFT_BOUNDARY_X, WorldConstants.RIGHT_BOUNDARY_X)
                        val gY = groundYAt(impactX)
                        if (!slammed) {
                            slammed = true
                            soundEngine.play(RetroSfx.CHOPPER_MONSTER_SLAM, 1.0f)
                            triggerShake(11.8f, 0.58f)
                            triggerFlash(0.24f, Color(0xFFFFE8A6))
                            craters.add(GroundCrater(x = impactX, y = gY, age = 0f, maxAge = 5.5f))
                            slashVfxs.add(
                                SwordSlashVfx(
                                    id = nextEntityId++,
                                    x = impactX,
                                    y = gY - 8f,
                                    facing = luffy.facing,
                                    kind = SlashVfxKind.MONSTER_SHOCKWAVE,
                                    maxAge = 0.44f
                                )
                            )
                            spawnAxeImpactEffects(particles, impactX, gY, isPier = isOnPier(impactX))
                            spawnStoneRubbleBurst(particles, impactX, gY - 8f, count = 16)
                        }
                        if (nextTime < roarEnd + 0.18f) {
                            activeHitboxes.add(
                                combatSystem.createHitbox(
                                    sourceId = luffy.id,
                                    attackType = AttackType.CHOPPER_MONSTER_POINT,
                                    bounds = Rect2D(impactX - 74f, gY - 66f, 148f, 70f),
                                    knockbackX = dir * 230f,
                                    knockbackY = -175f,
                                    stunSeconds = 0.95f,
                                    hitStopSeconds = 0.088f,
                                    shakeIntensity = 11.5f
                                )
                            )
                        }
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.ACTIVE,
                            stretchFactor = 1f,
                            monsterSlamTriggered = slammed
                        )
                    }
                    nextTime < totalEnd -> {
                        if (luffy.attackPhase == AttackPhase.ACTIVE) {
                            soundEngine.play(RetroSfx.CHOPPER_GUARD_PUFF, 0.72f)
                            spawnDustBurst(particles, luffy.x, luffy.y - 10f, count = 10, isPier = false)
                        }
                        luffy.copy(
                            attackTimer = nextTime,
                            attackPhase = AttackPhase.RETRACT,
                            stretchFactor = 0f
                        )
                    }
                    else -> {
                        luffy.copy(
                            activeAttack = null,
                            attackPhase = AttackPhase.NONE,
                            attackTimer = 0f,
                            attackHitTargets = emptySet(),
                            stretchFactor = 0f,
                            monsterRoarTriggered = false,
                            monsterSlamTriggered = false
                        )
                    }
                }
            }

            null -> luffy
        }
    }

    private fun aimedProjectileVelocity(
        originX: Float,
        originY: Float,
        dir: Float,
        speed: Float,
        enemies: List<EnemyState>,
        maxRange: Float
    ): Pair<Float, Float> {
        val target = enemies.filter { e ->
            e.isAlive && (e.x - originX) * dir > -8f && abs(e.x - originX) <= maxRange
        }.minByOrNull { e ->
            // Prefer closer targets, especially elevated balcony snipers when aiming toward them
            abs(e.x - originX)
        } ?: return (dir * speed) to 0f

        val targetY = target.y - target.kind.hitboxHeight * 0.55f
        val dy = targetY - originY
        if (abs(dy) < 10f) return (dir * speed) to 0f
        val dx = (target.x - originX).let { if (abs(it) < 18f) dir * 18f else it }
        val clampedDy = dy.coerceIn(-abs(dx) * 1.15f, abs(dx) * 0.85f)
        val dist = hypot(dx.toDouble(), clampedDy.toDouble()).toFloat().coerceAtLeast(1f)
        return ((dx / dist) * speed) to ((clampedDy / dist) * speed)
    }

    private fun spawnElectricSparks(particles: MutableList<Particle>, x: Float, y: Float, count: Int) {
        val elecPalette = listOf(
            Color(0xFFFFFFFF),
            Color(0xFFFFF066),
            Color(0xFF7BE8FF),
            Color(0xFF38BDF8)
        )
        repeat(count) { i ->
            val angle = rng.nextFloat() * (2f * PI.toFloat())
            val speed = 45f + rng.nextFloat() * 110f
            particles.add(
                Particle(
                    x = x + (rng.nextFloat() * 12f - 6f),
                    y = y + (rng.nextFloat() * 10f - 5f),
                    vx = cos(angle) * speed,
                    vy = sin(angle) * speed - 20f,
                    size = 2.2f + rng.nextFloat() * 2.0f,
                    color = elecPalette[i % elecPalette.size],
                    age = 0f,
                    maxAge = 0.16f + rng.nextFloat() * 0.16f,
                    kind = ParticleKind.IMPACT_SPARK,
                    gravityScale = 0.08f
                )
            )
        }
    }

    private fun spawnFireSparks(particles: MutableList<Particle>, x: Float, y: Float, count: Int) {
        val firePalette = listOf(
            Color(0xFFFFFBE6),
            Color(0xFFFFD84D),
            Color(0xFFFF8A1F),
            Color(0xFFE62E2D)
        )
        repeat(count) { i ->
            val angle = rng.nextFloat() * (2f * PI.toFloat())
            val speed = 42f + rng.nextFloat() * 115f
            particles.add(
                Particle(
                    x = x + (rng.nextFloat() * 12f - 6f),
                    y = y + (rng.nextFloat() * 10f - 5f),
                    vx = cos(angle) * speed,
                    vy = sin(angle) * speed - 48f,
                    size = 2.2f + rng.nextFloat() * 2.4f,
                    color = firePalette[i % firePalette.size],
                    age = 0f,
                    maxAge = 0.22f + rng.nextFloat() * 0.24f,
                    kind = if (i % 3 == 0) ParticleKind.IMPACT_SPARK else ParticleKind.DEBRIS,
                    gravityScale = 0.22f
                )
            )
        }
    }

    private fun spawnZoroWindStreaks(particles: MutableList<Particle>, x: Float, y: Float, dir: Float, count: Int) {
        repeat(count) {
            particles.add(
                Particle(
                    x = x + rng.nextFloat() * 14f - 7f,
                    y = y + rng.nextFloat() * 16f - 8f,
                    vx = dir * (150f + rng.nextFloat() * 110f),
                    vy = rng.nextFloat() * 24f - 12f,
                    size = 2f,
                    color = if (rng.nextBoolean()) Color(0xFFFFFFFF) else Color(0xFF4CF57E),
                    age = 0f,
                    maxAge = 0.13f + rng.nextFloat() * 0.06f,
                    kind = ParticleKind.WIND_STREAK,
                    stretchX = 4.2f + rng.nextFloat() * 2.5f
                )
            )
        }
    }

    private fun updateParticles(particles: List<Particle>, dt: Float): List<Particle> {
        val alive = mutableListOf<Particle>()
        for (p in particles) {
            val age = p.age + dt
            if (age < p.maxAge) {
                val pVy = when (p.kind) {
                    ParticleKind.MAP_PAPER -> (p.vy + 45f * dt).coerceAtMost(32f)
                    ParticleKind.BUBBLE, ParticleKind.HEAL_CROSS -> p.vy
                    else -> p.vy + WorldConstants.GRAVITY * p.gravityScale * dt
                }
                val pVx = when (p.kind) {
                    ParticleKind.DUST, ParticleKind.SAND -> p.vx * (1f - 2.6f * dt)
                    ParticleKind.MAP_PAPER -> p.vx * (1f - 1.2f * dt) + sin(age * 9f + p.x * 0.1f) * 34f * dt
                    ParticleKind.HEAL_CROSS -> p.vx * (1f - 1.5f * dt) + sin(age * 11f + p.x * 0.2f) * 18f * dt
                    else -> p.vx
                }
                alive.add(
                    p.copy(
                        x = p.x + pVx * dt,
                        y = p.y + pVy * dt,
                        vx = pVx,
                        vy = pVy,
                        age = age
                    )
                )
            }
        }
        return alive
    }

    private fun spawnWaterSplashBurst(particles: MutableList<Particle>, x: Float, y: Float, count: Int) {
        repeat(count) { i ->
            val spread = (i.toFloat() / (count - 1).coerceAtLeast(1)) * 2f - 1f
            particles.add(
                Particle(
                    x = x + spread * 12f,
                    y = y,
                    vx = spread * (35f + rng.nextFloat() * 58f),
                    vy = -85f - rng.nextFloat() * 110f,
                    size = 2.5f + rng.nextFloat() * 2.5f,
                    color = if (i % 3 == 0) Color(0xFFFFFFFF) else Color(0xFF5CE1FF),
                    age = 0f,
                    maxAge = 0.42f + rng.nextFloat() * 0.25f,
                    kind = ParticleKind.WATER_SPRAY,
                    gravityScale = 0.72f
                )
            )
        }
    }

    private fun spawnStoneRubbleBurst(particles: MutableList<Particle>, x: Float, y: Float, count: Int) {
        val colors = listOf(Color(0xFFD2C9BA), Color(0xFFA39887), Color(0xFF6B6257), Color(0xFFB8783E))
        repeat(count) { i ->
            particles.add(
                Particle(
                    x = x + (rng.nextFloat() * 22f - 11f),
                    y = y + (rng.nextFloat() * 24f - 12f),
                    vx = (rng.nextFloat() * 220f - 80f),
                    vy = -90f - rng.nextFloat() * 135f,
                    size = 3f + rng.nextFloat() * 3.5f,
                    color = colors[i % colors.size],
                    age = 0f,
                    maxAge = 0.55f + rng.nextFloat() * 0.35f,
                    kind = ParticleKind.DEBRIS,
                    gravityScale = 0.85f
                )
            )
        }
    }

    private fun spawnMapPaperBurst(particles: MutableList<Particle>, x: Float, y: Float, count: Int) {
        val paperColors = listOf(Color(0xFFFFF7DC), Color(0xFFFFEAB8), Color(0xFFEBD29B))
        repeat(count) { i ->
            particles.add(
                Particle(
                    x = x + (rng.nextFloat() * 26f - 13f),
                    y = y + (rng.nextFloat() * 18f - 9f),
                    vx = (rng.nextFloat() * 110f - 55f),
                    vy = -48f - rng.nextFloat() * 68f,
                    size = 4.5f + rng.nextFloat() * 2.5f,
                    color = paperColors[i % paperColors.size],
                    age = 0f,
                    maxAge = 1.45f + rng.nextFloat() * 0.85f,
                    kind = ParticleKind.MAP_PAPER
                )
            )
        }
    }

    private fun spawnHealCrossBurst(particles: MutableList<Particle>, x: Float, y: Float, count: Int) {
        val healColors = listOf(Color(0xFF4CF57E), Color(0xFF2EE66B), Color(0xFFA8FFD0))
        repeat(count) { i ->
            particles.add(
                Particle(
                    x = x + (rng.nextFloat() * 34f - 17f),
                    y = y + (rng.nextFloat() * 14f - 7f),
                    vx = (rng.nextFloat() * 20f - 10f),
                    vy = -26f - rng.nextFloat() * 26f,
                    size = 4.5f + rng.nextFloat() * 1.5f,
                    color = healColors[i % healColors.size],
                    age = 0f,
                    maxAge = 0.72f + rng.nextFloat() * 0.35f,
                    kind = ParticleKind.HEAL_CROSS
                )
            )
        }
    }

    private fun updateFloatingTexts(texts: List<FloatingDamageText>, dt: Float): List<FloatingDamageText> {
        return texts.mapNotNull { ft ->
            val age = ft.age + dt
            if (age < ft.maxAge) {
                ft.copy(y = ft.y + ft.vy * dt, vy = ft.vy * (1f - 2.2f * dt), age = age)
            } else {
                null
            }
        }
    }

    private fun updateClouds(clouds: List<CloudEntity>, dt: Float): List<CloudEntity> {
        return clouds.map { cloud ->
            var cx = cloud.x + cloud.speed * dt
            if (cx > WorldConstants.WORLD_WIDTH + 60f) cx = -50f
            cloud.copy(x = cx)
        }
    }

    private fun updateSeagulls(seagulls: List<SeagullEntity>, dt: Float): List<SeagullEntity> {
        return seagulls.map { gull ->
            var gx = gull.x + gull.speed * dt
            if (gx > WorldConstants.WORLD_WIDTH + 40f) gx = -30f
            if (gx < -40f) gx = WorldConstants.WORLD_WIDTH + 30f
            gull.copy(x = gx, wingPhase = gull.wingPhase + dt * 7.5f)
        }
    }

    private data class ShakeTick(
        val offsetX: Float,
        val offsetY: Float,
        val intensity: Float,
        val duration: Float,
        val elapsed: Float
    )

    private fun updateShake(
        intensity: Float,
        duration: Float,
        elapsed: Float,
        dt: Float
    ): ShakeTick {
        if (elapsed < duration && duration > 0f) {
            val nextElapsed = elapsed + dt
            val decay = (1f - (nextElapsed / duration)).coerceIn(0f, 1f)
            val mag = intensity * decay
            return ShakeTick(
                offsetX = (rng.nextFloat() * 2f - 1f) * mag,
                offsetY = (rng.nextFloat() * 2f - 1f) * mag,
                intensity = intensity,
                duration = duration,
                elapsed = nextElapsed
            )
        }
        return ShakeTick(0f, 0f, 0f, 0f, elapsed)
    }

    private fun spawnFootstepPuff(particles: MutableList<Particle>, x: Float, y: Float, isPier: Boolean) {
        val color = if (isPier) Color(0xFFD9B68C) else Color(0xFFF7DC94)
        particles.add(
            Particle(
                x = x,
                y = y - 2f,
                vx = (rng.nextFloat() * 18f - 9f),
                vy = -12f - rng.nextFloat() * 10f,
                size = 2.5f,
                color = color,
                age = 0f,
                maxAge = 0.24f,
                kind = ParticleKind.DUST
            )
        )
    }

    private fun spawnDustBurst(particles: MutableList<Particle>, x: Float, y: Float, count: Int, isPier: Boolean) {
        val palette = if (isPier) {
            listOf(Color(0xFFE5CFAF), Color(0xFFC49B6C), Color(0xFFFFF4E0))
        } else {
            listOf(Color(0xFFFFEAB3), Color(0xFFF0C76E), Color(0xFFFFF8E3))
        }
        repeat(count) { i ->
            val spread = (i.toFloat() / (count - 1).coerceAtLeast(1)) * 2f - 1f
            particles.add(
                Particle(
                    x = x + spread * 5f,
                    y = y - 2f,
                    vx = spread * (28f + rng.nextFloat() * 24f),
                    vy = -14f - rng.nextFloat() * 22f,
                    size = 2.5f + rng.nextFloat() * 2f,
                    color = palette[i % palette.size],
                    age = 0f,
                    maxAge = 0.26f + rng.nextFloat() * 0.14f,
                    kind = ParticleKind.DUST
                )
            )
        }
    }

    private fun spawnWindStreaks(particles: MutableList<Particle>, x: Float, y: Float, dir: Float, count: Int) {
        repeat(count) {
            particles.add(
                Particle(
                    x = x + rng.nextFloat() * 16f - 8f,
                    y = y + rng.nextFloat() * 14f - 7f,
                    vx = dir * (160f + rng.nextFloat() * 120f),
                    vy = rng.nextFloat() * 18f - 9f,
                    size = 2f,
                    color = if (rng.nextBoolean()) Color(0xFFFFFFFF) else Color(0xFF7CE8FF),
                    age = 0f,
                    maxAge = 0.11f + rng.nextFloat() * 0.06f,
                    kind = ParticleKind.WIND_STREAK,
                    stretchX = 4.5f + rng.nextFloat() * 3f
                )
            )
        }
    }

    private fun spawnImpactSparkBurst(particles: MutableList<Particle>, x: Float, y: Float, count: Int) {
        repeat(count) { i ->
            val angle = (i.toFloat() / count) * (2f * PI.toFloat()) + rng.nextFloat() * 0.4f
            val speed = 45f + rng.nextFloat() * 55f
            particles.add(
                Particle(
                    x = x,
                    y = y,
                    vx = cos(angle) * speed,
                    vy = sin(angle) * speed,
                    size = 2.5f,
                    color = if (i % 2 == 0) Color(0xFFFFF59D) else Color(0xFFFFFFFF),
                    age = 0f,
                    maxAge = 0.16f,
                    kind = ParticleKind.IMPACT_SPARK
                )
            )
        }
    }

    private fun spawnAxeImpactEffects(particles: MutableList<Particle>, x: Float, y: Float, isPier: Boolean) {
        for (dir in listOf(-1f, 1f)) {
            particles.add(
                Particle(
                    x = x + dir * 6f,
                    y = y - 6f,
                    vx = dir * 135f,
                    vy = 0f,
                    size = 10f,
                    color = Color(0xFFFFF6CC),
                    age = 0f,
                    maxAge = 0.34f,
                    kind = ParticleKind.SHOCKWAVE_RING
                )
            )
            particles.add(
                Particle(
                    x = x + dir * 4f,
                    y = y - 4f,
                    vx = dir * 90f,
                    vy = 0f,
                    size = 7f,
                    color = Color(0xFF7CE8FF),
                    age = 0f,
                    maxAge = 0.28f,
                    kind = ParticleKind.SHOCKWAVE_RING
                )
            )
        }

        val cloudColors = if (isPier) {
            listOf(Color(0xFFFFF2DB), Color(0xFFD9B68C), Color(0xFF9C7248))
        } else {
            listOf(Color(0xFFFFF7D6), Color(0xFFF5D076), Color(0xFFD4A04A))
        }
        repeat(20) { i ->
            val side = if (i % 2 == 0) 1f else -1f
            particles.add(
                Particle(
                    x = x + (rng.nextFloat() * 22f - 11f),
                    y = y - 3f - rng.nextFloat() * 5f,
                    vx = side * (22f + rng.nextFloat() * 96f),
                    vy = -25f - rng.nextFloat() * 68f,
                    size = 4f + rng.nextFloat() * 5f,
                    color = cloudColors[i % cloudColors.size],
                    age = 0f,
                    maxAge = 0.42f + rng.nextFloat() * 0.30f,
                    kind = ParticleKind.DUST
                )
            )
        }

        val debrisColor = if (isPier) Color(0xFF7A4B1E) else Color(0xFFB8843B)
        repeat(10) {
            particles.add(
                Particle(
                    x = x + (rng.nextFloat() * 14f - 7f),
                    y = y - 4f,
                    vx = (rng.nextFloat() * 160f - 80f),
                    vy = -125f - rng.nextFloat() * 95f,
                    size = 2.5f + rng.nextFloat() * 1.5f,
                    color = debrisColor,
                    age = 0f,
                    maxAge = 0.55f + rng.nextFloat() * 0.2f,
                    kind = ParticleKind.DEBRIS,
                    gravityScale = 0.9f
                )
            )
        }

        if (isPier) {
            repeat(8) {
                particles.add(
                    Particle(
                        x = x + (rng.nextFloat() * 34f - 17f),
                        y = y + 8f,
                        vx = (rng.nextFloat() * 50f - 25f),
                        vy = -90f - rng.nextFloat() * 60f,
                        size = 3f,
                        color = Color(0xFFA6F2FF),
                        age = 0f,
                        maxAge = 0.45f,
                        kind = ParticleKind.WATER_SPRAY,
                        gravityScale = 0.7f
                    )
                )
            }
        }
    }

    private fun approach(value: Float, target: Float, delta: Float): Float {
        return when {
            value < target -> (value + delta).coerceAtMost(target)
            value > target -> (value - delta).coerceAtLeast(target)
            else -> target
        }
    }
}
