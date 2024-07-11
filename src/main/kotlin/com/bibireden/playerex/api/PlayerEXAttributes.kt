package com.bibireden.playerex.api

import com.bibireden.playerex.PlayerEX
import net.minecraft.entity.attribute.ClampedEntityAttribute
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object PlayerEXAttributes {
    @JvmField
    val LEVEL = register("level", 0.0, 0.0, 100.0)


    private fun register(path: String, base: Double, min: Double, max: Double): ClampedEntityAttribute {
        val attribute = ClampedEntityAttribute("attribute.name.${PlayerEX.MOD_ID}.$path", base, min, max)
        return Registry.register(Registries.ATTRIBUTE, Identifier.of(PlayerEX.MOD_ID, path)!!, attribute)
    }
}