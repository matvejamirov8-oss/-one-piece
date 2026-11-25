package com.example.game.ui

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.game.model.ArlongStagePhase
import com.example.game.model.AttackType
import com.example.game.model.CooldownState
import com.example.game.model.HudUiState
import com.example.game.model.LevelPhase
import com.example.game.model.PlayableHero
import com.example.game.sprites.ChopperSpriteSheet
import com.example.game.sprites.LuffySpriteSheet
import com.example.game.sprites.NamiSpriteSheet
import com.example.game.sprites.SanjiSpriteSheet
import com.example.game.sprites.UsoppSpriteSheet
import com.example.game.sprites.ZoroSpriteSheet
import com.example.ui.theme.CooldownTint
import com.example.ui.theme.HudTextLight
import com.example.ui.theme.HudTextMuted
import com.example.ui.theme.LuffyRed
import com.example.ui.theme.PixelBorderLight
import com.example.ui.theme.PixelInk
import com.example.ui.theme.PixelPanelBg
import com.example.ui.theme.PixelPanelDark
import com.example.ui.theme.ReadyGreen
import com.example.ui.theme.StrawGold
import com.example.ui.theme.StrawGoldShadow
import com.example.ui.theme.SurfCyan
import java.util.Locale
import kotlin.math.hypot

private val ZoroEmerald = Color(0xFF38C958)
private val ZoroMint = Color(0xFF7AF29B)
private val ZoroEmeraldDark = Color(0xFF1A6630)
private val SanjiFlame = Color(0xFFFF8A1F)
private val SanjiBlue = Color(0xFF4C9BFF)
private val SanjiDark = Color(0xFF1E3A6B)
private val NamiCyan = Color(0xFF5CE1FF)
private val NamiOrange = Color(0xFFFF9238)
private val NamiDark = Color(0xFF163D6B)
private val UsoppGold = Color(0xFFE8C547)
private val UsoppOlive = Color(0xFF7BB858)
private val UsoppDark = Color(0xFF473318)
private val ChopperPink = Color(0xFFFF78B4)
private val ChopperMint = Color(0xFF4CF57E)
private val ChopperDark = Color(0xFF5A1D3D)
private val Gear4Crimson = Color(0xFFE62E4D)
private val Gear4Gold = Color(0xFFFFD84D)
private val Gear4Obsidian = Color(0xFF241B33)
private val ZoroHellGreen = Color(0xFF39FF88)
private val ZoroHellObsidian = Color(0xFF10151F)
private val ZoroAsuraViolet = Color(0xFFB55CFF)

private fun heroAccentColor(hero: PlayableHero): Color = when (hero) {
    PlayableHero.LUFFY -> StrawGold
    PlayableHero.ZORO -> ZoroEmerald
    PlayableHero.SANJI -> SanjiFlame
    PlayableHero.NAMI -> NamiCyan
    PlayableHero.USOPP -> UsoppGold
    PlayableHero.CHOPPER -> ChopperPink
}

