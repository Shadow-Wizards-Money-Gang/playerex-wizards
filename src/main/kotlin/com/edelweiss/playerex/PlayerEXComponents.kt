package com.edelweiss.playerex

import com.edelweiss.playerex.api.PlayerEXAPI
import com.edelweiss.playerex.components.LivingEntityComponent
import com.edelweiss.playerex.components.PlayerEntityComponent
import dev.onyxstudios.cca.api.v3.component.ComponentKey
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer
import dev.onyxstudios.cca.api.v3.level.LevelComponentFactoryRegistry
import dev.onyxstudios.cca.api.v3.level.LevelComponentInitializer
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer
import net.minecraft.entity.LivingEntity

class PlayerEXComponents : EntityComponentInitializer, LevelComponentInitializer, WorldComponentInitializer {
    companion object {
        val livingEntities: ComponentKey<LivingEntityComponent> = ComponentRegistry.getOrCreate(PlayerEXAPI.id("living_entities"), LivingEntityComponent::class.java)
        val playerEntities: ComponentKey<PlayerEntityComponent> = ComponentRegistry.getOrCreate(PlayerEXAPI.id("player_entities"), PlayerEntityComponent::class.java)
    }

    override fun registerEntityComponentFactories(registry: EntityComponentFactoryRegistry) {
        registry.registerFor(LivingEntity::class.java, livingEntities) { entity -> LivingEntityComponent(entity) }
        registry.registerForPlayers(playerEntities) { player -> PlayerEntityComponent(player) }
    }

    override fun registerLevelComponentFactories(registry: LevelComponentFactoryRegistry) {
        
    }

    override fun registerWorldComponentFactories(registry: WorldComponentFactoryRegistry) {
        
    }
}