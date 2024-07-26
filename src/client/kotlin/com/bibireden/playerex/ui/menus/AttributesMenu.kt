package com.bibireden.playerex.ui.menus

import com.bibireden.playerex.api.attribute.PlayerEXAttributes
import com.bibireden.playerex.components.player.IPlayerDataComponent
import com.bibireden.playerex.ext.level
import com.bibireden.playerex.ui.components.MenuComponent
import com.bibireden.playerex.ui.components.AttributeComponent
import com.bibireden.playerex.ui.components.labels.AttributeLabelComponent
import com.bibireden.playerex.util.PlayerEXUtil
import io.wispforest.owo.ui.component.Components
import io.wispforest.owo.ui.container.Containers
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.Insets
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
        this.forEachDescendant { component ->
            if (component is AttributeComponent) {
                component.refresh()
                return@forEachDescendant
            }
            if (component is AttributeLabelComponent) {
                component.refresh()
                return@forEachDescendant
            }
        }
    }

    override fun build(player: ClientPlayerEntity, adapter: OwoUIAdapter<FlowLayout>, component: IPlayerDataComponent) {
        child(Containers.verticalFlow(Sizing.fill(35), Sizing.fill(100)).apply {
            child(Components.label(Text.translatable("playerex.ui.category.primary_attributes")))
            child(
                Components.textBox(Sizing.fixed(27))
                    .also {
                        it.setMaxLength(4)
                    }
                    .text("1")
                    .verticalSizing(Sizing.fixed(10))
                    .positioning(Positioning.relative(100, 0))
                    .id("input")
            )
            child(Components.box(Sizing.fill(100), Sizing.fixed(2)))
            gap(5)
            children(PlayerEXAttributes.PRIMARY_ATTRIBUTE_IDS.mapNotNull(Registries.ATTRIBUTE::get).map { AttributeComponent(it, player, component) })
        })

        padding(Insets.both(12, 12))



        this.onLevelUpdate(player.level.toInt())
        this.onAttributeUpdate()

        this.onLevelUpdated.subscribe(::onLevelUpdate)
        this.onAttributeUpdated.subscribe { _, _ -> onAttributeUpdate() }
    }
}