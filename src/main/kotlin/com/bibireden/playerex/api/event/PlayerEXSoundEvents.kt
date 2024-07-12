package com.bibireden.playerex.api.event

import com.bibireden.playerex.PlayerEX
import net.minecraft.sound.SoundEvent

object PlayerEXSoundEvents {
    val LEVEL_UP_SOUND = SoundEvent.of(PlayerEX.id("level_up"))
    val SPEND_SOUND = SoundEvent.of(PlayerEX.id("spend"))
}