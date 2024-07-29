package com.bibireden.playerex.ui.menus

import com.bibireden.data_attributes.api.DataAttributesAPI
import com.bibireden.data_attributes.api.attribute.EntityAttributeSupplier
import com.bibireden.data_attributes.api.attribute.IEntityAttribute
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
class PlayerEXAttributesMenu : MenuComponent(algorithm = Algorithm.HORIZONTAL) {
    private val MELEE_COMBAT_STATS: List<Pair<EntityAttributeSupplier, FormattingPredicate>> = listOf(
        EntityAttributeSupplier(EntityAttributes.GENERIC_ATTACK_DAMAGE.id) to FormattingPredicates.NORMAL,
        EntityAttributeSupplier(EntityAttributes.GENERIC_ATTACK_SPEED.id) to FormattingPredicates.NORMAL,
        EntityAttributeSupplier(PlayerEXAttributes.MELEE_CRITICAL_DAMAGE.id) to FormattingPredicates.PERCENTAGE_DIVIDE,
        EntityAttributeSupplier(PlayerEXAttributes.MELEE_CRITICAL_CHANCE.id) to FormattingPredicates.PERCENTAGE_DIVIDE
    )

    private val RANGED_COMBAT_STATS: List<Pair<EntityAttributeSupplier, FormattingPredicate>> = listOf(
        EntityAttributeSupplier(PlayerEXAttributes.RANGED_DAMAGE.id) to FormattingPredicates.NORMAL,
        EntityAttributeSupplier(PlayerEXAttributes.RANGED_CRITICAL_DAMAGE.id) to FormattingPredicates.PERCENTAGE_DIVIDE,
        EntityAttributeSupplier(PlayerEXAttributes.RANGED_CRITICAL_CHANCE.id) to FormattingPredicates.PERCENTAGE_DIVIDE,
        EntityAttributeSupplier(EntityAttributes_RangedWeapon.HASTE.id) to FormattingPredicates.fromBaseValue(EntityAttributes_RangedWeapon.HASTE.attribute),
    )

    private val DEFENSE_COMBAT_STATS: List<Pair<EntityAttributeSupplier, FormattingPredicate>> = listOf(
        EntityAttributeSupplier(EntityAttributes.GENERIC_ARMOR.id) to FormattingPredicates.NORMAL,
        EntityAttributeSupplier(AdditionalEntityAttributes.MAGIC_PROTECTION.id) to FormattingPredicates.NORMAL,
        EntityAttributeSupplier(EntityAttributes.GENERIC_ARMOR_TOUGHNESS.id) to FormattingPredicates.NORMAL,
        EntityAttributeSupplier(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE.id) to FormattingPredicates.PERCENTAGE_DIVIDE,
        EntityAttributeSupplier(PlayerEXAttributes.EVASION.id) to FormattingPredicates.PERCENTAGE_DIVIDE,
    )

    private val VITALITY_STATS: List<Pair<EntityAttributeSupplier, FormattingPredicate>> = listOf(
        EntityAttributeSupplier(PlayerEXAttributes.HEALTH_REGENERATION.id) to FormattingPredicates.NORMAL,
        EntityAttributeSupplier(PlayerEXAttributes.HEAL_AMPLIFICATION.id) to FormattingPredicates.NORMAL,
        EntityAttributeSupplier(PlayerEXAttributes.LIFESTEAL.id) to FormattingPredicates.PERCENTAGE_DIVIDE,
        EntityAttributeSupplier(EntityAttributes.GENERIC_MOVEMENT_SPEED.id) to FormattingPredicates.NORMAL,
    )

    private val RESISTANCE_STATS: List<Pair<EntityAttributeSupplier, FormattingPredicate>> = listOf(
        EntityAttributeSupplier(PlayerEXAttributes.FIRE_RESISTANCE.id) to FormattingPredicates.PERCENTAGE_MULTIPLY,
        EntityAttributeSupplier(PlayerEXAttributes.FREEZE_RESISTANCE.id) to FormattingPredicates.PERCENTAGE_MULTIPLY,
        EntityAttributeSupplier(PlayerEXAttributes.LIGHTNING_RESISTANCE.id) to FormattingPredicates.PERCENTAGE_MULTIPLY,
        EntityAttributeSupplier(PlayerEXAttributes.POISON_RESISTANCE.id) to FormattingPredicates.PERCENTAGE_MULTIPLY,
    )

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
            if (component is AttributeListEntryComponent) {
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
            Sizing.fill(45),
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
                verticalAlignment(VerticalAlignment.TOP)
                gap(5)
                padding(Insets.right(5))
                children(PlayerEXAttributes.PRIMARY_ATTRIBUTE_IDS.mapNotNull(Registries.ATTRIBUTE::get).map { AttributeComponent(it, player, component) })
            }.id("attributes"))
        )
        child(Containers.verticalScroll(
            Sizing.content(),
            Sizing.fill(100),
            Containers.verticalFlow(Sizing.content(), Sizing.content()).apply {
                child(AttributeListComponent("playerex.ui.main.categories.vitality", player, VITALITY_STATS))
                child(AttributeListComponent("playerex.ui.main.categories.resistance", player, RESISTANCE_STATS))
                padding(Insets.right(5))
                gap(8)
            }
        ))

        child(
            Containers.verticalScroll(
                Sizing.content(),
                Sizing.fill(100),
                Containers.verticalFlow(Sizing.content(), Sizing.content()).apply {
                    child(AttributeListComponent("playerex.ui.main.categories.melee_combat", player, MELEE_COMBAT_STATS))
                    child(AttributeListComponent("playerex.ui.main.categories.ranged_combat", player, RANGED_COMBAT_STATS))
                    child(AttributeListComponent("playerex.ui.main.categories.defense_combat", player, DEFENSE_COMBAT_STATS))
                    padding(Insets.right(5))
                    gap(10)
                }.id("combat_stats")
            )
        )

        gap(10)

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