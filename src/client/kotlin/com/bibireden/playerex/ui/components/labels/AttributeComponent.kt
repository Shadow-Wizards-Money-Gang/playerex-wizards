package com.bibireden.playerex.ui.components.labels

import com.bibireden.data_attributes.DataAttributes
import com.bibireden.data_attributes.api.DataAttributesAPI
import com.bibireden.data_attributes.api.attribute.EntityAttributeSupplier
import com.bibireden.data_attributes.api.attribute.StackingBehavior
import com.bibireden.playerex.components.player.IPlayerDataComponent
import com.bibireden.playerex.ext.id
import com.bibireden.playerex.ui.PlayerEXScreen.AttributeButtonComponentType
import com.bibireden.playerex.ui.childById
import io.wispforest.owo.ui.component.Components
import io.wispforest.owo.ui.component.LabelComponent
import io.wispforest.owo.ui.container.Containers
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.Positioning
import io.wispforest.owo.ui.core.Sizing
import io.wispforest.owo.ui.core.VerticalAlignment
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Formatting

private val StackingBehavior.symbol: String
    get() = if (this == StackingBehavior.Add) "+" else "×"

class AttributeComponent(val attribute: EntityAttribute, val player: PlayerEntity, component: IPlayerDataComponent) : FlowLayout(Sizing.fill(100), Sizing.fixed(18), Algorithm.HORIZONTAL) {
    fun updateTooltip() {
        // todo: allow data_attributes to have API funcs for obtaining this data.
        val entries = DataAttributes.FUNCTIONS_CONFIG.functions.data[attribute.id]
        if (!entries.isNullOrEmpty()) {
            this.childById(LabelComponent::class, "${this.attribute.id}:label")?.tooltip(
                Text.translatable("playerex.ui.attribute_functions").also { text ->
                text.append("\n")
                text.append(Text.literal(attribute.id.toString()).formatted(Formatting.DARK_GRAY))
                text.append("\n\n")
                entries.forEach { function ->
                    val childAttribute = EntityAttributeSupplier(function.id).get() ?: return@forEach
                    text.append(Text.translatable(childAttribute.translationKey).formatted(Formatting.AQUA))
                    text.append(Text.literal(" ${function.behavior.symbol}").formatted(Formatting.GREEN))
                    text.append(Text.literal("${function.value}"))
                    text.append(
                        Text.literal(
                            String.format(
                                " (%.2f)\n",
                                DataAttributesAPI.getValue(EntityAttributeSupplier(function.id), player).orElse(0.0)
                            )
                        ).formatted(Formatting.GRAY)
                    )
                }
                text.formatted(Formatting.ITALIC)
            })
        }
    }

    init {
        this.child(
            Components.label(Text.translatable(attribute.translationKey)).sizing(Sizing.content(), Sizing.fill(100))
                .also { this.updateTooltip() }.id("${attribute.id}:label")
        )
        this.child(AttributeLabelComponent(attribute, player))
        this.child(
            Containers.horizontalFlow(Sizing.fill(50), Sizing.fill(100)).also {
                it.child(AttributeButtonComponent(attribute, player, component, AttributeButtonComponentType.Remove))
                it.child(AttributeButtonComponent(attribute, player, component, AttributeButtonComponentType.Add))
                it.child(Components.textBox(Sizing.fixed(27)).text("1").verticalSizing(Sizing.fixed(12)).id("entry:${attribute.id}"))
                it.gap(4)
            }.positioning(Positioning.relative(100, 0)).verticalAlignment(VerticalAlignment.CENTER)
        )
        this.gap(3)
    }
}