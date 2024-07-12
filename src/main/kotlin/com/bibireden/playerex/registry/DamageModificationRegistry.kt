package com.bibireden.playerex.registry

import com.bibireden.playerex.api.damage.DamageFunction
import com.bibireden.playerex.api.damage.DamagePredicate

object DamageModificationRegistry {
    private val entries: MutableList<DamageModification> = mutableListOf()

    fun register(predicate: DamagePredicate, function: DamageFunction) {
        entries.add { it(predicate, function) }
    }

    fun get() = entries.toList()

    fun interface DamageModification {
        fun provide(provider: (DamagePredicate, DamageFunction) -> Float): Float
    }
}