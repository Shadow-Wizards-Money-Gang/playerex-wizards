package com.bibireden.playerex.api.event

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.LivingEntity

object LivingEntityEvents {
    /**
     * Fired before [LivingEntity.heal]; allows the amount healed to be modified before healing happens.
     * Setting the output to `0` is an unreliable way to negate incoming damage depending on other mods installed.
     *
     * @see [LivingEntityEvents.SHOULD_HEAL].
     */
    @JvmField
    val ON_HEAL: Event<Healed> = EventFactory.createArrayBacked(Healed::class.java) { callbacks -> Healed { entity, original ->
        var previous = original
        callbacks.forEach { previous = it.onHeal(entity, previous) }
        previous
    }}

    /**
     * Fired at the start of [LivingEntity.heal], but before healing is applied. Can return false to cancel all healing, or true to allow it.
     */
    @JvmField
    val SHOULD_HEAL: Event<Heal> = EventFactory.createArrayBacked(Heal::class.java) { callbacks -> Heal { entity, original ->
        callbacks.forEach { if (!it.shouldHeal(entity, original)) return@Heal false }
        true
    }}

    /**
     * Fired once at the end of [LivingEntity.tick].
     */
    @JvmField
    val ON_TICK: Event<OnTick> = EventFactory.createArrayBacked(OnTick::class.java) { callbacks -> OnTick { entity -> callbacks.forEach { it.onTick(entity) } }}

    /**
     * Fired once at the end of [LivingEntity.tick], every `20` ticks (~1 second).
     */
    @JvmField
    val ON_EVERY_SECOND: Event<OnEverySecond> = EventFactory.createArrayBacked(OnEverySecond::class.java) { callbacks -> OnEverySecond { entity -> callbacks.forEach { it.onEverySecond(entity) } }}

    /**
     * Fired before [LivingEntity.hurt].
     *
     * This allows the amount of damage to be modified before it is used in any way.
     * It can be used to perform logic prior to the damage method,
     * and can return the original damage to avoid modifying the value.
     *
     * The original value is the incoming damage, followed by the result of this event by any previous registries.
     *
     * Setting the output to 0 is an unreliable way to negate incoming damage depending on other mods installed.
     * Instead, use [LivingEntityEvents.SHOULD_DAMAGE].
     */
    @JvmField
    val ON_DAMAGE: Event<Damaged> = EventFactory.createArrayBacked(Damaged::class.java) { callbacks -> Damaged { entity, source, original ->
        var previous = original
        callbacks.forEach { previous = it.onDamage(entity, source, previous) }
        previous
    }}

    /**
     * Fired after:
     * - [LivingEntity.isInvulnerableTo],
     * - [net.minecraft.world.World.isClient],
     * - [LivingEntity.isDeadOrDying],
     * - ([DamageSource] && [LivingEntity.hasEffect] for Fire Resistance), and [LivingEntity.isSleeping]
     *
     * is checked, but before all other logic is performed. Can be used to cancel the method and prevent damage from being taken by returning false.
     * Returning true allows the logic to continue.
     */
    @JvmField
    val SHOULD_DAMAGE: Event<Damage> = EventFactory.createArrayBacked(Damage::class.java) { callbacks -> Damage { entity, source, original ->
        callbacks.forEach { if (!it.shouldDamage(entity, source, original)) return@Damage false }
        true
    }}

    fun interface Healed {
        fun onHeal(livingEntity: LivingEntity, original: Float): Float
    }

    fun interface Heal {
        fun shouldHeal(livingEntity: LivingEntity, original: Float): Boolean
    }

    fun interface OnTick {
        fun onTick(livingEntity: LivingEntity)
    }

    fun interface OnEverySecond {
        fun onEverySecond(livingEntity: LivingEntity)
    }

    fun interface Damaged {
        fun onDamage(livingEntity: LivingEntity, source: DamageSource, original: Float): Float
    }

    fun interface Damage {
        fun shouldDamage(livingEntity: LivingEntity, source: DamageSource, original: Float): Boolean
    }
}