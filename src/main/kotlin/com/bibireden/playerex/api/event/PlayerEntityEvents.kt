package com.bibireden.playerex.api.event

import com.bibireden.playerex.api.event.PlayerEntityEvents.ShouldCritical
import com.bibireden.playerex.api.event.PlayerEntityEvents.AttackCriticalDamage
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity

object PlayerEntityEvents {

    /**
     * Fired if the player lands a critical hit. The result is the damage.
     */
    @JvmField
    val ON_CRITICAL: Event<AttackCriticalDamage> = EventFactory.createArrayBacked(AttackCriticalDamage::class.java) { callbacks: Array<AttackCriticalDamage> ->
        AttackCriticalDamage { player: PlayerEntity, target: Entity, amount: Float ->
            var previous = amount
            for (callback in callbacks) {
                previous = callback.onCriticalDamage(player, target, previous)
            }
            previous
        }
    }

    /**
     * Fired when determining if the player's attack is critical. Return true if it is critical, return false if it is not.
     */
    @JvmField
    val SHOULD_CRITICAL: Event<ShouldCritical> = EventFactory.createArrayBacked(ShouldCritical::class.java) { callbacks: Array<ShouldCritical> ->
        ShouldCritical { player: PlayerEntity, target: Entity, vanilla: Boolean ->
            for (callback in callbacks) {
                if (callback.shouldCritical(player, target, vanilla)) return@ShouldCritical true
            }
            false
        }
    }

    fun interface AttackCriticalDamage {
        fun onCriticalDamage(player: PlayerEntity, target: Entity, amount: Float): Float
    }

    fun interface ShouldCritical {
        fun shouldCritical(player: PlayerEntity, target: Entity, vanilla: Boolean): Boolean
    }
}