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
import com.bibireden.playerex.ui.components.buttons.AttributeButtonComponent
import com.bibireden.playerex.ui.components.labels.AttributeLabelComponent
import com.bibireden.playerex.ui.util.Colors
import io.wispforest.owo.ui.component.Components
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.*
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import kotlin.jvm.optionals.getOrNull

private val StackingBehavior.symbol: String
    get() = if (this == StackingBehavior.Add) "+" else "×"

class AttributeComponent(private val attribute: EntityAttribute, private val player: PlayerEntity, component: IPlayerDataComponent) : FlowLayout(Sizing.fill(100), Sizing.fixed(18), Algorithm.HORIZONTAL) {
    val label: AttributeLabelComponent

    fun refresh() {
        val entries = DataAttributesAPI.clientManager.data.functions[attribute.id]
        if (!entries.isNullOrEmpty()) {
            label.tooltip(
                Text.translatable("playerex.ui.main.modified_attributes").also { text ->
                    text.append("\n")
                    text.append(Text.literal(attribute.id.toString()).formatted(Formatting.DARK_GRAY))
                    text.append("\n\n")
                    entries.forEach { function ->
                        val childAttribute = EntityAttributeSupplier(function.id).get().getOrNull() ?: return@forEach
                        val formula = (childAttribute as IEntityAttribute).`data_attributes$formula`()

                        text.apply {
                            append(Text.translatable(childAttribute.translationKey).styled { it.withColor(Colors.SATURATED_BLUE) })
                            append(" (")
                            append(Text.literal(function.behavior.symbol).styled { it.withColor(Colors.DARK_GREEN) })
                            append(Text.literal("${function.value}"))
                            append(Text.literal(":").fillStyle(Style.EMPTY.withColor(Colors.DARK_GRAY)))
                            append(Text.literal(formula.name.lowercase()).styled { it.withColor(if (formula == StackingFormula.Flat) Colors.SANDY else Colors.IMPISH_RED) })
                            append(")")
                            val decLength = function.value.toString().substringAfter('.').length
                            append(Text.literal(" (%.${decLength}f)\n".format(DataAttributesAPI.getValue(childAttribute, player).orElse(0.0))).formatted(Formatting.GRAY))
                        }
                    }
                }
            )
        }
    }

    init {
        child(
            Components.label(Text.translatable(attribute.translationKey))
                .verticalTextAlignment(VerticalAlignment.CENTER)
                .sizing(Sizing.content(), Sizing.fill(100))
                .positioning(Positioning.relative(0, 50))
                .id("${attribute.id}:label")
        )

        child(AttributeButtonComponent(attribute, player, component, AttributeButtonComponentType.Remove))
        child(
            AttributeLabelComponent(attribute, player).also { label = it }
                .horizontalSizing(Sizing.fill(34))
        )
        child(AttributeButtonComponent(attribute, player, component, AttributeButtonComponentType.Add))

        horizontalAlignment(HorizontalAlignment.RIGHT)
        verticalAlignment(VerticalAlignment.CENTER)

        verticalAlignment(VerticalAlignment.CENTER)

        refresh()
    }
}