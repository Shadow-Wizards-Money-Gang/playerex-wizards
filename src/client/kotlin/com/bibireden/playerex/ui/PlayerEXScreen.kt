package com.bibireden.playerex.ui

import com.bibireden.playerex.PlayerEX
import com.bibireden.playerex.PlayerEXClient
import com.bibireden.playerex.api.attribute.PlayerEXAttributes
import com.bibireden.playerex.components.PlayerEXComponents
import com.bibireden.playerex.ext.level
import com.bibireden.playerex.networking.NetworkingChannels
import com.bibireden.playerex.networking.NetworkingPackets
import com.bibireden.playerex.util.PlayerEXUtil
import io.wispforest.owo.ui.base.BaseUIModelScreen
import io.wispforest.owo.ui.component.ButtonComponent
import io.wispforest.owo.ui.component.Components
import io.wispforest.owo.ui.component.LabelComponent
import io.wispforest.owo.ui.component.TextBoxComponent
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.Component
import io.wispforest.owo.ui.core.ParentComponent
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import kotlin.reflect.KClass

// Transformers
fun <T : Component> ParentComponent.childById(clazz: KClass<T>, id: String) = this.childById(clazz.java, id)

/** Primary screen for the mod that brings everything intended together. */
class PlayerEXScreen : BaseUIModelScreen<FlowLayout>(FlowLayout::class.java, DataSource.asset(PlayerEXClient.MAIN_UI_SCREEN_ID)) {
    private var currentPage = 0

    // TODO: Make registry based?
    private val pages: List<List<Component>> = listOf(testLayout(), testLayout2()) // Temp just to help myself make code - prob will change

    override fun shouldPause(): Boolean = false

    /** Whenever the level attribute gets modified, and on initialization of the screen, this will be called. */
    fun onLevelUpdated() {
        val player = client?.player ?: return
        val data = PlayerEXComponents.PLAYER_DATA.get(player)

        val root = this.uiAdapter.rootComponent

        root.childById(LabelComponent::class, "level:current")?.apply {
            text(Text.translatable("playerex.ui.current_level", player.level.toInt(), PlayerEXUtil.getRequiredXpForNextLevel(player)))
        }
        root.childById(LabelComponent::class, "points_available")?.apply {
            text(Text.literal(data.skillPoints.toString())
                .formatted(when (data.skillPoints) {
                    0 -> Formatting.WHITE
                    else -> Formatting.YELLOW
                }
            ))
        }
        updateLevelUpButton(player, root.childById(TextBoxComponent::class, "level:amount")!!.text, root.childById(ButtonComponent::class, "level:button")!!)
    }

    /** Whenever any attribute is updated, this will be called. */
    // todo: this is subject to change... and needs to be done first
    fun onAttributesUpdated() {

    }

    private fun onPagesUpdated() {
        val root = this.uiAdapter.rootComponent
        val pageCounter = root.childById(LabelComponent::class, "counter")!!
        val content = root.childById(FlowLayout::class, "content")!!

        pageCounter.text(Text.of("${currentPage + 1}/${pages.size}"))
        content.clearChildren()
        content.children(pages[currentPage])
    }

    private fun updateLevelUpButton(player: PlayerEntity, text: String, levelUpButton: ButtonComponent) {
        val amount = text.toIntOrNull() ?: return
        val result = player.level + amount

        if (result > PlayerEXAttributes.LEVEL.maxValue) return

        levelUpButton.tooltip(Text.translatable("playerex.ui.level_button", PlayerEXUtil.getRequiredXpForLevel(player, result), amount, player.experienceLevel))
    }

    override fun build(rootComponent: FlowLayout) {
        val player = client?.player ?: return

        val levelAmount = rootComponent.childById(TextBoxComponent::class, "level:amount")!!
        val levelUpButton = rootComponent.childById(ButtonComponent::class, "level:button")!!

        updateLevelUpButton(player, levelAmount.text, levelUpButton)
        levelAmount.onChanged().subscribe { updateLevelUpButton(player, it, levelUpButton) }

        val previousPage = rootComponent.childById(ButtonComponent::class, "previous")!!
        val pageCounter = rootComponent.childById(LabelComponent::class, "counter")!!
        val nextPage = rootComponent.childById(ButtonComponent::class, "next")!!
        val exit = rootComponent.childById(ButtonComponent::class, "exit")!!
        val content = rootComponent.childById(FlowLayout::class, "content")!!
        val footer = rootComponent.childById(FlowLayout::class, "footer")!!

        this.onLevelUpdated()
        this.onAttributesUpdated()
        this.onPagesUpdated()

        pageCounter.text(Text.of("${currentPage + 1}/${pages.size}"))

        content.clearChildren()
        content.children(pages[currentPage])

        previousPage.onPress {
            if (currentPage > 0) {
                currentPage--
                this.onPagesUpdated()
            }
        }
        nextPage.onPress {
            if (currentPage < pages.lastIndex) {
                currentPage++
                this.onPagesUpdated()
            }
        }

        levelUpButton.onPress {
            levelAmount.text.toIntOrNull()?.let { NetworkingChannels.MODIFY.clientHandle().send(NetworkingPackets.Level(it)) }
        }

        exit.onPress { this.close() }
    }

    // TODO: Remove
    private fun testLayout(): List<Component> {
        return listOf(
            Components.button(
                Text.literal("Test Button"),
            ) {
                PlayerEX.LOGGER.info("Test Button")
            }
        )
    }

    // TODO: Remove
    private fun testLayout2(): List<Component> {
        return listOf(
            Components.button(
                Text.literal("Test Button 2"),
            ) {
                PlayerEX.LOGGER.info("Test Button 2")
            }
        )
    }
}