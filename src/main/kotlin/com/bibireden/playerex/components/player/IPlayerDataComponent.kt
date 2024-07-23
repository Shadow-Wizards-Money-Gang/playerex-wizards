package com.bibireden.playerex.components.player

import dev.onyxstudios.cca.api.v3.component.Component
import net.minecraft.entity.attribute.EntityAttribute

/** Utilized to access and modify PlayerEX attribute modifiers. */
interface IPlayerDataComponent : Component {
    /** Provides the current number of skill points. */
    val skillPoints: Int
    /** Returns the current number of refundable points. */
    val refundablePoints: Int

    /** Gets the cached [EntityAttribute] modifier value, or provides `0` if it does not exist. */
    fun get(attribute: EntityAttribute): Double
    /** Applies the provided modifier value to the [EntityAttribute], and creates it if it does not exist. */
    fun set(attribute: EntityAttribute, value: Int)
    /** Removes the [EntityAttribute] modifier if it exists. */
    fun remove(attribute: EntityAttribute)
    /** Adds the current [EntityAttribute]'s value with the provided value together. */
    fun add(attribute: EntityAttribute, value: Double)
    /**
     * Resets all the data (which includes all [EntityAttribute] modifiers) to their defaults.
     *
     * @param percent Depending on what is provided, it will preserve the amount of skill points.
    */
    fun reset(percent: Int = 0)
    /** Applies skill points to the player. */
    fun addSkillPoints(points: Int)
    /** Applies refundable points to the player. */
    fun addRefundablePoints(points: Int): Int
    /**
     * Levels up the player based on the given amount that you wish to level them up by.
     *
     * If it is not possible, it will return `false`.
     * This can be changed by setting `true` to the [override] argument.
     */
    fun levelUp(amount: Int, override: Boolean = false): Boolean

    /** Skills up the player based on the given amount.
     *
     * If not possible, it will return `false`.
     * This can be changed by setting `true` to the [override] argument. */
    fun skillUp(skill: EntityAttribute, amount: Int, override: Boolean = false): Boolean

    /** Refunds skill points based on the amount provided and how much [refundablePoints] the player currently has. */
    fun refund(skill: EntityAttribute, amount: Int): Boolean
}