package com.bibireden.playerex.factory

import com.bibireden.playerex.api.attribute.PlayerEXAttributes
import com.bibireden.playerex.components.player.IPlayerDataComponent
import net.minecraft.entity.player.PlayerEntity
import java.util.function.Consumer

object RefundFactory {
    fun forEach(registry: Consumer<(IPlayerDataComponent, PlayerEntity) -> Double>) {
        registry.accept { data, player -> data.get(PlayerEXAttributes.CONSTITUTION) }
        registry.accept { data, player -> data.get(PlayerEXAttributes.STRENGTH) }
        registry.accept { data, player -> data.get(PlayerEXAttributes.DEXTERITY) }
        registry.accept { data, player -> data.get(PlayerEXAttributes.INTELLIGENCE) }
        registry.accept { data, player -> data.get(PlayerEXAttributes.LUCKINESS) }
    }
}