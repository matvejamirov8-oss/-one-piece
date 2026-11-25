package com.example.game.sprites

import androidx.compose.ui.graphics.ImageBitmap

/**
 * 16-bit Pixel Art Environment Sprites for the sunny East Blue island:
 * Pirate ship on the sea, swaying palm trees, beach props, clouds, and seagulls.
 */
class EastBlueSprites {

    private val envPalette: Map<Char, Int> = mapOf(
        '.' to 0x00000000,
        'K' to 0xFF1A1C2C.toInt(), // Deep pixel outline
        // Wood & Bark
        'B' to 0xFF8B5A2B.toInt(), // Bark / Wood mid
        'D' to 0xFF5A3619.toInt(), // Wood dark shadow
        'L' to 0xFFB87F47.toInt(), // Wood sunlit highlight
        'C' to 0xFF6B3E1D.toInt(), // Coconut brown
        // Palm Foliage
        'G' to 0xFF2E9E45.toInt(), // Lush palm green
        'E' to 0xFF1A6932.toInt(), // Deep leaf shadow
        'H' to 0xFF6CD959.toInt(), // Bright sunlit leaf tip
        'Y' to 0xFFA7F070.toInt(), // Sunlit lime gleam
        // Ship & Sails
        'W' to 0xFFFFFBF0.toInt(), // Bright sunlit sail white
        'S' to 0xFFD2E2F2.toInt(), // Sail cool shadow
        'A' to 0xFF9EB4CC.toInt(), // Sail deep shadow
        'R' to 0xFFD93030.toInt(), // Red stripe / pirate band
        'O' to 0xFFF4C542.toInt(), // Golden hull trim / straw emblem
        'N' to 0xFF23273B.toInt(), // Jolly Roger black flag
        // Beach Props & Rocks
        'P' to 0xFFFF6B7A.toInt(), // Starfish coral
        'Q' to 0xFFC73E52.toInt(), // Starfish shade
        'I' to 0xFF758599.toInt(), // Iron barrel band / stone mid
        'J' to 0xFF475466.toInt(), // Stone shadow
        'M' to 0xFFA9B7C7.toInt(), // Stone highlight
        // Clouds
        'U' to 0xFFFFFFFF.toInt(), // Cloud bright white
        'V' to 0xFFD9EEFF.toInt(), // Cloud mid blue-white
        'Z' to 0xFFA2C9EB.toInt()  // Cloud soft underside
    )

    // Going Merry-inspired East Blue Pirate Caravel (52x44)
    private val pirateShipRows = listOf(
        ".....................KKKKKKKK.......................",
        "....................KNNNNNNNNK......................",
        "....................KNNWWNWNNK......................",
        "....................KNNWWWWNNK......................",
        "....................KNNWNNWNNK......................",
        "....................KKKKKKKKKK......................",
        ".......................KDK..........................",
        ".......................KDK..........................",
        ".....................KKDDDKK........................",
        "....................KLLLLLLLK.......................",
        ".....................KKKKKKK........................",
        ".......................KDK..........................",
        "..............KKKKKKKKKKKKKKKKKKKK..................",
        "............KKWWWWWWWWWWWWWWWWWWWWKK................",
        "...........KWWWWWWWWWWWWWWWWWWWWWWSSK...............",
        "..........KWWWWWWWWWKKKKKKWWWWWWWWWSSK..............",
        ".........KWWWWWWWWKKOOOOOOKKWWWWWWWWSSK.............",
        ".........KWWWWWWWKOOWWWWWWOOKWWWWWWWSSK.............",
        "........KWWWWWWWWKOWKWWWWKWOKWWWWWWWWSSK............",
        "........KWWWWWWWWKOWWWWWWWWOKWWWWWWWWSSK............",
        "........KWWWWWWWWKKOOWWWWOOKKWWWWWWWWSSK............",
        ".........KWWWWWWWWWKKRRRRKKWWWWWWWWWSSK.............",
        ".........KWWWWWWWWWWWKKKKWWWWWWWWWWWSSK.............",
        "..........KWWWWWWWWWWWWWWWWWWWWWWWWSSK..............",
        "...........KSSSSSSSSSSSSSSSSSSSSSSSSAK..............",
        "............KKKKKKKKKKKKKKKKKKKKKKKKK...............",
        ".......................KDK.............KK...........",
        ".......................KDK............KWWK..........",
        ".......KKKK............KDK...........KWWWWK.........",
        "......KLLLLK...........KDK..........KWWKWSSK........",
        ".....KLLBBLLKKKKKKKKKKKKKKKKKKKKKKKKWWWWWWSSK.......",
        "....KLLBBBBBBLLLLLLLLLLLLLLLLLLLLLLBBWWWWWWK........",
        "....KDBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBKKKKKK.........",
        "....KDOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOK.............",
        ".....KDBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBDK.............",
        ".....KDBBBDDBBBBDDBBBBDDBBBBDDBBBBDDBDK.............",
        "......KDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDK..............",
        ".......KDDDDDDDDDDDDDDDDDDDDDDDDDDDDK...............",
        "........KKKKKKKKKKKKKKKKKKKKKKKKKKKK................",
        "...................................................."
    )

