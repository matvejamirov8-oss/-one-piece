package com.example.game.sprites

import androidx.compose.ui.graphics.ImageBitmap

/**
 * 16-bit Pixel Art sprite sheets for Level 3 (Arlong Park):
 * - Fishman Bouncer (muscular shover fishman)
 * - Fishman Chew Sniper (balcony water-bullet marksman)
 * - Hatchan (6-sword octopus fishman mini-boss with spinning whirlwind frames)
 * - Boss Arlong Saw (Phase 1 courtyard shark-fishman + Shark On Darts torpedo + Phase 2 Kiribati saw-blade)
 * - Projectiles & Interactive Props: Water Bullet, Arlong Stone Gate, Map Bookshelf, Stone Pillar, Nami's Desk
 */
class ArlongParkSpriteSheet {

    private val palette: Map<Char, Int> = mapOf(
        '.' to 0x00000000,
        'k' to 0xFF181425.toInt(), // crisp dark outline
        // Fishman skins
        'b' to 0xFF4A90C8.toInt(), // Arlong / bouncer shark blue
        'B' to 0xFF2B5B8A.toInt(), // deep shark blue shade
        'l' to 0xFF79C4EB.toInt(), // light blue / Chew skin
        'L' to 0xFF468DB8.toInt(), // light blue shade
        'p' to 0xFFF28B9A.toInt(), // Hatchan coral-pink octopus skin
        'P' to 0xFFC2586B.toInt(), // Hatchan dark pink shade
        'u' to 0xFF8B6EC9.toInt(), // Arlong lavender-blue skin highlight
        'U' to 0xFF5D4694.toInt(), // Arlong deep shadow
        // Clothing & accents
        'w' to 0xFFF7F9FC.toInt(), // white cloth / teeth / map paper
        'W' to 0xFFD4DCE8.toInt(), // white shade
        'r' to 0xFFE53844.toInt(), // Arlong crimson vest / sash / shark emblem
        'R' to 0xFF9C1B28.toInt(), // deep crimson
        'y' to 0xFFFFD84D.toInt(), // gold jewelry / Hatchan hair / lamp
        'Y' to 0xFFD1921D.toInt(), // gold shade
        'o' to 0xFFFF8826.toInt(), // orange tropical shirt accents
        'g' to 0xFF38C958.toInt(), // tropical palm / bandana green
        'G' to 0xFF1E7A38.toInt(), // deep green
        'n' to 0xFF27324A.toInt(), // dark navy pants
        'N' to 0xFF161C2E.toInt(), // deeper navy
        // Blades & water
        'm' to 0xFFE4EFFA.toInt(), // bright blade steel (Kiribati / Hatchan swords)
        'M' to 0xFF8E9FB5.toInt(), // mid blade steel
        'h' to 0xFF4A566B.toInt(), // dark iron / stone shadow
        'c' to 0xFF5CE1FF.toInt(), // bright water cyan
        'C' to 0xFF1985D4.toInt(), // deep water blue
        // Architecture & furniture
        's' to 0xFFD2C9BA.toInt(), // warm limestone light
        'S' to 0xFFA39887.toInt(), // limestone mid
        'd' to 0xFF6B6257.toInt(), // stone dark crack
        't' to 0xFFB8783E.toInt(), // mahogany wood light
        'T' to 0xFF7A4820.toInt(), // wood dark
        'a' to 0xFFFFF2C8.toInt(), // parchment map paper
        'A' to 0xFFDCC38A.toInt()  // parchment shade
    )

    // =========================================================================
    // 1. FISHMAN BOUNCER (28x32) — bulky blue fishman who shoves into the pool
    // =========================================================================

    private val bouncerWalk1 = listOf(
        "............................",
        "...........kkkkk............",
        "..........kbbBbbk...........",
        ".........kbbbbbbbk..........",
        ".........kbwkbwkbk..........",
        ".........kbbwwwbbk..........",
        ".........kBbbbbbBk..........",
        "........kkbbbbbbbkk.........",
        ".......kbbbrrrrrbbbk........",
        "......kbbbbbrrrbbbbbk.......",
        "......kbbkrrrrrrrkbBk.......",
        ".....kbbbkrrrrrrrkbbk.......",
        ".....kBbbkrrrrrrrkbbk.......",
        "......kkkkyyyyyyykkkk.......",
        ".........knnnnnnnk..........",
        "........knnnkknnnk..........",
        "........knnk..knnk..........",
        ".......kNNk....kNNk.........",
        ".......kbbk....kbbk.........",
        "......kBBBk....kBBBk........",
        ".......kkk......kkk.........",
        "............................"
    )

