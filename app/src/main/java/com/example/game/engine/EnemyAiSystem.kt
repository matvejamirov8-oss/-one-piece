package com.example.game.engine

import androidx.compose.ui.graphics.Color
import com.example.game.audio.RetroSfx
import com.example.game.model.BossAttackVariant
import com.example.game.model.BuggyPartEntity
import com.example.game.model.BuggyPartKind
import com.example.game.model.EastBlueAiAction
import com.example.game.model.EastBlueEnemyAi
import com.example.game.model.EnemyBehaviorState
import com.example.game.model.EnemyKind
import com.example.game.model.EnemyState
import com.example.game.model.FacingDirection
import com.example.game.model.LuffyState
import com.example.game.model.Particle
import com.example.game.model.ParticleKind
import com.example.game.model.Projectile
import com.example.game.model.ProjectileKind
import com.example.game.model.Rect2D
import com.example.game.model.WorldConstants
import com.example.game.model.SlashVfxKind
import com.example.game.model.SwordSlashVfx
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.random.Random

data class EnemyMeleeHit(
    val damage: Int,
    val knockbackDir: Float,
    val shakeIntensity: Float,
    val hitStopSeconds: Float,
    val knockbackSpeed: Float = 112f
)

data class EnemyStepCallbacks(
    val groundYAt: (Float) -> Float,
    val playSfx: (RetroSfx, Float) -> Unit,
    val triggerShake: (Float, Float) -> Unit,
    val spawnProjectile: (Projectile) -> Unit,
    val spawnBuggyParts: (List<BuggyPartEntity>) -> Unit,
    val nextProjectileId: () -> Long,
    val addParticle: (Particle) -> Unit,
    val onPlayerHit: (EnemyMeleeHit) -> Unit,
    val isArlongMapRoom: Boolean = false,
    val isLevel3: Boolean = false,
    val onlyChewsLeft: Boolean = false,
    val isFinisherSlowMo: Boolean = false,
    val addSlashVfx: (SwordSlashVfx) -> Unit = {},
    val onMapRoomShock: (Float, Float) -> Unit = { _, _ -> }
)

/**
 * AI & physics controller for Level 1 & Level 2 enemies:
 * - Level 1: Marine Soldier, Marine Rifleman, Pirate Bandit, Boss Captain Morgan
 * - Level 2: Buggy Acrobat, Knife Thrower, Elite Cabaji (unicycle), Boss Captain Buggy
 */
class EnemyAiSystem(private val rng: Random) {

    private val marineSoldierAi = EastBlueEnemyAi.forKind(EnemyKind.MARINE_SOLDIER)
    private val marineRiflemanAi = EastBlueEnemyAi.forKind(EnemyKind.MARINE_RIFLEMAN)
    private val pirateBanditAi = EastBlueEnemyAi.forKind(EnemyKind.PIRATE_BANDIT)

    fun updateEnemy(
        enemy: EnemyState,
        luffy: LuffyState,
        dt: Float,
        cb: EnemyStepCallbacks
    ): EnemyState {
        var e = enemy.copy(
            flashTimer = (enemy.flashTimer - dt).coerceAtLeast(0f),
            attackCooldown = (enemy.attackCooldown - dt).coerceAtLeast(0f),
            animTime = enemy.animTime + dt
        )

        // Freeze Arlong cleanly stunned on the Map Room floor during Slow-Mo Finisher so Battle Axe never misses
        if (cb.isFinisherSlowMo && e.kind == EnemyKind.BOSS_ARLONG) {
            return e.copy(
                x = e.x.coerceIn(
                    WorldConstants.ARLONG_MAP_ROOM_MIN_X + 20f,
                    WorldConstants.ARLONG_MAP_ROOM_MAX_X - 20f
                ),
                y = WorldConstants.BEACH_GROUND_Y,
                vx = 0f,
                vy = 0f,
                isGrounded = true,
                behavior = EnemyBehaviorState.HURT,
                stunTimer = maxOf(0.5f, e.stunTimer),
                attackHitApplied = false
            )
        }

        val dxToLuffy = luffy.x - e.x
        val absDx = abs(dxToLuffy)
        val dirToLuffy = if (dxToLuffy >= 0f) FacingDirection.RIGHT else FacingDirection.LEFT

        // 1. Stunned / Hurt recovery state
        if (e.stunTimer > 0f) {
            val nextStun = (e.stunTimer - dt).coerceAtLeast(0f)
            val friction = if (e.isGrounded) 540f else 130f
            val nextVx = approach(e.vx, 0f, friction * dt)
            e = e.copy(
                vx = nextVx,
                stunTimer = nextStun,
                behavior = if (nextStun > 0f) {
                    if (e.behavior == EnemyBehaviorState.STUNNED) EnemyBehaviorState.STUNNED else EnemyBehaviorState.HURT
                } else {
                    EnemyBehaviorState.CHASE
                },
                stateTimer = if (nextStun <= 0f) 0f else e.stateTimer + dt,
                attackHitApplied = false
            )
            return integratePhysics(e, dt, cb)
        }

        // 2. Kind-specific combat AI state machines
        e = when (e.kind) {
            EnemyKind.MARINE_SOLDIER -> runEastBlueAi(marineSoldierAi, e, luffy, dt, cb)
            EnemyKind.MARINE_RIFLEMAN -> runEastBlueAi(marineRiflemanAi, e, luffy, dt, cb)
            EnemyKind.PIRATE_BANDIT -> runEastBlueAi(pirateBanditAi, e, luffy, dt, cb)
            EnemyKind.BOSS_MORGAN -> updateBossMorgan(e, luffy, absDx, dirToLuffy, dt, cb)
            EnemyKind.BUGGY_ACROBAT -> updateBuggyAcrobat(e, luffy, absDx, dirToLuffy, dt, cb)
            EnemyKind.KNIFE_THROWER -> updateKnifeThrower(e, luffy, absDx, dirToLuffy, dt, cb)
            EnemyKind.CABAJI -> updateCabaji(e, luffy, absDx, dirToLuffy, dt, cb)
            EnemyKind.BOSS_BUGGY -> updateBossBuggy(e, luffy, absDx, dirToLuffy, dt, cb)
            EnemyKind.FISHMAN_BOUNCER -> updateFishmanBouncer(e, luffy, absDx, dirToLuffy, dt, cb)
            EnemyKind.FISHMAN_CHEW -> updateFishmanChew(e, luffy, absDx, dirToLuffy, dt, cb)
            EnemyKind.HATCHAN -> updateHatchan(e, luffy, absDx, dirToLuffy, dt, cb)
            EnemyKind.BOSS_ARLONG -> updateBossArlong(e, luffy, absDx, dirToLuffy, dt, cb)
        }

        return integratePhysics(e, dt, cb)
    }

    private fun runEastBlueAi(
        ai: EastBlueEnemyAi,
        e: EnemyState,
        luffy: LuffyState,
        dt: Float,
        cb: EnemyStepCallbacks
    ): EnemyState {
        val result = ai.step(e, luffy, dt)
        for (action in result.actions) {
            when (action) {
                EastBlueAiAction.PlaySwingSfx -> {
                    cb.playSfx(RetroSfx.PUNCH_WHOOSH, 0.55f)
                }
                is EastBlueAiAction.MeleeHit -> {
                    cb.onPlayerHit(
                        EnemyMeleeHit(
                            damage = action.damage,
                            knockbackDir = action.knockbackDir,
                            shakeIntensity = action.shakeIntensity,
                            hitStopSeconds = action.hitStopSeconds
                        )
                    )
                }
                is EastBlueAiAction.FireRifle -> {
                    val dir = action.facing.sign.toFloat()
                    cb.playSfx(RetroSfx.RIFLE_SHOT, 0.78f)
                    cb.spawnProjectile(
                        Projectile(
                            id = cb.nextProjectileId(),
                            kind = ProjectileKind.RIFLE_BULLET,
                            x = action.muzzleX,
                            y = action.muzzleY,
                            vx = dir * 158f,
                            width = 8f,
                            height = 4f,
                            damage = action.damage,
                            facing = action.facing,
                            maxAge = 2.6f
                        )
                    )
                    repeat(4) {
                        cb.addParticle(
                            Particle(
                                x = action.muzzleX,
                                y = action.muzzleY,
                                vx = dir * (40f + rng.nextFloat() * 55f),
                                vy = rng.nextFloat() * 34f - 17f,
                                size = 2.5f,
                                color = if (rng.nextBoolean()) Color(0xFFFFF59D) else Color(0xFFFF8C24),
                                age = 0f,
                                maxAge = 0.14f,
                                kind = ParticleKind.IMPACT_SPARK
                            )
                        )
                    }
                }
            }
        }
        return result.enemy
    }

