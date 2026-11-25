package com.example.game.sprites

import androidx.compose.ui.graphics.ImageBitmap

/**
 * Hand-crafted 16-bit Pixel Art Sprite Sheet for Monkey D. Luffy:
 * 1) Post-Timeskip Base Luffy (28x34): tilted golden straw hat with crimson ribbon,
 *    open 3/4-sleeve crimson cardigan with 3 white buttons, bare chest with iconic X-scar,
 *    golden-yellow waist sash with diagonal tail, athletic cuffed blue denim shorts,
 *    straw sandals, and raised-fist fighting guard stance.
 * 2) Gear 4: Boundman (48x38): spiky black hair & fierce grin, huge puffed muscular torso
 *    with rosy X-scar and dark crimson Haki flames on both shoulders, colossal dual
 *    obsidian-crimson Busoshoku Haki fists, fluttering red cardigan flaps, golden waist sash
 *    with streaming ribbon tail, compact cuffed blue shorts, and springy Haki tiptoes.
 */
class LuffySpriteSheet {

    private val luffyPalette: Map<Char, Int> = mapOf(
        '.' to 0x00000000, // Transparent
        'K' to 0xFF14101C.toInt(), // Crisp 16-bit dark ink outline
        'L' to 0xFFFFF2A6.toInt(), // Straw hat / sash bright cream-gold highlight
        'Y' to 0xFFE8C451.toInt(), // Straw hat / waist sash rich gold
        'O' to 0xFFAD8224.toInt(), // Straw hat / sash warm ochre shadow
        'R' to 0xFFD9234F.toInt(), // Post-Timeskip crimson-red cardigan & hat band
        'P' to 0xFFFF597B.toInt(), // Crimson bright highlight
        'D' to 0xFF7D0D2C.toInt(), // Deep burgundy cardigan shadow
        'H' to 0xFF1E1B29.toInt(), // Shaggy jet-black hair
        'Q' to 0xFFFFE1A8.toInt(), // Warm sunlit skin highlight
        'S' to 0xFFF2C37E.toInt(), // Warm golden-tan skin midtone
        'T' to 0xFFCC8D43.toInt(), // Warm bronze skin shadow
        'C' to 0xFFB54534.toInt(), // Iconic X-shaped chest scar & under-eye scar
        'W' to 0xFFFFFFFF.toInt(), // White sclera, cardigan buttons, shorts cuffs
        'G' to 0xFFFFD84D.toInt(), // Gold buckle/spark accent
        'A' to 0xFF4E82DE.toInt(), // Denim shorts bright blue highlight
        'B' to 0xFF2952A8.toInt(), // Denim shorts cobalt midtone
        'N' to 0xFF152C66.toInt(), // Denim shorts deep navy shadow
        'M' to 0xFFC98842.toInt(), // Straw sandal leather/straw
        'U' to 0xFF6E3F19.toInt(), // Sandal dark brown strap/sole
        'E' to 0xFF5EE8FF.toInt(), // Wind/rubber impact cyan gleam
        'V' to 0xFF241B33.toInt()  // Deep slate accent
    )

