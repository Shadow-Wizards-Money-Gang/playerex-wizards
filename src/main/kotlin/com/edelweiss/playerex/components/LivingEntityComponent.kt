package com.edelweiss.playerex.components

import com.edelweiss.playerex.constants.PlayerEXDCAttributes
import dev.onyxstudios.cca.api.v3.component.Component
import dev.onyxstudios.cca.api.v3.entity.PlayerComponent
import net.minecraft.nbt.NbtCompound

class LivingEntityComponent() : PlayerComponent<Component>, Component {
    companion object {}

    private val attributes = mutableMapOf<PlayerEXDCAttributes, Int>()

    fun setAttribute(attribute: PlayerEXDCAttributes, value: Int) {
        this.attributes[attribute] = value
    }

    fun getAttribute(attribute: PlayerEXDCAttributes): Int? = this.attributes[attribute]

    override fun readFromNbt(tag: NbtCompound) {
        PlayerEXDCAttributes.entries.forEach { attribute -> attributes[attribute] = tag.getInt(attribute.tag) }
    }

    override fun writeToNbt(tag: NbtCompound) {
        attributes.forEach { (key, value) -> tag.putInt(key.tag, value) }
    }
}