    // Lush Tropical Palm Tree (36x50)
    private val palmTreeRows = listOf(
        ".............KKKKK..................",
        ".........KKKKHYYHHKK................",
        "......KKKHYYHGGGGGGHK..KKKKK........",
        "....KKHHGGGGGEEGGGGGHKKHYYHHKK......",
        "..KKHGGEEEEEKKKEGGGGGHHGGGGGGHKK....",
        ".KHGEEKKKK......KKEGGGGGEEGGGGGHK...",
        ".KKK.........KKKKKKKEGGEKKKKEGGGHK..",
        "...........KKHYYHHKKEKKK....KKKGGK..",
        ".........KKHGGGGGHHKKK..KKK....KK...",
        "........KHGEEGGGGGGKCCKHHYYKK.......",
        ".......KGEKKKEGGGGGKCCKGGGGGHKK.....",
        ".......KK.....KKGGEKCCKGGEEGGGHK....",
        "...............KKK.KLLKKEKKKKEGHK...",
        "...................KBBK......KKK....",
        "..................KLLBK.............",
        "..................KBBBK.............",
        "..................KDDDK.............",
        ".................KLLBBK.............",
        ".................KBBBBK.............",
        ".................KDDDDK.............",
        "................KLLBBK..............",
        "................KBBBBK..............",
        "................KDDDDK..............",
        "...............KLLBBK...............",
        "...............KBBBBK...............",
        "...............KDDDDK...............",
        "...............KLLBBK...............",
        "..............KBBBBK................",
        "..............KDDDDK................",
        "..............KLLBBK................",
        "..............KBBBBK................",
        "..............KDDDDK................",
        ".............KLLBBK.................",
        ".............KBBBBK.................",
        ".............KDDDDK.................",
        ".............KLLBBK.................",
        ".............KBBBBK.................",
        ".............KDDDDK.................",
        "............KLLBBBK.................",
        "............KBBBBBK.................",
        "............KDDDDDK.................",
        "...........KLLBBBBK.................",
        "...........KBBBBBBK.................",
        "..........KDDDDDDDK.................",
        ".........KLLBBBBBBBK................",
        "........KBBBBBBBBBBBK...............",
        "........KKKKKKKKKKKKK..............."
    )

    // Wooden Pirate Barrel (14x16)
    private val barrelRows = listOf(
        "...KKKKKKKK...",
        "..KLLLLLLLLK..",
        ".KIIIIIJJIIIK.",
        ".KLLBBBBBBDKK.",
        "KLLBBDBBBDDBBK",
        "KLLBBDBBBDDBBK",
        "KIIIIIIJJIIIIK",
        "KLLBBDBBBDDBBK",
        "KLLBBDBBBDDBBK",
        "KLLBBDBBBDDBBK",
        "KIIIIIIJJIIIIK",
        ".KLLBBBBBBDKK.",
        ".KDDDDDDDDDDK.",
        "..KKKKKKKKKK.."
    )

