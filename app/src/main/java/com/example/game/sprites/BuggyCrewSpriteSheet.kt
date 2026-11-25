package com.example.game.sprites

import androidx.compose.ui.graphics.ImageBitmap

/**
 * 16-bit Pixel Art sprite sheets for Level 2 (Orange Town):
 * - Buggy Pirates: Acrobat, Knife Thrower, Elite Cabaji on unicycle
 * - Boss: Captain Buggy the Clown (walk, windup, Bara-Bara Cannon, split feet + detached parts)
 * - Projectiles: Throwing Knife, Cabaji Flame, Bara-Bara Fist, Buggy Ball
 * - Orange Town Sunset Props: Striped Circus Tent, Ruined Port Houses, Buggy's Clown Ship
 */
class BuggyCrewSpriteSheet {

    private val palette: Map<Char, Int> = mapOf(
        '.' to 0x00000000,
        'k' to 0xFF181425.toInt(), // crisp dark outline
        's' to 0xFFF2BE96.toInt(), // skin
        'd' to 0xFFD4976C.toInt(), // skin shade
        'w' to 0xFFF7F9FC.toInt(), // white cloth/gloves/skull
        'W' to 0xFFD4DCE8.toInt(), // white shade
        'r' to 0xFFE5303B.toInt(), // Buggy crimson red (nose/cape/stripes)
        'R' to 0xFFA31B26.toInt(), // dark crimson
        'c' to 0xFF2EA7F2.toInt(), // Buggy bright blue hair
        'C' to 0xFF1A6BB3.toInt(), // deep blue hair / hat shadow
        'b' to 0xFF243B6B.toInt(), // navy pirate hat / pants
        'B' to 0xFF142240.toInt(), // dark navy
        'p' to 0xFF8C3BB8.toInt(), // circus purple
        'P' to 0xFF561D7A.toInt(), // dark purple
        'y' to 0xFFFFD84D.toInt(), // gold trim / sash / spark
        'Y' to 0xFFD4961C.toInt(), // gold shadow
        'g' to 0xFF38C958.toInt(), // Cabaji green sash
        'G' to 0xFF1E7A38.toInt(), // dark green
        'm' to 0xFFD8E4F0.toInt(), // bright blade steel
        'M' to 0xFF8A9CB3.toInt(), // mid steel
        'h' to 0xFF4F5C6E.toInt(), // dark iron / tire
        'o' to 0xFFFF8826.toInt(), // fire orange / terracotta
        'O' to 0xFFC74E1C.toInt(), // deep orange / roof shade
        't' to 0xFF9C6631.toInt(), // wood / brown boots
        'T' to 0xFF5E3717.toInt(), // dark wood
        'a' to 0xFFEBCBA0.toInt(), // town stucco wall
        'A' to 0xFFB8946E.toInt(), // wall shadow
        'v' to 0xFF5CE1FF.toInt()  // Bara-Bara blue aura spark
    )

    // =========================================================================
    // 1. BUGGY PIRATE ACROBAT (28x30) — striped circus suit + diving blades
    // =========================================================================

    private val acrobatRun1 = listOf(
        "............................",
        "...........kkkkk............",
        "..........kpppppk...........",
        ".........kPppppppk..........",
        ".........kkssssskk..........",
        ".........ksWskWsk...........",
        ".........kssssssk...........",
        "..........kddddk............",
        "........kkrrrrrrkk..........",
        ".......kwwrrrrrrwwk.........",
        "......krrwwrrrrwwrrk........",
        ".....ksskkwwwwwwkkssk.......",
        ".....kssk.krrrrk.kmmk.......",
        "......kk..kwwwwk..kmmk......",
        "..........krrrrk...kmmk.....",
        ".........kkyyyykk...kk......",
        ".........kppkkppk...........",
        "........kppk..kppk..........",
        ".......kppk....kppk.........",
        "......kPPk......kPPk........",
        ".....kttk........kttk.......",
        ".....kTTk........kTTk.......",
        "......kk..........kk........",
        "............................",
        "............................",
        "............................",
        "............................",
        "............................",
        "............................",
        "............................"
    )

