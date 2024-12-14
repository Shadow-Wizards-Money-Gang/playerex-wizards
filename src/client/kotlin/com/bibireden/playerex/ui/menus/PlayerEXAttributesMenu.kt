package com.bibireden.playerex.ui.menus

import com.bibireden.data_attributes.api.DataAttributesAPI
import com.bibireden.data_attributes.api.attribute.EntityAttributeSupplier
import com.bibireden.data_attributes.ext.round
import com.bibireden.playerex.api.attribute.PlayerEXAttributes
import com.bibireden.playerex.ext.component
import com.bibireden.playerex.ext.id
import com.bibireden.playerex.ui.PlayerEXScreen
import com.bibireden.playerex.ui.childById
import com.bibireden.playerex.ui.components.*
import com.bibireden.playerex.ui.components.buttons.AttributeButtonComponent
import com.bibireden.playerex.ui.components.labels.AttributeLabelComponent
import com.bibireden.playerex.ui.helper.InputHelper
import de.dafuqs.additionalentityattributes.AdditionalEntityAttributes
import io.wispforest.owo.ui.component.BoxComponent
import io.wispforest.owo.ui.component.Components
import io.wispforest.owo.ui.component.TextBoxComponent
import io.wispforest.owo.ui.container.Containers
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.*
import net.fabric_extras.ranged_weapon.api.EntityAttributes_RangedWeapon
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Player
import net.minecraft.network.chat.Component
import net.minecraft.util.Mth
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Internal
class PlayerEXAttributesMenu : MenuComponent(algorithm = Algorithm.HORIZONTAL) {
    private val lungProgressBar = ProgressBarComponent(Sizing.fill(65), Sizing.fixed(4))
        .color(Color.ofRgb(0x399ce9), Color.ofRgb(0x68a8d9))
        .apply { positioning(Positioning.relative(50, 100)).id("progress:lung") }

    private val progressHealthBar = ProgressBarComponent(Sizing.fill(65), Sizing.fixed(4))
        .color(Color.ofRgb(0xea233a), Color.ofRgb(0xf3394f))
        .apply { positioning(Positioning.relative(50, 80)).id("progress:health") }

    private val labelHealth = Components.label(Component.literal("<n/a>"))
        .apply {
            color(Color.ofArgb(0x32FFFFFF))
            positioning(Positioning.relative(50, 74))
        }

    private val labelLung = Components.label(Component.literal("<n/a>"))
        .apply {
            color(Color.ofArgb(0x32FFFFFF))
            positioning(Positioning.relative(50, 94))
        }

    private val MELEE_COMBAT_STATS: List<EntityAttributeSupplier> = listOf(
        EntityAttributeSupplier(Attributes.ATTACK_DAMAGE.id),
        EntityAttributeSupplier(Attributes.ATTACK_SPEED.id),
        EntityAttributeSupplier(PlayerEXAttributes.MELEE_CRITICAL_DAMAGE.id),
        EntityAttributeSupplier(PlayerEXAttributes.MELEE_CRITICAL_CHANCE.id)
    )

    private val RANGED_COMBAT_STATS: List<EntityAttributeSupplier> = listOf(
        EntityAttributeSupplier(PlayerEXAttributes.RANGED_CRITICAL_DAMAGE.id),
        EntityAttributeSupplier(PlayerEXAttributes.RANGED_CRITICAL_CHANCE.id),
        EntityAttributeSupplier(EntityAttributes_RangedWeapon.HASTE.id),
        EntityAttributeSupplier(EntityAttributes_RangedWeapon.DAMAGE.id),
    )

    private val DEFENSE_COMBAT_STATS: List<EntityAttributeSupplier> = listOf(
        EntityAttributeSupplier(Attributes.ARMOR.id),
        EntityAttributeSupplier(AdditionalEntityAttributes.MAGIC_PROTECTION.id),
        EntityAttributeSupplier(Attributes.ARMOR_TOUGHNESS.id),
        EntityAttributeSupplier(Attributes.KNOCKBACK_RESISTANCE.id),
        EntityAttributeSupplier(PlayerEXAttributes.EVASION.id),
    )