@Composable
fun PixelHudOverlay(
    hud: HudUiState,
    sprites: LuffySpriteSheet,
    zoroSprites: ZoroSpriteSheet,
    sanjiSprites: SanjiSpriteSheet,
    namiSprites: NamiSpriteSheet,
    usoppSprites: UsoppSpriteSheet,
    chopperSprites: ChopperSpriteSheet,
    isCompactWidth: Boolean,
    onToggleMute: () -> Unit,
    onToggleHitboxes: () -> Unit,
    onToggleTouchControls: () -> Unit,
    onLeftChanged: (Boolean) -> Unit,
    onRightChanged: (Boolean) -> Unit,
    onJumpChanged: (Boolean) -> Unit,
    onPistolClick: () -> Unit,
    onGatlingClick: () -> Unit,
    onAxeClick: () -> Unit,
    onGear4Click: () -> Unit,
    onSwitchHero: () -> Unit,
    onSelectHero: (PlayableHero) -> Unit,
    onRestartLevel: () -> Unit,
    onStartLevel: (Int) -> Unit,
    onNextLevel: () -> Unit,
    onOpenMainMenu: () -> Unit
) {
    val isMainMenu = hud.levelPhase == LevelPhase.MAIN_MENU
    val isPlaying = hud.levelPhase == LevelPhase.PLAYING

    val isGear4Luffy = hud.activeHero == PlayableHero.LUFFY && hud.gear4Active
    val isZoroHell = hud.activeHero == PlayableHero.ZORO && hud.zoroHellActive

    fun portraitFor(hero: PlayableHero): ImageBitmap = when (hero) {
        PlayableHero.LUFFY -> if (hud.gear4Active) sprites.hudPortraitBoundman else sprites.hudPortrait
        PlayableHero.ZORO -> zoroSprites.hudPortrait
        PlayableHero.SANJI -> sanjiSprites.hudPortrait
        PlayableHero.NAMI -> namiSprites.hudPortrait
        PlayableHero.USOPP -> usoppSprites.hudPortrait
        PlayableHero.CHOPPER -> chopperSprites.hudPortrait
    }

    val activePortrait = portraitFor(hud.activeHero)
    val standbyPortrait = portraitFor(hud.standbyHero)

    val skill1Icon = when (hud.activeHero) {
        PlayableHero.LUFFY -> if (isGear4Luffy) sprites.iconKongGun else sprites.iconPistol
        PlayableHero.ZORO -> zoroSprites.iconSlash
        PlayableHero.SANJI -> sanjiSprites.iconKick
        PlayableHero.NAMI -> namiSprites.iconSpark
        PlayableHero.USOPP -> usoppSprites.iconShot
        PlayableHero.CHOPPER -> chopperSprites.iconKungFu
    }
    val skill2Icon = when (hud.activeHero) {
        PlayableHero.LUFFY -> if (isGear4Luffy) sprites.iconKongOrgan else sprites.iconGatling
        PlayableHero.ZORO -> zoroSprites.iconOnigiri
        PlayableHero.SANJI -> sanjiSprites.iconPartyTable
        PlayableHero.NAMI -> namiSprites.iconCyclone
        PlayableHero.USOPP -> usoppSprites.iconExplodingStar
        PlayableHero.CHOPPER -> chopperSprites.iconGuardHeal
    }
    val skill3Icon = when (hud.activeHero) {
        PlayableHero.LUFFY -> if (isGear4Luffy) sprites.iconKingKongGun else sprites.iconAxe
        PlayableHero.ZORO -> zoroSprites.iconTatsumaki
        PlayableHero.SANJI -> sanjiSprites.iconDiableJambe
        PlayableHero.NAMI -> namiSprites.iconThunderbolt
        PlayableHero.USOPP -> usoppSprites.iconFirebird
        PlayableHero.CHOPPER -> chopperSprites.iconMonsterPoint
    }

    val skill1Label = stringResource(
        when (hud.activeHero) {
            PlayableHero.LUFFY -> if (isGear4Luffy) R.string.skill_gear4_kong_gun_short else R.string.skill_pistol_short
            PlayableHero.ZORO -> if (isZoroHell) R.string.skill_zoro_hell_slash_short else R.string.skill_zoro_slash_short
            PlayableHero.SANJI -> R.string.skill_sanji_kick_short
            PlayableHero.NAMI -> R.string.skill_nami_spark_short
            PlayableHero.USOPP -> R.string.skill_usopp_shot_short
            PlayableHero.CHOPPER -> R.string.skill_chopper_kungfu_short
        }
    )
    val skill2Label = stringResource(
        when (hud.activeHero) {
            PlayableHero.LUFFY -> if (isGear4Luffy) R.string.skill_gear4_kong_organ_short else R.string.skill_gatling_short
            PlayableHero.ZORO -> if (isZoroHell) R.string.skill_zoro_purgatory_short else R.string.skill_zoro_onigiri_short
            PlayableHero.SANJI -> R.string.skill_sanji_party_short
            PlayableHero.NAMI -> R.string.skill_nami_cyclone_short
            PlayableHero.USOPP -> R.string.skill_usopp_exploding_short
            PlayableHero.CHOPPER -> R.string.skill_chopper_guard_short
        }
    )
    val skill3Label = stringResource(
        when (hud.activeHero) {
            PlayableHero.LUFFY -> if (isGear4Luffy) R.string.skill_gear4_king_kong_short else R.string.skill_axe_short
            PlayableHero.ZORO -> if (isZoroHell) R.string.skill_zoro_asura_short else R.string.skill_zoro_tatsumaki_short
            PlayableHero.SANJI -> R.string.skill_sanji_diable_short
            PlayableHero.NAMI -> R.string.skill_nami_thunder_short
            PlayableHero.USOPP -> R.string.skill_usopp_firebird_short
            PlayableHero.CHOPPER -> R.string.skill_chopper_monster_short
        }
    )

    val skill1Desc = stringResource(
        when (hud.activeHero) {
            PlayableHero.LUFFY -> if (isGear4Luffy) R.string.cd_attack_gear4_kong_gun else R.string.cd_attack_pistol
            PlayableHero.ZORO -> if (isZoroHell) R.string.cd_attack_zoro_hell_slash else R.string.cd_attack_zoro_slash
            PlayableHero.SANJI -> R.string.cd_attack_sanji_kick
            PlayableHero.NAMI -> R.string.cd_attack_nami_spark
            PlayableHero.USOPP -> R.string.cd_attack_usopp_shot
            PlayableHero.CHOPPER -> R.string.cd_attack_chopper_kungfu
        }
    )
    val skill2Desc = stringResource(
        when (hud.activeHero) {
            PlayableHero.LUFFY -> if (isGear4Luffy) R.string.cd_attack_gear4_kong_organ else R.string.cd_attack_gatling
            PlayableHero.ZORO -> if (isZoroHell) R.string.cd_attack_zoro_purgatory else R.string.cd_attack_zoro_onigiri
            PlayableHero.SANJI -> R.string.cd_attack_sanji_party
            PlayableHero.NAMI -> R.string.cd_attack_nami_cyclone
            PlayableHero.USOPP -> R.string.cd_attack_usopp_exploding
            PlayableHero.CHOPPER -> R.string.cd_attack_chopper_guard
        }
    )
    val skill3Desc = stringResource(
        when (hud.activeHero) {
            PlayableHero.LUFFY -> if (isGear4Luffy) R.string.cd_attack_gear4_king_kong else R.string.cd_attack_axe
            PlayableHero.ZORO -> if (isZoroHell) R.string.cd_attack_zoro_asura else R.string.cd_attack_zoro_tatsumaki
            PlayableHero.SANJI -> R.string.cd_attack_sanji_diable
            PlayableHero.NAMI -> R.string.cd_attack_nami_thunder
            PlayableHero.USOPP -> R.string.cd_attack_usopp_firebird
            PlayableHero.CHOPPER -> R.string.cd_attack_chopper_monster
        }
    )

    val skill1Accent = when (hud.activeHero) {
        PlayableHero.LUFFY -> if (isGear4Luffy) Gear4Crimson else StrawGold
        PlayableHero.ZORO -> if (isZoroHell) ZoroHellGreen else ZoroEmerald
        PlayableHero.SANJI -> SanjiBlue
        PlayableHero.NAMI -> NamiCyan
        PlayableHero.USOPP -> UsoppOlive
        PlayableHero.CHOPPER -> ChopperPink
    }
    val skill2Accent = when (hud.activeHero) {
        PlayableHero.LUFFY -> if (isGear4Luffy) Color(0xFFFF6B81) else SurfCyan
        PlayableHero.ZORO -> if (isZoroHell) ZoroHellGreen else ZoroMint
        PlayableHero.SANJI -> StrawGold
        PlayableHero.NAMI -> NamiOrange
        PlayableHero.USOPP -> SanjiFlame
        PlayableHero.CHOPPER -> ChopperMint
    }
    val skill3Accent = when (hud.activeHero) {
        PlayableHero.LUFFY -> if (isGear4Luffy) Gear4Gold else LuffyRed
        PlayableHero.ZORO -> if (isZoroHell) ZoroAsuraViolet else Color(0xFF2EE6A6)
        PlayableHero.SANJI -> SanjiFlame
        PlayableHero.NAMI -> StrawGold
        PlayableHero.USOPP -> LuffyRed
        PlayableHero.CHOPPER -> StrawGold
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // === LEFT HALF: FLOATING VIRTUAL JOYSTICK TOUCH ZONE ===
        if (hud.showTouchControls && isPlaying) {
            FloatingPixelJoystickZone(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth(0.45f)
                    .fillMaxHeight(0.72f)
                    .displayCutoutPadding()
                    .navigationBarsPadding(),
                onLeftChanged = onLeftChanged,
                onRightChanged = onRightChanged
            )
        }

        // === TOP PIXEL HUD BAR (hidden while Main Menu is shown) ===
        if (!isMainMenu) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .displayCutoutPadding()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Top-Left: Active Hero Combat Badge + Direct 5-Hero Crew Selector Strip
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    HeroCombatBadge(
                        activeHero = hud.activeHero,
                        portrait = activePortrait,
                        standbyHero = hud.standbyHero,
                        standbyPortrait = standbyPortrait,
                        standbyHp = hud.standbyHp,
                        standbyMaxHp = hud.standbyMaxHp,
                        hp = hud.luffyHp,
                        maxHp = hud.luffyMaxHp,
                        gear4Meter = hud.gear4Meter,
                        gear4Active = hud.gear4Active,
                        gear4Timer = hud.gear4Timer,
                        zoroHellMeter = hud.zoroHellMeter,
                        zoroHellActive = hud.zoroHellActive,
                        zoroHellTimer = hud.zoroHellTimer,
                        zoroParryCritReady = hud.zoroParryCritReady,
                        levelLabel = hud.levelLabel,
                        waveLabel = hud.waveLabel,
                        berries = hud.berries,
                        compact = isCompactWidth,
                        onSwitchHero = onSwitchHero,
                        onGear4Click = onGear4Click
                    )
                    CrewRosterStrip(
                        activeHero = hud.activeHero,
                        heroHpMap = hud.heroHpMap,
                        portraitFor = ::portraitFor,
                        compact = isCompactWidth,
                        onSelectHero = onSelectHero
                    )
                }

                // Top-Right: Ability Cooldown Slots + Utility Toggles
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    PixelAbilitySlot(
                        icon = skill1Icon,
                        keyHint = "Z/J",
                        label = skill1Label,
                        cooldown = hud.pistolCooldown,
                        isActive = hud.activeAttack == AttackType.PISTOL ||
                            hud.activeAttack == AttackType.GEAR4_KONG_GUN ||
                            hud.activeAttack == AttackType.ZORO_SLASH ||
                            hud.activeAttack == AttackType.ZORO_HELL_SLASH ||
                            hud.activeAttack == AttackType.SANJI_KICK ||
                            hud.activeAttack == AttackType.NAMI_SPARK ||
                            hud.activeAttack == AttackType.USOPP_SHOT ||
                            hud.activeAttack == AttackType.CHOPPER_KUNGFU,
                        accentColor = skill1Accent,
                        compact = isCompactWidth,
                        testTag = "hud_skill_pistol",
                        contentDesc = skill1Desc,
                        onClick = onPistolClick
                    )
                    PixelAbilitySlot(
                        icon = skill2Icon,
                        keyHint = "X/K",
                        label = skill2Label,
                        cooldown = hud.gatlingCooldown,
                        isActive = hud.activeAttack == AttackType.GATLING ||
                            hud.activeAttack == AttackType.GEAR4_KONG_ORGAN ||
                            hud.activeAttack == AttackType.ZORO_ONIGIRI ||
                            hud.activeAttack == AttackType.ZORO_PURGATORY_ONIGIRI ||
                            hud.activeAttack == AttackType.SANJI_PARTY_TABLE ||
                            hud.activeAttack == AttackType.NAMI_CYCLONE ||
                            hud.activeAttack == AttackType.USOPP_EXPLODING_STAR ||
                            hud.activeAttack == AttackType.CHOPPER_GUARD_HEAL,
                        accentColor = skill2Accent,
                        compact = isCompactWidth,
                        testTag = "hud_skill_gatling",
                        contentDesc = skill2Desc,
                        onClick = onGatlingClick
                    )
                    PixelAbilitySlot(
                        icon = skill3Icon,
                        keyHint = "C/L",
                        label = skill3Label,
                        cooldown = hud.axeCooldown,
                        isActive = hud.activeAttack == AttackType.AXE ||
                            hud.activeAttack == AttackType.GEAR4_KING_KONG_GUN ||
                            hud.activeAttack == AttackType.ZORO_TATSUMAKI ||
                            hud.activeAttack == AttackType.ZORO_ASURA ||
                            hud.activeAttack == AttackType.SANJI_DIABLE_JAMBE ||
                            hud.activeAttack == AttackType.NAMI_THUNDERBOLT ||
                            hud.activeAttack == AttackType.USOPP_FIREBIRD_STAR ||
                            hud.activeAttack == AttackType.CHOPPER_MONSTER_POINT,
                        accentColor = skill3Accent,
                        compact = isCompactWidth,
                        testTag = "hud_skill_axe",
                        contentDesc = skill3Desc,
                        onClick = onAxeClick
                    )

                    if (!isCompactWidth) {
                        Spacer(modifier = Modifier.width(2.dp))
                        HudUtilityButtonsRow(
                            isMuted = hud.isMuted,
                            showHitboxes = hud.showHitboxes,
                            showTouchControls = hud.showTouchControls,
                            onToggleMute = onToggleMute,
                            onToggleHitboxes = onToggleHitboxes,
                            onToggleTouchControls = onToggleTouchControls,
                            onOpenMainMenu = onOpenMainMenu
                        )
                    }
                }
            }

            if (isCompactWidth) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    HudUtilityButtonsRow(
                        isMuted = hud.isMuted,
                        showHitboxes = hud.showHitboxes,
                        showTouchControls = hud.showTouchControls,
                        onToggleMute = onToggleMute,
                        onToggleHitboxes = onToggleHitboxes,
                        onToggleTouchControls = onToggleTouchControls,
                        onOpenMainMenu = onOpenMainMenu
                    )
                }
            }

            // === WAVE / BOSS INTRO BANNER ===
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val waveBanner = hud.waveBanner
                androidx.compose.animation.AnimatedVisibility(
                    visible = waveBanner != null && isPlaying,
                    enter = fadeIn() + scaleIn(initialScale = 0.85f),
                    exit = fadeOut() + scaleOut(targetScale = 0.92f)
                ) {
                    waveBanner?.let { wb ->
                        val accent = if (wb.isBoss) LuffyRed else StrawGold
                        Box(
                            modifier = Modifier
                                .background(PixelPanelDark)
                                .border(2.dp, PixelInk)
                                .padding(2.dp)
                                .border(2.dp, accent)
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = wb.text,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = if (isCompactWidth) 10.sp else 12.sp
                                ),
                                color = accent,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // === ANIME MOVE CALLOUT POP-UP ===
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val activeBanner = hud.banner
                androidx.compose.animation.AnimatedVisibility(
                    visible = activeBanner != null && hud.waveBanner == null && isPlaying,
                    enter = fadeIn() + scaleIn(initialScale = 0.85f),
                    exit = fadeOut() + scaleOut(targetScale = 0.92f)
                ) {
                    activeBanner?.let { banner ->
                        val (textRes, accent) = when (banner.attackType) {
                            AttackType.PISTOL -> R.string.callout_pistol to StrawGold
                            AttackType.GATLING -> R.string.callout_gatling to SurfCyan
                            AttackType.AXE -> R.string.callout_axe to LuffyRed
                            AttackType.ZORO_SLASH -> R.string.callout_zoro_slash to ZoroEmerald
                            AttackType.ZORO_ONIGIRI -> R.string.callout_zoro_onigiri to ZoroMint
                            AttackType.ZORO_TATSUMAKI -> R.string.callout_zoro_tatsumaki to Color(0xFF2EE6A6)
                            AttackType.ZORO_HELL_SLASH -> R.string.callout_zoro_hell_slash to ZoroHellGreen
                            AttackType.ZORO_PURGATORY_ONIGIRI -> R.string.callout_zoro_purgatory to ZoroHellGreen
                            AttackType.ZORO_ASURA -> R.string.callout_zoro_asura to ZoroAsuraViolet
                            AttackType.SANJI_KICK -> R.string.callout_sanji_kick to SanjiBlue
                            AttackType.SANJI_PARTY_TABLE -> R.string.callout_sanji_party to StrawGold
                            AttackType.SANJI_DIABLE_JAMBE -> R.string.callout_sanji_diable to SanjiFlame
                            AttackType.NAMI_SPARK -> R.string.callout_nami_spark to NamiCyan
                            AttackType.NAMI_CYCLONE -> R.string.callout_nami_cyclone to SurfCyan
                            AttackType.NAMI_THUNDERBOLT -> R.string.callout_nami_thunder to StrawGold
                            AttackType.USOPP_SHOT -> R.string.callout_usopp_shot to UsoppGold
                            AttackType.USOPP_EXPLODING_STAR -> R.string.callout_usopp_exploding to SanjiFlame
                            AttackType.USOPP_FIREBIRD_STAR -> R.string.callout_usopp_firebird to LuffyRed
                            AttackType.CHOPPER_KUNGFU -> R.string.callout_chopper_kungfu to ChopperPink
                            AttackType.CHOPPER_GUARD_HEAL -> R.string.callout_chopper_guard to ChopperMint
                            AttackType.CHOPPER_MONSTER_POINT -> R.string.callout_chopper_monster to StrawGold
                            AttackType.GEAR4_KONG_GUN -> R.string.callout_gear4_kong_gun to Gear4Crimson
                            AttackType.GEAR4_KONG_ORGAN -> R.string.callout_gear4_kong_organ to Color(0xFFFF6B81)
                            AttackType.GEAR4_KING_KONG_GUN -> R.string.callout_gear4_king_kong to Gear4Gold
                        }
                        Box(
                            modifier = Modifier
                                .background(PixelPanelDark)
                                .border(2.dp, PixelInk)
                                .padding(2.dp)
                                .border(1.5.dp, accent)
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = stringResource(textRes),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = if (isCompactWidth) 9.sp else 10.sp
                                ),
                                color = accent,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // === LEVEL 3: LUFFY DROWNING 5-SECOND AIR TIMER ALERT ===
            if (isPlaying && hud.levelNumber == 3 && hud.luffyDrowning) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    val ratio = (hud.luffyDrownTimer / 5.0f).coerceIn(0f, 1f)
                    val secs = String.format(Locale.US, "%.1f", hud.luffyDrownTimer.coerceAtLeast(0f))
                    val urgentColor = if (hud.luffyDrownTimer < 2.2f) LuffyRed else Color(0xFF47DBFF)
                    Column(
                        modifier = Modifier
                            .testTag("luffy_drowning_alert")
                            .background(PixelPanelDark.copy(alpha = 0.94f))
                            .border(2.dp, PixelInk)
                            .padding(2.dp)
                            .border(1.5.dp, urgentColor)
                            .padding(horizontal = 12.dp, vertical = 5.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            text = if (hud.activeHero == PlayableHero.LUFFY) {
                                "ЛУФФИ ТОНЕТ! (${secs}с) — СМЕНИ ГЕРОЯ (TAB / 2-6) И СПАСИ!"
                            } else {
                                "СПАСИ ЛУФФИ ИЗ БАССЕЙНА! ВОЗДУХ: ${secs}с"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = if (isCompactWidth) 8.sp else 10.sp
                            ),
                            color = urgentColor,
                            maxLines = 1
                        )
                        Box(
                            modifier = Modifier
                                .width(if (isCompactWidth) 170.dp else 220.dp)
                                .height(7.dp)
                                .background(Color(0xFF0A2442))
                                .border(1.dp, PixelInk)
                                .padding(1.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(ratio)
                                    .background(urgentColor)
                            )
                        }
                    }
                }
            }

            // === LEVEL 3 MAP ROOM: TOWER DESTRUCTION METER ===
            if (isPlaying && hud.levelNumber == 3 &&
                (hud.arlongPhase == ArlongStagePhase.ARLONG_PHASE2 ||
                    hud.arlongPhase == ArlongStagePhase.FINISHER_SLOWMO ||
                    hud.arlongPhase == ArlongStagePhase.TOWER_COLLAPSE)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    val pct = (hud.towerDestruction * 100f).toInt().coerceIn(0, 100)
                    Row(
                        modifier = Modifier
                            .testTag("tower_destruction_bar")
                            .background(PixelPanelDark.copy(alpha = 0.90f))
                            .border(1.5.dp, PixelInk)
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "РАЗРУШЕНИЕ БАШНИ: $pct%",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = if (isCompactWidth) 8.sp else 9.sp
                            ),
                            color = StrawGold,
                            maxLines = 1
                        )
                        Box(
                            modifier = Modifier
                                .width(if (isCompactWidth) 80.dp else 110.dp)
                                .height(7.dp)
                                .background(Color(0xFF2B1218))
                                .border(1.dp, PixelInk)
                                .padding(1.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(hud.towerDestruction.coerceIn(0f, 1f))
                                    .background(if (pct >= 80) LuffyRed else SanjiFlame)
                            )
                        }
                    }
                }
            }

            // === LEVEL 3 FINISHER SLOW-MO CTA: RUBBER BATTLE AXE! ===
            if (isPlaying && hud.levelNumber == 3 && hud.arlongPhase == ArlongStagePhase.FINISHER_SLOWMO) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("arlong_finisher_axe_cta")
                            .background(LuffyRed)
                            .border(2.5.dp, PixelInk)
                            .padding(2.dp)
                            .border(2.dp, StrawGold)
                            .clickable(onClick = onAxeClick)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "ДОБЕЙ РЕЗИНОВЫМ ТОПОРОМ! (C / НАЖМИ)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = if (isCompactWidth) 10.sp else 12.sp
                            ),
                            color = Color.White,
                            maxLines = 1
                        )
                    }
                }
            }
        }
        } // end !isMainMenu top HUD bar

        // === BOTTOM-RIGHT ARCADE TOUCH CONTROLS ===
        if (hud.showTouchControls && isPlaying) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .displayCutoutPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.End
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (hud.activeHero == PlayableHero.LUFFY) {
                        PixelGear4Pad(
                            meter = hud.gear4Meter,
                            isActive = hud.gear4Active,
                            timer = hud.gear4Timer,
                            portrait = sprites.hudPortraitBoundman,
                            size = if (isCompactWidth) 48.dp else 52.dp,
                            contentDesc = stringResource(R.string.cd_activate_gear4),
                            testTag = "touch_activate_gear4",
                            onTrigger = onGear4Click
                        )
                    } else if (hud.activeHero == PlayableHero.ZORO) {
                        PixelZoroHellPad(
                            meter = hud.zoroHellMeter,
                            isActive = hud.zoroHellActive,
                            timer = hud.zoroHellTimer,
                            portrait = zoroSprites.hudPortrait,
                            size = if (isCompactWidth) 48.dp else 52.dp,
                            contentDesc = stringResource(R.string.cd_activate_zoro_hell),
                            testTag = "touch_activate_zoro_hell",
                            onTrigger = onGear4Click
                        )
                    }
                    PixelHeroSwitchPad(
                        standbyHero = hud.standbyHero,
                        standbyPortrait = standbyPortrait,
                        standbyHp = hud.standbyHp,
                        standbyMaxHp = hud.standbyMaxHp,
                        size = if (isCompactWidth) 48.dp else 52.dp,
                        contentDesc = stringResource(R.string.cd_switch_hero),
                        testTag = "touch_switch_hero",
                        onSwitch = onSwitchHero
                    )
                }
                Spacer(modifier = Modifier.height(if (isCompactWidth) 16.dp else 22.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PixelActionPad(
                        keyLabel = "X",
                        icon = skill2Icon,
                        cooldown = hud.gatlingCooldown,
                        accentColor = skill2Accent,
                        size = if (isCompactWidth) 48.dp else 54.dp,
                        contentDesc = skill2Desc,
                        testTag = "touch_attack_gatling",
                        onTrigger = onGatlingClick
                    )
                    PixelActionPad(
                        keyLabel = "C",
                        icon = skill3Icon,
                        cooldown = hud.axeCooldown,
                        accentColor = skill3Accent,
                        size = if (isCompactWidth) 48.dp else 54.dp,
                        contentDesc = skill3Desc,
                        testTag = "touch_attack_axe",
                        onTrigger = onAxeClick
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PixelActionPad(
                        keyLabel = "Z",
                        icon = skill1Icon,
                        cooldown = hud.pistolCooldown,
                        accentColor = skill1Accent,
                        size = if (isCompactWidth) 54.dp else 60.dp,
                        contentDesc = skill1Desc,
                        testTag = "touch_attack_pistol",
                        onTrigger = onPistolClick
                    )
                    PixelHoldButton(
                        icon = Icons.Filled.KeyboardArrowUp,
                        keyLabel = "SPC",
                        size = if (isCompactWidth) 54.dp else 60.dp,
                        accentColor = ReadyGreen,
                        contentDesc = stringResource(R.string.cd_jump),
                        testTag = "touch_jump",
                        onPressedChanged = onJumpChanged
                    )
                }
            }
        }

        // === BOTTOM-CENTER CAPTAIN MORGAN BOSS HP BAR & HINT STRIP ===
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val bossName = hud.bossName
            if (bossName != null && isPlaying) {
                BossHealthBar(
                    bossName = bossName,
                    hp = hud.bossHp,
                    maxHp = hud.bossMaxHp,
                    compact = isCompactWidth
                )
            }

            if (!hud.showTouchControls && isPlaying) {
                Box(
                    modifier = Modifier
                        .background(PixelPanelDark.copy(alpha = 0.82f))
                        .border(1.5.dp, PixelInk)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.controls_hint_keyboard),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = if (isCompactWidth) 8.sp else 10.sp
                        ),
                        color = HudTextMuted,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // === MAIN MENU / GAME OVER / LEVEL COMPLETE OVERLAYS ===
        if (isMainMenu) {
            MainMenuOverlay(
                luffySprites = sprites,
                zoroSprites = zoroSprites,
                sanjiSprites = sanjiSprites,
                namiSprites = namiSprites,
                usoppSprites = usoppSprites,
                chopperSprites = chopperSprites,
                compact = isCompactWidth,
                onSelectLevel = onStartLevel
            )
        } else if (!isPlaying) {
            LevelResultOverlay(
                isVictory = hud.levelPhase == LevelPhase.LEVEL_COMPLETE,
                levelNumber = hud.levelNumber,
                berries = hud.berries,
                defeatedCount = hud.defeatedCount,
                compact = isCompactWidth,
                onNextLevel = onNextLevel,
                onRestart = onRestartLevel,
                onOpenMenu = onOpenMainMenu
            )
        }
    }
}