    private val acrobatRun2 = listOf(
        "............................",
        "............................",
        "...........kkkkk............",
        "..........kpppppk...........",
        ".........kPppppppk..........",
        ".........kkssssskk..........",
        ".........ksWskWsk...........",
        ".........kssssssk...........",
        "..........kddddk............",
        "........kkrrrrrrkk..........",
        ".......kwwrrrrrrwwk.........",
        "......krrwwrrrrwwrrk........",
        "......ksskwwwwwwkssk........",
        ".......kk.krrrrk.kmmk.......",
        "..........kwwwwk..kmmk......",
        ".........kkyyyykk..kk.......",
        "..........kppkppk...........",
        ".........kppk.kppk..........",
        ".........kPPk..kPPk.........",
        "........kttk....kttk........",
        "........kTTk....kTTk........",
        ".........kk......kk.........",
        "............................",
        "............................",
        "............................",
        "............................",
        "............................",
        "............................",
        "............................",
        "............................"
    )

    private val acrobatDive = listOf(
        "............................",
        "..............kkkkk.........",
        ".............kpppppk........",
        "............kPppppppk.......",
        "............kkssssskk.......",
        "............ksWskWsk........",
        "...........kkssssssk........",
        ".........kkrrrrrrrrk........",
        "........kwwrrrrrrrrk........",
        ".......krrwwwwwwrrkk........",
        "......kppkrrrrrrkssk........",
        ".....kppk.kwwwwk.kmmk.......",
        "....kttk..kyyyyk..kmmk......",
        "....kTTk...kppk....kmmk.....",
        ".....kk...kttk......kmmk....",
        "..........kTTk.......kMk....",
        "...........kk.........kk....",
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
        "............................",
        "............................",
        "............................"
    )

    val acrobatRun: List<DirectionalFlashSprite> = listOf(
        PixelArtBuilder.buildFlashPair(PixelArtBuilder.alignBottom(acrobatRun1, 28), palette),
        PixelArtBuilder.buildFlashPair(PixelArtBuilder.alignBottom(acrobatRun2, 28), palette)
    )
    val acrobatDiveSprite: DirectionalFlashSprite =
        PixelArtBuilder.buildFlashPair(PixelArtBuilder.alignBottom(acrobatDive, 26), palette)

    // =========================================================================
    // 2. KNIFE THROWER (28x32) — purple vest, gold sash, gleaming throwing knife
    // =========================================================================

