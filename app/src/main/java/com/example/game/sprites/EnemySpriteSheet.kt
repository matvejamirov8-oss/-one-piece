package com.example.game.sprites

import androidx.compose.ui.graphics.ImageBitmap

/**
 * 16-bit Pixel Art Sprites for Level 1 Enemies, Boss Captain Axe-Hand Morgan,
 * Projectiles (Rifle Bullet, Morgan Ground Shockwave), and Loot (Meat on Bone, Berry Coin).
 * Every enemy frame includes a pre-compiled pure-white hit-flash silhouette.
 */
class EnemySpriteSheet {

    private val enemyPalette: Map<Char, Int> = mapOf(
        '.' to 0x00000000,
        'K' to 0xFF181425.toInt(), // Crisp 16-bit dark ink outline
        // Marine White / Navy
        'W' to 0xFFFFFBF2.toInt(), // Crisp Marine coat white
        'C' to 0xFFD2DFED.toInt(), // Coat cool shadow
        'B' to 0xFF2457A6.toInt(), // Marine blue accent / trousers
        'N' to 0xFF15346B.toInt(), // Deep navy shadow
        'A' to 0xFF4C8BE8.toInt(), // Marine bright sky blue
        // Skin & Hair
        'S' to 0xFFF2BE96.toInt(), // Warm skin
        'T' to 0xFFD4976C.toInt(), // Skin shadow
        'H' to 0xFF2A2638.toInt(), // Dark hair / boots
        'Y' to 0xFFF5D142.toInt(), // Blonde hair / Gold epaulettes / Berry coin
        'L' to 0xFFFFF29C.toInt(), // Gold bright gleam
        'O' to 0xFFC7881A.toInt(), // Gold shadow
        // Steel & Iron (Sabers, Rifle, Morgan's Iron Jaw & Giant Axe!)
        'M' to 0xFFDEE8F2.toInt(), // Bright steel edge
        'I' to 0xFF9EB0C4.toInt(), // Steel mid-tone
        'J' to 0xFF5A6B80.toInt(), // Dark riveted steel shadow
        // Wood & Bandit Colors
        'U' to 0xFF8C5327.toInt(), // Wood rifle stock / roasted meat mid
        'D' to 0xFF593115.toInt(), // Dark wood / roasted meat crust
        'R' to 0xFFD92B3A.toInt(), // Bandit crimson bandana / danger glint
        'P' to 0xFF8F1826.toInt(), // Dark crimson shade
        'V' to 0xFF6B3FA0.toInt(), // Bandit purple vest
        'E' to 0xFF63E6FF.toInt(), // Shockwave cyan highlight
        'F' to 0xFFFF8C24.toInt()  // Muzzle flash / warm orange
    )

    // =========================================================================
    // 1. MARINE SOLDIER (Солдат Морского Дозора — ближний бой с саблей, 26x32)
    // Facing RIGHT by default
    // =========================================================================
    private val marineWalk0 = PixelArtBuilder.alignBottom(listOf(
        ".......KKKKKKKK...........",
        "......KWWWWWWWWK..........",
        ".....KWWBBBBBBWWK.........",
        "....KWWWWWWWWWWKKKK.......",
        "....KKBBBBBBBBBBBBK.......",
        "......KSSSSSSSKKKK........",
        "......KSSKSKSK............",
        "......KTSSSSSK............",
        ".......KTTTTK.....K.......",
        ".....KKWBBBBWKK..KMK......",
        "....KWWWAWWAWWWK.KMK......",
        "....KWWCWAAWCWWKKMK.......",
        "...KWWKCWWWWCKWWKMK.......",
        "...KSSKCWWWWCKSSKK........",
        "...KTTKKBBBBKKYYK.........",
        "....KK.KWWWWK.KK..........",
        ".......KBBBBK.............",
        "......KBNNBBBK............",
        "......KBBK.KBBK...........",
        ".....KBBK...KBBK..........",
        ".....KNNK...KNNK..........",
        "....KHHHK...KHHHK.........",
        "....KHHHK...KHHHK.........",
        ".....KKK.....KKK..........",
        "..........................",
        "..........................",
        "..........................",
        "..........................",
        "..........................",
        "..........................",
        "..........................",
        ".........................."
    ), 30)

