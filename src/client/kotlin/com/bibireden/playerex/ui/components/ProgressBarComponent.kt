package com.bibireden.playerex.ui.components

import io.wispforest.owo.ui.component.BoxComponent
import io.wispforest.owo.ui.component.Components
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.Color
import io.wispforest.owo.ui.core.Easing
import io.wispforest.owo.ui.core.Sizing
import org.jetbrains.annotations.ApiStatus
import kotlin.jvm.Throws

@ApiStatus.Internal
class ProgressBarComponent(horizontalSizing: Sizing, verticalSizing: Sizing, private val easing: Easing = Easing.SINE, private val fill: Boolean = true) : FlowLayout(horizontalSizing, verticalSizing, Algorithm.HORIZONTAL) {
    /** The current percentage of the progress bar. */
    var percentage: Double = 0.0
        set(amount) {
            progress((amount * 100).toInt())
            field = amount
        }

    private val progressBar = Components.box(Sizing.fill(0), verticalSizing)
        .fill(fill)
        .direction(BoxComponent.GradientDirection.LEFT_TO_RIGHT)

    private val progressBarBackground = Components.box(Sizing.fill(100), verticalSizing)
        .apply {
            color(Color.WHITE)
            fill(fill)
            zIndex(-1)
        }

    init {
        child(progressBar)
        child(progressBarBackground)
    }

    fun color(start: Color, end: Color? = null): ProgressBarComponent {
        if (end == null) {
            progressBar.color(start)
        }
        else {
            progressBar.startColor(start)
            progressBar.endColor(end)
        }
        return this
    }

    fun backgroundColor(color: Color): ProgressBarComponent {
        progressBarBackground.color(color)
        return this
    }

    @Throws(IllegalArgumentException::class)
    private fun progress(percentage: Int) {
        if (percentage < 0 || percentage > 100) throw IllegalArgumentException("Percentage must be between 0 and 100")
        progressBar.horizontalSizing(Sizing.fill(percentage))
    }
}