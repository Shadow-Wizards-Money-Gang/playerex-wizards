package com.bibireden.playerex.api.attribute

import com.bibireden.data_attributes.config.functions.AttributeFunction
import com.bibireden.data_attributes.data.EntityTypeData
import com.bibireden.playerex.ext.id
import net.minecraft.util.Identifier

object DefaultAttributeImpl {
    val FUNCTIONS: Map<Identifier, List<AttributeFunction>> = mapOf()
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
            PlayerEXAttributes.HEALTH_REGENERATION.id to 0.0,
            PlayerEXAttributes.HEAL_AMPLIFICATION.id to 0.0,
            PlayerEXAttributes.HEAL_AMPLIFICATION.id to 0.0,
            PlayerEXAttributes.MELEE_CRIT_DAMAGE.id to 0.0,
            PlayerEXAttributes.MELEE_CRIT_CHANCE.id to 0.0,
            PlayerEXAttributes.RANGED_CRITICAL_DAMAGE.id to 0.0,
            PlayerEXAttributes.RANGED_CRITICAL_CHANCE.id to 0.0,
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