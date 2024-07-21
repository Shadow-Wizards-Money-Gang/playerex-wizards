package com.bibireden.playerex.networking.types

import com.bibireden.playerex.PlayerEX
import com.bibireden.playerex.components.player.IPlayerDataComponent
import com.bibireden.playerex.util.PlayerEXUtil
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity

enum class AttributePacketType(val applyIfValid: (server: MinecraftServer, player: ServerPlayerEntity, component: IPlayerDataComponent) -> Boolean) {
    Default({ _, _, _ -> true }),
    Skill(::onSkill),
    Refund(::onRefund);
}

private fun onSkill(server: MinecraftServer, player: ServerPlayerEntity, component: IPlayerDataComponent): Boolean {
    val isConsumable = component.skillPoints >= 1
    if (isConsumable) component.addSkillPoints(-1)
    return isConsumable
}

private fun onRefund(server: MinecraftServer, player: ServerPlayerEntity, component: IPlayerDataComponent): Boolean {
    val isRefundable = component.skillPoints >= 1
    if (isRefundable) {
        component.addRefundablePoints(-1)
        component.addSkillPoints(1)
    }
    return isRefundable
}