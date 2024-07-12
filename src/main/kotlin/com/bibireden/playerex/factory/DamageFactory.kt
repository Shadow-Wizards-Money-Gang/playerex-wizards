package com.bibireden.playerex.factory

import com.bibireden.data_attributes.api.DataAttributesAPI
import com.bibireden.playerex.api.attribute.PlayerEXAttributes
import com.bibireden.playerex.api.damage.DamageFunction
import com.bibireden.playerex.api.damage.DamagePredicate
import net.minecraft.entity.AreaEffectCloudEntity
import net.minecraft.entity.damage.DamageTypes
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.projectile.thrown.PotionEntity

object DamageFactory {

    fun forEach(registry: (damagePredicate: DamagePredicate, damageFunction: DamageFunction) -> Unit)
    {
        registry.invoke({ _, source, _ -> source.isOf(DamageTypes.ON_FIRE) }, { entity, _, damage ->
            val fireResistanceOptional = DataAttributesAPI.getValue(PlayerEXAttributes.FIRE_RESISTANCE, entity)

            if (fireResistanceOptional.isPresent)
            {
                return@invoke ((1.0 - (fireResistanceOptional.get()/100)) * damage).toFloat()
            } else
            {
                return@invoke damage
            }
        })
        registry.invoke({ _, source, _ -> source.isOf(DamageTypes.FREEZE)}, { living, _, damage ->
            val freezeResistanceOptional = DataAttributesAPI.getValue(PlayerEXAttributes.FREEZE_RESISTANCE, living)

            if (freezeResistanceOptional.isPresent)
            {
                return@invoke (damage * (1.0-freezeResistanceOptional.get())).toFloat()
            } else {
                return@invoke damage
            }
        })
        registry.invoke({ _, source, _ -> source.isOf(DamageTypes.LIGHTNING_BOLT)}, { living, _, damage ->
            val lightningResistanceOptional = DataAttributesAPI.getValue(PlayerEXAttributes.LIGHTNING_RESISTANCE, living)

            if (lightningResistanceOptional.isPresent)
            {
                return@invoke (damage * (1.0-lightningResistanceOptional.get())).toFloat()
            } else {
                return@invoke damage
            }
        })
        registry.invoke({ living, source, damage ->
            living.hasStatusEffect(StatusEffects.POISON) && source.isOf(DamageTypes.MAGIC) && damage <= 1.0F}, {living, _, damage ->
            val poisonResistanceOptional = DataAttributesAPI.getValue(PlayerEXAttributes.POISON_RESISTANCE, living)

            if (poisonResistanceOptional.isPresent)
            {
                return@invoke (damage * (1.0-poisonResistanceOptional.get())).toFloat()
            } else {
                return@invoke damage
            }
        })
        registry.invoke({_, source, _ -> source.isOf(DamageTypes.WITHER) || (source.isOf(DamageTypes.INDIRECT_MAGIC) && (source.source is PotionEntity || source.source is AreaEffectCloudEntity))}, {
            living, source, damage ->
            val witherResistanceOptional = DataAttributesAPI.getValue(PlayerEXAttributes.WITHER_RESISTANCE, living)

            if (witherResistanceOptional.isPresent)
            {
                if (source.isOf(DamageTypes.WITHER) && living.isUndead) return@invoke 0.0F
                if (source.isOf(DamageTypes.INDIRECT_MAGIC) && source.source is PotionEntity && living.isUndead) return@invoke damage
                return@invoke (damage * (1.0 - witherResistanceOptional.get())).toFloat()
            } else {
                return@invoke damage
            }
        })
    }
}