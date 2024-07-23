package com.bibireden.playerex.ui.components.labels

import com.bibireden.data_attributes.api.DataAttributesAPI
import com.bibireden.data_attributes.api.attribute.IEntityAttribute
import com.bibireden.playerex.ext.id
import io.wispforest.owo.ui.component.LabelComponent
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Formatting

private fun createTextFromAttribute(attribute: EntityAttribute, player: PlayerEntity) = Text.literal("(")
    .append(Text.literal("${DataAttributesAPI.getValue(attribute, player).map(Double::toInt).orElse(0)}").formatted(Formatting.GOLD))
    .append("/${(attribute as IEntityAttribute).`data_attributes$max`().toInt()})")

class AttributeLabelComponent(private val attribute: EntityAttribute, private val player: PlayerEntity) : LabelComponent(createTextFromAttribute(attribute, player)) {
    init {
        this.id("${attribute.id}:current_level")
    }

    fun update(): LabelComponent = text(createTextFromAttribute(attribute, player))
}