    /**
     * Shifts a 34-row Base Luffy sprite vertically so its lowest non-transparent pixel sits on
     * [targetBottomRow], guaranteeing zero vertical foot pop across all animations.
     */
    private fun alignBottom(rows: List<String>, targetBottomRow: Int = 32): List<String> {
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

    // =========================================================================
    // 28x34 POST-TIMESKIP BASE LUFFY SPRITES (facing RIGHT; mirrored for LEFT)
    // =========================================================================

    private val idleFrame0 = listOf(
        "..........KKKKKK............",
        "........KKLLYYYYKK..........",
        ".......KLLYYRRRRYYKKKKK.....",
        "......KLYRRRPPPPRRYYYYOK....",
        "......KRRDDYYYYYYOOKKKK.....",
        ".....KLYYOOHHHHKKKK.........",
        "....KLOOOHHSHWKSHK..........",
        "....KKKKKHHSSSTSHK..........",
        ".........KKSSTSSK....KKKK...",
        "..........KKSTKK....KQQSSK..",
        ".......KKKKSTTSKKK..KSTKTK..",
        "......KRPRKSQTSRRPKK.KTSTK..",
        ".....KRPRWKQCTQRRDPKKSQSTK..",
        "....KDRPRWKSCCSRDRPKKSSTK...",
        "....KDRPKWKTCTTRDRPPRSTK....",
        "...KDRPK.WKQSSQRRDDPPKK.....",
        "...KTTK..KDRYYYRDRKK........",
        "..KQQSK..KDRLYYYRDK.........",
        "..KSTSK...KKBBNBYOK.........",
        "..KQTK....KBAABBBYOK........",
        "...KK....KBAAABBBBYOK.......",
        ".........KBAABBBBNBYOK......",
        "........KBAABKKBBBNBYK......",
        "........KBBBK..KBBBNK.......",
        ".......KWWWK....KWWWK.......",
        ".......KTSK......KSTK.......",
        "......KQTSK......KSTQK......",
        "......KQSTK......KSTQK......",
        ".....KQTSK........KSTQK.....",
        ".....KTSK..........KSTK.....",
        "...KKMMUK..........KUMMKK...",
        "..KMMUMUK..........KUMUMMK..",
        "..KKKKKKK..........KKKKKKK..",
        "............................"
    )

    private val idleFrame1 = listOf(
        "............................",
        "..........KKKKKK............",
        "........KKLLYYYYKK..........",
        ".......KLLYYRRRRYYKKKKK.....",
        "......KLYRRRPPPPRRYYYYOK....",
        "......KRRDDYYYYYYOOKKKK.....",
        ".....KLYYOOHHHHKKKK.........",
        "....KLOOOHHSHWKSHK..........",
        "....KKKKKHHSSSTSHK..........",
        ".........KKSSTSSK.....KKKK..",
        "..........KKSTKK.....KQQSSK.",
        ".......KKKKSTTSKKK...KSTKTK.",
        "......KRPRKSQTSRRPKK..KTSTK.",
        ".....KRPRWKQCTQRRDPK.KSQSTK.",
        "....KDRPRWKSCCSRDRPKKKSSTK..",
        "....KDRPKWKTCTTRDRPPRSTK....",
        "...KDRPK.WKQSSQRRDDPPKK.....",
        "...KTTK..KDRYYYRDRKK........",
        "..KQQSK..KDRLYYYRDK.........",
        "..KSTSK...KKBBNBYOK.........",
        "...KKK....KBAABBBYOK........",
        ".........KBAAABBBBYOK.......",
        "........KBAABBBBNBYOK.......",
        ".......KBAABKKKBBBNBYK......",
        ".......KBBBK...KBBBNK.......",
        "......KWWWK.....KWWWK.......",
        "......KQTSK.....KSTQK.......",
        ".....KQTSK.......KSTQK......",
        ".....KQSTK.......KSTQK......",
        "....KQTSK.........KSTQK.....",
        "...KKMMUK.........KUMMKK....",
        "..KMMUMUK.........KUMUMMK...",
        "..KKKKKKK.........KKKKKKK...",
        "............................"
    )

    private val idleFrame2 = listOf(
        "..........KKKKKK............",
        "........KKLLYYYYKK..........",
        ".......KLLYYRRRRYYKKKKK.....",
        "......KLYRRRPPPPRRYYYYOK....",
        "......KRRDDYYYYYYOOKKKK.....",
        ".....KLYYOOHHHHKKKK.........",
        "....KLOOOHHSHWKSHK..........",
        "....KKKKKHHSSSTSHK...KKKK...",
        ".........KKSSTSSK...KQQSSK..",
        "..........KKSTKK....KSTKTK..",
        ".......KKKKSTTSKKK...KTSTK..",
        "......KRPRKSQTSRRPKKKSQSTK..",
        ".....KRPRWKQCTQRRDPKKSSTK...",
        "....KDRPRWKSCCSRDRPPKSTK....",
        "....KDRPKWKTCTTRDRDDPKK.....",
        "...KDRPK.WKQSSQRRDKK........",
        "...KTTK..KDRYYYRDRK.........",
        "..KQQSK..KDRLYYYRDK.........",
        "..KSTSK...KKBBNBYOK.........",
        "...KKK....KBAABBBYOK........",
        ".........KBAAABBBBYOK.......",
        ".........KBAABBBBNBYOK......",
        "........KBAABKKBBBNBYK......",
        "........KBBBK..KBBBNK.......",
        ".......KWWWK....KWWWK.......",
        ".......KQTSK....KSTQK.......",
        "......KQTSK......KSTQK......",
        "......KQSTK......KSTQK......",
        "......KTSK........KSTK......",
        ".....KQTSK........KSTQK.....",
        "...KKMMUK..........KUMMKK...",
        "..KMMUMUK..........KUMUMMK..",
        "..KKKKKKK..........KKKKKKK..",
        "............................"
    )

    // 4-frame athletic Post-Timeskip sprint with billowing red cardigan & trailing yellow sash
    private val runFrame0 = listOf(
        ".............KKKKKK.........",
        "...........KKLLYYYYKK.......",
        "..........KLLYYRRRRYYKKKK...",
        ".........KLYRRRPPPPRRYYYOK..",
        ".........KRRDDYYYYYYOOKKK...",
        "........KLYYOOHHHHKKKK......",
        ".......KLOOOHHSHWKSHK.......",
        ".......KKKKKHHSSSTSHK.......",
        "............KKSSTSSK........",
        "........KKKKKKSTKK..........",
        "......KDRPPRKSQTSRPKK.......",
        ".....KDRPRRWKQCTQRRDPK......",
        "....KDRPRK.WKSCCSRRDPKK.....",
        "...KDRPK...WKTCTTRRPRSTK....",
        "..KDRKK...KDRYYYRDRPKSQSK...",
        "...KK..KKKYYRLYYYRDK.KSTK...",
        "......KYYOOKKBBNBBK...KK....",
        ".....KYYOK.KBAABBBBK........",
        "......KK..KBAAABBBBBK.......",
        ".........KBAABKKBBBNBK......",
        "........KBBBK....KBBBNK.....",
        ".......KWWWK......KWWWK.....",
        "......KQTSK........KSTQK....",
        ".....KQTSK..........KSTQK...",
        "....KQSTK............KSTQK..",
        "....KTSK..............KSTK..",
        "...KMMUK..............KUMMK.",
        "..KMMUMK..............KUMMMK",
        "..KKKKK................KKKKK",
        "............................",
        "............................",
        "............................",
        "............................",
        "............................"
    )

    private val runFrame1 = listOf(
        "............................",
        ".............KKKKKK.........",
        "...........KKLLYYYYKK.......",
        "..........KLLYYRRRRYYKKKK...",
        ".........KLYRRRPPPPRRYYYOK..",
        ".........KRRDDYYYYYYOOKKK...",
        "........KLYYOOHHHHKKKK......",
        ".......KLOOOHHSHWKSHK.......",
        ".......KKKKKHHSSSTSHK.......",
        "............KKSSTSSK........",
        ".........KKKKKSTKK..........",
        ".......KDRPRKSQTSRPKK.......",
        "......KDRPRWKQCTQRRDPK......",
        ".....KDRPK.WKSCCSRRDPK......",
        "....KDRKK..WKTCTTRDRSK......",
        "....KTTK..KDRYYYRDRSQK......",
        "....KQQK.KKYRLYYYRDKSTK.....",
        ".....KK.KYYOKBBNBBK.KK......",
        "........KYYKBAABBBK.........",
        ".........KKBAAABBBK.........",
        "..........KBAABBNBK.........",
        "..........KBBBKKBBK.........",
        ".........KWWWK.KWWWK........",
        ".........KQTSK.KSTQK........",
        "........KQTSK...KSTQK.......",
        "........KTSK....KSTK........",
        ".......KMMUK....KUMMK.......",
        "......KMMUMK....KUMMMK......",
        "......KKKKK......KKKKK......",
        "............................",
        "............................",
        "............................",
        "............................",
        "............................"
    )

    private val runFrame2 = listOf(
        ".............KKKKKK.........",
        "...........KKLLYYYYKK.......",
        "..........KLLYYRRRRYYKKKK...",
        ".........KLYRRRPPPPRRYYYOK..",
        ".........KRRDDYYYYYYOOKKK...",
        "........KLYYOOHHHHKKKK......",
        ".......KLOOOHHSHWKSHK.......",
        ".......KKKKKHHSSSTSHK.......",
        "............KKSSTSSK........",
        "........KKKKKKSTKK..........",
        ".......KDRPRKSQTSRPKKK......",
        "......KDRPRWKQCTQRRDPPK.....",
        ".....KDRPK.WKSCCSRDRPPK.....",
        "....KTTKK..WKTCTTRDKSTSK....",
        "...KQQSK..KDRYYYRDRK.KQQK...",
        "...KSTK.KKYYRLYYYRDK..KK....",
        "....KK.KYYOKKBBNBBK.........",
        ".......KYYK.KBAABBBK........",
        "........KK.KBAAABBBBK.......",
        "..........KBAABKKBBBNK......",
        ".........KBBBK...KBBBNK.....",
        "........KWWWK.....KWWWK.....",
        ".......KQTSK.......KSTQK....",
        "......KQTSK.........KSTQK...",
        ".....KQSTK...........KSTK...",
        ".....KTSK............KUMMK..",
        "....KMMUK............KUMMMK.",
        "...KMMUMK.............KKKKK.",
        "...KKKKK....................",
        "............................",
        "............................",
        "............................",
        "............................",
        "............................"
    )

    private val runFrame3 = listOf(
        "............................",
        ".............KKKKKK.........",
        "...........KKLLYYYYKK.......",
        "..........KLLYYRRRRYYKKKK...",
        ".........KLYRRRPPPPRRYYYOK..",
        ".........KRRDDYYYYYYOOKKK...",
        "........KLYYOOHHHHKKKK......",
        ".......KLOOOHHSHWKSHK.......",
        ".......KKKKKHHSSSTSHK.......",
        "............KKSSTSSK........",
        ".........KKKKKSTKK..........",
        ".......KDRPRKSQTSRPKK.......",
        "......KDRPRWKQCTQRRDPK......",
        ".....KDRPR.WKSCCSRRDPK......",
        ".....KDRK..WKTCTTRDRPK......",
        ".....KTTK.KDRYYYRDRKK.......",
        "......KK.KKYRLYYYRDK........",
        "........KYYOKBBNBBK.........",
        "........KYYKBAABBBK.........",
        ".........KKBAAABBBK.........",
        "..........KBAABBNBK.........",
        ".........KBBBKKBBBK.........",
        ".........KWWWK.KWWWK........",
        ".........KQTSK.KSTQK........",
        ".........KTSK...KSTQK.......",
        "........KQTSK...KSTK........",
        ".......KMMUK....KUMMK.......",
        "......KMMUMK....KUMMMK......",
        "......KKKKK......KKKKK......",
        "............................",
        "............................",
        "............................",
        "............................",
        "............................"
    )

    private val jumpRiseFrame = listOf(
        "..........KKKKKK......KKKK..",
        "........KKLLYYYYKK...KQQSSK.",
        ".......KLLYYRRRRYYKK.KSTKTK.",
        "......KLYRRRPPPPRRYYK.KTSTK.",
        "......KRRDDYYYYYYOOKKKSQSTK.",
        ".....KLYYOOHHHHKKKK..KSSTK..",
        "....KLOOOHHSHWKSHK...KSTK...",
        "....KKKKKHHSSSTSHK..KRRPK...",
        ".........KKSSTSSK..KRRDPK...",
        "..........KKSTKK..KRRDPK....",
        ".......KKKKSTTSKKKRRDPK.....",
        "......KRPRKSQTSRRPDDKK......",
        ".....KRPRWKQCTQRRDPK........",
        "....KDRPKWKSCCSRDRPK........",
        "...KDRPK.WKTCTTRDRK.........",
        "...KTTK..WKQSSQRRDK.........",
        "..KQQSK..KDRYYYRDRK.........",
        "..KSTK..KYYRLYYYRDK.........",
        "...KK..KYYOKKBBNBYOK........",
        ".......KYYK.KBAABBBBK.......",
        "........KK.KBAAABBBBBK......",
        "..........KBAABKKBBBNBK.....",
        ".........KBBBK...KWWWK......",
        "........KWWWK....KSTQK......",
        "........KQTSK....KSTQK......",
        ".......KQTSK......KUMMK.....",
        ".......KTSK.......KUMMMK....",
        "......KMMUK........KKKKK....",
        ".....KMMUMK.................",
        ".....KKKKK..................",
        "............................",
        "............................",
        "............................",
        "............................"
    )

    private val jumpFallFrame = listOf(
        "..........KKKKKK............",
        "........KKLLYYYYKK..........",
        ".......KLLYYRRRRYYKKKKK.....",
        "......KLYRRRPPPPRRYYYYOK....",
        "......KRRDDYYYYYYOOKKKK.....",
        ".....KLYYOOHHHHKKKK.........",
        "....KLOOOHHSHWKSHK..........",
        "....KKKKKHHSSSTSHK..........",
        "..KKKK...KKSSTSSK.....KKKK..",
        ".KQQSSK...KKSTKK.....KQQSSK.",
        ".KSTKTKKKKKSTTSKKKK..KSTKTK.",
        "..KSTKRPRRKSQTSRRPPKKKTSTK..",
        "...KKDRPRWKQCTQRRDPPRKSTK...",
        "....KDRPKWKSCCSRDRPDDKK.....",
        "....KDRK.WKTCTTRDRKK........",
        "...KDRK..KDRYYYRDRK.........",
        "....KK..KYYRLYYYRDKK........",
        ".......KYYOKKBBNBYOK........",
        "........KK.KBAABBBYOK.......",
        "..........KBAAABBBBYOK......",
        ".........KBAABKKBBBNBYK.....",
        "........KBBBK....KBBBNK.....",
        ".......KWWWK......KWWWK.....",
        ".......KQTSK......KSTQK.....",
        "......KQTSK........KSTQK....",
        "......KTSK..........KSTK....",
        ".....KMMUK..........KUMMK...",
        "....KMMUMK..........KUMMMK..",
        "....KKKKK............KKKKK..",
        "............................",
        "............................",
        "............................",
        "............................",
        "............................"
    )

    private val landSquashFrame = listOf(
        "............................",
        "............................",
        "............................",
        "..........KKKKKK............",
        "........KKLLYYYYKK..........",
        ".......KLLYYRRRRYYKKKKK.....",
        "......KLYRRRPPPPRRYYYYOK....",
        "......KRRDDYYYYYYOOKKKK.....",
        ".....KLYYOOHHHHKKKK.........",
        "....KLOOOHHSHWKSHK..........",
        "....KKKKKHHSSSTSHK..........",
        ".........KKSSTSSK...........",
        ".......KKKKSTTSKKKK.........",
        "......KRPRKSQTSRRPPK........",
        ".....KRPRWKQCTQRRDPPK.......",
        "....KDRPRWKSCCSRDRPPKK......",
        "...KDRPK.WKTCTTRDRPKSQK.....",
        "...KTTK..KDRYYYRDRK.KSTK....",
        "..KQQSK..KDRLYYYRDK.KQQK....",
        "..KSTK....KKBBNBYOK..KK.....",
        "...KK....KBAABBBBYOK........",
        "........KBAAABBBBNBYK.......",
        ".......KBAABKKKBBBNBK.......",
        "......KBBBK.....KBBBNK......",
        ".....KWWWK.......KWWWK......",
        ".....KQTSK.......KSTQK......",
        "....KQTSK.........KSTQK.....",
        "....KQSTK.........KSTQK.....",
        "...KQTSK...........KSTQK....",
        "...KTSK.............KSTK....",
        ".KKMMUK.............KUMMKK..",
        "KMMUMUK.............KUMUMMK.",
        "KKKKKKK.............KKKKKKK.",
        "............................"
    )

    private val hurtFrame = listOf(
        "............................",
        "......KKKKKK................",
        "....KKLLYYYYKK..............",
        "...KLLYYRRRRYYKKKK..........",
        "..KLYRRRPPPPRRYYYOK.........",
        "..KRRDDYYYYYYOOKKK..........",
        "...KKYYOOHHHHKKKK...........",
        "......KKHHSTWKSHK...........",
        "........HHSSWKSHK...........",
        "........KKSKKSSK............",
        ".........KKSTKK.....KKKK....",
        "......KKKKSTTSKKK..KQQSSK...",
        "....KKRPRKSQTSRRPK.KSTKTK...",
        "...KRPRRWKQCTQRRDPKKTSTK....",
        "..KDRPRKWKSCCSRDRPPRSTK.....",
        "..KTTKK.WKTCTTRDRDDPKK......",
        ".KQQSK..KDRYYYRDRKK.........",
        "..KKK...KDRLYYYRDK..........",
        ".........KKBBNBYOK..........",
        "........KBAABBBBYOK.........",
        ".......KBAAABBBBNBYK........",
        ".......KBAABKKBBBNBK........",
        "......KBBBK....KBBBNK.......",
        ".....KWWWK......KWWWK.......",
        ".....KQTSK......KSTQK.......",
        "....KQTSK........KSTQK......",
        "....KTSK..........KSTK......",
        "...KMMUK..........KUMMK.....",
        "..KMMUMK..........KUMMMK....",
        "..KKKKK............KKKKK....",
        "............................",
        "............................",
        "............................",
        "............................"
    )

    // Pistol firing stance: right crimson sleeve extends straight forward to x=18, y=17
    private val pistolPoseFrame = listOf(
        "............KKKKKK..........",
        "..........KKLLYYYYKK........",
        ".........KLLYYRRRRYYKKKKK...",
        "........KLYRRRPPPPRRYYYYOK..",
        "........KRRDDYYYYYYOOKKKK...",
        ".......KLYYOOHHHHKKKK.......",
        "......KLOOOHHSHWKSHK........",
        "......KKKKKHHSSSTSHK........",
        "...........KKSSTSSK.........",
        "............KKSTKK..........",
        "........KKKKKSTTSKKKK.......",
        ".......KRPRRKSQTSRRPPK......",
        "......KRPRRWKQCTQRRDPPK.....",
        ".....KDRPRKWKSCCSRDRPPKK....",
        "....KDRPK..WKTCTTRDRPPPKK...",
        "...KDRPK...WKQSSQRRDDPPPK...",
        "...KTTK...KDRYYYRDRKKKKK....",
        "..KQQSK..KYYRLYYYRDK........",
        "..KSTSK.KYYOKKBBNBYOK.......",
        "...KKK...KK.KBAABBBYOK......",
        "...........KBAAABBBBYOK.....",
        "..........KBAABBBBNBYOK.....",
        ".........KBAABKKBBBNBYK.....",
        "........KBBBK....KBBBNK.....",
        ".......KWWWK......KWWWK.....",
        "......KQTSK........KSTQK....",
        "......KQSTK........KSTQK....",
        ".....KQTSK..........KSTQK...",
        ".....KTSK............KSTK...",
        "....KMMUK............KUMMK..",
        "...KMMUMUK..........KUMUMMK.",
        "...KKKKKKK..........KKKKKKK.",
        "............................",
        "............................"
    )

    // Gatling rapid alternating barrage poses
    private val gatlingFrame0 = listOf(
        "...........KKKKKK...........",
        ".........KKLLYYYYKK.........",
        "........KLLYYRRRRYYKKKKK....",
        ".......KLYRRRPPPPRRYYYYOK...",
        ".......KRRDDYYYYYYOOKKKK....",
        "......KLYYOOHHHHKKKK........",
        ".....KLOOOHHSHWKSHK.........",
        ".....KKKKKHHSSWKSHK.........",
        "..........KKSKKSSK..........",
        "...........KKSTKK...........",
        ".......KKKKKSTTSKKKK........",
        "......KRPRRKSQTSRRPPKK......",
        ".....KRPRRWKQCTQRRDPPPK.....",
        "....KDRPRKWKSCCSRDRPPPK.....",
        "...KDRPK..WKTCTTRDRDDPK.....",
        "...KTTK...KDRYYYRDRKKK......",
        "..KQQSK..KYYRLYYYRDK........",
        "...KKK..KYYOKKBBNBYOK.......",
        ".........KK.KBAABBBYOK......",
        "...........KBAAABBBBYOK.....",
        "..........KBAABBBBNBYOK.....",
        ".........KBAABKKBBBNBYK.....",
        "........KBBBK....KBBBNK.....",
        ".......KWWWK......KWWWK.....",
        "......KQTSK........KSTQK....",
        "......KQSTK........KSTQK....",
        ".....KQTSK..........KSTQK...",
        ".....KTSK............KSTK...",
        "....KMMUK............KUMMK..",
        "...KMMUMUK..........KUMUMMK.",
        "...KKKKKKK..........KKKKKKK.",
        "............................",
        "............................",
        "............................"
    )

    private val gatlingFrame1 = listOf(
        "............................",
        "...........KKKKKK...........",
        ".........KKLLYYYYKK.........",
        "........KLLYYRRRRYYKKKKK....",
        ".......KLYRRRPPPPRRYYYYOK...",
        ".......KRRDDYYYYYYOOKKKK....",
        "......KLYYOOHHHHKKKK........",
        ".....KLOOOHHSHWKSHK.........",
        ".....KKKKKHHSSWKSHK.........",
        "..........KKSKKSSK..........",
        "...........KKSTKK...........",
        ".......KKKKKSTTSKKKKK.......",
        "......KRPRRKSQTSRRPPPK......",
        ".....KRPRRWKQCTQRRDPPPK.....",
        "....KDRPRKWKSCCSRDRDDPK.....",
        "....KDRK..WKTCTTRDRKKK......",
        "...KQQSK..KDRYYYRDRK........",
        "....KKK..KYYRLYYYRDK........",
        "........KYYOKKBBNBYOK.......",
        ".........KK.KBAABBBYOK......",
        "...........KBAAABBBBYOK.....",
        ".........KBAABBBBNBYOK......",
        "........KBAABKKKBBBNBYK.....",
        ".......KBBBK.....KBBBNK.....",
        "......KWWWK.......KWWWK.....",
        "......KQTSK.......KSTQK.....",
        ".....KQSTK.........KSTQK....",
        ".....KQTSK.........KSTQK....",
        "....KTSK............KSTK....",
        "...KMMUK............KUMMK...",
        "..KMMUMUK..........KUMUMMK..",
        "..KKKKKKK..........KKKKKKK..",
        "............................",
        "............................"
    )

    // Battle Axe windup: right leg stretched straight upward at cols 16..19 from row 4
    private val axeWindupFrame = listOf(
        "................KSTK........",
        "................KSTK........",
        "................KSTK........",
        "..........KKKKKKKSTK........",
        "........KKLLYYYYKSTK........",
        ".......KLLYYRRRRKSTK........",
        "......KLYRRRPPPPRSTK........",
        "......KRRDDYYYYYKSTK........",
        ".....KLYYOOHHHHKKSTK........",
        "....KLOOOHHSHWKSHSTK........",
        "....KKKKKHHSSSTSHSTK........",
        ".........KKSSTSSKSTK........",
        ".......KKKKSTTSKKSTK........",
        "......KRPRKSQTSRRSTK........",
        ".....KRPRWKQCTQRRSTK........",
        "....KDRPRWKSCCSRDRTK........",
        "...KDRPK.WKTCTTRDWWK........",
        "...KTTK..KDRYYYRDBBK........",
        "..KQQSK..KDRLYYYRBBK........",
        "..KSTK....KKBBNBBBNK........",
        "...KK....KBAABBBBBK.........",
        "........KBAAABBBBK..........",
        "........KBAABBBKK...........",
        "........KBBBK...............",
        ".......KWWWK................",
        ".......KQTSK................",
        ".......KQSTK................",
        ".......KQTSK................",
        ".......KTSK.................",
        "......KMMUK.................",
        ".....KMMUMUK................",
        ".....KKKKKKK................",
        "............................",
        "............................"
    )

    // Battle Axe slam: right leg launching forward from hip (x=19, y=24)
    private val axeSlamFrame = listOf(
        "............KKKKKK..........",
        "..........KKLLYYYYKK........",
        ".........KLLYYRRRRYYKKKKK...",
        "........KLYRRRPPPPRRYYYYOK..",
        "........KRRDDYYYYYYOOKKKK...",
        ".......KLYYOOHHHHKKKK.......",
        "......KLOOOHHSHWKSHK........",
        "......KKKKKHHSSWKSHK........",
        "...........KKSKKSSK.........",
        "............KKSTKK..........",
        ".......KKKKKSTTSKKKK........",
        "......KRPRRKSQTSRRPPK.......",
        ".....KRPRRWKQCTQRRDPK.......",
        "....KDRPRKWKSCCSRDRPK.......",
        "...KDRPK..WKTCTTRDRK........",
        "...KTTK...KDRYYYRDRK........",
        "..KQQSK..KYYRLYYYRDK........",
        "...KKK..KYYOKKBBNBYOK.......",
        ".........KK.KBAABBBYOK......",
        "...........KBAAABBBBYOK.....",
        "..........KBAABBBBBBBWWK....",
        ".........KBBBK.KKBBBBWWK....",
        "........KWWWK....KKKKKK.....",
        "........KQTSK...............",
        ".......KQTSK................",
        ".......KQSTK................",
        "......KQTSK.................",
        "......KTSK..................",
        ".....KMMUK..................",
        "....KMMUMUK.................",
        "....KKKKKKK.................",
        "............................",
        "............................",
        "............................"
    )

    // 14x12 Rubber Fist (facing RIGHT)
    private val rubberFistRows = listOf(
        "....KKKKKK....",
        "..KKQQQQSSKK..",
        ".KQQQQSSSSSTK.",
        "KQQQSSKSSKSTTK",
        "KQQSSSKSSKSTTK",
        "KQQSSSSSSKSTTK",
        "KSSSTTSSSKSTTK",
        "KSSSTTTSSKSTTK",
        ".KTTTTTTTKTTK.",
        "..KKTTTTTTTKK.",
        "....KKKKKKK...",
        ".............."
    )

    // 14x12 High-Speed Gatling Ghost Fist
    private val gatlingGhostFistRows = listOf(
        "....KKKKKK....",
        ".EEKKQQSSKK...",
        "EEEKQQSSSSSTK.",
        "EEKKQQKSSKSTTK",
        "EEEKQQKSSKSTTK",
        "EEKKSSSSSKSTTK",
        ".EEKTTTSSKSTTK",
        "..KTTTTTTKTTK.",
        "...KKTTTTTTKK.",
        ".....KKKKKK...",
        "..............",
        ".............."
    )

    // 14x14 Sky-High Battle Axe Foot (pointing up)
    private val axeFootUpRows = listOf(
        "...KKKKKKKK...",
        "..KMMUUUUUMK..",
        ".KMMUUUUUUUMK.",
        ".KMMUMMMMUMMK.",
        ".KQQSSMMSSQQK.",
        ".KQQSSSSSSTTK.",
        "..KQQSSSSSTK..",
        "..KTTSSSSSTK..",
        "...KTTSSSTK...",
        "....KTTSTK....",
        "....KQQSTK....",
        "....KQQSTK....",
        "....KTTSTK....",
        "....KKKKKK...."
    )

    // 16x12 Battle Axe Slamming Sandal Foot
    private val axeFootSlamRows = listOf(
        "....KKKKKK......",
        "...KQQSSSTK.....",
        "..KQQSSSSSTK....",
        ".KQQSSSSSSSTKK..",
        ".KTTSSSSSSSTTTK.",
        ".KMMMMUUUUUMMMMK",
        "KMMUUUUUUUUUUUMK",
        "KMMUUUUUUUUUUUMK",
        ".KMMMMMMMMMMMMK.",
        "..KKKKKKKKKKKK..",
        "................",
        "................"
    )

    // 18x18 Post-Timeskip Luffy HUD Portrait
    private val hudPortraitRows = listOf(
        ".....KKKKKK.......",
        "...KKLLYYYYKK.....",
        "..KLLYYRRRRYYKKKK.",
        ".KLYRRRPPPPRRYYYOK",
        ".KRRDDYYYYYYOOKKK.",
        "KLYYOOHHHHKKKK....",
        "KLOOOHHSHWKSHK....",
        ".KKKKHHSSSTSHK....",
        ".....KKSKCSTSK....",
        "......KSSKKSSK....",
        ".....KKKSTTSKKK...",
        "...KKRPRSQTSRPPKK.",
        "..KRPRWKQCTQRRDPK.",
        "..KRPRWKSCCSRDRPK.",
        "..KDRPWKTCTTRDRPK.",
        "..KDRPKKQSSQRDRPK.",
        "...KKKKKKKKKKKKK..",
        ".................."
    )

    // 16x16 Skill Icons for Base Luffy
    private val iconPistolRows = listOf(
        "................",
        ".......KKKKKK...",
        ".....KKQQQQSSK..",
        "....KQQQQSSSTTK.",
        ".KKKQQSSKSSKSTTK",
        "KQQQSSSKKSSKSTTK",
        "KTTTTTTSSSSKSTTK",
        ".KKKKTTSSSSKTTK.",
        "....KTTTTTTKTTK.",
        "..EE.KKKKKKKKK..",
        "EEEE............",
        "..EE............",
        "................",
        "................",
        "................",
        "................"
    )

    private val iconGatlingRows = listOf(
        "................",
        ".....KKKKKK.....",
        "...KKQQSSSTK....",
        "..EEQQKSTTK.....",
        "...KKKKKKKKKK...",
        "........KQQSSK..",
        "..EEEE.KQQSSTTK.",
        "...KKKKKQKSSTTK.",
        ".KKQQSSKKTTTTK..",
        "EEQQSSTTKKKKK...",
        "..KKKKKKK.......",
        ".....EEEEE......",
        ".KKKKKKK........",
        "KQQSSTTTK.......",
        ".KKKKKKK........",
        "................"
    )

    private val iconAxeRows = listOf(
        ".....KKKKK......",
        "....KQQSSTK.....",
        "....KQQSSTK.....",
        "....KTTSSSK.....",
        "...KTTSSSSSK....",
        "..KQQSSSSSSSK...",
        "..KMMUUUUUUMMK..",
        ".KMMUUUUUUUUMMK.",
        "..KKKKKKKKKKKK..",
        ".EE..GGGGGG..EE.",
        "EE.GGWWWWWWGG.EE",
        ".GGWW......WWGG.",
        "................",
        "................",
        "................",
        "................"
    )

    val idleFrames: List<DirectionalSprite> = listOf(
        PixelArtBuilder.buildPair(alignBottom(idleFrame0), luffyPalette),
        PixelArtBuilder.buildPair(alignBottom(idleFrame1), luffyPalette),
        PixelArtBuilder.buildPair(alignBottom(idleFrame2), luffyPalette),
        PixelArtBuilder.buildPair(alignBottom(idleFrame1), luffyPalette)
    )
    val runFrames: List<DirectionalSprite> = listOf(
        PixelArtBuilder.buildPair(alignBottom(runFrame0), luffyPalette),
        PixelArtBuilder.buildPair(alignBottom(runFrame1), luffyPalette),
        PixelArtBuilder.buildPair(alignBottom(runFrame2), luffyPalette),
        PixelArtBuilder.buildPair(alignBottom(runFrame3), luffyPalette)
    )
    val jumpRise: DirectionalSprite = PixelArtBuilder.buildPair(alignBottom(jumpRiseFrame, 30), luffyPalette)
    val jumpFall: DirectionalSprite = PixelArtBuilder.buildPair(alignBottom(jumpFallFrame, 31), luffyPalette)
    val landSquash: DirectionalSprite = PixelArtBuilder.buildPair(alignBottom(landSquashFrame), luffyPalette)
    val hurtPose: DirectionalSprite = PixelArtBuilder.buildPair(alignBottom(hurtFrame), luffyPalette)
    val pistolPose: DirectionalSprite = PixelArtBuilder.buildPair(alignBottom(pistolPoseFrame), luffyPalette)
    val gatlingFrames: List<DirectionalSprite> = listOf(
        PixelArtBuilder.buildPair(alignBottom(gatlingFrame0), luffyPalette),
        PixelArtBuilder.buildPair(alignBottom(gatlingFrame1), luffyPalette)
    )
    val axeWindup: DirectionalSprite = PixelArtBuilder.buildPair(alignBottom(axeWindupFrame), luffyPalette)
    val axeSlam: DirectionalSprite = PixelArtBuilder.buildPair(alignBottom(axeSlamFrame), luffyPalette)

    val rubberFist: DirectionalSprite = PixelArtBuilder.buildPair(rubberFistRows, luffyPalette)
    val gatlingGhostFist: DirectionalSprite = PixelArtBuilder.buildPair(gatlingGhostFistRows, luffyPalette)
    val axeFootUp: DirectionalSprite = PixelArtBuilder.buildPair(axeFootUpRows, luffyPalette)
    val axeFootSlam: DirectionalSprite = PixelArtBuilder.buildPair(axeFootSlamRows, luffyPalette)

    val hudPortrait: ImageBitmap = PixelArtBuilder.build(hudPortraitRows, luffyPalette)
    val iconPistol: ImageBitmap = PixelArtBuilder.build(iconPistolRows, luffyPalette)
    val iconGatling: ImageBitmap = PixelArtBuilder.build(iconGatlingRows, luffyPalette)
    val iconAxe: ImageBitmap = PixelArtBuilder.build(iconAxeRows, luffyPalette)

    // =========================================================================
    // 48x38 GEAR 4: BOUNDMAN SPRITES (Obsidian-Crimson Busoshoku Haki + Steam)
    // =========================================================================

    private val boundmanPalette: Map<Char, Int> = mapOf(
        '.' to 0x00000000, // Transparent
        'K' to 0xFF140A12.toInt(), // Crisp dark obsidian ink outline
        'H' to 0xFF1B1522.toInt(), // Spiky jet-black hair
        'Q' to 0xFFFFE1BD.toInt(), // Warm puffed-torso highlight
        'S' to 0xFFF2BE96.toInt(), // Warm muscular skin midtone
        'T' to 0xFFC98556.toInt(), // Deep bronze torso/ab shading
        'U' to 0xFF8F4D2A.toInt(), // Deep muscular crease shadow
        'X' to 0xFFE85A6B.toInt(), // Iconic rosy-red X-shaped chest scar
        'F' to 0xFFFFA3AD.toInt(), // Bright pink chest scar highlight
        'V' to 0xFF240C18.toInt(), // Obsidian-plum Busoshoku Haki deep shadow
        'Z' to 0xFF5E1028.toInt(), // Rich wine-crimson Haki midtone
        'C' to 0xFFB81D46.toInt(), // Metallic crimson Haki sheen
        'J' to 0xFFFF4D7D.toInt(), // Blazing magenta-crimson Haki knuckle highlight
        'R' to 0xFFC91C36.toInt(), // Fluttering red cardigan flap
        'P' to 0xFFFF4764.toInt(), // Red cardigan highlight
        'D' to 0xFF6E0B1D.toInt(), // Deep red cardigan shadow
        'W' to 0xFFFFFFFF.toInt(), // Pure white steam core, eyes, grin teeth, cuffs
        'I' to 0xFFE6F3FF.toInt(), // Soft bright steam vapor highlight
        'M' to 0xFFB8CFE8.toInt(), // Cool mist-blue steam vapor shadow
        'L' to 0xFFFFF2A6.toInt(), // Golden waist sash bright highlight
        'Y' to 0xFFE8C451.toInt(), // Golden waist sash & fluttering ribbon tail
        'O' to 0xFF9C721C.toInt(), // Sash shadow
        'A' to 0xFF548AE8.toInt(), // Compact blue shorts highlight
        'B' to 0xFF2952A8.toInt(), // Compact blue denim shorts
        'N' to 0xFF142B63.toInt(), // Compact blue shorts deep shadow
        'G' to 0xFFFFD84D.toInt()  // Impact shockwave gold
    )

    private fun alignBottom38(rows: List<String>, targetBottomRow: Int = 36): List<String> {
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

    // Boundman Heroic Guard Stance (48x38) with curling Hagoromo shoulder steam
    private val boundmanIdle0 = listOf(
        "................................................",
        "....KKKKKK..................II..................",
        "...KJCZZVVK..KKKKKK.....KK.KKWM.................",
        "..KJJCVVVVKKKJCZZVVKK..KHHKHHKK.................",
        "..KCVVVZZZZZZCVVVZZVKKKHHHHHHHKK.II.............",
        "..KVVZCCJJCCZVVZCCZZVKKHHHHHHHKKWWI.............",
        "...KVZCCVVCCZZZCCVVCZKKHSSKKSHKKMM..............",
        "....KVZVVVVZZVZCCJJCZKKHWWKSSHK..KKKKKKKK.......",
        ".....KVVZZCVZVZVVVVZVK.KSTKKSTK.KJCZZZZVVKK.....",
        "......KVVZCCJCVZZVVZK..KWWWWWKKKJJCVVZZZZZVKK...",
        ".......KVVZCCVVZVVZK..KZKSTSKZKKCVVZZCCJJCCZVK..",
        "........KVVZCCVZZK.WWKZZCKSSKZZKVVZZCCVVVVCCZVK.",
        ".........KKVVZZZK.IWKKZZCZQSSQZZKVVZCCJJVVJJCCZK",
        "...........KKKK...MMKZZCQQXFFXQZZKVVZCCVVVVCCZZK",
        "........KKKK......WKZZCQQTXFFXTQZZKVZZCCJJCCZVCK",
        ".......KRPPK.....MKZZCZQTTXFFXTTQZKVVZZVVVVZZVCK",
        "......KRPRDK....KVVZZQTTXXFFXXTTQKKVVZZZZZZVVJCK",
        "......KRDDK....KVVVZQQTTXFFFFXTTQQKKKVVZZVVVJJCK",
        ".......KKK.KKKKVVVZZQTTXXFXXFXXTTQQKKDRRPPKVVCCK",
        "..........KVVVVVVVZZQUTXTTUUTTXTUQQK.KDRRPK.KKK.",
        "...........KVVVZZZQSSUTTTUSSUTTTUSSQK.KDRPK.....",
        "............KKVZZQSSQUTTUUSSUUTTUQSSQK.KKK......",
        "..............KZZQSTQUTTUUSSUUTTUQTSQK..........",
        "...............KKQSTTUTTUUSSUUTTUTTSQK..........",
        ".................KTTTUTTUUSSUUTTUTTTK..KKKKK....",
        "..................KKKLLLLYYYYYYLLKKK.KKLLYYYK...",
        "...................KLOOYYYYYYYYYOOLKKLLYYYYOOK..",
        "...................KKKKKBBBBBBBBKKKKKKOOYYYYK...",
        "...................KBAABBBBNBBBBBAABK..KKKKK....",
        "..................KBAAABBBKKKBBBBAAABK..........",
        "..................KBBBNBK.....KBNBBBBK..........",
        ".................KWWWWKK.......KKWWWWK..........",
        ".................KZVVK...........KVVZK..........",
        "..................KCVK...........KVCK...........",
        "..................KZVVK.........KVVZK...........",
        "...................KCVK.........KVCK............",
        "...................KKVK.........KVKK............",
        "................................................"
    )

    // Boundman Elastic Squash Compression (2px lower bounce bend, flaring steam puffs)
    private val boundmanIdle1 = listOf(
        "................................................",
        "................................................",
        "..................WW..............II............",
        ".....KKKKKK......IWM..............WM............",
        "....KJCZZVVK..KKKKKK.....KK.KK..................",
        "...KJJCVVVVKKKJCZZVVKK..KHHKHHKK.WW.............",
        "...KCVVVZZZZZZCVVVZZVKKKHHHHHHHKKIM.............",
        "...KVVZCCJJCCZVVZCCZZVKKHHHHHHHK................",
        "....KVZCCVVCCZZZCCVVCZKKHSSKKSHKK...............",
        ".....KVZVVVVZZVZCCJJCZKKHWWKSSHK..KKKKKKKK......",
        "......KVVZZCVZVZVVVVZVK.KSTKKSTK.KJCZZZZVVKK....",
        ".......KVVZCCJCVZZVVZK..KWWWWWKKKJJCVVZZZZZVKK..",
        "........KVVZCCVVZVVZK..KZKSTSKZKKCVVZZCCJJCCZVK.",
        ".........KVVZCCVZZK.IWKZZCKSSKZZKVVZZCCVVVVCCZVK",
        "..........KKVVZZZK.WMWZZCZQSSQZZKVVZCCJJVVJJCCZK",
        ".........KKKKKKK...MMKZZCQQXFFXQZZKVVZCCVVVVCCZK",
        "........KRPPK......KZZCQQTXFFXTQZZKVZZCCJJCCZVCK",
        ".......KRPRDK.....KVVZZQTTXFFXTTQZKVVZZVVVVZZVCK",
        ".......KRDDK.....KVVVZQQTTXXFFXXTQQKKVVZZZZZVJCK",
        "........KKK.KKKKKVVVZZQTTXFFFFXTTQQKKKVVZZVVJJCK",
        "...........KVVVVVVVVZZQTTXXFXXFXXTQQKKDRRPPKVCCK",
        "............KVVVZZZQSSUTXTTUUTTXTUQQK.KDRRPKKKK.",
        ".............KKVZZQSSQUTTUUSSUUTTUQSSQKDRPK.....",
        "...............KZZQSTQUTTUUSSUUTTUQTSQKKKK......",
        "................KKQSTTUTTUUSSUUTTUTTSQK.........",
        "..................KTTTUTTUUSSUUTTUTTTK..KKKKK...",
        "..................KKKLLLLYYYYYYLLKKK..KKLLYYYK..",
        "..................KLOOYYYYYYYYYOOLKKKLLYYYYOOK..",
        "..................KKKKKBBBBBBBBKKKKKKKOOYYYYK...",
        "..................KBAABBBBNBBBBBAABK...KKKKK....",
        ".................KBAAABBBKKKBBBBAAABK...........",
        "................KBBBNBK.......KBNBBBBK..........",
        "...............KWWWWKK.........KKWWWWK..........",
        "................KZVVKK.........KKVVZK...........",
        ".................KCVZK.........KZVCCK...........",
        "...............WW.KCVK.........KVCK.WW..........",
        "..............IM..KKVK.........KVKK..MI.........",
        "................................................"
    )

    // Boundman Elastic Spring Rebound Peak (lifted high on tiptoes, fists & steam rising)
    private val boundmanIdle2 = listOf(
        "....KKKKKK...................WW.................",
        "...KJCZZVVK..KKKKKK.....KK.KKIM.................",
        "..KJJCVVVVKKKJCZZVVKK..KHHKHHKK.................",
        "..KCVVVZZZZZZCVVVZZVKKKHHHHHHHKK...WW...........",
        "..KVVZCCJJCCZVVZCCZZVKKHHHHHHHK...IMM...........",
        "...KVZCCVVCCZZZCCVVCZKKHSSKKSHKK...KKKKKKKK.....",
        "....KVZVVVVZZVZCCJJCZKKHWWKSSHK..KKJCZZZZVVKK...",
        ".....KVVZZCVZVZVVVVZVK.KSTKKSTK.KJJCVVZZZZZVKK..",
        "......KVVZCCJCVZZVVZK..KWWWWWKK.KCVVZZCCJJCCZVK.",
        ".......KVVZCCVVZVVZK..KZKSTSKZKKVVZZCCVVVVCCZVK.",
        "........KVVZCCVZZK.WWKZZCKSSKZZKVVZCCJJVVJJCCZVK",
        ".........KKVVZZZK.IMMZZCZQSSQZZKVVZCCVVVVCCZZVK.",
        "...........KKKK....KZZCQQXFFXQZZKVZZCCJJCCZVVCK.",
        "........KKKK......KZZCQQTXFFXTQZZKVVZZVVVVZZVVCK",
        ".......KRPPK.....KVVZZQTTXFFXTTQZKVVZZZZZZVVJCK.",
        "......KRPRDK....KVVVZQQTTXXFFXXTTQKKVVZZVVVJJCK.",
        "......KRDDK.....KVVVZZQTTXFFFFXTTQQKDRRPPKVVCCK.",
        ".......KKK.KKKKKVVVVZZQTTXXFXXFXXTQQKKDRRPKKKK..",
        "..........KVVVVVVVZZQSSUTXTTUUTTXTUQQK.KDRPK....",
        "...........KVVVZZZQSSQUTTUUSSUUTTUQSSQK.KKK.....",
        "............KKVZZQSTQUTTUUSSUUTTUQTSQK..........",
        "..............KZZQSTTUTTUUSSUUTTUTTSQK..........",
        "...............KKQTTTUTTUUSSUUTTUTTTK...KKKKK...",
        ".................KKKLLLLYYYYYYLLKKK...KKLLYYYK..",
        "..................KLOOYYYYYYYYYOOLKKKLLYYYYOOK..",
        "..................KKKKKBBBBBBBBKKKKKKKOOYYYYK...",
        "..................KBAABBBBNBBBBBAABK...KKKKK....",
        "..................KBAAABBBKKKBBBBAAABK..........",
        "..................KBBBNBK.....KBNBBBBK..........",
        "..................KWWWWK.......KWWWWK...........",
        "..................KZVVK.........KVVZK...........",
        "...................KCVK.........KVCK............",
        "...................KZVVK.......KVVZK............",
        "....................KCVK.......KVCK.............",
        "....................KZVVK.....KVVZK.............",
        ".....................KCVK.....KVCK..............",
        "..................I..KKVK.....KVKK..I...........",
        "................................................"
    )

    // 4-Frame Bouncy Elastic Movement Cycle
    // Run 0: Deep elastic compression coil before spring-off (steam flaring back)
    private val boundmanRun0 = listOf(
        "................................................",
        "................................................",
        "................................................",
        "............WW...........KK.KK..................",
        ".....KKKKKKIWM..........KHHKHHKK................",
        "....KJCZZVVKK..........KHHHHHHHKK...............",
        "...KJJCVVVVKKK.........KHHHHHHHK................",
        "...KCVVVZZZZZVKK.......KHSSKKSHKK...KKKKKKKK....",
        "...KVVZCCJJCCZVKK......KHWWKSSHK..KKJCZZZZVVKK..",
        "....KVZCCVVCCZZVKK.....KSTKKSTK..KJJCVVZZZZZVKK.",
        ".....KVZVVVVZZZVZK.....KWWWWWKK..KCVVZZCCJJCCZVK",
        "......KVVZZCVZZVZK....KZKSTSKZKK.KVVZZCCVVVVCCZK",
        ".......KVVZCCJCVZK.WWKZZCKSSKZZKKVVZCCJJVVJJCCZK",
        "........KVVZCCVVZKIMMZZCZQSSQZZKKVVZCCVVVVCCZZVK",
        ".........KKVVZZZKK.KZZCQQXFFXQZZKVVZZCCJJCCZVVCK",
        "........KKKKKKK...KZZCQQTXFFXTQZZKVVZZVVVVZZVVCK",
        ".......KRPPK.....KVVZZQTTXFFXTTQZKVVZZZZZZVVJCK.",
        "......KRPRDK....KVVVZQQTTXXFFXXTQQKKVVZZVVVJJCK.",
        "......KRDDK.....KVVVZZQTTXFFFFXTTQQKDRRPPKVVCCK.",
        ".......KKK.KKKKKVVVVZZQTTXXFXXFXXTQQKKDRRPKKKK..",
        "..........KVVVVVVVVZZQSSUTXTTUUTTXTUQQK.KDRPK...",
        "...........KVVVZZZQSSQUTTUUSSUUTTUQSSQK..KKK....",
        "......KKKKK.KKVZZQSTQUTTUUSSUUTTUQTSQK..........",
        "....KKLLYYYK..KZZQSTTUTTUUSSUUTTUTTSQK..........",
        "...KLLYYYYOOKKKKKQTTTUTTUUSSUUTTUTTTK...........",
        "...KOOYYYYKKKLLLLYYYYYYLLKKK....................",
        "....KKKKK..KLOOYYYYYYYYYOOLKK...................",
        "...........KKKKKBBBBBBBBKKKK....................",
        "...........KBAABBBBNBBBBBAABK...................",
        "..........KBAAABBBKKKBBBBAAABK..................",
        ".........KBBBNBK.......KBNBBBBK.................",
        "........KWWWWKK.........KKWWWWK.................",
        ".........KZVVKK.........KKVVZK..................",
        "..........KCVZK.........KZVCCK..................",
        "........WW.KCVK.........KVCK.WW.................",
        ".......IMM.KZVVK.......KVVZK.MMI................",
        "...........KKVK.........KVKK....................",
        "................................................"
    )

    // Run 1: Explosive upward-forward spring launch (legs extended, steam trailing)
    private val boundmanRun1 = listOf(
        "................................................",
        ".........................KK.KK..................",
        "............II..........KHHKHHKK................",
        "....KKKKKK..WM.........KHHHHHHHKK...............",
        "...KJCZZVVK............KHHHHHHHK................",
        "..KJJCVVVVKKK..........KHSSKKSHKK...KKKKKKKK....",
        "..KCVVVZZZZZVKK........KHWWKSSHK..KKJCZZZZVVKK..",
        "..KVVZCCJJCCZVKK.......KSTKKSTK..KJJCVVZZZZZVKK.",
        "...KVZCCVVCCZZVKK......KWWWWWKK..KCVVZZCCJJCCZVK",
        "....KVZVVVVZZZVZK.....KZKSTSKZKK.KVVZZCCVVVVCCZK",
        ".....KVVZZCVZZVZK..WWKZZCKSSKZZKKVVZCCJJVVJJCCZK",
        "......KVVZCCJCVZK.IMMZZCZQSSQZZKKVVZCCVVVVCCZZVK",
        ".......KVVZCCVVZK..KZZCQQXFFXQZZKVVZZCCJJCCZVVCK",
        "........KKVVZZZKK.KZZCQQTXFFXTQZZKVVZZVVVVZZVVCK",
        ".......KKKKKKK...KVVZZQTTXFFXTTQZKVVZZZZZZVVJCK.",
        "......KRPPK.....KVVVZQQTTXXFFXXTQQKKVVZZVVVJJCK.",
        ".....KRPRDK.....KVVVZZQTTXFFFFXTTQQKDRRPPKVVCCK.",
        ".....KRDDK......KVVVZZQTTXXFXXFXXTQQKKDRRPKKKK..",
        "......KKK..KKKKKVVVVZZQSSUTXTTUUTTXTUQQK.KDRPK..",
        "..........KVVVVVVVZZQSSQUTTUUSSUUTTUQSSQK.KKK...",
        "...........KVVVZZZQSTQUTTUUSSUUTTUQTSQK.........",
        ".......KKKKKKKVZZQSTTUTTUUSSUUTTUTTSQK..........",
        ".....KKLLYYYK..KZZQTTTUTTUUSSUUTTUTTTK..........",
        "....KLLYYYYOOKKKKKKLLLLYYYYYYLLKKK..............",
        "....KOOYYYYKKKLOOYYYYYYYYYYOOLKK................",
        ".....KKKKK...KKKKKBBBBBBBBKKKK..................",
        ".............KBAABBBBNBBBBBAABK.................",
        "............KBAAABBBKKKBBBBAAABK................",
        "...........KBBBNBK.......KBNBBBBK...............",
        "..........KWWWWKK.........KKWWWWK...............",
        ".........KZVVK...............KVVZK..............",
        "........KCVK..................KVCK..............",
        ".......KZVVK...................KVVZK............",
        "....WW.KCVK.....................KVCK............",
        "...IMMM.KVK.....................KVK.............",
        "........KKK.....................KKK.............",
        "................................................",
        "................................................"
    )

    // Run 2: High mid-air elastic bounding apex (tucked Haki knees, billowing steam plume)
    private val boundmanRun2 = listOf(
        ".........................KK.KK..................",
        "..........WW............KHHKHHKK................",
        ".....KKKKKKM...........KHHHHHHHKK...............",
        "....KJCZZVVK...........KHHHHHHHK................",
        "...KJJCVVVVKKK.........KHSSKKSHKK....KKKKKKKK...",
        "...KCVVVZZZZZVKK.......KHWWKSSHK...KKJCZZZZVVKK.",
        "...KVVZCCJJCCZVKK......KSTKKSTK...KJJCVVZZZZZVKK",
        "....KVZCCVVCCZZVKK.....KWWWWWKK...KCVVZZCCJJCCZK",
        ".....KVZVVVVZZZVZK....KZKSTSKZKK..KVVZZCCVVVVCCK",
        "......KVVZZCVZZVZK.WWKZZCKSSKZZKK.KVVZCCJJVVJJCK",
        ".......KVVZCCJCVZKIMMZZCZQSSQZZKK.KVVZCCVVVVCCZK",
        "........KVVZCCVVZK.KZZCQQXFFXQZZKKVZZCCJJCCZVVCK",
        ".........KKVVZZZKKKZZCQQTXFFXTQZZKVVZZVVVVZZVVCK",
        "........KKKKKKK..KVVZZQTTXFFXTTQZKVVZZZZZZVVJCK.",
        ".......KRPPK....KVVVZQQTTXXFFXXTQQKKVVZZVVVJJCK.",
        "......KRPRDK....KVVVZZQTTXFFFFXTTQQKDRRPPKVVCCK.",
        "......KRDDK.....KVVVZZQTTXXFXXFXXTQQKKDRRPKKKK..",
        ".......KKK..KKKKVVVVZZQSSUTXTTUUTTXTUQQKDRPK....",
        "...........KVVVVVVZZZQSSQUTTUUSSUUTTUQSSQKK.....",
        "......WW....KVVVZZZQSTQUTTUUSSUUTTUQTSQK........",
        ".....IMKKKKK.KKVZZQSTTUTTUUSSUUTTUTTSQK.........",
        "....KKLLYYYK...KZZQTTTUTTUUSSUUTTUTTTK..........",
        "...KLLYYYYOOKKKKKKKLLLLYYYYYYLLKKK..............",
        "...KOOYYYYKKKLOOYYYYYYYYYYOOLKK.................",
        "....KKKKK...KKKKKBBBBBBBBKKKK...................",
        "............KBAABBBBNBBBBBAABK..................",
        "...........KBAAABBBKKKBBBBAAABK.................",
        "...........KBBBNBK.....KBNBBBBK.................",
        "...........KWWWWK.......KWWWWK..................",
        "............KZVVK.......KVVZK...................",
        ".............KCVK.......KVCK....................",
        "..........WW..KZVVK...KVVZK.....................",
        ".........IMM...KCVK...KVCK......................",
        "...............KKVK...KVKK......................",
        "................................................",
        "................................................",
        "................................................",
        "................................................"
    )

    // Run 3: Elastic touchdown descent (Haki legs reaching down to rebound)
    private val boundmanRun3 = listOf(
        "................................................",
        ".........................KK.KK..................",
        "........................KHHKHHKK................",
        ".....KKKKKK.....WW.....KHHHHHHHKK...............",
        "....KJCZZVVK....IM.....KHHHHHHHK................",
        "...KJJCVVVVKKK.........KHSSKKSHKK...KKKKKKKK....",
        "...KCVVVZZZZZVKK.......KHWWKSSHK..KKJCZZZZVVKK..",
        "...KVVZCCJJCCZVKK......KSTKKSTK..KJJCVVZZZZZVKK.",
        "....KVZCCVVCCZZVKK.....KWWWWWKK..KCVVZZCCJJCCZVK",
        ".....KVZVVVVZZZVZK....KZKSTSKZKK.KVVZZCCVVVVCCZK",
        "......KVVZZCVZZVZK.IIKZZCKSSKZZKKVVZCCJJVVJJCCZK",
        ".......KVVZCCJCVZK.WMZZCZQSSQZZKKVVZCCVVVVCCZZVK",
        "........KVVZCCVVZK.KZZCQQXFFXQZZKVVZZCCJJCCZVVCK",
        ".........KKVVZZZKKKZZCQQTXFFXTQZZKVVZZVVVVZZVVCK",
        "........KKKKKKK..KVVZZQTTXFFXTTQZKVVZZZZZZVVJCK.",
        ".......KRPPK....KVVVZQQTTXXFFXXTQQKKVVZZVVVJJCK.",
        "......KRPRDK....KVVVZZQTTXFFFFXTTQQKDRRPPKVVCCK.",
        "......KRDDK.....KVVVZZQTTXXFXXFXXTQQKKDRRPKKKK..",
        ".......KKK.KKKKKVVVVZZQSSUTXTTUUTTXTUQQK.KDRPK..",
        "..........KVVVVVVVZZQSSQUTTUUSSUUTTUQSSQK.KKK...",
        "...........KVVVZZZQSTQUTTUUSSUUTTUQTSQK.........",
        ".......KKKKKKKVZZQSTTUTTUUSSUUTTUTTSQK..........",
        ".....KKLLYYYK..KZZQTTTUTTUUSSUUTTUTTTK..........",
        "....KLLYYYYOOKKKKKKLLLLYYYYYYLLKKK..............",
        "....KOOYYYYKKKLOOYYYYYYYYYYOOLKK................",
        ".....KKKKK...KKKKKBBBBBBBBKKKK..................",
        ".............KBAABBBBNBBBBBAABK.................",
        "............KBAAABBBKKKBBBBAAABK................",
        "............KBBBNBK.....KBNBBBBK................",
        "...........KWWWWKK.......KKWWWWK................",
        "...........KZVVK...........KVVZK................",
        "............KCVK...........KVCK.................",
        "............KZVVK.........KVVZK.................",
        ".............KCVK.........KVCK..................",
        ".............KZVVK.......KVVZK..................",
        "..............KCVK.......KVCK...................",
        "..............KKVK.......KVKK...................",
        "................................................"
    )

    // Boundman Elastic Jump Rise / Geppo Sky-Kick (downward white air-blast vapor jets)
    private val boundmanJumpRiseRows = listOf(
        "....KKKKKK...................WW.................",
        "...KJCZZVVK..KKKKKK.....KK.KKIM.................",
        "..KJJCVVVVKKKJCZZVVKK..KHHKHHKK.................",
        "..KCVVVZZZZZZCVVVZZVKKKHHHHHHHKK................",
        "..KVVZCCJJCCZVVZCCZZVKKHHHHHHHK.................",
        "...KVZCCVVCCZZZCCVVCZKKHSSKKSHKK..KKKKKKKK......",
        "....KVZVVVVZZVZCCJJCZKKHWWKSSHK..KJCZZZZVVKK....",
        ".....KVVZZCVZVZVVVVZVK.KSTKKSTK.KJJCVVZZZZZVKK..",
        "......KVVZCCJCVZZVVZK..KWWWWWKK.KCVVZZCCJJCCZVK.",
        ".......KVVZCCVVZVVZK..KZKSTSKZKKVVZZCCVVVVCCZVK.",
        "........KVVZCCVZZK.WWKZZCKSSKZZKVVZCCJJVVJJCCZVK",
        ".........KKVVZZZK.IMMZZCZQSSQZZKVVZCCVVVVCCZZVK.",
        "........KKKKKKK....KZZCQQXFFXQZZKVZZCCJJCCZVVCK.",
        ".......KRPPK......KZZCQQTXFFXTQZZKVVZZVVVVZZVVCK",
        "......KRPRDK.....KVVZZQTTXFFXTTQZKVVZZZZZZVVJCK.",
        "......KRDDK.....KVVVZQQTTXXFFXXTQQKKVVZZVVVJJCK.",
        ".......KKK.KKKKKVVVVZZQTTXFFFFXTTQQKDRRPPKVVCCK.",
        "..........KVVVVVVVZZQTTXXFXXFXXTQQKKDRRPK.KKK...",
        "...........KVVVZZZQSSUTXTTUUTTXTUQQK.KDRPK......",
        "............KKVZZQSSQUTTUUSSUUTTUQSSQK.KKK......",
        "..............KZZQSTQUTTUUSSUUTTUQTSQK...KKKKK..",
        "...............KKQSTTUTTUUSSUUTTUTTSQK.KKLLYYYK.",
        ".................KTTTUTTUUSSUUTTUTTTKKLLYYYYOOK.",
        "..................KKKLLLLYYYYYYLLKKKKKOOYYYYK...",
        "...................KLOOYYYYYYYYYOOLKK..KKKKK....",
        "...................KKKKKBBBBBBBBKKKK............",
        "..................KBAABBBBNBBBBBAABK............",
        ".................KBAAABBBKKKBBBBAAABK...........",
        ".................KWWWWKK.....KKWWWWK............",
        "..................KZVVK.......KVVZK.............",
        "...................KCVK.......KVCK..............",
        "...................KZVVK.....KVVZK..............",
        "..................IWWWWI.....IWWWWI.............",
        ".................MMWIIWM.....MWIIWMM............",
        "..................MM..MM.....MM..MM.............",
        "................................................",
        "................................................",
        "................................................"
    )

    // Boundman Elastic Jump Fall (arms wide, rising steam wisps overhead)
    private val boundmanJumpFallRows = listOf(
        "..............WW...............WW...............",
        "....KKKKKK...IMM..............IMM...............",
        "...KJCZZVVK..KKKKKK.....KK.KK...................",
        "..KJJCVVVVKKKJCZZVVKK..KHHKHHKK.................",
        "..KCVVVZZZZZZCVVVZZVKKKHHHHHHHKK...KKKKKKKK.....",
        "..KVVZCCJJCCZVVZCCZZVKKHHHHHHHK..KKJCZZZZVVKK...",
        "...KVZCCVVCCZZZCCVVCZKKHSSKKSHKKKJJCVVZZZZZVKK..",
        "....KVZVVVVZZVZCCJJCZKKHWWKSSHK.KCVVZZCCJJCCZVK.",
        ".....KVVZZCVZVZVVVVZVK.KSTKKSTKKVVZZCCVVVVCCZVK.",
        "......KVVZCCJCVZZVVZK..KWWWWWKKKVVZCCJJVVJJCCZVK",
        ".......KVVZCCVVZVVZK..KZKSTSKZKKVVZCCVVVVCCZZVK.",
        "........KVVZCCVZZK.WWKZZCKSSKZZKVZZCCJJCCZVVCK..",
        ".........KKVVZZZK.IMMZZCZQSSQZZKVVZZVVVVZZVVCK..",
        "........KKKKKKK....KZZCQQXFFXQZZKVVZZZZZZVVJCK..",
        ".......KRPPK......KZZCQQTXFFXTQZZKKVVZZVVVJJCK..",
        "......KRPRDK.....KVVZZQTTXFFXTTQZKDRRPPKVVCCK...",
        "......KRDDK.....KVVVZQQTTXXFFXXTQQKDRRPK.KKK....",
        ".......KKK.KKKKKVVVVZZQTTXFFFFXTTQQKKRPK........",
        "..........KVVVVVVVZZQTTXXFXXFXXTQQKK.KK.........",
        "...........KVVVZZZQSSUTXTTUUTTXTUQQK....KKKKK...",
        "............KKVZZQSSQUTTUUSSUUTTUQSSQK.KKLLYYYK.",
        "..............KZZQSTQUTTUUSSUUTTUQTSQKKLLYYYYOOK",
        "...............KKQSTTUTTUUSSUUTTUTTSQK.KOOYYYYK.",
        ".................KTTTUTTUUSSUUTTUTTTK...KKKKK...",
        "..................KKKLLLLYYYYYYLLKKK............",
        "...................KLOOYYYYYYYYYOOLKK...........",
        "...................KKKKKBBBBBBBBKKKK............",
        "..................KBAABBBBNBBBBBAABK............",
        ".................KBAAABBBKKKBBBBAAABK...........",
        ".................KBBBNBK.....KBNBBBBK...........",
        "................KWWWWKK.......KKWWWWK...........",
        "................KZVVK...........KVVZK...........",
        ".................KCVK...........KVCK............",
        ".................KZVVK.........KVVZK............",
        "..................KKVK.........KVKK.............",
        "................................................",
        "................................................",
        "................................................"
    )

    // Boundman Elastic Land Squash (deep rubber compression + side-flaring impact steam)
    private val boundmanLandRows = listOf(
        "................................................",
        "................................................",
        "................................................",
        "................................................",
        ".....KKKKKK..........WW.......WW................",
        "....KJCZZVVK..KKKKKKIWM..KK.KKMWI...............",
        "...KJJCVVVVKKKJCZZVVKK..KHHKHHKK................",
        "...KCVVVZZZZZZCVVVZZVKKKHHHHHHHKK...............",
        "...KVVZCCJJCCZVVZCCZZVKKHHHHHHHK................",
        "....KVZCCVVCCZZZCCVVCZKKHSSKKSHKK...............",
        ".....KVZVVVVZZVZCCJJCZKKHWWKSSHK..KKKKKKKK......",
        "......KVVZZCVZVZVVVVZVK.KSTKKSTK.KJCZZZZVVKK....",
        ".......KVVZCCJCVZZVVZK..KWWWWWKKKJJCVVZZZZZVKK..",
        "........KVVZCCVVZVVZK..KZKSTSKZKKCVVZZCCJJCCZVK.",
        ".........KVVZCCVZZK.WWKZZCKSSKZZKVVZZCCVVVVCCZVK",
        "..........KKVVZZZK.IMMZZCZQSSQZZKVVZCCJJVVJJCCZK",
        "........KKKKKKK.....KZZCQQXFFXQZZKVVZCCVVVVCCZZK",
        ".......KRPPK.......KZZCQQTXFFXTQZZKVZZCCJJCCZVCK",
        "......KRPRDK......KVVZZQTTXFFXTTQZKVVZZVVVVZZVCK",
        "......KRDDK......KVVVZQQTTXXFFXXTQQKKVVZZZZZVJCK",
        ".......KKK.KKKKKKVVVVZZQTTXFFFFXTTQQKKKVVZZVVJCK",
        "..........KVVVVVVVVVZZQTTXXFXXFXXTQQKKDRRPPKVCCK",
        "...........KVVVZZZQSSUTXTTUUTTXTUQQK..KDRRPKKKK.",
        "............KKVZZQSSQUTTUUSSUUTTUQSSQK.KDRPK....",
        "..............KZZQSTQUTTUUSSUUTTUQTSQK..KKK.....",
        "...............KKQSTTUTTUUSSUUTTUTTSQK..KKKKK...",
        ".................KTTTUTTUUSSUUTTUTTTKKKLLYYYK...",
        ".................KKKLLLLYYYYYYLLKKKKLLYYYYOOK...",
        ".................KLOOYYYYYYYYYOOLKKKKOOYYYYK....",
        ".................KKKKKBBBBBBBBKKKKK...KKKKK.....",
        "................KBAABBBBNBBBBBAABK..............",
        "...............KBAAABBBKKKBBBBAAABK.............",
        "..............KBBBNBK.......KBNBBBBK............",
        ".............KWWWWKK.........KKWWWWK............",
        "..........WW..KZVVKK.........KKVVZK..WW.........",
        "........IIWWMM.KCVZK.........KZVCCK.MMWWII......",
        ".......MMIWWMM..KKVK.........KVKK..MMWWIMM......",
        "................................................"
    )

    // Boundman Haki-Armored Hurt Recoil (elastic knockback with bursting steam)
    private val boundmanHurtRows = listOf(
        "................................................",
        "................................................",
        "..................KK.KK......WW.................",
        "....KKKKKK.......KHHKHHKK...IWM.................",
        "...KJCZZVVK.....KHHHHHHHKK......................",
        "..KJJCVVVVKKK...KHHHHHHHK.......................",
        "..KCVVVZZZZZVK..KHSSKKSHKK......................",
        "..KVVZCCJJCCZVK.KHSWWKSHK...KKKKKKKK............",
        "...KVZCCVVCCZVK..KSTKKSTK..KJCZZZZVVKK..........",
        "....KVZVVVVZZVK..KWWKKWWK.KJJCVVZZZZZVKK........",
        ".....KVVZZCVZVK.KZKSTSKZKKKCVVZZCCJJCCZVK.......",
        "......KVVZCCJVK.KZZCKSSKZZKVVZZCCVVVVCCZVK......",
        ".......KVVZCCVKWZZCZQSSQZZKVVZCCJJVVJJCCZVK.....",
        "........KKVVZZKMKZZCQQXFFXQZZKVVZCCVVVVCCZVK....",
        ".......KKKKKKK..KZZCQQTXFFXTQZZKVZZCCJJCCZVCK...",
        "......KRPPK....KVVZZQTTXFFXTTQZKVVZZVVVVZZVVCK..",
        ".....KRPRDK...KVVVZQQTTXXFFXXTTQKKVVZZZZZZVJCK..",
        ".....KRDDK....KVVVZZQTTXFFFFXTTQQKKVVZZVVVJJCK..",
        "......KKK.KKKKVVVVZZQTTXXFXXFXXTQQKKDRRPPKVCCK..",
        ".........KVVVVVVVVZZQSSUTXTTUUTTXTUQKDRRPKKKK...",
        "..........KVVVZZZQSSQUTTUUSSUUTTUQSSQKDRPK......",
        "...........KKVZZQSTQUTTUUSSUUTTUQTSQK.KKK.......",
        ".............KZZQSTTUTTUUSSUUTTUTTSQK...........",
        "..............KKQTTTUTTUUSSUUTTUTTTK...KKKKK....",
        "................KKKLLLLYYYYYYLLKKK...KKLLYYYK...",
        ".................KLOOYYYYYYYYYOOLKKKLLYYYYOOK...",
        ".................KKKKKBBBBBBBBKKKKKKKOOYYYYK....",
        ".................KBAABBBBNBBBBBAABK...KKKKK.....",
        "................KBAAABBBKKKBBBBAAABK............",
        "...............KBBBNBK.....KBNBBBBK.............",
        "..............KWWWWKK.......KKWWWWK.............",
        "..............KZVVK...........KVVZK.............",
        "...............KCVK...........KVCK..............",
        "...............KZVVK.........KVVZK..............",
        "................KCVK.........KVCK...............",
        ".............WW.KKVK.........KVKK.WW............",
        "............IMM...................MMI...........",
        "................................................"
    )

    // Boundman Kong Gun Cannon Launch Stance (right shoulder opens cleanly at x=30, y=16)
    private val boundmanKongGunRows = listOf(
        "................................................",
        "....KKKKKK...................WW.................",
        "...KJCZZVVK..KKKKKK......KK.KKIM................",
        "..KJJCVVVVKKKJCZZVVKK...KHHKHHKK................",
        "..KCVVVZZZZZZCVVVZZVKK.KHHHHHHHKK...............",
        "..KVVZCCJJCCZVVZCCZZVK.KHHHHHHHK..WW............",
        "...KVZCCVVCCZZZCCVVCZK.KHSSKKSHKKIMM............",
        "....KVZVVVVZZVZCCJJCZK.KHWWKSSHK................",
        ".....KVVZZCVZVZVVVVZVK..KSTKKSTK................",
        "......KVVZCCJCVZZVVZK...KWWWWWKK................",
        ".......KVVZCCVVZVVZK...KZKSTSKZKK...............",
        "........KVVZCCVZZK.WW.KZZCKSSKZZKK..............",
        ".........KKVVZZZK.IMM.KZZCZQSSQZZZKK............",
        "...........KKKK.....KZZCQQXFFXQZZZCK............",
        "........KKKK.......KZZCQQTXFFXTQZZZCK...........",
        ".......KRPPK......KVVZZQTTXFFXTTQZZCCK..........",
        "......KRPRDK.....KVVVZQQTTXXFFXXTQQZZVK.........",
        "......KRDDK.....KVVVZZQTTXFFFFXTTQQZZVK.........",
        ".......KKK.KKKKKVVVVZZQTTXXFXXFXXTQQDRPK........",
        "..........KVVVVVVVZZQSSUTXTTUUTTXTUQKDRPK.......",
        "...........KVVVZZZQSSQUTTUUSSUUTTUQSSQKK........",
        "............KKVZZQSTQUTTUUSSUUTTUQTSQK..........",
        ".......KKKKK..KZZQSTTUTTUUSSUUTTUTTSQK..........",
        ".....KKLLYYYK..KKQTTTUTTUUSSUUTTUTTTK...........",
        "....KLLYYYYOOKKKKKKLLLLYYYYYYLLKKK..............",
        "....KOOYYYYKKKLOOYYYYYYYYYYOOLKK................",
        ".....KKKKK...KKKKKBBBBBBBBKKKK..................",
        ".............KBAABBBBNBBBBBAABK.................",
        "............KBAAABBBKKKBBBBAAABK................",
        "............KBBBNBK.....KBNBBBBK................",
        "...........KWWWWKK.......KKWWWWK................",
        "...........KZVVK...........KVVZK................",
        "............KCVK...........KVCK.................",
        "............KZVVK.........KVVZK.................",
        ".............KCVK.........KVCK..................",
        ".............KZVVK.......KVVZK..................",
        ".............KKVK.........KVKK..................",
        "................................................"
    )

    // Boundman Kong Organ Alternate Pumping Stance (rapid torso twist + intense steam flare)
    private val boundmanKongOrganAltRows = listOf(
        "................................................",
        "................................................",
        ".................WW......KK.KK....WW............",
        ".....KKKKKK.....IWM.....KHHKHHKK.IWM............",
        "....KJCZZVVK...........KHHHHHHHKK...............",
        "...KJJCVVVVKKK.........KHHHHHHHK................",
        "...KCVVVZZZZZVK........KHSSKKSHKK...............",
        "...KVVZCCJJCCZVK.......KHWWKSSHK................",
        "....KVZCCVVCCZVK........KSTKKSTK................",
        ".....KVZVVVVZZVK........KWWWWWKK................",
        "......KVVZZCVZVK.......KZKSTSKZKK...............",
        ".......KVVZCCJVK..WW..KZZCKSSKZZKK..............",
        "........KKVVZZZK.IMM.KZZCZQSSQZZZKK.............",
        ".........KKKKKK.....KZZCQQXFFXQZZZCK............",
        "........KRPPK......KZZCQQTXFFXTQZZZCK...........",
        ".......KRPRDK.....KVVZZQTTXFFXTTQZZCCK..........",
        ".......KRDDK.....KVVVZQQTTXXFFXXTQQZZVK.........",
        "........KKK.KKKKKVVVVZZQTTXFFFFXTTQQZZVK........",
        "...........KVVVVVVVVZZQTTXXFXXFXXTQQDRPK........",
        "............KVVVZZZQSSUTXTTUUTTXTUQKDRPK........",
        ".............KKVZZQSSQUTTUUSSUUTTUQSSQKK........",
        ".......KKKKK...KZZQSTQUTTUUSSUUTTUQTSQK.........",
        ".....KKLLYYYK...KKQSTTUTTUUSSUUTTUTTSQK.........",
        "....KLLYYYYOOKKKKKQTTTUTTUUSSUUTTUTTTK..........",
        "....KOOYYYYKKKLLLLYYYYYYLLKKK...................",
        ".....KKKKK..KLOOYYYYYYYYYYOOLKK.................",
        "............KKKKKBBBBBBBBKKKK...................",
        "............KBAABBBBNBBBBBAABK..................",
        "...........KBAAABBBKKKBBBBAAABK.................",
        "..........KBBBNBK.......KBNBBBBK................",
        ".........KWWWWKK.........KKWWWWK................",
        "..........KZVVKK.........KKVVZK.................",
        "...........KCVZK.........KZVCCK.................",
        "............KCVK.........KVCK...................",
        ".........WW.KZVVK.......KVVZK.WW................",
        "........IMM.KKVK.........KVKK.MMI...............",
        "................................................",
        "................................................"
    )

    // Boundman King Kong Gun Stance (braced wide stance, surging shoulder steam)
    private val boundmanKingKongRows = listOf(
        "................................................",
        "..................WW........WW..................",
        ".................IWM.....KK.KKMWI...............",
        "........................KHHKHHKK................",
        ".......................KHHHHHHHKK...............",
        ".......................KHHHHHHHK................",
        ".......................KHSSKKSHKK...............",
        ".......................KHWWKSSHK................",
        "........................KSTKKSTK................",
        "........................KWWWWWKK................",
        "....................WW.KZKSTSKZKK...............",
        "...................IMMKZZCKSSKZZKK..............",
        ".....................KZZCZQSSQZZZKK.............",
        "........KKKK........KZZCQQXFFXQZZZCK............",
        ".......KRPPK.......KZZCQQTXFFXTQZZZCK...........",
        "......KRPRDK......KVVZZQTTXFFXTTQZZCCK..........",
        "....KKKRRDDK.....KVVVZQQTTXXFFXXTQQZZVK.........",
        "...KJCZZVVKK....KVVVZZQTTXFFFFXTTQQZZVK.........",
        "..KJJCVVVZZVK..KVVVVZZQTTXXFXXFXXTQQDRPK........",
        "..KCVVVZZZZZVK.KVVVVZZQSSUTXTTUUTTXTUQDRK.......",
        "..KVVZCCJJCCZVK.KVVVZZQSSQUTTUUSSUUTTUQSK.......",
        "...KVZCCVVCCZVK..KKVZZQSTQUTTUUSSUUTTUQSK.......",
        "....KVZVVVVZZVK....KZZQSTTUTTUUSSUUTTUTK..KKKK..",
        ".....KVVZZCVZK......KKQTTTUTTUUSSUUTTTKKKLLYYYK.",
        "......KKKKKKK........KKKLLLLYYYYYYLLKKKLLYYYYOOK",
        "....................KLOOYYYYYYYYYYOOLKKKOOYYYYK.",
        "....................KKKKKBBBBBBBBKKKKK...KKKKK..",
        "...................KBAABBBBNBBBBBAABK...........",
        "..................KBAAABBBKKKBBBBAAABK..........",
        ".................KBBBNBK.......KBNBBBBK.........",
        "................KWWWWKK.........KKWWWWK.........",
        "................KZVVK.............KVVZK.........",
        ".................KCVK.............KVCK..........",
        ".................KZVVK...........KVVZK..........",
        "..................KCVK...........KVCK...........",
        "...............WW.KZVVK.........KVVZK.WW........",
        "..............IMM.KKVK...........KVKK.MMI.......",
        "................................................"
    )

    // 10x8 Animated 16-bit Pixel Steam Vapor Puff Sprites (dense -> curling -> dissipating)
    private val boundmanSteamPuff0Rows = listOf(
        "...IIII...",
        ".IIWWWWII.",
        "IWWWWWWWWI",
        "IWWWWWWWMI",
        "MWWWWWWWMM",
        ".MMWWWWMM.",
        "..MMMMMM..",
        ".........."
    )

    private val boundmanSteamPuff1Rows = listOf(
        "....III...",
        "..IIWWWI..",
        ".IWWWWWMI.",
        ".MWWWWMM..",
        "IWWMM..MM.",
        "MWM.......",
        ".M........",
        ".........."
    )

    private val boundmanSteamPuff2Rows = listOf(
        ".....II...",
        "...IIWWI..",
        "..IWWMM...",
        "..MM......",
        ".....IM...",
        "....MM....",
        "..........",
        ".........."
    )

    // 20x18 Colossal Obsidian-Crimson Busoshoku Haki Fist (Kong Gun & Kong Organ)
    private val hakiFistRows = listOf(
        ".....KKKKKKKKK......",
        "...KKJCZZZZZVVKK....",
        "..KJJCVVVZZZZZZVK...",
        ".KCVVVZCCJJCCZZVVK..",
        ".KVVZZCCVVVVCCZZVVK.",
        "KVVZZCCJJVVJJCCZZVVK",
        "KVZZCCVVVVVVVVCCZZVK",
        "KVZZCCJJVVJJCCZZZVVK",
        "KVVZZCCVVVVCCZZZVVJK",
        "KVVZZZCCJJCCZZVVVJCK",
        ".KVVZZZZZZZZVVVVJJCK",
        ".KVVZCCZZZZVVVVJCCK.",
        "..KVVZCCJJCCVVVCCK..",
        "...KVVZZZZZZVVCCK...",
        "....KKVVVVVVVVKK....",
        "......KKKKKKKK......",
        "....................",
        "...................."
    )

    // 26x22 Colossal King Kong Gun Haki Fist (screen-shaking ultimate)
    private val hakiKingFistRows = listOf(
        ".......KKKKKKKKKKKK.......",
        ".....KKJCZZZZZZZZVVKK.....",
        "...KKJJCVVVZZZZZZZZZVKK...",
        "..KJJCVVVZZCCJJJCCZZZVVK..",
        ".KCVVVZZZCCVVVVVVVCCZZVVK.",
        ".KVVZZZCCJJJVVVVJJJCCZZVVK",
        "KVVZZZCCVVVVVVVVVVVVCCZZVK",
        "KVVZZCCJJJVVVVVVVVJJJCCZVK",
        "KVZZZCCVVVKKVVVVKKVVVCCZVK",
        "KVZZZCCVVVKKVVVVKKVVVCCZVK",
        "KVZZZCCJJJVVVVVVVVJJJCCZVK",
        "KVVZZZCCVVVVVVVVVVVVCCZZVK",
        "KVVZZZZCCJJJVVVVJJJCCZZVJK",
        "KVVZZZZZCCVVVVVVVCCZZZVJCK",
        ".KVVZZZZZCCCJJJCCCZZVVJJCK",
        ".KVVZCCCZZZZZZZZZZZVVJJCK.",
        "..KVVZCCJJJCCZZZZVVVVJCCK.",
        "...KVVZCCVVVCCZZVVVVCCKK..",
        "....KVVZZZZZZZZVVVVCCK....",
        ".....KKVVVVVVVVVVVVKK.....",
        ".......KKKKKKKKKKKK.......",
        ".........................."
    )

    // 18x18 Fierce Gear 4: Boundman HUD Portrait (with Hagoromo steam wisps)
    private val hudPortraitBoundmanRows = listOf(
        ".....KK.KK...WW...",
        "....KHHKHHKK.IM...",
        "...KHHHHHHHKK.....",
        "..KKHHHHHHHK..KKK.",
        ".KJZKHHSSKSHKKJCVK",
        "KJJZKHWWKSSHKJJCVK",
        "KCVZK.KSTKKSTKCVZK",
        "KVVZKKWWWWWWKVVZZK",
        ".KZZKZKSTSKZKVZZK.",
        ".KZZCKQSSQQCZVVZK.",
        "KZZCQQXFFXQQCZZVZK",
        "KZZQTTXFFXTTQZZVZK",
        "KVVQTTXXXXTTQVVZZK",
        ".KVVQUTUUTUQVVZZK.",
        "..KKKLLYYYYLLKKK..",
        "....KKKKKKKKKK....",
        "..................",
        ".................."
    )

    // 16x16 Gear 4 Skill Icons (Obsidian-Crimson Haki palette)
    private val iconKongGunRows = listOf(
        "................",
        ".......KKKKKK...",
        ".....KKJCZZVVK..",
        "....KJJCVVZZZVK.",
        ".KKKCVVZZCCZZVVK",
        "KCCZVVZZJJCCZVVK",
        "KVVZZCCVVVVCCVVK",
        "KCCZVVZZJJCCZVVK",
        ".KKKVVZZZZZZVVK.",
        "....KVVZCCZVVK..",
        "..JJ..KKKKKK....",
        "JJCC..GG........",
        "................",
        "................",
        "................",
        "................"
    )

    private val iconKongOrganRows = listOf(
        "................",
        ".....KKKKKK.....",
        "...KKJCZZVVK....",
        "..JJCVVCCVVK....",
        "...KKKKKKKKKK...",
        "........KJCZVK..",
        "..JJCC.KJJCVVZK.",
        "...KKKKKCVVCCZK.",
        ".KKJCZZVKVVVZK..",
        "JJCVVCCVKKKKK...",
        "..KKKKKKK.......",
        ".....JJJJJ......",
        ".KKKKKKK........",
        "KJCZCCVVK.......",
        ".KKKKKKK........",
        "................"
    )

    private val iconKingKongGunRows = listOf(
        "....KKKKKKKK....",
        "...KJCZCCZVVK...",
        "..KJJCVJJVCVVK..",
        ".KCVVZZVVZZVVVK.",
        ".KVVZCCVVCCZVVK.",
        ".KVVZZKVVZZVVVK.",
        ".KVVZCKVVKZCVVK.",
        "..KKKKKVVKKKKK..",
        "...KKKKKKKKKK...",
        ".JJ..GGGGGG..JJ.",
        "JJ.GGWWWWWWGG.JJ",
        ".GGWWWJJJJWWWGG.",
        "..CCC......CCC..",
        ".CC..........CC.",
        "................",
        "................"
    )

    val boundmanIdleFrames: List<DirectionalSprite> = listOf(
        PixelArtBuilder.buildPair(alignBottom38(boundmanIdle0), boundmanPalette),
        PixelArtBuilder.buildPair(alignBottom38(boundmanIdle1), boundmanPalette),
        PixelArtBuilder.buildPair(alignBottom38(boundmanIdle2), boundmanPalette),
        PixelArtBuilder.buildPair(alignBottom38(boundmanIdle0), boundmanPalette)
    )
    val boundmanRunFrames: List<DirectionalSprite> = listOf(
        PixelArtBuilder.buildPair(alignBottom38(boundmanRun0), boundmanPalette),
        PixelArtBuilder.buildPair(alignBottom38(boundmanRun1), boundmanPalette),
        PixelArtBuilder.buildPair(alignBottom38(boundmanRun2, 34), boundmanPalette),
        PixelArtBuilder.buildPair(alignBottom38(boundmanRun3), boundmanPalette)
    )
    val boundmanJumpRise: DirectionalSprite = PixelArtBuilder.buildPair(alignBottom38(boundmanJumpRiseRows, 34), boundmanPalette)
    val boundmanJumpFall: DirectionalSprite = PixelArtBuilder.buildPair(alignBottom38(boundmanJumpFallRows, 35), boundmanPalette)
    val boundmanAir: DirectionalSprite = boundmanJumpRise
    val boundmanLand: DirectionalSprite = PixelArtBuilder.buildPair(alignBottom38(boundmanLandRows), boundmanPalette)
    val boundmanHurt: DirectionalSprite = PixelArtBuilder.buildPair(alignBottom38(boundmanHurtRows), boundmanPalette)
    val boundmanKongGun: DirectionalSprite = PixelArtBuilder.buildPair(alignBottom38(boundmanKongGunRows), boundmanPalette)
    val boundmanKongOrganFrames: List<DirectionalSprite> = listOf(
        boundmanKongGun,
        PixelArtBuilder.buildPair(alignBottom38(boundmanKongOrganAltRows), boundmanPalette)
    )
    val boundmanKingKong: DirectionalSprite = PixelArtBuilder.buildPair(alignBottom38(boundmanKingKongRows), boundmanPalette)
    val boundmanSteamPuffs: List<ImageBitmap> = listOf(
        PixelArtBuilder.build(boundmanSteamPuff0Rows, boundmanPalette),
        PixelArtBuilder.build(boundmanSteamPuff1Rows, boundmanPalette),
        PixelArtBuilder.build(boundmanSteamPuff2Rows, boundmanPalette)
    )
    val hakiFist: DirectionalSprite = PixelArtBuilder.buildPair(hakiFistRows, boundmanPalette)
    val hakiKingFist: DirectionalSprite = PixelArtBuilder.buildPair(hakiKingFistRows, boundmanPalette)

    val hudPortraitBoundman: ImageBitmap = PixelArtBuilder.build(hudPortraitBoundmanRows, boundmanPalette)
    val iconKongGun: ImageBitmap = PixelArtBuilder.build(iconKongGunRows, boundmanPalette)
    val iconKongOrgan: ImageBitmap = PixelArtBuilder.build(iconKongOrganRows, boundmanPalette)
    val iconKingKongGun: ImageBitmap = PixelArtBuilder.build(iconKingKongGunRows, boundmanPalette)
}

