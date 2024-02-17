package com.edelweiss.playerex

import com.edelweiss.playerex.api.PlayerEXDCApi
import com.edelweiss.playerex.components.LivingEntityComponent
import dev.onyxstudios.cca.api.v3.component.ComponentKey
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer
import net.minecraft.entity.LivingEntity

class PlayerEXDCEntityComponents : EntityComponentInitializer {
    val livingEntities: ComponentKey<LivingEntityComponent> = ComponentRegistry.getOrCreate(PlayerEXDCApi.createID("living_entities"), LivingEntityComponent::class.java)
    override fun registerEntityComponentFactories(registry: EntityComponentFactoryRegistry) {
        registry.registerFor(LivingEntity::class.java, livingEntities) { LivingEntityComponent() }
    }
}