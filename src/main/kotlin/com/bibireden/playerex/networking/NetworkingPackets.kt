package com.bibireden.playerex.networking

import com.bibireden.playerex.networking.types.AttributePacketType
import com.bibireden.playerex.networking.types.NotificationType
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.util.Identifier


object NetworkingPackets {
    /**
     * Meant to represent an empty packet.
     */
    @JvmRecord
    data class Notify(val type: NotificationType)

    /**
     * Updates the provided attribute(s) with an associated [Double] value.
     * Possibility will be dictated based on the [AttributePacketType]
     */
    @JvmRecord
    data class Update(val type: AttributePacketType, val refs: Map<Identifier, Double>)
}