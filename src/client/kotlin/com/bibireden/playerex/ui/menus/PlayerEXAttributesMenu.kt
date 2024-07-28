package com.bibireden.playerex.ui.menus

import com.bibireden.data_attributes.api.DataAttributesAPI
import com.bibireden.data_attributes.api.attribute.EntityAttributeSupplier
import com.bibireden.data_attributes.api.attribute.IEntityAttribute
import com.bibireden.playerex.api.attribute.ModdedAttributes
import com.bibireden.playerex.api.attribute.PlayerEXAttributes
import com.bibireden.playerex.components.player.IPlayerDataComponent
import com.bibireden.playerex.ext.id
import com.bibireden.playerex.ext.level
import com.bibireden.playerex.ui.PlayerEXScreen
import com.bibireden.playerex.ui.childById
import com.bibireden.playerex.ui.components.*
import com.bibireden.playerex.ui.components.buttons.AttributeButtonComponent
import com.bibireden.playerex.ui.components.labels.AttributeLabelComponent
import com.bibireden.playerex.ui.util.FormattingPredicates
import de.dafuqs.additionalentityattributes.AdditionalEntityAttributes
import io.wispforest.owo.ui.component.Components
import io.wispforest.owo.ui.component.LabelComponent
import io.wispforest.owo.ui.component.TextBoxComponent
import io.wispforest.owo.ui.container.Containers
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.*
import net.fabric_extras.ranged_weapon.api.EntityAttributes_RangedWeapon
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.registry.Registries
import net.minecraft.text.Text
import org.jetbrains.annotations.ApiStatus

// todo: cache buttons/certain UI elements

@ApiStatus.Internal
class PlayerEXAttributesMenu : MenuComponent(algorithm = Algorithm.VERTICAL) {

    private fun onLevelUpdate(level: Int) {}

    /** Whenever ANY attribute gets updated. */
    private fun onAttributeUpdate() {
        // refresh all attribute labels
        this.forEachDescendant { component ->
            // todo: use derived interface to check instance
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
        child(Containers.verticalScroll(
            Sizing.fill(35),
            Sizing.fill(100),
            Containers.verticalFlow(Sizing.fill(100), Sizing.content()).apply {
                child(Containers.horizontalFlow(Sizing.fill(100), Sizing.content(2)).apply {
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
                })
                child(Components.box(Sizing.fill(100), Sizing.fixed(2)))
                verticalAlignment(VerticalAlignment.CENTER)
                gap(5)
                children(PlayerEXAttributes.PRIMARY_ATTRIBUTE_IDS.mapNotNull(Registries.ATTRIBUTE::get).map { AttributeComponent(it, player, component) })
            }.id("attributes"))
        )

        padding(Insets.both(8, 8))

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