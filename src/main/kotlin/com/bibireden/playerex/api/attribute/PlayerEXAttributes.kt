package com.bibireden.playerex.api.attribute

import com.bibireden.playerex.PlayerEX
import com.bibireden.playerex.ext.id
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.ai.attributes.RangedAttribute
import org.jetbrains.annotations.ApiStatus

object PlayerEXAttributes {
    @JvmField
    val PRIMARY_ATTRIBUTE_IDS: Set<ResourceLocation>

    @JvmField
    val LEVEL = register("level", 0.0, 0.0, 25.0)

    @JvmField
    val BODY = register("body", 0.0, 0.0, 25.0)

    @JvmField
    val DEXTERITY = register("dexterity", 0.0, 0.0, 25.0)

    @JvmField
    val FAITH = register("faith", 0.0, 0.0, 25.0)

    @JvmField
    val INTELLIGENCE = register("intelligence", 0.0, 0.0, 25.0)

    @JvmField
    val OCCULT = register("occult", 0.0, 0.0, 25.0)

    @JvmField
    val WISDOM = register("wisdom", 0.0, 0.0, 25.0)

    @JvmField
    val HEALTH_REGENERATION = register("health_regeneration", 0.0, 0.0, 1.0)

    @JvmField
    val HEAL_AMPLIFICATION = register("heal_amplification", 0.0, 0.0, 1.0)

    @JvmField
    val LIFESTEAL = register("lifesteal", 0.0, 0.0, 1.0)

    @JvmField
    val BREAKING_SPEED = register("breaking_speed", 0.0, 0.0, 100.0)

    @JvmField
    val FIRE_RESISTANCE = register("fire_resistance", 0.0, 0.0, 1.0)

    @JvmField
    val FREEZE_RESISTANCE = register("freeze_resistance", 0.0, 0.0, 1.0)

    @JvmField
    val LIGHTNING_RESISTANCE = register("lightning_resistance", 0.0, 0.0, 1.0)

    @JvmField
    val WITHER_RESISTANCE = register("wither_resistance", 0.0, 0.0, 1.0)

    @JvmField
    val POISON_RESISTANCE = register("poison_resistance", 0.0, 0.0, 1.0)

    @JvmField
    val EVASION = register("evasion", 0.0, 0.0, 1.0)

    @JvmField
    val MELEE_CRITICAL_CHANCE = register("melee_crit_chance", 0.0, 0.0, 1.0)

    @JvmField
    val MELEE_CRITICAL_DAMAGE = register("melee_crit_damage", 0.0, 0.0, 1_000_000.0)

    @JvmField
    val RANGED_CRITICAL_CHANCE = register("ranged_crit_chance", 0.0, 0.0, 1.0)

    @JvmField
    val RANGED_CRITICAL_DAMAGE = register("ranged_crit_damage", 0.0, 0.0, 1_000_000.0)

    @ApiStatus.Internal
    fun register(path: String, base: Double, min: Double, max: Double): RangedAttribute {
        val attribute = RangedAttribute("attribute.name.${PlayerEX.MOD_ID}.$path", base, min, max)
        return Registry.register(BuiltInRegistries.ATTRIBUTE, ResourceLocation.tryBuild(PlayerEX.MOD_ID, path)!!, attribute)
    }

    init {
        PRIMARY_ATTRIBUTE_IDS = setOf(BODY.id, DEXTERITY.id, FAITH.id, INTELLIGENCE.id, OCCULT.id, WISDOM.id)
    }
}