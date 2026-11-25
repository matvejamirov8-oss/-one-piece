package com.example.game.engine

import com.example.game.model.EnemyKind
import com.example.game.model.EnemySpawnSpec
import com.example.game.model.LevelDefinition
import com.example.game.model.WaveDefinition

/**
 * Data-driven Level & Wave Catalog.
 * Currently contains Level 1 (Shells Town / East Blue Beach & Pier — 3 waves + Boss Captain Morgan).
 * Future levels (Level 2+, Orange Town, Syrup Village, Arlong Park) can be appended cleanly here.
 */
object LevelCatalog {

    val LEVEL_1 = LevelDefinition(
        levelNumber = 1,
        title = "УРОВЕНЬ 1",
        waves = listOf(
            // Wave 1: Patrol introduction — Marine Soldiers + Rifleman
            WaveDefinition(
                bannerTitle = "УРОВЕНЬ 1 • ВОЛНА 1/3",
                isBossWave = false,
                spawns = listOf(
                    EnemySpawnSpec(EnemyKind.MARINE_SOLDIER, offsetFromPlayer = 125f),
                    EnemySpawnSpec(EnemyKind.MARINE_SOLDIER, offsetFromPlayer = -115f),
                    EnemySpawnSpec(EnemyKind.MARINE_RIFLEMAN, offsetFromPlayer = 185f)
                )
            ),
            // Wave 2: Fast Pirate Bandits join the Marines
            WaveDefinition(
                bannerTitle = "ВОЛНА 2/3",
                isBossWave = false,
                spawns = listOf(
                    EnemySpawnSpec(EnemyKind.PIRATE_BANDIT, offsetFromPlayer = 110f),
                    EnemySpawnSpec(EnemyKind.PIRATE_BANDIT, offsetFromPlayer = -120f),
                    EnemySpawnSpec(EnemyKind.MARINE_SOLDIER, offsetFromPlayer = 160f),
                    EnemySpawnSpec(EnemyKind.MARINE_RIFLEMAN, offsetFromPlayer = -175f)
                )
            ),
            // Wave 3: Full East Blue skirmish before Captain Morgan arrives
            WaveDefinition(
                bannerTitle = "ВОЛНА 3/3",
                isBossWave = false,
                spawns = listOf(
                    EnemySpawnSpec(EnemyKind.MARINE_SOLDIER, offsetFromPlayer = 115f),
                    EnemySpawnSpec(EnemyKind.PIRATE_BANDIT, offsetFromPlayer = -105f),
                    EnemySpawnSpec(EnemyKind.MARINE_RIFLEMAN, offsetFromPlayer = 180f),
                    EnemySpawnSpec(EnemyKind.PIRATE_BANDIT, offsetFromPlayer = 150f),
                    EnemySpawnSpec(EnemyKind.MARINE_RIFLEMAN, offsetFromPlayer = -170f)
                )
            ),
            // Final Wave: Boss Captain Axe-Hand Morgan!
            WaveDefinition(
                bannerTitle = "БОСС: КАПИТАН МОРГАН!",
                isBossWave = true,
                spawns = listOf(
                    EnemySpawnSpec(EnemyKind.BOSS_MORGAN, offsetFromPlayer = 155f)
                )
            )
        )
    )

    val LEVEL_2 = LevelDefinition(
        levelNumber = 2,
        title = "УРОВЕНЬ 2",
        waves = listOf(
            // Wave 1: Buggy's Circus Acrobats & Knife Throwers in Orange Town
            WaveDefinition(
                bannerTitle = "УРОВЕНЬ 2 • ВОЛНА 1/3",
                isBossWave = false,
                spawns = listOf(
                    EnemySpawnSpec(EnemyKind.BUGGY_ACROBAT, offsetFromPlayer = 125f),
                    EnemySpawnSpec(EnemyKind.BUGGY_ACROBAT, offsetFromPlayer = -115f),
                    EnemySpawnSpec(EnemyKind.KNIFE_THROWER, offsetFromPlayer = 180f)
                )
            ),
            // Wave 2: Acrobat & Knife Thrower ambush
            WaveDefinition(
                bannerTitle = "ВОЛНА 2/3",
                isBossWave = false,
                spawns = listOf(
                    EnemySpawnSpec(EnemyKind.BUGGY_ACROBAT, offsetFromPlayer = 105f),
                    EnemySpawnSpec(EnemyKind.KNIFE_THROWER, offsetFromPlayer = -155f),
                    EnemySpawnSpec(EnemyKind.BUGGY_ACROBAT, offsetFromPlayer = 155f),
                    EnemySpawnSpec(EnemyKind.KNIFE_THROWER, offsetFromPlayer = 195f)
                )
            ),
            // Wave 3: Elite Cabaji on unicycle leads the final circus squad before Buggy!
            WaveDefinition(
                bannerTitle = "ВОЛНА 3/3 • ЭЛИТА: КАБАДЖИ!",
                isBossWave = false,
                spawns = listOf(
                    EnemySpawnSpec(EnemyKind.CABAJI, offsetFromPlayer = 150f),
                    EnemySpawnSpec(EnemyKind.BUGGY_ACROBAT, offsetFromPlayer = -115f),
                    EnemySpawnSpec(EnemyKind.KNIFE_THROWER, offsetFromPlayer = -175f)
                )
            ),
            // Final Wave: Boss Captain Buggy the Clown!
            WaveDefinition(
                bannerTitle = "БОСС: КАПИТАН БАГГИ!",
                isBossWave = true,
                spawns = listOf(
                    EnemySpawnSpec(EnemyKind.BOSS_BUGGY, offsetFromPlayer = 160f)
                )
            )
        )
    )

    val LEVEL_3 = LevelDefinition(
        levelNumber = 3,
        title = "УРОВЕНЬ 3",
        waves = listOf(
            WaveDefinition(
                bannerTitle = "АРЛОНГ-ПАРК • РАЗБЕЙ ВОРОТА!",
                isBossWave = false,
                spawns = emptyList()
            ),
            WaveDefinition(
                bannerTitle = "ДВОР АРЛОНГ-ПАРКА • РЫБОЛЮДИ!",
                isBossWave = false,
                spawns = emptyList()
            ),
            WaveDefinition(
                bannerTitle = "МИНИ-БОСС: ХАТТЯН (6 МЕЧЕЙ)!",
                isBossWave = false,
                spawns = emptyList()
            ),
            WaveDefinition(
                bannerTitle = "БОСС: АРЛОНГ ПИЛА!",
                isBossWave = true,
                spawns = emptyList()
            )
        )
    )

    val levels: List<LevelDefinition> = listOf(LEVEL_1, LEVEL_2, LEVEL_3)

    fun getLevel(levelNumber: Int): LevelDefinition {
        return levels.firstOrNull { it.levelNumber == levelNumber } ?: LEVEL_1
    }
}
