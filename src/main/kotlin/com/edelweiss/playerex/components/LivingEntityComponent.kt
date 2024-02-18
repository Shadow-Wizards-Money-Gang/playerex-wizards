package com.edelweiss.playerex.components

import com.edelweiss.playerex.constants.EntityAttributes
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent
import net.minecraft.entity.LivingEntity
import net.minecraft.nbt.NbtCompound

class LivingEntityComponent(private val entity: LivingEntity) : AutoSyncedComponent {
    companion object {}

    private val attributes = mutableMapOf<EntityAttributes, Double>()

    fun setAttribute(attribute: EntityAttributes, value: Double) {
        this.attributes[attribute] = value
    }

    fun getAttribute(attribute: EntityAttributes): Double? = this.attributes[attribute]

    override fun readFromNbt(tag: NbtCompound) {
        EntityAttributes.entries.forEach { attribute -> attributes[attribute] = tag.getDouble(attribute.id.path) }
    }

    override fun writeToNbt(tag: NbtCompound) {
        attributes.forEach { (key, value) -> tag.putDouble(key.id.path, value) }
    }
}