    private val marineWalk1 = PixelArtBuilder.alignBottom(listOf(
        "..........................",
        ".......KKKKKKKK...........",
        "......KWWWWWWWWK..........",
        ".....KWWBBBBBBWWK.........",
        "....KWWWWWWWWWWKKKK.......",
        "....KKBBBBBBBBBBBBK.......",
        "......KSSSSSSSKKKK........",
        "......KSSKSKSK............",
        "......KTSSSSSK.....K......",
        ".......KTTTTK.....KMK.....",
        ".....KKWBBBBWKK..KMK......",
        "....KWWWAWWAWWWK.KMK......",
        "....KWWCWAAWCWWKKMK.......",
        "....KSSKCWWWWCKSSK........",
        "....KTTKKBBBBKKYYK........",
        ".....KK.KWWWWK.KK.........",
        ".......KBBBBK.............",
        ".......KBNNBK.............",
        ".......KBBKBBK............",
        ".......KBBKBBK............",
        ".......KNNKNNK............",
        "......KHHHKHHHK...........",
        "......KHHHKHHHK...........",
        ".......KKK.KKK............",
        "..........................",
        "..........................",
        "..........................",
        "..........................",
        "..........................",
        "..........................",
        "..........................",
        ".........................."
    ), 30)

    private val marineWindup = PixelArtBuilder.alignBottom(listOf(
        "............KMK...........",
        "...........KMMK...........",
        "..........KMIK............",
        ".......KKKKKKK............",
        "......KWWWWWWWWK..........",
        ".....KWWBBBBBBWWK.........",
        "....KWWWWWWWWWWKKKK.......",
        "....KKBBBBBBBBBBBBK.......",
        "......KSSSSSSSKKKK........",
        "......KSSKRKRK............",
        "......KTSSSSSK............",
        ".......KTTTTK.KYYK........",
        ".....KKWBBBBWKKSSK........",
        "....KWWWAWWAWWWKTTK.......",
        "....KSSCWAAWCWWKKK........",
        "....KTTKCWWWWCK...........",
        ".....KK.KBBBBK............",
        ".......KWWWWK.............",
        "......KBNNBBBK............",
        ".....KBBK..KBBK...........",
        ".....KNNK..KNNK...........",
        "....KHHHK..KHHHK..........",
        "....KHHHK..KHHHK..........",
        ".....KKK....KKK...........",
        "..........................",
        "..........................",
        "..........................",
        "..........................",
        "..........................",
        "..........................",
        "..........................",
        ".........................."
    ), 30)

    private val marineSlash = PixelArtBuilder.alignBottom(listOf(
        "........KKKKKKKK..........",
        ".......KWWWWWWWWK.........",
        "......KWWBBBBBBWWK........",
        ".....KWWWWWWWWWWKKKK......",
        ".....KKBBBBBBBBBBBBK......",
        ".......KSSSSSSSKKKK.......",
        ".......KSSKSKSK...........",
        ".......KTSSSSSK...........",
        "........KTTTTK............",
        "......KKWBBBBWKK..........",
        ".....KWWWAWWAWWWK..KKKKKKK",
        "....KWWKCWAAWCWWWKKMMMMMMK",
        "....KSSKCWWWWCKSSKYIIIIIIK",
        ".....KK.KBBBBK.KK.KKKKKKK.",
        "........KWWWWK............",
        ".......KBNNBBBK...........",
        "......KBBK..KBBK..........",
        ".....KBBK....KBBK.........",
        ".....KNNK....KNNK.........",
        "....KHHHK....KHHHK........",
        "....KHHHK....KHHHK........",
        ".....KKK......KKK.........",
        "..........................",
        "..........................",
        "..........................",
        "..........................",
        "..........................",
        "..........................",
        "..........................",
        "..........................",
        "..........................",
        ".........................."
    ), 30)

