package com.example.game.ui

import android.view.KeyEvent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.game.audio.RetroSoundEngine
import com.example.game.engine.GameEngine
import com.example.game.model.ArlongStagePhase
import com.example.game.model.AttackType
import com.example.game.model.CooldownState
import com.example.game.model.HudUiState
import com.example.game.model.InputSnapshot
import com.example.game.model.LevelPhase
import com.example.game.model.PlayableHero
import com.example.game.renderer.EastBlueRenderer
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * MVVM ViewModel bridging hardware keyboard + floating joystick / multi-touch arcade controls,
 * the 60fps GameEngine simulation, RetroSoundEngine, and pixel HUD StateFlow.
 */
class GameViewModel : ViewModel() {

    val soundEngine = RetroSoundEngine()
    val engine = GameEngine(soundEngine)
    val luffySprites = LuffySpriteSheet()
    val zoroSprites = ZoroSpriteSheet()
    val sanjiSprites = SanjiSpriteSheet()
    val namiSprites = NamiSpriteSheet()
    val usoppSprites = UsoppSpriteSheet()
    val chopperSprites = ChopperSpriteSheet()
    val envSprites = EastBlueSprites()
    val enemySprites = EnemySpriteSheet()
    val buggySprites = BuggyCrewSpriteSheet()
    val arlongSprites = ArlongParkSpriteSheet()
    val renderer = EastBlueRenderer(
        luffySprites,
        zoroSprites,
        sanjiSprites,
        namiSprites,
        usoppSprites,
        chopperSprites,
        envSprites,
        enemySprites,
        buggySprites,
        arlongSprites
    )

    var worldState by mutableStateOf(engine.state)
        private set

    private val _hudState = MutableStateFlow(buildHudState(engine.state, HudUiState()))
    val hudState: StateFlow<HudUiState> = _hudState.asStateFlow()

    // Separated keyboard & touch hold states so multi-input never conflicts
    private var keyLeft = false
    private var keyRight = false
    private var keyJump = false

    private var touchLeft = false
    private var touchRight = false
    private var touchJump = false

    // Edge-triggered queues consumed once per simulation step
    private var queuedJump = false
    private var queuedPistol = false
    private var queuedGatling = false
    private var queuedAxe = false
    private var queuedSwitchHero = false
    private var queuedSelectLuffy = false
    private var queuedSelectZoro = false
    private var queuedSelectSanji = false
    private var queuedSelectNami = false
    private var queuedSelectUsopp = false
    private var queuedSelectChopper = false
    private var queuedGear4 = false

    fun stepFrame(dtSeconds: Float, viewportVirtualWidth: Float) {
        val snapshot = InputSnapshot(
            moveLeft = keyLeft || touchLeft,
            moveRight = keyRight || touchRight,
            jumpHeld = keyJump || touchJump,
            jumpJustPressed = queuedJump,
            pistolJustPressed = queuedPistol,
            gatlingJustPressed = queuedGatling,
            axeJustPressed = queuedAxe,
            switchHeroJustPressed = queuedSwitchHero,
            selectLuffyJustPressed = queuedSelectLuffy,
            selectZoroJustPressed = queuedSelectZoro,
            selectSanjiJustPressed = queuedSelectSanji,
            selectNamiJustPressed = queuedSelectNami,
            selectUsoppJustPressed = queuedSelectUsopp,
            selectChopperJustPressed = queuedSelectChopper,
            gear4JustPressed = queuedGear4
        )
        queuedJump = false
        queuedPistol = false
        queuedGatling = false
        queuedAxe = false
        queuedSwitchHero = false
        queuedSelectLuffy = false
        queuedSelectZoro = false
        queuedSelectSanji = false
        queuedSelectNami = false
        queuedSelectUsopp = false
        queuedSelectChopper = false
        queuedGear4 = false

        engine.step(dtSeconds, snapshot, viewportVirtualWidth)
        val newWorld = engine.state
        worldState = newWorld

        _hudState.update { prev ->
            val next = buildHudState(newWorld, prev)
            if (next == prev) prev else next
        }
    }

