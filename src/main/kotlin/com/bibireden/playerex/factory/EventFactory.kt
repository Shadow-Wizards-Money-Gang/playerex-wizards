package com.bibireden.playerex.factory

import com.bibireden.data_attributes.api.DataAttributesAPI
import com.bibireden.data_attributes.api.attribute.IEntityAttribute
import com.bibireden.playerex.api.attribute.PlayerEXAttributes
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.PersistentProjectileEntity
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import java.util.Optional

object EventFactory {
    fun serverStarting(sever: MinecraftServer)
    {
        // todo: Create config?
    }

    fun reset(oldPlayer: ServerPlayerEntity, newPlayer: ServerPlayerEntity, isAlive: Boolean)
    {

    }

    fun clamped(attributeIn: EntityAttribute, valueIn: Double)
    {
        val attribute: IEntityAttribute = attributeIn as IEntityAttribute;
    }

    fun healed(livingEntity: LivingEntity, amount: Float): Float
    {
        val healAmplificationOption: Optional<Double> = DataAttributesAPI.getValue(PlayerEXAttributes.HEAL_AMPLIFICATION, livingEntity);

        if (healAmplificationOption.isPresent)
        {
            return (amount * (1.0 + healAmplificationOption.get())).toFloat()
        }
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
            val user: LivingEntity = attacker;
            val lifestealOption = DataAttributesAPI.getValue(PlayerEXAttributes.LIFESTEAL, livingEntity);

            if (lifestealOption.isPresent)
            {
                user.heal((original * lifestealOption.get() * 10.0).toFloat());
            }
        }

        val evasionOption = DataAttributesAPI.getValue(PlayerEXAttributes.EVASION, livingEntity)

        if (evasionOption.isPresent)
        {
            val chance = livingEntity.random.nextFloat();
            return !(chance < evasionOption.get() && origin is PersistentProjectileEntity);
        }
    }

    fun onCritAttack(
        player: PlayerEntity,
        target: Entity,
        amount: Float
    ): Float
    {
        if (target !is LivingEntity) return amount;

        val meleeCritOption = DataAttributesAPI.getValue(PlayerEXAttributes.MELEE_CRIT_DAMAGE, player);

        if (meleeCritOption.isPresent)
        {
            return (amount * (1.0 + (meleeCritOption.get() * 10.0)) / 1.5).toFloat();
        }
    }

    fun attackIsCrit(player: PlayerEntity, target: Entity, vanilla: Boolean): Boolean
    {
        if (target !is LivingEntity) return vanilla;

        val critChanceOptional = DataAttributesAPI.getValue(PlayerEXAttributes.MELEE_CRIT_CHANCE, player);

        if (critChanceOptional.isPresent)
        {
            val chance = player.random.nextFloat();
            return (chance < critChanceOptional.get()) && !player.isClimbing && !player.hasStatusEffect(StatusEffects.BLINDNESS) && !player.hasVehicle();
        }
    }

}