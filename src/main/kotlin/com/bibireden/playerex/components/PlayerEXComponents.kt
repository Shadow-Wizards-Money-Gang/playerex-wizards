package com.bibireden.playerex.components

import com.bibireden.playerex.PlayerEX
import com.bibireden.playerex.components.experience.ExperienceDataComponent
import com.bibireden.playerex.components.experience.IExperienceDataComponent
import com.bibireden.playerex.components.player.IPlayerDataComponent
import com.bibireden.playerex.components.player.PlayerDataComponent
import dev.onyxstudios.cca.api.v3.chunk.ChunkComponentFactoryRegistry
import dev.onyxstudios.cca.api.v3.chunk.ChunkComponentInitializer
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy
import net.minecraft.util.Identifier

class PlayerEXComponents : EntityComponentInitializer, ChunkComponentInitializer {
    companion object {
        @JvmField
        val PLAYER_DATA = ComponentRegistry.getOrCreate(Identifier.of(PlayerEX.MOD_ID, "player-data")!!, IPlayerDataComponent::class.java)
        @JvmField
        val EXPERIENCE_DATA = ComponentRegistry.getOrCreate(Identifier.of(PlayerEX.MOD_ID, "experience-data")!!, IExperienceDataComponent::class.java)
    }

    override fun registerEntityComponentFactories(registry: EntityComponentFactoryRegistry) {
        registry.registerForPlayers(PLAYER_DATA, ::PlayerDataComponent, RespawnCopyStrategy.ALWAYS_COPY)
    }

    override fun registerChunkComponentFactories(registry: ChunkComponentFactoryRegistry) {
        registry.register(EXPERIENCE_DATA, ExperienceDataComponent::class.java, ::ExperienceDataComponent)
    }
}