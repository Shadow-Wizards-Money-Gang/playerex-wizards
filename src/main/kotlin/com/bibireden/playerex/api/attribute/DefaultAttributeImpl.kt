package com.bibireden.playerex.api.attribute

import com.bibireden.data_attributes.api.attribute.StackingBehavior
import com.bibireden.data_attributes.api.attribute.StackingFormula
import com.bibireden.data_attributes.config.functions.AttributeFunction
import com.bibireden.data_attributes.config.models.OverridesConfigModel
import com.bibireden.data_attributes.config.models.OverridesConfigModel.AttributeOverride
import com.bibireden.data_attributes.data.EntityTypeData
import com.bibireden.playerex.ext.id
import de.dafuqs.additionalentityattributes.AdditionalEntityAttributes
import net.fabric_extras.ranged_weapon.api.EntityAttributes_RangedWeapon
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.util.Identifier
import net.projectile_damage.api.EntityAttributes_ProjectileDamage

object DefaultAttributeImpl {
    val OVERRIDES: Map<Identifier, AttributeOverride> = mapOf(
        EntityAttributes.GENERIC_ARMOR.id to AttributeOverride(
            max = 1024.0,
            formula = StackingFormula.Diminished
        )
    )
    val FUNCTIONS: Map<Identifier, List<AttributeFunction>> = mapOf(
        PlayerEXAttributes.CONSTITUTION.id to listOf(
            AttributeFunction(EntityAttributes.GENERIC_MAX_HEALTH.id, StackingBehavior.Add, 0.5),
            AttributeFunction(EntityAttributes.GENERIC_ARMOR.id, StackingBehavior.Add, 0.25),
            AttributeFunction(AdditionalEntityAttributes.MAGIC_PROTECTION.id, StackingBehavior.Add, 0.25),
            AttributeFunction(AdditionalEntityAttributes.LUNG_CAPACITY.id, StackingBehavior.Add, 0.01),
            AttributeFunction(PlayerEXAttributes.POISON_RESISTANCE.id, StackingBehavior.Add, 0.1),
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
            AttributeFunction(EntityAttributes_RangedWeapon.HASTE.id, StackingBehavior.Multiply, 0.02),
            AttributeFunction(EntityAttributes_ProjectileDamage.GENERIC_PROJECTILE_DAMAGE.id, StackingBehavior.Multiply, 0.1),
        ),
        PlayerEXAttributes.INTELLIGENCE.id to listOf(
            // todo: spell haste? (see wizards/spell power)
            AttributeFunction(AdditionalEntityAttributes.DROPPED_EXPERIENCE.id, StackingBehavior.Multiply, 0.01),
            AttributeFunction(PlayerEXAttributes.WITHER_RESISTANCE.id, StackingBehavior.Add, 0.1),
            // todo: max mana? (see archon)
            // todo: enchanting power? (see zenith)
        ),
        PlayerEXAttributes.FOCUS.id to listOf(
            AttributeFunction(PlayerEXAttributes.HEALTH_REGENERATION.id, StackingBehavior.Add, 0.01),
            AttributeFunction(PlayerEXAttributes.HEAL_AMPLIFICATION.id, StackingBehavior.Multiply, 0.05),
            AttributeFunction(PlayerEXAttributes.FREEZE_RESISTANCE.id, StackingBehavior.Add, 0.1),
            AttributeFunction(PlayerEXAttributes.LIGHTNING_RESISTANCE.id, StackingBehavior.Add, 0.1),
            AttributeFunction(PlayerEXAttributes.FIRE_RESISTANCE.id, StackingBehavior.Add, 0.1),
        ),
        PlayerEXAttributes.LUCKINESS.id to listOf(
            AttributeFunction(PlayerEXAttributes.MELEE_CRIT_CHANCE.id, StackingBehavior.Multiply, 0.02),
            AttributeFunction(PlayerEXAttributes.RANGED_CRITICAL_CHANCE.id, StackingBehavior.Multiply, 0.02),
            // todo: spell crit chance (see wizards/spell power)
            // loot table chance?? wh-
            AttributeFunction(EntityAttributes.GENERIC_LUCK.id, StackingBehavior.Add, 0.05),
            AttributeFunction(PlayerEXAttributes.EVASION.id, StackingBehavior.Add, 0.01),
        ),
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