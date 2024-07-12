package com.bibireden.playerex.networking

import com.bibireden.playerex.PlayerEX
import io.wispforest.owo.network.OwoNetChannel

object NetworkingChannels {
    val CONFIG = OwoNetChannel.create(PlayerEX.id("config"))
    val MODIFY = OwoNetChannel.create(PlayerEX.id("modify"))
    val SCREEN = OwoNetChannel.create(PlayerEX.id("screen"))
    val NOTIFICATIONS = OwoNetChannel.create(PlayerEX.id("notify"))
}