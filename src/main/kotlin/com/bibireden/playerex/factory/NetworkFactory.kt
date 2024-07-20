package com.bibireden.playerex.factory

import com.bibireden.data_attributes.api.DataAttributesAPI
import com.bibireden.playerex.components.PlayerEXComponents
import com.bibireden.playerex.networking.NetworkingChannels
import com.bibireden.playerex.networking.NetworkingPackets
import com.bibireden.playerex.networking.types.NotificationType
import io.wispforest.owo.network.ServerAccess
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerLoginNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity

object NetworkFactory {
    @JvmStatic
    fun onLoginQueryStart(handler: ServerLoginNetworkHandler, server: MinecraftServer, sender: PacketSender, synchronizer: ServerLoginNetworking.LoginSynchronizer) {
        // todo: requires a config implementation :: requires synchronization at this point, but i think this is automatically handled by owo so probably no need for this, so what's the point of even including this function in the first place i'm going to to crash out
    }

    /** Sends an empty packet to a [ServerPlayerEntity], notifying of a level-up. */
    @JvmStatic
    fun sendLevelUpNotification(player: ServerPlayerEntity) {
        NetworkingChannels.NOTIFICATIONS.serverHandle(player).send(NetworkingPackets.Notify(NotificationType.LevelUp))
    }

//    fun onAttributeModification(packet: NetworkingPackets.Attributes, access: ServerAccess) {
//        val (type, consumers) = packet
//
//        val player = access.player
//        val component = PlayerEXComponents.PLAYER_DATA.get(player)
//        // todo: think a bit more about how you want to deliver and recieve packets, and handle custom behavior. PacketType in the previous was more involved than it seemed.
//        // todo: Also, allow client(s) to configure how many levels/etc they want removed at once. This is to prevent having to subtract all the time using that button.
//        if (type.function.apply(player.server, player, component)) {
//            for (consumer in consumers) {
//                consumer.accept { supplier, value ->
//                    DataAttributesAPI.getValue(supplier::get, player).ifPresent { component.add(supplier.get(), it) }
//                }
//            }
//        }
//    }
}