    private val bouncerWalk2 = listOf(
        "............................",
        "............................",
        "...........kkkkk............",
        "..........kbbBbbk...........",
        ".........kbbbbbbbk..........",
        ".........kbwkbwkbk..........",
        ".........kbbwwwbbk..........",
        ".........kBbbbbbBk..........",
        "........kkbbbbbbbkk.........",
        ".......kbbbrrrrrbbbk........",
        "......kbbbbbrrrbbbbbk.......",
        "......kbbkrrrrrrrkbBk.......",
        "......kBbkrrrrrrrkbbk.......",
        ".......kkkyyyyyyykkk........",
        ".........knnnnnnnk..........",
        ".........knnkknnk...........",
        "........knnk..knnk..........",
        "........kNNk..kNNk..........",
        "........kbbk..kbbk..........",
        ".......kBBBk..kBBBk.........",
        "........kkk....kkk..........",
        "............................"
    )

    private val bouncerShove = listOf(
        "............................",
        ".............kkkkk..........",
        "............kbbBbbk.........",
        "...........kbbbbbbbk........",
        "...........kbwkbwkbk........",
        "...........kbbwwwbbk........",
        "..........kkBbbbbbBk........",
        "........kkbbbrrrrrbkk.......",
        ".......kbbbbbrrrrrbbbkk.....",
        "......kBbbbkkrrrrrbbbbbbk...",
        ".......kkkk.krrrrrbbBbbbwk..",
        "............kyyyyykbbbbwwk..",
        "...........knnnnnnkbbkkk....",
        "..........knnnkknnnk........",
        ".........knnk....knnk.......",
        "........kNNk......kNNk......",
        "........kbbk......kbbk......",
        ".......kBBBk......kBBBk.....",
        "........kkk........kkk......",
        "............................",
        "............................",
        "............................"
    )

    // =========================================================================
    // 2. FISHMAN CHEW SNIPER (26x28) — balcony sniper with protruding water lips
    // =========================================================================

    private val chewIdle = listOf(
        "..........................",
        ".........kkkkkk...........",
        "........kllllllk..........",
        ".......klwklwllk..........",
        ".......klllllllkkk........",
        ".......kllllllloook.......",
        ".......kLllllllkkk........",
        "........kkllllkk..........",
        ".......kggyyyyggk.........",
        "......kggggrrggggk........",
        "......kllkggggkllk........",
        "......kllkggggkllk........",
        ".......kkkyyyykkk.........",
        "........knnnnnnk..........",
        "........knnkknnk..........",
        ".......knnk..knnk.........",
        ".......kNNk..kNNk.........",
        ".......kllk..kllk.........",
        "......kLLLk..kLLLk........",
        ".......kkk....kkk.........",
        ".........................."
    )

    private val chewShoot = listOf(
        "..........................",
        "..........kkkkkk..........",
        ".........kllllllk.........",
        "........klwklwllk.........",
        "........klllllllkkkk......",
        "........kllllllloooock....",
        "........kLllllllkkkcck....",
        ".........kkllllkk...kk....",
        "........kggyyyyggk........",
        ".......kggggrrggggk.......",
        ".......kllkggggkllk.......",
        "........kk.kggk.kk........",
        ".........kkyyyykk.........",
        ".........knnnnnnk.........",
        "........knnk..knnk........",
        ".......kNNk....kNNk.......",
        ".......kllk....kllk.......",
        "......kLLLk....kLLLk......",
        ".......kkk......kkk.......",
        "..........................",
        ".........................."
    )

    // =========================================================================
    // 3. HATCHAN MINI-BOSS (36x34) — 6-sword coral-pink octopus fishman
    // =========================================================================