    private fun updateBossMorgan(
        e: EnemyState,
        luffy: LuffyState,
        absDx: Float,
        dirToLuffy: FacingDirection,
        dt: Float,
        cb: EnemyStepCallbacks
    ): EnemyState {
        val cleaveRange = 46f
        val waveMinRange = 76f
        val waveMaxRange = 230f
        val t = e.stateTimer + dt

        return when (e.behavior) {
            EnemyBehaviorState.PATROL, EnemyBehaviorState.CHASE -> {
                if (e.attackCooldown <= 0f && e.isGrounded) {
                    when {
                        absDx <= cleaveRange -> {
                            return e.copy(
                                vx = 0f,
                                facing = dirToLuffy,
                                behavior = EnemyBehaviorState.WINDUP,
                                bossAttack = BossAttackVariant.CLEAVE,
                                stateTimer = 0f,
                                attackHitApplied = false
                            )
                        }
                        absDx in waveMinRange..waveMaxRange -> {
                            return e.copy(
                                vx = 0f,
                                facing = dirToLuffy,
                                behavior = EnemyBehaviorState.WINDUP,
                                bossAttack = BossAttackVariant.GROUND_WAVE,
                                stateTimer = 0f,
                                attackHitApplied = false
                            )
                        }
                    }
                }
                val speed = if (e.currentHp < e.maxHp * 0.45f) e.kind.moveSpeed * 1.18f else e.kind.moveSpeed
                e.copy(
                    behavior = EnemyBehaviorState.CHASE,
                    vx = approach(e.vx, dirToLuffy.sign * speed, 340f * dt),
                    facing = dirToLuffy,
                    stateTimer = t
                )
            }
            EnemyBehaviorState.WINDUP -> {
                // Clear, dramatic telegraph overhead axe lift!
                val windupDur = if (e.bossAttack == BossAttackVariant.CLEAVE) 0.56f else 0.60f
                if (t >= windupDur) {
                    val dir = e.facing.sign.toFloat()
                    if (e.bossAttack == BossAttackVariant.CLEAVE) {
                        cb.playSfx(RetroSfx.MORGAN_SWING, 0.95f)
                        cb.triggerShake(4.8f, 0.24f)
                        e.copy(
                            behavior = EnemyBehaviorState.ATTACK,
                            stateTimer = 0f,
                            vx = dir * 82f,
                            attackHitApplied = false
                        )
                    } else {
                        // Ground-slam shockwave!
                        cb.playSfx(RetroSfx.AXE_SLAM, 0.95f)
                        cb.triggerShake(6.2f, 0.34f)
                        val impactX = (e.x + dir * 24f).coerceIn(
                            WorldConstants.LEFT_BOUNDARY_X,
                            WorldConstants.RIGHT_BOUNDARY_X
                        )
                        val impactGroundY = cb.groundYAt(impactX)
                        cb.spawnProjectile(
                            Projectile(
                                id = cb.nextProjectileId(),
                                kind = ProjectileKind.MORGAN_SHOCKWAVE,
                                x = impactX,
                                y = impactGroundY - 7f,
                                vx = dir * 138f,
                                width = 16f,
                                height = 14f,
                                damage = 18,
                                facing = e.facing,
                                maxAge = 2.8f
                            )
                        )
                        spawnMorganSlamDust(impactX, impactGroundY, cb.addParticle)
                        e.copy(
                            behavior = EnemyBehaviorState.ATTACK,
                            stateTimer = 0f,
                            vx = 0f,
                            attackHitApplied = false
                        )
                    }
                } else {
                    e.copy(vx = approach(e.vx, 0f, 480f * dt), stateTimer = t)
                }
            }
            EnemyBehaviorState.ATTACK -> {
                val attackDur = if (e.bossAttack == BossAttackVariant.CLEAVE) 0.26f else 0.30f
                var hitApplied = e.attackHitApplied
                if (!hitApplied && t >= 0.03f && t <= 0.16f) {
                    val cleaveReach = if (e.bossAttack == BossAttackVariant.CLEAVE) 42f else 28f
                    val cleaveBox = meleeBoxInFront(e, reach = cleaveReach, height = 34f)
                    if (cleaveBox.intersects(luffy.hurtbox)) {
                        hitApplied = true
                        cb.onPlayerHit(
                            EnemyMeleeHit(
                                damage = 22,
                                knockbackDir = e.facing.sign.toFloat(),
                                shakeIntensity = 6.5f,
                                hitStopSeconds = 0.065f
                            )
                        )
                    }
                }
                if (t >= attackDur) {
                    val enraged = e.currentHp < e.maxHp * 0.45f
                    e.copy(
                        behavior = EnemyBehaviorState.RECOVER,
                        stateTimer = 0f,
                        attackCooldown = if (enraged) 0.95f else 1.25f,
                        attackHitApplied = hitApplied
                    )
                } else {
                    e.copy(vx = approach(e.vx, 0f, 360f * dt), stateTimer = t, attackHitApplied = hitApplied)
                }
            }
            EnemyBehaviorState.RECOVER -> {
                if (t >= 0.44f) {
                    e.copy(behavior = EnemyBehaviorState.CHASE, stateTimer = 0f)
                } else {
                    e.copy(vx = approach(e.vx, 0f, 460f * dt), stateTimer = t)
                }
            }
            else -> e.copy(behavior = EnemyBehaviorState.CHASE, stateTimer = 0f)
        }
    }

    // === LEVEL 2 ENEMIES (BUGGY PIRATES) ===

    private fun updateBuggyAcrobat(
        e: EnemyState,
        luffy: LuffyState,
        absDx: Float,
        dirToLuffy: FacingDirection,
        dt: Float,
        cb: EnemyStepCallbacks
    ): EnemyState {
        val t = e.stateTimer + dt
        return when (e.behavior) {
            EnemyBehaviorState.PATROL, EnemyBehaviorState.CHASE -> {
                if (absDx in 48f..125f && e.attackCooldown <= 0f && e.isGrounded) {
                    // Crouch briefly before high acrobatic leap!
                    e.copy(
                        vx = 0f,
                        facing = dirToLuffy,
                        behavior = EnemyBehaviorState.WINDUP,
                        stateTimer = 0f,
                        attackHitApplied = false
                    )
                } else if (absDx <= 26f && e.attackCooldown <= 0f && e.isGrounded) {
                    // Quick close-range leap away & strike
                    e.copy(
                        vx = 0f,
                        facing = dirToLuffy,
                        behavior = EnemyBehaviorState.WINDUP,
                        stateTimer = 0f,
                        attackHitApplied = false
                    )
                } else {
                    e.copy(
                        behavior = EnemyBehaviorState.CHASE,
                        vx = approach(e.vx, dirToLuffy.sign * e.kind.moveSpeed, 640f * dt),
                        facing = dirToLuffy,
                        stateTimer = t
                    )
                }
            }
            EnemyBehaviorState.WINDUP -> {
                if (t >= 0.22f) {
                    cb.playSfx(RetroSfx.JUMP, 0.72f)
                    val jumpVx = dirToLuffy.sign * (absDx.coerceIn(55f, 130f) * 1.15f)
                    e.copy(
                        behavior = EnemyBehaviorState.ATTACK,
                        stateTimer = 0f,
                        facing = dirToLuffy,
                        vx = jumpVx,
                        vy = -255f,
                        isGrounded = false,
                        comboStep = 0,
                        attackHitApplied = false
                    )
                } else {
                    e.copy(vx = approach(e.vx, 0f, 560f * dt), facing = dirToLuffy, stateTimer = t)
                }
            }
            EnemyBehaviorState.ATTACK -> {
                var nextVx = e.vx
                var nextVy = e.vy
                var diveStarted = e.comboStep
                // At apex of high jump, snap sharply downward toward player!
                if (diveStarted == 0 && e.vy >= -12f && t > 0.16f) {
                    diveStarted = 1
                    cb.playSfx(RetroSfx.PUNCH_WHOOSH, 0.72f)
                    nextVx = dirToLuffy.sign * 155f
                    nextVy = 230f
                }
                var hitApplied = e.attackHitApplied
                if (!hitApplied && (diveStarted == 1 || t > 0.20f)) {
                    val diveBox = Rect2D(
                        x = e.x - 13f,
                        y = e.y - 24f,
                        width = 26f,
                        height = 24f
                    )
                    if (diveBox.intersects(luffy.hurtbox)) {
                        hitApplied = true
                        cb.onPlayerHit(
                            EnemyMeleeHit(
                                damage = 12,
                                knockbackDir = dirToLuffy.sign.toFloat(),
                                shakeIntensity = 4.6f,
                                hitStopSeconds = 0.045f
                            )
                        )
                    }
                }
                if (e.isGrounded && t > 0.18f) {
                    cb.playSfx(RetroSfx.LAND, 0.55f)
                    e.copy(
                        behavior = EnemyBehaviorState.RECOVER,
                        stateTimer = 0f,
                        vx = nextVx * 0.3f,
                        comboStep = 0,
                        attackCooldown = 0.95f,
                        attackHitApplied = hitApplied
                    )
                } else {
                    e.copy(
                        vx = nextVx,
                        vy = nextVy,
                        comboStep = diveStarted,
                        facing = if (diveStarted == 0) dirToLuffy else e.facing,
                        stateTimer = t,
                        attackHitApplied = hitApplied
                    )
                }
            }
            EnemyBehaviorState.RECOVER -> {
                if (t >= 0.30f) {
                    e.copy(behavior = EnemyBehaviorState.CHASE, stateTimer = 0f)
                } else {
                    e.copy(vx = approach(e.vx, 0f, 520f * dt), stateTimer = t)
                }
            }
            else -> e.copy(behavior = EnemyBehaviorState.CHASE, stateTimer = 0f)
        }
    }

