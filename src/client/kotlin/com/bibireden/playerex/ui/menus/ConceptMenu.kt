package com.bibireden.playerex.ui.menus

import com.bibireden.playerex.components.player.IPlayerDataComponent
import com.bibireden.playerex.ui.components.MenuComponent
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.OwoUIAdapter
import net.minecraft.client.network.ClientPlayerEntity
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Internal
class ConceptMenu : MenuComponent(algorithm = Algorithm.VERTICAL) {
    override fun build(player: ClientPlayerEntity, adapter: OwoUIAdapter<FlowLayout>, component: IPlayerDataComponent) {
    }
}