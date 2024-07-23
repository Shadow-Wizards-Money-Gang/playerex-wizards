package com.bibireden.playerex.ui

import com.bibireden.data_attributes.api.DataAttributesAPI
import com.bibireden.data_attributes.api.attribute.EntityAttributeSupplier
import com.bibireden.data_attributes.api.attribute.IEntityAttribute
import com.bibireden.playerex.PlayerEX
import com.bibireden.playerex.PlayerEXClient
import com.bibireden.playerex.api.attribute.PlayerEXAttributes
import com.bibireden.playerex.components.PlayerEXComponents
import com.bibireden.playerex.ext.id
import com.bibireden.playerex.ext.level
import com.bibireden.playerex.networking.NetworkingChannels
import com.bibireden.playerex.networking.NetworkingPackets
import com.bibireden.playerex.networking.types.UpdatePacketType
import com.bibireden.playerex.util.PlayerEXUtil
import io.wispforest.owo.ui.base.BaseUIModelScreen
import io.wispforest.owo.ui.component.ButtonComponent
import io.wispforest.owo.ui.component.Components
import io.wispforest.owo.ui.component.LabelComponent
import io.wispforest.owo.ui.component.TextBoxComponent
import io.wispforest.owo.ui.container.Containers
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.Component
import io.wispforest.owo.ui.core.ParentComponent
import io.wispforest.owo.ui.core.Positioning
import io.wispforest.owo.ui.core.Sizing
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.registry.Registries
import net.minecraft.text.Text
import net.minecraft.util.Colors
import net.minecraft.util.Formatting
import kotlin.reflect.KClass

// Transformers
fun <T : Component> ParentComponent.childById(clazz: KClass<T>, id: String) = this.childById(clazz.java, id)

/** Primary screen for the mod that brings everything intended together. */
class PlayerEXScreen : BaseUIModelScreen<FlowLayout>(FlowLayout::class.java, DataSource.asset(PlayerEXClient.MAIN_UI_SCREEN_ID)) {
    private var currentPage = 0

    // TODO: Make registry based?
    private val pages: MutableList<List<Component>> = mutableListOf() // Temp just to help myself make code - prob will change

    private val playerComponent by lazy { PlayerEXComponents.PLAYER_DATA.get(this.client?.player!!) }

    override fun shouldPause(): Boolean = false

    /** Whenever the level attribute gets modified, and on initialization of the screen, this will be called. */
    fun onLevelUpdated() {
        val player = client?.player ?: return

        val root = this.uiAdapter.rootComponent

        root.childById(LabelComponent::class, "level:current")?.apply {
            text(Text.translatable("playerex.ui.current_level", player.level.toInt(), PlayerEXUtil.getRequiredXpForNextLevel(player)))
        }
        updatePointsAvailable()
        updateLevelUpButton(player, root.childById(TextBoxComponent::class, "level:amount")!!.text, root.childById(ButtonComponent::class, "level:button")!!)
    }

    /** Whenever any attribute is updated, this will be called. */
    // todo: this is subject to change... and needs to be done first
    fun onAttributesUpdated() {
        PlayerEXAttributes.PRIMARY_ATTRIBUTE_IDS.forEach {
            val component = this.uiAdapter.rootComponent.childById(TextBoxComponent::class, "entry:${it}")
            this.uiAdapter.rootComponent.childById(LabelComponent::class, "${it}:current_level")?.apply {
                text(EntityAttributeSupplier(it).get()?.let { attribute -> attributeLabel(attribute, client?.player!!) })
            }
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
        content.children(pages[currentPage])
    }

    private fun updateLevelUpButton(player: PlayerEntity, text: String, levelUpButton: ButtonComponent) {
        val amount = text.toIntOrNull() ?: return
        val result = player.level + amount

        if (result > PlayerEXAttributes.LEVEL.maxValue) return

        levelUpButton.tooltip(Text.translatable("playerex.ui.level_button", PlayerEXUtil.getRequiredXpForLevel(player, result), amount, player.experienceLevel))
    }

    private fun attributeButtonComponent(attribute: EntityAttribute, type: AttributeButtonComponentType): Component {
        val player = this.client?.player ?: return Components.label(Text.of("ohno"))
        return Components.button(Text.of(type.symbol)) {
            it.parent()?.childById(TextBoxComponent::class, "entry:${attribute.id}")?.let { ta ->
                val amount = ta.text.toDoubleOrNull() ?: return@let
                val points = if (type == AttributeButtonComponentType.Add) playerComponent.skillPoints else playerComponent.refundablePoints

                if (points < amount) return@let // invalid, not enough points.

                DataAttributesAPI.getValue(attribute, player).ifPresent { NetworkingChannels.MODIFY.clientHandle().send(NetworkingPackets.Update(type.packet, attribute.id, amount.toInt())) }
            }
        }
            .renderer(ButtonComponent.Renderer.flat(Colors.BLACK, Colors.BLACK, Colors.BLACK))
            .sizing(Sizing.fixed(12), Sizing.fixed(12))
    }

    // todo: migrate
    private fun createAttributeComponent(attribute: EntityAttribute): Component {
        return Containers.horizontalFlow(Sizing.fill(100), Sizing.fixed(18)).also {
            it.child(Components.label(Text.translatable(attribute.translationKey)).sizing(Sizing.content(), Sizing.fill(100)))
            it.child(Components.label(attributeLabel(attribute, this.client?.player!!)).id("${attribute.id}:current_level"))
            it.child(
                Containers.horizontalFlow(Sizing.fill(50), Sizing.fill(100)).also {
                    it.child(attributeButtonComponent(attribute, AttributeButtonComponentType.Remove))
                    it.child(attributeButtonComponent(attribute, AttributeButtonComponentType.Add))
                    it.child(
                        Components.textBox(Sizing.fixed(27))
                            .text("1")
                            .verticalSizing(Sizing.fixed(12))
                            .id("entry:${attribute.id}")
                    )
                    it.gap(4)
                }.positioning(Positioning.relative(100, 0))
            )
            it.gap(3)
        }
    }

    private fun attributeLabel(attribute: EntityAttribute, player: ClientPlayerEntity): Text? {
        return Text.literal("(").append(Text.literal("${DataAttributesAPI.getValue(attribute, player).map(Double::toInt).orElse(0)}").formatted(Formatting.GOLD)).append("/${(attribute as IEntityAttribute).`data_attributes$max`().toInt()})")
    }

    // todo: migrate to Registry once completed
    private fun temporarySupplyAttributePage(): List<Component> = listOf(
        Containers.verticalFlow(Sizing.fill(75), Sizing.content()).also {
            it.child(Components.label(Text.translatable("playerex.ui.category.primary_attributes")))
            it.child(Components.box(Sizing.fill(60), Sizing.fixed(2)))
            it.children(PlayerEXAttributes.PRIMARY_ATTRIBUTE_IDS.mapNotNull(Registries.ATTRIBUTE::get).map(::createAttributeComponent))
            it.gap(5)
        }.positioning(Positioning.relative(10, 25))
    )

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

        pages.addAll(listOf(temporarySupplyAttributePage(), testLayout2()))

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

    enum class AttributeButtonComponentType {
        Add,
        Remove;

        val symbol: String
            get() = if (this == Add) "+" else "-"

        val packet: UpdatePacketType
            get() = when (this) {
                Add -> UpdatePacketType.Skill
                Remove -> UpdatePacketType.Refund
            }
    }
}