@Composable
private fun CrewRosterStrip(
    activeHero: PlayableHero,
    heroHpMap: Map<PlayableHero, Int>,
    portraitFor: (PlayableHero) -> ImageBitmap,
    compact: Boolean,
    onSelectHero: (PlayableHero) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlayableHero.entries.forEachIndexed { idx, hero ->
            val hp = heroHpMap[hero] ?: hero.maxHp
            val isAlive = hp > 0
            val isActive = hero == activeHero
            val accent = heroAccentColor(hero)
            val ratio = (hp.toFloat() / hero.maxHp.coerceAtLeast(1)).coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .testTag("hud_crew_${hero.name.lowercase()}")
                    .semantics { contentDescription = "Выбрать ${hero.displayName}" }
                    .background(
                        when {
                            isActive -> accent.copy(alpha = 0.28f)
                            isAlive -> PixelPanelDark.copy(alpha = 0.88f)
                            else -> PixelPanelDark.copy(alpha = 0.40f)
                        }
                    )
                    .border(
                        width = if (isActive) 2.dp else 1.dp,
                        color = when {
                            isActive -> accent
                            isAlive -> PixelBorderLight.copy(alpha = 0.55f)
                            else -> PixelInk
                        }
                    )
                    .clickable(enabled = isAlive && !isActive) { onSelectHero(hero) }
                    .padding(horizontal = 3.dp, vertical = 2.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    Box {
                        PixelBitmapIcon(
                            bitmap = portraitFor(hero),
                            modifier = Modifier.size(if (compact) 18.dp else 22.dp)
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .background(PixelInk.copy(alpha = 0.85f))
                                .padding(horizontal = 2.dp)
                        ) {
                            Text(
                                text = "${idx + 1}",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 6.sp),
                                color = if (isActive) accent else HudTextLight
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .width(if (compact) 18.dp else 22.dp)
                            .height(2.5.dp)
                            .background(Color(0xFF2B1218))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(ratio)
                                .background(if (ratio > 0.25f) accent else LuffyRed)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroCombatBadge(
    activeHero: PlayableHero,
    portrait: ImageBitmap,
    standbyHero: PlayableHero,
    standbyPortrait: ImageBitmap,
    standbyHp: Int,
    standbyMaxHp: Int,
    hp: Int,
    maxHp: Int,
    gear4Meter: Float,
    gear4Active: Boolean,
    gear4Timer: Float,
    zoroHellMeter: Float,
    zoroHellActive: Boolean,
    zoroHellTimer: Float,
    zoroParryCritReady: Boolean,
    levelLabel: String,
    waveLabel: String,
    berries: Int,
    compact: Boolean,
    onSwitchHero: () -> Unit,
    onGear4Click: () -> Unit
) {
    val isGear4Luffy = activeHero == PlayableHero.LUFFY && gear4Active
    val isZoroHell = activeHero == PlayableHero.ZORO && zoroHellActive
    val heroAccent = when {
        isGear4Luffy -> Gear4Crimson
        isZoroHell -> ZoroHellGreen
        else -> heroAccentColor(activeHero)
    }
    val heroBorderShadow = when (activeHero) {
        PlayableHero.LUFFY -> if (isGear4Luffy) Gear4Crimson else StrawGoldShadow
        PlayableHero.ZORO -> if (isZoroHell) ZoroHellGreen else ZoroEmeraldDark
        PlayableHero.SANJI -> SanjiDark
        PlayableHero.NAMI -> NamiDark
        PlayableHero.USOPP -> UsoppDark
        PlayableHero.CHOPPER -> ChopperDark
    }
    val portraitBg = when (activeHero) {
        PlayableHero.LUFFY -> if (isGear4Luffy) Gear4Obsidian else Color(0xFF19589E)
        PlayableHero.ZORO -> if (isZoroHell) ZoroHellObsidian else Color(0xFF14422A)
        PlayableHero.SANJI -> Color(0xFF1A2238)
        PlayableHero.NAMI -> Color(0xFF143C6E)
        PlayableHero.USOPP -> Color(0xFF3A2B18)
        PlayableHero.CHOPPER -> Color(0xFF15423E)
    }

    val hpRatio = (hp.toFloat() / maxHp.coerceAtLeast(1)).coerceIn(0f, 1f)
    val hpColor = when {
        hpRatio > 0.5f -> ReadyGreen
        hpRatio > 0.25f -> StrawGold
        else -> LuffyRed
    }

    val standbyRatio = (standbyHp.toFloat() / standbyMaxHp.coerceAtLeast(1)).coerceIn(0f, 1f)
    val standbyAccent = heroAccentColor(standbyHero)

    Row(
        modifier = Modifier
            .background(PixelPanelBg)
            .border(2.dp, PixelInk)
            .padding(2.dp)
            .border(1.dp, heroBorderShadow)
            .padding(horizontal = 6.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Box(
            modifier = Modifier
                .size(if (compact) 34.dp else 40.dp)
                .background(portraitBg)
                .border(1.5.dp, heroAccent),
            contentAlignment = Alignment.Center
        ) {
            PixelBitmapIcon(
                bitmap = portrait,
                modifier = Modifier.size(if (compact) 30.dp else 36.dp)
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when {
                        isGear4Luffy -> "ГИР 4"
                        isZoroHell -> "КОРОЛЬ АДА"
                        else -> activeHero.displayName
                    },
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = if (compact) 8.sp else 9.sp
                    ),
                    color = heroAccent,
                    maxLines = 1
                )
                Text(
                    text = "• $levelLabel • $waveLabel",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = if (compact) 7.sp else 8.sp
                    ),
                    color = StrawGold,
                    maxLines = 1
                )
                Text(
                    text = "฿$berries",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = if (compact) 8.sp else 9.sp
                    ),
                    color = Color(0xFFFFF29C),
                    maxLines = 1
                )
            }

            // Crisp Pixel Hero HP Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(if (compact) 88.dp else 118.dp)
                        .height(10.dp)
                        .testTag("luffy_hp_bar")
                        .background(Color(0xFF2B1218))
                        .border(1.5.dp, PixelInk)
                        .padding(1.5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(hpRatio)
                            .background(hpColor)
                    )
                }
                Text(
                    text = "$hp/$maxHp",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = if (compact) 8.sp else 9.sp
                    ),
                    color = HudTextLight,
                    maxLines = 1
                )
            }

            // GEAR 4 / HAKI Gauge (shown whenever Luffy is active)
            if (activeHero == PlayableHero.LUFFY) {
                val g4Ratio = (gear4Meter / 100f).coerceIn(0f, 1f)
                val g4Ready = !gear4Active && gear4Meter >= 99.9f
                val barColor = when {
                    gear4Active -> Gear4Gold
                    g4Ready -> Gear4Crimson
                    else -> Color(0xFFC92A4C)
                }
                val labelText = when {
                    gear4Active -> String.format(Locale.US, "G4 %.1fс", gear4Timer.coerceAtLeast(0f))
                    g4Ready -> "G4 [G/V]!"
                    else -> "HAKI ${gear4Meter.toInt()}%"
                }
                Row(
                    modifier = Modifier
                        .testTag("gear4_haki_bar")
                        .clickable(enabled = g4Ready, onClick = onGear4Click),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(if (compact) 88.dp else 118.dp)
                            .height(7.dp)
                            .background(Gear4Obsidian)
                            .border(1.dp, if (g4Ready || gear4Active) Gear4Gold else PixelInk)
                            .padding(1.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(g4Ratio)
                                .background(barColor)
                        )
                    }
                    Text(
                        text = labelText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = if (compact) 7.sp else 8.sp
                        ),
                        color = if (gear4Active || g4Ready) Gear4Gold else Color(0xFFFF8FA3),
                        maxLines = 1
                    )
                }
            } else if (activeHero == PlayableHero.ZORO) {
                val hellRatio = (zoroHellMeter / 100f).coerceIn(0f, 1f)
                val hellReady = !zoroHellActive && zoroHellMeter >= 99.9f
                val barColor = when {
                    zoroHellActive -> ZoroHellGreen
                    hellReady -> StrawGold
                    else -> ZoroEmerald
                }
                val labelText = when {
                    zoroHellActive -> String.format(Locale.US, "АД %.1fс", zoroHellTimer.coerceAtLeast(0f))
                    hellReady -> "АД [G/V]!"
                    else -> "ХАКИ ${zoroHellMeter.toInt()}%"
                }
                Row(
                    modifier = Modifier
                        .testTag("zoro_hell_haki_bar")
                        .clickable(enabled = hellReady, onClick = onGear4Click),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(if (compact) 88.dp else 118.dp)
                            .height(7.dp)
                            .background(ZoroHellObsidian)
                            .border(1.dp, if (hellReady || zoroHellActive) ZoroHellGreen else PixelInk)
                            .padding(1.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(hellRatio)
                                .background(barColor)
                        )
                    }
                    Text(
                        text = labelText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = if (compact) 7.sp else 8.sp
                        ),
                        color = if (zoroHellActive || hellReady) ZoroHellGreen else ZoroMint,
                        maxLines = 1
                    )
                    if (zoroParryCritReady) {
                        Text(
                            text = "⚡100% КРИТ",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = if (compact) 7.sp else 8.sp
                            ),
                            color = StrawGold,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Compact clickable Standby Partner Badge (also works with touch controls hidden)
        Column(
            modifier = Modifier
                .testTag("hud_standby_switch")
                .semantics { contentDescription = "Переключить на ${standbyHero.displayName}" }
                .background(PixelPanelDark.copy(alpha = if (standbyHp > 0) 0.92f else 0.45f))
                .border(1.5.dp, if (standbyHp > 0) standbyAccent else PixelBorderLight.copy(alpha = 0.4f))
                .clickable(enabled = standbyHp > 0, onClick = onSwitchHero)
                .padding(3.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            PixelBitmapIcon(
                bitmap = standbyPortrait,
                modifier = Modifier.size(if (compact) 18.dp else 22.dp)
            )
            Box(
                modifier = Modifier
                    .width(if (compact) 20.dp else 24.dp)
                    .height(3.dp)
                    .background(Color(0xFF2B1218))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(standbyRatio)
                        .background(if (standbyRatio > 0.25f) standbyAccent else LuffyRed)
                )
            }
            Text(
                text = "TAB",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 6.sp),
                color = if (standbyHp > 0) standbyAccent else HudTextMuted,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun PixelHeroSwitchPad(
    standbyHero: PlayableHero,
    standbyPortrait: ImageBitmap,
    standbyHp: Int,
    standbyMaxHp: Int,
    size: Dp,
    contentDesc: String,
    testTag: String,
    onSwitch: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val isStandbyAlive = standbyHp > 0
    val accentColor = heroAccentColor(standbyHero)
    val hpRatio = (standbyHp.toFloat() / standbyMaxHp.coerceAtLeast(1)).coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .size(size)
            .minimumInteractiveComponentSize()
            .testTag(testTag)
            .semantics { contentDescription = contentDesc }
            .background(
                when {
                    !isStandbyAlive -> PixelPanelDark.copy(alpha = 0.55f)
                    pressed -> accentColor.copy(alpha = 0.42f)
                    else -> PixelPanelBg.copy(alpha = 0.82f)
                }
            )
            .border(2.dp, PixelInk)
            .padding(2.dp)
            .border(
                1.5.dp,
                when {
                    !isStandbyAlive -> PixelBorderLight.copy(alpha = 0.35f)
                    pressed -> Color.White
                    else -> accentColor
                }
            )
            .pointerInput(isStandbyAlive) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    if (isStandbyAlive) {
                        pressed = true
                        onSwitch()
                    }
                    waitForUpOrCancellation()
                    pressed = false
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            PixelBitmapIcon(
                bitmap = standbyPortrait,
                modifier = Modifier.size(size * 0.46f)
            )
            Box(
                modifier = Modifier
                    .width(size * 0.62f)
                    .height(3.dp)
                    .background(Color(0xFF2B1218))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(hpRatio)
                        .background(if (hpRatio > 0.25f) accentColor else LuffyRed)
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .background(PixelInk)
                .padding(horizontal = 3.dp, vertical = 1.dp)
        ) {
            Text(
                text = "⇄",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                color = if (isStandbyAlive) accentColor else HudTextMuted
            )
        }
    }
}

@Composable
private fun PixelGear4Pad(
    meter: Float,
    isActive: Boolean,
    timer: Float,
    portrait: ImageBitmap,
    size: Dp,
    contentDesc: String,
    testTag: String,
    onTrigger: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val isReady = !isActive && meter >= 99.9f
    val ratio = (meter / 100f).coerceIn(0f, 1f)
    val borderColor = when {
        isActive -> Gear4Gold
        isReady -> if (pressed) Color.White else Gear4Gold
        else -> PixelBorderLight.copy(alpha = 0.4f)
    }

    Box(
        modifier = Modifier
            .size(size)
            .minimumInteractiveComponentSize()
            .testTag(testTag)
            .semantics { contentDescription = contentDesc }
            .background(
                when {
                    isActive -> Gear4Crimson.copy(alpha = 0.55f)
                    isReady && pressed -> Gear4Crimson.copy(alpha = 0.72f)
                    isReady -> Gear4Crimson.copy(alpha = 0.42f)
                    else -> PixelPanelDark.copy(alpha = 0.78f)
                }
            )
            .border(2.dp, PixelInk)
            .padding(2.dp)
            .border(1.5.dp, borderColor)
            .pointerInput(isReady) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    if (isReady) {
                        pressed = true
                        onTrigger()
                    }
                    waitForUpOrCancellation()
                    pressed = false
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            PixelBitmapIcon(
                bitmap = portrait,
                modifier = Modifier.size(size * 0.44f)
            )
            Box(
                modifier = Modifier
                    .width(size * 0.66f)
                    .height(3.dp)
                    .background(Gear4Obsidian)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(ratio)
                        .background(if (isActive || isReady) Gear4Gold else Gear4Crimson)
                )
            }
            Text(
                text = when {
                    isActive -> String.format(Locale.US, "%.0fс", timer.coerceAtLeast(0f))
                    isReady -> "G4!"
                    else -> "${meter.toInt()}%"
                },
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp),
                color = if (isActive || isReady) Gear4Gold else HudTextMuted,
                maxLines = 1
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .background(PixelInk)
                .padding(horizontal = 2.dp, vertical = 1.dp)
        ) {
            Text(
                text = "G4",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp),
                color = if (isActive || isReady) Gear4Gold else Color(0xFFFF8FA3)
            )
        }
    }
}

