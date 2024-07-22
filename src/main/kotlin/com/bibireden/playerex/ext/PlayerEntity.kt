package com.bibireden.playerex.ext

import com.bibireden.data_attributes.api.DataAttributesAPI
import com.bibireden.playerex.api.attribute.PlayerEXAttributes
import net.minecraft.entity.player.PlayerEntity

val PlayerEntity.level: Double
    get() = DataAttributesAPI.getValue(PlayerEXAttributes.LEVEL, this).orElse(1.0)