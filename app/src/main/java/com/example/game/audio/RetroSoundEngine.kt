package com.example.game.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

enum class RetroSfx {
    JUMP,
    LAND,
    RUBBER_STRETCH,
    PUNCH_WHOOSH,
    GATLING_POP,
    AXE_STRETCH,
    AXE_SLAM,
    STEP,
    ENEMY_HIT_LIGHT,
    ENEMY_HIT_HEAVY,
    RIFLE_SHOT,
    MORGAN_SWING,
    PLAYER_HURT,
    PICKUP_MEAT,
    PICKUP_BERRY,
    ENEMY_DEFEAT,
    VICTORY_FANFARE,
    GAME_OVER,
    ZORO_DRAW,
    ZORO_SLASH,
    ZORO_ONIGIRI,
    ZORO_TATSUMAKI,
    CHAR_SWITCH,
    SANJI_KICK,
    SANJI_SKYWALK,
    SANJI_PARTY_TABLE,
    SANJI_DIABLE_FIRE,
    KNIFE_THROW,
    CABAJI_FIRE,
    BUGGY_SPLIT,
    CANNON_FIRE,
    BUGGY_EXPLOSION,
    NAMI_SPARK,
    NAMI_CYCLONE,
    NAMI_THUNDER,
    USOPP_SLING_DRAW,
    USOPP_SHOT,
    USOPP_EXPLOSION,
    USOPP_FIREBIRD,
    GATE_SMASH,
    WATER_SPLASH,
    WATER_BUBBLE,
    DEFLECT_CLANG,
    SHARK_DART,
    KIRIBATI_SLASH,
    TOWER_COLLAPSE,
    CHOPPER_KUNGFU,
    CHOPPER_GUARD_PUFF,
    CHOPPER_HEAL,
    CHOPPER_MONSTER_ROAR,
    CHOPPER_MONSTER_SLAM,
    GEAR4_ACTIVATE,
    GEAR4_BOUNCE,
    GEAR4_KONG_GUN_IMPACT,
    GEAR4_HAKI_HIT,
    GEAR4_KING_KONG_SLAM,
    ZORO_PARRY_CLANG,
    ZORO_360_CANNON,
    ZORO_HELL_ACTIVATE,
    ZORO_HELL_SLASH,
    ZORO_PURGATORY_ONIGIRI,
    ZORO_ASURA_AURA,
    ZORO_ASURA_BURST
}

/**
 * Pure Kotlin 8-bit chiptune PCM synthesizer using Android AudioTrack.
 * Pre-generates crisp square/triangle/noise waveforms for zero-latency combat & movement audio.
 */
class RetroSoundEngine {
    private val sampleRate = 22050
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val clips: Map<RetroSfx, ShortArray>

    @Volatile
    var isMuted: Boolean = false

    init {
        clips = mapOf(
            RetroSfx.JUMP to synthJump(),
            RetroSfx.LAND to synthLand(),
            RetroSfx.RUBBER_STRETCH to synthRubberStretch(),
            RetroSfx.PUNCH_WHOOSH to synthPunchWhoosh(),
            RetroSfx.GATLING_POP to synthGatlingPop(),
            RetroSfx.AXE_STRETCH to synthAxeStretch(),
            RetroSfx.AXE_SLAM to synthAxeSlam(),
            RetroSfx.STEP to synthStep(),
            RetroSfx.ENEMY_HIT_LIGHT to synthEnemyHitLight(),
            RetroSfx.ENEMY_HIT_HEAVY to synthEnemyHitHeavy(),
            RetroSfx.RIFLE_SHOT to synthRifleShot(),
            RetroSfx.MORGAN_SWING to synthMorganSwing(),
            RetroSfx.PLAYER_HURT to synthPlayerHurt(),
            RetroSfx.PICKUP_MEAT to synthPickupMeat(),
            RetroSfx.PICKUP_BERRY to synthPickupBerry(),
            RetroSfx.ENEMY_DEFEAT to synthEnemyDefeat(),
            RetroSfx.VICTORY_FANFARE to synthVictoryFanfare(),
            RetroSfx.GAME_OVER to synthGameOver(),
            RetroSfx.ZORO_DRAW to synthZoroDraw(),
            RetroSfx.ZORO_SLASH to synthZoroSlash(),
            RetroSfx.ZORO_ONIGIRI to synthZoroOnigiri(),
            RetroSfx.ZORO_TATSUMAKI to synthZoroTatsumaki(),
            RetroSfx.CHAR_SWITCH to synthCharSwitch(),
            RetroSfx.SANJI_KICK to synthSanjiKick(),
            RetroSfx.SANJI_SKYWALK to synthSanjiSkyWalk(),
            RetroSfx.SANJI_PARTY_TABLE to synthSanjiPartyTable(),
            RetroSfx.SANJI_DIABLE_FIRE to synthSanjiDiableFire(),
            RetroSfx.KNIFE_THROW to synthKnifeThrow(),
            RetroSfx.CABAJI_FIRE to synthCabajiFire(),
            RetroSfx.BUGGY_SPLIT to synthBuggySplit(),
            RetroSfx.CANNON_FIRE to synthCannonFire(),
            RetroSfx.BUGGY_EXPLOSION to synthBuggyExplosion(),
            RetroSfx.NAMI_SPARK to synthNamiSpark(),
            RetroSfx.NAMI_CYCLONE to synthNamiCyclone(),
            RetroSfx.NAMI_THUNDER to synthNamiThunder(),
            RetroSfx.USOPP_SLING_DRAW to synthUsoppSlingDraw(),
            RetroSfx.USOPP_SHOT to synthUsoppShot(),
            RetroSfx.USOPP_EXPLOSION to synthUsoppExplosion(),
            RetroSfx.USOPP_FIREBIRD to synthUsoppFirebird(),
            RetroSfx.GATE_SMASH to synthGateSmash(),
            RetroSfx.WATER_SPLASH to synthWaterSplash(),
            RetroSfx.WATER_BUBBLE to synthWaterBubble(),
            RetroSfx.DEFLECT_CLANG to synthDeflectClang(),
            RetroSfx.SHARK_DART to synthSharkDart(),
            RetroSfx.KIRIBATI_SLASH to synthKiribatiSlash(),
            RetroSfx.TOWER_COLLAPSE to synthTowerCollapse(),
            RetroSfx.CHOPPER_KUNGFU to synthChopperKungFu(),
            RetroSfx.CHOPPER_GUARD_PUFF to synthChopperGuardPuff(),
            RetroSfx.CHOPPER_HEAL to synthChopperHeal(),
            RetroSfx.CHOPPER_MONSTER_ROAR to synthChopperMonsterRoar(),
            RetroSfx.CHOPPER_MONSTER_SLAM to synthChopperMonsterSlam(),
            RetroSfx.GEAR4_ACTIVATE to synthGear4Activate(),
            RetroSfx.GEAR4_BOUNCE to synthGear4Bounce(),
            RetroSfx.GEAR4_KONG_GUN_IMPACT to synthGear4KongGunImpact(),
            RetroSfx.GEAR4_HAKI_HIT to synthGear4HakiHit(),
            RetroSfx.GEAR4_KING_KONG_SLAM to synthGear4KingKongSlam(),
            RetroSfx.ZORO_PARRY_CLANG to synthZoroParryClang(),
            RetroSfx.ZORO_360_CANNON to synthZoro360Cannon(),
            RetroSfx.ZORO_HELL_ACTIVATE to synthZoroHellActivate(),
            RetroSfx.ZORO_HELL_SLASH to synthZoroHellSlash(),
            RetroSfx.ZORO_PURGATORY_ONIGIRI to synthZoroPurgatoryOnigiri(),
            RetroSfx.ZORO_ASURA_AURA to synthZoroAsuraAura(),
            RetroSfx.ZORO_ASURA_BURST to synthZoroAsuraBurst()
        )
    }

    fun play(sfx: RetroSfx, volume: Float = 0.85f) {
        if (isMuted) return
        val data = clips[sfx] ?: return
        scope.launch {
            playBuffer(data, volume.coerceIn(0f, 1f))
        }
    }

