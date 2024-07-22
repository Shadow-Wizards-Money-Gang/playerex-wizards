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
            DataAttributesAPI.getValue(PlayerEXAttributes.FIRE_RESISTANCE, entity).map { ((1.0 - (it / 100)) * damage).toFloat() }.orElse(damage)
        })
        registry.invoke({ _, source, _ -> source.isOf(DamageTypes.FREEZE)}, { living, _, damage ->
            DataAttributesAPI.getValue(PlayerEXAttributes.FREEZE_RESISTANCE, living).map { (damage * (1.0 - it)).toFloat() }.orElse(damage)
        })
        registry.invoke({ _, source, _ -> source.isOf(DamageTypes.LIGHTNING_BOLT)}, { living, _, damage ->
            DataAttributesAPI.getValue(PlayerEXAttributes.LIGHTNING_RESISTANCE, living).map { (damage * (1.0 - it)).toFloat() }.orElse(damage)
        })
        registry.invoke(
            { living, source, damage -> living.hasStatusEffect(StatusEffects.POISON) && source.isOf(DamageTypes.MAGIC) && damage <= 1.0F },
            {living, _, damage -> DataAttributesAPI.getValue(PlayerEXAttributes.POISON_RESISTANCE, living).map { (damage * (1.0 - it)).toFloat() }.orElse(damage) }
        )
        registry.invoke({_, source, _ -> source.isOf(DamageTypes.WITHER) || (source.isOf(DamageTypes.INDIRECT_MAGIC) && (source.source is PotionEntity || source.source is AreaEffectCloudEntity))}, {
            living, source, damage ->
            DataAttributesAPI.getValue(PlayerEXAttributes.WITHER_RESISTANCE, living).map {
                if (source.isOf(DamageTypes.WITHER) && living.isUndead) return@map 0.0F
                if (source.isOf(DamageTypes.INDIRECT_MAGIC) && source.source is PotionEntity && living.isUndead) return@map damage
                (damage * (1.0 - it)).toFloat()
            }.orElse(damage)
        })
    }
}