package com.example.rankit.domain

import com.example.rankit.data.db.entities.ComponentDefinition
import com.example.rankit.data.db.entities.ComponentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object ScoreCalculator {

    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Returns the mean of all scoring slider values for an item.
     * Values are stored as strings in valuesJson and parsed to Float.
     * Returns null if no scoring slider has a valid value (item sorts last).
     */
    fun computeScore(defs: List<ComponentDefinition>, valuesJson: String): Float? {
        val values = parseScoringSliderValues(defs, valuesJson)
        return if (values.isEmpty()) null else values.average().toFloat()
    }

    /**
     * Returns the float value of a single component, or null if missing/unparseable.
     * Used for sorting by a specific category.
     */
    fun scoreForCategory(componentId: String, valuesJson: String): Float? {
        return try {
            val blob = json.parseToJsonElement(valuesJson).jsonObject
            val element = blob[componentId] ?: return null
            if (element == JsonNull) return null
            element.jsonPrimitive.contentOrNull?.toFloatOrNull()
        } catch (e: Exception) {
            null
        }
    }

    private fun parseScoringSliderValues(
        defs: List<ComponentDefinition>,
        valuesJson: String
    ): List<Float> {
        val blob = try {
            json.parseToJsonElement(valuesJson).jsonObject
        } catch (e: Exception) {
            return emptyList()
        }

        return defs
            .filter { it.type == ComponentType.SLIDER }
            .mapNotNull { def ->
                val element = blob[def.id] ?: return@mapNotNull null
                if (element == JsonNull) return@mapNotNull null
                element.jsonPrimitive.contentOrNull?.toFloatOrNull()
            }
    }
}