    private fun buildHudState(
        world: com.example.game.model.WorldState,
        prev: HudUiState
    ): HudUiState {
        val luffy = world.luffy
        val boss = world.activeBoss
        val waveLabel = if (world.levelNumber == 3) {
            when (world.arlongPhase) {
                ArlongStagePhase.NONE,
                ArlongStagePhase.GATE_INTRO -> "ВОРОТА"
                ArlongStagePhase.COURTYARD_FISHMEN -> "ДВОР"
                ArlongStagePhase.COURTYARD_HATCHAN -> "ХАТТЯН"
                ArlongStagePhase.ARLONG_PHASE1 -> "АРЛОНГ"
                ArlongStagePhase.TOWER_TRANSITION,
                ArlongStagePhase.ARLONG_PHASE2,
                ArlongStagePhase.FINISHER_SLOWMO,
                ArlongStagePhase.TOWER_COLLAPSE -> "КОМНАТА КАРТ"
            }
        } else if (boss != null || world.waveIndex >= world.totalRegularWaves) {
            "БОСС"
        } else {
            "ВОЛНА ${world.waveIndex + 1}/${world.totalRegularWaves}"
        }
        val (skill1Key, skill2Key, skill3Key) = when (luffy.activeHero) {
            PlayableHero.LUFFY -> if (luffy.gear4Active) {
                Triple(AttackType.GEAR4_KONG_GUN, AttackType.GEAR4_KONG_ORGAN, AttackType.GEAR4_KING_KONG_GUN)
            } else {
                Triple(AttackType.PISTOL, AttackType.GATLING, AttackType.AXE)
            }
            PlayableHero.ZORO -> if (luffy.zoroHellActive) {
                Triple(AttackType.ZORO_HELL_SLASH, AttackType.ZORO_PURGATORY_ONIGIRI, AttackType.ZORO_ASURA)
            } else {
                Triple(AttackType.ZORO_SLASH, AttackType.ZORO_ONIGIRI, AttackType.ZORO_TATSUMAKI)
            }
            PlayableHero.SANJI -> Triple(AttackType.SANJI_KICK, AttackType.SANJI_PARTY_TABLE, AttackType.SANJI_DIABLE_JAMBE)
            PlayableHero.NAMI -> Triple(AttackType.NAMI_SPARK, AttackType.NAMI_CYCLONE, AttackType.NAMI_THUNDERBOLT)
            PlayableHero.USOPP -> Triple(AttackType.USOPP_SHOT, AttackType.USOPP_EXPLODING_STAR, AttackType.USOPP_FIREBIRD_STAR)
            PlayableHero.CHOPPER -> Triple(AttackType.CHOPPER_KUNGFU, AttackType.CHOPPER_GUARD_HEAL, AttackType.CHOPPER_MONSTER_POINT)
        }
        val skill1Cd = luffy.cooldowns[skill1Key] ?: CooldownState()
        val skill2Cd = luffy.cooldowns[skill2Key] ?: CooldownState()
        val skill3Cd = luffy.cooldowns[skill3Key] ?: CooldownState()

        return prev.copy(
            activeHero = luffy.activeHero,
            standbyHero = luffy.standbyHero,
            standbyHp = luffy.standbyHp,
            standbyMaxHp = luffy.standbyHero.maxHp,
            heroHpMap = luffy.heroHp,
            luffyHp = luffy.currentHp,
            luffyMaxHp = luffy.maxHp,
            berries = luffy.berries,
            defeatedCount = luffy.defeatedCount,
            levelNumber = world.levelNumber,
            levelLabel = world.levelTitle,
            waveLabel = waveLabel,
            bossName = boss?.kind?.displayName,
            bossHp = boss?.currentHp ?: 0,
            bossMaxHp = boss?.maxHp ?: 1,
            levelPhase = world.levelPhase,
            waveBanner = world.waveBanner,
            pistolCooldown = skill1Cd,
            gatlingCooldown = skill2Cd,
            axeCooldown = skill3Cd,
            activeAttack = luffy.activeAttack,
            banner = world.banner,
            showHitboxes = world.showHitboxes,
            arlongPhase = world.arlongPhase,
            luffyDrowning = world.luffyDrowning,
            luffyDrownTimer = world.luffyDrownTimer,
            towerDestruction = world.towerDestruction,
            gear4Meter = luffy.gear4Meter,
            gear4Active = luffy.gear4Active,
            gear4Timer = luffy.gear4Timer,
            zoroHellMeter = luffy.zoroHellMeter,
            zoroHellActive = luffy.zoroHellActive,
            zoroHellTimer = luffy.zoroHellTimer,
            zoroParryCritReady = luffy.zoroParryCritReady
        )
    }

