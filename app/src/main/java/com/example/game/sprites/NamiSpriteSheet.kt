package com.example.game.sprites

import androidx.compose.ui.graphics.ImageBitmap

/**
 * Clean, minimalist 16-bit Pixel Art Sprite Sheet for Nami (Navigator — Clima-Tact),
 * crafted in the exact same artistic style, proportions, dark-ink outline ('K'),
 * and 2-tone palette discipline as Luffy, Zoro, and Sanji.
 *
 * - All character sprites: 28x32, grounded strictly on foot baseline row 30.
 * - Clean bob-cut orange hair with bangs (2 tones: bright orange + back shadow),
 *   uncluttered bright face with crisp eyes, crisp white-and-blue striped top,
 *   dark navy denim skirt (high contrast against orange hair), dark sandals,
 *   and a clean straight blue Clima-Tact held steadily without idle/run visual clutter.
 */
class NamiSpriteSheet {

    private val palette: Map<Char, Int> = mapOf(
        '.' to 0x00000000,         // Transparent
        'K' to 0xFF181425.toInt(), // Crisp pixel-art ink outline (matches Luffy & Sanji)
        'O' to 0xFFFF8826.toInt(), // Clean vibrant orange hair
        'R' to 0xFFD45614.toInt(), // Hair back shadow (single shade)
        'S' to 0xFFF5C6A0.toInt(), // Clean bright skin
        'A' to 0xFFD4976C.toInt(), // Subtle chin/neck shadow
        'W' to 0xFFFFFFFF.toInt(), // Crisp white (shirt stripes & eye glint)
        'B' to 0xFF3284EE.toInt(), // Clean blue (shirt stripes & Clima-Tact)
        'C' to 0xFF7BE8FF.toInt(), // Bright cyan (Clima-Tact highlight & wind/spark VFX)
        'D' to 0xFF1C4EA3.toInt(), // Deep blue (Clima-Tact/cyclone shade)
        'N' to 0xFF233B75.toInt(), // Dark navy denim skirt base
        'J' to 0xFF15254D.toInt(), // Dark navy skirt shadow
        'E' to 0xFF4A2B1D.toInt(), // Dark brown sandals/boots
        'H' to 0xFF7D4E34.toInt(), // Sandal highlight
        'Y' to 0xFFFFE854.toInt(), // Electric lightning yellow (VFX & icons)
        'G' to 0xFF5A6B85.toInt(), // Storm cloud mid-gray
        'M' to 0xFF96A7C2.toInt(), // Storm cloud light rim
        'P' to 0xFF263044.toInt()  // Storm cloud dark base
    )

    val idleFrames: List<DirectionalSprite>
    val runFrames: List<DirectionalSprite>
    val jumpRise: DirectionalSprite
    val jumpFall: DirectionalSprite
    val landSquash: DirectionalSprite
    val hurtPose: DirectionalSprite

    // Clima-Tact Combat Poses
    val sparkPose: DirectionalSprite
    val cyclonePose: DirectionalSprite
    val thunderPose: DirectionalSprite

    // Projectiles & Weather VFX Sprites
    val electricBolt: DirectionalSprite
    val cycloneFrames: List<ImageBitmap>
    val thunderCloud: ImageBitmap