    // =========================================================================
    // 2. MARINE RIFLEMAN (Дозорный-стрелок — дальний бой с ружьём, 28x32)
    // =========================================================================
    private val rifleWalk0 = PixelArtBuilder.alignBottom(listOf(
        ".......KKKKKKKK.............",
        "......KWWWWWWWWK............",
        ".....KWWBBBBBBWWK...........",
        "....KWWWWWWWWWWKKKK.........",
        "....KKBBBBBBBBBBBBK.........",
        "......KSSSSSSSKKKK..........",
        "......KSSKSKSK..............",
        "......KTSSSSSK..............",
        ".......KTTTTK...............",
        ".....KKBBBBBBKK.............",
        "....KBBWAWWAWBBK...KKKKKKKK.",
        "....KBBWWAAWWBBKKKKIIIIIIIK.",
        "....KSSKWWWWKSSKUUUUKKKKKKK.",
        "....KTTKBBBBKTTKUDDK........",
        ".....KK.KWWWWK..KKK.........",
        ".......KBBBBK...............",
        "......KBNNBBBK..............",
        "......KBBK.KBBK.............",
        ".....KBBK...KBBK............",
        ".....KNNK...KNNK............",
        "....KHHHK...KHHHK...........",
        "....KHHHK...KHHHK...........",
        ".....KKK.....KKK............",
        "............................",
        "............................",
        "............................",
        "............................",
        "............................",
        "............................",
        "............................",
        "............................",
        "............................"
    ), 30)

    private val rifleAim = PixelArtBuilder.alignBottom(listOf(
        ".......KKKKKKKK.............",
        "......KWWWWWWWWK............",
        ".....KWWBBBBBBWWK...........",
        "....KWWWWWWWWWWKKKK.........",
        "....KKBBBBBBBBBBBBK.........",
        "......KSSSSSSSKKKK..........",
        "......KSSKRKRK..............",
        "......KTSSSSSK..............",
        ".......KTTTTK...............",
        ".....KKBBBBBBKK.............",
        "....KBBWAWWAWBBKKKKKKKKKKKK.",
        "....KSSWWAAWWBBKUUUIIIIIIIIF",
        "....KTTKWWWWKSSKUDDKKKKKKKK.",
        ".....KK.KBBBBK..KKK.........",
        ".......KWWWWK...............",
        "......KBNNBBBK..............",
        ".....KBBK..KBBK.............",
        ".....KNNK..KNNK.............",
        "....KHHHK..KHHHK............",
        "....KHHHK..KHHHK............",
        ".....KKK....KKK.............",
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
    ), 30)

    // =========================================================================
    // 3. PIRATE BANDIT (Пират-разбойник — быстрый враг в бандане, 26x30)
    // =========================================================================
    private val banditRun0 = PixelArtBuilder.alignBottom(listOf(
        "......KKKKKKKKK...........",
        "....KKRRRRPRRRKK..........",
        "...KRRPPPRRRRRPRK.........",
        "..KPPKRRRRRRRRRRK.........",
        "...KK.KSSSSSSSKK..........",
        "......KSKKKSWSK...........",
        "......KTSSSSSSK...........",
        ".......KTTTTKK............",
        ".....KKVVVVVVKK...........",
        "....KVVVSSSSVVVK..KKKK....",
        "...KSSKVVSSVVKKSSKMMMK....",
        "...KTTKVVVVVVK.KKKIIK.....",
        "....KK.KYYYYK.....KK......",
        ".......KHHHHK.............",
        "......KHKKHHHK............",
        ".....KHHK..KHHK...........",
        "....KHHK....KHHK..........",
        "....KDDK....KDDK..........",
        "...KDDDK....KDDDK.........",
        "....KKK......KKK..........",
        "..........................",
        "..........................",
        "..........................",
        "..........................",
        "..........................",
        "..........................",
        "..........................",
        "..........................",
        "..........................",
        ".........................."
    ), 28)