    private val hatchanWalk1 = listOf(
        "....................................",
        "..............kkyykk................",
        ".............kyyyyyyk...............",
        "............kkyyppyykk..............",
        "............kppppppppk..............",
        "............kpwkpwpppk..............",
        "............kppppppppk..............",
        ".............kPppppPk...............",
        "....kmk.....kkppppppkk.....kmk......",
        ".....kmk...kppnnnnnnppk...kmk.......",
        "......kmk.kppkknnnnkkppk.kmk........",
        "..kmmmkkpkkppknnnnnnkppkkpkkmmmk....",
        "...kkkkkppppkknnnnnnkkppppkkkkk.....",
        ".......kPpppkknnnnnnkkpppPk.........",
        "....kmkkppppkkyyyyyykkppppkkmk......",
        ".....kmkPppk..knnnnk..kppPkmk.......",
        "......kkkkk..knnkknnk..kkkkk........",
        "............knnk..knnk..............",
        "...........kppk....kppk.............",
        "..........kppk......kppk............",
        "..........kPPk......kPPk............",
        "...........kk........kk.............",
        "...................................."
    )

    private val hatchanWalk2 = listOf(
        "....................................",
        "....................................",
        "..............kkyykk................",
        ".............kyyyyyyk...............",
        "............kkyyppyykk..............",
        "............kppppppppk..............",
        "............kpwkpwpppk..............",
        "............kppppppppk..............",
        ".............kPppppPk...............",
        ".....kmk....kkppppppkk....kmk.......",
        "......kmk..kppnnnnnnppk..kmk........",
        "...kmmkkpkkppkknnnnkkppkkpkkmmk.....",
        "....kkkkppppkknnnnnnkkppppkkkk......",
        ".......kPpppkknnnnnnkkpppPk.........",
        ".....kmkppppkkyyyyyykkppppkmk.......",
        "......kkkPpk..knnnnk..kpPkkk........",
        ".............knnkknnk...............",
        "............knnk..knnk..............",
        "............kppk..kppk..............",
        "...........kPPk....kPPk.............",
        "............kk......kk..............",
        "....................................",
        "...................................."
    )

    private val hatchanSlash = listOf(
        "....................................",
        "..............kkyykk................",
        ".............kyyyyyyk...............",
        "............kkyyppyykk..............",
        "............kppppppppk..............",
        "............kpwkpwpppk...kmmmk......",
        "............kppppppppk..kmmmmmk.....",
        ".............kPppppPk..kmmMMmmk.....",
        "..........kkpppppppppkkmmk..kk......",
        ".........kppnnnnnnpppppkkmmmmmk.....",
        "........kppkknnnnkkppppkmmmmmmmk....",
        "........kppknnnnnnkpppppkkmmMMk.....",
        ".........kkPnnnnnnkppppppkmmmmmk....",
        "..........kkyyyyyykppPppk.kmmmmk....",
        "...........knnnnnnk.kkkk...kkkk.....",
        "..........knnk..knnk................",
        ".........kppk....kppk...............",
        ".........kPPk....kPPk...............",
        "..........kk......kk................",
        "....................................",
        "....................................",
        "....................................",
        "...................................."
    )

    private val hatchanSpin1 = listOf(
        "....................................",
        "..............kkyykk................",
        ".............kyyyyyyk...............",
        "....kmmmmkkkkkppppppkkkkkmmmmk......",
        "..kmmmmmmMMmmkppppppkmmMMmmmmmmk....",
        ".kmmwwwwmmmmmkpPppPpkmmmmmwwwwmmk...",
        "..kkkkkkkkkppkknnnnkkppkkkkkkkkk....",
        "......kmmmmppknnnnnnkppmmmmk........",
        "...kmmmmmmMppknnnnnnkppMmmmmmmk.....",
        "..kmmwwwmmmppkkyyyykkppmmmwwwmmk....",
        "...kkkkkkkkPpkknnnnkkpPkkkkkkkk.....",
        ".......kmmmmmknnnnnnkmmmmmk.........",
        ".....kmmmmMMmknnkknnkmmMMmmmmk......",
        "......kkkkkkkkppk.kppkkkkkkkk.......",
        "............kppk...kppk.............",
        "............kPPk...kPPk.............",
        ".............kk.....kk..............",
        "....................................",
        "....................................",
        "....................................",
        "....................................",
        "....................................",
        "...................................."
    )