    private fun updateKnifeThrower(
        e: EnemyState,
        luffy: LuffyState,
        absDx: Float,
        dirToLuffy: FacingDirection,
        dt: Float,
        cb: EnemyStepCallbacks
    ): EnemyState {
        val preferredMin = 84f
        val preferredMax = 172f
        val t = e.stateTimer + dt

        return when (e.behavior) {
            EnemyBehaviorState.PATROL, EnemyBehaviorState.CHASE -> {
                val targetVx = when {
                    absDx < preferredMin -> -dirToLuffy.sign * e.kind.moveSpeed * 0.95f
                    absDx > preferredMax -> dirToLuffy.sign * e.kind.moveSpeed
                    else -> 0f
                }
                if (absDx in 58f..210f && e.attackCooldown <= 0f && e.isGrounded) {
                    e.copy(
                        vx = 0f,
                        facing = dirToLuffy,
                        behavior = EnemyBehaviorState.WINDUP,
                        stateTimer = 0f,
                        attackHitApplied = false
                    )
                } else {
                    e.copy(
                        behavior = EnemyBehaviorState.CHASE,
                        vx = approach(e.vx, targetVx, 440f * dt),
                        facing = dirToLuffy,
                        stateTimer = t
                    )
                }
            }
            EnemyBehaviorState.WINDUP -> {
                if (t >= 0.38f) {
                    val dir = e.facing.sign.toFloat()
                    val handX = e.x + dir * 13f
                    val handY = e.y - 15f
                    cb.playSfx(RetroSfx.KNIFE_THROW, 0.80f)
                    cb.spawnProjectile(
                        Projectile(
                            id = cb.nextProjectileId(),
                            kind = ProjectileKind.THROWING_KNIFE,
                            x = handX,
                            y = handY,
                            vx = dir * 192f,
                            width = 11f,
                            height = 4f,
                            damage = 11,
                            facing = e.facing,
                            maxAge = 2.4f
                        )
                    )
                    e.copy(
                        behavior = EnemyBehaviorState.ATTACK,
                        stateTimer = 0f,
                        vx = -dir * 18f,
                        attackHitApplied = true
                    )
                } else {
                    e.copy(vx = approach(e.vx, 0f, 520f * dt), facing = dirToLuffy, stateTimer = t)
                }
            }
            EnemyBehaviorState.ATTACK -> {
                if (t >= 0.16f) {
                    e.copy(
                        behavior = EnemyBehaviorState.RECOVER,
                        stateTimer = 0f,
                        attackCooldown = 1.25f
                    )
                } else {
                    e.copy(vx = approach(e.vx, 0f, 400f * dt), stateTimer = t)
                }
            }
            EnemyBehaviorState.RECOVER -> {
                if (t >= 0.28f) {
                    e.copy(behavior = EnemyBehaviorState.CHASE, stateTimer = 0f)
                } else {
                    e.copy(vx = approach(e.vx, 0f, 460f * dt), stateTimer = t)
                }
            }
            else -> e.copy(behavior = EnemyBehaviorState.CHASE, stateTimer = 0f)
        }
    }

    private fun updateCabaji(
        e: EnemyState,
        luffy: LuffyState,
        absDx: Float,
        dirToLuffy: FacingDirection,
        dt: Float,
        cb: EnemyStepCallbacks
    ): EnemyState {
        val t = e.stateTimer + dt
        return when (e.behavior) {
            EnemyBehaviorState.PATROL, EnemyBehaviorState.CHASE -> {
                if (e.attackCooldown <= 0f && e.isGrounded && absDx <= 155f) {
                    // Alternate between fiery breath at mid/close range and lightning unicycle sword dash!
                    val useFire = (absDx in 38f..112f) && (e.comboStep % 2 == 1)
                    e.copy(
                        vx = 0f,
                        facing = dirToLuffy,
                        behavior = EnemyBehaviorState.WINDUP,
                        bossAttack = if (useFire) BossAttackVariant.CABAJI_FIRE else BossAttackVariant.CABAJI_DASH,
                        comboStep = e.comboStep + 1,
                        stateTimer = 0f,
                        attackHitApplied = false
                    )
                } else {
                    e.copy(
                        behavior = EnemyBehaviorState.CHASE,
                        vx = approach(e.vx, dirToLuffy.sign * e.kind.moveSpeed, 580f * dt),
                        facing = dirToLuffy,
                        stateTimer = t
                    )
                }
            }
            EnemyBehaviorState.WINDUP -> {
                val windupDur = if (e.bossAttack == BossAttackVariant.CABAJI_FIRE) 0.42f else 0.36f
                if (t >= windupDur) {
                    val dir = e.facing.sign.toFloat()
                    if (e.bossAttack == BossAttackVariant.CABAJI_FIRE) {
                        cb.playSfx(RetroSfx.CABAJI_FIRE, 0.88f)
                        repeat(3) { i ->
                            cb.spawnProjectile(
                                Projectile(
                                    id = cb.nextProjectileId(),
                                    kind = ProjectileKind.CABAJI_FLAME,
                                    x = e.x + dir * (14f + i * 7f),
                                    y = e.y - 20f + (i - 1) * 3.5f,
                                    vx = dir * (138f + i * 24f),
                                    vy = (i - 1) * 14f,
                                    width = 11f,
                                    height = 8f,
                                    damage = 12,
                                    facing = e.facing,
                                    maxAge = 0.65f + i * 0.08f
                                )
                            )
                        }
                        e.copy(
                            behavior = EnemyBehaviorState.ATTACK,
                            stateTimer = 0f,
                            vx = -dir * 20f,
                            attackHitApplied = true
                        )
                    } else {
                        cb.playSfx(RetroSfx.ZORO_SLASH, 0.84f)
                        e.copy(
                            behavior = EnemyBehaviorState.ATTACK,
                            stateTimer = 0f,
                            vx = dir * 245f,
                            attackHitApplied = false
                        )
                    }
                } else {
                    e.copy(vx = approach(e.vx, 0f, 520f * dt), facing = dirToLuffy, stateTimer = t)
                }
            }
            EnemyBehaviorState.ATTACK -> {
                val attackDur = if (e.bossAttack == BossAttackVariant.CABAJI_FIRE) 0.34f else 0.32f
                var hitApplied = e.attackHitApplied
                if (e.bossAttack == BossAttackVariant.CABAJI_DASH) {
                    if (rng.nextFloat() < 0.6f) {
                        cb.addParticle(
                            Particle(
                                x = e.x - e.facing.sign * 8f,
                                y = e.y - 4f,
                                vx = -e.facing.sign * 35f,
                                vy = -12f - rng.nextFloat() * 14f,
                                size = 2.5f,
                                color = Color(0xFFFFD84D),
                                age = 0f,
                                maxAge = 0.20f,
                                kind = ParticleKind.DUST
                            )
                        )
                    }
                    if (!hitApplied) {
                        val dashBox = meleeBoxInFront(e, reach = 28f, height = 28f)
                        if (dashBox.intersects(luffy.hurtbox)) {
                            hitApplied = true
                            cb.onPlayerHit(
                                EnemyMeleeHit(
                                    damage = 15,
                                    knockbackDir = e.facing.sign.toFloat(),
                                    shakeIntensity = 5.2f,
                                    hitStopSeconds = 0.05f
                                )
                            )
                        }
                    }
                }
                if (t >= attackDur) {
                    e.copy(
                        behavior = EnemyBehaviorState.RECOVER,
                        stateTimer = 0f,
                        attackCooldown = 0.92f,
                        attackHitApplied = hitApplied
                    )
                } else {
                    val drag = if (e.bossAttack == BossAttackVariant.CABAJI_DASH) 220f else 420f
                    e.copy(vx = approach(e.vx, 0f, drag * dt), stateTimer = t, attackHitApplied = hitApplied)
                }
            }
            EnemyBehaviorState.RECOVER -> {
                if (t >= 0.34f) {
                    e.copy(behavior = EnemyBehaviorState.CHASE, stateTimer = 0f)
                } else {
                    e.copy(vx = approach(e.vx, 0f, 520f * dt), stateTimer = t)
                }
            }
            else -> e.copy(behavior = EnemyBehaviorState.CHASE, stateTimer = 0f)
        }
    }

