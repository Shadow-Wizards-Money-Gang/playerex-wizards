package com.bibireden.playerex.config

import com.bibireden.data_attributes.api.DataAttributesAPI
import com.bibireden.data_attributes.api.util.Maths
import com.bibireden.playerex.PlayerEX
import com.bibireden.playerex.api.attribute.PlayerEXAttributes
import io.wispforest.owo.config.Option
import io.wispforest.owo.config.annotation.*
import net.minecraft.entity.player.PlayerEntity
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder
import net.objecthunter.exp4j.function.Function
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.round

@Modmenu(modId = PlayerEX.MOD_ID)
@Config(
    name = "playerex-config",
    wrapperName = "PlayerEXConfig"
)
class PlayerEXConfigModel {
    @SectionHeader("Server Options")
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @JvmField
    var resetOnDeath: Boolean = false

    @JvmField
    var disableAttributesGui: Boolean = false

    @JvmField
    var showLevelNameplates: Boolean = true

    @JvmField
    var skillPointsPerLevelUp: Int = 1

    @JvmField
    @Hook
    var levelFormula: String = "stairs(x,0.2,2.4,17,10,25)"

//    @JvmField
//    var expression: Expression

    @SectionHeader("Client Options")
    @Sync(Option.SyncMode.NONE)
    @JvmField
    @RangeConstraint(min = 0.0, max = 150.0)
    var levelUpVolume: Int = 100

    @JvmField
    @RangeConstraint(min = 0.0, max = 150.0)
    var skillUpVolume: Int = 100

    @JvmField
    @RangeConstraint(min = 0.0, max = 50.0)
    var textScaleX: Int = 50;

    @JvmField
    @RangeConstraint(min = 0.0, max = 50.0)
    var textScaleY: Int = 50;
}