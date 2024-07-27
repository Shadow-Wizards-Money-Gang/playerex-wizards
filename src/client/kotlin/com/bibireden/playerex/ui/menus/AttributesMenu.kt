package com.bibireden.playerex.ui.menus

import com.bibireden.data_attributes.api.DataAttributesAPI
import com.bibireden.data_attributes.api.attribute.IEntityAttribute
import com.bibireden.playerex.api.attribute.PlayerEXAttributes
import com.bibireden.playerex.components.player.IPlayerDataComponent
import com.bibireden.playerex.ext.level
import com.bibireden.playerex.ui.PlayerEXScreen
import com.bibireden.playerex.ui.childById
import com.bibireden.playerex.ui.components.MenuComponent
import com.bibireden.playerex.ui.components.AttributeComponent
import com.bibireden.playerex.ui.components.buttons.AttributeButtonComponent
import com.bibireden.playerex.ui.components.labels.AttributeLabelComponent
import com.bibireden.playerex.util.PlayerEXUtil
import io.wispforest.owo.ui.component.Components
import io.wispforest.owo.ui.component.TextBoxComponent
import io.wispforest.owo.ui.container.Containers
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.Insets
import io.wispforest.owo.ui.core.OwoUIAdapter
import io.wispforest.owo.ui.core.Positioning
import io.wispforest.owo.ui.core.Sizing
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.registry.Registries
import net.minecraft.text.Text

// todo: cache buttons/certain UI elements

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

    private fun onInputFieldUpdated(player: PlayerEntity, component: IPlayerDataComponent) {
        this.childById(FlowLayout::class, "attributes")?.childById(TextBoxComponent::class, "input")?.also {
            val result = it.text.toDoubleOrNull() ?: return@also
            this.forEachDescendant { descendant ->
                if (descendant is AttributeButtonComponent) {
                    val max = (descendant.attribute as IEntityAttribute).`data_attributes$max`()
                    val current = DataAttributesAPI.getValue(descendant.attribute, player).orElse(0.0)
                    when (descendant.type) {
                        PlayerEXScreen.AttributeButtonComponentType.Add -> descendant.active(result > 0 && component.skillPoints >= result && (current + result) <= max)
                        PlayerEXScreen.AttributeButtonComponentType.Remove -> descendant.active(result > 0 && component.refundablePoints > 0 && (current - result > 0))
                    }
                }
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
                        it.onChanged().subscribe { onInputFieldUpdated(player, component) }
                    }
                    .text("1")
                    .verticalSizing(Sizing.fixed(10))
                    .positioning(Positioning.relative(100, 0))
                    .id("input")
            )
            child(Components.box(Sizing.fill(100), Sizing.fixed(2)))
            gap(5)
            children(PlayerEXAttributes.PRIMARY_ATTRIBUTE_IDS.mapNotNull(Registries.ATTRIBUTE::get).map { AttributeComponent(it, player, component) })
        }.id("attributes"))

        padding(Insets.both(12, 12))

        onLevelUpdate(player.level.toInt())
        onAttributeUpdate()
        onInputFieldUpdated(player, component)

        onLevelUpdated.subscribe(::onLevelUpdate)
        onAttributeUpdated.subscribe { _, _ ->
            onAttributeUpdate()
            onInputFieldUpdated(player, component)
        }
    }
}