    private val throwerWalk1 = listOf(
        "............................",
        "..........kkkkkkk...........",
        ".........krrrrrrrk..........",
        "........kRrrrrrrrRk.........",
        ".........ksssssssk..........",
        ".........ksWskWsk...........",
        ".........kssrsssk...........",
        "..........kddddk............",
        "........kkppppppkk..........",
        ".......kpppyyyypppk.........",
        "......kppkppppppkppk........",
        "......ksskppyyppkssk........",
        ".......kk.kppppk.kmmk.......",
        "..........kppppk..kMk.......",
        ".........kkyyyykk..k........",
        ".........kbbbbbbk...........",
        "........kbbbkkbbbk..........",
        "........kbbk..kbbk..........",
        ".......kBBk....kBBk.........",
        ".......kttk....kttk.........",
        "......kTTTk....kTTTk........",
        "......kkkk......kkkk........",
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

    private val throwerThrow = listOf(
        "............................",
        "..........kkkkkkk...........",
        ".........krrrrrrrk..........",
        "........kRrrrrrrrRk.........",
        ".........ksssssssk..........",
        ".........ksWskWsk...........",
        ".........kssrsssk...........",
        "..........kddddk............",
        "........kkppppppkk..........",
        ".......kpppyyyypppk.........",
        "......kppkppppppksskkkk.....",
        "......ksskppyyppkssmmmmk....",
        ".......kk.kppppk.kkMMMMk....",
        "..........kppppk...kkkk.....",
        ".........kkyyyykk...........",
        ".........kbbbbbbk...........",
        "........kbbbkkbbbk..........",
        "........kbbk..kbbk..........",
        ".......kBBk....kBBk.........",
        ".......kttk....kttk.........",
        "......kTTTk....kTTTk........",
        "......kkkk......kkkk........",
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

    val throwerWalk: List<DirectionalFlashSprite> = listOf(
        PixelArtBuilder.buildFlashPair(PixelArtBuilder.alignBottom(throwerWalk1, 30), palette),
        PixelArtBuilder.buildFlashPair(PixelArtBuilder.alignBottom(throwerWalk1, 29), palette)
    )
    val throwerThrowSprite: DirectionalFlashSprite =
        PixelArtBuilder.buildFlashPair(PixelArtBuilder.alignBottom(throwerThrow, 30), palette)

    // =========================================================================
    // 3. ELITE CABAJI ON UNICYCLE (34x38) — long scarf, sword, unicycle wheel
    // =========================================================================

    private val cabajiRide1 = listOf(
        "..................................",
        "............kkkkkk................",
        "...........kbbbbbbk...............",
        "..........kbbbbbssbk..............",
        "..........kbbbbkWsk...............",
        "...........kssssssk...............",
        "......kkkkkkggggggk...............",
        ".....kggggggggggggk...............",
        "....kGGk...kwwwwwwk...............",
        "...kggk...kwwkkkkwwk....k.........",
        "..........ksskwwksskkkkmmk........",
        "...........kk.kwwk.kyymmmk........",
        "..............kggk..kmmmmk........",
        ".............kggggk..kMMk.........",
        ".............kbbbbk...kk..........",
        "............kbbkkbbk..............",
        "............kbbk.kbbk.............",
        "............kBBk..kBBk............",
        ".............kttk.kttk............",
        "..............kkkkkkk.............",
        "...............kMMMk..............",
        "..............khhhhhk.............",
        ".............khhMMhhhk............",
        "............khhMkkMhhhk...........",
        "............khhkMMkhhhk...........",
        "............khhMkkMhhhk...........",
        ".............khhMMhhhk............",
        "..............khhhhhk.............",
        "...............kkkkk..............",
        "..................................",
        "..................................",
        "..................................",
        "..................................",
        "..................................",
        "..................................",
        "..................................",
        "..................................",
        ".................................."
    )

    private val cabajiRide2 = listOf(
        "..................................",
        "............kkkkkk................",
        "...........kbbbbbbk...............",
        "..........kbbbbbssbk..............",
        "..........kbbbbkWsk...............",
        "...........kssssssk...............",
        ".....kkkkkkkggggggk...............",
        "....kgggggggggggggk...............",
        "...kGGk....kwwwwwwk...............",
        "..........kwwkkkkwwk....k.........",
        "..........ksskwwksskkkkmmk........",
        "...........kk.kwwk.kyymmmk........",
        "..............kggk..kmmmmk........",
        ".............kggggk..kMMk.........",
        ".............kbbbbk...kk..........",
        "............kbbkkbbk..............",
        ".............kbbkkbbk.............",
        ".............kBBkkBBk.............",
        ".............kttkkttk.............",
        "..............kkkkkkk.............",
        "...............kMMMk..............",
        "..............khhhhhk.............",
        ".............khhkkhhhk............",
        "............khhkMMkhhhk...........",
        "............khhMMMMhhhk...........",
        "............khhkMMkhhhk...........",
        ".............khhkkhhhk............",
        "..............khhhhhk.............",
        "...............kkkkk..............",
        "..................................",
        "..................................",
        "..................................",
        "..................................",
        "..................................",
        "..................................",
        "..................................",
        "..................................",
        ".................................."
    )

    private val cabajiDash = listOf(
        "..................................",
        "..................................",
        ".............kkkkkk...............",
        "............kbbbbbbk..............",
        "...........kbbbbbssbk.............",
        "...........kbbbbkWsk..............",
        "....kkkkkkkksssssssk..............",
        "...kggggggggggggggk...............",
        "..kGGk......kwwwwwwk..............",
        "...........kwwkkkkwwk.............",
        "...........ksskkkkksskkkkkkkkkkk..",
        "............kk.kyyymmmmmmmmmmmmmk.",
        "..............kggggkMMMMMMMMMMMMk.",
        ".............kbbbbk.kkkkkkkkkkkk..",
        "............kbbkkbbk..............",
        "............kBBk.kBBk.............",
        ".............kttk.kttk............",
        "..............kkkkkkk.............",
        "...............kMMMk..............",
        "..............khhhhhk.............",
        ".............khhMMhhhk............",
        "............khhMkkMhhhk...........",
        "............khhkMMkhhhk...........",
        "............khhMkkMhhhk...........",
        ".............khhMMhhhk............",
        "..............khhhhhk.............",
        "...............kkkkk..............",
        "..................................",
        "..................................",
        "..................................",
        "..................................",
        "..................................",
        "..................................",
        "..................................",
        "..................................",
        "..................................",
        "..................................",
        ".................................."
    )

    private val cabajiFire = listOf(
        "..................................",
        "............kkkkkk................",
        "...........kbbbbbbk...............",
        "..........kbbbbbssbk..............",
        "..........kbbbbkWsk...............",
        "...........kssssroook.............",
        "......kkkkkkggggoooyok............",
        ".....kgggggggggggroook............",
        "....kGGk...kwwwwwwkk..............",
        "..........kwwkkkkwwk..............",
        "..........ksskwwkssk..............",
        "...........kk.kwwk.kk.............",
        "..............kggk................",
        ".............kggggk...............",
        ".............kbbbbk...............",
        "............kbbkkbbk..............",
        "............kBBk.kBBk.............",
        ".............kttk.kttk............",
        "..............kkkkkkk.............",
        "...............kMMMk..............",
        "..............khhhhhk.............",
        ".............khhMMhhhk............",
        "............khhMkkMhhhk...........",
        "............khhkMMkhhhk...........",
        "............khhMkkMhhhk...........",
        ".............khhMMhhhk............",
        "..............khhhhhk.............",
        "...............kkkkk..............",
        "..................................",
        "..................................",
        "..................................",
        "..................................",
        "..................................",
        "..................................",
        "..................................",
        "..................................",
        "..................................",
        ".................................."
    )

    val cabajiRide: List<DirectionalFlashSprite> = listOf(
        PixelArtBuilder.buildFlashPair(PixelArtBuilder.alignBottom(cabajiRide1, 36), palette),
        PixelArtBuilder.buildFlashPair(PixelArtBuilder.alignBottom(cabajiRide2, 36), palette)
    )
    val cabajiDashSprite: DirectionalFlashSprite =
        PixelArtBuilder.buildFlashPair(PixelArtBuilder.alignBottom(cabajiDash, 36), palette)
    val cabajiFireSprite: DirectionalFlashSprite =
        PixelArtBuilder.buildFlashPair(PixelArtBuilder.alignBottom(cabajiFire, 36), palette)

    // =========================================================================
    // 4. BOSS CAPTAIN BUGGY THE CLOWN (38x42)
    //    Big pirate tricorn hat, blue hair, red clown nose, striped shirt,
    //    red cape, knives between fingers!
    // =========================================================================

    private val buggyWalk1 = listOf(
        "......................................",
        "...........kkkkkkkkkkkkkk.............",
        ".........kkbbbbbbbbbbbbbbkk...........",
        "........kbbbbbbwwwwwwbbbbbbk..........",
        ".......kbbbbbwwkwwwwkwwbbbbbk.........",
        "......kbbbbbbwwrrwwrrwwbbbbbbk........",
        "......kbbbbbbbbwwwwwwbbbbbbbbk........",
        "......kkbbbbbbbbbbbbbbbbbbbbkk........",
        ".......kcckkkkkkkkkkkkkkkkcck.........",
        "......kccckssssskkkssssskccck.........",
        "......kcckssWskssssskWssskcck.........",
        ".....kccCksssskrrrrrksssskCcck........",
        ".....kccCksssskrrrrrksssskCcck........",
        "......kcckssssskrrrkksssskcck.........",
        ".......kk.ksswwkrrrkkwwsskk...........",
        "........kkrrkkkssssskkkrrkk...........",
        ".......krrrrkrrrrrrrrrkrrrrk..........",
        "......krrrrkwwwwwwwwwwwkrrrrk.........",
        ".....krrrrkrrrrrrrrrrrrrkrrrrk........",
        ".....kRrrkwwwwwwwwwwwwwwwkrrRk........",
        "....kRrrkwwkkrrrrrrrrrkkwwkrrRk.......",
        "....kRrrkwwk.kwwwwwwwk.kwwkmmmk.......",
        "....kRrrk.k..krrrrrrrk..kkmmmmk.......",
        "....kRrrk...kkyyyyyyykk...kMMmk.......",
        "....kRrrk...kgggggggggk....kkk........",
        "....kRrrk...kbbbbbbbbbk...............",
        ".....kRRk..kbbbbkkbbbbbk..............",
        ".....kRRk..kbbbk..kbbbbk..............",
        "......kk...kBBk....kBBBk..............",
        "..........ktttk....kttttk.............",
        ".........kTTTTk....kTTTTTk............",
        ".........kkkkk......kkkkkk............",
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
    )

    private val buggyWalk2 = listOf(
        "......................................",
        "......................................",
        "...........kkkkkkkkkkkkkk.............",
        ".........kkbbbbbbbbbbbbbbkk...........",
        "........kbbbbbbwwwwwwbbbbbbk..........",
        ".......kbbbbbwwkwwwwkwwbbbbbk.........",
        "......kbbbbbbwwrrwwrrwwbbbbbbk........",
        "......kbbbbbbbbwwwwwwbbbbbbbbk........",
        "......kkbbbbbbbbbbbbbbbbbbbbkk........",
        ".......kcckkkkkkkkkkkkkkkkcck.........",
        "......kccckssssskkkssssskccck.........",
        "......kcckssWskssssskWssskcck.........",
        ".....kccCksssskrrrrrksssskCcck........",
        ".....kccCksssskrrrrrksssskCcck........",
        "......kcckssssskrrrkksssskcck.........",
        ".......kk.ksswwkrrrkkwwsskk...........",
        "........kkrrkkkssssskkkrrkk...........",
        ".......krrrrkrrrrrrrrrkrrrrk..........",
        "......krrrrkwwwwwwwwwwwkrrrrk.........",
        ".....krrrrkrrrrrrrrrrrrrkrrrrk........",
        ".....kRrrkwwwwwwwwwwwwwwwkrrRk........",
        "....kRrrkwwkkrrrrrrrrrkkwwkrrRk.......",
        "....kRrrkwwk.kwwwwwwwk.kwwkmmmk.......",
        "....kRrrk.k..krrrrrrrk..kkmmmmk.......",
        "....kRrrk...kkyyyyyyykk...kMMmk.......",
        "....kRrrk...kgggggggggk....kkk........",
        "....kRrrk...kbbbbbbbbbk...............",
        ".....kRRk...kbbbkkbbbbk...............",
        ".....kRRk...kbbk..kbbbk...............",
        "......kk....kBBk..kBBBk...............",
        "...........ktttk..ktttk...............",
        "...........kTTTk..kTTTk...............",
        "...........kkkk....kkkk...............",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................"
    )

    private val buggyWindup = listOf(
        "......................................",
        "...........kkkkkkkkkkkkkk.............",
        ".........kkbbbbbbbbbbbbbbkk...........",
        "........kbbbbbbwwwwwwbbbbbbk..........",
        ".......kbbbbbwwkwwwwkwwbbbbbk.........",
        "......kbbbbbbwwrrwwrrwwbbbbbbk........",
        "......kbbbbbbbbwwwwwwbbbbbbbbk........",
        "......kkbbbbbbbbbbbbbbbbbbbbkk........",
        ".......kcckkkkkkkkkkkkkkkkcck.........",
        "......kccckssssskkkssssskccck.........",
        "......kcckssWskssssskWssskcck.........",
        ".....kccCksssskrrrrrksssskCcck........",
        ".....kccCksssskrrrrrksssskCcck........",
        "......kcckssssskrrrkksssskcck.........",
        ".....kmmk.ksswwkrrrkkwwsskk.kmmk......",
        "....kmmmk.krrkkssssskkrrkk.kmmmk......",
        "....kwwk.krrkrrrrrrrrrkrrk.kwwk.......",
        ".....kk.krrkwwwwwwwwwwwkrrk.kk........",
        ".......krrkrrrrrrrrrrrrrkrrk..........",
        "......kRrkwwwwwwwwwwwwwwwkrRk.........",
        ".....kRrk.krrrrrrrrrrrrrk.krRk........",
        ".....kRrk..kwwwwwwwwwwwk..krRk........",
        ".....kRrk...kkyyyyyyykk...krRk........",
        ".....kRRk...kgggggggggk...kRRk........",
        "......kk....kbbbbbbbbbk....kk.........",
        "...........kbbbbkkbbbbbk..............",
        "..........kbbbk....kbbbbk.............",
        "..........kBBk......kBBBk.............",
        ".........ktttk......kttttk............",
        "........kTTTTk......kTTTTTk...........",
        "........kkkkk........kkkkkk...........",
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
    )

    private val buggyCannonPose = listOf(
        "......................................",
        "...........kkkkkkkkkkkkkk.............",
        ".........kkbbbbbbbbbbbbbbkk...........",
        "........kbbbbbbwwwwwwbbbbbbk..........",
        ".......kbbbbbwwkwwwwkwwbbbbbk.........",
        "......kbbbbbbwwrrwwrrwwbbbbbbk........",
        "......kbbbbbbbbwwwwwwbbbbbbbbk........",
        "......kkbbbbbbbbbbbbbbbbbbbbkk........",
        ".......kcckkkkkkkkkkkkkkkkcck.........",
        "......kccckssssskkkssssskccck.........",
        "......kcckssWskssssskWssskcck.........",
        ".....kccCksssskrrrrrksssskCcck........",
        ".....kccCksssskrrrrrksssskCcck........",
        "......kcckssssskrrrkksssskcck.........",
        ".......kk.ksswwkrrrkkwwsskk...........",
        "........kkrrkkkssssskkkrrkk...........",
        ".......krrrrkrrrrrrrrrkrrrrk..........",
        "......krrrrkwwwwwwwwwwwkrrrrk.........",
        ".....krrrrkrrrrrrrrrrrrrkrrvvvk.......",
        ".....kRrrkwwwwwwwwwwwwwwwkvvvvvk......",
        "....kRrrk.krrrrrrrrrrrrrk.kvvvvk......",
        "....kRrrk..kwwwwwwwwwwwk...kvvk.......",
        "....kRrrk...kkyyyyyyykk...............",
        "....kRrrk...kgggggggggk...............",
        "....kRRk....kbbbbbbbbbk...............",
        ".....kk....kbbbbkkbbbbbk..............",
        "..........kbbbk....kbbbbk.............",
        "..........kBBk......kBBBk.............",
        ".........ktttk......kttttk............",
        "........kTTTTk......kTTTTTk...........",
        "........kkkkk........kkkkkk...........",
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
    )

    /** Grounded feet + Bara-Bara swirling aura left on the street during Bara-Bara Festival */
    private val buggySplitFeet = listOf(
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
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        "......................................",
        ".............vv....vv.................",
        "...........vvvvvvvvvvvv...............",
        "...........kkbbbbbbbbkk...............",
        "..........kbbbbkkbbbbbk...............",
        "..........kbbbk..kbbbbk...............",
        "..........kBBk....kBBBk...............",
        ".........ktttk....kttttk..............",
        "........kTTTTk....kTTTTTk.............",
        "........kkkkk......kkkkkk.............",
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
    )

    val buggyWalk: List<DirectionalFlashSprite> = listOf(
        PixelArtBuilder.buildFlashPair(PixelArtBuilder.alignBottom(buggyWalk1, 40), palette),
        PixelArtBuilder.buildFlashPair(PixelArtBuilder.alignBottom(buggyWalk2, 40), palette)
    )
    val buggyWindupSprite: DirectionalFlashSprite =
        PixelArtBuilder.buildFlashPair(PixelArtBuilder.alignBottom(buggyWindup, 40), palette)
    val buggyCannonSprite: DirectionalFlashSprite =
        PixelArtBuilder.buildFlashPair(PixelArtBuilder.alignBottom(buggyCannonPose, 40), palette)
    val buggySplitFeetSprite: DirectionalFlashSprite =
        PixelArtBuilder.buildFlashPair(PixelArtBuilder.alignBottom(buggySplitFeet, 40), palette)

    // =========================================================================
    // 5. BARA-BARA DETACHED FLYING PARTS (HEAD, TORSO, KNIFE HAND)
    // =========================================================================

    val buggyPartHead: DirectionalSprite = PixelArtBuilder.buildPair(
        listOf(
            "....kkkkkkkkkk....",
            "..kkbbbbbbbbbbkk..",
            ".kbbbbwwwwwwbbbbk.",
            ".kbbbwwrrwwrrbbbk.",
            ".kkbbbbbbbbbbbbkk.",
            "..kcckkkkkkkkcck..",
            ".kcckssWskkWsscck.",
            ".kccksskrrrrkssck.",
            "..kcckskrrrrksck..",
            "...kk.kssrrsskk...",
            "......kvvvvvvk....",
            ".......kkkkkk....."
        ),
        palette
    )

    val buggyPartTorso: DirectionalSprite = PixelArtBuilder.buildPair(
        listOf(
            "...vvvvvvvvvv...",
            "..krrrrrrrrrrk..",
            ".kwwwwwwwwwwwwk.",
            ".krrrrrrrrrrrrk.",
            ".kwwwwwwwwwwwwk.",
            "..kkyyyyyyyykk..",
            "...kggggggggk...",
            "....kvvvvvvk....",
            ".....kkkkkk....."
        ),
        palette
    )

    val buggyPartHand: DirectionalSprite = PixelArtBuilder.buildPair(
        listOf(
            ".......kmmk...",
            ".vvvkkwwkmmmk.",
            "vvkyywwwwmmmmk",
            "vvkyywwwwmmmmk",
            ".vvvkkwwkmmmk.",
            ".......kMMk..."
        ),
        palette
    )

    // =========================================================================
    // 6. LEVEL 2 PROJECTILES (THROWING KNIFE, CABAJI FLAME, BUGGY FIST, BUGGY BALL)
    // =========================================================================

    val throwingKnife: DirectionalSprite = PixelArtBuilder.buildPair(
        listOf(
            "........kk..",
            "kkkyykmmmmmk",
            "kkkyykMMMMMk",
            "........kk.."
        ),
        palette
    )

    val cabajiFlame: DirectionalSprite = PixelArtBuilder.buildPair(
        listOf(
            "....rrrr....",
            "..rrooooor..",
            ".rooyyyyyor.",
            "rooyywwyyoor",
            "rooyyyyyyoor",
            ".rroooooorr.",
            "...rrrrrr..."
        ),
        palette
    )

    val buggyFist: DirectionalSprite = PixelArtBuilder.buildPair(
        listOf(
            ".........kkmmk..",
            "..vvkkwwwwwmmmk.",
            ".vvkyywwwwwmmmmk",
            "vvvkyywwwwwmmmmk",
            ".vvkyywwwwwmmmmk",
            "..vvkkwwwwwmmmk.",
            ".........kkMMk.."
        ),
        palette
    )

    /** Iconic Crimson Buggy Ball with clown skull emblem & glowing fuse */
    val buggyBall: ImageBitmap = PixelArtBuilder.build(
        listOf(
            ".......yy.......",
            "......ywwy......",
            ".......ko.......",
            "....kkkRkkk.....",
            "..kkrrrrrrrkk...",
            ".krrrrwwwrrrrk..",
            ".krrrwwwwwrrrk..",
            "krrrwkwwwkwrrrk.",
            "krrrrwwrwwrrrrk.",
            "krrrrrwwwrrrrrk.",
            ".krrrrwkrrrrrk..",
            ".kRRrrrrrrrRRk..",
            "..kkRRRRRRRkk...",
            "....kkkkkkk....."
        ),
        palette
    )

    // =========================================================================
    // 7. ORANGE TOWN SUNSET SCENERY SPRITES (CIRCUS TENT, RUINED HOUSES, SHIP)
    // =========================================================================

    val circusTent: ImageBitmap = PixelArtBuilder.build(
        listOf(
            ".......................rryy.....................",
            ".......................rrrr.....................",
            ".......................k........................",
            "......................kkk.......................",
            ".....................krrrk......................",
            "....................krrwrrk.....................",
            "...................krrwwwrrk....................",
            "..................krrwwrwwrrk...................",
            ".................krrwwrrrwwrrk..................",
            "................krrwwrrrrrwwrrk.................",
            "...............krrwwrrrwwrrrwwrk................",
            "..............krrwwrrrwwwwrrrwwrk...............",
            ".............krrwwrrrwwrrwwrrrwwrk..............",
            "............krrwwrrrwwrrrrwwrrrwwrk.............",
            "...........krrwwrrrwwrrrrrrwwrrrwwrk............",
            "..........krrwwrrrwwrrrwwrrrwwrrrwwrk...........",
            ".........krrwwrrrwwrrrwwwwrrrwwrrrwwrk..........",
            "........krrwwrrrwwrrrwwrrwwrrrwwrrrwwrk.........",
            ".......krrwwrrrwwrrrwwrrrrwwrrrwwrrrwwrk........",
            "......krrwwrrrwwrrrwwrrrrrrwwrrrwwrrrwwrk.......",
            ".....kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk......",
            ".....kyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyk......",
            ".....krwrrwwrrwwrrwwrrkkkkrrwwrrwwrrwwrrwk......",
            ".....krwrrwwrrwwrrwwkBBBBBBkwwrrwwrrwwrrwk......",
            ".....krwrrwwrrwwrrwkBBBBBBBBkwwrrwwrrwwrrk......",
            ".....krwrrwwrrwwrrwkBBBBBBBBkwwrrwwrrwwrrk......",
            ".....krwrrwwrrwwrrwkBBBBBBBBkwwrrwwrrwwrrk......",
            ".....krwrrwwrrwwrrwkBBBBBBBBkwwrrwwrrwwrrk......",
            ".....krwrrwwrrwwrrwkBBBBBBBBkwwrrwwrrwwrrk......",
            ".....krwrrwwrrwwrrwkBBBBBBBBkwwrrwwrrwwrrk......",
            ".....krwrrwwrrwwrrwkBBBBBBBBkwwrrwwrrwwrrk......",
            ".....kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk......"
        ),
        palette
    )

    val ruinedHouseA: ImageBitmap = PixelArtBuilder.build(
        listOf(
            "......kkkkkkkkkk...............",
            "....kkooooooooooookk...........",
            "...kOooooooooooooooOk..........",
            "..kOOoooooooooooooooOOk........",
            ".kkkkkkkkkkkkkkkkkkk..kk.......",
            "..kaaaaaaaaaaaaaaaaak..........",
            "..kaAAaaaakkkkaaaaaak..........",
            "..kaaaaaakyyyykaaaaak..........",
            "..kaaaaaakyyyykaaaAAk..........",
            "..kaaaaaakkkkkkaaaaak..........",
            "..kaaaAAaaaaaaaaaaaak..........",
            "..kaaaaaaaaaaaaakkkka..........",
            "..kaaakkkkkaaaaakkkka..........",
            "..kaakTTTTTkaaaaaaaak..........",
            "..kaakTTTTTkaaaAAaaak..........",
            "..kaakTTTTTkaaaaaaaak..........",
            "..kaakTTTTTkaaaaaaaak..........",
            "..kaakTTTTTkaaaaaaaak..........",
            "..kkkkkkkkkkkkkkkkkkk.........."
        ),
        palette
    )

    val ruinedHouseB: ImageBitmap = PixelArtBuilder.build(
        listOf(
            ".............kkkkkkkkkk........",
            "...........kkoooooooooookk.....",
            "..........kOoooooooooooooOk....",
            "....kk...kOOooooooooooooooOk...",
            "...kaak.kkkkkkkkkkkkkkkkkkkkk..",
            "...kaaaaaaaaaaaaaaaaAAaaaaaaak.",
            "...kaAAaaaaakkkkaaaakkkkaaaaak.",
            "...kaaaaaaakyyyykaakyyyykaaaak.",
            "...kaaaaaaakkkkkkaakkkkkkaaaak.",
            "...kaaaaaaaaaaaaaaaaaaaaaAAaak.",
            "...kaaaaaaaaaaakkkkkaaaaaaaaak.",
            "...kaaaAAaaaaakTTTTTkaaaaaaaak.",
            "...kaaaaaaaaaakTTTTTkaaaaaaaak.",
            "...kaaaaaaaaaakTTTTTkaaaaaaaak.",
            "...kaaaaaaaaaakTTTTTkaaaaaaaak.",
            "...kkkkkkkkkkkkkkkkkkkkkkkkkkk."
        ),
        palette
    )

    /** Buggy's Big Top pirate ship on the sunset horizon with red clown figurehead */
    val buggyShip: ImageBitmap = PixelArtBuilder.build(
        listOf(
            ".................rryy...................",
            ".................rrrr...................",
            ".................k......................",
            "...............kkwrrkk..................",
            "..............kwwrrrrwk.................",
            ".............kwwrrwwrrwk................",
            "............kwwrrwwwwrrwk...............",
            "............kwwrrwkkwrrwk...............",
            "............kwwrrwrrwrrwk...............",
            "............kwwrrwwwwrrwk...............",
            ".............kwwrrrrrrwk................",
            "..............kkkkkkkkk.................",
            ".................k......................",
            "....kkkkkkkkkkkkkkkkkkkkkkkkkkk.........",
            "...ktttttttttttttttttttttttttttk..kkk...",
            "...kTtttttyyyyyyyyyyyyyyyyyttttk.ksssk..",
            "....kttttttttttttttttttttttttttkkssrrk..",
            ".....kTTttttttttttttttttttttttTTkssrrk..",
            "......kTTTTTTTTTTTTTTTTTTTTTTTTk.kkkk...",
            ".......kkkkkkkkkkkkkkkkkkkkkkkk........."
        ),
        palette
    )
}