    // === LEVEL 2 BOSS — CAPTAIN BUGGY THE CLOWN (BARA-BARA NO MI) ===

    private fun updateBossBuggy(
        e: EnemyState,
        luffy: LuffyState,
        absDx: Float,
        dirToLuffy: FacingDirection,
        dt: Float,
        cb: EnemyStepCallbacks
    ): EnemyState {
        val t = e.stateTimer + dt
        val enraged = e.currentHp < e.maxHp * 0.45f

        return when (e.behavior) {
            EnemyBehaviorState.PATROL, EnemyBehaviorState.CHASE -> {
                if (e.attackCooldown <= 0f && e.isGrounded) {
                    // Cycle smoothly through Buggy's 3 signature attacks based on comboStep & range
                    val chosen = when (e.comboStep % 3) {
                        0 -> if (absDx <= 165f) BossAttackVariant.BUGGY_FESTIVAL else BossAttackVariant.BUGGY_CANNON
                        1 -> BossAttackVariant.BUGGY_CANNON
                        else -> BossAttackVariant.BUGGY_BALL
                    }
                    return e.copy(
                        vx = if (chosen == BossAttackVariant.BUGGY_BALL) -dirToLuffy.sign * 115f else 0f,
                        vy = if (chosen == BossAttackVariant.BUGGY_BALL) -115f else 0f,
                        isGrounded = chosen != BossAttackVariant.BUGGY_BALL,
                        facing = dirToLuffy,
                        behavior = EnemyBehaviorState.WINDUP,
                        bossAttack = chosen,
                        comboStep = e.comboStep + 1,
                        stateTimer = 0f,
                        attackHitApplied = false
                    )
                }

                // Buggy dances around mid-range (65..140px) rather than blindly walking into melee
                val desiredVx = when {
                    absDx < 58f -> -dirToLuffy.sign * e.kind.moveSpeed * 0.85f
                    absDx > 142f -> dirToLuffy.sign * (if (enraged) e.kind.moveSpeed * 1.22f else e.kind.moveSpeed)
                    else -> dirToLuffy.sign * e.kind.moveSpeed * 0.35f
                }
                e.copy(
                    behavior = EnemyBehaviorState.CHASE,
                    vx = approach(e.vx, desiredVx, 380f * dt),
                    facing = dirToLuffy,
                    stateTimer = t
                )
            }
            EnemyBehaviorState.WINDUP -> {
                val windupDur = when (e.bossAttack) {
                    BossAttackVariant.BUGGY_FESTIVAL -> 0.44f
                    BossAttackVariant.BUGGY_CANNON -> 0.42f
                    BossAttackVariant.BUGGY_BALL -> 0.56f
                    else -> 0.45f
                }
                if (t >= windupDur) {
                    val dir = e.facing.sign.toFloat()
                    when (e.bossAttack) {
                        BossAttackVariant.BUGGY_FESTIVAL -> {
                            cb.playSfx(RetroSfx.BUGGY_SPLIT, 0.95f)
                            cb.triggerShake(4.2f, 0.24f)
                            val festivalDur = if (enraged) 2.25f else 1.95f
                            val parts = listOf(
                                BuggyPartEntity(
                                    id = cb.nextProjectileId(),
                                    kind = BuggyPartKind.HAND_RIGHT,
                                    x = e.x + dir * 10f,
                                    y = e.y - 20f,
                                    orbitAngle = 0f,
                                    orbitRadiusX = 56f,
                                    orbitRadiusY = 20f,
                                    maxAge = festivalDur,
                                    damage = 12,
                                    facing = e.facing
                                ),
                                BuggyPartEntity(
                                    id = cb.nextProjectileId(),
                                    kind = BuggyPartKind.HAND_LEFT,
                                    x = e.x - dir * 10f,
                                    y = e.y - 22f,
                                    orbitAngle = PI.toFloat(),
                                    orbitRadiusX = 56f,
                                    orbitRadiusY = 22f,
                                    maxAge = festivalDur,
                                    damage = 12,
                                    facing = e.facing
                                ),
                                BuggyPartEntity(
                                    id = cb.nextProjectileId(),
                                    kind = BuggyPartKind.TORSO,
                                    x = e.x,
                                    y = e.y - 18f,
                                    orbitAngle = PI.toFloat() * 0.5f,
                                    orbitRadiusX = 42f,
                                    orbitRadiusY = 28f,
                                    maxAge = festivalDur,
                                    damage = 11,
                                    facing = e.facing
                                ),
                                BuggyPartEntity(
                                    id = cb.nextProjectileId(),
                                    kind = BuggyPartKind.HEAD,
                                    x = e.x,
                                    y = e.y - 32f,
                                    orbitAngle = PI.toFloat() * 1.5f,
                                    orbitRadiusX = 36f,
                                    orbitRadiusY = 34f,
                                    maxAge = festivalDur,
                                    damage = 10,
                                    facing = e.facing
                                )
                            )
                            cb.spawnBuggyParts(parts)
                            // Blue Bara-Bara split burst particles
                            repeat(12) { idx ->
                                val ang = (idx / 12f) * 2f * PI.toFloat()
                                cb.addParticle(
                                    Particle(
                                        x = e.x,
                                        y = e.y - 20f,
                                        vx = kotlin.math.cos(ang) * 75f,
                                        vy = kotlin.math.sin(ang) * 55f,
                                        size = 3f,
                                        color = if (idx % 2 == 0) Color(0xFF5CE1FF) else Color(0xFFFFD84D),
                                        age = 0f,
                                        maxAge = 0.28f,
                                        kind = ParticleKind.IMPACT_SPARK
                                    )
                                )
                            }
                            e.copy(
                                behavior = EnemyBehaviorState.ATTACK,
                                stateTimer = 0f,
                                vx = 0f,
                                attackHitApplied = false
                            )
                        }
                        BossAttackVariant.BUGGY_CANNON -> {
                            cb.playSfx(RetroSfx.BUGGY_SPLIT, 0.85f)
                            cb.playSfx(RetroSfx.PUNCH_WHOOSH, 0.90f)
                            cb.triggerShake(3.6f, 0.18f)
                            val launchX = e.x + dir * 16f
                            val launchY = e.y - 17f
                            cb.spawnProjectile(
                                Projectile(
                                    id = cb.nextProjectileId(),
                                    kind = ProjectileKind.BUGGY_FIST,
                                    x = launchX,
                                    y = launchY,
                                    vx = dir * (if (enraged) 248f else 218f),
                                    width = 16f,
                                    height = 9f,
                                    damage = 16,
                                    facing = e.facing,
                                    maxAge = 2.2f
                                )
                            )
                            repeat(6) {
                                cb.addParticle(
                                    Particle(
                                        x = launchX,
                                        y = launchY,
                                        vx = dir * (50f + rng.nextFloat() * 45f),
                                        vy = rng.nextFloat() * 36f - 18f,
                                        size = 2.5f,
                                        color = Color(0xFF5CE1FF),
                                        age = 0f,
                                        maxAge = 0.20f,
                                        kind = ParticleKind.IMPACT_SPARK
                                    )
                                )
                            }
                            e.copy(
                                behavior = EnemyBehaviorState.ATTACK,
                                stateTimer = 0f,
                                vx = -dir * 26f,
                                attackHitApplied = true
                            )
                        }
                        else -> {
                            // BUGGY BALL: massive red skull cannonball fired in a high arc!
                            cb.playSfx(RetroSfx.CANNON_FIRE, 0.98f)
                            cb.triggerShake(5.5f, 0.26f)
                            val dist = absDx.coerceIn(70f, 210f)
                            val flightTime = 0.78f
                            val ballVx = dir * (dist / flightTime)
                            val ballVy = -148f
                            val startX = e.x + dir * 16f
                            val startY = e.y - 22f
                            cb.spawnProjectile(
                                Projectile(
                                    id = cb.nextProjectileId(),
                                    kind = ProjectileKind.BUGGY_BALL,
                                    x = startX,
                                    y = startY,
                                    vx = ballVx,
                                    vy = ballVy,
                                    gravityScale = 0.56f,
                                    width = 14f,
                                    height = 14f,
                                    damage = 24,
                                    facing = e.facing,
                                    maxAge = 3.0f
                                )
                            )
                            // Cannon muzzle smoke & sparks
                            repeat(10) { i ->
                                cb.addParticle(
                                    Particle(
                                        x = startX,
                                        y = startY,
                                        vx = dir * (35f + rng.nextFloat() * 65f),
                                        vy = -25f + rng.nextFloat() * 45f,
                                        size = if (i % 2 == 0) 4.5f else 3f,
                                        color = if (i % 2 == 0) Color(0xFFFF9E3D) else Color(0xFFE0E4EC),
                                        age = 0f,
                                        maxAge = 0.30f,
                                        kind = ParticleKind.DUST
                                    )
                                )
                            }
                            e.copy(
                                behavior = EnemyBehaviorState.ATTACK,
                                stateTimer = 0f,
                                vx = -dir * 45f,
                                attackHitApplied = true
                            )
                        }
                    }
                } else {
                    val drag = if (e.bossAttack == BossAttackVariant.BUGGY_BALL) 240f else 480f
                    e.copy(vx = approach(e.vx, 0f, drag * dt), facing = dirToLuffy, stateTimer = t)
                }
            }
            EnemyBehaviorState.ATTACK -> {
                val attackDur = when (e.bossAttack) {
                    BossAttackVariant.BUGGY_FESTIVAL -> if (enraged) 2.25f else 1.95f
                    BossAttackVariant.BUGGY_CANNON -> 0.32f
                    BossAttackVariant.BUGGY_BALL -> 0.36f
                    else -> 0.30f
                }
                if (t >= attackDur) {
                    if (e.bossAttack == BossAttackVariant.BUGGY_FESTIVAL) {
                        cb.playSfx(RetroSfx.BUGGY_SPLIT, 0.72f)
                    }
                    e.copy(
                        behavior = EnemyBehaviorState.RECOVER,
                        stateTimer = 0f,
                        attackCooldown = if (enraged) 0.90f else 1.25f
                    )
                } else {
                    e.copy(vx = approach(e.vx, 0f, 380f * dt), stateTimer = t)
                }
            }
            EnemyBehaviorState.RECOVER -> {
                if (t >= 0.42f) {
                    e.copy(behavior = EnemyBehaviorState.CHASE, stateTimer = 0f)
                } else {
                    e.copy(vx = approach(e.vx, 0f, 460f * dt), stateTimer = t)
                }
            }
            else -> e.copy(behavior = EnemyBehaviorState.CHASE, stateTimer = 0f)
        }
    }