    private val VITALITY_STATS: List<EntityAttributeSupplier> = listOf(
        EntityAttributeSupplier(PlayerEXAttributes.HEALTH_REGENERATION.id),
        EntityAttributeSupplier(PlayerEXAttributes.HEAL_AMPLIFICATION.id),
        EntityAttributeSupplier(PlayerEXAttributes.LIFESTEAL.id),
        EntityAttributeSupplier(Attributes.MOVEMENT_SPEED.id),
    )

    private val RESISTANCE_STATS: List<EntityAttributeSupplier> = listOf(
        EntityAttributeSupplier(PlayerEXAttributes.FIRE_RESISTANCE.id),
        EntityAttributeSupplier(PlayerEXAttributes.FREEZE_RESISTANCE.id),
        EntityAttributeSupplier(PlayerEXAttributes.LIGHTNING_RESISTANCE.id),
        EntityAttributeSupplier(PlayerEXAttributes.POISON_RESISTANCE.id),
        EntityAttributeSupplier(PlayerEXAttributes.WITHER_RESISTANCE.id),
    )

    /** Whenever ANY attribute gets updated. */
    private fun onAttributeUpdate() {
        // refresh all attribute labels
        this.forEachDescendant { component ->
            // a bit more tolerable
            when (component) {
                is AttributeComponent -> component.refresh()
                is AttributeLabelComponent -> component.refresh()
                is AttributeListEntryComponent -> component.refresh()
            }
        }
    }

    private fun onInputFieldUpdated(player: Player) {
        this.childById(FlowLayout::class, "attributes")?.childById(TextBoxComponent::class, "input")?.also {
            val result = it.value.toDoubleOrNull() ?: return@also
            this.forEachDescendant { descendant ->
                if (descendant is AttributeButtonComponent) {
                    val max = descendant.attribute.`data_attributes$max`()
                    val current = player.component.get(descendant.attribute)
                    when (descendant.type) {
                        PlayerEXScreen.ButtonType.Add -> descendant.active(result > 0 && player.component.skillPoints >= result && (current + result) <= max)
                        PlayerEXScreen.ButtonType.Remove -> descendant.active(result > 0 && player.component.refundablePoints > 0 && (current - result >= 0))
                    }
                }
            }
        }

    }

    private fun onHealthUpdated() {
        val player = client!!.player!!
        val percentage = Mth.clamp((player.health / player.maxHealth).toDouble(), 0.0, 1.0)
        progressHealthBar.percentage = percentage
        labelHealth.text(Component.translatable("playerex.ui.main.labels.health").append(" - ${player.health.toDouble().round(1)}"))
    }

    private fun onLungCapacityUpdated() {
        val player = client!!.player!!
        val percentage = Mth.clamp(player.airSupply.toDouble() / player.maxAirSupply, 0.0, 1.0)
        lungProgressBar.percentage = percentage
        labelLung.text(Component.translatable("attribute.name.additionalentityattributes.generic.lung_capacity").append(" - ${player.airSupply}"))
    }