@Composable
private fun PixelZoroHellPad(
    meter: Float,
    isActive: Boolean,
    timer: Float,
    portrait: ImageBitmap,
    size: Dp,
    contentDesc: String,
    testTag: String,
    onTrigger: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val isReady = !isActive && meter >= 99.9f
    val ratio = (meter / 100f).coerceIn(0f, 1f)
    val borderColor = when {
        isActive -> ZoroHellGreen
        isReady -> if (pressed) Color.White else ZoroHellGreen
        else -> PixelBorderLight.copy(alpha = 0.4f)
    }

    Box(
        modifier = Modifier
            .size(size)
            .minimumInteractiveComponentSize()
            .testTag(testTag)
            .semantics { contentDescription = contentDesc }
            .background(
                when {
                    isActive -> ZoroEmeraldDark.copy(alpha = 0.80f)
                    isReady && pressed -> ZoroEmerald.copy(alpha = 0.72f)
                    isReady -> ZoroEmeraldDark.copy(alpha = 0.72f)
                    else -> ZoroHellObsidian.copy(alpha = 0.82f)
                }
            )
            .border(2.dp, PixelInk)
            .padding(2.dp)
            .border(1.5.dp, borderColor)
            .pointerInput(isReady) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    if (isReady) {
                        pressed = true
                        onTrigger()
                    }
                    waitForUpOrCancellation()
                    pressed = false
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            PixelBitmapIcon(
                bitmap = portrait,
                modifier = Modifier.size(size * 0.44f)
            )
            Box(
                modifier = Modifier
                    .width(size * 0.66f)
                    .height(3.dp)
                    .background(ZoroHellObsidian)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(ratio)
                        .background(if (isActive || isReady) ZoroHellGreen else ZoroEmerald)
                )
            }
            Text(
                text = when {
                    isActive -> String.format(Locale.US, "%.0fс", timer.coerceAtLeast(0f))
                    isReady -> "АД!"
                    else -> "${meter.toInt()}%"
                },
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp),
                color = if (isActive || isReady) ZoroHellGreen else HudTextMuted,
                maxLines = 1
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .background(PixelInk)
                .padding(horizontal = 2.dp, vertical = 1.dp)
        ) {
            Text(
                text = "АД",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp),
                color = if (isActive || isReady) ZoroHellGreen else ZoroMint
            )
        }
    }
}

