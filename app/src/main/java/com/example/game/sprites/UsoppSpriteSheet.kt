package com.example.game.sprites

import androidx.compose.ui.graphics.ImageBitmap

/**
 * 16-bit Pixel Art Sprite Sheet for Usopp (Sniper King — Kabuto Slingshot).
 * All full-body sprites are 28x32 aligned to foot baseline row 30 to match the Straw Hat crew.
 * Features signature long nose, curly black hair, bandana + sniper goggles on forehead,
 * brown overalls, long Kabuto slingshot, metal pellets, Exploding Star, and blazing Firebird Star!
 */
class UsoppSpriteSheet {

    private val palette: Map<Char, Int> = mapOf(
        '.' to 0x00000000,         // Transparent
        'K' to 0xFF181425.toInt(), // Deep pixel-art ink outline
        'N' to 0xFF222433.toInt(), // Curly black hair highlight
        'S' to 0xFFE8B084.toInt(), // Warm tan skin & long nose
        'A' to 0xFFC78B5E.toInt(), // Skin shading
        'G' to 0xFF4F7A38.toInt(), // Olive bandana & Kabuto staff body
        'D' to 0xFF2F4E22.toInt(), // Bandana & Kabuto deep green shade
        'C' to 0xFF7BE8FF.toInt(), // Sniper goggle glass cyan
        'Y' to 0xFFFFD84D.toInt(), // Goggle gold frame / waist sash / Firebird yellow
        'B' to 0xFF8C5A32.toInt(), // Brown overalls base
        'M' to 0xFF5E391D.toInt(), // Brown overalls shadow & boots
        'T' to 0xFFB87F4B.toInt(), // Overalls highlight & Kabuto wood
        'W' to 0xFFFFFBE6.toInt(), // White armband / hot core / pellet glint
        'E' to 0xFF6B748C.toInt(), // Steel pellet metal midtone
        'L' to 0xFFAAB4CC.toInt(), // Steel pellet specular shine
        'O' to 0xFFFF7A1A.toInt(), // Firebird & Exploding Star blaze orange
        'R' to 0xFFE62E2D.toInt()  // Firebird & Exploding Star crimson fire
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

    val aimPullPose: DirectionalSprite = PixelArtBuilder.buildPair(alignBottom(aimPullFrame, 30), palette)
    val fireReleasePose: DirectionalSprite = PixelArtBuilder.buildPair(alignBottom(fireReleaseFrame, 30), palette)
    val specialChargePose: DirectionalSprite = PixelArtBuilder.buildPair(alignBottom(specialChargeFrame, 30), palette)

    val metalPellet: DirectionalSprite = PixelArtBuilder.buildPair(metalPelletRows, palette)
    val explodingStar: ImageBitmap = PixelArtBuilder.build(explodingStarRows, palette)
    val firebirdFrames: List<DirectionalSprite> = listOf(
        PixelArtBuilder.buildPair(firebirdFrame0, palette),
        PixelArtBuilder.buildPair(firebirdFrame1, palette)
    )

    val hudPortrait: ImageBitmap = PixelArtBuilder.build(portraitRows, palette)
    val iconShot: ImageBitmap = PixelArtBuilder.build(iconShotRows, palette)
    val iconExplodingStar: ImageBitmap = PixelArtBuilder.build(iconExplodingStarRows, palette)
    val iconFirebird: ImageBitmap = PixelArtBuilder.build(iconFirebirdRows, palette)

    companion object {
        // === IDLE STANCE (28x32) — Curly hair, goggles+bandana, LONG NOSE, brown overalls, Kabuto ===
        private val idleFrame0 = listOf(
            "............................",
            "...................K.K......",
            "........KKKKKK....KGDGK.....",
            "......KKGGGGGGKK...KDK......",
            ".....KGYYCCYYCCGK..KGK......",
            "....KNNKKGGGGKKNNK.KDK......",
            "...KNNKKSSSSSSKNNK.KGK......",
            "...KNKKSSKSSKSSKKKKDK.......",
            "....KKKSSSSSSSSSSSGGK.......",
            ".....KKSSAAAAAKKKKGK........",
            ".......KKSSSSKK..KDK........",
            ".......KSSBBSSK.KGK.........",
            "......KSSKBBKSSKDK..........",
            "......KWWKBBKWWKGK..........",
            "......KSSKBTKSSKK...........",
            ".......KKBBTBKK.............",
            ".......KYYYYYYK.............",
            ".......KBBBBBBK.............",
            ".......KBBMMBBK.............",
            ".......KBBKKBBK.............",
            ".......KBBK.KBBK............",
            ".......KMMK.KMMK............",
            ".......KBBK.KBBK............",
            ".......KBBK.KBBK............",
            ".......KMMK.KMMK............",
            ".......KMMK.KMMK............",
            "......KMMMK.KMMMK...........",
            "......KKKKK.KKKKK...........",
            "............................",
            "............................",
            "............................",
            "............................"
        )

        private val idleFrame1 = listOf(
            "............................",
            "............................",
            "...................K.K......",
            "........KKKKKK....KGDGK.....",
            "......KKGGGGGGKK...KDK......",
            ".....KGYYCCYYCCGK..KGK......",
            "....KNNKKGGGGKKNNK.KDK......",
            "...KNNKKSSSSSSKNNK.KGK......",
            "...KNKKSSKSSKSSKKKKDK.......",
            "....KKKSSSSSSSSSSSGGK.......",
            ".....KKSSAAAAAKKKKGK........",
            ".......KKSSSSKK..KDK........",
            ".......KSSBBSSK.KGK.........",
            "......KSSKBBKSSKDK..........",
            "......KWWKBBKWWKGK..........",
            "......KSSKBTKSSKK...........",
            ".......KKBBTBKK.............",
            ".......KYYYYYYK.............",
            ".......KBBBBBBK.............",
            ".......KBBMMBBK.............",
            ".......KBBKKBBK.............",
            ".......KBBK.KBBK............",
            ".......KMMK.KMMK............",
            ".......KBBK.KBBK............",
            ".......KMMK.KMMK............",
            ".......KMMK.KMMK............",
            "......KMMMK.KMMMK...........",
            "......KKKKK.KKKKK...........",
            "............................",
            "............................",
            "............................",
            "............................"
        )

        private val idleFrame2 = listOf(
            "...................K.K......",
            "........KKKKKK....KGDGK.....",
            "......KKGGGGGGKK...KDK......",
            ".....KGYYCCYYCCGK..KGK......",
            "....KNNKKGGGGKKNNK.KDK......",
            "...KNNKKSSSSSSKNNK.KGK......",
            "...KNKKSSKSSKSSKKKKDK.......",
            "....KKKSSSSSSSSSSSGGK.......",
            ".....KKSSAAAAAKKKKGK........",
            ".......KKSSSSKK..KDK........",
            ".......KSSBBSSK.KGK.........",
            "......KSSKBBKSSKDK..........",
            "......KWWKBBKWWKGK..........",
            "......KSSKBTKSSKK...........",
            ".......KKBBTBKK.............",
            ".......KYYYYYYK.............",
            ".......KBBBBBBK.............",
            ".......KBBMMBBK.............",
            ".......KBBKKBBK.............",
            ".......KBBK.KBBK............",
            ".......KMMK.KMMK............",
            ".......KBBK.KBBK............",
            ".......KBBK.KBBK............",
            ".......KMMK.KMMK............",
            ".......KMMK.KMMK............",
            "......KMMMK.KMMMK...........",
            "......KKKKK.KKKKK...........",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................"
        )

        // === RUN CYCLE (4 frames, 28x32) ===
        private val runFrame0 = listOf(
            "............................",
            "............................",
            ".........KKKKKK.............",
            ".......KKGGGGGGKK...........",
            "......KGYYCCYYCCGK..........",
            ".....KNNKKGGGGKKNNK.........",
            "....KNNKKSSSSSSKNNK.........",
            "....KNKKSSKSSKSSKK..........",
            ".....KKKSSSSSSSSSSSK........",
            "......KKSSAAAAAKKKK.........",
            "........KKSSSSKK............",
            ".......KSSBBSSK.............",
            "...KKKKKWWBBWWKK............",
            "..KGGDDSSKBBKSSK............",
            "...KKKKKKKBTKKK.............",
            ".......KYYYYYYK.............",
            ".......KBBBBBBK.............",
            ".......KBBMMBBK.............",
            "......KBBK..KBBK............",
            ".....KBBK....KBBK...........",
            "....KMMK......KMMK..........",
            "....KBBK.......KBBK.........",
            "...KMMK.........KMMK........",
            "..KMMMK.........KMMMK.......",
            "..KKKKK.........KKKKK.......",
            "............................",
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
            ".........KKKKKK.............",
            ".......KKGGGGGGKK...........",
            "......KGYYCCYYCCGK..........",
            ".....KNNKKGGGGKKNNK.........",
            "....KNNKKSSSSSSKNNK.........",
            "....KNKKSSKSSKSSKK..........",
            ".....KKKSSSSSSSSSSSK........",
            "......KKSSAAAAAKKKK.........",
            "........KKSSSSKK............",
            ".......KSSBBSSK.............",
            "....KKKKWWBBWWK.............",
            "...KGGDSSKBBKSSK............",
            "....KKKKKKBTKK..............",
            ".......KYYYYYYK.............",
            ".......KBBBBBBK.............",
            ".......KBBMMBBK.............",
            "........KBBKBBK.............",
            "........KMMKMMK.............",
            "........KBBKBBK.............",
            "........KMMKMMK.............",
            ".......KMMMKMMK.............",
            ".......KKKKKKKK.............",
            "............................",
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
            ".........KKKKKK.............",
            ".......KKGGGGGGKK...........",
            "......KGYYCCYYCCGK..........",
            ".....KNNKKGGGGKKNNK.........",
            "....KNNKKSSSSSSKNNK.........",
            "....KNKKSSKSSKSSKK..........",
            ".....KKKSSSSSSSSSSSK........",
            "......KKSSAAAAAKKKK.........",
            "........KKSSSSKK............",
            ".......KSSBBSSK.............",
            ".......KWWBBWWKKKKKK........",
            ".......KSSBBSSGGDDGGK.......",
            "........KKBTKKKKKK..........",
            ".......KYYYYYYK.............",
            ".......KBBBBBBK.............",
            ".......KBBMMBBK.............",
            ".......KBBK..KBBK...........",
            "......KBBK....KBBK..........",
            ".....KMMK......KMMK.........",
            "....KBBK........KBBK........",
            "....KMMK.........KMMK.......",
            "...KMMMK.........KMMMK......",
            "...KKKKK.........KKKKK......",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................"
        )

        private val runFrame3 = runFrame1

        // === JUMP RISE / FALL / LAND / HURT ===
        private val jumpRiseFrame = listOf(
            "............................",
            "........KKKKKK..............",
            "......KKGGGGGGKK....K.K.....",
            ".....KGYYCCYYCCGK..KGDGK....",
            "....KNNKKGGGGKKNNK..KDK.....",
            "...KNNKKSSSSSSKNNK.KGK......",
            "...KNKKSSKSSKSSKK..KDK......",
            "....KKKSSSSSSSSSSSKGK.......",
            ".....KKSSAAAAAKKKKKK........",
            ".......KSSBBSSK.............",
            "......KWWKBBKWWK............",
            "......KSSKBTKSSK............",
            ".......KYYYYYYK.............",
            ".......KBBBBBBK.............",
            "......KBBK..KBBK............",
            ".....KBBK....KBBK...........",
            ".....KMMK....KMMK...........",
            ".....KMMK....KBBK...........",
            "....KMMMK....KMMK...........",
            "....KKKKK...KMMMK...........",
            "............KKKKK...........",
            "............................",
            "............................",
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
            "........KKKKKK..............",
            "......KKGGGGGGKK............",
            ".....KGYYCCYYCCGK...........",
            "....KNNKKGGGGKKNNK..........",
            "...KNNKKSSSSSSKNNK..........",
            "...KNKKSSKSSKSSKK....K.K....",
            "....KKKSSSSSSSSSSSK.KGDGK...",
            ".....KKSSAAAAAKKKK...KDK....",
            ".......KSSBBSSK.....KGK.....",
            "......KWWKBBKWWKKKKKDK......",
            "......KSSKBTKSSKGGGGK.......",
            ".......KYYYYYYK.............",
            ".......KBBBBBBK.............",
            ".......KBBKKBBK.............",
            "......KBBK..KBBK............",
            "......KMMK..KMMK............",
            ".....KBBK....KBBK...........",
            ".....KMMK....KMMK...........",
            "....KMMMK....KMMMK..........",
            "....KKKKK....KKKKK..........",
            "............................",
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
            "........KKKKKK..............",
            "......KKGGGGGGKK............",
            ".....KGYYCCYYCCGK...........",
            "....KNNKKGGGGKKNNK..........",
            "...KNNKKSSSSSSKNNK..........",
            "...KNKKSSKSSKSSKK...........",
            "....KKKSSSSSSSSSSSK.........",
            ".....KKSSAAAAAKKKK..........",
            "......KSSBBSSKK.............",
            ".....KWWKBBKWWKKKK..........",
            ".....KSSKBTKSSGGGGK.........",
            "......KYYYYYYK..............",
            "......KBBBBBBK..............",
            "......KBBKKBBK..............",
            ".....KBBK..KBBK.............",
            ".....KMMK..KMMK.............",
            ".....KMMK..KMMK.............",
            "....KMMMK..KMMMK............",
            "....KKKKK..KKKKK............",
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

        private val hurtFrame = listOf(
            "............................",
            "............................",
            "......KKKKKK................",
            "....KKGGGGGGKK..............",
            "...KGYYCCYYCCGK.............",
            "..KNNKKGGGGKKNNK............",
            ".KNNKKSSSSSSKNNK............",
            ".KNKKSSWSSWSSKK.............",
            "..KKKSSSSSSSSSSSK...........",
            "...KKSSAAAAAKKKK............",
            ".....KSSBBSSK...............",
            "....KWWKBBKWWK..............",
            "....KSSKBTKSSK..............",
            ".....KYYYYYYK...............",
            ".....KBBBBBBK...............",
            ".....KBBKKBBK...............",
            "......KBBK.KBBK.............",
            ".......KMMK.KMMK............",
            ".......KBBK..KBBK...........",
            ".......KMMK..KMMK...........",
            "......KMMMK..KMMMK..........",
            "......KKKKK..KKKKK..........",
            "............................",
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

        // === SLINGSHOT PULLBACK AIM POSE (Kabuto stretched back!) ===
        private val aimPullFrame = listOf(
            "............................",
            "............................",
            "........KKKKKK..............",
            "......KKGGGGGGKK............",
            ".....KGYYCCYYCCGK...........",
            "....KNNKKGGGGKKNNK..........",
            "...KNNKKSSSSSSKNNK..........",
            "...KNKKSSKSSKSSKK.......KK..",
            "....KKKSSSSSSSSSSSK....KGGK.",
            ".....KKSSAAAAAKKKK....KGDGK.",
            ".......KKSSSSKK..YYYYYYKDK..",
            "......KSSBBSSKK.YWWY..KGGK..",
            ".....KWWKBBKSSKKYYYYKKGGGK..",
            ".....KSSKBTKSSSSSSSSGGGGK...",
            "......KKBBTBKKKKKK..KGDGK...",
            ".......KYYYYYYK......KGGK...",
            ".......KBBBBBBK.......KK....",
            ".......KBBMMBBK.............",
            "......KBBK..KBBK............",
            ".....KBBK....KBBK...........",
            ".....KMMK....KMMK...........",
            "....KBBK......KBBK..........",
            "....KMMK......KMMK..........",
            "...KMMMK......KMMMK.........",
            "...KKKKK......KKKKK.........",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................"
        )

        // === SLINGSHOT RELEASE / FIRE POSE ===
        private val fireReleaseFrame = listOf(
            "............................",
            "............................",
            ".........KKKKKK.............",
            ".......KKGGGGGGKK...........",
            "......KGYYCCYYCCGK..........",
            ".....KNNKKGGGGKKNNK.........",
            "....KNNKKSSSSSSKNNK.........",
            "....KNKKSSKSSKSSKK......KK..",
            ".....KKKSSSSSSSSSSSK...KGGK.",
            "......KKSSAAAAAKKKK...KGDGK.",
            "........KKSSSSKK......KDKW..",
            ".......KSSBBSSKK......KGGWW.",
            "......KWWKBBKSSKKKKKKKGGGWK.",
            "......KSSKBTKSSSSSSSSGGGGK..",
            ".......KKBBTBKKKKKK..KGDGK..",
            "........KYYYYYYK......KGGK..",
            "........KBBBBBBK.......KK...",
            "........KBBMMBBK............",
            ".......KBBK..KBBK...........",
            "......KBBK....KBBK..........",
            "......KMMK....KMMK..........",
            ".....KBBK......KBBK.........",
            ".....KMMK......KMMK.........",
            "....KMMMK......KMMMK........",
            "....KKKKK......KKKKK........",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................"
        )

        // === FIREBIRD / EXPLODING STAR SPECIAL CHARGE POSE ===
        private val specialChargeFrame = listOf(
            "............................",
            "............................",
            "........KKKKKK..............",
            "......KKGGGGGGKK............",
            ".....KGYYCCYYCCGK...........",
            "....KNNKKGGGGKKNNK..........",
            "...KNNKKSSSSSSKNNK..........",
            "...KNKKSSKSSKSSKK......KK...",
            "....KKKSSSSSSSSSSSK...KGGK..",
            ".....KKSSAAAAAKKKK...KGDGK..",
            ".......KKSSSSKK..ORRRRKDK...",
            "......KSSBBSSKK.OYWWYOKGGK..",
            ".....KWWKBBKSSKKORRRRKGGGK..",
            ".....KSSKBTKSSSSSSSSGGGGK...",
            "......KKBBTBKKKKKK..KGDGK...",
            ".......KYYYYYYK......KGGK...",
            ".......KBBBBBBK.......KK....",
            ".......KBBMMBBK.............",
            "......KBBK..KBBK............",
            ".....KBBK....KBBK...........",
            ".....KMMK....KMMK...........",
            "....KBBK......KBBK..........",
            "....KMMK......KMMK..........",
            "...KMMMK......KMMMK.........",
            "...KKKKK......KKKKK.........",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................",
            "............................"
        )

        // === FAST METAL SLINGSHOT PELLET (8x6) ===
        private val metalPelletRows = listOf(
            "..KKKK..",
            ".KLLWWK.",
            "KEELWWWK",
            "KEEELLWK",
            ".KEEEEK.",
            "..KKKK.."
        )

        // === EXPLODING STAR PROJECTILE (12x12) ===
        private val explodingStarRows = listOf(
            ".....RR.....",
            "....ROOR....",
            "..RROYYORR..",
            ".ROOYWWYOOR.",
            "RROYWYYWYORR",
            "OOYWWWWWWYOO",
            "OOYWWWWWWYOO",
            "RROYWYYWYORR",
            ".ROOYWWYOOR.",
            "..RROYYORR..",
            "....ROOR....",
            ".....RR....."
        )

        // === BLAZING FIREBIRD STAR PHOENIX (36x24, 2 flapping wing frames!) ===
        private val firebirdFrame0 = listOf(
            "..........RRRR......................",
            "........RROOOORR....................",
            ".......ROOYYYYOORR..................",
            "......ROYYWWWWYYOOR.................",
            ".......ROOYYYYYYOORR................",
            "........RROOYYYYYOOOR...............",
            "..RRRR....RROOYYYYYOORR....RRRR.....",
            ".ROOOORR....RROOYYYYYOORR.ROOOORR...",
            "ROYYYYOORR...ROOOYYYYYYOOROYYYYOOR..",
            ".RROYYYYOORRRROOYYYYYYYYOYYWWWWYYOR.",
            "..RROOYYYYYOOOOYYYYWWWWWWWWWWWWWWYYR",
            "....RROOYYYYYYYYYYWWWWWWWWWWWWWWWWWR",
            ".....RROOYYYYYYYYYWWWWWWWWWWWWWWYYOR",
            "..RRROOOYYYYOOOOYYYYYYYYYYYYYYYOOOR.",
            ".ROOYYYYOORRRROOYYYYYYYYOOYYYYOORR..",
            "..RRRRRR....RROOYYYYYOORR.RRRRRR....",
            "..........RROOYYYYYOORR.............",
            "........RROOYYYYYYOORR..............",
            ".......ROOYYWWWWYYOOR...............",
            "......ROOYYYYOORRRR.................",
            ".......ROOOORR......................",
            "........RRRR........................",
            "....................................",
            "...................................."
        )

        private val firebirdFrame1 = listOf(
            "....................................",
            "....................................",
            "....................................",
            "............RRRR....................",
            "..........RROOOORR..................",
            "........RROOYYYYOORR................",
            "..RRRR..ROOYYYYYYOOOR.....RRRR......",
            ".ROOOORR.ROOYYYYYYYOORR..ROOOORR....",
            "ROYYYYOORROOOYYYYYYYYOORROYYYYOOR...",
            ".RROYYYYYOOOOYYYYYYYYYYOYYWWWWYYOR..",
            "..RROOYYYYYYYYYYWWWWWWWWWWWWWWWWYYR.",
            "....RROOYYYYYYYYWWWWWWWWWWWWWWWWWWWR",
            ".....RROOYYYYYYYYYWWWWWWWWWWWWWWYYOR",
            "..RRROOOYYYYOOOOYYYYYYYYYYYYYYYOOOR.",
            ".ROOYYYYOORRRROOYYYYYYYYOOYYYYOORR..",
            "..RRRRRR...ROOYYYYYYOORR..RRRRRR....",
            "..........ROOYYYYYYOORR.............",
            "...........RROOYYYYOORR.............",
            "............RROOOORR................",
            "..............RRRR..................",
            "....................................",
            "....................................",
            "....................................",
            "...................................."
        )

        // === HUD PORTRAIT (18x18) ===
        private val portraitRows = listOf(
            "....KKKKKKKKKK....",
            "...KGGGGGGGGGGK...",
            "..KGYYCCYYCCYYGK..",
            ".KNNKKGGGGGGKKNNK.",
            ".KNKKSSSSSSSSKNNK.",
            ".KNKSKSSSSKSKKNNK.",
            ".KKKSSSSSSSSSSSSK.",
            "..KKSSSSSSKKKKKK..",
            "...KKSSAAASSK.....",
            "....KKSSSSSK......",
            "...KSSBBBBSSK.....",
            "..KWWKBBBBKWWK....",
            "..KSSKBBTBKSSK....",
            "...KKYYYYYYKK.....",
            "...KBBBBBBBBK..K..",
            "...KBBMMMMBBK.KGK.",
            "...KBBBBBBBBKKDK..",
            "....KKKKKKKKKKK..."
        )

        // === SKILL 1 ICON: KABUTO SLINGSHOT PELLET (16x16) ===
        private val iconShotRows = listOf(
            "................",
            "..........KKKK..",
            ".........KLLWWK.",
            "........KEELWWWK",
            "..KK....KEEELLWK",
            ".KGGK....KEEEEK.",
            ".KGDGK....KKKK..",
            "..KDK...........",
            "..KGGK..........",
            ".KGDGK..........",
            "..KDK...........",
            "..KGK...........",
            "..KDK...........",
            "..KGK...........",
            "..KK............",
            "................"
        )

        // === SKILL 2 ICON: EXPLODING STAR (16x16) ===
        private val iconExplodingStarRows = listOf(
            "................",
            ".......RR.......",
            "......ROOR......",
            "....RROYYORR....",
            "...ROOYWWYOOR...",
            "..RROYWYYWYORR..",
            ".ROOYWWWWWWYOOR.",
            ".ROOYWWWWWWYOOR.",
            "..RROYWYYWYORR..",
            "...ROOYWWYOOR...",
            "....RROYYORR....",
            "......ROOR......",
            ".......RR.......",
            "................",
            "................",
            "................"
        )

        // === SKILL 3 ICON: FIREBIRD STAR PHOENIX (16x16) ===
        private val iconFirebirdRows = listOf(
            "................",
            ".....RRR........",
            "....ROOOR.......",
            "...ROYYYOR......",
            "....ROYYOOR..RR.",
            ".RR..ROYYOORROOR",
            "ROOR.ROYYYYOYYOR",
            "RYYORROYYWWWWYYR",
            ".RYYOYYWWWWWWWWR",
            "..RROYYYYWWWWYYR",
            "....ROYYYYOYYOR.",
            "...ROOYYOORRRR..",
            "....ROOOR.......",
            ".....RRR........",
            "................",
            "................"
        )
    }
}
