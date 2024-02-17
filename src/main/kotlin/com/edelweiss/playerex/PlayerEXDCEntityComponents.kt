package com.edelweiss.playerex

import com.edelweiss.playerex.api.PlayerEXDCApi
import com.edelweiss.playerex.components.PlayerEntityComponent
import dev.onyxstudios.cca.api.v3.component.ComponentKey
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer

class PlayerEXDCEntityComponents : EntityComponentInitializer {
    val playerEntities: ComponentKey<PlayerEntityComponent> = ComponentRegistry.getOrCreate(PlayerEXDCApi.createID("player_entities"), PlayerEntityComponent::class.java)
    override fun registerEntityComponentFactories(registry: EntityComponentFactoryRegistry) {
        registry.registerForPlayers(playerEntities) { PlayerEntityComponent() }
    }
}