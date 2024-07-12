package com.bibireden.playerex.api.damage

import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource

fun interface DamageFunction {
    /**
     * Using the input conditions, modifies the incoming damage (either reducing or increasing it) and returns the result.
     * @param livingEntity
     * @param source
     * @param damage
     * @return
     */
    fun apply(livingEntity: LivingEntity, source: DamageSource, damage: Float): Float
}