    private val hatchanSpin2 = listOf(
        "....................................",
        "..............kkyykk................",
        ".............kyyyyyyk...............",
        "..kmmmmmmkkkkkppppppkkkkkmmmmmmk....",
        ".kmmwwwwMMmmmkppppppkmmmMMwwwwmmk...",
        "..kkkkkkkkpppkkPppPkkpppkkkkkkkk....",
        "....kmmmmmmppknnnnnnkppmmmmmmk......",
        ".kmmmmwwwMMppknnnnnnkppMMwwwmmmmk...",
        "..kkkkkkkkkppkkyyyykkppkkkkkkkkk....",
        ".....kmmmmPppkknnnnkkppPmmmmk.......",
        "...kmmmmMMmmkknnnnnnkmmMMmmmmk......",
        "....kkkkkkkkkknnkknnkkkkkkkkk.......",
        "............kppk..kppk..............",
        "...........kppk....kppk.............",
        "...........kPPk....kPPk.............",
        "............kk......kk..............",
        "....................................",
        "....................................",
        "....................................",
        "....................................",
        "....................................",
        "....................................",
        "...................................."
    )

    // =========================================================================
    // 4. BOSS ARLONG SAW (44x44) — Phase 1 Shark + Torpedo + Phase 2 Kiribati!
    // =========================================================================

    private val arlongWalk1 = listOf(
        "............................................",
        "..................kkkkk.....................",
        ".................knnnnnk....................",
        "................knnnnnnnk...................",
        "...............kbbuuuuubbk..................",
        "..............kbbbwkbbwkbbkkkkk.............",
        "..............kbbbuuuuuubbbMmMmk............",
        "..............kbbbwwwwwwbbbkkk..............",
        "..............kBbbwkwkwkbbBk................",
        ".............kkBbbbbbbbbbbBkk...............",
        "...........kkrrrooooyyoooorrrkk.............",
        "..........krrrrrooyyyyyyoorrrrrk............",
        ".........kbbkrrrooooyyoooorrrkbbk...........",
        "........kbbbkrrrrrooyyoorrrrrkbbbk..........",
        "........kBbbkrrrrbbuuuuubrrrrkbbBk..........",
        "........kbbbkrrrbbbuuuubbrrrrkbbbk..........",
        ".........kkkkrrrkbbbuubbkrrrrkkkk...........",
        "..........kkrrrrkBbbbbbbkrrrrkk.............",
        "...........kkyyykbbBuubbkkyyyk..............",
        "...........knnnnnnnnnnnnnnnnnk..............",
        "..........knnnnnnnkkkkknnnnnnnk.............",
        "..........knnnnnnk.....knnnnnnk.............",
        ".........kNNNNNNk.......kNNNNNNk............",
        ".........kbbbbbbk.......kbbbbbbk............",
        "........kBBBBBBBk.......kBBBBBBBk...........",
        "........ktttttttk.......ktttttttk...........",
        ".........kkkkkkk.........kkkkkkk............",
        "............................................"
    )

    private val arlongWalk2 = listOf(
        "............................................",
        "............................................",
        "..................kkkkk.....................",
        ".................knnnnnk....................",
        "................knnnnnnnk...................",
        "...............kbbuuuuubbk..................",
        "..............kbbbwkbbwkbbkkkkk.............",
        "..............kbbbuuuuuubbbMmMmk............",
        "..............kbbbwwwwwwbbbkkk..............",
        "..............kBbbwkwkwkbbBk................",
        ".............kkBbbbbbbbbbbBkk...............",
        "...........kkrrrooooyyoooorrrkk.............",
        "..........krrrrrooyyyyyyoorrrrrk............",
        ".........kbbkrrrooooyyoooorrrkbbk...........",
        ".........kBbkrrrrrooyyoorrrrrkbbk...........",
        "..........kkkrrrrbbuuuuubrrrrkkk............",
        "...........krrrrbbbuuuubbrrrrrk.............",
        "...........kkyyykbbbuubbkyyykk..............",
        "...........knnnnnnnnnnnnnnnnnk..............",
        "...........knnnnnnkkkkknnnnnnk..............",
        "..........knnnnnnk....knnnnnnk..............",
        "..........kNNNNNk......kNNNNNk..............",
        "..........kbbbbbk......kbbbbbk..............",
        ".........kBBBBBBk......kBBBBBBk.............",
        ".........kttttttk......kttttttk.............",
        "..........kkkkkk........kkkkkk..............",
        "............................................",
        "............................................"
    )

