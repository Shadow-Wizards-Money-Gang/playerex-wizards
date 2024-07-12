package com.bibireden.playerex.ext

import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier


val EntityAttribute.id: Identifier
    get() = Registries.ATTRIBUTE.getId(this)!!