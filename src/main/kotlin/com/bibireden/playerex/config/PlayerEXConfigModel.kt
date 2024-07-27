package com.bibireden.playerex.config

import com.bibireden.playerex.PlayerEX
import io.wispforest.owo.config.Option

import io.wispforest.owo.config.annotation.*

@Suppress("UNUSED")
@Modmenu(modId = PlayerEX.MOD_ID)
@Config(name = "playerex-config", wrapperName = "PlayerEXConfig")
class PlayerEXConfigModel {
    @SectionHeader("client_options")

    @Sync(Option.SyncMode.NONE)
    @JvmField
    var tooltip: Tooltip = Tooltip.PlayerEX

    @Sync(Option.SyncMode.NONE)
    @JvmField
    var showLevelOnNameplates: Boolean = true

    data class SoundSettings(
        @Sync(Option.SyncMode.NONE)
        @JvmField
        @RangeConstraint(min = 0.0, max = 150.0)
        var levelUpVolume: Int = 100,

        @Sync(Option.SyncMode.NONE)
        @JvmField
        @RangeConstraint(min = 0.0, max = 150.0)
        var skillUpVolume: Int = 100,

        @Sync(Option.SyncMode.NONE)
        @JvmField
        @RangeConstraint(min = 0.0, max = 150.0)
        var refundVolume: Int = 100
    )

    @JvmField @Nest var soundSettings = SoundSettings()

    @SectionHeader("server_options")
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @JvmField
    var resetOnDeath: Boolean = false

    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @JvmField
    var disableUI: Boolean = false

    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @JvmField
    var skillPointsPerLevelUp: Int = 1

    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @JvmField
    @Hook
    var levelFormula: String = "stairs(x,0.2,2.4,17,10,25)"

//    @JvmField
//    var expression: Expression

    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @JvmField
    var restorativeForceTicks: Int = 600

    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @JvmField
    var restorativeForceMultiplier: Int = 110

    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @JvmField
    var expNegationFactor: Int = 95

    enum class Tooltip { Default, Vanilla, PlayerEX }
}