    private val arlongSharkDart = listOf(
        "............................................",
        "............................................",
        "............................................",
        "....................kkkk....................",
        "...................kbbbbk...................",
        "..................kbbBbbbk..................",
        ".......kkkkkkkkkkkbbbbbbbbkkkk..............",
        "....kcckrrrrrooyyobbbbbbbbbbbbkkkkk.........",
        "..kcccckrrrrrrooyybbbwkbbbbbbbbMmMmk........",
        ".kccwwwkrrrrrooyyobbbbwwwwwwbbbMmMmk........",
        "..kcccckrrrrrooyyobbbbbbbbbbbbkkkkk.........",
        "....kccknnnnnnnnnnbbbbbbbbkkkk..............",
        ".......kkkkkkkkkkkbbBbbbbbkk................",
        "..................kbbBbbbk..................",
        "...................kbbbbk...................",
        "....................kkkk....................",
        "............................................",
        "............................................",
        "............................................",
        "............................................",
        "............................................",
        "............................................",
        "............................................",
        "............................................",
        "............................................",
        "............................................",
        "............................................",
        "............................................"
    )

    private val arlongKiribatiIdle = listOf(
        ".................................kk.........",
        "................................kmmk........",
        "..................kkkkk........kmmmMk.......",
        ".................knnnnnk......kmmMmk........",
        "................knnnnnnnk....kmmmMmk........",
        "...............kbbuuuuubbk..kmmMmk..........",
        "..............kbbbwkbbwkbbkkmmMmk...........",
        "..............kbbbuuuuuubbbMmMmk............",
        "..............kbbbwwwwwwbbbmmMmk............",
        "..............kBbbwkwkwkbbBkMmk.............",
        ".............kkBbbbbbbbbbbBkyyk.............",
        "...........kkrrrooooyyoooorrbbk.............",
        "..........krrrrrooyyyyyyoorrbbk.............",
        ".........kbbkrrrooooyyoooorrkkk.............",
        "........kbbbkrrrrrooyyoorrrrrk..............",
        "........kBbbkrrrrbbuuuuubrrrrk..............",
        ".........kkkkrrrbbbuuuubbrrrrk..............",
        "...........kkyyykbbbuubbkyyyk...............",
        "...........knnnnnnnnnnnnnnnnnk..............",
        "..........knnnnnnnkkkkknnnnnnnk.............",
        "..........knnnnnnk.....knnnnnnk.............",
        ".........kNNNNNNk.......kNNNNNNk............",
        ".........kbbbbbbk.......kbbbbbbk............",
        "........kBBBBBBBk.......kBBBBBBBk...........",
        "........ktttttttk.......ktttttttk...........",
        ".........kkkkkkk.........kkkkkkk............",
        "............................................",
        "............................................"
    )

    private val arlongKiribatiCleave = listOf(
        "............................................",
        "..................kkkkk.....................",
        ".................knnnnnk....................",
        "................knnnnnnnk...................",
        "...............kbbuuuuubbk..................",
        "..............kbbbwkbbwkbbkkkkk.............",
        "..............kbbbuuuuuubbbMmMmk............",
        "..............kbbbwwwwwwbbbkkk..............",
        "..............kBbbwkwkwkbbBk................",
        ".............kkBbbbbbbbbbbBkk...............",
        "...........kkrrrooooyyoooorrrkk.............",
        "..........krrrrrooyyyyyyoorrrrrkkkk.........",
        ".........kbbkrrrooooyyoooorrrbbbyykmmkmkmkmk",
        "........kBbbkrrrrrooyyoorrrrrbbbyykmmmmmmmmk",
        ".........kkkkrrrrbbuuuuubrrrrkkk..kMMmMMmMMk",
        "...........krrrrbbbuuuubbrrrrrk...kk.kk.kk..",
        "...........kkyyykbbbuubbkyyykk..............",
        "...........knnnnnnnnnnnnnnnnnk..............",
        "..........knnnnnnnkkkkknnnnnnnk.............",
        ".........knnnnnnk.......knnnnnnk............",
        "........kNNNNNNk.........kNNNNNNk...........",
        "........kbbbbbbk.........kbbbbbbk...........",
        ".......kBBBBBBBk.........kBBBBBBBk..........",
        ".......ktttttttk.........ktttttttk..........",
        "........kkkkkkk...........kkkkkkk...........",
        "............................................",
        "............................................",
        "............................................"
    )

