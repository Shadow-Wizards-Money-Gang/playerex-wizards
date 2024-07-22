package com.bibireden.playerex.networking

import com.bibireden.playerex.networking.types.UpdatePacketType
import com.bibireden.playerex.networking.types.NotificationType
import net.minecraft.util.Identifier


object NetworkingPackets {
    /**
     * Meant to represent an empty packet.
     */
    @JvmRecord
    data class Notify(val type: NotificationType)

    /**
     * Updates the provided attribute with an associated [Double] value.
     * Possibility will be dictated based on the [UpdatePacketType]
     *
     * todo: make it add the amounts given, not set.
     * todo: also, ensure they SHOULD be able to do this, based on the amount of skill points.
     */
    @JvmRecord
    data class Update(val type: UpdatePacketType, val id: Identifier, val amount: Int)

    /** Packet specifically for handling leveling events. Provides the amount of levels the client intends to increase by. */
    @JvmRecord
    data class Level(val amount: Int)
}