    // === LEVEL 3 ENEMIES (ARLONG PARK: FISHMEN, CHEW SNIPER, HATCHAN & BOSS ARLONG) ===

    private fun updateFishmanBouncer(
        e: EnemyState,
        luffy: LuffyState,
        absDx: Float,
        dirToLuffy: FacingDirection,
        dt: Float,
        cb: EnemyStepCallbacks
    ): EnemyState {
        val shoveRange = 40f
        val t = e.stateTimer + dt
        val poolCenterX = (WorldConstants.ARLONG_POOL_LEFT_X + WorldConstants.ARLONG_POOL_RIGHT_X) * 0.5f

        return when (e.behavior) {
            EnemyBehaviorState.PATROL, EnemyBehaviorState.CHASE -> {
                if (absDx <= shoveRange && e.attackCooldown <= 0f && e.isGrounded) {
                    e.copy(
                        vx = 0f,
                        facing = dirToLuffy,
                        behavior = EnemyBehaviorState.WINDUP,
                        stateTimer = 0f,
                        attackHitApplied = false
                    )
                } else {
                    // Stay aggressively near player on the dry decks to shove them into the pool
                    val desiredDir = dirToLuffy.sign.toFloat()
                    e.copy(
                        behavior = EnemyBehaviorState.CHASE,
                        vx = approach(e.vx, desiredDir * e.kind.moveSpeed, 460f * dt),
                        facing = dirToLuffy,
                        stateTimer = t
                    )
                }
            }
            EnemyBehaviorState.WINDUP -> {
                if (t >= 0.42f) {
                    cb.playSfx(RetroSfx.PUNCH_WHOOSH, 0.78f)
                    e.copy(
                        behavior = EnemyBehaviorState.ATTACK,
                        stateTimer = 0f,
                        vx = e.facing.sign * 155f,
                        attackHitApplied = false
                    )
                } else {
                    e.copy(vx = approach(e.vx, 0f, 520f * dt), facing = dirToLuffy, stateTimer = t)
                }
            }
            EnemyBehaviorState.ATTACK -> {
                val attackDur = 0.24f
                var hitApplied = e.attackHitApplied
                if (!hitApplied && t >= 0.02f) {
                    val shoveBox = meleeBoxInFront(e, reach = 30f, height = 28f)
                    if (shoveBox.intersects(luffy.hurtbox)) {
                        hitApplied = true
                        // Deliberately shove player TOWARD the central pool when possible!
                        val towardPool = if (luffy.x < poolCenterX - 18f) 1f else if (luffy.x > poolCenterX + 18f) -1f else e.facing.sign.toFloat()
                        cb.onPlayerHit(
                            EnemyMeleeHit(
                                damage = 9,
                                knockbackDir = towardPool,
                                shakeIntensity = 5.4f,
                                hitStopSeconds = 0.05f,
                                knockbackSpeed = 235f
                            )
                        )
                    }
                }
                if (t >= attackDur) {
                    e.copy(
                        behavior = EnemyBehaviorState.RECOVER,
                        stateTimer = 0f,
                        attackCooldown = 1.05f,
                        attackHitApplied = hitApplied
                    )
                } else {
                    e.copy(vx = approach(e.vx, 0f, 360f * dt), stateTimer = t, attackHitApplied = hitApplied)
                }
            }
            EnemyBehaviorState.RECOVER -> {
                if (t >= 0.34f) {
                    e.copy(behavior = EnemyBehaviorState.CHASE, stateTimer = 0f)
                } else {
                    e.copy(vx = approach(e.vx, 0f, 480f * dt), stateTimer = t)
                }
            }
            else -> e.copy(behavior = EnemyBehaviorState.CHASE, stateTimer = 0f)
        }
    }

    private fun updateFishmanChew(
        e: EnemyState,
        luffy: LuffyState,
        absDx: Float,
        dirToLuffy: FacingDirection,
        dt: Float,
        cb: EnemyStepCallbacks
    ): EnemyState {
        val t = e.stateTimer + dt
        val isFarOrElevated = absDx > 110f || abs(luffy.y - e.y) > 22f
        val nextPerchFarTimer = if (!e.droppedFromBalcony) {
            if (isFarOrElevated) e.perchFarTimer + dt else 0f
        } else {
            0f
        }

        // Auto-jump down from balcony if Chew is the last enemy type remaining OR player stayed far for >= 5s!
        if (!e.droppedFromBalcony && (cb.onlyChewsLeft || nextPerchFarTimer >= 5.0f)) {
            val towardCenter = if (e.x < 380f) 1f else -1f
            cb.playSfx(RetroSfx.JUMP, 0.72f)
            return e.copy(
                vx = towardCenter * 72f,
                vy = -125f,
                isGrounded = false,
                droppedFromBalcony = true,
                perchFarTimer = 0f,
                facing = dirToLuffy,
                behavior = EnemyBehaviorState.CHASE,
                stateTimer = 0f,
                attackCooldown = 0.55f,
                attackHitApplied = false
            )
        }

        val tracked = e.copy(perchFarTimer = nextPerchFarTimer)
        return when (tracked.behavior) {
            EnemyBehaviorState.PATROL, EnemyBehaviorState.CHASE -> {
                if (absDx <= 320f && tracked.attackCooldown <= 0f && tracked.isGrounded) {
                    tracked.copy(
                        vx = 0f,
                        facing = dirToLuffy,
                        behavior = EnemyBehaviorState.WINDUP,
                        stateTimer = 0f,
                        attackHitApplied = false
                    )
                } else {
                    val desiredVx = if (tracked.droppedFromBalcony && tracked.isGrounded) {
                        when {
                            absDx < 62f -> -dirToLuffy.sign * tracked.kind.moveSpeed * 0.85f
                            absDx > 155f -> dirToLuffy.sign * tracked.kind.moveSpeed
                            else -> 0f
                        }
                    } else {
                        0f
                    }
                    tracked.copy(
                        behavior = EnemyBehaviorState.CHASE,
                        vx = approach(tracked.vx, desiredVx, 460f * dt),
                        facing = dirToLuffy,
                        stateTimer = t
                    )
                }
            }
            EnemyBehaviorState.WINDUP -> {
                if (t >= 0.56f) {
                    val dir = tracked.facing.sign.toFloat()
                    val mouthX = tracked.x + dir * 12f
                    val mouthY = tracked.y - 20f
                    val targetX = luffy.x
                    val targetY = luffy.y - 16f
                    val dx = targetX - mouthX
                    val dy = targetY - mouthY
                    val dist = hypot(dx.toDouble(), dy.toDouble()).toFloat().coerceAtLeast(1f)
                    val spd = 164f
                    cb.playSfx(RetroSfx.WATER_SPLASH, 0.62f)
                    cb.spawnProjectile(
                        Projectile(
                            id = cb.nextProjectileId(),
                            kind = ProjectileKind.WATER_BULLET,
                            x = mouthX,
                            y = mouthY,
                            vx = (dx / dist) * spd,
                            vy = (dy / dist) * spd,
                            width = 10f,
                            height = 7f,
                            damage = 11,
                            facing = tracked.facing,
                            maxAge = 2.6f
                        )
                    )
                    repeat(4) {
                        cb.addParticle(
                            Particle(
                                x = mouthX,
                                y = mouthY,
                                vx = dir * (28f + rng.nextFloat() * 34f),
                                vy = rng.nextFloat() * 28f - 14f,
                                size = 2.2f,
                                color = Color(0xFF5CE1FF),
                                age = 0f,
                                maxAge = 0.22f,
                                kind = ParticleKind.WATER_SPRAY
                            )
                        )
                    }
                    tracked.copy(
                        behavior = EnemyBehaviorState.ATTACK,
                        stateTimer = 0f,
                        attackHitApplied = true
                    )
                } else {
                    tracked.copy(vx = approach(tracked.vx, 0f, 520f * dt), facing = dirToLuffy, stateTimer = t)
                }
            }
            EnemyBehaviorState.ATTACK -> {
                if (t >= 0.20f) {
                    tracked.copy(
                        behavior = EnemyBehaviorState.RECOVER,
                        stateTimer = 0f,
                        attackCooldown = 1.65f
                    )
                } else {
                    tracked.copy(stateTimer = t)
                }
            }
            EnemyBehaviorState.RECOVER -> {
                if (t >= 0.30f) {
                    tracked.copy(behavior = EnemyBehaviorState.CHASE, stateTimer = 0f)
                } else {
                    tracked.copy(stateTimer = t)
                }
            }
            else -> tracked.copy(behavior = EnemyBehaviorState.CHASE, stateTimer = 0f)
        }
    }

