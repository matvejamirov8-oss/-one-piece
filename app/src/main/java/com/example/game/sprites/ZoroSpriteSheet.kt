package com.example.game.sprites

import androidx.compose.ui.graphics.ImageBitmap

/**
 * 16-bit Pixel Art Sprite Sheet for Roronoa Zoro (Santoryu — Three-Sword Style).
 * All full-body sprites are 28x32 aligned to foot baseline row 30 to match Luffy's grounding.
 * Features spiky green hair, white shirt, green haramaki sash, dark pants/boots,
 * three katana scabbards at the waist, two blades in hands, and Wado Ichimonji clenched in teeth.
 */
class ZoroSpriteSheet {

    private val palette: Map<Char, Int> = mapOf(
        '.' to 0x00000000,         // Transparent
        'K' to 0xFF181425.toInt(), // Deep pixel-art ink outline
        'G' to 0xFF38C958.toInt(), // Zoro bright moss-green hair
        'E' to 0xFF1E8A3B.toInt(), // Hair shadow green
        'L' to 0xFF88F59A.toInt(), // Hair specular highlight
        'H' to 0xFF2EA84F.toInt(), // Haramaki sash green
        'D' to 0xFF196630.toInt(), // Haramaki sash dark folds
        'W' to 0xFFF5F8FC.toInt(), // White t-shirt & Wado Ichimonji hilt
        'C' to 0xFFC2D4E6.toInt(), // White shirt shading
        'S' to 0xFFF2BE96.toInt(), // Warm skin
        'T' to 0xFFD4976C.toInt(), // Skin shading
        'P' to 0xFF2B3042.toInt(), // Dark charcoal trousers & boots
        'N' to 0xFF171A26.toInt(), // Trousers/boots deep shadow
        'B' to 0xFFEAF6FF.toInt(), // Katana steel bright edge
        'M' to 0xFF9BB4CC.toInt(), // Katana steel midtone
        'I' to 0xFF5D728A.toInt(), // Katana steel dark spine
        'Y' to 0xFFF7C948.toInt(), // Gold katana tsuba guard & earrings
        'R' to 0xFFB82534.toInt(), // Sandai Kitetsu crimson hilt/scabbard
        'Q' to 0xFF5A3178.toInt(), // Yubashiri dark-violet scabbard
        'Z' to 0xFF2EE66B.toInt()  // Santoryu emerald aura highlight
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

    val slashFrames: List<DirectionalSprite> = listOf(
        slashFrame0, slashFrame1, slashFrame2
    ).map { PixelArtBuilder.buildPair(alignBottom(it, 30), palette) }

    val onigiriWindup: DirectionalSprite = PixelArtBuilder.buildPair(alignBottom(onigiriWindupFrame, 30), palette)
    val onigiriDash: DirectionalSprite = PixelArtBuilder.buildPair(alignBottom(onigiriDashFrame, 30), palette)

    val tatsumakiFrames: List<DirectionalSprite> = listOf(
        tatsumakiFrame0, tatsumakiFrame1
    ).map { PixelArtBuilder.buildPair(alignBottom(it, 30), palette) }

    val hudPortrait: ImageBitmap = PixelArtBuilder.build(portraitRows, palette)
    val iconSlash: ImageBitmap = PixelArtBuilder.build(iconSlashRows, palette)
    val iconOnigiri: ImageBitmap = PixelArtBuilder.build(iconOnigiriRows, palette)
    val iconTatsumaki: ImageBitmap = PixelArtBuilder.build(iconTatsumakiRows, palette)

    companion object {
        // === IDLE BATTLE STANCE (28x32) — 2 katanas in hands, Wado Ichimonji in teeth, 3 scabbards at hip ===
        private val idleFrame0 = listOf(
            "............................",
            "............................",
            ".........KK.KK.KK...........",
            "........KGLKGGKGEK..........",
            ".......KLLGGGGGGEEK.........",
            ".......KGGGGGGGGGEEK........",
            ".......KKEEEEEEEEEEK........",
            "........KSSSSSSSSKK.........",
            "........KSKKSKKSSK..........",
            ".......YKTSSSSSSSK..........",
            "........KTTKWKKKKBBBBBBK....",
            ".........KKWWKKKKMMMMMMK....",
            "........KKWWWWWWKK..........",
            "....K..KWWWWWWCCWWK....K....",
            "...KBMKSSKWWWWCCKSSK..KBMK..",
            "..KBMKKSSKWWWWCCKSSK.KBMK...",
            ".KBMK..KTKHHHHHHKTK.KBMK....",
            ".KIK...KYYHDHHHDKYKKBMK.....",
            "..K...KRRKHHHHHHKWWKIK......",
            ".....KQQKKPPPPPPKK..K.......",
            "....KWWKKPPPNPPPPK..........",
            "...KRRK.KPPKKNPPPK..........",
            "...KK...KPPK.KPPPK..........",
            "........KNNK.KNNNK..........",
            "........KPPK.KPPPK..........",
            "........KPPK.KPPPK..........",
            "........KNNK.KNNNK..........",
            ".......KPPPK.KPPPK..........",
            ".......KNNNK.KNNNK..........",
            "........KKK...KKK...........",
            "............................",
            "............................"
        )

        private val idleFrame1 = listOf(
            "............................",
            "............................",
            "............................",
            ".........KK.KK.KK...........",
            "........KGLKGGKGEK..........",
            ".......KLLGGGGGGEEK.........",
            ".......KGGGGGGGGGEEK........",
            ".......KKEEEEEEEEEEK........",
            "........KSSSSSSSSKK.........",
            "........KSKKSKKSSK..........",
            ".......YKTSSSSSSSK..........",
            "........KTTKWKKKKBBBBBBK....",
            ".........KKWWKKKKMMMMMMK....",
            "........KKWWWWWWKK..........",
            "....K..KWWWWWWCCWWK....K....",
            "...KBMKSSKWWWWCCKSSK..KBMK..",
            "..KBMKKSSKWWWWCCKSSK.KBMK...",
            ".KBMK..KTKHHHHHHKTK.KBMK....",
            ".KIK...KYYHDHHHDKYKKBMK.....",
            "..K...KRRKHHHHHHKWWKIK......",
            ".....KQQKKPPPPPPKK..K.......",
            "....KWWKKPPPNPPPPK..........",
            "...KRRK.KPPKKNPPPK..........",
            "...KK...KPPK.KPPPK..........",
            "........KNNK.KNNNK..........",
            "........KPPK.KPPPK..........",
            "........KNNK.KNNNK..........",
            ".......KPPPK.KPPPK..........",
            ".......KNNNK.KNNNK..........",
            "........KKK...KKK...........",
            "............................",
            "............................"
        )

        private val idleFrame2 = listOf(
            "............................",
            "............................",
            ".........KK.KK.KK...........",
            "........KGLKGGKGEK..........",
            ".......KLLGGGGGGEEK.........",
            ".......KGGGGGGGGGEEK........",
            ".......KKEEEEEEEEEEK........",
            "........KSSSSSSSSKK.........",
            "........KSKKSKKSSK..........",
            ".......YKTSSSSSSSK..........",
            "........KTTKWKKKKBBBBBBK....",
            ".........KKWWKKKKMMMMMMK....",
            "........KKWWWWWWKK..........",
            "...K...KWWWWWWCCWWK.....K...",
            "..KBMK.SSKWWWWCCKSSK...KBMK.",
            ".KBMK.KSSKWWWWCCKSSK..KBMK..",
            ".KIK...KTKHHHHHHKTK..KBMK...",
            "..K....KYYHDHHHDKYK.KBMK....",
            "......KRRKHHHHHHKWWKKIK.....",
            ".....KQQKKPPPPPPKK...K......",
            "....KWWKKPPPNPPPPK..........",
            "...KRRK.KPPKKNPPPK..........",
            "...KK...KPPK.KPPPK..........",
            "........KNNK.KNNNK..........",
            "........KPPK.KPPPK..........",
            "........KPPK.KPPPK..........",
            "........KNNK.KNNNK..........",
            ".......KPPPK.KPPPK..........",
            ".......KNNNK.KNNNK..........",
            "........KKK...KKK...........",
            "............................",
            "............................"
        )

        // === FAST SWORDSMAN RUN (4 frames, leaning forward with twin blades trailing & mouth blade level) ===
        private val runFrame0 = listOf(
            "............................",
            "............................",
            "...........KK.KK.KK.........",
            "..........KGLKGGKGEK........",
            ".........KLLGGGGGGEEK.......",
            ".........KGGGGGGGGGEEK......",
            ".........KKEEEEEEEEEEK......",
            "..........KSSSSSSSSKK.......",
            "..........KSKKSKKSSK........",
            ".........YKTSSSSSSSK........",
            "..........KTTKWKKKKBBBBBBK..",
            "...........KKWWKKKKMMMMMMK..",
            ".........KKWWWWWWKK.........",
            "........KWWWWWWCCWWK........",
            ".......KSSKWWWWCCKSSK.......",
            "..KKKKKKTTKHHHHHHKTTK.......",
            ".KBBBBMMYYHDHHHDHKKYK.......",
            ".KMMMMIIKRRKHHHHHHK.........",
            "..KKKKKKQQKKPPPPPPK.........",
            "......KWWKKPPPNNPPPK........",
            ".....KPPPK....KPPPK.........",
            "....KPPPK......KPPPK........",
            "...KNNNK........KNNNK.......",
            "..KPPPK..........KPPPK......",
            "..KNNK............KNNNK.....",
            "...KK..............KKK......",
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
            "...........KK.KK.KK.........",
            "..........KGLKGGKGEK........",
            ".........KLLGGGGGGEEK.......",
            ".........KGGGGGGGGGEEK......",
            ".........KKEEEEEEEEEEK......",
            "..........KSSSSSSSSKK.......",
            "..........KSKKSKKSSK........",
            ".........YKTSSSSSSSK........",
            "..........KTTKWKKKKBBBBBBK..",
            "...........KKWWKKKKMMMMMMK..",
            ".........KKWWWWWWKK.........",
            "........KWWWWWWCCWWK........",
            "..KKKKKKSSKWWWWCCKSSK.......",
            ".KBBBBMMTTKHHHHHHKTTK.......",
            ".KMMMMIIYYHDHHHDHKKYK.......",
            "..KKKKKKRRKHHHHHHK..........",
            ".......KQQKPPPPPPK..........",
            "........KKPPPNPPPK..........",
            ".........KPPPKPPPK..........",
            "........KPPPK.KPPPK.........",
            "........KNNK...KNNK.........",
            ".......KPPPK...KPPPK........",
            ".......KNNNK...KNNNK........",
            "........KKK.....KKK.........",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................"
        )

        private val runFrame2 = listOf(
            "............................",
            "............................",
            "...........KK.KK.KK.........",
            "..........KGLKGGKGEK........",
            ".........KLLGGGGGGEEK.......",
            ".........KGGGGGGGGGEEK......",
            ".........KKEEEEEEEEEEK......",
            "..........KSSSSSSSSKK.......",
            "..........KSKKSKKSSK........",
            ".........YKTSSSSSSSK........",
            "..........KTTKWKKKKBBBBBBK..",
            "...........KKWWKKKKMMMMMMK..",
            ".........KKWWWWWWKK.........",
            "........KWWWWWWCCWWK........",
            ".......KSSKWWWWCCKSSK.......",
            ".KKKKKKKTTKHHHHHHKTTK.......",
            "KBBBBMMMYYHDHHHDHKKYK.......",
            "KMMMMIIIKRRKHHHHHHK.........",
            ".KKKKKKKQQKKPPPPPPK.........",
            ".......KWWKKPPPNNPPK........",
            "......KPPPK..KPPPK..........",
            ".....KPPPK....KPPPK.........",
            ".....KNNNK....KNNNK.........",
            "......KPPK.....KPPPK........",
            "......KNNK......KNNNK.......",
            ".......KK........KKK........",
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
            "...........KK.KK.KK.........",
            "..........KGLKGGKGEK........",
            ".........KLLGGGGGGEEK.......",
            ".........KGGGGGGGGGEEK......",
            ".........KKEEEEEEEEEEK......",
            "..........KSSSSSSSSKK.......",
            "..........KSKKSKKSSK........",
            ".........YKTSSSSSSSK........",
            "..........KTTKWKKKKBBBBBBK..",
            "...........KKWWKKKKMMMMMMK..",
            ".........KKWWWWWWKK.........",
            "........KWWWWWWCCWWK........",
            "..KKKKKKSSKWWWWCCKSSK.......",
            ".KBBBBMMTTKHHHHHHKTTK.......",
            ".KMMMMIIYYHDHHHDHKKYK.......",
            "..KKKKKKRRKHHHHHHK..........",
            ".......KQQKPPPPPPK..........",
            "........KKPPPNPPPK..........",
            "........KPPPKPPPK...........",
            ".......KPPPK.KPPK...........",
            ".......KNNK..KNNK...........",
            "......KPPPK..KPPPK..........",
            "......KNNNK..KNNNK..........",
            ".......KKK....KKK...........",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................"
        )

        // === JUMP RISE & FALL ===
        private val jumpRiseFrame = listOf(
            "............................",
            ".........KK.KK.KK...........",
            "........KGLKGGKGEK..........",
            ".......KLLGGGGGGEEK.........",
            ".......KGGGGGGGGGEEK........",
            ".......KKEEEEEEEEEEK........",
            "........KSSSSSSSSKK.........",
            "........KSKKSKKSSK..........",
            ".......YKTSSSSSSSK..........",
            "....K...KTTKWKKKKBBBBBBK....",
            "...KBMK..KKWWKKKKMMMMMMK....",
            "..KBMK..KKWWWWWWKK....K.....",
            ".KBMK..KSSKWWWWCCKSSKKBMK...",
            ".KIK...KTTKHHHHHHKTTKBMK....",
            "..K....KYYHDHHHDHKYKKIK.....",
            "......KRRKHHHHHHKWWK.K......",
            ".....KQQKKPPPPPPKK..........",
            "....KWWKKPPPNPPPPK..........",
            ".......KPPPK.KPPPK..........",
            "......KPPPK...KPPPK.........",
            "......KNNK.....KNNK.........",
            ".....KPPPK.....KPPPK........",
            ".....KNNNK......KNNK........",
            "......KKK........KK.........",
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
            "...K.....KK.KK.KK.....K.....",
            "..KBMK..KGLKGGKGEK...KBMK...",
            ".KBMK..KLLGGGGGGEEK..KBMK...",
            ".KIK...KGGGGGGGGGEEK.KIK....",
            "..K....KKEEEEEEEEEEK..K.....",
            "........KSSSSSSSSKK.........",
            "........KSKKSKKSSK..........",
            ".......YKTSSSSSSSK..........",
            "........KTTKWKKKKBBBBBBK....",
            ".......KKWWKKKKKKMMMMMMK....",
            "......KSSKWWWWWWKKSSK.......",
            "......KTTKWWWWCCWKTTK.......",
            ".......KKYHHHHHHHYKK........",
            "......KRRKHDHHHDHKWWK.......",
            ".....KQQKKPPPPPPKK..........",
            "........KPPPNPPPPK..........",
            ".......KPPPK.KPPPK..........",
            ".......KNNK...KNNK..........",
            "......KPPPK...KPPPK.........",
            "......KNNNK...KNNNK.........",
            ".......KKK.....KKK..........",
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
            ".........KK.KK.KK...........",
            "........KGLKGGKGEK..........",
            ".......KLLGGGGGGEEK.........",
            ".......KGGGGGGGGGEEK........",
            ".......KKEEEEEEEEEEK........",
            "........KSSSSSSSSKK.........",
            "........KSKKSKKSSK..........",
            ".......YKTSSSSSSSK..........",
            "........KTTKWKKKKBBBBBBK....",
            ".........KKWWKKKKMMMMMMK....",
            ".......KKWWWWWWWWKK.........",
            "..KKKKKSSKWWWWCCKSSKKKKK....",
            ".KBBBMMTTKHHHHHHKTTMMBBBK...",
            ".KMMMIIYYHDHHHDHKYYIIMMMK...",
            "..KKKKKRRKPPPPPPKRRKKKKK....",
            "......KPPPK....KPPPK........",
            ".....KPPPK......KPPPK.......",
            ".....KNNNK......KNNNK.......",
            "....KPPPK........KPPPK......",
            "....KNNNK........KNNNK......",
            ".....KKK..........KKK.......",
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
            "......KK.KK.KK..............",
            ".....KGLKGGKGEK.............",
            "....KLLGGGGGGEEK............",
            "....KGGGGGGGGGEEK...........",
            "....KKEEEEEEEEEEK...........",
            ".....KSSSSSSSSKK............",
            ".....KSKWKSKWSSK............",
            "....YKTSSSSSSSK.............",
            ".....KTTKWWSSSK.............",
            "......KKKTTTTKK.............",
            ".....KKWWWWWWKK.............",
            "...KKSSKWWWWCCKSSK..........",
            "..KTTKK.KWWWWCCKTTK.........",
            "...KK...KHHHHHHK.KK.........",
            ".......KHDHHHDHK............",
            "......KRRKPPPPPPK...........",
            ".....KQQKKPPN.PPPK..........",
            ".....KPPPK.....KPPPK........",
            "....KPPPK.......KPPPK.......",
            "....KNNK.........KNNK.......",
            "...KPPPK.........KPPPK......",
            "...KNNNK.........KNNNK......",
            "....KKK...........KKK.......",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................"
        )

        // === SANTORYU BASIC SLASH COMBO (3 dynamic sword-swing poses) ===
        // Frame 0: Overhead diagonal downward cleave
        private val slashFrame0 = listOf(
            "............................",
            "............................",
            "..........KK.KK.KK..........",
            ".........KGLKGGKGEK.........",
            "........KLLGGGGGGEEK........",
            "........KGGGGGGGGGEEK.......",
            "........KKEEEEEEEEEEK.......",
            ".........KSSSSSSSSKK........",
            ".........KSKKSKKSSK.........",
            "........YKTSSSSSSSK.........",
            ".........KTTKWKKKKBBBBBBBBK.",
            "..........KKWWKKKKMMMMMMMMK.",
            "........KKWWWWWWKKK.........",
            ".......KWWWWWWCCWSSKKKKKK...",
            "......KSSKWWWWCCKTTYYBBBBBK.",
            "......KTTKHHHHHHK..KKMMMMMK.",
            ".......KKHDHHHDHK...KBBBBBK.",
            ".....KRRKHHHHHHK.....KMMMMK.",
            "....KQQKKPPPPPPK......KKKK..",
            "...KWWKKPPPNPPPPK...........",
            "......KPPPK..KPPPK..........",
            ".....KPPPK....KPPPK.........",
            ".....KNNK......KNNK.........",
            "....KPPPK......KPPPK........",
            "....KNNNK......KNNNK........",
            ".....KKK........KKK.........",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................"
        )

        // Frame 1: Sweeping upward reverse cross-slash
        private val slashFrame1 = listOf(
            "......................KKKK..",
            ".....................KBBBBK.",
            "....................KBBMMMK.",
            "..........KK.KK.KK.KBBMMK...",
            ".........KGLKGGKGEKBBMMK....",
            "........KLLGGGGGGEEKMMK.....",
            "........KGGGGGGGGGEEKK......",
            "........KKEEEEEEEEEEK.......",
            ".........KSSSSSSSSKK........",
            ".........KSKKSKKSSK.........",
            "........YKTSSSSSSSK.........",
            ".........KTTKWKKKKBBBBBBBK..",
            "..........KKWWKKKKMMMMMMMK..",
            "........KKWWWWWWKKSSKYYK....",
            ".......KWWWWWWCCWKTTKBBBK...",
            "......KSSKHHHHHHK.KKKMMMK...",
            "......KTTKHDHHHDHK...KKK....",
            ".....KRRKHHHHHHK............",
            "....KQQKKPPPPPPK............",
            "...KWWKKPPPNPPPPK...........",
            "......KPPPK..KPPPK..........",
            ".....KPPPK....KPPPK.........",
            ".....KNNK......KNNK.........",
            "....KPPPK......KPPPK........",
            "....KNNNK......KNNNK........",
            ".....KKK........KKK.........",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................"
        )

        // Frame 2: Santoryu 3-blade X-finisher thrust
        private val slashFrame2 = listOf(
            "............................",
            "............................",
            "...........KK.KK.KK.........",
            "..........KGLKGGKGEK........",
            ".........KLLGGGGGGEEK.......",
            ".........KGGGGGGGGGEEK......",
            ".........KKEEEEEEEEEEK......",
            "..........KSSSSSSSSKK.......",
            "..........KSKKSKKSSK........",
            ".........YKTSSSSSSSK........",
            "..........KTTKWKKKKBBBBBBBBK",
            "...........KKWWKKKKMMMMMMMMK",
            "........KKWWWWWWKK..........",
            ".......KWWWWWWCCWSSKKKKKKK..",
            "......KSSKWWWWCCKTTYYBBBBBBK",
            "......KTTKHHHHHHK..KKMMMMMMK",
            ".....KRRKHDHHHDHK...KBBBBBBK",
            "....KQQKKHHHHHHK....KMMMMMMK",
            "....KWWKKPPPPPPK.....KKKKKK.",
            ".......KPPPK..KPPPK.........",
            "......KPPPK....KPPPK........",
            ".....KPPPK......KPPPK.......",
            ".....KNNK........KNNK.......",
            "....KPPPK........KPPPK......",
            "....KNNNK........KNNNK......",
            ".....KKK..........KKK.......",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................"
        )

        // === ONIGIRI (Demon Slash) — Crossed-sword crouch windup & high-speed dash-through ===
        private val onigiriWindupFrame = listOf(
            "............................",
            "............................",
            "............................",
            "............................",
            "...........KK.KK.KK.........",
            "..........KGLKGGKGEK........",
            ".........KLLGGGGGGEEK.......",
            ".........KGGGGGGGGGEEK......",
            ".........KKEEEEEEEEEEK......",
            "..........KSSSSSSSSKK.......",
            "..........KSZZSKZZSK........",
            ".........YKTSSSSSSSK........",
            "..........KTTKWKKKKBBBBBBK..",
            "....KKKK...KKWWKKKKMMMMMMK..",
            "...KBBBBKKWWWWWWKKBBBBK.....",
            "..KBBMMKSSKWWWWCCKSSKMMK....",
            "...KKKKKTTKHHHHHHKTTKKK.....",
            "......KKYYHDHHHDHKKYYK......",
            ".....KRRKHHHHHHHKWWK........",
            "....KQQKKPPPPPPPPKK.........",
            "...KPPPK..........KPPPK.....",
            "..KPPPK............KPPPK....",
            "..KNNNK............KNNNK....",
            "..KPPPK............KPPPK....",
            "..KNNNK............KNNNK....",
            "...KKK..............KKK.....",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................"
        )

        private val onigiriDashFrame = listOf(
            "............................",
            "............................",
            "............................",
            ".............KK.KK.KK.......",
            "............KGLKGGKGEK......",
            "...........KLLGGGGGGEEK.....",
            "...........KGGGGGGGGGEEK....",
            "...........KKEEEEEEEEEEK....",
            "............KSSSSSSSSKK.....",
            "............KSZZSKZZSK......",
            "...........YKTSSSSSSSK......",
            "............KTTKWKKKKBBBBBBK",
            ".KKKKKKKK....KKWWKKKKMMMMMMK",
            "KBBBBBBBBK.KKWWWWWWKK.......",
            "KMMMMMMMMKKWWWWWWCCWSSK.....",
            ".KKKKKKKKKSSKHHHHHHKTTK.....",
            "KBBBBBBBBKTTKHDHHHDHKYK.....",
            "KMMMMMMMMKRRKPPPPPPKK.......",
            ".KKKKKKKKKQQKPPPNNPPPK......",
            "........KPPPK....KPPPK......",
            ".......KPPPK......KPPPK.....",
            "......KNNNK........KNNNK....",
            ".....KPPPK..........KPPPK...",
            ".....KNNK............KNNK...",
            "......KK..............KK....",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................"
        )

        // === TATSUMAKI (Dragon Twister) — 2-frame spinning whirlwind blade stance ===
        private val tatsumakiFrame0 = listOf(
            "............................",
            "............................",
            "..........KK.KK.KK..........",
            ".........KGLKGGKGEK.........",
            "........KLLGGGGGGEEK........",
            "........KGGGGGGGGGEEK.......",
            "........KKEEEEEEEEEEK.......",
            ".........KSSSSSSSSKK........",
            ".........KSZZSKZZSK.........",
            "........YKTSSSSSSSK.........",
            ".KKKKKKK..KTTKWKKKKBBBBBBBK.",
            "KBBBBBBBK..KKWWKKKKMMMMMMMK.",
            "KMMMMMMMK.KKWWWWWWKK........",
            ".KKKKKKKYYSSKWWWWCCKSSYYKKKK",
            ".......KTTKWWWWWWCCKTTKBBBBK",
            "........KKKHHHHHHHHKK.KMMMMK",
            ".........KHDHHHHHHDK...KKKK.",
            ".........KPPPPPPPPK.........",
            "........KPPPNNPPPPK.........",
            "........KPPPK.KPPPK.........",
            "........KPPK...KPPK.........",
            "........KNNK...KNNK.........",
            ".......KPPPK...KPPPK........",
            ".......KNNNK...KNNNK........",
            "........KKK.....KKK.........",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................"
        )

        private val tatsumakiFrame1 = listOf(
            "............................",
            "............................",
            "..........KK.KK.KK..........",
            ".........KGEKGGKGLK.........",
            "........KEEGGLGGGGKK........",
            "........KEGGGGGGGGGK........",
            "........KEEEEEEEEEEKK.......",
            ".........KKSSSSSSSSK........",
            "..........KSSKZZSZZK........",
            "..........KSSSSSSSTKY.......",
            ".KBBBBBBBKKKKWKKTTK..KKKKKK.",
            ".KMMMMMMMKKKKWWKK...KBBBBBBK",
            ".........KKWWWWWWKK.KMMMMMMK",
            "KKKKYYSSKCCWWWWKSSYYKKKKKKK.",
            "KBBBBKTTKCCWWWWWWKTTK.......",
            "KMMMMK.KKHHHHHHHHKKK........",
            ".KKKK...KDHHHHHHHDK.........",
            ".........KPPPPPPPPK.........",
            "........KPPPPNNPPPK.........",
            "........KPPPK.KPPPK.........",
            "........KPPK...KPPK.........",
            "........KNNK...KNNK.........",
            ".......KPPPK...KPPPK........",
            ".......KNNNK...KNNNK........",
            "........KKK.....KKK.........",
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
            "....KK.KK.KK......",
            "...KGLKGGKGEK.....",
            "..KLLGGGGGGEEK....",
            "..KGGGGGGGGGEEK...",
            "..KGGEEEEEEEGGK...",
            "..KKEEEEEEEEKK....",
            "...KSSSSSSSSK.....",
            "...KSSKKSKKSK.....",
            "..YKTSSTSSSTK.....",
            "...KTSSSSSSSK.....",
            "...KTTKWKKKKBBBK..",
            "....KKWWKKKKMMMK..",
            ".....KTTTTTTK.....",
            "...KKWWWWWWWWKK...",
            "..KWWWWWWCCWWWWK..",
            "..KWWWCWWWWCWWWK..",
            "...KHHHHHHHHHHK...",
            "....KKKKKKKKKK...."
        )

        // === HUD ABILITY ICONS (16x16) ===
        private val iconSlashRows = listOf(
            ".............KK.",
            "...........KKZBK",
            ".........KKZZBBK",
            "........KZZBBMMK",
            ".......KZZBBMMK.",
            "......KZZBBMMK..",
            ".....KZZBBMMK...",
            "....KZZBBMMK....",
            "...KZZBBMMK.....",
            "..KZZYBBMK......",
            ".KZZKYWMK.......",
            "KZZK.KWWK.......",
            ".KK.KWWKK.......",
            "...KWWK.........",
            "..KWWK..........",
            "..KKK..........."
        )

        private val iconOnigiriRows = listOf(
            ".KK..........KK.",
            "KZZK........KZZK",
            ".KBBK......KBBK.",
            "..KBBK....KBBK..",
            "...KBBK..KBBK...",
            "....KBBKKBBK....",
            ".....KBBBBK.....",
            "....KZZWWZZK....",
            "....KZZWWZZK....",
            ".....KBBBBK.....",
            "....KBBKKBBK....",
            "...KBBK..KBBK...",
            "..KYYK....KYYK..",
            ".KWWK......KRRK.",
            "KWWK........KRRK",
            ".KK..........KK."
        )

        private val iconTatsumakiRows = listOf(
            "....KKKKKKKK....",
            "..KKZZZZZZZZKK..",
            ".KZZWWWWWWWWZZK.",
            "..KKZZGGGGZZKK..",
            "....KKZZZZKK....",
            "..KKZZWWWWZZKK..",
            ".KZZGGGGGGGGZZK.",
            "..KKZZWWWWZZKK..",
            "....KKZZZZKK....",
            "...KZZWWWWZZK...",
            "....KKGGGGKK....",
            ".....KWWWWK.....",
            ".....KZZZZK.....",
            "......KGGK......",
            "......KZZK......",
            ".......KK......."
        )
    }
}
