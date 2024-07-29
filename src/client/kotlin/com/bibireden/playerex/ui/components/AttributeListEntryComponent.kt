package com.bibireden.playerex.ui.components

import com.bibireden.data_attributes.api.DataAttributesAPI
import com.bibireden.playerex.ext.id
import com.bibireden.playerex.ui.util.Colors
import io.wispforest.owo.ui.component.LabelComponent
import io.wispforest.owo.ui.core.HorizontalAlignment
import io.wispforest.owo.ui.core.VerticalAlignment
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text

typealias FormattingPredicate = (Double) -> String

class AttributeListEntryComponent(
    val attribute: EntityAttribute,
    val player: PlayerEntity,
    private val formattingPredicate: FormattingPredicate
) : LabelComponent(Text.empty()) {
    init {
        horizontalTextAlignment(HorizontalAlignment.CENTER)
        verticalTextAlignment(VerticalAlignment.CENTER)

        refresh()
    }

    fun refresh() {
        text(
            Text.translatable(attribute.translationKey)
                .append(": ")
                .append(Text.literal(
                    DataAttributesAPI.getValue(attribute, player).map { formattingPredicate(it) }
                        .orElse("N/A")).styled { it.withColor(Colors.GOLD) }
                )
        )
    }
}