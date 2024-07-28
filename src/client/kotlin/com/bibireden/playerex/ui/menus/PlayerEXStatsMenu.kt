package com.bibireden.playerex.ui.menus

import com.bibireden.data_attributes.api.attribute.EntityAttributeSupplier
import com.bibireden.playerex.api.attribute.ModdedAttributes
import com.bibireden.playerex.api.attribute.PlayerEXAttributes
import com.bibireden.playerex.components.player.IPlayerDataComponent
import com.bibireden.playerex.ext.id
import com.bibireden.playerex.ui.components.AttributeListComponent
import com.bibireden.playerex.ui.components.AttributeListEntryComponent
import com.bibireden.playerex.ui.components.MenuComponent
import com.bibireden.playerex.ui.util.FormattingPredicates
import de.dafuqs.additionalentityattributes.AdditionalEntityAttributes
import io.wispforest.owo.ui.component.Components
import io.wispforest.owo.ui.container.Containers
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.Insets
import io.wispforest.owo.ui.core.OwoUIAdapter
import io.wispforest.owo.ui.core.Positioning
import io.wispforest.owo.ui.core.Sizing
import net.fabric_extras.ranged_weapon.api.EntityAttributes_RangedWeapon
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.text.Text
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Internal
class PlayerEXStatsMenu : MenuComponent(algorithm = Algorithm.VERTICAL) {
    val MELEE_COMBAT_STATS: List<Pair<EntityAttributeSupplier, FormattingPredicates>> = listOf(
        EntityAttributeSupplier(EntityAttributes.GENERIC_ATTACK_DAMAGE.id) to FormattingPredicates.Normal,
        EntityAttributeSupplier(EntityAttributes.GENERIC_ATTACK_SPEED.id) to FormattingPredicates.Normal,
        ModdedAttributes.ATTACK_RANGE to FormattingPredicates.Normal,
        EntityAttributeSupplier(PlayerEXAttributes.MELEE_CRIT_DAMAGE.id) to FormattingPredicates.PercentageDiv,
        EntityAttributeSupplier(PlayerEXAttributes.MELEE_CRIT_CHANCE.id) to FormattingPredicates.PercentageDiv
    )

    val RANGED_COMBAT_STATS: List<Pair<EntityAttributeSupplier, FormattingPredicates>> = listOf(
        EntityAttributeSupplier(PlayerEXAttributes.RANGED_DAMAGE.id) to FormattingPredicates.Normal,
        EntityAttributeSupplier(PlayerEXAttributes.RANGED_CRITICAL_DAMAGE.id) to FormattingPredicates.PercentageDiv,
        EntityAttributeSupplier(PlayerEXAttributes.RANGED_CRITICAL_CHANCE.id) to FormattingPredicates.PercentageDiv,
        EntityAttributeSupplier(EntityAttributes_RangedWeapon.HASTE.id) to FormattingPredicates.Percent,
    )

    val DEFENSE_COMBAT_STATS: List<Pair<EntityAttributeSupplier, FormattingPredicates>> = listOf(
        EntityAttributeSupplier(EntityAttributes.GENERIC_ARMOR.id) to FormattingPredicates.Normal,
        EntityAttributeSupplier(AdditionalEntityAttributes.MAGIC_PROTECTION.id) to FormattingPredicates.Normal,
        EntityAttributeSupplier(EntityAttributes.GENERIC_ARMOR_TOUGHNESS.id) to FormattingPredicates.Normal,
        EntityAttributeSupplier(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE.id) to FormattingPredicates.PercentageDiv,
        EntityAttributeSupplier(PlayerEXAttributes.EVASION.id) to FormattingPredicates.PercentageDiv,
    )

    override fun build(player: ClientPlayerEntity, adapter: OwoUIAdapter<FlowLayout>, component: IPlayerDataComponent) {
        child(
            Containers.verticalScroll(
                Sizing.fill(100),
                Sizing.fill(100),
                Containers.verticalFlow(Sizing.fill(100), Sizing.content()).apply {
                    child(AttributeListComponent("playerex.ui.main.categories.melee_combat", player, MELEE_COMBAT_STATS))
                    child(AttributeListComponent("playerex.ui.main.categories.ranged_combat", player, RANGED_COMBAT_STATS))
                    child(AttributeListComponent("playerex.ui.main.categories.defense_combat", player, DEFENSE_COMBAT_STATS))
                    gap(10)
                }.id("combat_stats")
            )
        )

        this.onAttributeUpdated.subscribe { _, _ ->
            this.forEachDescendant {
                if (it is AttributeListEntryComponent) it.refresh()
            }
        }

        padding(Insets.both(8, 12))
    }
}