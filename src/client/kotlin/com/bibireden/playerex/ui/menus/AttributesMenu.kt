package com.bibireden.playerex.ui.menus

import com.bibireden.playerex.api.attribute.PlayerEXAttributes
import com.bibireden.playerex.components.player.IPlayerDataComponent
import com.bibireden.playerex.ext.level
import com.bibireden.playerex.ui.components.MenuComponent
import com.bibireden.playerex.ui.components.AttributeComponent
import com.bibireden.playerex.ui.components.labels.AttributeLabelComponent
import io.wispforest.owo.ui.component.Components
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.OwoUIAdapter
import io.wispforest.owo.ui.core.Positioning
import io.wispforest.owo.ui.core.Sizing
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.registry.Registries
import net.minecraft.text.Text

class AttributesMenu : MenuComponent(Sizing.fill(100), Sizing.fill(100), Algorithm.VERTICAL) {
    private fun onLevelUpdate(level: Int) {}

    /** Whenever ANY attribute gets updated. */
    private fun onAttributeUpdate() {
        // refresh all attribute labels
        this.children().forEach { component ->
            if (component !is AttributeComponent) return@forEach
            component.refresh()
            component.children().filterIsInstance<AttributeLabelComponent>().forEach(AttributeLabelComponent::refresh)
        }
    }

    override fun build(player: ClientPlayerEntity, adapter: OwoUIAdapter<FlowLayout>, component: IPlayerDataComponent) {
        child(Components.label(Text.translatable("playerex.ui.category.primary_attributes")))
        child(Components.box(Sizing.fill(35), Sizing.fixed(2)))
        gap(5)
        children(PlayerEXAttributes.PRIMARY_ATTRIBUTE_IDS.mapNotNull(Registries.ATTRIBUTE::get).map { AttributeComponent(it, player, component) })
        positioning(Positioning.relative(10, 25))



        this.onLevelUpdate(player.level.toInt())
        this.onAttributeUpdate()

        this.onLevelUpdated.subscribe(::onLevelUpdate)
        this.onAttributeUpdated.subscribe { _, _ -> onAttributeUpdate() }
    }
}