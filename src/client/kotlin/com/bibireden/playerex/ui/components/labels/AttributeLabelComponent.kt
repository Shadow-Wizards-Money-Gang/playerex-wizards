package com.bibireden.playerex.ui.components.labels

import com.bibireden.data_attributes.api.DataAttributesAPI
import com.bibireden.data_attributes.api.attribute.IEntityAttribute
import com.bibireden.playerex.ext.id
import com.bibireden.playerex.ui.util.Colors
import io.wispforest.owo.ui.component.LabelComponent
import io.wispforest.owo.ui.core.Sizing
import io.wispforest.owo.ui.core.VerticalAlignment
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Formatting

private fun createTextFromAttribute(attribute: EntityAttribute, player: PlayerEntity) = Text.literal("(")
    .append(Text.literal("${DataAttributesAPI.getValue(attribute, player).map(Double::toInt).orElse(0)}").styled {
        it.withColor(Colors.GOLD)
    })
    .append("/${(attribute as IEntityAttribute).`data_attributes$max`().toInt()})")

class AttributeLabelComponent(private val attribute: EntityAttribute, private val player: PlayerEntity) : LabelComponent(createTextFromAttribute(attribute, player)) {
    init {
        id("${attribute.id}:current_level")
        verticalSizing(Sizing.fill(100))
        verticalTextAlignment(VerticalAlignment.CENTER)
    }

    fun refresh(): LabelComponent = text(createTextFromAttribute(attribute, player))
}