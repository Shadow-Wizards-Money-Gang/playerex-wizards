package com.bibireden.playerex.ui

import com.bibireden.data_attributes.DataAttributes
import com.bibireden.data_attributes.api.DataAttributesAPI
import com.bibireden.data_attributes.api.attribute.EntityAttributeSupplier
import com.bibireden.data_attributes.api.attribute.IEntityAttribute
import com.bibireden.playerex.PlayerEXClient
import com.bibireden.playerex.api.attribute.PlayerEXAttributes
import com.bibireden.playerex.components.PlayerEXComponents
import com.bibireden.playerex.components.player.IPlayerDataComponent
import com.bibireden.playerex.ext.id
import com.bibireden.playerex.ext.level
import com.bibireden.playerex.networking.NetworkingChannels
import com.bibireden.playerex.networking.NetworkingPackets
import com.bibireden.playerex.networking.types.UpdatePacketType
import com.bibireden.playerex.registry.AttributesMenuRegistry
import com.bibireden.playerex.ui.components.MenuComponent
import com.bibireden.playerex.ui.components.labels.AttributeComponent
import com.bibireden.playerex.ui.components.labels.AttributeLabelComponent
import com.bibireden.playerex.util.PlayerEXUtil
import io.wispforest.owo.ui.base.BaseUIModelScreen
import io.wispforest.owo.ui.component.ButtonComponent
import io.wispforest.owo.ui.component.LabelComponent
import io.wispforest.owo.ui.component.TextBoxComponent
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.Component
import io.wispforest.owo.ui.core.ParentComponent
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import kotlin.reflect.KClass

// Transformers
fun <T : Component> ParentComponent.childById(clazz: KClass<T>, id: String) = this.childById(clazz.java, id)

/** Primary screen for the mod that brings everything intended together. */
class PlayerEXScreen : BaseUIModelScreen<FlowLayout>(FlowLayout::class.java, DataSource.asset(PlayerEXClient.MAIN_UI_SCREEN_ID)) {
    private var currentPage = 0

    private val pages: MutableList<MenuComponent> = mutableListOf()

    private val playerComponent by lazy { PlayerEXComponents.PLAYER_DATA.get(this.client?.player!!) }

    override fun shouldPause(): Boolean = false

    /** Whenever the level attribute gets modified, and on initialization of the screen, this will be called. */
    fun onLevelUpdated(level: Int) {
        val player = client?.player ?: return

        val root = this.uiAdapter.rootComponent

        root.childById(LabelComponent::class, "level:current")?.apply {
            text(Text.translatable("playerex.ui.current_level", player.level.toInt(), PlayerEXUtil.getRequiredXpForNextLevel(player)))
        }

        updatePointsAvailable()
        updateLevelUpButton(player, root.childById(TextBoxComponent::class, "level:amount")!!.text, root.childById(ButtonComponent::class, "level:button")!!)

        this.uiAdapter.rootComponent.forEachDescendant { descendant ->
            if (descendant is MenuComponent) descendant.onLevelUpdatedEvents.sink().onLevelUpdated(level)
        }
    }

    /** Whenever any attribute is updated, this will be called. */
    fun onAttributesUpdated(attribute: EntityAttribute, value: Double) {
        this.uiAdapter.rootComponent.forEachDescendant { descendant ->
            if (descendant is MenuComponent) descendant.onAttributeUpdatedEvents.sink().onAttributeUpdated(attribute, value)
        }
        updatePointsAvailable()
    }

    private fun updatePointsAvailable() {
        this.uiAdapter.rootComponent.childById(LabelComponent::class, "points_available")?.apply {
            text(Text.literal(playerComponent.skillPoints.toString())
                .formatted(when (playerComponent.skillPoints) {
                    0 -> Formatting.WHITE
                    else -> Formatting.YELLOW
                }
            ))
        }
    }

    private fun onPagesUpdated() {
        val root = this.uiAdapter.rootComponent
        val pageCounter = root.childById(LabelComponent::class, "counter")!!
        val content = root.childById(FlowLayout::class, "content")!!

        pageCounter.text(Text.of("${currentPage + 1}/${pages.size}"))
        content.clearChildren()
        content.child(pages[currentPage])
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

        AttributesMenuRegistry.get().forEach {
            val instance = it.getDeclaredConstructor().newInstance()
            instance.build(player, this.uiAdapter, playerComponent)
            pages.add(instance)
        }

        this.onLevelUpdated(player.level.toInt())
        this.onPagesUpdated()

        pageCounter.text(Text.of("${currentPage + 1}/${pages.size}"))

        content.clearChildren()
        content.child(pages[currentPage])

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

    enum class AttributeButtonComponentType {
        Add,
        Remove;

        fun getPointsFromComponent(component: IPlayerDataComponent): Int = if (this == Add) component.skillPoints else component.refundablePoints

        val symbol: String
            get() = if (this == Add) "+" else "-"

        val packet: UpdatePacketType
            get() = when (this) {
                Add -> UpdatePacketType.Skill
                Remove -> UpdatePacketType.Refund
            }
    }
}