    private fun clearAllInputs() {
        keyLeft = false
        keyRight = false
        keyJump = false
        touchLeft = false
        touchRight = false
        touchJump = false
        queuedJump = false
        queuedPistol = false
        queuedGatling = false
        queuedAxe = false
        queuedSwitchHero = false
        queuedSelectLuffy = false
        queuedSelectZoro = false
        queuedSelectSanji = false
        queuedSelectNami = false
        queuedSelectUsopp = false
        queuedSelectChopper = false
        queuedGear4 = false
    }

    fun startLevel(levelNumber: Int) {
        clearAllInputs()
        engine.startLevel(levelNumber)
        val freshWorld = engine.state
        worldState = freshWorld
        _hudState.update { prev -> buildHudState(freshWorld, prev) }
    }

    fun nextLevel() {
        val next = (engine.state.levelNumber + 1).coerceAtMost(3)
        startLevel(next)
    }

    fun openMainMenu() {
        clearAllInputs()
        engine.openMainMenu()
        val menuWorld = engine.state
        worldState = menuWorld
        _hudState.update { prev -> buildHudState(menuWorld, prev) }
    }

    fun restartLevel() {
        startLevel(engine.state.levelNumber)
    }

    /**
     * Handles physical/emulator keyboard events cleanly.
     * Returns true if the key belongs to the game controls.
     */
    fun onHardwareKeyEvent(keyCode: Int, isDown: Boolean, repeatCount: Int): Boolean {
        if (isDown && repeatCount == 0) {
            if (keyCode == KeyEvent.KEYCODE_ESCAPE && worldState.levelPhase != LevelPhase.MAIN_MENU) {
                openMainMenu()
                return true
            }
            when (worldState.levelPhase) {
                LevelPhase.MAIN_MENU -> {
                    when (keyCode) {
                        KeyEvent.KEYCODE_1, KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_SPACE -> {
                            startLevel(1)
                            return true
                        }
                        KeyEvent.KEYCODE_2 -> {
                            startLevel(2)
                            return true
                        }
                        KeyEvent.KEYCODE_3 -> {
                            startLevel(3)
                            return true
                        }
                    }
                    return false
                }
                LevelPhase.LEVEL_COMPLETE -> {
                    if ((keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_SPACE) &&
                        worldState.levelNumber < 3
                    ) {
                        nextLevel()
                        return true
                    }
                    if (keyCode == KeyEvent.KEYCODE_R || keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_SPACE) {
                        restartLevel()
                        return true
                    }
                }
                LevelPhase.GAME_OVER -> {
                    if (keyCode == KeyEvent.KEYCODE_R || keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_SPACE) {
                        restartLevel()
                        return true
                    }
                }
                LevelPhase.PLAYING -> Unit
            }
        }
        return when (keyCode) {
            KeyEvent.KEYCODE_A, KeyEvent.KEYCODE_DPAD_LEFT -> {
                keyLeft = isDown
                true
            }
            KeyEvent.KEYCODE_D, KeyEvent.KEYCODE_DPAD_RIGHT -> {
                keyRight = isDown
                true
            }
            KeyEvent.KEYCODE_SPACE, KeyEvent.KEYCODE_W, KeyEvent.KEYCODE_DPAD_UP -> {
                if (isDown && repeatCount == 0 && !keyJump) {
                    queuedJump = true
                }
                keyJump = isDown
                true
            }
            KeyEvent.KEYCODE_Z, KeyEvent.KEYCODE_J -> {
                if (isDown && repeatCount == 0) queuedPistol = true
                true
            }
            KeyEvent.KEYCODE_X, KeyEvent.KEYCODE_K -> {
                if (isDown && repeatCount == 0) queuedGatling = true
                true
            }
            KeyEvent.KEYCODE_C, KeyEvent.KEYCODE_L -> {
                if (isDown && repeatCount == 0) queuedAxe = true
                true
            }
            KeyEvent.KEYCODE_G, KeyEvent.KEYCODE_V -> {
                if (isDown && repeatCount == 0) queuedGear4 = true
                true
            }
            KeyEvent.KEYCODE_TAB -> {
                if (isDown && repeatCount == 0) queuedSwitchHero = true
                true
            }
            KeyEvent.KEYCODE_1 -> {
                if (isDown && repeatCount == 0) queuedSelectLuffy = true
                true
            }
            KeyEvent.KEYCODE_2 -> {
                if (isDown && repeatCount == 0) queuedSelectZoro = true
                true
            }
            KeyEvent.KEYCODE_3 -> {
                if (isDown && repeatCount == 0) queuedSelectSanji = true
                true
            }
            KeyEvent.KEYCODE_4 -> {
                if (isDown && repeatCount == 0) queuedSelectNami = true
                true
            }
            KeyEvent.KEYCODE_5 -> {
                if (isDown && repeatCount == 0) queuedSelectUsopp = true
                true
            }
            KeyEvent.KEYCODE_6 -> {
                if (isDown && repeatCount == 0) queuedSelectChopper = true
                true
            }
            KeyEvent.KEYCODE_R -> {
                if (isDown && repeatCount == 0) restartLevel()
                true
            }
            KeyEvent.KEYCODE_M -> {
                if (isDown && repeatCount == 0) toggleMute()
                true
            }
            KeyEvent.KEYCODE_H -> {
                if (isDown && repeatCount == 0) toggleHitboxes()
                true
            }
            else -> false
        }
    }