@Composable
private fun BossHealthBar(
    bossName: String,
    hp: Int,
    maxHp: Int,
    compact: Boolean,
    modifier: Modifier = Modifier
) {
    val ratio = (hp.toFloat() / maxHp.coerceAtLeast(1)).coerceIn(0f, 1f)
    Column(
        modifier = modifier
            .fillMaxWidth(if (compact) 0.94f else 0.62f)
            .widthIn(max = 460.dp)
            .testTag("boss_hp_bar")
            .background(PixelPanelDark.copy(alpha = 0.94f))
            .border(2.dp, PixelInk)
            .padding(2.dp)
            .border(1.5.dp, LuffyRed)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = bossName,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = if (compact) 9.sp else 11.sp
                ),
                color = StrawGold,
                maxLines = 1
            )
            Text(
                text = "$hp / $maxHp",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = if (compact) 8.sp else 10.sp
                ),
                color = HudTextLight,
                maxLines = 1
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (compact) 12.dp else 14.dp)
                .background(Color(0xFF2B1016))
                .border(2.dp, PixelInk)
                .padding(2.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(ratio)
                    .background(LuffyRed)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(ratio)
                    .height(3.dp)
                    .background(Color(0xFFFF7A85))
            )
        }
    }
}

@Composable
private fun LevelResultOverlay(
    isVictory: Boolean,
    levelNumber: Int,
    berries: Int,
    defeatedCount: Int,
    compact: Boolean,
    onNextLevel: () -> Unit,
    onRestart: () -> Unit,
    onOpenMenu: () -> Unit
) {
    val accent = if (isVictory) StrawGold else LuffyRed
    val titleRes = when {
        !isVictory -> R.string.game_over_title
        levelNumber == 3 -> R.string.level_3_complete_title
        levelNumber == 2 -> R.string.level_2_complete_title
        else -> R.string.level_complete_title
    }
    val subtitleRes = when {
        !isVictory -> R.string.game_over_subtitle
        levelNumber == 3 -> R.string.level_3_complete_subtitle
        levelNumber == 2 -> R.string.level_2_complete_subtitle
        else -> R.string.level_complete_subtitle
    }
    val hasNextLevel = isVictory && levelNumber < 3

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xBB0C0A16))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 420.dp)
                .fillMaxWidth()
                .background(PixelPanelDark)
                .border(3.dp, PixelInk)
                .padding(3.dp)
                .border(2.dp, accent)
                .padding(horizontal = 18.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = stringResource(titleRes),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = if (compact) 14.sp else 17.sp
                ),
                color = accent,
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(subtitleRes),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = if (compact) 11.sp else 13.sp
                ),
                color = HudTextLight,
                textAlign = TextAlign.Center
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PixelPanelBg)
                    .border(1.5.dp, PixelInk)
                    .padding(horizontal = 14.dp, vertical = 9.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.stat_berries),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = HudTextMuted
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "฿$berries",
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 12.sp),
                        color = StrawGold
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.stat_defeated),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = HudTextMuted
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "$defeatedCount",
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 12.sp),
                        color = SurfCyan
                    )
                }
            }

            if (hasNextLevel) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .minimumInteractiveComponentSize()
                        .testTag("next_level_button")
                        .background(StrawGold)
                        .border(2.dp, PixelInk)
                        .clickable(onClick = onNextLevel)
                        .padding(horizontal = 18.dp, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = stringResource(R.string.btn_next_level),
                        tint = PixelInk,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.btn_next_level),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = if (compact) 10.sp else 12.sp
                        ),
                        color = PixelInk
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .testTag("restart_level_button")
                        .background(if (hasNextLevel) PixelPanelBg else accent)
                        .border(2.dp, PixelInk)
                        .padding(if (hasNextLevel) 1.dp else 0.dp)
                        .border(if (hasNextLevel) 1.dp else 0.dp, if (hasNextLevel) SurfCyan else Color.Transparent)
                        .clickable(onClick = onRestart)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = stringResource(R.string.btn_restart),
                        tint = if (hasNextLevel) HudTextLight else PixelInk,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.btn_restart),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = if (compact) 9.sp else 10.sp
                        ),
                        color = if (hasNextLevel) HudTextLight else PixelInk,
                        maxLines = 1
                    )
                }

                Row(
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("result_main_menu_button")
                        .background(PixelPanelBg)
                        .border(2.dp, PixelInk)
                        .padding(1.dp)
                        .border(1.dp, PixelBorderLight)
                        .clickable(onClick = onOpenMenu)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Home,
                        contentDescription = stringResource(R.string.btn_main_menu),
                        tint = StrawGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = stringResource(R.string.btn_main_menu),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = if (compact) 9.sp else 10.sp
                        ),
                        color = HudTextLight,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun PixelAbilitySlot(
    icon: ImageBitmap,
    keyHint: String,
    label: String,
    cooldown: CooldownState,
    isActive: Boolean,
    accentColor: Color,
    compact: Boolean,
    testTag: String,
    contentDesc: String,
    onClick: () -> Unit
) {
    val slotSize = if (compact) 48.dp else 54.dp
    val borderColor = when {
        isActive -> Color.White
        cooldown.isReady -> accentColor
        else -> PixelBorderLight.copy(alpha = 0.55f)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(slotSize)
                .minimumInteractiveComponentSize()
                .testTag(testTag)
                .semantics { contentDescription = contentDesc }
                .background(PixelPanelBg)
                .border(2.dp, PixelInk)
                .padding(2.dp)
                .border(1.5.dp, borderColor)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            PixelBitmapIcon(
                bitmap = icon,
                modifier = Modifier.size(if (compact) 30.dp else 34.dp)
            )

            if (!cooldown.isReady) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .fillMaxHeight(cooldown.progress)
                        .background(CooldownTint)
                )
                Text(
                    text = String.format(Locale.US, "%.1f", cooldown.remaining),
                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 9.sp),
                    color = HudTextLight
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .background(PixelInk)
                    .padding(horizontal = 3.dp, vertical = 1.dp)
            ) {
                Text(
                    text = keyHint,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                    color = if (cooldown.isReady) accentColor else HudTextMuted
                )
            }
        }

        if (!compact) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                color = if (cooldown.isReady) HudTextLight else HudTextMuted,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun HudUtilityButtonsRow(
    isMuted: Boolean,
    showHitboxes: Boolean,
    showTouchControls: Boolean,
    onToggleMute: () -> Unit,
    onToggleHitboxes: () -> Unit,
    onToggleTouchControls: () -> Unit,
    onOpenMainMenu: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        PixelIconToggle(
            icon = Icons.Filled.Home,
            isActive = true,
            activeColor = StrawGold,
            contentDesc = stringResource(R.string.cd_open_menu),
            testTag = "open_main_menu_button",
            onClick = onOpenMainMenu
        )
        PixelIconToggle(
            icon = if (isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
            isActive = !isMuted,
            activeColor = StrawGold,
            contentDesc = stringResource(R.string.cd_sound_toggle),
            testTag = "toggle_sound_button",
            onClick = onToggleMute
        )
        PixelIconToggle(
            icon = Icons.Filled.CropFree,
            isActive = showHitboxes,
            activeColor = ReadyGreen,
            contentDesc = stringResource(R.string.cd_hitbox_toggle),
            testTag = "toggle_hitbox_button",
            onClick = onToggleHitboxes
        )
        PixelIconToggle(
            icon = Icons.Filled.Gamepad,
            isActive = showTouchControls,
            activeColor = SurfCyan,
            contentDesc = stringResource(R.string.cd_controls_toggle),
            testTag = "toggle_touch_controls_button",
            onClick = onToggleTouchControls
        )
    }
}

