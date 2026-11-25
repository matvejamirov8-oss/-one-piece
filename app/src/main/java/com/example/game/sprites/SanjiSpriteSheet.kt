package com.example.game.sprites

import androidx.compose.ui.graphics.ImageBitmap

/**
 * 16-bit Pixel Art Sprite Sheet for Vinsmoke Sanji (Black Leg Style — Куроаси).
 * All full-body sprites are 28x32 aligned to foot baseline row 30 to match Luffy & Zoro grounding.
 * Features swept blond hair covering one eye, sleek black double-breasted suit, blue dress shirt
 * with dark tie, dark dress shoes, hands-in-pockets stance, purely acrobatic kick animations,
 * handstand Party Table Kick Course, and blazing Diable Jambe flaming leg poses.
 */
class SanjiSpriteSheet {

    private val palette: Map<Char, Int> = mapOf(
        '.' to 0x00000000,         // Transparent
        'K' to 0xFF181425.toInt(), // Deep pixel-art ink outline
        'Y' to 0xFFFFE45E.toInt(), // Bright golden blond hair
        'G' to 0xFFE6B632.toInt(), // Golden hair midtone
        'O' to 0xFFB8801A.toInt(), // Hair warm shadow
        'S' to 0xFFF2BE96.toInt(), // Warm skin
        'A' to 0xFFD4976C.toInt(), // Skin shading
        'B' to 0xFF3B82F6.toInt(), // Blue dress shirt bright
        'D' to 0xFF1D4ED8.toInt(), // Blue dress shirt shade
        'P' to 0xFF232736.toInt(), // Black suit coat & trousers
        'N' to 0xFF12141E.toInt(), // Suit deep shadow & tie
        'M' to 0xFF3F465E.toInt(), // Suit lapel & crease highlight
        'E' to 0xFF2E3342.toInt(), // Dark dress shoe midtone
        'H' to 0xFF6B7491.toInt(), // Dress shoe specular shine
        'W' to 0xFFFFFBE6.toInt(), // White-hot flame core / cigarette smoke
        'F' to 0xFFFF8A1F.toInt(), // Diable Jambe orange flame
        'R' to 0xFFE62E2D.toInt(), // Diable Jambe crimson flame edge
        'C' to 0xFFA6F0FF.toInt()  // Sky Walk air ring accent
    )

    private fun alignBottom(rows: List<String>, targetBottomRow: Int = 30): List<String> {
        val lastNonEmpty = rows.indexOfLast { line -> line.any { it != '.' } }
        if (lastNonEmpty < 0 || lastNonEmpty == targetBottomRow) return rows
        val shift = targetBottomRow - lastNonEmpty
        val emptyRow = ".".repeat(rows.first().length)
        return if (shift > 0) {
            List(shift) { emptyRow } + rows.dropLast(shift)
        } else {
            val trim = -shift
            rows.drop(trim) + List(trim) { emptyRow }
        }
    }

    val idleFrames: List<DirectionalSprite> = listOf(
        idleFrame0, idleFrame1, idleFrame2, idleFrame1
    ).map { PixelArtBuilder.buildPair(alignBottom(it, 30), palette) }

    val runFrames: List<DirectionalSprite> = listOf(
        runFrame0, runFrame1, runFrame2, runFrame3
    ).map { PixelArtBuilder.buildPair(alignBottom(it, 30), palette) }

    val jumpRise: DirectionalSprite = PixelArtBuilder.buildPair(alignBottom(jumpRiseFrame, 28), palette)
    val jumpFall: DirectionalSprite = PixelArtBuilder.buildPair(alignBottom(jumpFallFrame, 29), palette)
    val landSquash: DirectionalSprite = PixelArtBuilder.buildPair(alignBottom(landFrame, 30), palette)
    val hurtPose: DirectionalSprite = PixelArtBuilder.buildPair(alignBottom(hurtFrame, 30), palette)

    val kickFrames: List<DirectionalSprite> = listOf(
        kickFrame0, kickFrame1, kickFrame2
    ).map { PixelArtBuilder.buildPair(alignBottom(it, 30), palette) }

