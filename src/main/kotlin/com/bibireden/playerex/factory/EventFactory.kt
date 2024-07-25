package com.bibireden.playerex.factory

import com.bibireden.data_attributes.api.DataAttributesAPI
import com.bibireden.playerex.PlayerEX
import com.bibireden.playerex.api.PlayerEXAPI
import com.bibireden.playerex.api.attribute.PlayerEXAttributes
import com.bibireden.playerex.components.PlayerEXComponents
import com.bibireden.playerex.components.player.IPlayerDataComponent
import com.bibireden.playerex.registry.DamageModificationRegistry
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.PersistentProjectileEntity
import net.minecraft.server.network.ServerPlayerEntity

object EventFactory {
    fun reset(oldPlayer: ServerPlayerEntity, newPlayer: ServerPlayerEntity, isAlive: Boolean)
    {
        PlayerEXComponents.PLAYER_DATA.get(newPlayer).reset(if (PlayerEX.CONFIG.resetOnDeath) 0 else 100)
    }

    fun healed(livingEntity: LivingEntity, amount: Float): Float
    {
        return DataAttributesAPI.getValue(PlayerEXAttributes.HEAL_AMPLIFICATION, livingEntity).map { (amount * (1.0 + it)).toFloat() }.orElse(amount)
    }

    fun healthRegeneration(livingEntity: LivingEntity)
    {
        if (!livingEntity.world.isClient) {
            val healthRegenerationOption = DataAttributesAPI.getValue(PlayerEXAttributes.HEALTH_REGENERATION, livingEntity);

            if (healthRegenerationOption.isPresent)
            {
                val healthRegeneration = healthRegenerationOption.get();

                if (healthRegeneration > 0.0 && livingEntity.health < livingEntity.maxHealth)
                {
                    livingEntity.heal(healthRegeneration.toFloat());
                }

                return
            }
        }
    }

    fun onDamage(livingEntity: LivingEntity, source: DamageSource, original: Float): Float
    {
        var amount = original
        for (condition in DamageModificationRegistry.get()) {
            val damage = amount
            amount = condition.provide { predicate, function ->
                return@provide if (predicate.test(livingEntity, source, damage)) {
                    function.apply(livingEntity, source, damage)
                }
                else {
                    damage
                }
            }
        }
        return amount
    }

    fun shouldDamage(livingEntity: LivingEntity, source: DamageSource, original: Float): Boolean
    {
        if (original == 0.0F)
        {
            return true;
        }

        val origin: Entity? = source.source;
        val attacker: Entity? = source.attacker;

        if (attacker is LivingEntity && (origin is LivingEntity || origin is PersistentProjectileEntity))
        {
            DataAttributesAPI.getValue(PlayerEXAttributes.LIFESTEAL, livingEntity).ifPresent {
                attacker.heal((original * it * 10.0).toFloat());
            }
        }

        val evasionOption = DataAttributesAPI.getValue(PlayerEXAttributes.EVASION, livingEntity)

        if (evasionOption.isPresent)
        {
            val chance = livingEntity.random.nextFloat();
            return !(chance < evasionOption.get() && origin is PersistentProjectileEntity);
        }

        return true
    }

    fun onCritAttack(player: PlayerEntity, target: Entity, amount: Float): Float
    {
        if (target !is LivingEntity) return amount;

        val meleeCritOption = DataAttributesAPI.getValue(PlayerEXAttributes.MELEE_CRIT_DAMAGE, player);

        if (meleeCritOption.isPresent)
        {
            return (amount * (1.0 + (meleeCritOption.get() * 10.0)) / 1.5).toFloat();
        }

        return amount
    }

    fun attackIsCrit(player: PlayerEntity, target: Entity, original: Boolean): Boolean
    {
        if (target !is LivingEntity) return original;

        val critChanceOptional = DataAttributesAPI.getValue(PlayerEXAttributes.MELEE_CRIT_CHANCE, player);

        if (critChanceOptional.isPresent)
        {
            val chance = player.random.nextFloat();
            return (chance < critChanceOptional.get()) && !player.isClimbing && !player.hasStatusEffect(StatusEffects.BLINDNESS) && !player.hasVehicle();
        }

        return original
    }

}