@Composable
private fun PixelIconToggle(
    icon: ImageVector,
    isActive: Boolean,
    activeColor: Color,
    contentDesc: String,
    testTag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .minimumInteractiveComponentSize()
            .testTag(testTag)
            .semantics { contentDescription = contentDesc }
            .background(PixelPanelBg)
            .border(2.dp, PixelInk)
            .padding(2.dp)
            .border(1.dp, if (isActive) activeColor else PixelBorderLight.copy(alpha = 0.45f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDesc,
            tint = if (isActive) activeColor else HudTextMuted,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun PixelHoldButton(
    icon: ImageVector,
    keyLabel: String,
    size: Dp,
    accentColor: Color,
    contentDesc: String,
    testTag: String,
    onPressedChanged: (Boolean) -> Unit
) {
    var pressed by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .size(size)
            .minimumInteractiveComponentSize()
            .testTag(testTag)
            .semantics { contentDescription = contentDesc }
            .background(if (pressed) accentColor.copy(alpha = 0.35f) else PixelPanelBg.copy(alpha = 0.72f))
            .border(2.dp, PixelInk)
            .padding(2.dp)
            .border(1.5.dp, if (pressed) Color.White else accentColor.copy(alpha = 0.8f))
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    pressed = true
                    onPressedChanged(true)
                    waitForUpOrCancellation()
                    pressed = false
                    onPressedChanged(false)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDesc,
            tint = if (pressed) Color.White else HudTextLight,
            modifier = Modifier.size(28.dp)
        )
        Text(
            text = keyLabel,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
            color = accentColor,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(3.dp)
        )
    }
}

@Composable
private fun PixelActionPad(
    keyLabel: String,
    icon: ImageBitmap,
    cooldown: CooldownState,
    accentColor: Color,
    size: Dp,
    contentDesc: String,
    testTag: String,
    onTrigger: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .size(size)
            .minimumInteractiveComponentSize()
            .testTag(testTag)
            .semantics { contentDescription = contentDesc }
            .background(if (pressed) accentColor.copy(alpha = 0.4f) else PixelPanelBg.copy(alpha = 0.78f))
            .border(2.dp, PixelInk)
            .padding(2.dp)
            .border(
                1.5.dp,
                when {
                    pressed -> Color.White
                    cooldown.isReady -> accentColor
                    else -> PixelBorderLight.copy(alpha = 0.45f)
                }
            )
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    pressed = true
                    onTrigger()
                    waitForUpOrCancellation()
                    pressed = false
                }
            },
        contentAlignment = Alignment.Center
    ) {
        PixelBitmapIcon(
            bitmap = icon,
            modifier = Modifier.size(size * 0.56f)
        )

        if (!cooldown.isReady) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .fillMaxHeight(cooldown.progress)
                    .background(CooldownTint)
            )
            Text(
                text = String.format(Locale.US, "%.1f", cooldown.remaining),
                style = MaterialTheme.typography.titleSmall.copy(fontSize = 8.sp),
                color = HudTextLight
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .background(PixelInk)
                .padding(horizontal = 3.dp, vertical = 1.dp)
        ) {
            Text(
                text = keyLabel,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                color = if (cooldown.isReady) accentColor else HudTextMuted
            )
        }
    }
}

