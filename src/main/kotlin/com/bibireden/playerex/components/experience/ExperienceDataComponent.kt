package com.bibireden.playerex.components.experience

import com.bibireden.data_attributes.endec.nbt.NbtDeserializer
import com.bibireden.data_attributes.endec.nbt.NbtSerializer
import io.wispforest.endec.Endec
import io.wispforest.endec.impl.StructEndecBuilder
import net.minecraft.nbt.NbtCompound
import net.minecraft.world.chunk.Chunk
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class ExperienceDataComponent(
    val chunk: Chunk,
    private var _ticks: Int = 0,
    private var _restorativeForceTicks: Int = 0, // todo: requires config
    private var _restorativeForce: Float = 0.0F, // todo: requires config
    private var _expNegationFactor: Float = 1.0F,
    private var _expNegationMultiplier: Float = 0.0F // todo: requires config
) : IExperienceDataComponent {
    companion object {
        @JvmRecord data class Packet(val expNegationFactor: Float)

        @JvmField
        val ENDEC = StructEndecBuilder.of(
            Endec.FLOAT.fieldOf("expNegationFactor") { it.expNegationFactor },
            ::Packet
        )
    }

    override fun updateExperienceNegationFactor(amount: Int): Boolean {
        if (Random.nextFloat() > this._expNegationFactor) return true;

        val dynamicMultiplier = this._expNegationMultiplier + ((1.0F - this._expNegationMultiplier) * (1.0F - (0.1F * amount)))
        this._expNegationFactor = max(this._expNegationFactor * dynamicMultiplier, 0.0F)
        this.chunk.setNeedsSaving(true)
        return false;
    }

    override fun resetExperienceNegationFactor() { this._expNegationMultiplier = 1.0F }

    override fun readFromNbt(tag: NbtCompound) {
        ENDEC.decode(NbtDeserializer.of(tag)).also {
            this._expNegationFactor = it.expNegationFactor
        }
    }

    override fun writeToNbt(tag: NbtCompound) {
        ENDEC.encode(NbtSerializer.of(tag), Packet(this._expNegationFactor))
    }

    override fun serverTick() {
        if (this._expNegationFactor == 1.0F) return
        if (this._ticks < this._restorativeForceTicks) this._ticks++
        else {
            this._ticks = 0
            this._expNegationFactor = min(this._expNegationFactor * this._restorativeForce, 1.0F)
            this.chunk.setNeedsSaving(true)
        }
    }
}