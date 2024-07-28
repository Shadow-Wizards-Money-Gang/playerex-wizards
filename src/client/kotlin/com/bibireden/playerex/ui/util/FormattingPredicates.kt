package com.bibireden.playerex.ui.util

import com.bibireden.playerex.ui.components.FormattingPredicate

enum class FormattingPredicates(val predicate: FormattingPredicate) {
    Normal({ "%.2f".format(it) }),
    Percent({ "${it.toInt()}%" }),
    PercentageMul({ "${(it * 100.0).toInt()}%" }),
    PercentageDiv({ "${(it / 100.0).toInt()}%" }),
}