    // Touch controls callbacks
    fun setTouchLeft(pressed: Boolean) {
        touchLeft = pressed
    }

    fun setTouchRight(pressed: Boolean) {
        touchRight = pressed
    }

    fun setTouchJump(pressed: Boolean) {
        if (pressed && !touchJump) {
            queuedJump = true
        }
        touchJump = pressed
    }

    fun triggerPistol() {
        queuedPistol = true
    }

    fun triggerGatling() {
        queuedGatling = true
    }

    fun triggerAxe() {
        queuedAxe = true
    }

    fun triggerGear4() {
        queuedGear4 = true
    }

    fun switchHero() {
        queuedSwitchHero = true
    }

    fun selectHero(hero: PlayableHero) {
        when (hero) {
            PlayableHero.LUFFY -> queuedSelectLuffy = true
            PlayableHero.ZORO -> queuedSelectZoro = true
            PlayableHero.SANJI -> queuedSelectSanji = true
            PlayableHero.NAMI -> queuedSelectNami = true
            PlayableHero.USOPP -> queuedSelectUsopp = true
            PlayableHero.CHOPPER -> queuedSelectChopper = true
        }
    }

    fun toggleMute() {
        val next = !soundEngine.isMuted
        soundEngine.isMuted = next
        _hudState.update { it.copy(isMuted = next) }
    }

    fun toggleHitboxes() {
        engine.toggleHitboxes()
        worldState = engine.state
        _hudState.update { it.copy(showHitboxes = engine.state.showHitboxes) }
    }

    fun toggleTouchControls() {
        _hudState.update { it.copy(showTouchControls = !it.showTouchControls) }
    }

    override fun onCleared() {
        super.onCleared()
        soundEngine.release()
    }
}
