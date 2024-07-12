package com.bibireden.playerex.components.player

import dev.onyxstudios.cca.api.v3.component.Component
import net.minecraft.entity.attribute.EntityAttribute

/** Utilized to access and modify PlayerEX attribute modifiers. */
interface IPlayerDataComponent : Component {
    /** Gets the cached [EntityAttribute] modifier value, or provides `0` if it does not exist. */
    fun get(attribute: EntityAttribute): Double
    /** Applies the provided modifier value to the [EntityAttribute], and creates it if it does not exist. */
    fun set(attribute: EntityAttribute, value: Double)
    /** Removes the [EntityAttribute] modifier if it exists. */
    fun remove(attribute: EntityAttribute)
    /** Adds the current [EntityAttribute]'s value with the provided value together. */
    fun add(attribute: EntityAttribute, value: Double)
    /**
     * Resets all the data (which includes all [EntityAttribute] modifiers) to their defaults.
     *
     * @param percent Depending on what is provided, it will preserve the amount of skill points.
    */
    fun reset(percent: Int)
    /** Applies skill points to the player. */
    fun addSkillPoints(points: Int)
    /** Applies refundable points to the player. */
    fun addRefundablePoints(points: Int)
    /** Provides the current number of skill points. */
    fun skillPoints(): Int
    /** Returns the current number of refundable points. */
    fun refundablePoints(): Int
}