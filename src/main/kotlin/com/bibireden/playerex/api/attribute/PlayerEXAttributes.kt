package com.bibireden.playerex.api.attribute

import com.bibireden.playerex.PlayerEX
import net.minecraft.entity.attribute.ClampedEntityAttribute
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object PlayerEXAttributes {
    @JvmField
    val LEVEL = register("level", 0.0, 0.0, 100.0)

    @JvmField
    val CONSTITUTION = register("constitution", 0.0, 0.0, 1000000.0);

    @JvmField
    val STRENGTH = register("strength", 0.0, 0.0, 1000000.0);

    @JvmField
    val DEXTERITY = register("dexterity", 0.0, 0.0, 1000000.0);

    @JvmField
    val INTELLIGENCE = register("intelligence", 0.0, 0.0, 1000000.0);

    @JvmField
    val LUCKINESS = register("luckiness", 0.0, 0.0, 1000000.0);

    @JvmField
    val HEALTH_REGENERATION = register("health_regeneration", 0.0, 0.0, 100.0);

    @JvmField
    val HEAL_AMPLIFICATION = register("heal_amplification", 0.0, 0.0, 100.0);

    @JvmField
    val LIFESTEAL = register("lifesteal", 0.0, 0.0, 100.0);

    @JvmField
    val MELEE_CRIT_DAMAGE = register("melee_crit_damage", 0.0, 0.0, 100.0);

    @JvmField
    val MELEE_CRIT_CHANCE = register("melee_crit_chance", 0.0, 0.0, 100.0);

    @JvmField
    val BREAKING_SPEED = register("breaking_speed", 0.0, 0.0, 100.0);

    @JvmField
    val FIRE_RESISTANCE = register("fire_resistance", 0.0, 0.0, 1.0);

    @JvmField
    val FREEZE_RESISTANCE = register("freeze_resistance", 0.0, 0.0, 1.0);

    @JvmField
    val LIGHTNING_RESISTANCE = register("lightning_resistance", 0.0, 0.0, 1.0);

    @JvmField
    val WITHER_RESISTANCE = register("wither_resistance", 0.0, 0.0, 1.0);

    @JvmField
    val POISON_RESISTANCE = register("poison_resistance", 0.0, 0.0, 1.0);

    @JvmField
    val EVASION = register("evasion", 0.0, 0.0, 100.0);

    @JvmField
    val FOCUS = register("focus", 0.0, 0.0, 1_000_000.0)

    @JvmField
    val RANGED_DAMAGE = register("ranged_damage", 0.0, 0.0, 1000000.0)

    @JvmField
    val RANGED_CRITICAL_CHANCE = register("ranged_crit_chance", 0.0, 0.0, 100.0)

    @JvmField
    val RANGED_CRITICAL_DAMAGE = register("ranged_crit_damage", 0.0, 0.0, 1000000.0)

    fun register(path: String, base: Double, min: Double, max: Double): ClampedEntityAttribute {
        val attribute = ClampedEntityAttribute("attribute.name.${PlayerEX.MOD_ID}.$path", base, min, max)
        return Registry.register(Registries.ATTRIBUTE, Identifier.of(PlayerEX.MOD_ID, path)!!, attribute)
    }
}