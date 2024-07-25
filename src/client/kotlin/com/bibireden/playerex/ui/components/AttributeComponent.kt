package com.bibireden.playerex.ui.components

import com.bibireden.data_attributes.DataAttributes
import com.bibireden.data_attributes.DataAttributesClient
import com.bibireden.data_attributes.api.DataAttributesAPI
import com.bibireden.data_attributes.api.attribute.EntityAttributeSupplier
import com.bibireden.data_attributes.api.attribute.IEntityAttribute
import com.bibireden.data_attributes.api.attribute.StackingBehavior
import com.bibireden.data_attributes.api.attribute.StackingFormula
import com.bibireden.playerex.components.player.IPlayerDataComponent
import com.bibireden.playerex.ext.id
import com.bibireden.playerex.ui.PlayerEXScreen.AttributeButtonComponentType
import com.bibireden.playerex.ui.childById
import com.bibireden.playerex.ui.components.buttons.AttributeButtonComponent
import com.bibireden.playerex.ui.components.labels.AttributeLabelComponent
import io.wispforest.owo.ui.component.Components
import io.wispforest.owo.ui.component.LabelComponent
import io.wispforest.owo.ui.container.Containers
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.HorizontalAlignment
import io.wispforest.owo.ui.core.Positioning
import io.wispforest.owo.ui.core.Sizing
import io.wispforest.owo.ui.core.VerticalAlignment
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Formatting

private val StackingBehavior.symbol: String
    get() = if (this == StackingBehavior.Add) "+" else "×"

class AttributeComponent(val attribute: EntityAttribute, val player: PlayerEntity, component: IPlayerDataComponent) : FlowLayout(Sizing.fill(35), Sizing.fixed(18), Algorithm.HORIZONTAL) {
    fun refresh() {
        // todo: allow data_attributes to have API funcs for obtaining this data.
        val entries = DataAttributesClient.MANAGER.data.functions[attribute.id]
        if (!entries.isNullOrEmpty()) {
            this.childById(LabelComponent::class, "${this.attribute.id}:label")?.tooltip(
                Text.translatable("playerex.ui.main.modified_attributes").also { text ->
                text.append("\n")
                text.append(Text.literal(attribute.id.toString()).formatted(Formatting.DARK_GRAY))
                text.append("\n\n")
                entries.forEach { function ->
                    val childAttribute = EntityAttributeSupplier(function.id).get() ?: return@forEach
                    val formula = (childAttribute as IEntityAttribute).`data_attributes$formula`()

                    text.apply {
                        append(Text.translatable(childAttribute.translationKey).styled { it.withColor(0x6EBAE5) })
                        append(" [")
                        append(Text.literal(formula.name.uppercase()).styled { it.withColor(if (formula == StackingFormula.Flat) 0xEDCD76 else 0xD63042) })
                        append("] ")
                        append(Text.literal(function.behavior.symbol).styled { it.withColor(0x48D19B) })
                        append(Text.literal("${function.value}"))
                        append(
                            Text.literal(
                                String.format(
                                    " (%.2f)\n",
                                    DataAttributesAPI.getValue(EntityAttributeSupplier(function.id), player).orElse(0.0)
                                )
                            ).formatted(Formatting.GRAY)
                        )
                        formatted(Formatting.ITALIC)
                    }
                }
            })
        }
    }

    init {
        this.child(
            Components.label(Text.translatable(attribute.translationKey))
                .verticalTextAlignment(VerticalAlignment.CENTER)
                .sizing(Sizing.content(), Sizing.fill(100))
                .also { this.refresh() }
                .id("${attribute.id}:label")
        )
        this.child(AttributeLabelComponent(attribute, player))
        this.child(
            Containers.horizontalFlow(Sizing.content(), Sizing.fill(100)).also {
                it.child(AttributeButtonComponent(attribute, player, component, AttributeButtonComponentType.Remove))
                it.child(AttributeButtonComponent(attribute, player, component, AttributeButtonComponentType.Add))
                it.child(Components.textBox(Sizing.fixed(27)).text("1").verticalSizing(Sizing.fixed(12)).id("entry:${attribute.id}"))
                it.gap(4)
            }
            .horizontalAlignment(HorizontalAlignment.RIGHT)
            .verticalAlignment(VerticalAlignment.CENTER)
            .positioning(Positioning.relative(100, 0))
        )
        this.gap(3)
        this.verticalAlignment(VerticalAlignment.CENTER)
    }
}