package com.bibireden.playerex.ui

import com.bibireden.data_attributes.api.DataAttributesAPI
import com.bibireden.playerex.PlayerEXClient
import com.bibireden.playerex.api.attribute.PlayerEXAttributes
import com.bibireden.playerex.components.PlayerEXComponents
import com.bibireden.playerex.util.PlayerEXUtil
import io.wispforest.owo.ui.base.BaseUIModelScreen
import io.wispforest.owo.ui.component.ButtonComponent
import io.wispforest.owo.ui.component.LabelComponent
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.Component
import io.wispforest.owo.ui.core.ParentComponent
import net.minecraft.text.Text
import kotlin.reflect.KClass

// transformers
fun <T : Component> ParentComponent.childById(clazz: KClass<T>, id: String) = this.childById(clazz.java, id)

/** Primary screen for the mod that brings everything intended together. */
class PlayerEXScreen : BaseUIModelScreen<FlowLayout>(FlowLayout::class.java, DataSource.asset(PlayerEXClient.MAIN_UI_SCREEN_ID)) {
    override fun build(rootComponent: FlowLayout) {
        val player = client?.player ?: return

        val level = DataAttributesAPI.getValue(PlayerEXAttributes.LEVEL, player).map(Double::toInt).orElse(0)

        val component = PlayerEXComponents.PLAYER_DATA.get(player)

        rootComponent.childById(LabelComponent::class, "current_level")?.text(Text.translatable("playerex.ui.current_level", level, PlayerEXUtil.getRequiredXp(player)))

        val pointsLabel = rootComponent.childById(LabelComponent::class, "points_available")!!

        pointsLabel.text(Text.of(component.skillPoints.toString()))

        rootComponent.childById(ButtonComponent::class, "exit")?.onPress { this.close() }
    }
}