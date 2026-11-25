package com.example.game.engine

import com.example.game.model.AttackHitbox
import com.example.game.model.AttackType
import com.example.game.model.CombatEntity
import com.example.game.model.DamageResult
import com.example.game.model.Faction
import com.example.game.model.Rect2D

/**
 * Modular Combat & Hitbox System.
 * Evaluates active AttackHitboxes against living CombatEntities of opposing factions
 * and returns structured DamageResults for knockback, hitstop, stun, and screen shake.
 */
class CombatSystem {
    private var nextHitboxId: Long = 1L

    fun createHitbox(
        sourceId: String,
        attackType: AttackType,
        bounds: Rect2D,
        knockbackX: Float,
        knockbackY: Float,
        stunSeconds: Float = 0.18f,
        hitStopSeconds: Float = 0.03f,
        shakeIntensity: Float = 2.0f,
        damageOverride: Int? = null
    ): AttackHitbox {
        return AttackHitbox(
            id = nextHitboxId++,
            sourceId = sourceId,
            attackType = attackType,
            bounds = bounds,
            damage = damageOverride ?: attackType.baseDamage,
            knockbackX = knockbackX,
            knockbackY = knockbackY,
            stunSeconds = stunSeconds,
            hitStopSeconds = hitStopSeconds,
            shakeIntensity = shakeIntensity
        )
    }

    /**
     * Resolves overlaps between active hitboxes and living CombatEntities of opposing factions,
     * skipping any target already struck during the current attack window / Gatling pulse.
     * Supports Zoro's Steel Parry detection against winding-up foes and critical hit scaling.
     */
    fun resolveHits(
        hitboxes: List<AttackHitbox>,
        targets: List<CombatEntity>,
        alreadyHitTargetIds: Set<String>,
        attackerFaction: Faction = Faction.PLAYER,
        canParry: Boolean = false,
        guaranteedCrit: Boolean = false,
        passiveCritChance: Float = 0f,
        critDamageMultiplier: Float = 1.75f,
        critRoll: () -> Float = { 1f }
    ): List<DamageResult> {
        if (hitboxes.isEmpty() || targets.isEmpty()) return emptyList()
        val results = mutableListOf<DamageResult>()
        val hitThisStep = HashSet<String>()

        for (hitbox in hitboxes) {
            for (target in targets) {
                if (!target.isAlive) continue
                if (target.faction == attackerFaction) continue
                if (target.id in alreadyHitTargetIds) continue
                if (target.id in hitThisStep) continue
                if (hitbox.bounds.intersects(target.hurtbox)) {
                    hitThisStep.add(target.id)
                    hitbox.hitTargets.add(target.id)
                    val isParry = canParry && target.isWindingUp
                    val isCrit = guaranteedCrit || (passiveCritChance > 0f && critRoll() < passiveCritChance)
                    val scaledDamage = if (isCrit) {
                        (hitbox.damage * critDamageMultiplier).toInt().coerceAtLeast(hitbox.damage + 1)
                    } else {
                        hitbox.damage
                    }
                    results.add(
                        DamageResult(
                            targetId = target.id,
                            damageDealt = scaledDamage,
                            knockbackX = if (isParry) hitbox.knockbackX * 1.45f else hitbox.knockbackX,
                            knockbackY = if (isParry) minOf(hitbox.knockbackY, -145f) else hitbox.knockbackY,
                            stunSeconds = if (isParry) maxOf(hitbox.stunSeconds, 1.15f) else hitbox.stunSeconds,
                            hitStopSeconds = if (isParry) maxOf(hitbox.hitStopSeconds, 0.068f) else hitbox.hitStopSeconds,
                            shakeIntensity = if (isParry) maxOf(hitbox.shakeIntensity, 5.8f) else hitbox.shakeIntensity,
                            attackType = hitbox.attackType,
                            isCrit = isCrit,
                            isParry = isParry
                        )
                    )
                }
            }
        }
        return results
    }
}
