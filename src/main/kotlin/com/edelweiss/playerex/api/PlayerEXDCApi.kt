package com.edelweiss.playerex.api

import com.edelweiss.playerex.PlayerEXDirectorsCut
import net.minecraft.util.Identifier

object PlayerEXDCApi {
    fun createID(str: String) = Identifier(PlayerEXDirectorsCut.MODID, str)
}
