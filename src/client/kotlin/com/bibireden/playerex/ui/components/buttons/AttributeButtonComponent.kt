package com.bibireden.playerex.ui.components.buttons

import com.bibireden.data_attributes.api.DataAttributesAPI
import com.bibireden.data_attributes.api.attribute.IEntityAttribute
import com.bibireden.playerex.components.player.IPlayerDataComponent
import com.bibireden.playerex.ext.id
import com.bibireden.playerex.networking.NetworkingChannels
import com.bibireden.playerex.networking.NetworkingPackets
import com.bibireden.playerex.ui.PlayerEXScreen
import com.bibireden.playerex.ui.childById
import io.wispforest.owo.ui.component.ButtonComponent
import io.wispforest.owo.ui.component.TextBoxComponent
import io.wispforest.owo.ui.core.Sizing
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text

class AttributeButtonComponent(private val attribute: EntityAttribute, private val player: PlayerEntity, private val component: IPlayerDataComponent, private val type: PlayerEXScreen.AttributeButtonComponentType) : ButtonComponent(
    Text.literal(type.symbol),
    {
        // reference text-box to get needed value to send to server
        it.parent()?.parent()?.childById(TextBoxComponent::class, "input")?.let { box ->
            val amount = box.text.toDoubleOrNull() ?: return@let
            val points = type.getPointsFromComponent(component)

            if (points < amount) return@let // invalid, not enough points.

            DataAttributesAPI.getValue(attribute, player).ifPresent {
                NetworkingChannels.MODIFY.clientHandle().send(NetworkingPackets.Update(type.packet, attribute.id, amount.toInt()))
            }
        }
    }
) {
    init {
        sizing(Sizing.fixed(12), Sizing.fixed(12))
        refresh()
    }

    fun refresh() {
       DataAttributesAPI.getValue(attribute, player).ifPresent { value ->
           var active = true
           val max = (attribute as IEntityAttribute).`data_attributes$max`();
           when (type) {
               PlayerEXScreen.AttributeButtonComponentType.Add -> {
                    active = component.skillPoints > 0 && max > value
               }
               PlayerEXScreen.AttributeButtonComponentType.Remove -> {
                   active = component.refundablePoints > 0 && value > 0
               }
           }
           this.active(active)
       }
    }
}