    private val banditRun1 = PixelArtBuilder.alignBottom(listOf(
        "..........................",
        "......KKKKKKKKK...........",
        "....KKRRRRPRRRKK..........",
        "...KRRPPPRRRRRPRK.........",
        "..KPPKRRRRRRRRRRK.........",
        "...KK.KSSSSSSSKK..........",
        "......KSKKKSWSK...........",
        "......KTSSSSSSK...........",
        ".......KTTTTKK............",
        ".....KKVVVVVVKK...........",
        "....KVVVSSSSVVVK.KKKK.....",
        "....KSSKVVSSVVKKSMMMK.....",
        "....KTTKVVVVVVK.KKIIK.....",
        ".....KK.KYYYYK....KK......",
        ".......KHHHHK.............",
        ".......KHKKHK.............",
        ".......KHHKHHK............",
        ".......KDDKDDK............",
        "......KDDDKDDDK...........",
        ".......KKK.KKK............",
        "..........................",
        "..........................",
        "..........................",
        "..........................",
        "..........................",
        "..........................",
        "..........................",
        "..........................",
        "..........................",
        ".........................."
    ), 28)

    private val banditSlash = PixelArtBuilder.alignBottom(listOf(
        ".......KKKKKKKKK..........",
        ".....KKRRRRPRRRKK.........",
        "....KRRPPPRRRRRPRK........",
        "...KPPKRRRRRRRRRRK........",
        "....KK.KSSSSSSSKK.........",
        ".......KSKKKSWSK..........",
        ".......KTSSSSSSK..........",
        "........KTTTTKK...........",
        "......KKVVVVVVKK..........",
        ".....KVVVSSSSVVVK.KKKKKKKK",
        "....KSSKVVSSVVKKSSKMMMMMMK",
        ".....KKKVVVVVVK.KKKIIIIIIK",
        "........KYYYYK....KKKKKKKK",
        ".......KHHHHHK............",
        "......KHHK.KHHK...........",
        ".....KHHK...KHHK..........",
        ".....KDDK...KDDK..........",
        "....KDDDK...KDDDK.........",
        ".....KKK.....KKK..........",
        "..........................",
        "..........................",
        "..........................",
        "..........................",
        "..........................",
        "..........................",
        "..........................",
        "..........................",
        "..........................",
        "..........................",
        ".........................."
    ), 28)

    // =========================================================================
    // 4. BOSS: CAPTAIN AXE-HAND MORGAN (Капитан Морган — крупный 38x44 спрайт!)
    // Blonde spiky hair, iron steel jaw, white Navy cape, muscular torso, HUGE steel axe-hand!
    // =========================================================================
    private val morganIdle0 = PixelArtBuilder.alignBottom(listOf(
        "..........KK..KK..KK..................",
        ".........KLLKKYYKKLLK.................",
        "........KYYYYYYYYYYYYK................",
        ".......KYYOYYYYYYYYOYYK...............",
        ".......KKSSSSSSSSSSSSKK...............",
        "........KSSKWKSSKWKSSK................",
        "........KSSSTTTTTTSSSK................",
        "........KMMIIIIIIIIIMK................",
        "........KIMKIIKKIIKMIK................",
        "........KJJJJJJJJJJJJK................",
        ".....KKKKKKKTTTTTTKKKKKKK.............",
        "...KKYYYYYKKSSSSSSKKYYYYYKK...........",
        "..KWWLLLLWKSSSSSSSSKWLLLLWWK..........",
        ".KWWWWWWWKSSSTTTTSSSKWWWWWWWK.........",
        ".KWWCWWWWKSSSTKKTSSSKWWWWCWWK.........",
        ".KWWCWWWKSSKTTTTTTKSSKWWWCWWK..KKKKK..",
        "KWWWCWWWKSSKSSSSSSKSSKWWWCWWKKKMMMMMK.",
        "KWWCWWWWKTTKSSSTSSKTTKWWWCWWKJJMMMMMMK",
        "KWWCWWWWK..KTTTTTTK..KWWWCWWKJJIIIIIIM",
        "KWWCWWWK...KYYYYYYK...KWWCWWKJJIIIIIIM",
        "KWWCWWWK..KKBBBBBBKK..KWWCWWKJJMMMMMMK",
        "KWWCWWK...KBBBBBBBBK...KWCWWKKKMMMMMK.",
        "KWWCWWK..KBBNBBBBNNBK..KWWCWK..KKKKK..",
        "KWWCWWK..KBBBBKKBBBBK..KWWCWK.........",
        "KWWCWWK..KBBBK..KBBBK..KWWCWK.........",
        ".KWCWWK..KNNNK..KNNNK..KWCWK..........",
        "..KKKK...KBBBK..KBBBK...KKK...........",
        ".........KHHHK..KHHHK.................",
        "........KHHHHK..KHHHHK................",
        "........KHHHHK..KHHHHK................",
        ".........KKKK....KKKK.................",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................"
    ), 42)