    private fun updateHatchan(
        e: EnemyState,
        luffy: LuffyState,
        absDx: Float,
        dirToLuffy: FacingDirection,
        dt: Float,
        cb: EnemyStepCallbacks
    ): EnemyState {
        val t = e.stateTimer + dt
        return when (e.behavior) {
            EnemyBehaviorState.PATROL, EnemyBehaviorState.CHASE -> {
                if (e.attackCooldown <= 0f && e.isGrounded) {
                    // Alternates 6-sword melee rush with Octopus Whirlwind Spin (which reflects projectiles!)
                    val doSpin = (e.comboStep % 2 == 1) || absDx in 58f..175f
                    val variant = if (doSpin) BossAttackVariant.HATCHAN_SPIN else BossAttackVariant.HATCHAN_SLASH
                    if (variant == BossAttackVariant.HATCHAN_SPIN || absDx <= 44f) {
                        return e.copy(
                            vx = 0f,
                            facing = dirToLuffy,
                            behavior = EnemyBehaviorState.WINDUP,
                            bossAttack = variant,
                            comboStep = e.comboStep + 1,
                            stateTimer = 0f,
                            attackHitApplied = false
                        )
                    }
                }
                e.copy(
                    behavior = EnemyBehaviorState.CHASE,
                    vx = approach(e.vx, dirToLuffy.sign * e.kind.moveSpeed, 520f * dt),
                    facing = dirToLuffy,
                    stateTimer = t
                )
            }
            EnemyBehaviorState.WINDUP -> {
                val windupDur = if (e.bossAttack == BossAttackVariant.HATCHAN_SPIN) 0.36f else 0.30f
                if (t >= windupDur) {
                    if (e.bossAttack == BossAttackVariant.HATCHAN_SPIN) {
                        cb.playSfx(RetroSfx.ZORO_TATSUMAKI, 0.78f)
                    } else {
                        cb.playSfx(RetroSfx.ZORO_SLASH, 0.85f)
                    }
                    e.copy(
                        behavior = EnemyBehaviorState.ATTACK,
                        stateTimer = 0f,
                        vx = e.facing.sign * if (e.bossAttack == BossAttackVariant.HATCHAN_SPIN) 58f else 95f,
                        attackHitApplied = false
                    )
                } else {
                    e.copy(vx = approach(e.vx, 0f, 520f * dt), facing = dirToLuffy, stateTimer = t)
                }
            }
            EnemyBehaviorState.ATTACK -> {
                if (e.bossAttack == BossAttackVariant.HATCHAN_SPIN) {
                    val spinDur = 1.75f
                    var hitApplied = e.attackHitApplied
                    // Swirling wind & blade spark trail
                    if (rng.nextFloat() < 0.55f) {
                        cb.addParticle(
                            Particle(
                                x = e.x + (rng.nextFloat() * 34f - 17f),
                                y = e.y - 12f - rng.nextFloat() * 18f,
                                vx = (rng.nextFloat() * 60f - 30f),
                                vy = -25f - rng.nextFloat() * 25f,
                                size = 2.4f,
                                color = if (rng.nextBoolean()) Color(0xFFE4EFFA) else Color(0xFFF28B9A),
                                age = 0f,
                                maxAge = 0.22f,
                                kind = ParticleKind.WIND_STREAK,
                                stretchX = 2.6f
                            )
                        )
                    }
                    // Periodic melee hurt check during spin
                    val spinBox = Rect2D(e.x - 22f, e.y - 32f, 44f, 30f)
                    if (!hitApplied && spinBox.intersects(luffy.hurtbox)) {
                        hitApplied = true
                        cb.onPlayerHit(
                            EnemyMeleeHit(
                                damage = 13,
                                knockbackDir = dirToLuffy.sign.toFloat(),
                                shakeIntensity = 5.0f,
                                hitStopSeconds = 0.045f,
                                knockbackSpeed = 148f
                            )
                        )
                    }
                    if (t >= spinDur) {
                        e.copy(
                            behavior = EnemyBehaviorState.RECOVER,
                            stateTimer = 0f,
                            attackCooldown = 1.15f,
                            attackHitApplied = false
                        )
                    } else {
                        val chaseVx = dirToLuffy.sign * 52f
                        e.copy(
                            vx = approach(e.vx, chaseVx, 240f * dt),
                            facing = dirToLuffy,
                            stateTimer = t,
                            attackHitApplied = if (t > 0.85f && hitApplied) false else hitApplied
                        )
                    }
                } else {
                    val slashDur = 0.24f
                    var hitApplied = e.attackHitApplied
                    if (!hitApplied && t >= 0.03f) {
                        val slashBox = meleeBoxInFront(e, reach = 32f, height = 30f)
                        if (slashBox.intersects(luffy.hurtbox)) {
                            hitApplied = true
                            cb.onPlayerHit(
                                EnemyMeleeHit(
                                    damage = 15,
                                    knockbackDir = e.facing.sign.toFloat(),
                                    shakeIntensity = 5.2f,
                                    hitStopSeconds = 0.05f,
                                    knockbackSpeed = 140f
                                )
                            )
                        }
                    }
                    if (t >= slashDur) {
                        e.copy(
                            behavior = EnemyBehaviorState.RECOVER,
                            stateTimer = 0f,
                            attackCooldown = 0.85f,
                            attackHitApplied = hitApplied
                        )
                    } else {
                        e.copy(vx = approach(e.vx, 0f, 360f * dt), stateTimer = t, attackHitApplied = hitApplied)
                    }
                }
            }
            EnemyBehaviorState.RECOVER -> {
                if (t >= 0.34f) {
                    e.copy(behavior = EnemyBehaviorState.CHASE, stateTimer = 0f)
                } else {
                    e.copy(vx = approach(e.vx, 0f, 480f * dt), stateTimer = t)
                }
            }
            else -> e.copy(behavior = EnemyBehaviorState.CHASE, stateTimer = 0f)
        }
    }

    // === LEVEL 3 BOSS — ARLONG SAW (PHASE 1 COURTYARD + PHASE 2 MAP ROOM KIRIBATI) ===

