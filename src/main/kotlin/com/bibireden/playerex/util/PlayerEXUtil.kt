package com.bibireden.playerex.util

import com.bibireden.data_attributes.api.DataAttributesAPI
import com.bibireden.data_attributes.api.util.Maths
import com.bibireden.playerex.PlayerEX
import com.bibireden.playerex.api.attribute.PlayerEXAttributes
import net.minecraft.entity.player.PlayerEntity
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder
import net.objecthunter.exp4j.function.Function
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.round

object PlayerEXUtil {
    const val VARIABLE = "x"
    /**
     * The function is as follows (according to previous implementation)
     * `stairs(x, stretch, steepness, x-offset, y-offset, y-limit)`
     */
    val STAIRCASE_FUNCTION = object : Function("stairs", 6) {
        override fun apply(vararg args: Double): Double {
            return min(Maths.stairs(args[0], args[1], args[2], args[3], args[4]), args[5])
        }
    }

    val expression: Expression
        get() = createExpression()

    private fun createExpression(): Expression {
        return ExpressionBuilder(PlayerEX.CONFIG.levelFormula).variable(VARIABLE).function(STAIRCASE_FUNCTION).build()
    }

    /** todo: document, none evident on former */
    fun level(value: Double): Int {
        val exp = expression.setVariable(VARIABLE, round(value))
        return abs(round(exp.evaluate())).toInt()
    }


    /** todo: document, none evident on former, resolve if orElse is needed here, and if we can do nullable or not without drastically changing things */
    @JvmStatic
    fun getRequiredXp(player: PlayerEntity): Int {
        return DataAttributesAPI.getValue(PlayerEXAttributes.LEVEL, player).map(::level).orElse(1)
    }
}