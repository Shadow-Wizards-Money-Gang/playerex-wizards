package com.bibireden.playerex.api.attribute

import com.bibireden.data_attributes.api.attribute.EntityAttributeSupplier
import net.minecraft.util.Identifier

/** Attributes that specifically dont relate to PlayerEX, but are used by the mod. They have a possibility of not existing depending on the mods currently active. */
object ModdedAttributes {
    /**
     * If JamieWhiteShirt `reach-entity-attributes` exists then this accesses the reach
     * distance attribute.
     */
    @JvmField
    val REACH_DISTANCE = EntityAttributeSupplier(Identifier.of("reach-entity-attributes", "reach")!!)
    /**
     * If JamieWhiteShirt `reach-entity-attributes` exists then this accesses the
     * attack range attribute.
     */
    @JvmField
    val ATTACK_RANGE = EntityAttributeSupplier(Identifier.of("reach-entity-attributes", "attack_range")!!)
}