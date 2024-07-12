package com.bibireden.playerex.factory

import com.bibireden.playerex.networking.NetworkingChannels
import com.bibireden.playerex.networking.NetworkingPackets
import com.bibireden.playerex.networking.NotificationType
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerLoginNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity

object NetworkFactory {
    fun onLoginQueryStart(handler: ServerLoginNetworkHandler, server: MinecraftServer, sender: PacketSender, synchronizer: ServerLoginNetworking.LoginSynchronizer) {
        // todo: requires a config implementation :: requires synchronization at this point, but i think this is automatically handled by owo so probably no need for this, so what's the point of even including this function in the first place i'm going to to crash out
    }

    /** Sends an empty packet to a [ServerPlayerEntity], notifying of a level-up. */
    fun sendLevelUpNotification(player: ServerPlayerEntity) {
        NetworkingChannels.NOTIFICATIONS.serverHandle(player).send(NetworkingPackets.Notify(NotificationType.LevelUp))
    }
}