    private val morganWalk1 = PixelArtBuilder.alignBottom(listOf(
        "......................................",
        "..........KK..KK..KK..................",
        ".........KLLKKYYKKLLK.................",
        "........KYYYYYYYYYYYYK................",
        ".......KYYOYYYYYYYYOYYK...............",
        ".......KKSSSSSSSSSSSSKK...............",
        "........KSSKWKSSKWKSSK................",
        "........KSSSTTTTTTSSSK................",
        "........KMMIIIIIIIIIMK................",
        "........KIMKIIKKIIKMIK................",
        "........KJJJJJJJJJJJJK................",
        ".....KKKKKKKTTTTTTKKKKKKK.............",
        "...KKYYYYYKKSSSSSSKKYYYYYKK...........",
        "..KWWLLLLWKSSSSSSSSKWLLLLWWK..........",
        ".KWWWWWWWKSSSTTTTSSSKWWWWWWWK.........",
        ".KWWCWWWWKSSSTKKTSSSKWWWWCWWK...KKKKK.",
        ".KWWCWWWKSSKTTTTTTKSSKWWWCWWKKKMMMMMK.",
        "KWWWCWWWKSSKSSSSSSKSSKWWWCWWKJJMMMMMMK",
        "KWWCWWWWKTTKSSSTSSKTTKWWWCWWKJJIIIIIIM",
        "KWWCWWWWK..KTTTTTTK..KWWWCWWKJJIIIIIIM",
        "KWWCWWWK...KYYYYYYK...KWWCWWKJJMMMMMMK",
        "KWWCWWWK..KKBBBBBBKK..KWWCWWKKKMMMMMK.",
        "KWWCWWK...KBBBBBBBBK...KWCWWK...KKKKK.",
        "KWWCWWK...KBBNBBNNBK...KWWCWK.........",
        "KWWCWWK...KBBBK.KBBK...KWWCWK.........",
        ".KWCWWK...KNNNK.KNNK...KWCWK..........",
        "..KKKK....KBBBK.KBBK....KKK...........",
        "..........KHHHK.KHHK..................",
        ".........KHHHHK.KHHHHK................",
        ".........KHHHHK.KHHHHK................",
        "..........KKKK...KKKK.................",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................"
    ), 42)