    private fun playBuffer(samples: ShortArray, volume: Float) {
        var track: AudioTrack? = null
        try {
            val byteSize = samples.size * 2
            val minBuf = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(byteSize)

            track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(minBuf)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            if (track.state == AudioTrack.STATE_INITIALIZED) {
                track.write(samples, 0, samples.size)
                track.setVolume(volume)
                track.play()
                val durationMs = (samples.size * 1000L / sampleRate) + 40L
                Thread.sleep(durationMs)
            }
        } catch (_: Throwable) {
            // Safely ignore audio device unavailability in headless/test environments
        } finally {
            try {
                track?.stop()
                track?.release()
            } catch (_: Throwable) {
            }
        }
    }

    fun release() {
        scope.cancel()
    }

    // --- 8-Bit Waveform Helpers ---

    private fun squareWave(phase: Double, duty: Double = 0.5): Double {
        val p = phase - kotlin.math.floor(phase)
        return if (p < duty) 1.0 else -1.0
    }

    private fun triangleWave(phase: Double): Double {
        val p = phase - kotlin.math.floor(phase)
        return 4.0 * kotlin.math.abs(p - 0.5) - 1.0
    }

    private fun toPcm(sample: Double): Short {
        return (sample.coerceIn(-1.0, 1.0) * 28000.0).toInt().toShort()
    }