    // =========================================================================
    // 5. WATER BULLET PROJECTILE (12x8)
    // =========================================================================

    private val waterBulletGrid = listOf(
        "....kkkk....",
        "..kkcccckk..",
        ".kccwwwwcck.",
        "kCCccccccCCk",
        "kCCCccccCCCk",
        ".kCCCCCCCCk.",
        "..kkkkkkkk..",
        "............"
    )

    // =========================================================================
    // 6. ARLONG PARK DESTRUCTIBLE MAP ROOM PROPS & STONE GATE
    // =========================================================================

    private val mapShelfIntactGrid = listOf(
        "kkkkkkkkkkkkkkkkkkkk",
        "kTTTTTTTTTTTTTTTTTTk",
        "kTttttttttttttttttTk",
        "kTkaakaakaakaakaakTk",
        "kTkaAkaAkaAkaAkaAkTk",
        "kTkaakaakaakaakaakTk",
        "kTTTTTTTTTTTTTTTTTTk",
        "kTttttttttttttttttTk",
        "kTkwwkaakcckaakwwkTk",
        "kTkWWkaAkCCkaAkWWkTk",
        "kTkwwkaakcckaakwwkTk",
        "kTTTTTTTTTTTTTTTTTTk",
        "kTttttttttttttttttTk",
        "kTkaakrrkaakaakggkTk",
        "kTkaAkRRkaAkaAkGGkTk",
        "kTkaakrrkaakaakggkTk",
        "kTTTTTTTTTTTTTTTTTTk",
        "kTttttttttttttttttTk",
        "kTtkkkkkkkkkkkkkktTk",
        "kkkkkkkkkkkkkkkkkkkk"
    )

    private val mapShelfBrokenGrid = listOf(
        "....................",
        "....................",
        "....................",
        "....................",
        "....................",
        "....................",
        "....................",
        "....................",
        "....................",
        "....................",
        "....kkk......kk.....",
        "...kTTtk....ktTk....",
        "..kTtttkkkkkktTTk...",
        ".kTtkkaaaAkaaattTk..",
        ".kTkkaAkkkkkAAkTTk..",
        "kTTtkaakTttkaakttTk.",
        "kTtttTTttttTTtttttTk",
        "kTTTTTTTTTTTTTTTTTTk",
        "kkkkkkkkkkkkkkkkkkkk",
        "...................."
    )

    private val stonePillarIntactGrid = listOf(
        "kkkkkkkkkkkkkk",
        "kssssssssssssk",
        "kSSSSSSSSSSSSk",
        ".kssSSSSSSssk.",
        ".kssSSddSSssk.",
        ".kssSSSSSSssk.",
        ".kssSSrrSSssk.",
        ".kssSrrrrSssk.",
        ".kssSSrrSSssk.",
        ".kssSSSSSSssk.",
        ".kssSSddSSssk.",
        ".kssSSSSSSssk.",
        ".kssSSSSSSssk.",
        ".kssSSddSSssk.",
        ".kssSSSSSSssk.",
        ".kssSSSSSSssk.",
        ".kssSSddSSssk.",
        ".kssSSSSSSssk.",
        "kssssssssssssk",
        "kSSSSSSSSSSSSk",
        "kddddddddddddk",
        "kkkkkkkkkkkkkk"
    )

    private val stonePillarBrokenGrid = listOf(
        "..............",
        "..............",
        "..............",
        "..............",
        "..............",
        "..............",
        "..............",
        "..............",
        "..............",
        "..............",
        "..............",
        "..............",
        "...kk...kk....",
        "..kssk.kSSk...",
        ".kssSSkSSddk..",
        ".kssSSddSSSk..",
        "kssssSSSSddsk.",
        "kSSddSSSSSSSk.",
        "kssssssssssssk",
        "kSSSSSSSSSSSSk",
        "kddddddddddddk",
        "kkkkkkkkkkkkkk"
    )