    // Morgan High Overhead Axe Windup (clear danger telegraph!)
    private val morganWindup = PixelArtBuilder.alignBottom(listOf(
        "...................KKKKKKKK...........",
        "..................KMMMMMMMMK..........",
        ".................KMMIIIIIIIMK.........",
        ".................KMMIIIIIIIMK.........",
        "..................KMMJJJJJJMK.........",
        "...................KKJJJJJKK..........",
        "..........KK..KK..KK..KJJJK...........",
        ".........KLLKKYYKKLLK.KJJJK...........",
        "........KYYYYYYYYYYYYKKSSSK...........",
        ".......KYYOYYYYYYYYOYYKSSK............",
        ".......KKSSSSSSSSSSSSKKTTK............",
        "........KSSKRKSSKRKSSK................",
        "........KSSSTTTTTTSSSK................",
        "........KMMIIIIIIIIIMK................",
        "........KIMKIIKKIIKMIK................",
        "........KJJJJJJJJJJJJK................",
        ".....KKKKKKKTTTTTTKKKKKKK.............",
        "...KKYYYYYKKSSSSSSKKYYYYYKK...........",
        "..KWWLLLLWKSSSSSSSSKWLLLLWWK..........",
        ".KWWWWWWWKSSSTTTTSSSKWWWWWWWK.........",
        ".KWWCWWWWKSSSTKKTSSSKWWWWCWWK.........",
        "KWWWCWWWKSSKTTTTTTKSSKWWWCWWK.........",
        "KWWCWWWWKTTKSSSSSSKTTKWWWCWWK.........",
        "KWWCWWWK...KTTTTTTK...KWWCWWK.........",
        "KWWCWWWK...KYYYYYYK...KWWCWWK.........",
        "KWWCWWK...KKBBBBBBKK...KWCWWK.........",
        "KWWCWWK..KBBNBBBBNNBK..KWWCWK.........",
        "KWWCWWK..KBBBBKKBBBBK..KWWCWK.........",
        ".KWCWWK..KBBBK..KBBBK..KWCWK..........",
        "..KKKK...KNNNK..KNNNK...KKK...........",
        ".........KHHHK..KHHHK.................",
        "........KHHHHK..KHHHHK................",
        "........KHHHHK..KHHHHK................",
        ".........KKKK....KKKK.................",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................"
    ), 42)

    // Morgan Massive Forward Cleave / Ground Slam Slash!
    private val morganSlash = PixelArtBuilder.alignBottom(listOf(
        "............KK..KK..KK................",
        "...........KLLKKYYKKLLK...............",
        "..........KYYYYYYYYYYYYK..............",
        ".........KYYOYYYYYYYYOYYK.............",
        ".........KKSSSSSSSSSSSSKK.............",
        "..........KSSKWKSSKWKSSK..............",
        "..........KSSSTTTTTTSSSK..............",
        "..........KMMIIIIIIIIIMK..............",
        "..........KIMKIIKKIIKMIK..............",
        "..........KJJJJJJJJJJJJK..............",
        ".......KKKKKKKTTTTTTKKKKKKK...........",
        ".....KKYYYYYKKSSSSSSKKYYYYYKK.........",
        "....KWWLLLLWKSSSSSSSSKWLLLLWWK........",
        "...KWWWWWWWKSSSTTTTSSSKWWWWWWWK.......",
        "..KWWCWWWWKSSSTKKTSSSKWWWWCWWK........",
        ".KWWWCWWWKSSKTTTTTTKSSKWWWCWWK..KKKKKK",
        ".KWWCWWWWKTTKSSSSSSKTTKKKKJJJJKKMMMMMK",
        ".KWWCWWWK...KTTTTTTK...KKKJJJJJMMMMMMK",
        ".KWWCWWWK...KYYYYYYK......KKKKJIIIIIIM",
        ".KWWCWWK...KKBBBBBBKK........KJIIIIIIM",
        ".KWWCWWK..KBBNBBBBNNBK.......KJMMMMMMK",
        "..KWCWWK..KBBBBKKBBBBK.......KKMMMMMK.",
        "...KKKK..KBBBK....KBBBK........KKKKKK.",
        ".........KNNNK....KNNNK...............",
        "........KHHHHK....KHHHHK..............",
        "........KHHHHK....KHHHHK..............",
        ".........KKKK......KKKK...............",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................"
    ), 42)

    // =========================================================================
    // 5. PROJECTILES & LOOT SPRITES
    // =========================================================================
    private val bulletRows = listOf(
        "..KKKKKK",
        "FFLLLLYK",
        "FFYYYYOK",
        "..KKKKKK"
    )

