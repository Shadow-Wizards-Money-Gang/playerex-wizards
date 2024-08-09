package com.bibireden.playerex.ext

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.ai.attributes.Attribute


/**
 * Expects the [Attribute] to be registered by the time it is invoked.
 *
 * Returns an unresolved [ResourceLocation] if invalid.
 * Do not use this in static initialization.
 * */
val Attribute.id: ResourceLocation
    get() = BuiltInRegistries.ATTRIBUTE.getKey(this) ?: ResourceLocation.tryBuild("unresolved", "id")!!