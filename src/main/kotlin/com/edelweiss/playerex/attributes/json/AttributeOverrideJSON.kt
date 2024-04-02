package com.edelweiss.playerex.attributes.json

import com.edelweiss.playerex.attributes.tags.AttributeOverrideTags
import com.edelweiss.playerex.attributes.utils.NbtIO
import com.edelweiss.skillattributes.enums.StackingFormula
import com.edelweiss.skillattributes.utils.from
import kotlinx.serialization.Serializable
import net.minecraft.entity.attribute.ClampedEntityAttribute
import net.minecraft.nbt.NbtCompound

//"minecraft:generic.max_health": {
//    "default": 1.0,
//    "min": 0.0,
//    "max": 0.0,
//    "increment": 0.0,
//    "behavior": "ADD/PERCENT",
//    "formula": "FLAT/DIMINISHED"
//}

@Serializable
data class AttributeOverrideJSON(
    private var default: Double,
    private var min: Double,
    private var max: Double,
    private var increment: Double,
    private var formula: StackingFormula,
    private var translationKey: String
) : NbtIO
{
    /** Creates and returns a `ClampedEntityAttribute`. */
    fun create() = ClampedEntityAttribute(this.translationKey, this.default, this.min, this.max)

//    fun override(attribute: MutableEntityAttribute) = attribute.override(this)

    override fun readFromNbt(tag: NbtCompound) {
        this.default = tag.getDouble(AttributeOverrideTags.DEFAULT)
        this.min = tag.getDouble(AttributeOverrideTags.MIN)
        this.max = tag.getDouble(AttributeOverrideTags.MAX)
        this.increment = tag.getDouble(AttributeOverrideTags.INCREMENT)
        this.translationKey = tag.getString(AttributeOverrideTags.TRANSLATION_KEY)
        val byte = tag.getByte(AttributeOverrideTags.FORMULA)
        this.formula = (StackingFormula::id from byte) ?: StackingFormula.Flat
    }

    override fun writeToNbt(tag: NbtCompound) {
        tag.putDouble(AttributeOverrideTags.DEFAULT, this.default)
        tag.putDouble(AttributeOverrideTags.MIN, this.min)
        tag.putDouble(AttributeOverrideTags.MAX, this.max)
        tag.putDouble(AttributeOverrideTags.INCREMENT, this.increment)
        tag.putString(AttributeOverrideTags.TRANSLATION_KEY, this.translationKey)
        tag.putByte(AttributeOverrideTags.FORMULA, this.formula.id)
    }
}