    private val morganWaveRows = listOf(
        "........KKKK....",
        "......KKMMMK....",
        ".....KMMWWWMK...",
        "....KMMWWWWEMK..",
        "...KMMWWEEEEMK..",
        "..KMMWWEEEEEMK..",
        "..KMWWEEEEEEEMK.",
        ".KMWWEEEEEEEEMK.",
        ".KMWWEEEEEEEEMK.",
        "KMMWEEEEEEEEEMK.",
        "KMWWEEEEEEEEEMK.",
        "KMWWEEEEEEEEEMK.",
        "KMMWWWWWWWWWMMK.",
        ".KKKKKKKKKKKKK..",
        "................",
        "................"
    )

    // Iconic Luffy Meat on the Bone (14x12)
    private val meatOnBoneRows = listOf(
        "....KKKKKK....",
        "...KUUUUUUK...",
        ".KKUUDUUUUDKK.",
        "KWWKUUUUUUUKWK",
        "KWWKUDUUUUDKWK",
        ".KKKUUUUUUUKKK",
        "KWWKUDDDDDUKWK",
        "KWWKUDDDDDUKWK",
        ".KKKDDDDDDDKKK",
        "...KKKKKKKK...",
        "..............",
        ".............."
    )

    // Golden Berry Coin (10x10)
    private val berryCoinRows = listOf(
        "..KKKKKK..",
        ".KLLYYYYK.",
        "KLLYYYYOYK",
        "KLYKYYKOYK",
        "KLYKKKKOYK",
        "KLYKYYKOYK",
        "KLYYYYYOYK",
        "KLOOOOOOOK",
        ".KOOOOOOK.",
        "..KKKKKK.."
    )

    // Compiled DirectionalFlashSprites
    val marineWalk: List<DirectionalFlashSprite> = listOf(
        PixelArtBuilder.buildFlashPair(marineWalk0, enemyPalette),
        PixelArtBuilder.buildFlashPair(marineWalk1, enemyPalette)
    )
    val marineWindupSprite: DirectionalFlashSprite = PixelArtBuilder.buildFlashPair(marineWindup, enemyPalette)
    val marineSlashSprite: DirectionalFlashSprite = PixelArtBuilder.buildFlashPair(marineSlash, enemyPalette)

    val rifleWalk: List<DirectionalFlashSprite> = listOf(
        PixelArtBuilder.buildFlashPair(rifleWalk0, enemyPalette),
        PixelArtBuilder.buildFlashPair(rifleWalk0, enemyPalette)
    )
    val rifleAimSprite: DirectionalFlashSprite = PixelArtBuilder.buildFlashPair(rifleAim, enemyPalette)

    val banditRun: List<DirectionalFlashSprite> = listOf(
        PixelArtBuilder.buildFlashPair(banditRun0, enemyPalette),
        PixelArtBuilder.buildFlashPair(banditRun1, enemyPalette)
    )
    val banditSlashSprite: DirectionalFlashSprite = PixelArtBuilder.buildFlashPair(banditSlash, enemyPalette)

    val morganWalk: List<DirectionalFlashSprite> = listOf(
        PixelArtBuilder.buildFlashPair(morganIdle0, enemyPalette),
        PixelArtBuilder.buildFlashPair(morganWalk1, enemyPalette)
    )
    val morganWindupSprite: DirectionalFlashSprite = PixelArtBuilder.buildFlashPair(morganWindup, enemyPalette)
    val morganSlashSprite: DirectionalFlashSprite = PixelArtBuilder.buildFlashPair(morganSlash, enemyPalette)

    val rifleBullet: DirectionalSprite = PixelArtBuilder.buildPair(bulletRows, enemyPalette)
    val morganShockwave: DirectionalSprite = PixelArtBuilder.buildPair(morganWaveRows, enemyPalette)
    val meatOnBone: ImageBitmap = PixelArtBuilder.build(meatOnBoneRows, enemyPalette)
    val berryCoin: ImageBitmap = PixelArtBuilder.build(berryCoinRows, enemyPalette)
}
