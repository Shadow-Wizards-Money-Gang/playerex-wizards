package com.edelweiss.playerex.skill_attributes.json

import kotlinx.serialization.Serializable

@Serializable
data class Properties(val values: MutableMap<String, MutableMap<String, String>>) : DataMerger<String, String, String>(values)
