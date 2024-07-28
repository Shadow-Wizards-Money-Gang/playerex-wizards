package com.bibireden.playerex.ui.components

import com.bibireden.data_attributes.api.attribute.EntityAttributeSupplier
import com.bibireden.playerex.ui.util.FormattingPredicates
import io.wispforest.owo.ui.component.Components
import io.wispforest.owo.ui.container.Containers
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.Insets
import io.wispforest.owo.ui.core.Positioning
import io.wispforest.owo.ui.core.Sizing
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text

private fun transform(array: List<Pair<EntityAttributeSupplier, FormattingPredicates>>): List<Pair<EntityAttribute, FormattingPredicate>> {
    val filtered = mutableListOf<Pair<EntityAttribute, FormattingPredicate>>()
    for ((attribute, pred) in array) {
        if (attribute.get().isPresent) filtered.add(Pair(attribute.get().get(), pred.predicate))
    }
    return filtered
}

class AttributeListComponent(translationKey: String, private val player: PlayerEntity, private val gimmie: List<Pair<EntityAttributeSupplier, FormattingPredicates>>) : FlowLayout(Sizing.fill(25), Sizing.content(), Algorithm.VERTICAL) {
    val entriesSection: FlowLayout

    init {
        child(
            Components.label(Text.translatable(translationKey))
                .horizontalSizing(Sizing.fill(100))
        )
        child(Components.box(Sizing.fill(100), Sizing.fixed(2)))
        entriesSection = Containers.verticalFlow(Sizing.fill(100), Sizing.content())
            .apply {
                gap(4)
            }.also(::child)

        gap(4)
        refresh()
    }

    fun refresh() {
        entriesSection.children().filterIsInstance<AttributeListEntryComponent>().forEach(::removeChild)
        entriesSection.children(transform(gimmie).map {
            Containers.horizontalScroll(Sizing.fill(100), Sizing.content(), AttributeListEntryComponent(it.first, player, it.second)).scrollbarThiccness(2)
        })
    }
}