package com.bibireden.playerex.networking

import com.bibireden.playerex.api.attribute.EntityAttributeSupplier
import java.util.function.BiConsumer

object NetworkingPackets {
    /**
     * Meant to represent an empty packet.
     */
    @JvmRecord
    data class Notify(val type: NotificationType)

    @JvmRecord
    data class Attributes(val type: AttributePacketType, val attributes: List<BiConsumer<EntityAttributeSupplier, Double>>)
}