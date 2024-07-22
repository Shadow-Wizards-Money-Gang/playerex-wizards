package com.bibireden.playerex.api.attribute

import com.bibireden.data_attributes.api.attribute.StackingBehavior
import com.bibireden.data_attributes.config.functions.AttributeFunction
import com.bibireden.data_attributes.data.EntityTypeData
import com.bibireden.playerex.ext.id
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.util.Identifier
import net.projectile_damage.ProjectileDamageMod

object DefaultAttributeImpl {
    val FUNCTIONS: Map<Identifier, List<AttributeFunction>> = mapOf(
        PlayerEXAttributes.CONSTITUTION.id to listOf(
            AttributeFunction(EntityAttributes.GENERIC_MAX_HEALTH.id, StackingBehavior.Add, 0.5),
            AttributeFunction(EntityAttributes.GENERIC_ARMOR.id, StackingBehavior.Add, 0.25),
            AttributeFunction(PlayerEXAttributes.POISON_RESISTANCE.id, StackingBehavior.Add, 0.1),
            AttributeFunction(PlayerEXAttributes.FREEZE_RESISTANCE.id, StackingBehavior.Add, 0.1),
            AttributeFunction(PlayerEXAttributes.LIGHTNING_RESISTANCE.id, StackingBehavior.Add, 0.1),
            // todo: add additionalentityattributes as a modImplementation, making it mandatory.
            AttributeFunction(Identifier.of("additionalentityattributes", "generic.magic_protection")!!, StackingBehavior.Add, 0.25),
            AttributeFunction(Identifier.of("additionalentityattributes", "generic.lung_capacity")!!, StackingBehavior.Add, 0.25)
        ),
        PlayerEXAttributes.STRENGTH.id to listOf(
            AttributeFunction(EntityAttributes.GENERIC_ATTACK_DAMAGE.id, StackingBehavior.Multiply, 0.02),
            AttributeFunction(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE.id, StackingBehavior.Add, 0.1),
            AttributeFunction(PlayerEXAttributes.MELEE_CRIT_DAMAGE.id, StackingBehavior.Multiply, 0.05),
            AttributeFunction(PlayerEXAttributes.BREAKING_SPEED.id, StackingBehavior.Add, 0.01),
        ),
        PlayerEXAttributes.DEXTERITY.id to listOf(
            AttributeFunction(EntityAttributes.GENERIC_ATTACK_SPEED.id, StackingBehavior.Multiply, 0.01),
            AttributeFunction(PlayerEXAttributes.RANGED_DAMAGE.id, StackingBehavior.Multiply, 0.02),
            AttributeFunction(PlayerEXAttributes.RANGED_CRITICAL_DAMAGE.id, StackingBehavior.Multiply, 0.05),
//            AttributeFunction(.id, StackingBehavior.Multiply, 0.05), idk what or where draw speed is
            AttributeFunction(PlayerEXAttributes.FIRE_RESISTANCE.id, StackingBehavior.Add, 0.1),
        ),
        PlayerEXAttributes.INTELLIGENCE.id to listOf(
        //   AttributeFunction(.id, StackingBehavior.Multiply, 0.05), idk what or where spell haste is.
        //            couulldd supply thru wizardex
            // what is dropped experience
            AttributeFunction(PlayerEXAttributes.WITHER_RESISTANCE.id, StackingBehavior.Add, 0.1),
            AttributeFunction(PlayerEXAttributes.HEAL_AMPLIFICATION.id, StackingBehavior.Multiply, 0.05),
            AttributeFunction(PlayerEXAttributes.HEALTH_REGENERATION.id, StackingBehavior.Add, 0.01),
        ),
        PlayerEXAttributes.LUCKINESS.id to listOf(
            AttributeFunction(PlayerEXAttributes.MELEE_CRIT_CHANCE.id, StackingBehavior.Multiply, 0.02),
            AttributeFunction(PlayerEXAttributes.RANGED_CRITICAL_CHANCE.id, StackingBehavior.Multiply, 0.02),
            // cannot find spell crit chance atm, maybe a wizard thing
            // loot table chance?? wh-
            AttributeFunction(PlayerEXAttributes.MELEE_CRIT_CHANCE.id, StackingBehavior.Add, 0.01),
            AttributeFunction(EntityAttributes.GENERIC_LUCK.id, StackingBehavior.Add, 0.05),
            AttributeFunction(PlayerEXAttributes.EVASION.id, StackingBehavior.Multiply, 0.01),
        )
    )
    val ENTITY_TYPES: Map<Identifier, EntityTypeData> = mapOf(
        Identifier.of("minecraft", "player")!! to EntityTypeData(mapOf(
            PlayerEXAttributes.LEVEL.id to 0.0,
            PlayerEXAttributes.CONSTITUTION.id to 0.0,
            PlayerEXAttributes.STRENGTH.id to 0.0,
            PlayerEXAttributes.STRENGTH.id to 0.0,
            PlayerEXAttributes.DEXTERITY.id to 0.0,
            PlayerEXAttributes.INTELLIGENCE.id to 0.0,
            PlayerEXAttributes.LUCKINESS.id to 0.0,
            PlayerEXAttributes.EVASION.id to 0.0,
            PlayerEXAttributes.LIFESTEAL.id to 0.0,
            PlayerEXAttributes.FOCUS.id to 0.0,
            PlayerEXAttributes.HEALTH_REGENERATION.id to 0.0,
            PlayerEXAttributes.HEAL_AMPLIFICATION.id to 0.0,
            PlayerEXAttributes.HEAL_AMPLIFICATION.id to 0.0,
            PlayerEXAttributes.MELEE_CRIT_DAMAGE.id to 0.0,
            PlayerEXAttributes.MELEE_CRIT_CHANCE.id to 0.0,
            PlayerEXAttributes.RANGED_CRITICAL_DAMAGE.id to 0.0,
            PlayerEXAttributes.RANGED_CRITICAL_CHANCE.id to 0.0,
            PlayerEXAttributes.RANGED_DAMAGE.id to 0.0,
            PlayerEXAttributes.FIRE_RESISTANCE.id to 0.0,
            PlayerEXAttributes.FREEZE_RESISTANCE.id to 0.0,
            PlayerEXAttributes.LIGHTNING_RESISTANCE.id to 0.0,
            PlayerEXAttributes.POISON_RESISTANCE.id to 0.0,
            PlayerEXAttributes.WITHER_RESISTANCE.id to 0.0,
            PlayerEXAttributes.BREAKING_SPEED.id to 1.0,

            TradeSkillAttributes.ENCHANTING.id to 0.0,
            TradeSkillAttributes.LOGGING.id to 0.0,
            TradeSkillAttributes.ALCHEMY.id to 0.0,
            TradeSkillAttributes.FISHING.id to 0.0,
            TradeSkillAttributes.MINING.id to 0.0,
            TradeSkillAttributes.SMITHING.id to 0.0,
            TradeSkillAttributes.FARMING.id to 0.0
        ))
    )
}