    override fun build(screenRoot: FlowLayout) {
        val player = client?.player ?: return
        val component = playerComponent ?: return

        // -- divider line -- //

        child(Components.box(Sizing.fixed(2), Sizing.fill(100)).color(Color.ofArgb(0x32FFFFFF)).positioning(Positioning.relative(36, 50)))
        child(Components.box(Sizing.fill(64), Sizing.fixed(2)).color(Color.ofArgb(0x32FFFFFF)).positioning(Positioning.relative(100, 50)))

        child(Containers.verticalScroll(Sizing.fill(35), Sizing.fill(70), Containers.verticalFlow(Sizing.fill(100), Sizing.content(6)).apply {
            verticalAlignment(VerticalAlignment.CENTER)
            gap(10)
            padding(Insets.right(5))

            child(Containers.horizontalFlow(Sizing.fill(100), Sizing.content(4)).apply {
                verticalAlignment(VerticalAlignment.CENTER)

                child(Components.label(Component.translatable("playerex.ui.category.attributes")).color(Color.ofArgb(0x32FFFFFF)))
                child(
                    Components.textBox(Sizing.fixed(27))
                        .text("1")
                        .also {
                            it.setMaxLength(3)
                            it.setFilter(InputHelper::isUIntInput)
                            it.onChanged().subscribe { onInputFieldUpdated(player) }
                        }
                        .verticalSizing(Sizing.fixed(15))
                        .positioning(Positioning.relative(100, 50))
                        .id("input")
                )
            })

            child(
                Components.box(Sizing.fill(100), Sizing.fixed(2))
                    .color(Color.ofArgb(0x36FFFFFF))
            )

            for (id in PlayerEXAttributes.PRIMARY_ATTRIBUTE_IDS) {
                val attribute = BuiltInRegistries.ATTRIBUTE.get(id) ?: continue
                child(AttributeComponent(attribute, player, component))
                child(
                    Components.box(Sizing.fill(100), Sizing.fixed(2)).fill(true)
                        .color(Color.ofArgb(0x10FFFFFF))
                )
            }
        }
            .verticalAlignment(VerticalAlignment.CENTER)
            .id("attributes")
        )
            .positioning(Positioning.relative(0, 50))
        )

        // VIGOR

        child(
            Containers.verticalFlow(Sizing.fill(65), Sizing.fill(46))
                .apply {
                    child(Components.label(Component.translatable("playerex.ui.main.sections.vigor")).color(Color.ofArgb(0x32FFFFFF))
                        .horizontalSizing(Sizing.content()))

                    // health progress
                    child(labelHealth)
                    child(progressHealthBar)

                    // lung progress
                    child(labelLung)
                    child(lungProgressBar)

                    child(Containers.verticalScroll(
                        Sizing.fill(100),
                        Sizing.fill(50),
                        Containers.horizontalFlow(Sizing.fill(100), Sizing.content()).apply {
                            child(AttributeListComponent("playerex.ui.main.categories.vitality", player, VITALITY_STATS).horizontalSizing(Sizing.fill(45)))
                            child(AttributeListComponent("playerex.ui.main.categories.resistance", player, RESISTANCE_STATS).horizontalSizing(Sizing.fill(45)))
                            gap(8)
                            alignment(HorizontalAlignment.CENTER, VerticalAlignment.TOP)
                        }
                    )
                        .positioning(Positioning.relative(50, 20))
                    )
                }
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .positioning(Positioning.relative(100, 100))
        )

        // COMBAT STATS
        child(
            Containers.verticalFlow(Sizing.fill(63), Sizing.fill(46))
                .apply {
                    child(Components.label(Component.translatable("playerex.ui.main.sections.combat_stats")).color(Color.ofArgb(0x32FFFFFF))
                        .horizontalSizing(Sizing.content()))

                    child(
                        Containers.verticalScroll(
                            Sizing.fill(100),
                            Sizing.fill(90),
                            Containers.horizontalFlow(Sizing.fill(100), Sizing.content()).apply {
                                child(AttributeListComponent("playerex.ui.main.categories.melee_combat", player, MELEE_COMBAT_STATS).horizontalSizing(Sizing.fill(30)))
                                child(AttributeListComponent("playerex.ui.main.categories.ranged_combat", player, RANGED_COMBAT_STATS).horizontalSizing(Sizing.fill(32)))
                                child(AttributeListComponent("playerex.ui.main.categories.defense_combat", player, DEFENSE_COMBAT_STATS).horizontalSizing(Sizing.fill(34)))
                                padding(Insets.vertical(12))
                                gap(6)
                                alignment(HorizontalAlignment.CENTER, VerticalAlignment.TOP)
                            }
                                .positioning(Positioning.relative(50, 100))
                                .id("combat_stats")
                        )
                    )
                }
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .positioning(Positioning.relative(100, 0))
        )

        gap(10)

        padding(Insets.both(8, 8))

        onAttributeUpdate()
        onInputFieldUpdated(player)

        onLungCapacityUpdated()
        onHealthUpdated()

        onAttributeUpdated.subscribe { attribute, _ ->
            onAttributeUpdate()
            onInputFieldUpdated(player)

            if (attribute == AdditionalEntityAttributes.LUNG_CAPACITY) {
                onLungCapacityUpdated()
            }
            else if (attribute == Attributes.MAX_HEALTH) {
                onHealthUpdated()
            }
        }
    }
}