package com.bibireden.playerex.ui.menus

import com.bibireden.data_attributes.api.attribute.StackingBehavior
import com.bibireden.playerex.api.attribute.PlayerEXAttributes
import com.bibireden.playerex.components.player.IPlayerDataComponent
import com.bibireden.playerex.ext.level
import com.bibireden.playerex.ui.childById
import com.bibireden.playerex.ui.components.MenuComponent
import com.bibireden.playerex.ui.components.labels.AttributeComponent
import com.bibireden.playerex.ui.components.labels.AttributeLabelComponent
import io.wispforest.owo.ui.component.Components
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.OwoUIAdapter
import io.wispforest.owo.ui.core.Positioning
import io.wispforest.owo.ui.core.Sizing
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.registry.Registries
import net.minecraft.text.Text

private val StackingBehavior.symbol: String
    get() = if (this == StackingBehavior.Add) "+" else "×"

class AttributeMenu : MenuComponent(Sizing.fill(75), Sizing.content(), Algorithm.VERTICAL) {
    private fun onLevelUpdate(level: Int) {

    }

    private fun onAttributeUpdate(attribute: EntityAttribute? = null, value: Double? = null) {
        for (id in PlayerEXAttributes.PRIMARY_ATTRIBUTE_IDS) {
            this.childById(AttributeLabelComponent::class, "${id}:current_level")!!.update()
        }
    }

    override fun build(player: ClientPlayerEntity, adapter: OwoUIAdapter<FlowLayout>, component: IPlayerDataComponent) {
        child(Components.label(Text.translatable("playerex.ui.category.primary_attributes")))
        child(Components.box(Sizing.fill(60), Sizing.fixed(2)))
        gap(5)
        children(PlayerEXAttributes.PRIMARY_ATTRIBUTE_IDS.mapNotNull(Registries.ATTRIBUTE::get).map { AttributeComponent(it, player, component) })
        positioning(Positioning.relative(10, 25))

        this.onLevelUpdate(player.level.toInt())
        this.onAttributeUpdate()

        this.onLevelUpdated.subscribe(::onLevelUpdate)
        this.onAttributeUpdated.subscribe(::onAttributeUpdate)
    }
}