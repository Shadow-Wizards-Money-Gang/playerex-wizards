package com.bibireden.playerex.components.experience

import com.bibireden.playerex.PlayerEX
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.chunk.ChunkAccess
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class ExperienceDataComponent(val chunk: ChunkAccess, private var _ticks: Int = 0, private var _expNegationFactor: Float = 1.0F) : IExperienceDataComponent {
    override fun updateExperienceNegationFactor(amount: Int): Boolean {
        if (Random.nextFloat() > this._expNegationFactor) return true

        val negFactor = PlayerEX.CONFIG.advancedSettings.expNegationFactor
        val dynamicMultiplier = negFactor + ((1.0F - negFactor) * (1.0F - (0.1F * amount)))
        this._expNegationFactor = max(this._expNegationFactor * dynamicMultiplier, 0.0F)
        this.chunk.isUnsaved = true
        return false
    }

    override fun resetExperienceNegationFactor() { PlayerEX.CONFIG.advancedSettings.expNegationFactor = 1 }

    override fun readFromNbt(tag: CompoundTag) {
        this._expNegationFactor = tag.getFloat("exp_factor")
    }

    override fun writeToNbt(tag: CompoundTag) {
        tag.putFloat("exp_factor", this._expNegationFactor)
    }

    override fun serverTick() {
        if (this._expNegationFactor == 1.0F) return
        if (this._ticks < PlayerEX.CONFIG.advancedSettings.restorativeForceTicks) this._ticks++
        else {
            this._ticks = 0
            this._expNegationFactor = min(this._expNegationFactor * PlayerEX.CONFIG.advancedSettings.restorativeForceMultiplier, 1.0F)
            this.chunk.isUnsaved = true
        }
    }
}