    // HUD Portrait & Skill Icons
    val hudPortrait: ImageBitmap
    val iconSpark: ImageBitmap
    val iconCyclone: ImageBitmap
    val iconThunderbolt: ImageBitmap

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
            PixelArtBuilder.buildPair(alignBottom(RUN_3, 30), palette)
        )
        jumpRise = PixelArtBuilder.buildPair(alignBottom(JUMP_RISE, 28), palette)
        jumpFall = PixelArtBuilder.buildPair(alignBottom(JUMP_FALL, 29), palette)
        landSquash = PixelArtBuilder.buildPair(alignBottom(LAND_SQUASH, 30), palette)
        hurtPose = PixelArtBuilder.buildPair(alignBottom(HURT_POSE, 30), palette)

        sparkPose = PixelArtBuilder.buildPair(alignBottom(SPARK_POSE, 30), palette)
        cyclonePose = PixelArtBuilder.buildPair(alignBottom(CYCLONE_POSE, 30), palette)
        thunderPose = PixelArtBuilder.buildPair(alignBottom(THUNDER_POSE, 30), palette)

        electricBolt = PixelArtBuilder.buildPair(ELECTRIC_BOLT, palette)
        cycloneFrames = listOf(
            PixelArtBuilder.build(CYCLONE_0, palette),
            PixelArtBuilder.build(CYCLONE_1, palette)
        )
        thunderCloud = PixelArtBuilder.build(THUNDER_CLOUD, palette)

        hudPortrait = PixelArtBuilder.build(HUD_PORTRAIT, palette)
        iconSpark = PixelArtBuilder.build(ICON_SPARK, palette)
        iconCyclone = PixelArtBuilder.build(ICON_CYCLONE, palette)
        iconThunderbolt = PixelArtBuilder.build(ICON_THUNDER, palette)
    }

    private fun alignBottom(rows: List<String>, targetBottomRow: Int): List<String> {
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

    companion object {
        // ============================================================
        // IDLE (28x32, feet strictly on row 30)
        // Same head/body/leg proportions & crisp 'K' outline as Luffy & Sanji.
        // Clean orange bob-cut, bright face with crisp eyes, 3-stripe white/blue top,
        // dark navy skirt, dark sandals, straight blue Clima-Tact (no idle sparks).
        // ============================================================
        private val IDLE_0 = listOf(
            "............................", // 0
            "............................", // 1
            "............................", // 2
            "............................", // 3
            "..........KKKKKK............", // 4  Clean bob-cut crown
            "........KKOOOOOOKK..........", // 5
            ".......KOOOOOOOORRK.........", // 6
            "......KROOOKKKOOORK.........", // 7  Neat bangs
            "......KROKSSSSSKOOK.........", // 8  Clean forehead & face
            "......KROKSWKSWSKOK.........", // 9  Crisp Luffy-style eyes
            "......KRRKSSSSSSKOK.........", // 10 Clean bright cheeks
            ".......KKKSSSSSSKK..........", // 11
            ".........KKAAAAKK...........", // 12 Neck
            "........KKWWWWWWKK....KK....", // 13 White stripe + straight Clima-Tact
            ".......KSSBBBBBBSSK..KCBK...", // 14 Blue stripe
            ".......KSSWWWWWWSSK.KCBK....", // 15 White stripe
            ".......KSSBBBBBBSSKKCBK.....", // 16 Blue stripe + hand holding staff
            "........KKWWWWWWKKKCBK......", // 17 Waist hem
            "........KNNNNNNNNKCBK.......", // 18 Dark navy denim skirt
            ".......KNNNNNNNJJKKBK.......", // 19
            ".......KNNNNNNJJJK.K........", // 20
            ".......KKKKKKKKKKK..........", // 21 Clean skirt hem
            ".........KSSK.KSSK..........", // 22 Straight clean legs
            ".........KSSK.KSSK..........", // 23
            ".........KSSK.KSSK..........", // 24
            ".........KSSK.KSSK..........", // 25
            ".........KSSK.KSSK..........", // 26
            ".........KHEK.KHEK..........", // 27 Dark sandals
            "........KHEEK.KHEEK.........", // 28
            "........KKKKK.KKKKK.........", // 29 -> shifted to row 30 by alignBottom
            "............................", // 30
            "............................"  // 31
        )

        // IDLE 1: Gentle 1px breathing bob (head & face completely undistorted!)
        private val IDLE_1 = listOf(
            "............................", // 0
            "............................", // 1
            "............................", // 2
            "............................", // 3
            "............................", // 4
            "..........KKKKKK............", // 5  1px lower torso/head breath
            "........KKOOOOOOKK..........", // 6
            ".......KOOOOOOOORRK.........", // 7
            "......KROOOKKKOOORK.........", // 8
            "......KROKSSSSSKOOK.........", // 9
            "......KROKSWKSWSKOK.........", // 10
            "......KRRKSSSSSSKOK.........", // 11
            ".......KKKSSSSSSKK..........", // 12
            ".........KKAAAAKK.....KK....", // 13
            "........KKWWWWWWKK...KCBK...", // 14
            ".......KSSBBBBBBSSK.KCBK....", // 15
            ".......KSSWWWWWWSSKKCBK.....", // 16
            ".......KSSBBBBBBSSKCBK......", // 17
            "........KNNNNNNNNKCBK.......", // 18
            ".......KNNNNNNNJJKKBK.......", // 19
            ".......KNNNNNNJJJK.K........", // 20
            ".......KKKKKKKKKKK..........", // 21
            ".........KSSK.KSSK..........", // 22
            ".........KSSK.KSSK..........", // 23
            ".........KSSK.KSSK..........", // 24
            ".........KSSK.KSSK..........", // 25
            ".........KSSK.KSSK..........", // 26
            ".........KHEK.KHEK..........", // 27
            "........KHEEK.KHEEK.........", // 28
            "........KKKKK.KKKKK.........", // 29
            "............................", // 30
            "............................"  // 31
        )

        // IDLE 2: Subtle calm breath lift (identical clean face & hair)
        private val IDLE_2 = listOf(
            "............................", // 0
            "............................", // 1
            "............................", // 2
            "............................", // 3
            "..........KKKKKK............", // 4
            "........KKOOOOOOKK..........", // 5
            ".......KOOOOOOOORRK.........", // 6
            "......KROOOKKKOOORK.........", // 7
            "......KROKSSSSSKOOK.........", // 8
            "......KROKSWKSWSKOK.........", // 9
            "......KRRKSSSSSSKOK.........", // 10
            ".......KKKSSSSSSKK..........", // 11
            ".........KKAAAAKK......KK...", // 12
            "........KKWWWWWWKK....KCBK..", // 13
            ".......KSSBBBBBBSSK..KCBK...", // 14
            ".......KSSWWWWWWSSK.KCBK....", // 15
            ".......KSSBBBBBBSSKKCBK.....", // 16
            "........KKWWWWWWKKKCBK......", // 17
            "........KNNNNNNNNKCBK.......", // 18
            ".......KNNNNNNNJJKKBK.......", // 19
            ".......KNNNNNNJJJK.K........", // 20
            ".......KKKKKKKKKKK..........", // 21
            ".........KSSK.KSSK..........", // 22
            ".........KSSK.KSSK..........", // 23
            ".........KSSK.KSSK..........", // 24
            ".........KSSK.KSSK..........", // 25
            ".........KSSK.KSSK..........", // 26
            ".........KHEK.KHEK..........", // 27
            "........KHEEK.KHEEK.........", // 28
            "........KKKKK.KKKKK.........", // 29
            "............................", // 30
            "............................"  // 31
        )

        // ============================================================
        // RUN CYCLE (4 crisp frames, 28x32, feet strictly on row 30)
        // Face & bob-cut stay completely clean and intact; staff held steady.
        // ============================================================
        private val RUN_0 = listOf(
            "............................", // 0
            "............................", // 1
            "............................", // 2
            "............................", // 3
            "...........KKKKKK...........", // 4
            ".........KKOOOOOOKK.........", // 5
            "........KOOOOOOOORRK........", // 6
            ".......KROOOKKKOOORK........", // 7
            ".......KROKSSSSSKOOK........", // 8
            ".......KROKSWKSWSKOK........", // 9
            ".......KRRKSSSSSSKOK........", // 10
            "........KKKSSSSSSKK.........", // 11
            "..........KKAAAAKK..........", // 12
            ".........KKWWWWWWKK.........", // 13
            "....KKKKKSSBBBBBBSSK........", // 14 Straight blue Clima-Tact held cleanly back
            "..KKCCCCBSSWWWWWWSSK........", // 15
            "..KDDDDKKSSBBBBBBSSK........", // 16
            ".........KKWWWWWWKK.........", // 17
            ".........KNNNNNNNNK.........", // 18
            "........KNNNNNNNJJJK........", // 19
            "........KNNNNNNJJJJK........", // 20
            "........KKKKKKKKKKKK........", // 21
            "........KSSK....KSSK........", // 22 Wide stride
            ".......KSSK......KSSK.......", // 23
            "......KSSK........KSSK......", // 24
            ".....KSSK..........KSSK.....", // 25
            ".....KHEK..........KHEK.....", // 26
            "....KHEEK..........KHEEK....", // 27
            "....KKKKK..........KKKKK....", // 28
            "............................", // 29
            "............................", // 30
            "............................"  // 31
        )

        private val RUN_1 = listOf(
            "............................", // 0
            "............................", // 1
            "............................", // 2
            "............................", // 3
            "............................", // 4
            "...........KKKKKK...........", // 5  Passing stride 1px bob
            ".........KKOOOOOOKK.........", // 6
            "........KOOOOOOOORRK........", // 7
            ".......KROOOKKKOOORK........", // 8
            ".......KROKSSSSSKOOK........", // 9
            ".......KROKSWKSWSKOK........", // 10
            ".......KRRKSSSSSSKOK........", // 11
            "........KKKSSSSSSKK.........", // 12
            "..........KKAAAAKK..........", // 13
            ".........KKWWWWWWKK.........", // 14
            ".....KKKKSSBBBBBBSSK........", // 15
            "...KKCCCBSSWWWWWWSSK........", // 16
            "...KDDDKKSSBBBBBBSSK........", // 17
            ".........KNNNNNNNNK.........", // 18
            "........KNNNNNNNJJJK........", // 19
            "........KNNNNNNJJJJK........", // 20
            "........KKKKKKKKKKKK........", // 21
            "..........KSSKKSSK..........", // 22 Passing legs
            "..........KSSKKSSK..........", // 23
            "..........KSSKKSSK..........", // 24
            "..........KSSK.KHEK.........", // 25
            "..........KHEK.KKKK.........", // 26
            ".........KHEEK..............", // 27
            ".........KKKKK..............", // 28
            "............................", // 29
            "............................", // 30
            "............................"  // 31
        )

        private val RUN_2 = listOf(
            "............................", // 0
            "............................", // 1
            "............................", // 2
            "............................", // 3
            "...........KKKKKK...........", // 4
            ".........KKOOOOOOKK.........", // 5
            "........KOOOOOOOORRK........", // 6
            ".......KROOOKKKOOORK........", // 7
            ".......KROKSSSSSKOOK........", // 8
            ".......KROKSWKSWSKOK........", // 9
            ".......KRRKSSSSSSKOK........", // 10
            "........KKKSSSSSSKK.........", // 11
            "..........KKAAAAKK..........", // 12
            ".........KKWWWWWWKK.........", // 13
            "....KKKKKSSBBBBBBSSK........", // 14
            "..KKCCCCBSSWWWWWWSSK........", // 15
            "..KDDDDKKSSBBBBBBSSK........", // 16
            ".........KKWWWWWWKK.........", // 17
            ".........KNNNNNNNNK.........", // 18
            "........KNNNNNNNJJJK........", // 19
            "........KNNNNNNJJJJK........", // 20
            "........KKKKKKKKKKKK........", // 21
            ".........KSSK..KSSK.........", // 22 Opposite stride
            "........KSSK....KSSK........", // 23
            ".......KSSK......KSSK.......", // 24
            ".......KSSK......KSSK.......", // 25
            "......KHEK.......KHEK.......", // 26
            ".....KHEEK.......KHEEK......", // 27
            ".....KKKKK.......KKKKK......", // 28
            "............................", // 29
            "............................", // 30
            "............................"  // 31
        )

        private val RUN_3 = listOf(
            "............................", // 0
            "............................", // 1
            "............................", // 2
            "............................", // 3
            "............................", // 4
            "...........KKKKKK...........", // 5
            ".........KKOOOOOOKK.........", // 6
            "........KOOOOOOOORRK........", // 7
            ".......KROOOKKKOOORK........", // 8
            ".......KROKSSSSSKOOK........", // 9
            ".......KROKSWKSWSKOK........", // 10
            ".......KRRKSSSSSSKOK........", // 11
            "........KKKSSSSSSKK.........", // 12
            "..........KKAAAAKK..........", // 13
            ".........KKWWWWWWKK.........", // 14
            ".....KKKKSSBBBBBBSSK........", // 15
            "...KKCCCBSSWWWWWWSSK........", // 16
            "...KDDDKKSSBBBBBBSSK........", // 17
            ".........KNNNNNNNNK.........", // 18
            "........KNNNNNNNJJJK........", // 19
            "........KNNNNNNJJJJK........", // 20
            "........KKKKKKKKKKKK........", // 21
            ".........KSSKKSSK...........", // 22 Opposite passing legs
            ".........KSSKKSSK...........", // 23
            ".........KHEKKSSK...........", // 24
            ".........KKKK.KSSK..........", // 25
            "..............KHEK..........", // 26
            ".............KHEEK..........", // 27
            ".............KKKKK..........", // 28
            "............................", // 29
            "............................", // 30
            "............................"  // 31
        )

        // ============================================================
        // JUMP RISE / JUMP FALL / LAND SQUASH / HURT
        // ============================================================
        private val JUMP_RISE = listOf(
            "............................", // 0
            "............................", // 1
            "............................", // 2
            "..........KKKKKK............", // 3
            "........KKOOOOOOKK..........", // 4
            ".......KOOOOOOOORRK.........", // 5
            "......KROOOKKKOOORK.........", // 6
            "......KROKSSSSSKOOK...KK....", // 7
            "......KROKSWKSWSKOK..KCBK...", // 8
            "......KRRKSSSSSSKOK.KCBK....", // 9
            ".......KKKSSSSSSKK.KCBK.....", // 10
            ".........KKAAAAKK.KCBK......", // 11
            "........KKWWWWWWKKCBK.......", // 12
            ".......KSSBBBBBBSSBK........", // 13
            ".......KSSWWWWWWSSK.........", // 14
            ".......KSSBBBBBBSSK.........", // 15
            "........KNNNNNNNNK..........", // 16
            ".......KNNNNNNNJJJK.........", // 17
            ".......KNNNNNNJJJJK.........", // 18
            ".......KKKKKKKKKKKK.........", // 19
            "........KSSK...KSSK.........", // 20 Clean agile jump tuck
            "........KSSK....KSSK........", // 21
            "........KHEK....KHEK........", // 22
            ".......KHEEK....KKKK........", // 23
            ".......KKKKK................", // 24
            "............................", // 25
            "............................", // 26
            "............................", // 27
            "............................", // 28
            "............................", // 29
            "............................", // 30
            "............................"  // 31
        )

        private val JUMP_FALL = listOf(
            "............................", // 0
            "............................", // 1
            "............................", // 2
            "..........KKKKKK............", // 3
            "........KKOOOOOOKK..........", // 4
            ".......KOOOOOOOORRK.........", // 5
            "......KROOOKKKOOORK.........", // 6
            "......KROKSSSSSKOOK.........", // 7
            "......KROKSWKSWSKOK.........", // 8
            "......KRRKSSSSSSKOK.........", // 9
            ".......KKKSSSSSSKK..........", // 10
            "....KK...KKAAAAKK...........", // 11
            "...KCBK.KKWWWWWWKK..........", // 12
            "....KCBKSSBBBBBBSSK.........", // 13
            ".....KCBSSWWWWWWSSK.........", // 14
            "......KKSSBBBBBBSSK.........", // 15
            "........KNNNNNNNNK..........", // 16
            ".......KNNNNNNNJJJK.........", // 17
            ".......KNNNNNNJJJJK.........", // 18
            ".......KKKKKKKKKKKK.........", // 19
            "........KSSK..KSSK..........", // 20
            "........KSSK..KSSK..........", // 21
            "........KSSK..KSSK..........", // 22
            "........KHEK..KHEK..........", // 23
            ".......KHEEK..KHEEK.........", // 24
            ".......KKKKK..KKKKK.........", // 25
            "............................", // 26
            "............................", // 27
            "............................", // 28
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
            "..........KKKKKK............", // 6
            "........KKOOOOOOKK..........", // 7
            ".......KOOOOOOOORRK.........", // 8
            "......KROOOKKKOOORK.........", // 9
            "......KROKSSSSSKOOK.........", // 10
            "......KROKSWKSWSKOK.........", // 11
            "......KRRKSSSSSSKOK.........", // 12
            ".......KKKSSSSSSKK..........", // 13
            ".........KKAAAAKK.....KK....", // 14
            "........KKWWWWWWKK...KCBK...", // 15
            ".......KSSBBBBBBSSK.KCBK....", // 16
            ".......KSSWWWWWWSSKKCBK.....", // 17
            ".......KSSBBBBBBSSKCBK......", // 18
            "........KNNNNNNNNKKBK.......", // 19
            ".......KNNNNNNNJJJKK........", // 20
            ".......KNNNNNNJJJJK.........", // 21
            ".......KKKKKKKKKKKK.........", // 22
            "........KSSK..KSSK..........", // 23 Bent landing knees
            ".......KSSK....KSSK.........", // 24
            ".......KSSK....KSSK.........", // 25
            ".......KHEK....KHEK.........", // 26
            "......KHEEK....KHEEK........", // 27
            "......KKKKK....KKKKK........", // 28
            "............................", // 29
            "............................", // 30
            "............................"  // 31
        )

        private val HURT_POSE = listOf(
            "............................", // 0
            "............................", // 1
            "............................", // 2
            "............................", // 3
            "........KKKKKK..............", // 4
            "......KKOOOOOOKK............", // 5
            ".....KOOOOOOOORRK...........", // 6
            "....KROOOKKKOOORK...........", // 7
            "....KROKSSSSSKOOK...........", // 8
            "....KROKSSWWSSKOK...........", // 9  Surprised recoil eyes
            "....KRRKSSSSSSKOK...........", // 10
            ".....KKKSSKKSSKK............", // 11
            ".......KKAAAAKK.............", // 12
            "......KKWWWWWWKK............", // 13
            ".....KSSBBBBBBSSK...........", // 14
            ".....KSSWWWWWWSSK...........", // 15
            "......KKBBBBBBKK............", // 16
            ".......KNNNNNNNK............", // 17
            "......KNNNNNNJJJK...........", // 18
            "......KNNNNNJJJJK...........", // 19
            "......KKKKKKKKKKK...........", // 20
            ".......KSSK..KSSK...........", // 21
            "........KSSK..KSSK..........", // 22
            "........KSSK..KSSK..........", // 23
            "........KSSK..KSSK..........", // 24
            "........KHEK..KHEK..........", // 25
            ".......KHEEK..KHEEK.........", // 26
            ".......KKKKK..KKKKK.........", // 27
            "............................", // 28
            "............................", // 29
            "............................", // 30
            "............................"  // 31
        )

        // ============================================================
        // ATTACK 1 (Z): CLIMA-TACT SPARK — Clean forward thrust!
        // Straight horizontal blue Clima-Tact thrust ahead; electric sparks
        // render separately in front of the tip so Nami stays crisp.
        // ============================================================
        private val SPARK_POSE = listOf(
            "............................", // 0
            "............................", // 1
            "............................", // 2
            "............................", // 3
            ".........KKKKKK.............", // 4
            ".......KKOOOOOOKK...........", // 5
            "......KOOOOOOOORRK..........", // 6
            ".....KROOOKKKOOORK..........", // 7
            ".....KROKSSSSSKOOK..........", // 8
            ".....KROKSWKSWSKOK..........", // 9
            ".....KRRKSSSSSSKOK..........", // 10
            "......KKKSSSSSSKK...........", // 11
            "........KKAAAAKK............", // 12
            ".......KKWWWWWWKK...........", // 13
            "......KSSBBBBBBSSKKKKKKKKKKK", // 14 Straight forward Clima-Tact thrust
            "......KSSWWWWWWSSCCCCCCCCCCK", // 15 Bright cyan staff highlight
            "......KSSBBBBBBSSDDDDDDDDDDK", // 16 Clean blue staff shadow
            ".......KKWWWWWWKKKKKKKKKKKKK", // 17
            ".......KNNNNNNNNK...........", // 18
            "......KNNNNNNNJJJK..........", // 19
            "......KNNNNNNJJJJK..........", // 20
            "......KKKKKKKKKKKK..........", // 21
            "......KSSK....KSSK..........", // 22 Clean lunging stance
            ".....KSSK......KSSK.........", // 23
            ".....KSSK......KSSK.........", // 24
            "....KSSK........KSSK........", // 25
            "....KHEK........KHEK........", // 26
            "...KHEEK........KHEEK.......", // 27
            "...KKKKK........KKKKK.......", // 28
            "............................", // 29
            "............................", // 30
            "............................"  // 31
        )

        // ============================================================
        // ATTACK 2 (X): CYCLONE TEMPO — Clean forward diagonal sweep!
        // ============================================================
        private val CYCLONE_POSE = listOf(
            "............................", // 0
            "............................", // 1
            "............................", // 2
            "............................", // 3
            ".........KKKKKK.............", // 4
            ".......KKOOOOOOKK.....KK....", // 5
            "......KOOOOOOOORRK...KCBK...", // 6
            ".....KROOOKKKOOORK..KCBK....", // 7
            ".....KROKSSSSSKOOK.KCBK.....", // 8
            ".....KROKSWKSWSKOKKCBK......", // 9
            ".....KRRKSSSSSSKOKCBK.......", // 10
            "......KKKSSSSSSKKCBK........", // 11
            "........KKAAAAKKCBK.........", // 12
            ".......KKWWWWWWKCBK.........", // 13 Diagonal Clima-Tact sweep
            "......KSSBBBBBBSSK..........", // 14
            "......KSSWWWWWWSSK..........", // 15
            "......KSSBBBBBBSSK..........", // 16
            ".......KKWWWWWWKK...........", // 17
            ".......KNNNNNNNNK...........", // 18
            "......KNNNNNNNJJJK..........", // 19
            "......KNNNNNNJJJJK..........", // 20
            "......KKKKKKKKKKKK..........", // 21
            ".......KSSK...KSSK..........", // 22
            "......KSSK.....KSSK.........", // 23
            "......KSSK.....KSSK.........", // 24
            ".....KSSK.......KSSK........", // 25
            ".....KHEK.......KHEK........", // 26
            "....KHEEK.......KHEEK.......", // 27
            "....KKKKK.......KKKKK.......", // 28
            "............................", // 29
            "............................", // 30
            "............................"  // 31
        )

        // ============================================================
        // ATTACK 3 (C): THUNDERBOLT TEMPO — Staff raised cleanly skyward!
        // Electric lightning VFX renders above the tip so Nami stays crisp.
        // ============================================================
        private val THUNDER_POSE = listOf(
            ".............KKKK...........", // 0  Staff tip raised straight up
            ".............KCCK...........", // 1
            ".............KCBK...........", // 2
            ".............KCBK...........", // 3
            ".........KKKKKCBK...........", // 4
            ".......KKOOOOOCBK...........", // 5
            "......KOOOOOOOCBK...........", // 6
            ".....KROOOKKKOCBK...........", // 7
            ".....KROKSSSSSCBK...........", // 8
            ".....KROKSWKSWSBK...........", // 9
            ".....KRRKSSSSSSSK...........", // 10 Raised hand gripping staff
            "......KKKSSSSSSK............", // 11
            "........KKAAAAKK............", // 12
            ".......KKWWWWWWKK...........", // 13
            "......KSSBBBBBBSSK..........", // 14
            "......KSSWWWWWWSSK..........", // 15
            "......KSSBBBBBBSSK..........", // 16
            ".......KKWWWWWWKK...........", // 17
            ".......KNNNNNNNNK...........", // 18
            "......KNNNNNNNJJJK..........", // 19
            "......KNNNNNNJJJJK..........", // 20
            "......KKKKKKKKKKKK..........", // 21
            "........KSSK.KSSK...........", // 22
            "........KSSK.KSSK...........", // 23
            "........KSSK.KSSK...........", // 24
            "........KSSK.KSSK...........", // 25
            "........KHEK.KHEK...........", // 26
            ".......KHEEK.KHEEK..........", // 27
            ".......KKKKK.KKKKK..........", // 28
            "............................", // 29
            "............................", // 30
            "............................"  // 31
        )

        // ============================================================
        // ELECTRIC BOLT PROJECTILE (14x9) — Crisp cyan-yellow bolt
        // ============================================================
        private val ELECTRIC_BOLT = listOf(
            "......CC......",
            "....CCYYCC....",
            "..CCYYWWYYCC..",
            ".CYYWWWWWWYYC.",
            "CWWWWWWWWWWWWC",
            ".CYYWWWWWWYYC.",
            "..CCYYWWYYCC..",
            "....CCYYCC....",
            "......CC......"
        )

        // ============================================================
        // CYCLONE TEMPO WHIRLWIND (18x22, 2 swirling frames)
        // ============================================================
        private val CYCLONE_0 = listOf(
            "..KCCCCCCCCCCCCK..",
            ".KCCWWWWWWWWWWCCK.",
            "KCBWWWWCCCCWWWWBCK",
            ".KDDBBCCCCCCBBDDK.",
            "..KBBWWWWWWWWBBK..",
            "...KCCCCCCCCCCK...",
            "..KCBWWCCCCWWBCK..",
            "...KDBBCCCCBBDK...",
            "....KBWWWWWWBK....",
            "....KCCCCCCCCK....",
            "...KCBWWCCWWBCK...",
            "....KDBBCCBBDK....",
            ".....KBWWWWBK.....",
            ".....KCCCCCCK.....",
            "....KCWWCCWWCK....",
            ".....KDBBBBDK.....",
            "......KWWWWK......",
            "......KCCCCK......",
            ".......KBBK.......",
            ".......KCCK.......",
            "........KK........",
            ".................."
        )

        private val CYCLONE_1 = listOf(
            "...KCCCCCCCCCCK...",
            "..KCWWWWWWWWWWCK..",
            ".KCBCCWWWWWWCCBCK.",
            "KDDBBWWWWWWWWBBDDK",
            ".KBBCCCCCCCCCCBBK.",
            "..KCCWWWWWWWWCCK..",
            "...KCBCCCCCCBCK...",
            "..KDBBWWWWWWBBDK..",
            "...KBCCCCCCCCBK...",
            "....KCWWWWWWCK....",
            "....KCBCCCCBCK....",
            "...KDBBWWWWBBDK...",
            "....KBCCCCCCBK....",
            ".....KCWWWWCK.....",
            ".....KCBCCBCK.....",
            "....KDBBWWBBDK....",
            ".....KBCCCCBK.....",
            "......KWWWWK......",
            "......KCBBK.......",
            ".......KCCK.......",
            "........KK........",
            ".................."
        )

        // ============================================================
        // THUNDERBOLT TEMPO STORM CLOUD (28x14)
        // ============================================================
        private val THUNDER_CLOUD = listOf(
            "........PPPP....PPPP........",
            "......PPMMMMPPPPMMMMPP......",
            "....PPMMGGGGMMMMGGGGMMPP....",
            "...PMMGGGGGGMMMMGGGGGGMMP...",
            "..PMGGGGGGGGGGGGGGGGGGGGMP..",
            ".PMGPPPGGGGGGGGGGGGPPPGGMP..",
            "PMGGPPPPPGGGYYYYGGGPPPPPGMP.",
            "PPGGGGGGGPYYYYYYYYPGGGGGGPP.",
            ".PPPGGGGGGPPWWWWPPPGGGGPPP..",
            "..PPPPGGGGGGYYYYGGGGGPPPP...",
            "....PPPPPGGYYYYYYGGPPPPP....",
            ".......PP..YYWWYY..PP.......",
            "............YYYY............",
            ".............YY............."
        )

        // ============================================================
        // HUD PORTRAIT (18x18) — Matches Luffy & Sanji's EXACT head scale!
        // Clean orange bob-cut with bangs, bright clean face, crisp eyes,
        // white-and-blue striped shirt collar, crisp 'K' outline.
        // ============================================================
        private val HUD_PORTRAIT = listOf(
            "....KKKKKKKKKK....", // 0
            "..KKOOOOOOOOOOKK..", // 1  Clean solid bob-cut crown
            ".KOOOOOOOOOOOORRK.", // 2
            ".KROOOKKKKKKOOORK.", // 3  Clean straight bangs
            "KROOOKSSSSSSKOORK.", // 4  Bright clean forehead
            "KROOKSSSSSSSSKOOK.", // 5
            "KROOKSWKSSKWSKOOK.", // 6  Crisp expressive eyes (matches Luffy)
            "KROOKSSKSSKSSKOOK.", // 7
            "KRRKSSSSSSSSSSKOK.", // 8  Clean bright face (no clutter!)
            ".KKKSSSKKKSSSSKK..", // 9  Cheerful smile
            "...KSSSSSSSSSSK...", // 10
            "....KKAAAAAAKK....", // 11 Chin & neck
            "...KKWWWWWWWWKK...", // 12 White top stripe
            "..KSSBBBBBBBBSSK..", // 13 Blue top stripe
            "..KSSWWWWWWWWSSK..", // 14 White top stripe
            "..KSSBBBBBBBBSSK..", // 15 Blue top stripe
            "..KKKKKKKKKKKKKK..", // 16
            ".................."  // 17
        )

        // ============================================================
        // HUD SKILL ICONS (16x16) — Crisp clean icons
        // ============================================================
        private val ICON_SPARK = listOf(
            "................",
            "..........YY....",
            ".........YWWY...",
            "........CWWWWC..",
            ".......CWWYYWWC.",
            "......KCCWWWWCCK",
            ".....KCBKCWWCK..",
            "....KCBK..CC....",
            "...KCBK.........",
            "..KCBK..........",
            ".KCBK...........",
            ".KDK............",
            "..K.............",
            "................",
            "................",
            "................"
        )

        private val ICON_CYCLONE = listOf(
            "................",
            "..KCCCCCCCCCCK..",
            ".KCWWWWWWWWWWCK.",
            "KCBCCWWWWWWCCBCK",
            ".KDDBBCCCCBBDDK.",
            "..KBWWWWWWWWBK..",
            "...KCCCCCCCCK...",
            "..KCBWCCCCWBCK..",
            "...KDBBCCBBDK...",
            "....KBWWWWBK....",
            "....KCCCCCCK....",
            ".....KCWWCK.....",
            "......KBBK......",
            ".......KK.......",
            "................",
            "................"
        )

        private val ICON_THUNDER = listOf(
            "................",
            "....PPPPPPPP....",
            "..PPMMGGGGMMPP..",
            ".PMGGGGGGGGGGMP.",
            "PMGGPPGGGGPPGGMP",
            ".PPPGGYYYYGGPPP.",
            "...PPYYWWYYPP...",
            ".....YYWWY......",
            "....YYWWY.......",
            "...YYWWWWYY.....",
            ".....YYWWY......",
            "......YYW.......",
            ".......YY.......",
            ".......Y........",
            "................",
            "................"
        )
    }
}