    // Wooden Pier Crate (16x16)
    private val crateRows = listOf(
        "KKKKKKKKKKKKKKKK",
        "KLLLLLLLLLLLLLDK",
        "KLBBBBBBBBBBBDDK",
        "KLBKKBBBBBBKKDDK",
        "KLBBKKBBBBKKBDDK",
        "KLBBBKKBBKKBBDDK",
        "KLBBBBKKKKBBBDDK",
        "KLBBBBBKKBBBBDDK",
        "KLBBBBKKKKBBBDDK",
        "KLBBBKKBBKKBBDDK",
        "KLBBKKBBBBKKBDDK",
        "KLBKKBBBBBBKKDDK",
        "KLBBBBBBBBBBBDDK",
        "KDDDDDDDDDDDDDDK",
        "KKKKKKKKKKKKKKKK"
    )

    // Beach Starfish (9x8)
    private val starfishRows = listOf(
        "....K....",
        "...KPK...",
        "KKKPPPKKK",
        "KPPPPPPQK",
        ".KKPPQKK.",
        "..KPQPK..",
        ".KPK.KQK.",
        ".KK...KK."
    )

    // Coastal Rock (18x11)
    private val rockRows = listOf(
        "......KKKKKK......",
        "....KKMMMMMMKK....",
        "...KMMIIIIIIIMK...",
        "..KMIIIIIIIIIIJK..",
        ".KMIIIIIIJJJJIIJK.",
        "KMIIIIJJJJJJJJJJJK",
        "KIIIIJJJJJJJJJJJJK",
        "KJJJJJJJJJJJJJJJJK",
        "KKKKKKKKKKKKKKKKKK"
    )

    // Fluffy 16-bit Cloud Large (38x14)
    private val cloudLargeRows = listOf(
        "...........UUUUUUU....................",
        ".........UUUUUUUUUUU..................",
        "........UUUUUUUUUUUUU....UUUUU........",
        ".....UUUUUUUUUUUUUUUUU..UUUUUUU.......",
        "...UUUUUUUUUUUUUUUUUUUUUUUUUUUUUU.....",
        "..UUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUU....",
        ".UUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUU...",
        "UUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUU..",
        "VVUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUV..",
        "VVVVVVVVVVVVUUUUUUUUUUUUUUUUUVVVVVVV..",
        ".ZZZZVVVVVVVVVVVVVVVVVVVVVVVVVVZZZZ...",
        "..ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ...."
    )

    // Fluffy 16-bit Cloud Small (24x10)
    private val cloudSmallRows = listOf(
        ".......UUUUUU...........",
        ".....UUUUUUUUUU.........",
        "...UUUUUUUUUUUUUUU......",
        "..UUUUUUUUUUUUUUUUUU....",
        ".UUUUUUUUUUUUUUUUUUUU...",
        "UUUUUUUUUUUUUUUUUUUUUU..",
        "VVVVUUUUUUUUUUUUUUVVVV..",
        ".ZZZZVVVVVVVVVVVVZZZZ...",
        "..ZZZZZZZZZZZZZZZZZZ...."
    )

    // Seagull Flapping Frames (10x6)
    private val seagullUpRows = listOf(
        "K........K",
        ".KK....KK.",
        "..KUUUUk..",
        "...KWWK...",
        "....KK...."
    ).map { it.replace('k', 'K') }

    private val seagullDownRows = listOf(
        "..........",
        "...KKKK...",
        "..KUUUUk..",
        ".KK.KK.KK.",
        "K........K"
    ).map { it.replace('k', 'K') }

    val pirateShip: ImageBitmap = PixelArtBuilder.build(pirateShipRows, envPalette)
    val palmTreeRight: ImageBitmap = PixelArtBuilder.build(palmTreeRows, envPalette)
    val palmTreeLeft: ImageBitmap = PixelArtBuilder.flipHorizontal(palmTreeRows, envPalette)
    val barrel: ImageBitmap = PixelArtBuilder.build(barrelRows, envPalette)
    val crate: ImageBitmap = PixelArtBuilder.build(crateRows, envPalette)
    val starfish: ImageBitmap = PixelArtBuilder.build(starfishRows, envPalette)
    val rock: ImageBitmap = PixelArtBuilder.build(rockRows, envPalette)
    val cloudLarge: ImageBitmap = PixelArtBuilder.build(cloudLargeRows, envPalette)
    val cloudSmall: ImageBitmap = PixelArtBuilder.build(cloudSmallRows, envPalette)
    val seagullUp: ImageBitmap = PixelArtBuilder.build(seagullUpRows, envPalette)
    val seagullDown: ImageBitmap = PixelArtBuilder.build(seagullDownRows, envPalette)
}
