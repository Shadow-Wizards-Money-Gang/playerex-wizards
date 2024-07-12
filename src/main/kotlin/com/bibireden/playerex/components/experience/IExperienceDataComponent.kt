package com.bibireden.playerex.components.experience

import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent

/** Functions related to modifying the negation factor of experience data. */
interface IExperienceDataComponent : ServerTickingComponent {
    fun updateExperienceNegationFactor(amount: Int): Boolean
    fun resetExperienceNegationFactor()
}