    private fun synthJump(): ShortArray {
        val count = (sampleRate * 0.14).toInt()
        val out = ShortArray(count)
        var phase = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            val freq = 210.0 + 490.0 * (t * t * 0.7 + t * 0.3)
            phase += freq / sampleRate
            val env = (1.0 - t) * (if (t < 0.05) t / 0.05 else 1.0)
            out[i] = toPcm(squareWave(phase, 0.25) * env * 0.55)
        }
        return out
    }

    private fun synthLand(): ShortArray {
        val count = (sampleRate * 0.075).toInt()
        val out = ShortArray(count)
        val rng = Random(42)
        var phase = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            val freq = 115.0 - 65.0 * t
            phase += freq / sampleRate
            val env = (1.0 - t) * (1.0 - t)
            val noise = if (i % 4 == 0) rng.nextDouble(-1.0, 1.0) else 0.0
            out[i] = toPcm((triangleWave(phase) * 0.65 + noise * 0.35) * env * 0.50)
        }
        return out
    }

    private fun synthRubberStretch(): ShortArray {
        val count = (sampleRate * 0.17).toInt()
        val out = ShortArray(count)
        var phase = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            val wobble = sin(t * 2.0 * PI * 6.5) * 45.0
            phase += (190.0 + 360.0 * t + wobble) / sampleRate
            val env = if (t < 0.08) t / 0.08 else (1.0 - (t - 0.08) / 0.92)
            out[i] = toPcm((triangleWave(phase) * 0.55 + squareWave(phase * 0.5, 0.35) * 0.45) * env * 0.62)
        }
        return out
    }

    private fun synthPunchWhoosh(): ShortArray {
        val count = (sampleRate * 0.11).toInt()
        val out = ShortArray(count)
        val rng = Random(101)
        var phase = 0.0
        var lastNoise = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            if (i % 3 == 0) lastNoise = rng.nextDouble(-1.0, 1.0)
            phase += (520.0 * (1.0 - t * 0.72)) / sampleRate
            out[i] = toPcm((lastNoise * 0.60 + squareWave(phase, 0.25) * 0.40) * sin(t * PI) * 0.58)
        }
        return out
    }

    private fun synthGatlingPop(): ShortArray {
        val count = (sampleRate * 0.062).toInt()
        val out = ShortArray(count)
        val rng = Random(777)
        var phase = 0.0
        var holdNoise = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            if (i % 2 == 0) holdNoise = rng.nextDouble(-1.0, 1.0)
            phase += (340.0 + 260.0 * (1.0 - t)) / sampleRate
            val env = (1.0 - t) * (if (t < 0.12) t / 0.12 else 1.0)
            out[i] = toPcm((squareWave(phase, 0.25) * 0.52 + holdNoise * 0.48) * env * 0.48)
        }
        return out
    }

    private fun synthAxeStretch(): ShortArray {
        val count = (sampleRate * 0.26).toInt()
        val out = ShortArray(count)
        var phase = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            val vibrato = sin(t * 2.0 * PI * 9.0) * 32.0
            phase += (140.0 + 620.0 * (t * t) + vibrato) / sampleRate
            val env = if (t < 0.08) t / 0.08 else 1.0 - (t * 0.25)
            out[i] = toPcm((squareWave(phase, 0.5) * 0.45 + triangleWave(phase * 1.5) * 0.55) * env * 0.60)
        }
        return out
    }

    private fun synthAxeSlam(): ShortArray {
        val count = (sampleRate * 0.36).toInt()
        val out = ShortArray(count)
        val rng = Random(999)
        var phase = 0.0
        var subPhase = 0.0
        var lfsrNoise = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            val step = (2 + (t * 10).toInt())
            if (i % step == 0) lfsrNoise = if (rng.nextBoolean()) 1.0 else -1.0
            val freq = 175.0 * (1.0 - t * 0.80) + 32.0
            phase += freq / sampleRate
            subPhase += (freq * 0.5) / sampleRate
            val punchEnv = (1.0 - t) * (1.0 - t)
            out[i] = toPcm((squareWave(phase, 0.5) * 0.35 + triangleWave(subPhase) * 0.30 + lfsrNoise * 0.55) * punchEnv * 0.85)
        }
        return out
    }

    private fun synthStep(): ShortArray {
        val count = (sampleRate * 0.032).toInt()
        val out = ShortArray(count)
        val rng = Random(17)
        for (i in 0 until count) {
            val t = i.toDouble() / count
            out[i] = toPcm(rng.nextDouble(-1.0, 1.0) * (1.0 - t) * (1.0 - t) * 0.18)
        }
        return out
    }

    /** Crisp 8-bit punch impact on enemy */
    private fun synthEnemyHitLight(): ShortArray {
        val count = (sampleRate * 0.085).toInt()
        val out = ShortArray(count)
        val rng = Random(303)
        var phase = 0.0
        var n = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            if (i % 3 == 0) n = if (rng.nextBoolean()) 1.0 else -1.0
            phase += (420.0 - 290.0 * t) / sampleRate
            val env = (1.0 - t) * (1.0 - t)
            out[i] = toPcm((squareWave(phase, 0.35) * 0.55 + n * 0.45) * env * 0.72)
        }
        return out
    }

    /** Heavy crunch impact for Battle Axe / lethal strikes */
    private fun synthEnemyHitHeavy(): ShortArray {
        val count = (sampleRate * 0.16).toInt()
        val out = ShortArray(count)
        val rng = Random(404)
        var phase = 0.0
        var n = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            val step = 2 + (t * 6).toInt()
            if (i % step == 0) n = if (rng.nextBoolean()) 1.0 else -1.0
            phase += (240.0 * (1.0 - t * 0.75) + 45.0) / sampleRate
            val env = (1.0 - t) * (1.0 - t)
            out[i] = toPcm((squareWave(phase, 0.5) * 0.48 + n * 0.62) * env * 0.85)
        }
        return out
    }

    /** Sharp 8-bit rifle shot crack */
    private fun synthRifleShot(): ShortArray {
        val count = (sampleRate * 0.13).toInt()
        val out = ShortArray(count)
        val rng = Random(505)
        var phase = 0.0
        var n = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            val step = 1 + (t * 5).toInt()
            if (i % step == 0) n = rng.nextDouble(-1.0, 1.0)
            phase += (880.0 * (1.0 - t * 0.85) + 90.0) / sampleRate
            val env = (1.0 - t).let { it * it * it }
            out[i] = toPcm((n * 0.72 + squareWave(phase, 0.2) * 0.38) * env * 0.80)
        }
        return out
    }

    /** Heavy steel battle-axe swing for Captain Morgan */
    private fun synthMorganSwing(): ShortArray {
        val count = (sampleRate * 0.22).toInt()
        val out = ShortArray(count)
        val rng = Random(606)
        var phase = 0.0
        var n = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            if (i % 4 == 0) n = rng.nextDouble(-1.0, 1.0)
            phase += (260.0 * (1.0 - t * 0.68) + 60.0) / sampleRate
            val env = sin(t * PI)
            out[i] = toPcm((n * 0.55 + squareWave(phase, 0.5) * 0.45) * env * 0.78)
        }
        return out
    }

    /** Retro damage buzz when Luffy gets hit */
    private fun synthPlayerHurt(): ShortArray {
        val count = (sampleRate * 0.22).toInt()
        val out = ShortArray(count)
        var phase = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            val buzz = if ((i / 280) % 2 == 0) 210.0 else 155.0
            phase += (buzz * (1.0 - t * 0.45)) / sampleRate
            val env = 1.0 - t * 0.8
            out[i] = toPcm(squareWave(phase, 0.5) * env * 0.72)
        }
        return out
    }

    /** Cheerful ascending 3-note chime for Meat on the Bone heal */
    private fun synthPickupMeat(): ShortArray {
        val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.5)
        val noteLen = (sampleRate * 0.065).toInt()
        val out = ShortArray(notes.size * noteLen)
        var phase = 0.0
        for (nIdx in notes.indices) {
            val freq = notes[nIdx]
            for (j in 0 until noteLen) {
                val t = j.toDouble() / noteLen
                phase += freq / sampleRate
                val env = (1.0 - t * 0.45)
                out[nIdx * noteLen + j] = toPcm(squareWave(phase, 0.25) * env * 0.58)
            }
        }
        return out
    }

    /** Bright classic 2-note Berry coin ding */
    private fun synthPickupBerry(): ShortArray {
        val notes = doubleArrayOf(987.77, 1318.51)
        val len1 = (sampleRate * 0.055).toInt()
        val len2 = (sampleRate * 0.12).toInt()
        val out = ShortArray(len1 + len2)
        var phase = 0.0
        for (i in 0 until out.size) {
            val freq = if (i < len1) notes[0] else notes[1]
            val t = if (i < len1) i.toDouble() / len1 else (i - len1).toDouble() / len2
            phase += freq / sampleRate
            val env = 1.0 - t * 0.75
            out[i] = toPcm(squareWave(phase, 0.25) * env * 0.52)
        }
        return out
    }

    /** Classic arcade enemy defeated pop-drop */
    private fun synthEnemyDefeat(): ShortArray {
        val count = (sampleRate * 0.20).toInt()
        val out = ShortArray(count)
        val rng = Random(808)
        var phase = 0.0
        var n = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            if (i % 5 == 0) n = rng.nextDouble(-1.0, 1.0)
            phase += (340.0 * (1.0 - t * 0.75) + 55.0) / sampleRate
            val env = 1.0 - t
            out[i] = toPcm((squareWave(phase, 0.5) * 0.50 + n * 0.45) * env * 0.65)
        }
        return out
    }

    /** Heroic 8-bit Level 1 Complete victory fanfare */
    private fun synthVictoryFanfare(): ShortArray {
        val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.5, 783.99, 1046.5)
        val durs = doubleArrayOf(0.09, 0.09, 0.09, 0.16, 0.09, 0.30)
        val total = durs.sumOf { (it * sampleRate).toInt() }
        val out = ShortArray(total)
        var cursor = 0
        var phase = 0.0
        for (idx in notes.indices) {
            val len = (durs[idx] * sampleRate).toInt()
            for (j in 0 until len) {
                if (cursor >= out.size) break
                val t = j.toDouble() / len
                phase += notes[idx] / sampleRate
                val env = if (idx == notes.lastIndex) (1.0 - t * 0.85) else (1.0 - t * 0.35)
                out[cursor++] = toPcm((squareWave(phase, 0.25) * 0.65 + triangleWave(phase * 0.5) * 0.35) * env * 0.62)
            }
        }
        return out
    }

    /** Classic descending 8-bit Game Over melody */
    private fun synthGameOver(): ShortArray {
        val notes = doubleArrayOf(392.0, 369.99, 349.23, 311.13, 261.63)
        val durs = doubleArrayOf(0.14, 0.14, 0.14, 0.16, 0.36)
        val total = durs.sumOf { (it * sampleRate).toInt() }
        val out = ShortArray(total)
        var cursor = 0
        var phase = 0.0
        for (idx in notes.indices) {
            val len = (durs[idx] * sampleRate).toInt()
            for (j in 0 until len) {
                if (cursor >= out.size) break
                val t = j.toDouble() / len
                phase += notes[idx] / sampleRate
                val env = 1.0 - t * 0.7
                out[cursor++] = toPcm((squareWave(phase, 0.5) * 0.55 + triangleWave(phase * 0.5) * 0.45) * env * 0.58)
            }
        }
        return out
    }

    /** Crisp metallic katana steel ring when drawing swords or switching to Zoro */
    private fun synthZoroDraw(): ShortArray {
        val count = (sampleRate * 0.20).toInt()
        val out = ShortArray(count)
        var p1 = 0.0
        var p2 = 0.0
        val rng = Random(1101)
        for (i in 0 until count) {
            val t = i.toDouble() / count
            val f1 = 1280.0 + 980.0 * t
            val f2 = 1920.0 + 1320.0 * t
            p1 += f1 / sampleRate
            p2 += f2 / sampleRate
            val scrape = if (t < 0.35) rng.nextDouble(-1.0, 1.0) * (1.0 - t / 0.35) * 0.32 else 0.0
            val ring = (squareWave(p1, 0.25) * 0.50 + triangleWave(p2) * 0.50) * (1.0 - t) * (1.0 - t * 0.4)
            out[i] = toPcm((ring * 0.72 + scrape) * 0.70)
        }
        return out
    }

    /** Sharp air-slicing katana whoosh for Santoryu combo slashes */
    private fun synthZoroSlash(): ShortArray {
        val count = (sampleRate * 0.115).toInt()
        val out = ShortArray(count)
        val rng = Random(1202)
        var phase = 0.0
        var n = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            if (i % 2 == 0) n = rng.nextDouble(-1.0, 1.0)
            phase += (1450.0 * (1.0 - t * 0.72) + 280.0) / sampleRate
            val env = sin(t * PI) * (1.0 - t * 0.35)
            out[i] = toPcm((n * 0.58 + squareWave(phase, 0.18) * 0.42) * env * 0.75)
        }
        return out
    }

    /** Piercing high-speed twin-blade cut for Onigiri dash */
    private fun synthZoroOnigiri(): ShortArray {
        val count = (sampleRate * 0.26).toInt()
        val out = ShortArray(count)
        val rng = Random(1303)
        var p1 = 0.0
        var p2 = 0.0
        var n = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            if (i % 2 == 0) n = rng.nextDouble(-1.0, 1.0)
            val f1 = if (t < 0.45) 1800.0 - 1100.0 * (t / 0.45) else 2200.0 - 1200.0 * ((t - 0.45) / 0.55)
            val f2 = 760.0 * (1.0 - t * 0.5)
            p1 += f1 / sampleRate
            p2 += f2 / sampleRate
            val env = (1.0 - t * 0.85) * (if (t < 0.05) t / 0.05 else 1.0)
            out[i] = toPcm((n * 0.45 + squareWave(p1, 0.20) * 0.38 + triangleWave(p2) * 0.27) * env * 0.84)
        }
        return out
    }

    /** Swirling dragon wind-vortex + rapid blade pulses for Tatsumaki */
    private fun synthZoroTatsumaki(): ShortArray {
        val count = (sampleRate * 0.48).toInt()
        val out = ShortArray(count)
        val rng = Random(1404)
        var phase = 0.0
        var n = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            val step = 1 + ((1.0 - sin(t * PI)) * 3.0).toInt()
            if (i % step == 0) n = rng.nextDouble(-1.0, 1.0)
            val swirl = 0.65 + 0.35 * sin(t * 2.0 * PI * 9.0)
            phase += (320.0 + 680.0 * sin(t * PI) + 180.0 * sin(t * 2.0 * PI * 7.0)) / sampleRate
            val env = sin(t * PI).coerceAtLeast(0.0) * swirl
            out[i] = toPcm((n * 0.65 + squareWave(phase, 0.25) * 0.40) * env * 0.82)
        }
        return out
    }

    /** Snappy anime tag-in poof/chime when switching heroes */
    private fun synthCharSwitch(): ShortArray {
        val count = (sampleRate * 0.14).toInt()
        val out = ShortArray(count)
        val rng = Random(1505)
        var phase = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            val freq = if (t < 0.45) 520.0 else 780.0
            phase += freq / sampleRate
            val puff = if (t < 0.30) rng.nextDouble(-1.0, 1.0) * (1.0 - t / 0.30) * 0.40 else 0.0
            val chime = squareWave(phase, 0.25) * (1.0 - t) * 0.55
            out[i] = toPcm((chime + puff) * 0.72)
        }
        return out
    }

    /** Crisp acrobatic whip-kick whoosh for Sanji's Black Leg basic combo */
    private fun synthSanjiKick(): ShortArray {
        val count = (sampleRate * 0.095).toInt()
        val out = ShortArray(count)
        val rng = Random(1601)
        var phase = 0.0
        var n = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            if (i % 2 == 0) n = rng.nextDouble(-1.0, 1.0)
            phase += (740.0 * (1.0 - t * 0.68) + 190.0) / sampleRate
            val env = sin(t * PI) * (1.0 - t * 0.25)
            out[i] = toPcm((n * 0.50 + triangleWave(phase) * 0.30 + squareWave(phase * 0.5, 0.25) * 0.25) * env * 0.72)
        }
        return out
    }

    /** Snappy mid-air Sky Walk (Geppo) air-kick pop + ascending breeze */
    private fun synthSanjiSkyWalk(): ShortArray {
        val count = (sampleRate * 0.14).toInt()
        val out = ShortArray(count)
        val rng = Random(1602)
        var phase = 0.0
        var n = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            if (i % 2 == 0) n = rng.nextDouble(-1.0, 1.0)
            val freq = 340.0 + 780.0 * (1.0 - (1.0 - t) * (1.0 - t))
            phase += freq / sampleRate
            val pop = if (t < 0.24) n * (1.0 - t / 0.24) * 0.65 else n * (1.0 - t) * 0.20
            val tone = squareWave(phase, 0.25) * (1.0 - t) * 0.48
            out[i] = toPcm((pop + tone) * 0.76)
        }
        return out
    }

    /** Rapid rhythmic handstand spinning kick bursts for Party Table Kick Course */
    private fun synthSanjiPartyTable(): ShortArray {
        val count = (sampleRate * 0.44).toInt()
        val out = ShortArray(count)
        val rng = Random(1603)
        var phase = 0.0
        var n = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            if (i % 2 == 0) n = rng.nextDouble(-1.0, 1.0)
            val pulse = 0.45 + 0.55 * kotlin.math.abs(sin(t * PI * 6.0))
            phase += (480.0 + 360.0 * sin(t * 2.0 * PI * 6.0)) / sampleRate
            val env = sin(t * PI).coerceAtLeast(0.0) * pulse
            out[i] = toPcm((n * 0.52 + squareWave(phase, 0.25) * 0.35 + triangleWave(phase * 0.5) * 0.22) * env * 0.78)
        }
        return out
    }

    /** Blazing Diable Jambe flame ignition & fiery kick explosion */
    private fun synthSanjiDiableFire(): ShortArray {
        val count = (sampleRate * 0.38).toInt()
        val out = ShortArray(count)
        val rng = Random(1604)
        var phase = 0.0
        var crackle = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            val step = 1 + (t * 7.0).toInt()
            if (i % step == 0) crackle = if (rng.nextBoolean()) 1.0 else -1.0
            val freq = 260.0 * (1.0 - t * 0.78) + 48.0 + sin(t * 2.0 * PI * 14.0) * 35.0
            phase += freq / sampleRate
            val env = (1.0 - t * 0.88) * (if (t < 0.06) t / 0.06 else 1.0)
            val roar = crackle * 0.60 + squareWave(phase, 0.5) * 0.30 + triangleWave(phase * 0.5) * 0.25
            out[i] = toPcm(roar * env * 0.88)
        }
        return out
    }

    /** Crisp metallic whistle for throwing daggers */
    private fun synthKnifeThrow(): ShortArray {
        val count = (sampleRate * 0.11).toInt()
        val out = ShortArray(count)
        val rng = Random(1701)
        var phase = 0.0
        var n = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            if (i % 2 == 0) n = rng.nextDouble(-1.0, 1.0)
            val freq = 1950.0 - 1100.0 * t
            phase += freq / sampleRate
            val env = sin(t * PI) * (1.0 - t * 0.35)
            out[i] = toPcm((squareWave(phase, 0.18) * 0.55 + n * 0.45) * env * 0.66)
        }
        return out
    }

    /** Whooshing retro flame stream for Cabaji's fire breath */
    private fun synthCabajiFire(): ShortArray {
        val count = (sampleRate * 0.30).toInt()
        val out = ShortArray(count)
        val rng = Random(1702)
        var phase = 0.0
        var n = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            val step = 2 + (t * 4).toInt()
            if (i % step == 0) n = rng.nextDouble(-1.0, 1.0)
            phase += (340.0 + sin(t * 2.0 * PI * 11.0) * 90.0) / sampleRate
            val env = sin(t * PI).coerceAtLeast(0.0)
            out[i] = toPcm((n * 0.72 + triangleWave(phase) * 0.28) * env * 0.78)
        }
        return out
    }

    /** Quirky choppy spring/pop sound when Buggy splits into Bara-Bara parts */
    private fun synthBuggySplit(): ShortArray {
        val count = (sampleRate * 0.26).toInt()
        val out = ShortArray(count)
        var phase = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            val seg = (t * 4.0).toInt()
            val localT = (t * 4.0) - seg
            val baseFreq = when (seg) {
                0 -> 280.0
                1 -> 440.0
                2 -> 610.0
                else -> 820.0
            }
            val freq = baseFreq + localT * 240.0
            phase += freq / sampleRate
            val env = (1.0 - localT * 0.55) * (1.0 - t * 0.25)
            out[i] = toPcm((squareWave(phase, 0.25) * 0.62 + triangleWave(phase * 0.5) * 0.38) * env * 0.75)
        }
        return out
    }

    /** Deep pirate cannon blast for Buggy Ball launch */
    private fun synthCannonFire(): ShortArray {
        val count = (sampleRate * 0.28).toInt()
        val out = ShortArray(count)
        val rng = Random(1704)
        var phase = 0.0
        var n = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            val step = 1 + (t * 8.0).toInt()
            if (i % step == 0) n = if (rng.nextBoolean()) 1.0 else -1.0
            val freq = 210.0 * (1.0 - t * 0.82) + 38.0
            phase += freq / sampleRate
            val env = (1.0 - t) * (1.0 - t)
            out[i] = toPcm((n * 0.64 + squareWave(phase, 0.5) * 0.42 + triangleWave(phase * 0.5) * 0.25) * env * 0.90)
        }
        return out
    }

    /** Massive roaring 8-bit explosion for Buggy Ball detonation */
    private fun synthBuggyExplosion(): ShortArray {
        val count = (sampleRate * 0.55).toInt()
        val out = ShortArray(count)
        val rng = Random(1705)
        var phase = 0.0
        var subPhase = 0.0
        var lfsr = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            val step = 2 + (t * 14.0).toInt()
            if (i % step == 0) lfsr = if (rng.nextBoolean()) 1.0 else -1.0
            val freq = 140.0 * (1.0 - t * 0.78) + 28.0
            phase += freq / sampleRate
            subPhase += (freq * 0.5) / sampleRate
            val env = (1.0 - t).let { it * (0.45 + 0.55 * it) }
            val rumble = lfsr * 0.68 + squareWave(phase, 0.5) * 0.28 + triangleWave(subPhase) * 0.34
            out[i] = toPcm(rumble * env * 0.95)
        }
        return out
    }

    /** Crisp 8-bit electric crackle-zap for Nami's Clima-Tact spark */
    private fun synthNamiSpark(): ShortArray {
        val count = (sampleRate * 0.14).toInt()
        val out = ShortArray(count)
        val rng = Random(1801)
        var phase = 0.0
        var zap = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            if (i % 2 == 0) zap = if (rng.nextBoolean()) 1.0 else -1.0
            val jitter = if ((i / 180) % 2 == 0) 1750.0 else 1120.0
            phase += (jitter * (1.0 - t * 0.35)) / sampleRate
            val env = (1.0 - t) * (if (t < 0.06) t / 0.06 else 1.0)
            out[i] = toPcm((squareWave(phase, 0.2) * 0.56 + zap * 0.44) * env * 0.78)
        }
        return out
    }

    /** Swirling weather gust for Nami's Cyclone Tempo */
    private fun synthNamiCyclone(): ShortArray {
        val count = (sampleRate * 0.34).toInt()
        val out = ShortArray(count)
        val rng = Random(1802)
        var phase = 0.0
        var n = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            val step = 1 + ((1.0 - sin(t * PI)) * 3.0).toInt()
            if (i % step == 0) n = rng.nextDouble(-1.0, 1.0)
            val lfo = sin(t * 2.0 * PI * 8.0)
            phase += (520.0 + 380.0 * sin(t * PI) + lfo * 140.0) / sampleRate
            val env = sin(t * PI).coerceAtLeast(0.0) * (0.72 + 0.28 * lfo)
            out[i] = toPcm((n * 0.62 + triangleWave(phase) * 0.38) * env * 0.80)
        }
        return out
    }

    /** Massive crackling lightning strike + deep rolling 8-bit thunderclap for Nami's Thunderbolt Tempo */
    private fun synthNamiThunder(): ShortArray {
        val count = (sampleRate * 0.52).toInt()
        val out = ShortArray(count)
        val rng = Random(1803)
        var phase = 0.0
        var subPhase = 0.0
        var lfsr = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            val step = if (t < 0.22) 1 else (2 + ((t - 0.22) * 14.0).toInt())
            if (i % step == 0) lfsr = if (rng.nextBoolean()) 1.0 else -1.0
            val crackFreq = if (t < 0.18) (2200.0 - 1500.0 * (t / 0.18)) else (130.0 * (1.0 - t * 0.65) + 32.0)
            phase += crackFreq / sampleRate
            subPhase += 46.0 / sampleRate
            val boltCrack = if (t < 0.18) squareWave(phase, 0.2) * 0.55 else squareWave(phase, 0.5) * 0.25
            val rumble = lfsr * 0.68 + triangleWave(subPhase) * 0.30
            val env = (1.0 - t * 0.90) * (if (t < 0.02) t / 0.02 else 1.0)
            out[i] = toPcm((boltCrack + rumble) * env * 0.94)
        }
        return out
    }

    /** Elastic slingshot rubber band tension pull for Usopp's Kabuto */
    private fun synthUsoppSlingDraw(): ShortArray {
        val count = (sampleRate * 0.10).toInt()
        val out = ShortArray(count)
        var phase = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            val freq = 180.0 + 340.0 * t * t
            phase += freq / sampleRate
            val env = sin(t * PI) * 0.65
            out[i] = toPcm((triangleWave(phase) * 0.65 + squareWave(phase * 0.5, 0.3) * 0.35) * env * 0.65)
        }
        return out
    }

    /** Snappy slingshot twang + whistling pellet release for Usopp */
    private fun synthUsoppShot(): ShortArray {
        val count = (sampleRate * 0.12).toInt()
        val out = ShortArray(count)
        val rng = Random(1805)
        var phase = 0.0
        var n = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            if (i % 2 == 0) n = rng.nextDouble(-1.0, 1.0)
            val twang = 960.0 * (1.0 - t * 0.72) + 210.0 + sin(t * 2.0 * PI * 18.0) * 65.0
            phase += twang / sampleRate
            val env = (1.0 - t) * (1.0 - t * 0.5)
            out[i] = toPcm((squareWave(phase, 0.25) * 0.62 + n * 0.38) * env * 0.78)
        }
        return out
    }

    /** Punchy 8-bit star blast detonation for Usopp's Exploding Star */
    private fun synthUsoppExplosion(): ShortArray {
        val count = (sampleRate * 0.36).toInt()
        val out = ShortArray(count)
        val rng = Random(1806)
        var phase = 0.0
        var lfsr = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            val step = 1 + (t * 11.0).toInt()
            if (i % step == 0) lfsr = if (rng.nextBoolean()) 1.0 else -1.0
            phase += (190.0 * (1.0 - t * 0.80) + 36.0) / sampleRate
            val env = (1.0 - t) * (1.0 - t * 0.55)
            out[i] = toPcm((lfsr * 0.66 + squareWave(phase, 0.5) * 0.36 + triangleWave(phase * 0.5) * 0.28) * env * 0.90)
        }
        return out
    }

    /** Blazing phoenix screech + roaring fire jet for Usopp's Firebird Star */
    private fun synthUsoppFirebird(): ShortArray {
        val count = (sampleRate * 0.48).toInt()
        val out = ShortArray(count)
        val rng = Random(1807)
        var screechPhase = 0.0
        var flamePhase = 0.0
        var n = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            val step = 1 + (t * 5.0).toInt()
            if (i % step == 0) n = rng.nextDouble(-1.0, 1.0)
            // Phoenix bird cry sweeping high then dropping into fiery roar
            val cryFreq = if (t < 0.38) {
                860.0 + 980.0 * sin((t / 0.38) * PI)
            } else {
                540.0 * (1.0 - (t - 0.38) * 0.65)
            }
            screechPhase += cryFreq / sampleRate
            flamePhase += (210.0 + sin(t * 2.0 * PI * 12.0) * 55.0) / sampleRate
            val cryEnv = if (t < 0.42) sin((t / 0.42) * PI) * 0.55 else 0.0
            val fireEnv = (1.0 - t * 0.78) * (if (t < 0.08) t / 0.08 else 1.0)
            val sample = squareWave(screechPhase, 0.22) * cryEnv +
                (n * 0.60 + triangleWave(flamePhase) * 0.35) * fireEnv * 0.85
            out[i] = toPcm(sample * 0.88)
        }
        return out
    }

    /** Heavy stone gate shattering into pixel rubble */
    private fun synthGateSmash(): ShortArray {
        val count = (sampleRate * 0.52).toInt()
        val out = ShortArray(count)
        val rng = Random(1901)
        var phase = 0.0
        var lfsr = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            val step = 1 + (t * 14.0).toInt()
            if (i % step == 0) lfsr = if (rng.nextBoolean()) 1.0 else -1.0
            phase += (140.0 * (1.0 - t * 0.78) + 28.0) / sampleRate
            val env = (1.0 - t) * (1.0 - t * 0.35)
            out[i] = toPcm((lfsr * 0.72 + squareWave(phase, 0.5) * 0.32 + triangleWave(phase * 0.5) * 0.25) * env * 0.94)
        }
        return out
    }

    /** Sparkling water splash for pool plunge, Arlong leap, and rescue */
    private fun synthWaterSplash(): ShortArray {
        val count = (sampleRate * 0.34).toInt()
        val out = ShortArray(count)
        val rng = Random(1902)
        var phase = 0.0
        var n = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            val step = 1 + ((1.0 - t) * 4.0).toInt()
            if (i % step == 0) n = rng.nextDouble(-1.0, 1.0)
            phase += (480.0 - t * 280.0 + sin(t * 2.0 * PI * 14.0) * 60.0) / sampleRate
            val env = (if (t < 0.08) t / 0.08 else 1.0) * (1.0 - t)
            out[i] = toPcm((n * 0.68 + triangleWave(phase) * 0.34) * env * 0.84)
        }
        return out
    }

    /** Rising 8-bit underwater bloop/bubble for Luffy drowning timer */
    private fun synthWaterBubble(): ShortArray {
        val count = (sampleRate * 0.14).toInt()
        val out = ShortArray(count)
        var phase = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            val freq = 260.0 + t * t * 620.0
            phase += freq / sampleRate
            val env = sin(t * PI)
            out[i] = toPcm(triangleWave(phase) * env * 0.72)
        }
        return out
    }

    /** Sharp metallic blade clang when Hatchan's 6-sword spin deflects projectiles */
    private fun synthDeflectClang(): ShortArray {
        val count = (sampleRate * 0.18).toInt()
        val out = ShortArray(count)
        var p1 = 0.0
        var p2 = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            p1 += (1560.0 - t * 420.0) / sampleRate
            p2 += (2340.0 - t * 520.0) / sampleRate
            val env = (1.0 - t) * (1.0 - t)
            out[i] = toPcm((squareWave(p1, 0.25) * 0.55 + squareWave(p2, 0.15) * 0.45) * env * 0.84)
        }
        return out
    }

    /** High-velocity hydro-torpedo scream for Arlong's Shark On Darts */
    private fun synthSharkDart(): ShortArray {
        val count = (sampleRate * 0.38).toInt()
        val out = ShortArray(count)
        val rng = Random(1905)
        var phase = 0.0
        var n = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            if (i % 2 == 0) n = rng.nextDouble(-1.0, 1.0)
            val freq = 220.0 + 780.0 * sin(t * PI * 0.7)
            phase += freq / sampleRate
            val env = (if (t < 0.06) t / 0.06 else 1.0) * (1.0 - t * 0.72)
            out[i] = toPcm((squareWave(phase, 0.25) * 0.52 + n * 0.48) * env * 0.88)
        }
        return out
    }

    /** Heavy saw-blade metallic roar for Arlong's Kiribati sword cleave */
    private fun synthKiribatiSlash(): ShortArray {
        val count = (sampleRate * 0.30).toInt()
        val out = ShortArray(count)
        val rng = Random(1906)
        var phase = 0.0
        var n = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            if (i % 3 == 0) n = rng.nextDouble(-1.0, 1.0)
            val freq = 520.0 * (1.0 - t * 0.65) + sin(t * 2.0 * PI * 32.0) * 90.0
            phase += freq / sampleRate
            val env = (1.0 - t * 0.85)
            out[i] = toPcm((squareWave(phase, 0.35) * 0.58 + n * 0.42) * env * 0.90)
        }
        return out
    }

    /** Massive seismic pagoda tower collapse rumble for the Arlong Park finale */
    private fun synthTowerCollapse(): ShortArray {
        val count = (sampleRate * 0.95).toInt()
        val out = ShortArray(count)
        val rng = Random(1907)
        var phase = 0.0
        var lfsr = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            val step = 1 + (t * 18.0).toInt()
            if (i % step == 0) lfsr = if (rng.nextBoolean()) 1.0 else -1.0
            val pulse = 0.75 + 0.25 * sin(t * 2.0 * PI * 6.5)
            phase += (95.0 * (1.0 - t * 0.62) + 24.0) / sampleRate
            val env = (1.0 - t * 0.68) * pulse
            out[i] = toPcm((lfsr * 0.68 + triangleWave(phase) * 0.40 + squareWave(phase * 0.5, 0.5) * 0.24) * env * 0.95)
        }
        return out
    }

    /** Crisp martial-arts palm-chop whoosh for Chopper's Kung Fu Point combo */
    private fun synthChopperKungFu(): ShortArray {
        val count = (sampleRate * 0.088).toInt()
        val out = ShortArray(count)
        val rng = Random(2101)
        var phase = 0.0
        var n = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            if (i % 2 == 0) n = rng.nextDouble(-1.0, 1.0)
            val freq = 820.0 * (1.0 - t * 0.65) + 220.0
            phase += freq / sampleRate
            val env = sin(t * PI) * (1.0 - t * 0.20)
            out[i] = toPcm((squareWave(phase, 0.25) * 0.46 + n * 0.42 + triangleWave(phase * 0.5) * 0.18) * env * 0.74)
        }
        return out
    }

    /** Soft bouncy fluffy wool poof for Chopper's Guard Point shield */
    private fun synthChopperGuardPuff(): ShortArray {
        val count = (sampleRate * 0.18).toInt()
        val out = ShortArray(count)
        val rng = Random(2102)
        var phase = 0.0
        var n = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            if (i % 4 == 0) n = rng.nextDouble(-1.0, 1.0)
            val freq = 160.0 + 260.0 * sin(t * PI)
            phase += freq / sampleRate
            val env = (if (t < 0.12) t / 0.12 else 1.0) * (1.0 - t * 0.85)
            out[i] = toPcm((triangleWave(phase) * 0.68 + n * 0.34) * env * 0.80)
        }
        return out
    }

    /** Pleasant sparkling 8-bit medical chime arpeggio for Chopper's Doctor Heal */
    private fun synthChopperHeal(): ShortArray {
        val notes = doubleArrayOf(587.33, 739.99, 880.00, 1174.66, 1479.98)
        val noteLen = (sampleRate * 0.068).toInt()
        val out = ShortArray(notes.size * noteLen)
        var phase = 0.0
        var shimmerPhase = 0.0
        for (idx in notes.indices) {
            val freq = notes[idx]
            for (j in 0 until noteLen) {
                val t = j.toDouble() / noteLen
                phase += freq / sampleRate
                shimmerPhase += (freq * 1.5) / sampleRate
                val env = (1.0 - t * 0.42) * (if (idx == notes.lastIndex) (1.0 - t * 0.4) else 1.0)
                out[idx * noteLen + j] = toPcm((triangleWave(phase) * 0.55 + squareWave(shimmerPhase, 0.20) * 0.45) * env * 0.62)
            }
        }
        return out
    }

    /** Deep feral beast roar for Chopper's Monster Point transformation */
    private fun synthChopperMonsterRoar(): ShortArray {
        val count = (sampleRate * 0.44).toInt()
        val out = ShortArray(count)
        val rng = Random(2104)
        var phase = 0.0
        var growlPhase = 0.0
        var n = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            val step = 2 + (t * 6.0).toInt()
            if (i % step == 0) n = if (rng.nextBoolean()) 1.0 else -1.0
            val pitch = 135.0 + 55.0 * sin(t * PI) - 48.0 * t + sin(t * 2.0 * PI * 24.0) * 18.0
            phase += pitch / sampleRate
            growlPhase += (pitch * 0.5) / sampleRate
            val env = (if (t < 0.08) t / 0.08 else 1.0) * (1.0 - t * 0.72)
            out[i] = toPcm((squareWave(phase, 0.45) * 0.44 + triangleWave(growlPhase) * 0.36 + n * 0.45) * env * 0.92)
        }
        return out
    }

    /** Colossal seismic ground crash for Monster Point paw slam */
    private fun synthChopperMonsterSlam(): ShortArray {
        val count = (sampleRate * 0.52).toInt()
        val out = ShortArray(count)
        val rng = Random(2105)
        var phase = 0.0
        var subPhase = 0.0
        var lfsr = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            val step = 1 + (t * 15.0).toInt()
            if (i % step == 0) lfsr = if (rng.nextBoolean()) 1.0 else -1.0
            val freq = 145.0 * (1.0 - t * 0.82) + 26.0
            phase += freq / sampleRate
            subPhase += (freq * 0.5) / sampleRate
            val env = (1.0 - t) * (1.0 - t * 0.45)
            out[i] = toPcm((lfsr * 0.72 + squareWave(phase, 0.5) * 0.34 + triangleWave(subPhase) * 0.38) * env * 0.98)
        }
        return out
    }

    /** Multi-stage 8-bit chiptune Gear 4: Boundman activation (inflation hiss -> Haki shockwave crack + heroic arpeggio) */
    private fun synthGear4Activate(): ShortArray {
        val count = (sampleRate * 0.68).toInt()
        val out = ShortArray(count)
        val rng = Random(4001)
        var subPhase = 0.0
        var arpPhase = 0.0
        var harmPhase = 0.0
        var crackPhase = 0.0
        var steam = 0.0
        val arpNotes = doubleArrayOf(220.0, 277.18, 329.63, 440.0)
        for (i in 0 until count) {
            val t = i.toDouble() / count
            val noiseStep = if (t < 0.24) 2 else 1 + ((t - 0.24) * 10.0).toInt()
            if (i % noiseStep == 0) steam = if (rng.nextBoolean()) 1.0 else -1.0

            val sample = if (t < 0.24) {
                // Stage 1: Pressurized elastic inflation + rising Haki steam buildup
                val u = t / 0.24
                val subFreq = 64.0 + u * u * 165.0
                subPhase += subFreq / sampleRate
                arpPhase += (180.0 + u * 260.0) / sampleRate
                val env = (if (u < 0.12) u / 0.12 else 1.0) * (0.65 + 0.35 * u)
                (triangleWave(subPhase) * 0.48 +
                    squareWave(arpPhase, 0.25) * 0.24 +
                    steam * (0.34 + 0.22 * u)) * env
            } else {
                // Stage 2: Haki shockwave crack + heroic 4-note 8-bit power-up arpeggio + sub drop
                val u = (t - 0.24) / 0.76
                val noteIdx = (u * arpNotes.size).toInt().coerceIn(0, arpNotes.lastIndex)
                val arpFreq = arpNotes[noteIdx]
                val subFreq = 136.0 * (1.0 - u * 0.68) + 42.0
                subPhase += subFreq / sampleRate
                arpPhase += arpFreq / sampleRate
                harmPhase += (arpFreq * 2.0) / sampleRate
                crackPhase += (980.0 * (1.0 - u * 0.75)) / sampleRate
                val crackEnv = if (u < 0.16) (1.0 - u / 0.16) else 0.0
                val tailEnv = (1.0 - u * 0.72) * (1.0 - u * 0.25)
                (triangleWave(subPhase) * 0.46 +
                    squareWave(arpPhase, 0.25) * 0.34 +
                    squareWave(harmPhase, 0.125) * 0.18 +
                    squareWave(crackPhase, 0.15) * crackEnv * 0.32 +
                    steam * (0.42 * crackEnv + 0.16 * tailEnv)) * tailEnv
            }
            out[i] = toPcm(sample * 0.96)
        }
        return out
    }

    /** Iconic springy rubber boing-thump for Boundman's rhythmic bounce & elastic flight */
    private fun synthGear4Bounce(): ShortArray {
        val count = (sampleRate * 0.14).toInt()
        val out = ShortArray(count)
        var phase = 0.0
        var overtonePhase = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            val freq = 95.0 + 250.0 * sin(t * PI * 0.85) + 45.0 * sin(t * 2.0 * PI * 9.0)
            phase += freq / sampleRate
            overtonePhase += (freq * 2.0) / sampleRate
            val env = (if (t < 0.08) t / 0.08 else 1.0) * (1.0 - t * 0.82)
            out[i] = toPcm((triangleWave(phase) * 0.66 + squareWave(overtonePhase, 0.25) * 0.28) * env * 0.82)
        }
        return out
    }

    /** Heavy spring-cannon Busoshoku Haki impact for Kong Gun release & direct hits */
    private fun synthGear4KongGunImpact(): ShortArray {
        val count = (sampleRate * 0.30).toInt()
        val out = ShortArray(count)
        val rng = Random(4002)
        var bassPhase = 0.0
        var springPhase = 0.0
        var snapPhase = 0.0
        var lfsr = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            val step = 1 + (t * 10.0).toInt()
            if (i % step == 0) lfsr = if (rng.nextBoolean()) 1.0 else -1.0
            val bassFreq = 195.0 * (1.0 - t * 0.82) + 34.0
            val springFreq = 480.0 * (1.0 - t * 0.72) + 120.0 * sin(t * 2.0 * PI * 14.0)
            val snapFreq = 920.0 * (1.0 - t * 0.70) + 240.0
            bassPhase += bassFreq / sampleRate
            springPhase += springFreq / sampleRate
            snapPhase += snapFreq / sampleRate
            val snapEnv = if (t < 0.18) (1.0 - t / 0.18) else 0.0
            val env = (if (t < 0.02) t / 0.02 else 1.0) * (1.0 - t) * (1.0 - t * 0.32)
            val sample = lfsr * 0.54 +
                triangleWave(bassPhase) * 0.48 +
                squareWave(bassPhase * 0.5, 0.5) * 0.24 +
                squareWave(springPhase, 0.25) * 0.22 * (1.0 - t) +
                squareWave(snapPhase, 0.125) * snapEnv * 0.42
            out[i] = toPcm(sample * env * 0.98)
        }
        return out
    }

    /** Crisp metallic-obsidian Busoshoku Haki smack for Kong Organ barrage & Haki armor deflection */
    private fun synthGear4HakiHit(): ShortArray {
        val count = (sampleRate * 0.18).toInt()
        val out = ShortArray(count)
        val rng = Random(4003)
        var phase = 0.0
        var clangPhase = 0.0
        var lfsr = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            val step = 1 + (t * 7.0).toInt()
            if (i % step == 0) lfsr = if (rng.nextBoolean()) 1.0 else -1.0
            val bassFreq = 185.0 * (1.0 - t * 0.78) + 40.0
            phase += bassFreq / sampleRate
            clangPhase += (680.0 * (1.0 - t * 0.48)) / sampleRate
            val env = (1.0 - t) * (1.0 - t * 0.35)
            val clangEnv = if (t < 0.28) (1.0 - t / 0.28) * 0.44 else 0.0
            out[i] = toPcm((lfsr * 0.52 + triangleWave(phase) * 0.46 + squareWave(clangPhase, 0.18) * clangEnv) * env * 0.92)
        }
        return out
    }

    /** Colossal King Kong Gun aerial slam + multi-phase earth-shattering 8-bit seismic tremor */
    private fun synthGear4KingKongSlam(): ShortArray {
        val count = (sampleRate * 0.92).toInt()
        val out = ShortArray(count)
        val rng = Random(4004)
        var bassPhase = 0.0
        var subPhase = 0.0
        var crackPhase = 0.0
        var lfsr = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            // Coarse stepped LFSR noise widens over time to emulate shattering rock & seismic aftershocks
            val step = if (t < 0.12) 1 else (2 + ((t - 0.12) * 24.0).toInt())
            if (i % step == 0) lfsr = if (rng.nextBoolean()) 1.0 else -1.0

            val bassFreq = if (t < 0.14) {
                190.0 * (1.0 - (t / 0.14) * 0.72) + 52.0
            } else {
                56.0 * (1.0 - (t - 0.14) * 0.58) + 22.0
            }
            bassPhase += bassFreq / sampleRate
            subPhase += (bassFreq * 0.5) / sampleRate
            crackPhase += (1280.0 * (1.0 - t * 0.82) + 180.0) / sampleRate

            // Undulating seismic tremor modulation (7.5 Hz tectonic waves + 13 Hz rock shudder)
            val tremorMod = 0.70 + 0.20 * sin(t * 2.0 * PI * 7.5) + 0.10 * sin(t * 2.0 * PI * 13.0)
            val crackEnv = if (t < 0.14) (1.0 - t / 0.14) else 0.0
            val bodyEnv = (if (t < 0.015) t / 0.015 else 1.0) * (1.0 - t * 0.86) * (1.0 - t * 0.28)
            val rumbleMix = (lfsr * 0.66 + triangleWave(subPhase) * 0.48 + squareWave(bassPhase, 0.5) * 0.30) * tremorMod
            val crackMix = (squareWave(crackPhase, 0.15) * 0.44 + lfsr * 0.25) * crackEnv
            out[i] = toPcm((rumbleMix * bodyEnv + crackMix) * 1.0)
        }
        return out
    }

    /** Brilliant resonant steel-on-steel katana clang for Zoro's Steel Parry */
    private fun synthZoroParryClang(): ShortArray {
        val count = (sampleRate * 0.36).toInt()
        val out = ShortArray(count)
        val rng = Random(5001)
        var p1 = 0.0
        var p2 = 0.0
        var p3 = 0.0
        var spark = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            if (i % 2 == 0) spark = rng.nextDouble(-1.0, 1.0)
            p1 += (1760.0 + sin(t * 2.0 * PI * 18.0) * 28.0) / sampleRate
            p2 += (2637.0 - t * 140.0) / sampleRate
            p3 += (3520.0 - t * 260.0) / sampleRate
            val hitEnv = if (t < 0.08) (1.0 - t / 0.08) else 0.0
            val ringEnv = (if (t < 0.01) t / 0.01 else 1.0) * (1.0 - t) * (1.0 - t * 0.45)
            val ring = (squareWave(p1, 0.20) * 0.44 + triangleWave(p2) * 0.38 + squareWave(p3, 0.125) * 0.24) * ringEnv
            val clash = spark * 0.48 * hitEnv
            out[i] = toPcm((ring + clash) * 0.96)
        }
        return out
    }

    /** Compressed emerald air-blade crescent launch for 360 Pound Cannon */
    private fun synthZoro360Cannon(): ShortArray {
        val count = (sampleRate * 0.24).toInt()
        val out = ShortArray(count)
        val rng = Random(5002)
        var phase = 0.0
        var n = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            if (i % 2 == 0) n = rng.nextDouble(-1.0, 1.0)
            val freq = 1620.0 * (1.0 - t * 0.68) + 340.0 + sin(t * 2.0 * PI * 16.0) * 55.0
            phase += freq / sampleRate
            val env = (if (t < 0.05) t / 0.05 else 1.0) * (1.0 - t * 0.82)
            out[i] = toPcm((squareWave(phase, 0.22) * 0.48 + n * 0.52) * env * 0.88)
        }
        return out
    }

    /** Ominous Conqueror Haki flame ignition + triple-steel draw for King of Hell Awakening */
    private fun synthZoroHellActivate(): ShortArray {
        val count = (sampleRate * 0.72).toInt()
        val out = ShortArray(count)
        val rng = Random(5003)
        var subPhase = 0.0
        var steelPhase = 0.0
        var chordPhase = 0.0
        var crackle = 0.0
        val darkNotes = doubleArrayOf(196.0, 233.08, 293.66, 392.0)
        for (i in 0 until count) {
            val t = i.toDouble() / count
            val step = if (t < 0.25) 2 else 1 + ((t - 0.25) * 9.0).toInt()
            if (i % step == 0) crackle = if (rng.nextBoolean()) 1.0 else -1.0
            val sample = if (t < 0.25) {
                val u = t / 0.25
                steelPhase += (1420.0 + u * 1180.0) / sampleRate
                subPhase += (72.0 + u * 110.0) / sampleRate
                val env = (if (u < 0.1) u / 0.1 else 1.0) * (0.7 + 0.3 * u)
                (squareWave(steelPhase, 0.18) * 0.44 + triangleWave(subPhase) * 0.36 + crackle * 0.30) * env
            } else {
                val u = (t - 0.25) / 0.75
                val idx = (u * darkNotes.size).toInt().coerceIn(0, darkNotes.lastIndex)
                val note = darkNotes[idx]
                subPhase += (note * 0.5) / sampleRate
                chordPhase += note / sampleRate
                steelPhase += (note * 3.0) / sampleRate
                val burstEnv = if (u < 0.18) (1.0 - u / 0.18) else 0.0
                val tailEnv = (1.0 - u * 0.78)
                (triangleWave(subPhase) * 0.46 +
                    squareWave(chordPhase, 0.30) * 0.32 +
                    squareWave(steelPhase, 0.125) * 0.20 +
                    crackle * (0.48 * burstEnv + 0.22 * tailEnv)) * tailEnv
            }
            out[i] = toPcm(sample * 0.96)
        }
        return out
    }

    /** Ferocious black-emerald Haki flying blade slash for King of Hell combo */
    private fun synthZoroHellSlash(): ShortArray {
        val count = (sampleRate * 0.16).toInt()
        val out = ShortArray(count)
        val rng = Random(5004)
        var phase = 0.0
        var bassPhase = 0.0
        var n = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            if (i % 2 == 0) n = rng.nextDouble(-1.0, 1.0)
            phase += (1880.0 * (1.0 - t * 0.74) + 260.0) / sampleRate
            bassPhase += (190.0 * (1.0 - t * 0.65) + 55.0) / sampleRate
            val env = (if (t < 0.04) t / 0.04 else 1.0) * (1.0 - t * 0.85)
            out[i] = toPcm((squareWave(phase, 0.18) * 0.42 + triangleWave(bassPhase) * 0.34 + n * 0.44) * env * 0.90)
        }
        return out
    }

    /** Twin supersonic razor-blade sweep for Purgatory Onigiri */
    private fun synthZoroPurgatoryOnigiri(): ShortArray {
        val count = (sampleRate * 0.34).toInt()
        val out = ShortArray(count)
        val rng = Random(5005)
        var p1 = 0.0
        var p2 = 0.0
        var n = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            if (i % 2 == 0) n = rng.nextDouble(-1.0, 1.0)
            val passT = (t * 2.0) % 1.0
            p1 += (2400.0 - passT * 1550.0) / sampleRate
            p2 += (180.0 - passT * 95.0) / sampleRate
            val env = (1.0 - passT * 0.72) * (1.0 - t * 0.35)
            out[i] = toPcm((squareWave(p1, 0.16) * 0.42 + triangleWave(p2) * 0.36 + n * 0.46) * env * 0.94)
        }
        return out
    }

    /** Ominous nine-sword demon aura resonance for Kyutoryu: Asura manifestation */
    private fun synthZoroAsuraAura(): ShortArray {
        val count = (sampleRate * 0.46).toInt()
        val out = ShortArray(count)
        val rng = Random(5006)
        var p1 = 0.0
        var p2 = 0.0
        var p3 = 0.0
        var n = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            if (i % 3 == 0) n = rng.nextDouble(-1.0, 1.0)
            val rise = 1.0 + t * 0.65
            p1 += (110.0 * rise) / sampleRate
            p2 += (164.81 * rise + sin(t * 2.0 * PI * 14.0) * 18.0) / sampleRate
            p3 += (880.0 * rise) / sampleRate
            val env = (if (t < 0.12) t / 0.12 else 1.0) * (0.75 + 0.25 * sin(t * PI))
            out[i] = toPcm((triangleWave(p1) * 0.44 + squareWave(p2, 0.35) * 0.34 + squareWave(p3, 0.15) * 0.18 + n * 0.24) * env * 0.92)
        }
        return out
    }

    /** Dramatic anime delayed nine-blade cut eruption for Kyutoryu: Asura */
    private fun synthZoroAsuraBurst(): ShortArray {
        val count = (sampleRate * 0.62).toInt()
        val out = ShortArray(count)
        val rng = Random(5007)
        var bladePhase = 0.0
        var subPhase = 0.0
        var lfsr = 0.0
        for (i in 0 until count) {
            val t = i.toDouble() / count
            val step = if (t < 0.22) 1 else 2 + ((t - 0.22) * 16.0).toInt()
            if (i % step == 0) lfsr = if (rng.nextBoolean()) 1.0 else -1.0
            val rapidCut = 0.65 + 0.35 * kotlin.math.abs(sin(t * PI * 9.0))
            bladePhase += (2600.0 * (1.0 - t * 0.78) + 320.0) / sampleRate
            subPhase += (140.0 * (1.0 - t * 0.75) + 30.0) / sampleRate
            val env = (if (t < 0.02) t / 0.02 else 1.0) * (1.0 - t * 0.88)
            val sample = (squareWave(bladePhase, 0.14) * 0.42 * rapidCut +
                triangleWave(subPhase) * 0.44 +
                lfsr * 0.56) * env
            out[i] = toPcm(sample * 1.0)
        }
        return out
    }
}
