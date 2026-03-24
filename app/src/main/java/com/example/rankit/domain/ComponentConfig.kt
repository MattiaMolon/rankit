package com.example.rankit.domain

import com.example.rankit.data.db.entities.ComponentDefinition
import com.example.rankit.data.db.entities.ComponentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class SliderConfig(
    val min: Float = 0f,
    val max: Float = 5f,
    val step: Float = .5f
)

private val json = Json { ignoreUnknownKeys = true }

fun ComponentDefinition.parseSliderConfig(): SliderConfig {
    require(type == ComponentType.SLIDER) { "parseSliderConfig called on $type component" }
    return json.decodeFromString<SliderConfig>(configJson)
}

fun ComponentDefinition.validateConfig() {
    when (type) {
        ComponentType.SLIDER -> {
            val config = try {
                json.decodeFromString<SliderConfig>(configJson)
            } catch (e: Exception) {
                throw IllegalArgumentException(
                    "Invalid config for SLIDER component '$name': ${e.message}"
                )
            }
            require(config.min < config.max) {
                "SLIDER '$name': min (${config.min}) must be less than max (${config.max})"
            }
            require(config.step > 0f) {
                "SLIDER '$name': step must be positive"
            }
        }
        else -> {
            try {
                json.parseToJsonElement(configJson)
            } catch (e: Exception) {
                throw IllegalArgumentException(
                    "Invalid JSON in config for $type component '$name': ${e.message}"
                )
            }
        }
    }
}