    private fun updateBossArlong(
        e: EnemyState,
        luffy: LuffyState,
        absDx: Float,
        dirToLuffy: FacingDirection,
        dt: Float,
        cb: EnemyStepCallbacks
    ): EnemyState {
        val t = e.stateTimer + dt
        val inMapRoom = cb.isArlongMapRoom
        val poolCenterX = (WorldConstants.ARLONG_POOL_LEFT_X + WorldConstants.ARLONG_POOL_RIGHT_X) * 0.5f

        return when (e.behavior) {
            EnemyBehaviorState.PATROL, EnemyBehaviorState.CHASE -> {
                if (e.attackCooldown <= 0f && e.isGrounded) {
                    val chosen = if (!inMapRoom) {
                        // Phase 1 (Courtyard & Pool): Shark On Darts torpedo, Water Bullet fan, Jaw Slam
                        when (e.comboStep % 3) {
                            0 -> BossAttackVariant.ARLONG_SHARK_DART
                            1 -> BossAttackVariant.ARLONG_WATER_FAN
                            else -> if (absDx <= 64f) BossAttackVariant.ARLONG_JAW_SLAM else BossAttackVariant.ARLONG_SHARK_DART
                        }
                    } else {
                        // Phase 2 (Map Room): Massive Kiribati saw-blade cleaves & leaping tower slams!
                        when (e.comboStep % 3) {
                            0 -> if (absDx <= 72f) BossAttackVariant.ARLONG_KIRIBATI_CLEAVE else BossAttackVariant.ARLONG_KIRIBATI_SLAM
                            1 -> BossAttackVariant.ARLONG_KIRIBATI_SLAM
                            else -> BossAttackVariant.ARLONG_KIRIBATI_CLEAVE
                        }
                    }
                    return e.copy(
                        vx = 0f,
                        facing = dirToLuffy,
                        behavior = EnemyBehaviorState.WINDUP,
                        bossAttack = chosen,
                        comboStep = e.comboStep + 1,
                        stateTimer = 0f,
                        attackHitApplied = false
                    )
                }
                val speed = if (inMapRoom) e.kind.moveSpeed * 1.18f else e.kind.moveSpeed
                e.copy(
                    behavior = EnemyBehaviorState.CHASE,
                    vx = approach(e.vx, dirToLuffy.sign * speed, 420f * dt),
                    facing = dirToLuffy,
                    stateTimer = t
                )
            }
            EnemyBehaviorState.WINDUP -> {
                val windupDur = when (e.bossAttack) {
                    BossAttackVariant.ARLONG_SHARK_DART -> 0.52f
                    BossAttackVariant.ARLONG_WATER_FAN -> 0.44f
                    BossAttackVariant.ARLONG_KIRIBATI_SLAM -> 0.46f
                    BossAttackVariant.ARLONG_KIRIBATI_CLEAVE -> 0.44f
                    else -> 0.42f
                }
                if (t >= windupDur) {
                    val dir = e.facing.sign.toFloat()
                    when (e.bossAttack) {
                        BossAttackVariant.ARLONG_SHARK_DART -> {
                            cb.playSfx(RetroSfx.SHARK_DART, 0.96f)
                            cb.triggerShake(5.5f, 0.30f)
                            e.copy(
                                behavior = EnemyBehaviorState.ATTACK,
                                stateTimer = 0f,
                                vx = dir * 355f,
                                vy = -45f,
                                isGrounded = false,
                                attackHitApplied = false
                            )
                        }
                        BossAttackVariant.ARLONG_WATER_FAN -> {
                            cb.playSfx(RetroSfx.WATER_SPLASH, 0.90f)
                            // Fan of 3 pressurized water bullets!
                            val fanVys = floatArrayOf(-52f, -10f, 30f)
                            for (vyOff in fanVys) {
                                cb.spawnProjectile(
                                    Projectile(
                                        id = cb.nextProjectileId(),
                                        kind = ProjectileKind.WATER_BULLET,
                                        x = e.x + dir * 16f,
                                        y = e.y - 22f,
                                        vx = dir * 182f,
                                        vy = vyOff,
                                        width = 11f,
                                        height = 8f,
                                        damage = 14,
                                        facing = e.facing,
                                        maxAge = 2.4f
                                    )
                                )
                            }
                            e.copy(
                                behavior = EnemyBehaviorState.ATTACK,
                                stateTimer = 0f,
                                vx = -dir * 26f,
                                attackHitApplied = true
                            )
                        }
                        BossAttackVariant.ARLONG_KIRIBATI_SLAM -> {
                            cb.playSfx(RetroSfx.KIRIBATI_SLASH, 0.96f)
                            e.copy(
                                behavior = EnemyBehaviorState.ATTACK,
                                stateTimer = 0f,
                                vx = dir * (absDx.coerceIn(70f, 185f) * 1.15f),
                                vy = -235f,
                                isGrounded = false,
                                attackHitApplied = false
                            )
                        }
                        BossAttackVariant.ARLONG_KIRIBATI_CLEAVE -> {
                            cb.playSfx(RetroSfx.KIRIBATI_SLASH, 0.96f)
                            cb.triggerShake(6.2f, 0.28f)
                            cb.addSlashVfx(
                                SwordSlashVfx(
                                    id = cb.nextProjectileId(),
                                    x = e.x + dir * 28f,
                                    y = e.y - 22f,
                                    facing = e.facing,
                                    kind = SlashVfxKind.KIRIBATI_SLASH,
                                    maxAge = 0.26f
                                )
                            )
                            cb.onMapRoomShock(e.x + dir * 30f, 64f)
                            e.copy(
                                behavior = EnemyBehaviorState.ATTACK,
                                stateTimer = 0f,
                                vx = dir * 115f,
                                attackHitApplied = false
                            )
                        }
                        else -> {
                            cb.playSfx(RetroSfx.MORGAN_SWING, 0.88f)
                            e.copy(
                                behavior = EnemyBehaviorState.ATTACK,
                                stateTimer = 0f,
                                vx = dir * 105f,
                                attackHitApplied = false
                            )
                        }
                    }
                } else {
                    e.copy(vx = approach(e.vx, 0f, 520f * dt), facing = dirToLuffy, stateTimer = t)
                }
            }
            EnemyBehaviorState.ATTACK -> {
                var hitApplied = e.attackHitApplied
                when (e.bossAttack) {
                    BossAttackVariant.ARLONG_SHARK_DART -> {
                        val dartDur = 0.62f
                        // Cyan hydro-jet trail behind Shark On Darts torpedo!
                        if (rng.nextFloat() < 0.85f) {
                            cb.addParticle(
                                Particle(
                                    x = e.x - e.facing.sign * 14f,
                                    y = e.y - 18f + (rng.nextFloat() * 14f - 7f),
                                    vx = -e.facing.sign * (55f + rng.nextFloat() * 45f),
                                    vy = rng.nextFloat() * 34f - 17f,
                                    size = 3.2f,
                                    color = if (rng.nextBoolean()) Color(0xFF5CE1FF) else Color(0xFFFFFFFF),
                                    age = 0f,
                                    maxAge = 0.24f,
                                    kind = ParticleKind.WATER_SPRAY
                                )
                            )
                        }
                        if (!hitApplied) {
                            val dartBox = Rect2D(e.x - 18f, e.y - 32f, 36f, 28f)
                            if (dartBox.intersects(luffy.hurtbox)) {
                                hitApplied = true
                                cb.onPlayerHit(
                                    EnemyMeleeHit(
                                        damage = 19,
                                        knockbackDir = e.facing.sign.toFloat(),
                                        shakeIntensity = 7.4f,
                                        hitStopSeconds = 0.06f,
                                        knockbackSpeed = 215f
                                    )
                                )
                            }
                        }
                        val wallMin = if (cb.isLevel3) WorldConstants.ARLONG_ARENA_MIN_X + 10f else WorldConstants.LEFT_BOUNDARY_X + 12f
                        val wallMax = if (cb.isLevel3) WorldConstants.ARLONG_ARENA_MAX_X - 10f else WorldConstants.RIGHT_BOUNDARY_X - 12f
                        val hitWall = e.x <= wallMin || e.x >= wallMax
                        if (t >= dartDur || (hitWall && t > 0.14f)) {
                            cb.triggerShake(5.0f, 0.22f)
                            e.copy(
                                behavior = EnemyBehaviorState.RECOVER,
                                stateTimer = 0f,
                                vx = 0f,
                                attackCooldown = 1.05f,
                                attackHitApplied = hitApplied
                            )
                        } else {
                            e.copy(vx = e.facing.sign * 345f, stateTimer = t, attackHitApplied = hitApplied)
                        }
                    }
                    BossAttackVariant.ARLONG_KIRIBATI_SLAM -> {
                        if (!hitApplied && e.vy > 10f) {
                            val slamBox = Rect2D(e.x - 28f, e.y - 36f, 56f, 36f)
                            if (slamBox.intersects(luffy.hurtbox)) {
                                hitApplied = true
                                cb.onPlayerHit(
                                    EnemyMeleeHit(
                                        damage = 22,
                                        knockbackDir = e.facing.sign.toFloat(),
                                        shakeIntensity = 7.8f,
                                        hitStopSeconds = 0.065f,
                                        knockbackSpeed = 165f
                                    )
                                )
                            }
                        }
                        if (e.isGrounded && t > 0.18f) {
                            cb.playSfx(RetroSfx.AXE_SLAM, 0.94f)
                            cb.triggerShake(8.2f, 0.36f)
                            cb.addSlashVfx(
                                SwordSlashVfx(
                                    id = cb.nextProjectileId(),
                                    x = e.x + e.facing.sign * 20f,
                                    y = e.y - 16f,
                                    facing = e.facing,
                                    kind = SlashVfxKind.KIRIBATI_SLASH,
                                    maxAge = 0.28f
                                )
                            )
                            cb.onMapRoomShock(e.x, 86f)
                            if (!hitApplied && abs(luffy.x - e.x) <= 44f && abs(luffy.y - e.y) <= 22f) {
                                hitApplied = true
                                cb.onPlayerHit(
                                    EnemyMeleeHit(
                                        damage = 20,
                                        knockbackDir = if (luffy.x >= e.x) 1f else -1f,
                                        shakeIntensity = 7.5f,
                                        hitStopSeconds = 0.06f,
                                        knockbackSpeed = 160f
                                    )
                                )
                            }
                            e.copy(
                                behavior = EnemyBehaviorState.RECOVER,
                                stateTimer = 0f,
                                vx = 0f,
                                attackCooldown = 0.92f,
                                attackHitApplied = hitApplied
                            )
                        } else {
                            e.copy(stateTimer = t, attackHitApplied = hitApplied)
                        }
                    }
                    BossAttackVariant.ARLONG_KIRIBATI_CLEAVE -> {
                        if (!hitApplied && t in 0.03f..0.20f) {
                            val cleaveBox = meleeBoxInFront(e, reach = 48f, height = 38f)
                            if (cleaveBox.intersects(luffy.hurtbox)) {
                                hitApplied = true
                                cb.onPlayerHit(
                                    EnemyMeleeHit(
                                        damage = 21,
                                        knockbackDir = e.facing.sign.toFloat(),
                                        shakeIntensity = 7.0f,
                                        hitStopSeconds = 0.06f,
                                        knockbackSpeed = 158f
                                    )
                                )
                            }
                        }
                        if (t >= 0.30f) {
                            e.copy(
                                behavior = EnemyBehaviorState.RECOVER,
                                stateTimer = 0f,
                                attackCooldown = 0.85f,
                                attackHitApplied = hitApplied
                            )
                        } else {
                            e.copy(vx = approach(e.vx, 0f, 380f * dt), stateTimer = t, attackHitApplied = hitApplied)
                        }
                    }
                    BossAttackVariant.ARLONG_WATER_FAN -> {
                        if (t >= 0.30f) {
                            e.copy(
                                behavior = EnemyBehaviorState.RECOVER,
                                stateTimer = 0f,
                                attackCooldown = 1.05f
                            )
                        } else {
                            e.copy(vx = approach(e.vx, 0f, 360f * dt), stateTimer = t)
                        }
                    }
                    else -> {
                        if (!hitApplied && t in 0.03f..0.18f) {
                            val jawBox = meleeBoxInFront(e, reach = 36f, height = 34f)
                            if (jawBox.intersects(luffy.hurtbox)) {
                                hitApplied = true
                                val towardPool = if (luffy.x < poolCenterX - 16f) 1f else if (luffy.x > poolCenterX + 16f) -1f else e.facing.sign.toFloat()
                                cb.onPlayerHit(
                                    EnemyMeleeHit(
                                        damage = 16,
                                        knockbackDir = towardPool,
                                        shakeIntensity = 6.2f,
                                        hitStopSeconds = 0.055f,
                                        knockbackSpeed = 225f
                                    )
                                )
                            }
                        }
                        if (t >= 0.26f) {
                            e.copy(
                                behavior = EnemyBehaviorState.RECOVER,
                                stateTimer = 0f,
                                attackCooldown = 0.95f,
                                attackHitApplied = hitApplied
                            )
                        } else {
                            e.copy(vx = approach(e.vx, 0f, 380f * dt), stateTimer = t, attackHitApplied = hitApplied)
                        }
                    }
                }
            }
            EnemyBehaviorState.RECOVER -> {
                if (t >= 0.38f) {
                    e.copy(behavior = EnemyBehaviorState.CHASE, stateTimer = 0f)
                } else {
                    e.copy(vx = approach(e.vx, 0f, 460f * dt), stateTimer = t)
                }
            }
            else -> e.copy(behavior = EnemyBehaviorState.CHASE, stateTimer = 0f)
        }
    }

