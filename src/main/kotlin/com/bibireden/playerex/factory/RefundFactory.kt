package com.bibireden.playerex.factory

import com.bibireden.playerex.api.attribute.PlayerEXAttributes
import com.bibireden.playerex.components.player.IPlayerDataComponent
import net.minecraft.world.entity.player.Player
import java.util.function.Consumer

object RefundFactory {
    fun forEach(registry: Consumer<(IPlayerDataComponent, Player) -> Double>) {
        registry.accept { data, player -> data.get(PlayerEXAttributes.BODY) }
        registry.accept { data, player -> data.get(PlayerEXAttributes.DEXTERITY) }
        registry.accept { data, player -> data.get(PlayerEXAttributes.FAITH) }
        registry.accept { data, player -> data.get(PlayerEXAttributes.INTELLIGENCE) }
        registry.accept { data, player -> data.get(PlayerEXAttributes.OCCULT) }
        registry.accept { data, player -> data.get(PlayerEXAttributes.WISDOM) }
    }
}