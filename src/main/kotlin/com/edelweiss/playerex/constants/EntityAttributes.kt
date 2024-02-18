package com.edelweiss.playerex.constants

import com.edelweiss.playerex.api.PlayerEXAPI
import net.minecraft.util.Identifier

enum class EntityAttributes(val id: Identifier) {
    Luck(PlayerEXAPI.id("luck")),
    Level(PlayerEXAPI.id("level")),
    Focus(PlayerEXAPI.id("focus")),
    Evasion(PlayerEXAPI.id("evasion")),
    Lifesteal(PlayerEXAPI.id("lifesteal")),
    Strength(PlayerEXAPI.id("strength")),
    Dexterity(PlayerEXAPI.id("dexterity")),
    Intelligence(PlayerEXAPI.id("intelligence")),
    Constitution(PlayerEXAPI.id("constitution")),
    BreakingSpeed(PlayerEXAPI.id("breaking_speed")),
    HealthRegeneration(PlayerEXAPI.id("health_regeneration")),
    HealAmplification(PlayerEXAPI.id("heal_amplification")),
    MeleeCritDamage(PlayerEXAPI.id("melee_crit_damage")),
    MeleeCritChance(PlayerEXAPI.id("melee_crit_chance")),
    RangedCritDamage(PlayerEXAPI.id("ranged_crit_damage")),
    RangedCritChance(PlayerEXAPI.id("ranged_crit_chance")),
    RangedBonusDamage(PlayerEXAPI.id("ranged_damage")),
    FireResistance(PlayerEXAPI.id("fire_resistance")),
    FreezeResistance(PlayerEXAPI.id("freeze_resistance")),
    LightningResistance(PlayerEXAPI.id("lightning_resistance")),
    PoisonResistance(PlayerEXAPI.id("poison_resistance")),
    WitherResistance(PlayerEXAPI.id("wither_resistance")),
}