    private fun integratePhysics(
        e: EnemyState,
        dt: Float,
        cb: EnemyStepCallbacks
    ): EnemyState {
        // Chew snipers stay perched on their elevated left/right balconies until they jump down
        if (e.kind == EnemyKind.FISHMAN_CHEW && !e.droppedFromBalcony) {
            val isLeftBalcony = e.patrolOriginX < 380f
            val minX = if (isLeftBalcony) WorldConstants.ARLONG_LEFT_BALCONY_MIN_X + 10f else WorldConstants.ARLONG_RIGHT_BALCONY_MIN_X + 10f
            val maxX = if (isLeftBalcony) WorldConstants.ARLONG_LEFT_BALCONY_MAX_X - 10f else WorldConstants.ARLONG_RIGHT_BALCONY_MAX_X - 10f
            var vy = e.vy
            if (!e.isGrounded) {
                vy = (vy + WorldConstants.GRAVITY * dt).coerceAtMost(WorldConstants.MAX_FALL_SPEED)
            }
            val nextX = (e.x + e.vx * dt).coerceIn(minX, maxX)
            var nextY = (e.y + vy * dt).coerceAtLeast(42f)
            val balcY = WorldConstants.ARLONG_BALCONY_Y
            val grounded: Boolean
            if (nextY >= balcY) {
                nextY = balcY
                vy = 0f
                grounded = true
            } else {
                grounded = false
            }
            return e.copy(x = nextX, y = nextY, vy = vy, isGrounded = grounded)
        }

        var vy = e.vy
        if (!e.isGrounded) {
            vy = (vy + WorldConstants.GRAVITY * dt).coerceAtMost(WorldConstants.MAX_FALL_SPEED)
        }
        val minArenaX = when {
            cb.isLevel3 && cb.isArlongMapRoom -> WorldConstants.ARLONG_MAP_ROOM_MIN_X
            cb.isLevel3 -> WorldConstants.ARLONG_ARENA_MIN_X
            else -> WorldConstants.LEFT_BOUNDARY_X + 6f
        }
        val maxArenaX = when {
            cb.isLevel3 && cb.isArlongMapRoom -> WorldConstants.ARLONG_MAP_ROOM_MAX_X
            cb.isLevel3 -> WorldConstants.ARLONG_ARENA_MAX_X
            else -> WorldConstants.RIGHT_BOUNDARY_X - 6f
        }
        val nextX = (e.x + e.vx * dt).coerceIn(minArenaX, maxArenaX)
        var nextY = (e.y + vy * dt).coerceAtLeast(42f)
        val groundY = cb.groundYAt(nextX)
        val grounded: Boolean
        if (nextY >= groundY) {
            nextY = groundY
            vy = 0f
            grounded = true
        } else {
            grounded = false
        }
        return e.copy(x = nextX, y = nextY, vy = vy, isGrounded = grounded)
    }

    private fun meleeBoxInFront(e: EnemyState, reach: Float, height: Float): Rect2D {
        val boxX = if (e.facing == FacingDirection.RIGHT) {
            e.x + 2f
        } else {
            e.x - 2f - reach
        }
        return Rect2D(x = boxX, y = e.y - height - 2f, width = reach, height = height)
    }

    private fun spawnMorganSlamDust(x: Float, y: Float, addParticle: (Particle) -> Unit) {
        val colors = listOf(Color(0xFFFFF7D6), Color(0xFF7CE8FF), Color(0xFFD4A04A))
        repeat(14) { i ->
            val side = if (i % 2 == 0) 1f else -1f
            addParticle(
                Particle(
                    x = x + (rng.nextFloat() * 14f - 7f),
                    y = y - 3f,
                    vx = side * (25f + rng.nextFloat() * 80f),
                    vy = -24f - rng.nextFloat() * 55f,
                    size = 3.5f + rng.nextFloat() * 3.5f,
                    color = colors[i % colors.size],
                    age = 0f,
                    maxAge = 0.38f + rng.nextFloat() * 0.22f,
                    kind = ParticleKind.DUST
                )
            )
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