    val partyTableFrames: List<DirectionalSprite> = listOf(
        partyTableFrame0, partyTableFrame1
    ).map { PixelArtBuilder.buildPair(alignBottom(it, 30), palette) }

    val diableWindup: DirectionalSprite = PixelArtBuilder.buildPair(alignBottom(diableWindupFrame, 30), palette)
    val diableDash: DirectionalSprite = PixelArtBuilder.buildPair(alignBottom(diableDashFrame, 30), palette)

    val hudPortrait: ImageBitmap = PixelArtBuilder.build(portraitRows, palette)
    val iconKick: ImageBitmap = PixelArtBuilder.build(iconKickRows, palette)
    val iconPartyTable: ImageBitmap = PixelArtBuilder.build(iconPartyTableRows, palette)
    val iconDiableJambe: ImageBitmap = PixelArtBuilder.build(iconDiableJambeRows, palette)

    companion object {
        // === IDLE STANCE (28x32) — Cool hands-in-pockets pose, swept blond bangs over one eye ===
        private val idleFrame0 = listOf(
            "............................",
            "............................",
            "..........KKKKKK............",
            "........KKYYYYYYKK..........",
            ".......KYYYYYYYYGGK.........",
            "......KYYYGGYYYYGGOK........",
            "......KGGOKKYYYYYGGK........",
            ".......KKSSKYYYYYSK.........",
            "........KSSKGGGKSKK.........",
            "........KSSKOGKSSSK.........",
            "........KAAASSSSSKK.........",
            ".........KKAAASSK...........",
            "........KKPMBBNMPKK.........",
            ".......KPPPMBNMPPPPK........",
            ".......KPPPMDNMPPPPK........",
            ".......KMPPPNNPPPMPK........",
            ".......KMPPPNNPPPMPK........",
            ".......KPPPPNNPPPPPK........",
            ".......KKPPPNNPPPKKK........",
            "........KNNNNNNNNK..........",
            "........KPPPKKPPPK..........",
            "........KPPPKKPPPK..........",
            "........KPPK..KPPK..........",
            "........KNNK..KNNK..........",
            "........KPPK..KPPK..........",
            "........KPPK..KPPK..........",
            "........KNNK..KNNK..........",
            "........KEEK..KEEK..........",
            ".......KHEEK..KHEEK.........",
            ".......KKKKK..KKKKK.........",
            "............................",
            "............................"
        )

        private val idleFrame1 = listOf(
            "............................",
            "............................",
            "............................",
            "..........KKKKKK............",
            "........KKYYYYYYKK..........",
            ".......KYYYYYYYYGGK.........",
            "......KYYYGGYYYYGGOK........",
            "......KGGOKKYYYYYGGK........",
            ".......KKSSKYYYYYSK.........",
            "........KSSKGGGKSKK.........",
            "........KSSKOGKSSSK.........",
            "........KAAASSSSSKK.........",
            ".........KKAAASSK...........",
            "........KKPMBBNMPKK.........",
            ".......KPPPMBNMPPPPK........",
            ".......KPPPMDNMPPPPK........",
            ".......KMPPPNNPPPMPK........",
            ".......KPPPPNNPPPPPK........",
            ".......KKPPPNNPPPKKK........",
            "........KNNNNNNNNK..........",
            "........KPPPKKPPPK..........",
            "........KPPPKKPPPK..........",
            "........KPPK..KPPK..........",
            "........KNNK..KNNK..........",
            "........KPPK..KPPK..........",
            "........KPPK..KPPK..........",
            "........KNNK..KNNK..........",
            "........KEEK..KEEK..........",
            ".......KHEEK..KHEEK.........",
            ".......KKKKK..KKKKK.........",
            "............................",
            "............................"
        )

        private val idleFrame2 = listOf(
            "............................",
            "............................",
            "..........KKKKKK............",
            "........KKYYYYYYKK..........",
            ".......KYYYYYYYYGGK.........",
            "......KYYYGGYYYYGGOK........",
            "......KGGOKKYYYYYGGK........",
            ".......KKSSKYYYYYSK.........",
            "........KSSKGGGKSKK.........",
            "........KSSKOGKSSSK.........",
            "........KAAASSSSSKK.........",
            ".........KKAAASSK...........",
            "........KKPMBBNMPKK.........",
            ".......KPPPMBNMPPPPK........",
            ".......KPPPMDNMPPPPK........",
            ".......KMPPPNNPPPMPK........",
            ".......KMPPPNNPPPMPK........",
            "......KPPPPPNNPPPPPK........",
            "......KKPPPPNNPPPKKK........",
            "........KNNNNNNNNK..........",
            "........KPPPKKPPPK..........",
            "........KPPPKKPPPK..........",
            "........KPPK..KPPK..........",
            "........KNNK..KNNK..........",
            "........KPPK..KPPK..........",
            "........KPPK..KPPK..........",
            "........KNNK..KNNK..........",
            "........KEEK..KEEK..........",
            ".......KHEEK..KHEEK.........",
            ".......KKKKK..KKKKK.........",
            "............................",
            "............................"
        )

        // === ATHLETIC FAST RUN (4 frames, 28x32) ===
        private val runFrame0 = listOf(
            "............................",
            "............................",
            "............KKKKKK..........",
            "..........KKYYYYYYKK........",
            ".........KYYYYYYYYGGK.......",
            "........KYYYGGYYYYGGOK......",
            "........KGGOKKYYYYYGGK......",
            ".........KKSSKYYYYYSK.......",
            "..........KSSKGGGKSKK.......",
            "..........KAAASSSSSKK.......",
            "...........KKAAASSK.........",
            ".........KKPMBBNMPKK........",
            "........KPPPMBNMPPPPK.......",
            ".......KPPPMMDNMPPPPK.......",
            "......KMPPPPPNNPPPPK........",
            "......KKPPPPNNPPPPPK........",
            "........KKPPNNPPPKK.........",
            ".........KNNNNNNNK..........",
            "........KPPPKKPPPK..........",
            ".......KPPPK..KPPPK.........",
            "......KPPPK....KPPPK........",
            ".....KNNNK......KPPPK.......",
            "....KPPPK........KNNK.......",
            "...KHEEK..........KPPK......",
            "...KKKK............KHEEK....",
            "...................KKKKK....",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................"
        )

        private val runFrame1 = listOf(
            "............................",
            "............................",
            "............................",
            "............KKKKKK..........",
            "..........KKYYYYYYKK........",
            ".........KYYYYYYYYGGK.......",
            "........KYYYGGYYYYGGOK......",
            "........KGGOKKYYYYYGGK......",
            ".........KKSSKYYYYYSK.......",
            "..........KSSKGGGKSKK.......",
            "..........KAAASSSSSKK.......",
            "...........KKAAASSK.........",
            ".........KKPMBBNMPKK........",
            "........KPPPMBNMPPPPK.......",
            ".......KPPPMMDNMPPPPK.......",
            ".......KMPPPPNNPPPPK........",
            "........KPPPNNPPPPK.........",
            "........KNNNNNNNNK..........",
            ".........KPPPKPPK...........",
            ".........KPPPKPPK...........",
            "........KPPPK.KPPK..........",
            "........KNNK..KPPK..........",
            "........KPPK..KNNK..........",
            "........KPPK..KEEK..........",
            ".......KHEEK..KHEEK.........",
            ".......KKKK...KKKK..........",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................"
        )

        private val runFrame2 = listOf(
            "............................",
            "............................",
            "............KKKKKK..........",
            "..........KKYYYYYYKK........",
            ".........KYYYYYYYYGGK.......",
            "........KYYYGGYYYYGGOK......",
            "........KGGOKKYYYYYGGK......",
            ".........KKSSKYYYYYSK.......",
            "..........KSSKGGGKSKK.......",
            "..........KAAASSSSSKK.......",
            "...........KKAAASSK.........",
            ".........KKPMBBNMPKK........",
            "........KPPPMBNMPPPPK.......",
            ".......KPPPMMDNMPPPPK.......",
            "......KMPPPPPNNPPPPK........",
            "......KKPPPPNNPPPPPK........",
            "........KKPPNNPPPKK.........",
            ".........KNNNNNNNK..........",
            "........KPPPKKPPPK..........",
            ".......KPPPK..KPPPK.........",
            "......KPPPK....KPPPK........",
            "......KNNK......KPPPK.......",
            ".......KPPK......KNNNK......",
            ".......KHEEK......KHEEK.....",
            ".......KKKK.......KKKKK.....",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................"
        )

        private val runFrame3 = listOf(
            "............................",
            "............................",
            "............................",
            "............KKKKKK..........",
            "..........KKYYYYYYKK........",
            ".........KYYYYYYYYGGK.......",
            "........KYYYGGYYYYGGOK......",
            "........KGGOKKYYYYYGGK......",
            ".........KKSSKYYYYYSK.......",
            "..........KSSKGGGKSKK.......",
            "..........KAAASSSSSKK.......",
            "...........KKAAASSK.........",
            ".........KKPMBBNMPKK........",
            "........KPPPMBNMPPPPK.......",
            ".......KPPPMMDNMPPPPK.......",
            ".......KMPPPPNNPPPPK........",
            "........KPPPNNPPPPK.........",
            "........KNNNNNNNNK..........",
            ".........KPPKKPPK...........",
            "........KPPK..KPPK..........",
            ".......KPPK...KPPK..........",
            ".......KNNK...KNNK..........",
            "........KPPK..KPPK..........",
            "........KHEEK.KHEEK.........",
            "........KKKK..KKKK..........",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................"
        )

        // === JUMP RISE / FALL / LAND / HURT ===
        private val jumpRiseFrame = listOf(
            "............................",
            "..........KKKKKK............",
            "........KKYYYYYYKK..........",
            ".......KYYYYYYYYGGK.........",
            "......KYYYGGYYYYGGOK........",
            "......KGGOKKYYYYYGGK........",
            ".......KKSSKYYYYYSK.........",
            "........KSSKGGGKSKK.........",
            "........KAAASSSSSKK.........",
            ".........KKAAASSK...........",
            "........KKPMBBNMPKK.........",
            ".......KPPPMBNMPPPPK........",
            "......KPPPMMDNMPPPPK........",
            "......KMPPPPNNPPPPPK........",
            ".......KPPPPNNPPPPK.........",
            "........KNNNNNNNNK..........",
            "........KPPPKKPPPK..........",
            ".......KPPPK..KPPPK.........",
            "......KPPPK....KPPPK........",
            "......KNNK.....KHEEK........",
            ".......KPPK....KKKK.........",
            ".......KHEEK................",
            ".......KKKK.................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................"
        )

        private val jumpFallFrame = listOf(
            "............................",
            "............................",
            "..........KKKKKK............",
            "........KKYYYYYYKK..........",
            ".......KYYYYYYYYGGK.........",
            "......KYYYGGYYYYGGOK........",
            "......KGGOKKYYYYYGGK........",
            ".......KKSSKYYYYYSK.........",
            "........KSSKGGGKSKK.........",
            "........KAAASSSSSKK.........",
            ".........KKAAASSK...........",
            ".......KKKPMBBNMPKKK........",
            "......KPPPPMBNMPPPPPK.......",
            ".....KMPPPPMDNMPPPPMPK......",
            ".....KKPPPPNNPPPPPPKK.......",
            "........KPPPNNPPPK..........",
            "........KNNNNNNNNK..........",
            "........KPPPKKPPPK..........",
            ".......KPPPK..KPPPK.........",
            ".......KNNK....KNNK.........",
            "......KPPPK....KPPPK........",
            "......KHEEK....KHEEK........",
            "......KKKK.....KKKK.........",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................"
        )

        private val landFrame = listOf(
            "............................",
            "............................",
            "............................",
            "............................",
            "..........KKKKKK............",
            "........KKYYYYYYKK..........",
            ".......KYYYYYYYYGGK.........",
            "......KYYYGGYYYYGGOK........",
            "......KGGOKKYYYYYGGK........",
            ".......KKSSKYYYYYSK.........",
            "........KSSKGGGKSKK.........",
            "........KAAASSSSSKK.........",
            "........KKPMBBNMPKK.........",
            ".......KPPPMBNMPPPPK........",
            "......KPPPMMDNMPPPPPK.......",
            "......KMPPPPNNPPPPMPK.......",
            "......KKPPPPNNPPPPPKK.......",
            "........KNNNNNNNNK..........",
            ".......KPPPKKKPPPK..........",
            "......KPPPK...KPPPK.........",
            "......KNNK.....KNNK.........",
            "......KPPK.....KPPK.........",
            ".....KHEEK.....KHEEK........",
            ".....KKKKK.....KKKKK........",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................"
        )

        private val hurtFrame = listOf(
            "............................",
            "............................",
            "............................",
            "........KKKKKK..............",
            "......KKYYYYYYKK............",
            ".....KYYYYYYYYGGK...........",
            "....KYYYGGYYYYGGOK..........",
            "....KGGOKKYYYYYGGK..........",
            ".....KKSSKYYYYYSK...........",
            "......KSSKGGGKSSK...........",
            "......KAAASSSSSK............",
            ".......KKAAASSK.............",
            "......KKPMBBNMPKK...........",
            ".....KPPPMBNMPPPPK..........",
            ".....KPPPMDNMPPPPK..........",
            "......KPPPNNPPPPK...........",
            ".......KNNNNNNNK............",
            ".......KPPPKKPPPK...........",
            "......KPPPK..KPPPK..........",
            "......KNNK....KPPK..........",
            ".....KPPPK.....KNNK.........",
            ".....KHEEK.....KHEEK........",
            ".....KKKK......KKKK.........",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................"
        )

        // === BLACK LEG 3-HIT ACROBATIC KICK COMBO (28x32) ===
        // Kick 0: Lightning High Snap Kick forward
        private val kickFrame0 = listOf(
            "............................",
            "............................",
            ".......KKKKKK...............",
            ".....KKYYYYYYKK.............",
            "....KYYYYYYYYGGK............",
            "...KYYYGGYYYYGGOK...........",
            "...KGGOKKYYYYYGGK...........",
            "....KKSSKYYYYYSK............",
            ".....KSSKGGGKSKK............",
            ".....KAAASSSSSKK............",
            "......KKAAASSK..............",
            ".....KKPMBBNMPKK.......KKK..",
            "....KPPPMBNMPPPPK....KKHEEK.",
            "....KPPPMDNMPPPPK..KKPPPEEK.",
            "....KMPPPNNPPPPPKKKPPPPPKK..",
            ".....KPPPNNPPPPPPPPPNNKK....",
            "......KNNNNNNPPPPPPPKK......",
            ".......KPPPKKKKKKKK.........",
            ".......KPPPK................",
            ".......KNNK.................",
            ".......KPPK.................",
            ".......KPPK.................",
            ".......KNNK.................",
            ".......KEEK.................",
            "......KHEEK.................",
            "......KKKKK.................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................"
        )

        // Kick 1: Low-Leaning Spinning Roundhouse Kick
        private val kickFrame1 = listOf(
            "............................",
            "............................",
            "............................",
            ".....KKKKKK.................",
            "...KKYYYYYYKK...............",
            "..KYYYYYYYYGGK..............",
            ".KYYYGGYYYYGGOK.............",
            ".KGGOKKYYYYYGGK.............",
            "..KKSSKYYYYYSK..............",
            "...KSSKGGGKSKK..............",
            "...KAAASSSSSKK..............",
            "....KKAAASSK................",
            "...KKPMBBNMPKK..............",
            "..KPPPMBNMPPPPK.............",
            "..KPPPMDNMPPPPPKK...........",
            "...KPPPNNPPPPPPPPKKKKKKK....",
            "....KNNNNNNPPPPPPPPPPHEEK...",
            ".....KPPPKKNNNNNNPPPPPHEK...",
            "......KPPPK...KKKKKKKKKK....",
            "......KNNK..................",
            "......KPPK..................",
            "......KPPK..................",
            "......KNNK..................",
            "......KEEK..................",
            ".....KHEEK..................",
            ".....KKKKK..................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................"
        )

        // Kick 2: Acrobatic Overhead Axe-Heel Drop Kick (finisher)
        private val kickFrame2 = listOf(
            ".................KKK........",
            "................KHEEK.......",
            "...............KPPPEK.......",
            "......KKKKKK..KPPPPK........",
            "....KKYYYYYYKKPPPPK.........",
            "...KYYYYYYYYGGKPPPK.........",
            "..KYYYGGYYYYGGOKPPK.........",
            "..KGGOKKYYYYYGGKNNK.........",
            "...KKSSKYYYYYSKKPPK.........",
            "....KSSKGGGKSKKPPK..........",
            "....KAAASSSSSKPPK...........",
            ".....KKAAASSKPPK............",
            "....KKPMBBNMPPK.............",
            "...KPPPMBNMPPPK.............",
            "...KPPPMDNMPPPK.............",
            "....KPPPNNPPPPK.............",
            ".....KNNNNNNNNK.............",
            "......KPPPKPPPK.............",
            "......KPPPK.................",
            "......KNNK..................",
            "......KPPK..................",
            "......KPPK..................",
            "......KNNK..................",
            "......KEEK..................",
            ".....KHEEK..................",
            ".....KKKKK..................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................"
        )

        // === PARTY TABLE KICK COURSE (Handstand Spin, 2 frames, 28x32) ===
        private val partyTableFrame0 = listOf(
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            ".KKKK..................KKKK.",
            "KHEEK..................KHEEK",
            "KPEEPKKKK..........KKKKPEEPK",
            ".KKPPPPPPKK......KKPPPPPPKK.",
            "...KKPPNNPPKKKKKKPPNNPPKK...",
            ".....KKPPPPNNNNNNPPPPKK.....",
            ".......KKKKPPNNPPKKKK.......",
            "..........KPPNNPPK..........",
            ".........KPPPMBNMPK.........",
            ".........KPPPMDNMPK.........",
            "..........KPPBBPPK..........",
            "..........KKSSSSKK..........",
            ".........KYYGGGGYYK.........",
            ".........KYYYYYYYYK.........",
            "..........KKYYYYKK..........",
            ".........KPPKKKKPPK.........",
            "........KPPK....KPPK........",
            "........KSSK....KSSK........",
            "........KKKK....KKKK........",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................"
        )

        private val partyTableFrame1 = listOf(
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "....KKKK............KKKK....",
            "...KHEEK............KHEEK...",
            "..KPPPEPKKK......KKKPEPPPK..",
            "...KKPPPPPPKKKKKKPPPPPPKK...",
            ".....KKPPNNNNNNNNNNPPKK.....",
            ".......KKKPPNNNNPPKKK.......",
            "..........KPPNNPPK..........",
            ".........KPPPMDNMPK.........",
            ".........KPPPMBNMPK.........",
            "..........KPPBBPPK..........",
            "..........KKSSSSKK..........",
            ".........KGGYYYYGGK.........",
            ".........KYYYYYYYYK.........",
            "..........KKYYYYKK..........",
            "..........KPPKKPPK..........",
            ".........KPPK..KPPK.........",
            ".........KSSK..KSSK.........",
            ".........KKKK..KKKK.........",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................"
        )

        // === DIABLE JAMBE (Flame Ignition Windup & Blazing Flying Kick Dash) ===
        private val diableWindupFrame = listOf(
            "............................",
            "............................",
            "..........KKKKKK............",
            "........KKYYYYYYKK..........",
            ".......KYYYYYYYYGGK.........",
            "......KYYYGGYYYYGGOK........",
            "......KGGOKKYYYYYGGK........",
            ".......KKSSKYYYYYSK.........",
            "........KSSKGGGKSKK.........",
            "........KAAASSSSSKK.........",
            ".........KKAAASSK...........",
            "........KKPMBBNMPKK.........",
            ".......KPPPMBNMPPPPK........",
            "......KPPPMMDNMPPPPK........",
            "......KMPPPPNNPPPPPK........",
            ".......KPPPPNNPPPPK...R.....",
            "........KNNNNNNNNK...RF.....",
            "........KPPPKKPPPK..RFW.....",
            "........KPPK..KPPK.RFWFR....",
            "........KNNK..KFFKRFWWFR....",
            "........KPPK..KWWFFFWWFR....",
            "........KPPK..KFFWWFFFR.....",
            "........KNNK..KRFFFWR.......",
            "........KEEK...KFFWR........",
            ".......KHEEK....KRFR........",
            ".......KKKKK.....RR.........",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................"
        )

        private val diableDashFrame = listOf(
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "....KKKKKK..................",
            "..KKYYYYYYKK................",
            ".KYYYYYYYYGGK...............",
            ".KYYYGGYYYYGGK........RR....",
            ".KGGOKKYYYYYGK.......RFFR...",
            "..KKSSKYYYYYSK......RFWWFR..",
            "...KSSKGGGKSKK.....RFWWWWFR.",
            "...KAAASSSSSKKKKKKKFFWWWWFFR",
            "....KKPMBBNMPPPPPPPFFWWWWFFR",
            "...KPPPMBNMPPPPPPPPPPWWWWFR.",
            "...KPPPMDNMPPPPPPPNNKFFWWFR.",
            "....KKPPPPNNPPPPPPKK.RFFFR..",
            "......KKKKKKKKKKKK....RRR...",
            ".......KPPPK................",
            "......KPPPK.................",
            ".....KHEEK..................",
            ".....KKKK...................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................"
        )

        // === HUD PORTRAIT (18x18) ===
        private val portraitRows = listOf(
            "....KKKKKKKKKK....",
            "..KKYYYYYYYYYYKK..",
            ".KYYYYYYYYYYYYGGK.",
            ".KYYYYGGYYYYYYGGK.",
            "KYYYYGOKKYYYYYYGGK",
            "KYYYGOKSSKYYYYYGGK",
            "KYYGOKSSSKYYYYYGGK",
            ".KKOKSSKKKGGGGKSSK",
            "..K.KSSSSKOOGKSSSK",
            "..K.KSSSSKSSKSSSSK",
            "..K..KAAASSSSSSSK.",
            ".....KKAAAASSSKK..",
            "....KKPMBBNMPPKK..",
            "...KPPPMBNMPPPPPK.",
            "..KPPPMMDNMPPPPPPK",
            "..KMPPPPNNPPPPPPPK",
            "..KMPPPPNNPPPPPPPK",
            "..KKKKKKKKKKKKKKKK"
        )

        // === ABILITY ICONS (16x16) ===
        // Icon 1: Black Leg Kick
        private val iconKickRows = listOf(
            "................",
            "..........CCC...",
            "........CCWWC...",
            ".......CWWCC....",
            "......CWWC..KKK.",
            ".....CWWC..KHEEK",
            "....CWWC..KPPPEK",
            "....CWC..KPPPPK.",
            "........KPPPPK..",
            ".......KPPNNK...",
            "......KPPPPK....",
            ".....KPPPPK.....",
            "....KPPNNK......",
            "...KPPPPK.......",
            "...KKKKK........",
            "................"
        )

        // Icon 2: Party Table Kick Course (spinning kicks)
        private val iconPartyTableRows = listOf(
            "................",
            "....CCCCCCCC....",
            "..CCWWWWWWWWCC..",
            ".CWWCCCCCCCCWWC.",
            "KKK..........KKK",
            "HEEK..KKKK..KHEE",
            "PPPKKKPPPPKKKPPP",
            ".KKPPPPNNPPPPKK.",
            "...KKKPPPPKKK...",
            "......KPPK......",
            ".....KPMBPK.....",
            ".....KYYGGK.....",
            "......KKKK......",
            "..CCWWWWWWWWCC..",
            "....CCCCCCCC....",
            "................"
        )

        // Icon 3: Diable Jambe (Flaming Leg)
        private val iconDiableJambeRows = listOf(
            ".......RR.......",
            "......RFFR......",
            ".....RFFWFR.....",
            "....RFFWWFFR....",
            "...RFFWWWWFFR...",
            "...RFWWKKWWFR...",
            "..RFFWKPPKWFFR..",
            "..RFFWKPPKWFFR..",
            "..RFFWKPFFWFFR..",
            "...RFWKFFWWFR...",
            "...RFFWWWWFFR...",
            "....RFWWWWFR....",
            "....RFFWWFFR....",
            ".....RFFFFR.....",
            "......RRRR......",
            "................"
        )
    }
}
