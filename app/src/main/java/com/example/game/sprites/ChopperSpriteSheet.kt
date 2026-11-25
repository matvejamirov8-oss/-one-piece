package com.example.game.sprites

import androidx.compose.ui.graphics.ImageBitmap

/**
 * Hand-crafted 16-bit GBA/SNES pixel-art sprites for Tony Tony Chopper (Straw Hat Doctor).
 *
 * Visual Upgrade:
 * - 3-tone volumetric shading & selective colored outlines (deep burgundy for pink hat,
 *   warm espresso for fur/antlers, dark crimson for shorts).
 * - Compact, adorable Brain Point silhouette (noticeably shorter than crewmates) with
 *   sculpted pink top hat + crisp white X cross, branching antlers, cream muzzle,
 *   lively eyes, bright blue nose, red shorts, and dark split hooves anchored on row 30.
 * - Dramatic transformations: stocky martial-arts Kung Fu Point with crisp pixel speed lines,
 *   organic multi-layered fluffy Guard Point wool sphere (36x34, row 32), and towering shaggy
 *   Monster Point beast (44x56, row 54) with giant branching antlers and colossal slam paw.
 */
class ChopperSpriteSheet {

    private val palette: Map<Char, Int> = mapOf(
        '.' to 0x00000000,
        // Pink top hat (selective burgundy outline + 3 pink tones + crisp white X cross)
        'd' to 0xFF621C40.toInt(),
        'D' to 0xFFC73D7C.toInt(),
        'P' to 0xFFFF78B4.toInt(),
        'H' to 0xFFFFBBD9.toInt(),
        'W' to 0xFFFFFFFF.toInt(),
        'U' to 0xFFD4DFF0.toInt(),
        // Fur, muzzle & antlers (selective espresso outline + 3 fur tones + muzzle + antlers)
        'a' to 0xFF3E2110.toInt(),
        'M' to 0xFF8A512A.toInt(),
        'B' to 0xFFC8864E.toInt(),
        'T' to 0xFFE8B47D.toInt(),
        'L' to 0xFFF7DAB5.toInt(),
        'V' to 0xFFD9B284.toInt(),
        'A' to 0xFF703E20.toInt(),
        'J' to 0xFFA06338.toInt(),
        // Iconic blue nose (3 tones)
        'I' to 0xFF1554B8.toInt(),
        'N' to 0xFF288CFF.toInt(),
        'C' to 0xFF94E6FF.toInt(),
        // Red shorts & strap (selective dark crimson outline + 3 red tones)
        'r' to 0xFF5E121B.toInt(),
        'c' to 0xFFA61B24.toInt(),
        'R' to 0xFFE83135.toInt(),
        'q' to 0xFFFF6D6D.toInt(),
        // Deep ink (eyes, dark hooves) + hoof specular
        'K' to 0xFF1A1629.toInt(),
        'h' to 0xFF474059.toInt(),
        // Rumble Ball gold/amber & Monster Point shaggy dark fur + glowing eyes
        'Y' to 0xFFFFE14D.toInt(),
        'O' to 0xFFFF8A1F.toInt(),
        'f' to 0xFF24120A.toInt(),
        'S' to 0xFF472716.toInt(),
        'F' to 0xFF754325.toInt(),
        'Z' to 0xFF60F0FF.toInt(),
        // Medical cross emerald greens (for skill icons)
        'G' to 0xFF39D96C.toInt(),
        'E' to 0xFF198740.toInt()
    )

    val idleFrames: List<DirectionalSprite>
    val runFrames: List<DirectionalSprite>
    val jumpRise: DirectionalSprite
    val jumpFall: DirectionalSprite
    val landSquash: DirectionalSprite
    val hurtPose: DirectionalSprite

    // Combat Forms
    val kungFuJab: DirectionalSprite
    val kungFuPalm: DirectionalSprite
    val guardPointFrames: List<DirectionalSprite>
    val rumbleEatPose: DirectionalSprite
    val monsterRoar: DirectionalSprite
    val monsterSlam: DirectionalSprite

    // HUD Portrait & Skill Icons
    val hudPortrait: ImageBitmap
    val iconKungFu: ImageBitmap
    val iconGuardHeal: ImageBitmap
    val iconMonsterPoint: ImageBitmap

    init {
        idleFrames = listOf(
            PixelArtBuilder.buildPair(alignBottom(IDLE_0, 30), palette),
            PixelArtBuilder.buildPair(alignBottom(IDLE_1, 30), palette),
            PixelArtBuilder.buildPair(alignBottom(IDLE_2, 30), palette),
            PixelArtBuilder.buildPair(alignBottom(IDLE_1, 30), palette)
        )
        runFrames = listOf(
            PixelArtBuilder.buildPair(alignBottom(RUN_0, 30), palette),
            PixelArtBuilder.buildPair(alignBottom(RUN_1, 30), palette),
            PixelArtBuilder.buildPair(alignBottom(RUN_2, 30), palette),
            PixelArtBuilder.buildPair(alignBottom(RUN_1, 30), palette)
        )
        jumpRise = PixelArtBuilder.buildPair(alignBottom(JUMP_RISE, 28), palette)
        jumpFall = PixelArtBuilder.buildPair(alignBottom(JUMP_FALL, 29), palette)
        landSquash = PixelArtBuilder.buildPair(alignBottom(LAND_SQUASH, 30), palette)
        hurtPose = PixelArtBuilder.buildPair(alignBottom(HURT_POSE, 30), palette)

        kungFuJab = PixelArtBuilder.buildPair(alignBottom(KUNG_FU_JAB, 30), palette)
        kungFuPalm = PixelArtBuilder.buildPair(alignBottom(KUNG_FU_PALM, 30), palette)
        guardPointFrames = listOf(
            PixelArtBuilder.buildPair(alignBottom(GUARD_0, 32), palette),
            PixelArtBuilder.buildPair(alignBottom(GUARD_1, 32), palette)
        )
        rumbleEatPose = PixelArtBuilder.buildPair(alignBottom(RUMBLE_EAT, 30), palette)
        monsterRoar = PixelArtBuilder.buildPair(alignBottom(MONSTER_ROAR, 54), palette)
        monsterSlam = PixelArtBuilder.buildPair(alignBottom(MONSTER_SLAM, 54), palette)

        hudPortrait = PixelArtBuilder.build(HUD_PORTRAIT, palette)
        iconKungFu = PixelArtBuilder.build(ICON_KUNG_FU, palette)
        iconGuardHeal = PixelArtBuilder.build(ICON_GUARD_HEAL, palette)
        iconMonsterPoint = PixelArtBuilder.build(ICON_MONSTER, palette)
    }