/**
 * Floating 16-bit Pixel Virtual Joystick on the left side of the screen.
 */
@Composable
private fun FloatingPixelJoystickZone(
    modifier: Modifier = Modifier,
    onLeftChanged: (Boolean) -> Unit,
    onRightChanged: (Boolean) -> Unit
) {
    val density = LocalDensity.current
    val baseRadiusPx = with(density) { 46.dp.toPx() }
    val knobRadiusPx = with(density) { 20.dp.toPx() }
    val maxTravelPx = with(density) { 36.dp.toPx() }
    val deadzonePx = with(density) { 9.dp.toPx() }

    var isActive by remember { mutableStateOf(false) }
    var anchorPos by remember { mutableStateOf(Offset.Zero) }
    var knobOffset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .testTag("floating_joystick_zone")
            .semantics { contentDescription = "Плавающий джойстик движения" }
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val pointerId = down.id
                    isActive = true
                    anchorPos = down.position
                    knobOffset = Offset.Zero
                    onLeftChanged(false)
                    onRightChanged(false)

                    try {
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull { it.id == pointerId } ?: break
                            if (!change.pressed) {
                                break
                            }
                            val rawDelta = change.position - anchorPos
                            val dist = hypot(rawDelta.x.toDouble(), rawDelta.y.toDouble()).toFloat()
                            val clamped = if (dist > maxTravelPx && dist > 0.001f) {
                                Offset(
                                    x = rawDelta.x / dist * maxTravelPx,
                                    y = rawDelta.y / dist * maxTravelPx
                                )
                            } else {
                                rawDelta
                            }
                            knobOffset = clamped
                            onLeftChanged(clamped.x < -deadzonePx)
                            onRightChanged(clamped.x > deadzonePx)
                        }
                    } finally {
                        isActive = false
                        knobOffset = Offset.Zero
                        onLeftChanged(false)
                        onRightChanged(false)
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val restCenter = Offset(
                x = baseRadiusPx + 24.dp.toPx(),
                y = size.height - baseRadiusPx - 44.dp.toPx()
            )
            val center = if (isActive) anchorPos else restCenter
            val alpha = if (isActive) 0.92f else 0.36f
            val knobCenter = center + knobOffset

            drawCircle(
                color = PixelPanelBg.copy(alpha = 0.68f * alpha),
                radius = baseRadiusPx,
                center = center
            )
            drawCircle(
                color = PixelInk.copy(alpha = alpha),
                radius = baseRadiusPx,
                center = center,
                style = Stroke(width = 3.dp.toPx())
            )
            drawCircle(
                color = StrawGold.copy(alpha = 0.85f * alpha),
                radius = baseRadiusPx - 3.dp.toPx(),
                center = center,
                style = Stroke(width = 1.5.dp.toPx())
            )

            val pipOffset = baseRadiusPx * 0.64f
            val pipSize = 5.dp.toPx()
            val leftLit = isActive && knobOffset.x < -deadzonePx
            val rightLit = isActive && knobOffset.x > deadzonePx
            drawRect(
                color = (if (leftLit) Color.White else StrawGold).copy(alpha = alpha),
                topLeft = Offset(center.x - pipOffset - pipSize * 0.5f, center.y - pipSize * 0.5f),
                size = Size(pipSize, pipSize)
            )
            drawRect(
                color = (if (rightLit) Color.White else StrawGold).copy(alpha = alpha),
                topLeft = Offset(center.x + pipOffset - pipSize * 0.5f, center.y - pipSize * 0.5f),
                size = Size(pipSize, pipSize)
            )

            drawCircle(
                color = (if (isActive) StrawGold else PixelBorderLight).copy(alpha = 0.92f * alpha),
                radius = knobRadiusPx,
                center = knobCenter
            )
            drawCircle(
                color = PixelInk.copy(alpha = alpha),
                radius = knobRadiusPx,
                center = knobCenter,
                style = Stroke(width = 2.5.dp.toPx())
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.45f * alpha),
                radius = knobRadiusPx * 0.38f,
                center = Offset(knobCenter.x - knobRadiusPx * 0.2f, knobCenter.y - knobRadiusPx * 0.2f)
            )
        }
    }
}

/** Draws an ImageBitmap with strict nearest-neighbor FilterQuality.None for crisp pixel icons */
@Composable
fun PixelBitmapIcon(
    bitmap: ImageBitmap,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        drawImage(
            image = bitmap,
            dstOffset = IntOffset.Zero,
            dstSize = IntSize(size.width.toInt().coerceAtLeast(1), size.height.toInt().coerceAtLeast(1)),
            filterQuality = FilterQuality.None
        )
    }
}