    private val namiDeskIntactGrid = listOf(
        ".....kk.................",
        "....kyyk................",
        "....kYYk...kkkkkk.......",
        "....kttk..kaaaaaak......",
        "kkkkkkkkkkkkkkkkkkkkkkkk",
        "kttttttttttttttttttttttk",
        "kTTTTTTTTTTTTTTTTTTTTTTk",
        ".kTTk..kaaaAAaaak..kTTk.",
        ".kTTk..kaAAAAAAak..kTTk.",
        ".kTTk..kkkkkkkkkk..kTTk.",
        ".kTTk..............kTTk.",
        ".kTTk..............kTTk.",
        ".kTTk..............kTTk.",
        ".kkkk..............kkkk."
    )

    private val namiDeskBrokenGrid = listOf(
        "........................",
        "........................",
        "........................",
        "........................",
        "........................",
        "........................",
        "........................",
        "......kkkk..............",
        "....kkttTTkk....kkk.....",
        "..ktttTTaaaAkk.ktTTk....",
        ".kTTkkaaAAAkTTkttTTTk...",
        "kTTTtkkTTtkkTTTTTTTTTk..",
        "kkkkkkkkkkkkkkkkkkkkkkkk",
        "........................"
    )

    // Pre-built directional & flash-tinted sprite pairs
    val bouncerWalk: List<DirectionalFlashSprite> = listOf(
        PixelArtBuilder.buildFlashPair(bouncerWalk1, palette),
        PixelArtBuilder.buildFlashPair(bouncerWalk2, palette)
    )
    val bouncerShoveSprite: DirectionalFlashSprite =
        PixelArtBuilder.buildFlashPair(bouncerShove, palette)

    val chewIdleSprite: DirectionalFlashSprite =
        PixelArtBuilder.buildFlashPair(chewIdle, palette)
    val chewShootSprite: DirectionalFlashSprite =
        PixelArtBuilder.buildFlashPair(chewShoot, palette)

    val hatchanWalk: List<DirectionalFlashSprite> = listOf(
        PixelArtBuilder.buildFlashPair(hatchanWalk1, palette),
        PixelArtBuilder.buildFlashPair(hatchanWalk2, palette)
    )
    val hatchanSlashSprite: DirectionalFlashSprite =
        PixelArtBuilder.buildFlashPair(hatchanSlash, palette)
    val hatchanSpin: List<DirectionalFlashSprite> = listOf(
        PixelArtBuilder.buildFlashPair(hatchanSpin1, palette),
        PixelArtBuilder.buildFlashPair(hatchanSpin2, palette)
    )

    val arlongWalk: List<DirectionalFlashSprite> = listOf(
        PixelArtBuilder.buildFlashPair(arlongWalk1, palette),
        PixelArtBuilder.buildFlashPair(arlongWalk2, palette)
    )
    val arlongSharkDartSprite: DirectionalFlashSprite =
        PixelArtBuilder.buildFlashPair(arlongSharkDart, palette)
    val arlongKiribatiIdleSprite: DirectionalFlashSprite =
        PixelArtBuilder.buildFlashPair(arlongKiribatiIdle, palette)
    val arlongKiribatiCleaveSprite: DirectionalFlashSprite =
        PixelArtBuilder.buildFlashPair(arlongKiribatiCleave, palette)

    val waterBullet: DirectionalSprite =
        PixelArtBuilder.buildPair(waterBulletGrid, palette)

    val mapShelfIntact: ImageBitmap = PixelArtBuilder.build(mapShelfIntactGrid, palette)
    val mapShelfBroken: ImageBitmap = PixelArtBuilder.build(mapShelfBrokenGrid, palette)
    val stonePillarIntact: ImageBitmap = PixelArtBuilder.build(stonePillarIntactGrid, palette)
    val stonePillarBroken: ImageBitmap = PixelArtBuilder.build(stonePillarBrokenGrid, palette)
    val namiDeskIntact: ImageBitmap = PixelArtBuilder.build(namiDeskIntactGrid, palette)
    val namiDeskBroken: ImageBitmap = PixelArtBuilder.build(namiDeskBrokenGrid, palette)
}