    private fun alignBottom(rows: List<String>, targetBottomRow: Int): List<String> {
        val width = rows.first().length
        val blank = ".".repeat(width)
        val lastNonEmpty = rows.indexOfLast { row -> row.any { it != '.' } }
        if (lastNonEmpty < 0 || lastNonEmpty == targetBottomRow) return rows
        val shift = targetBottomRow - lastNonEmpty
        return List(rows.size) { r ->
            val src = r - shift
            if (src in rows.indices) rows[src] else blank
        }
    }

    companion object {
        // ============================================================
        // BRAIN POINT IDLE (28x32, compact ~22px tall, hooves on row 30)
        // Sculpted pink top hat + white X cross, crisp branching antlers,
        // big sparkling eyes, cream muzzle, bright blue nose, red shorts.
        // ============================================================
        private val IDLE_0 = listOf(
            "............................", // 0
            "............................", // 1
            "............................", // 2
            "............................", // 3
            "............................", // 4
            "............................", // 5
            "............................", // 6
            "............................", // 7
            "............................", // 8
            ".........dddddddddd.........", // 9  Hat crown
            "........dHHHPPPPPPDd........", // 10 Pastel highlight + pink
            "........dHPWPPPUPPDd........", // 11 White X cross top arms
            "....aa..dHPPWWUPPPDd..aa....", // 12 Antler tips + X center
            "...aJAa.dHPWPPPWPPDd.aJAa...", // 13 Branching antlers + X bottom
            "..aJaaAa.dDDDDDDDDd.aAaaJa..", // 14 Antler fork + hat brim shadow
            "...aaAJddddPPPPPPddddJAaa...", // 15 Flared pink hat brim
            "....aaAddHHPPPPPPDDddAaa....", // 16 Hat underside + antler roots
            "......aTTBBBBBBBBMMa........", // 17 Forehead fur (3-tone)
            ".....aTTBKWBBBBKWBMa........", // 18 Big lively eyes with white glint
            ".....aTBKKKKLLKKKKMa........", // 19 Lower eyes + cream muzzle bridge
            "......aBBLLLCINCLLMa........", // 20 Bright blue nose (C/N/I) + muzzle
            "......aMBLLLVVVVLLMa........", // 21 Cute rounded cheeks + chin
            ".......aaMMBBBBBMMaa........", // 22 Neck/chin contour
            ".......aTBBaYrrYaaBMa.......", // 23 Compact torso + shorts straps
            "......aTBBarqRRqRraBMa......", // 24 Little arms + bright red shorts
            "......aKh.rqRRRRRcraKa......", // 25 Dark tiny hoof hands + shorts
            "..........rRRccRRcr.........", // 26 Shorts hem shading
            "..........rrraaarrr.........", // 27 Selective crimson hem
            "..........aBMa.aBMa.........", // 28 Short sturdy legs
            "..........KhKa.KhKa.........", // 29 Dark cloven hooves (3D edge)
            "..........K.Ka.K.Ka.........", // 30 Split hooves flush on row 30
            "............................"  // 31
        )

        // IDLE 1: Cheerful hat bounce & breathing lift
        private val IDLE_1 = listOf(
            "............................", // 0
            "............................", // 1
            "............................", // 2
            "............................", // 3
            "............................", // 4
            "............................", // 5
            "............................", // 6
            "............................", // 7
            ".........dddddddddd.........", // 8  1px cheerful bounce
            "........dHHHPPPPPPDd........", // 9
            "........dHPWPPPUPPDd........", // 10
            "....aa..dHPPWWUPPPDd..aa....", // 11
            "...aJAa.dHPWPPPWPPDd.aJAa...", // 12
            "..aJaaAa.dDDDDDDDDd.aAaaJa..", // 13
            "...aaAJddddPPPPPPddddJAaa...", // 14
            "....aaAddHHPPPPPPDDddAaa....", // 15
            "......aTTBBBBBBBBMMa........", // 16
            ".....aTTBKWBBBBKWBMa........", // 17
            ".....aTBKKKKLLKKKKMa........", // 18
            "......aBBLLLCINCLLMa........", // 19
            "......aMBLLLVVVVLLMa........", // 20
            ".......aaMMBBBBBMMaa........", // 21
            ".......aTBBaYrrYaaBMa.......", // 22
            "......aTBBarqRRqRraBMa......", // 23
            "......aKh.rqRRRRRcraKa......", // 24
            "..........rRRccRRcr.........", // 25
            "..........rrraaarrr.........", // 26
            "..........aTBa.aTBa.........", // 27
            "..........aBMa.aBMa.........", // 28
            "..........KhKa.KhKa.........", // 29
            "..........K.Ka.K.Ka.........", // 30
            "............................"  // 31
        )

        // IDLE 2: Happy playful arm wiggle & nose glint
        private val IDLE_2 = listOf(
            "............................", // 0
            "............................", // 1
            "............................", // 2
            "............................", // 3
            "............................", // 4
            "............................", // 5
            "............................", // 6
            "............................", // 7
            "............................", // 8
            ".........dddddddddd.........", // 9
            "........dHHHPPPPPPDd........", // 10
            "........dHPWPPPUPPDd........", // 11
            "....aa..dHPPWWUPPPDd..aa....", // 12
            "...aJAa.dHPWPPPWPPDd.aJAa...", // 13
            "..aJaaAa.dDDDDDDDDd.aAaaJa..", // 14
            "...aaAJddddPPPPPPddddJAaa...", // 15
            "....aaAddHHPPPPPPDDddAaa....", // 16
            "......aTTBBBBBBBBMMa........", // 17
            ".....aTTBKWBBBBKWBMa........", // 18
            ".....aTBKKKKLLKKKKMa........", // 19
            "......aBBLLLCINCLLMa........", // 20
            "......aMBLLLVVVVLLMa........", // 21
            ".......aaMMBBBBBMMaa........", // 22
            ".....aKhTBaaYrrYaaBTKh......", // 23 Cute raised hoof wiggle
            "......aaBBarqRRqRraBaa......", // 24
            "..........rqRRRRRcr.........", // 25
            "..........rRRccRRcr.........", // 26
            "..........rrraaarrr.........", // 27
            "..........aBMa.aBMa.........", // 28
            "..........KhKa.KhKa.........", // 29
            "..........K.Ka.K.Ka.........", // 30
            "............................"  // 31
        )

        // ============================================================
        // RUN CYCLE: Energetic scampering reindeer dash (3 frames)
        // ============================================================
        private val RUN_0 = listOf(
            "............................", // 0
            "............................", // 1
            "............................", // 2
            "............................", // 3
            "............................", // 4
            "............................", // 5
            "............................", // 6
            "............................", // 7
            "............................", // 8
            "..........dddddddddd........", // 9  Forward lean
            ".........dHHHPPPPPPDd.......", // 10
            ".........dHPWPPPUPPDd.......", // 11
            ".....aa..dHPPWWUPPPDd..aa...", // 12
            "....aJAa.dHPWPPPWPPDd.aJAa..", // 13
            "...aJaaAa.dDDDDDDDDd.aAaaJa.", // 14
            "....aaAJddddPPPPPPddddJAaa..", // 15
            ".....aaAddHHPPPPPPDDddAaa...", // 16
            ".......aTTBBBBBBBBMMa.......", // 17
            "......aTTBKWBBBBKWBMa.......", // 18
            "......aTBKKKKLLKKKKMa.......", // 19
            ".......aBBLLLCINCLLMa.......", // 20
            ".......aMBLLLVVVVLLMa.......", // 21
            "........aaMMBBBBBMMaa.......", // 22
            "......aKTBaaYrrYaaBTK.......", // 23 Pumping arms
            ".......aaBarqRRqRraBaa......", // 24
            "..........rqRRRRRcr.........", // 25
            "..........rRRccRRcr.........", // 26
            ".........arrraaaarr.........", // 27
            "........aBMa....aBMa........", // 28 Energetic stride
            ".......KhKa......KhKa.......", // 29
            ".......K.Ka......K.Ka.......", // 30
            "............................"  // 31
        )

        private val RUN_1 = listOf(
            "............................", // 0
            "............................", // 1
            "............................", // 2
            "............................", // 3
            "............................", // 4
            "............................", // 5
            "............................", // 6
            "............................", // 7
            "..........dddddddddd........", // 8  Scamper hop
            ".........dHHHPPPPPPDd.......", // 9
            ".........dHPWPPPUPPDd.......", // 10
            ".....aa..dHPPWWUPPPDd..aa...", // 11
            "....aJAa.dHPWPPPWPPDd.aJAa..", // 12
            "...aJaaAa.dDDDDDDDDd.aAaaJa.", // 13
            "....aaAJddddPPPPPPddddJAaa..", // 14
            ".....aaAddHHPPPPPPDDddAaa...", // 15
            ".......aTTBBBBBBBBMMa.......", // 16
            "......aTTBKWBBBBKWBMa.......", // 17
            "......aTBKKKKLLKKKKMa.......", // 18
            ".......aBBLLLCINCLLMa.......", // 19
            ".......aMBLLLVVVVLLMa.......", // 20
            "........aaMMBBBBBMMaa.......", // 21
            "........aTBaYrrYaaBMa.......", // 22
            ".......aKBarqRRqRraBKa......", // 23
            "..........rqRRRRRcr.........", // 24
            "..........rRRccRRcr.........", // 25
            "..........rrraaarrr.........", // 26
            "..........aBMaaBMa..........", // 27 Passing legs
            "..........aBMaKhKa..........", // 28
            "..........KhKa..............", // 29
            "..........K.Ka..............", // 30
            "............................"  // 31
        )

        private val RUN_2 = listOf(
            "............................", // 0
            "............................", // 1
            "............................", // 2
            "............................", // 3
            "............................", // 4
            "............................", // 5
            "............................", // 6
            "............................", // 7
            "............................", // 8
            "..........dddddddddd........", // 9
            ".........dHHHPPPPPPDd.......", // 10
            ".........dHPWPPPUPPDd.......", // 11
            ".....aa..dHPPWWUPPPDd..aa...", // 12
            "....aJAa.dHPWPPPWPPDd.aJAa..", // 13
            "...aJaaAa.dDDDDDDDDd.aAaaJa.", // 14
            "....aaAJddddPPPPPPddddJAaa..", // 15
            ".....aaAddHHPPPPPPDDddAaa...", // 16
            ".......aTTBBBBBBBBMMa.......", // 17
            "......aTTBKWBBBBKWBMa.......", // 18
            "......aTBKKKKLLKKKKMa.......", // 19
            ".......aBBLLLCINCLLMa.......", // 20
            ".......aMBLLLVVVVLLMa.......", // 21
            "........aaMMBBBBBMMaa.......", // 22
            ".......aTBaaYrrYaaBTa.......", // 23 Opposite arm swing
            "......aKBarqRRqRraBKKa......", // 24
            "..........rqRRRRRcr.........", // 25
            "..........rRRccRRcr.........", // 26
            "..........rrraaarrr.........", // 27
            ".........aBMa..aBMa.........", // 28
            "........KhKa....KhKa........", // 29
            "........K.Ka....K.Ka........", // 30
            "............................"  // 31
        )

        // ============================================================
        // JUMP RISE / FALL / LAND / HURT
        // ============================================================
        private val JUMP_RISE = listOf(
            "............................", // 0
            "............................", // 1
            "............................", // 2
            "............................", // 3
            "............................", // 4
            "............................", // 5
            "............................", // 6
            ".........dddddddddd.........", // 7
            "........dHHHPPPPPPDd........", // 8
            "........dHPWPPPUPPDd........", // 9
            "....aa..dHPPWWUPPPDd..aa....", // 10
            "...aJAa.dHPWPPPWPPDd.aJAa...", // 11
            "..aJaaAa.dDDDDDDDDd.aAaaJa..", // 12
            "...aaAJddddPPPPPPddddJAaa...", // 13
            "....aaAddHHPPPPPPDDddAaa....", // 14
            "......aTTBBBBBBBBMMa........", // 15
            ".....aTTBKWBBBBKWBMa........", // 16
            ".....aTBKKKKLLKKKKMa........", // 17
            "......aBBLLLCINCLLMa........", // 18
            ".....aKMBLLLVVVVLLBMKa......", // 19 Joyful raised hooves!
            "......aaTBaaYrrYaaBTaa......", // 20
            ".........arqRRqRra..........", // 21
            ".........rqRRRRRcr..........", // 22
            ".........rRRccRRcr..........", // 23
            ".........rrraaarrr..........", // 24
            "........aBMa...aBMa.........", // 25 Tucked hooves mid-leap
            "........KhKa...KhKa.........", // 26
            "........K.Ka................", // 27
            "............................", // 28
            "............................", // 29
            "............................", // 30
            "............................"  // 31
        )

        private val JUMP_FALL = listOf(
            "............................", // 0
            "............................", // 1
            "............................", // 2
            "............................", // 3
            "............................", // 4
            "............................", // 5
            "............................", // 6
            ".........dddddddddd.........", // 7
            "........dHHHPPPPPPDd........", // 8
            "........dHPWPPPUPPDd........", // 9
            "....aa..dHPPWWUPPPDd..aa....", // 10
            "...aJAa.dHPWPPPWPPDd.aJAa...", // 11
            "..aJaaAa.dDDDDDDDDd.aAaaJa..", // 12
            "...aaAJddddPPPPPPddddJAaa...", // 13
            "....aaAddHHPPPPPPDDddAaa....", // 14
            "......aTTBBBBBBBBMMa........", // 15
            ".....aTTBKWBBBBKWBMa........", // 16
            ".....aTBKKKKLLKKKKMa........", // 17
            "......aBBLLLCINCLLMa........", // 18
            "......aMBLLLVVVVLLMa........", // 19
            ".....aKhTBaYrrYaaBTKha......", // 20 Arms out for balance
            "......aaBarqRRqRraBaa.......", // 21
            ".........rqRRRRRcr..........", // 22
            ".........rRRccRRcr..........", // 23
            ".........rrraaarrr..........", // 24
            ".........aBMa.aBMa..........", // 25 Hooves reaching down
            ".........aBMa.aBMa..........", // 26
            ".........KhKa.KhKa..........", // 27
            ".........K.Ka.K.Ka..........", // 28
            "............................", // 29
            "............................", // 30
            "............................"  // 31
        )

        private val LAND_SQUASH = listOf(
            "............................", // 0
            "............................", // 1
            "............................", // 2
            "............................", // 3
            "............................", // 4
            "............................", // 5
            "............................", // 6
            "............................", // 7
            "............................", // 8
            "............................", // 9
            ".........dddddddddd.........", // 10 Cute landing squish
            "........dHHHPPPPPPDd........", // 11
            "........dHPWPPPUPPDd........", // 12
            "....aa..dHPPWWUPPPDd..aa....", // 13
            "...aJAa.dHPWPPPWPPDd.aJAa...", // 14
            "..aJaaAa.dDDDDDDDDd.aAaaJa..", // 15
            "...aaAJddddPPPPPPddddJAaa...", // 16
            "....aaAddHHPPPPPPDDddAaa....", // 17
            "......aTTBBBBBBBBMMa........", // 18
            ".....aTTBKWBBBBKWBMa........", // 19
            ".....aTBKKKKLLKKKKMa........", // 20
            "......aBBLLLCINCLLMa........", // 21
            "......aMBLLLVVVVLLMa........", // 22
            "......aTBaaYrrYaaBMa........", // 23
            ".....aKBarqRRqRraBKa........", // 24
            "........rqRRRRRRcr..........", // 25
            "........rRRcccRRcr..........", // 26
            "........rrrraarrrr..........", // 27
            "........aBMa..aBMa..........", // 28
            "........KhKa..KhKa..........", // 29
            "........K.Ka..K.Ka..........", // 30
            "............................"  // 31
        )

        private val HURT_POSE = listOf(
            "............................", // 0
            "............................", // 1
            "............................", // 2
            "............................", // 3
            "............................", // 4
            "............................", // 5
            "............................", // 6
            "............................", // 7
            "............................", // 8
            "........dddddddddd..........", // 9  Hat tilted back in shock
            ".......dHHHPPPPPPDd.........", // 10
            ".......dHPWPPPUPPDd.........", // 11
            "...aa..dHPPWWUPPPDd..aa.....", // 12
            "..aJAa.dHPWPPPWPPDd.aJAa....", // 13
            ".aJaaAa.dDDDDDDDDd.aAaaJa...", // 14
            "..aaAJddddPPPPPPddddJAaa....", // 15
            "...aaAddHHPPPPPPDDddAaa.....", // 16
            "......aTTBBBBBBBBMMa........", // 17
            ".....aTTBWWBBBBWWBMa........", // 18 Wide shocked anime eyes!
            ".....aTBWKWKLLKWKWMa........", // 19
            "......aBBLLLCINCLLMa........", // 20
            "......aMBLLKKKKKLLMa........", // 21 Surprised open mouth
            ".....aKaaMMBBBBBMMaaKa......", // 22 Paws thrown up
            "......aTBaYrrYaaBMaa........", // 23
            "........arqRRqRra...........", // 24
            "........rqRRRRRcr...........", // 25
            "........rRRccRRcr...........", // 26
            "........rrraaarrr...........", // 27
            "........aBMa..aBMa..........", // 28
            ".......KhKa....KhKa.........", // 29
            ".......K.Ka....K.Ka.........", // 30
            "............................"  // 31
        )

        // ============================================================
        // ATTACK 1 (Z): KUNG FU POINT — Stocky martial-arts torso,
        // fierce determined expression, rapid strikes with crisp white
        // pixel motion-blur speed lines! (28x32, hooves on row 30)
        // ============================================================
        private val KUNG_FU_JAB = listOf(
            "............................", // 0
            "............................", // 1
            "............................", // 2
            "............................", // 3
            "............................", // 4
            "............................", // 5
            "............................", // 6
            "............................", // 7
            "........dddddddddd..........", // 8  Hat angled forward
            ".......dHHHPPPPPPDd.........", // 9
            ".......dHPWPPPUPPDd.........", // 10
            "...aa..dHPPWWUPPPDd..aa.....", // 11
            "..aJAa.dHPWPPPWPPDd.aJAa....", // 12
            ".aJaaAa.dDDDDDDDDd.aAaaJa...", // 13
            "..aaAJddddPPPPPPddddJAaa....", // 14
            ".....aTTTBBBBBBBBMMMa.......", // 15 Broad neckless Kung Fu shoulders
            "....aTTBKaWBBBBKaWBBMa......", // 16 Determined furrowed martial-arts brow
            "....aTBBKKKKLLKKKKBBMa..UU..", // 17
            "....aTBBBLLLCINCLLBBMaWWWWWW", // 18 Blazing white speed line!
            "...aTTBBBBLLVVVVLLBBBBaKhKWW", // 19 Extending Kung Fu punch + blur!
            "...aTTBBBBBBBBBBBBBBBBaKKKUU", // 20 Stocky round martial-arts torso
            "...aTBBaaBBBBBBBBBBaaTBBa...", // 21 Tucked rear guard fist
            "...aMBBKhBrrYrrYrrBBBMaUU...", // 22 Red shorts sash
            "....aMBBarqRRRRRRqrBMa......", // 23
            ".....aaarqqRRRRRRccra.......", // 24
            "........rRRRccccRRcr........", // 25
            "........rrrrraaarrrr........", // 26
            ".......aBBMa....aBBMa.......", // 27 Wide grounded Kung Fu stance
            "......aBBMa......aBBMa......", // 28
            "......KhKKa......KhKKa......", // 29
            "......K..Ka......K..Ka......", // 30
            "............................"  // 31
        )

        private val KUNG_FU_PALM = listOf(
            "............................", // 0
            "............................", // 1
            "............................", // 2
            "............................", // 3
            "............................", // 4
            "............................", // 5
            "............................", // 6
            "............................", // 7
            ".........dddddddddd.........", // 8
            "........dHHHPPPPPPDd........", // 9
            "........dHPWPPPUPPDd........", // 10
            "....aa..dHPPWWUPPPDd..aa....", // 11
            "...aJAa.dHPWPPPWPPDd.aJAa...", // 12
            "..aJaaAa.dDDDDDDDDd.aAaaJa..", // 13
            "...aaAJddddPPPPPPddddJAaa...", // 14
            "....aTTTBBBBBBBBMMMa....WW..", // 15 Upper speed streak
            "...aTTBKaWBBBBKaWBBMa.UWWWW.", // 16 Double-speed palm burst!
            "...aTBBKKKKLLKKKKBBMaKhKWWWW", // 17 High Kung Fu palm strike
            "...aTBBBLLLCINCLLBBBaKKKUUU.", // 18
            "..aTTBBBBLLVVVVLLBBBBaa.....", // 19 Stocky agile body twist
            "..aTTBBBBBBBBBBBBBBBBaWWWW..", // 20 Low follow-up speed streak
            "..aTBBaBBBBBBBBBBBBBaKhKWWU.", // 21 Rapid low flurry hoof
            "...aMBBarqRRRRRRRRqraKKa....", // 22
            "....aMMrqqRRRRRRRRccr.......", // 23
            ".....aarRRRccccccRRcr.......", // 24
            ".......rRRRRccccRRRcr.......", // 25
            ".......rrrrrraaarrrrr.......", // 26
            "......aBBMa......aBBMa......", // 27 Deep Kung Fu lunge
            ".....aBBMa........aBBMa.....", // 28
            ".....KhKKa........KhKKa.....", // 29
            ".....K..Ka........K..Ka.....", // 30
            "............................"  // 31
        )

        // ============================================================
        // ATTACK 2 (X): GUARD POINT (36x34, hooves on row 32)
        // Lush, organic, multi-layered fluffy fur ball with ragged wool
        // tufts & directional 3-tone wool shading (NOT a flat circle!),
        // pink hat + antlers peeking out top, tiny hooves peeking out bottom!
        // ============================================================
        private val GUARD_0 = listOf(
            "....................................", // 0
            ".............dddddddddd.............", // 1  Pink hat peeking out top!
            "............dHHHPPPPPPDd............", // 2
            "............dHPWPPPUPPDd............", // 3
            "........aa..dHPPWWUPPPDd..aa........", // 4  Antlers peeking from fluff
            ".......aJAa.dHPWPPPWPPDd.aJAa.......", // 5
            "......aJaaAa.dDDDDDDDDd.aAaaJa......", // 6
            ".......aaAJddddPPPPPPddddJAaa.......", // 7
            ".........aaaTTBBBBBBBBMMaaa.........", // 8  Upper fluffy wool ridge
            "......aaaTTTLLTTBBBBBBBBMMaaa.......", // 9  Organic tufted outer silhouette
            "....aaTTTLLLTTTBBTTBBBBBBMMMMaa.....", // 10 Layered top-left sunlit locks
            "...aTTLLLLTTTBBBBTTBBBBMMBBMMMa.....", // 11
            "..aTTTLLTTTBBBBBBBBBBBBMMBBBBMMa....", // 12 Ragged wool billow
            ".aTTLLTTBBBBTTBBBBBBBBMMBBBBMMMa....", // 13
            "aTTTLLTBBBBTTLLBBBBBBMMBBBBMMMMa....", // 14 Fluffy puff contour
            ".aTTLTTBBBTTLLBBaCNaBMBBBBMMMMMa....", // 15 Cute blue nose peeking in fluff!
            "aTTTLLTTBBTTLBBBBaIaBBBBMMMMMMMa....", // 16 Deep wool shadows
            "aTTLLTTBBBBTBBBBBBBBBBMBBMMMMMMa....", // 17
            ".aTTTBBBBBBBBBBTTBBBBMMBMMMMMMMa....", // 18 Scalloped tuft layers
            "aTTBBBBTTBBBBBBTBBBBMMBMMMMMMMMa....", // 19
            "aTBBBBTTBBBBBBBBBBBMMBBMMMMMMMa.....", // 20
            ".aTBBBBBBBBBBBBBBBMMBMMMMMMMMMa.....", // 21
            "aTTBBBBBBBBMMBBBBMMBBMMMMMMMMMa.....", // 22 Organic ragged lower tufts
            ".aTBBBBBBBMMBBBBMMBBMMMMMMMMMMa.....", // 23
            "..aMBBBBBMMBBBBMMBBMMMMMMMMMMa......", // 24
            "..aMMBBBMMBBBBMMBBMMMMMMMMMMa.......", // 25
            "...aMMBMMBBBBMMBBMMMMMMMMMMa........", // 26 Deep underside wool shade
            "....aMMMMBBBMMBBMMMMMMMMMMa.........", // 27
            ".....aaMMMMMMBBMMMMMMMMMaa..........", // 28
            ".......aaaMMMMMMMMMMMaaa............", // 29
            "..........aaaaaaaaaaa...............", // 30
            "............KhKaKhKa................", // 31 Tiny dark hooves peeking out!
            "............K.KaK.Ka................", // 32 Flush on ground row 32
            "...................................."  // 33
        )

        private val GUARD_1 = listOf(
            "....................................", // 0
            ".............dddddddddd.............", // 1  Hat gently bobbing in fluff
            "............dHHHPPPPPPDd............", // 2
            "............dHPWPPPUPPDd............", // 3
            "........aa..dHPPWWUPPPDd..aa........", // 4
            ".......aJAa.dHPWPPPWPPDd.aJAa.......", // 5
            "......aJaaAa.dDDDDDDDDd.aAaaJa......", // 6
            ".......aaAJddddPPPPPPddddJAaa.......", // 7
            "........aaaaTTBBBBBBBBMMaaaa........", // 8  Puffed out wool frame 2
            ".....aaaTTTTLLTTBBBBBBBBMMaaa.......", // 9
            "...aaTTTLLLLTTTBBTTBBBBBBMMMMaa.....", // 10
            "..aTTTLLLLTTTBBBBTTBBBBMMBBMMMa.....", // 11
            ".aTTTLLTTTBBBBBBBBBBBBMMBBBBMMMa....", // 12
            "aTTTLLTTBBBBTTBBBBBBBBMMBBBBMMMa....", // 13 Expanded fluffy tufts
            "aTTLLTTBBBBTTLLBBBBBBMMBBBBMMMMMa...", // 14
            "aTTTLLTBBBTTLLBBaCNaBMBBBBMMMMMMa...", // 15
            ".aTTLLTTBBTTLBBBBaIaBBBBMMMMMMMa....", // 16
            "aTTTLLTBBBBTBBBBBBBBBBMBBMMMMMMMa...", // 17
            "aTTBBBBBBBBBBBBTTBBBBMMBMMMMMMMMa...", // 18
            ".aTBBBBTTBBBBBBTBBBBMMBMMMMMMMMa....", // 19
            "aTTBBBTTBBBBBBBBBBBMMBBMMMMMMMa.....", // 20
            "aTBBBBBBBBBBBBBBBBMMBMMMMMMMMMa.....", // 21
            ".aTBBBBBBBMMBBBBMMBBMMMMMMMMMa......", // 22
            "..aMBBBBBMMBBBBMMBBMMMMMMMMMMa......", // 23
            "..aMMBBBMMBBBBMMBBMMMMMMMMMMa.......", // 24
            "...aMMBMMBBBBMMBBMMMMMMMMMMa........", // 25
            "....aMMMMBBBMMBBMMMMMMMMMMa.........", // 26
            ".....aaMMMMMMBBMMMMMMMMMaa..........", // 27
            ".......aaMMMMMMMMMMMMMaa............", // 28
            ".........aaaMMMMMMMaaa..............", // 29
            "...........aaaaaaaaa................", // 30
            "............KhKaKhKa................", // 31
            "............K.KaK.Ka................", // 32
            "...................................."  // 33
        )

        // ============================================================
        // RUMBLE BALL EAT WINDUP (28x32, hooves on row 30)
        // Chopper tilting head back with open mouth ready to swallow Rumble Ball!
        // ============================================================
        private val RUMBLE_EAT = listOf(
            "............................", // 0
            "............................", // 1
            "............................", // 2
            "............................", // 3
            "............................", // 4
            "............................", // 5
            "............................", // 6
            "............................", // 7
            "........dddddddddd..........", // 8  Hat tipped back
            ".......dHHHPPPPPPDd.........", // 9
            ".......dHPWPPPUPPDd.........", // 10
            "...aa..dHPPWWUPPPDd..aa.....", // 11
            "..aJAa.dHPWPPPWPPDd.aJAa....", // 12
            ".aJaaAa.dDDDDDDDDd.aAaaJa...", // 13
            "..aaAJddddPPPPPPddddJAaa....", // 14
            "...aaAddHHPPPPPPDDddAaa.....", // 15
            "......aTTBBBBBBBBMMa...YO...", // 16 Glowing golden Rumble Ball aura
            ".....aTTBKWBBBBKWBMa..YWWY..", // 17
            ".....aTBKKKKLLKKKKMa...YO...", // 18
            "......aBBLLLCINCLLMa........", // 19
            "......aMBLLKKKKKLLMa........", // 20 Wide open mouth catching pill!
            ".......aaMMBKKKBBMaaKh......", // 21 Tossing hoof raised
            ".......aTBaYrrYaaBTBaa......", // 22
            "......aKBarqRRqRraBaa.......", // 23
            "..........rqRRRRRcr.........", // 24
            "..........rRRccRRcr.........", // 25
            "..........rrraaarrr.........", // 26
            "..........aBMa.aBMa.........", // 27
            "..........aBMa.aBMa.........", // 28
            "..........KhKa.KhKa.........", // 29
            "..........K.Ka.K.Ka.........", // 30
            "............................"  // 31
        )

        // ============================================================
        // ATTACK 3 (C): MONSTER POINT ROAR (44x56, hooves on row 54)
        // Towering menacing horned reindeer titan! Giant branching antlers,
        // shredded pink hat remnants, glowing cyan eyes, roaring fanged snout,
        // shaggy multi-layered dark-brown mane, tattered red shorts, colossal hooves!
        // ============================================================
        private val MONSTER_ROAR = listOf(
            "............................................", // 0
            "....aa................................aa....", // 1  Colossal antler crown tips
            "...aJAa.......aa............aa.......aJAa...", // 2
            "...aJAa......aJAa..........aJAa......aJAa...", // 3
            "....aJAa.....aJAa..........aJAa.....aJAa....", // 4  Inner antler forks
            "..aa.aJAa...aJAAa..........aAAJa...aJAa.aa..", // 5
            ".aJAaaJAAa..aAAa...dddddd...aAAa..aAAJaaJAa.", // 6  Shredded pink hat remnants!
            "..aJAAAJAAaaAAa...dHPPPWDd...aAAaaAAJAAAJa..", // 7  Torn white X cross
            "...aaAAJAAAAAa....dPWPPPDd....aAAAAAJAAaa...", // 8
            ".....aaAAAAAAa...ddDDDDDdd...aAAAAAAaa......", // 9  Tattered brim
            ".......aaaAAAa.ffSSSSSSSSSff.aAAAaaa........", // 10 Heavy horned brow
            "..........aaaafSSFBBBBBBFSSFaaaa............", // 11
            ".............fSSFFBBBBBBFFSSf...............", // 12
            "............fSSFBKZZBBZZKBFSSf..............", // 13 Blazing glowing beast eyes!
            "............fSFBBKWWBBWWKBBFSf..............", // 14
            "............fSFBBBBICNIBBBBFSf..............", // 15 Feral blue nose
            "...........fSSFBBLWWWWWWLBBFSSf.............", // 16 Roaring fanged maw!
            "..........fSSFFBBKWKKKKWKBBFFSSf............", // 17
            ".........fSSFFFBBLWWWWWWLBBFFFSSf...........", // 18 Lower fangs
            ".......ffSSFFFFFBBBBBBBBBBFFFFFSSff.........", // 19 Massive shaggy mane begins
            ".....ffSSFFMMFFFFBBBBBBBBFFFFMMFFSSff.......", // 20 Layered dark-brown shaggy fur
            "...ffSSFFMMBBMMFFFBBBBBBFFFMMBBMMFFSSff.....", // 21 Broad towering titan shoulders
            "..fSSFFMMBBBBBBMFFBBBBBBFFMBBBBBBMMFFSSf....", // 22
            ".fSSFFMMBBTBBBBBMFBBBBBBFMBBBBBTBBMMFFSSf...", // 23 Shaggy fur tufts & specular ridges
            ".fSFFMMBBTTBBBBMMFBBBBBBFMMBBBBTTBBMMFFSf...", // 24
            "fSSFFMBBTTBBBBMMFFBBBBBBFFMMBBBBTTBBMFFSSf..", // 25 Huge muscular arms flung wide
            "fSFFMBBBTBBBBMMFFMBBBBBBMFFMMBBBBTBBBMFFSf..", // 26
            "fSFMMBBBBBBBMMfSFMBBBBBBMFSfMMBBBBBBBMMFSf..", // 27 Shaggy forearm fur
            "fSFMBBBBBBBMMf.fSMBBBBBBMSf.fMMBBBBBBBMFSf..", // 28
            "fSFMBBBBBBMMf..fSMBBBBBBMSf..fMMBBBBBBMFSf..", // 29
            "fSFFMBBBBBMf...fSMBBBBBBMSf...fMBBBBBMFFSf..", // 30
            ".fSSMMBBBBMf...fSMBBBBBBMSf...fMBBBBMMSSf...", // 31
            "..fKKhKKKff....fSMBBBBBBMSf....ffKKKhKKf....", // 32 Massive dark hoofed claws
            "..fKKhKKKf.....fSMMBBBBMMSf.....fKKKhKKf....", // 33
            "...fKK.KKf....frqRRRRRRRRqrf....fKK.KKf.....", // 34 Stretched tattered red shorts!
            "....ff.ff.....frRRRRccRRRRrf.....ff.ff......", // 35
            "..............frRRRccccRRRrf................", // 36
            "..............frRRccccccRRrf................", // 37
            "..............frrRrrrrrrRrrf................", // 38 Tattered shorts hem
            ".............fSSFMf....fMFSSf...............", // 39 Heavy shaggy beast thighs
            "............fSSFFMf....fMFFSSf..............", // 40
            "............fSFBBMf....fMBBFSf..............", // 41
            "...........fSSFBBMf....fMBBFSSf.............", // 42
            "...........fSFBBMMf....fMMBBFSf.............", // 43
            "...........fSFBMMf......fMMBFSf.............", // 44
            "...........fSFBMf........fMBFSf.............", // 45
            "..........fSSFBMf........fMBFSSf............", // 46
            "..........fSFBBMf........fMBBFSf............", // 47
            "..........fSFMMf..........fMMFSf............", // 48
            "..........fSSMMf..........fMMSSf............", // 49
            ".........fKKhhKKf........fKKhhKKf...........", // 50 Colossal beast hooves
            ".........fKKhhKKf........fKKhhKKf...........", // 51
            "........fKKKhhKKKf......fKKKhhKKKf..........", // 52
            "........fKKK..KKKf......fKKK..KKKf..........", // 53
            "........fff....fff......fff....fff..........", // 54 Hooves flush on ground row 54
            "............................................"  // 55
        )

        // ============================================================
        // ATTACK 3 (C): MONSTER POINT SLAM (44x56, hooves on row 54)
        // Leaning forward and driving a colossal clawed/hoofed paw
        // crashing straight into the ground with golden impact streaks!
        // ============================================================
        private val MONSTER_SLAM = listOf(
            "............................................", // 0
            "............................................", // 1
            "............................................", // 2
            "......aa..............................aa....", // 3  Antlers pitched forward
            ".....aJAa.......aa..........aa.......aJAa...", // 4
            ".....aJAa......aJAa........aJAa......aJAa...", // 5
            "......aJAa....aJAAa........aAAJa....aJAa....", // 6
            "....aa.aJAAaaaAAa...dddddd...aAAaaaAAJa.aa..", // 7  Shredded pink hat
            "...aJAaaJAAAAAa....dHPPPWDd....aAAAAAJaaJAa.", // 8
            "....aaAAJAAAAa.....dPWPPPDd.....aAAAAJAAaa..", // 9
            "......aaAAAAAaf..ffddDDDDddff..faAAAAAaa....", // 10
            "........aaaaafSSSFFBBBBBBFFSSSfafaaaa.......", // 11
            "............fSSFFBBBBBBBBBBFFSSf............", // 12
            "...........fSSFBKZZBBZZKBBBBBFSf............", // 13 Blazing fierce beast gaze
            "...........fSFBBKWWBBWWKBBBBBBFSf...........", // 14
            "..........fSSFBBLICNIBBBBBBBBBFSSf..........", // 15
            "..........fSFBBBLWWWWWWLBBBBBBBFSf..........", // 16 Snarling fanged snout
            ".........fSSFFBBBBBBBBBBBBBBBBBFFSSf........", // 17
            ".......ffSSFFFFMMBBBBBBBBBBMMFFFFFSSff......", // 18 Massive hunched shaggy mane
            ".....ffSSFFMMBBBBBBBBBBBBBBBBBBMMFFSSff.....", // 19
            "....fSSFFMMBBTTBBBBBBBBBBBBBBTTBBMMFFSSf....", // 20
            "...fSSFFMBBTTBBBBBBBBBBBBBBBBBBTTBBMFFSSf...", // 21 Huge slamming right shoulder
            "..fSSFFMBBTBBBBBBBBBBBBBBBBBBBBBBTBBMFFSSf..", // 22
            "..fSFFMBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBMFFSf..", // 23
            ".fSSFFMBBBBBBBBBMMBBBBBBBBMMBBBBBBBBBBMFSf..", // 24
            ".fSFFMBBBBBBBMMfSFMBBBBBBMFSfMMBBBBBBBMFSf..", // 25
            ".fSFMBBBBBBMMf..fSMBBBBBBMSf..fMMBBBBBMFSf..", // 26
            "..fSFMBBBBMMf...fSMBBBBBBMSf...fMMBBBBMMFSf.", // 27 Giant right arm plunging down!
            "..fSSMMBBMMf....fSMBBBBBBMSf....fMMBBBBMFSf.", // 28
            "...fKKhKKKf.....fSMMBBBBMMSf.....fMMBBBBMFSf", // 29
            "...fKK.KKf.....frqRRRRRRRRqrf.....fMMBBBMFSf", // 30
            "....ff.ff......frRRRRccRRRRrf......fMBBBMFSf", // 31
            "...............frRRRccccRRRrf......fMBBBMFSf", // 32
            "...............frRRccccccRRrf......fMBBBMFSf", // 33
            "...............frrRrrrrrrRrrf......fMBBBMFSf", // 34
            "..............fSSFMf....fMFSSf....fSSMBBMFSf", // 35
            ".............fSSFFMf....fMFFSSf...fSSMBBMFSf", // 36
            "............fSFBBMf......fMBBFSf..fSSMBBMFSf", // 37
            "...........fSSFBBMf......fMBBFSSf.fSMBBBMMFf", // 38
            "...........fSFBBMMf......fMMBBFSf.fSMBBBMMFf", // 39
            "..........fSFBMMf.........fMMBFSf.fSMBBBBMFf", // 40
            "..........fSFBMf...........fMBFSf.fSMBBBBMFf", // 41
            ".........fSSFBMf...........fMBFSSf.fSMBBMMFf", // 42
            ".........fSFBBMf...........fMBBFSf.fSMBBMMFf", // 43
            ".........fSFMMf.............fMMFSf.fKKhhKKKf", // 44 Colossal slamming paw!
            "........fSSMMf..............fMMSSf.fKKhhKKKf", // 45
            "........fSFMMf..............fMMFSfYKKKhhKKKY", // 46 Golden impact shock sparks!
            ".......fKKhhKKf............fKKhhKKWKKKhhKKKW", // 47
            ".......fKKhhKKf............fKKhhKKWKKKhhKKKW", // 48
            "......fKKKhhKKKf..........fKKKhhKKOKKKhhKKKO", // 49
            "......fKKKhhKKKf..........fKKKhhKKYKKKKKKKKY", // 50
            "......fKKK..KKKf..........fKKK..KKWKKKKKKKKW", // 51 Colossal paw slamming floor!
            "......fKKK..KKKf..........fKKK..KKYWWKKKKWWY", // 52
            "......fKKK..KKKf..........fKKK..KKOYYWWWWYYO", // 53 Shockwave blast!
            "......fff....fff..........fff....ffYYYYYYYY.", // 54 Ground impact flush row 54
            "............................................"  // 55
        )

        // ============================================================
        // HUD PORTRAIT (18x18) — Crisp, super-adorable 16-bit Chopper!
        // Sculpted pink top hat with clear white X cross, branching antlers,
        // big sparkling eyes, cream muzzle, bright blue nose, red collar!
        // ============================================================
        private val HUD_PORTRAIT = listOf(
            "....dddddddddd....", // 0  Hat crown
            "...dHHHPPPPPPDd...", // 1  Pastel highlight
            "...dHPWPPPUPPDd...", // 2  Crisp white X cross top
            "a..dHPPWWUPPPDd..a", // 3  Antler tips + X center
            "Ja.dHPWPPPWPPDd.aJ", // 4  Branching antlers + X bottom
            "aAa.dDDDDDDDDd.aAa", // 5  Hat shadow under band
            ".AJddddPPPPPPddddJ", // 6  Sculpted pink hat brim
            "..AddHHPPPPPPDDddA", // 7
            "...aTTTBBBBBBMMa..", // 8  3-tone fur forehead
            "..aTTBKWBBBBKWBMa.", // 9  Big sparkling eyes
            "..aTBKKKKLLKKKKMa.", // 10 Cream muzzle bridge
            "..aTBBLCCNNCCBBMa.", // 11 Bright iconic blue nose!
            "..aMBBLCINIICBBMa.", // 12 Blue nose shadow
            "...aMBLLVVVVLLMa..", // 13 Cute rounded muzzle cheeks
            "....aaMMBBBBMMaa..", // 14 Chin
            "....aTBaYrrYaBMa..", // 15 Red strap & collar
            "...aTBBarqqRraBMa.", // 16 Shoulders + red shorts
            "...aaMMrrrrrrMMaa."  // 17
        )

        // ============================================================
        // HUD SKILL ICONS (16x16) — Crisp 16-bit icons
        // ============================================================
        private val ICON_KUNG_FU = listOf(
            "................",
            "......aaaa......",
            "....aaTTBBaa....",
            "...aTTBBBBMMa...",
            "..aTBBBBKhKKaW..",
            "..aTBBKKhKKKWWU.",
            ".WWaMBKKKKKKWWW.",
            "UWWWaMMBBBBMUU..",
            "..WWaTBBBBMMa...",
            "....aMBKhKKaWW..",
            ".....aaKKKWWU...",
            ".......aaaWW....",
            "................",
            "................",
            "................",
            "................"
        )

        private val ICON_GUARD_HEAL = listOf(
            "................",
            ".....aaaaaa.....",
            "...aaTTBBMMaa...",
            "..aTTBBWWBBMMa..",
            ".aTTBBWGGWBBMMa.",
            ".aTBBBWGGWBBBMa.",
            "aTBBWWGGGGWWBBMa",
            "aTBBWGGEEGGWBBMa",
            "aTBBWWGGGGWWBBMa",
            ".aTBBBWGGWBBBMa.",
            ".aMBBBWGGWBBMMa.",
            "..aMMBBWWBBMMa..",
            "...aaMMMMMMaa...",
            ".....aaaaaa.....",
            "................",
            "................"
        )

        private val ICON_MONSTER = listOf(
            "................",
            ".aa..........aa.",
            "aJAa..a..a..aJAa",
            ".aJAaaJaaJaaJAa.",
            "..aaAAffffAAaa..",
            "...fSSBBBBSSf...",
            "..fSFBZBBZBFSf..",
            "..fSFBWBBWBFSf..",
            "..fSFBBNCBBFSf..",
            "..fSFBLWWLBFSf..",
            "...fSSKWWKSSf...",
            "...ffSSFFSSff...",
            "..fSSFFFFFFSSf..",
            "...ffSSSSSSff...",
            "................",
            "................"
        )
    }
}
