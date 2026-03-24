package com.example.rankit

import com.example.rankit.data.db.entities.ComponentDefinition
import com.example.rankit.data.db.entities.ComponentType
import com.example.rankit.domain.ScoreCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ScoreCalculatorTest {

    private fun sliderDef(id: String) = ComponentDefinition(
        id = id,
        listId = "list1",
        name = id,
        type = ComponentType.SLIDER,
        configJson = """{"min":0,"max":10}""",
        orderIndex = 0
    )

    private fun textDef(id: String) = ComponentDefinition(
        id = id,
        listId = "list1",
        name = id,
        type = ComponentType.TEXT,
        configJson = "{}",
        orderIndex = 0
    )

    @Test
    fun `mean of three scoring sliders`() {
        val defs = listOf(sliderDef("a"), sliderDef("b"), sliderDef("c"))
        // All values are strings
        val valuesJson = """{"a":"8.0","b":"6.0","c":"7.0"}"""
        assertEquals(7.0f, ScoreCalculator.computeScore(defs, valuesJson))
    }

    @Test
    fun `null slider value is excluded from mean`() {
        val defs = listOf(sliderDef("a"), sliderDef("b"))
        val valuesJson = """{"a":"6.0","b":null}"""
        assertEquals(6.0f, ScoreCalculator.computeScore(defs, valuesJson))
    }

    @Test
    fun `no sliders returns null`() {
        val defs = listOf(textDef("a"))
        val valuesJson = """{"a":"hello"}"""
        assertNull(ScoreCalculator.computeScore(defs, valuesJson))
    }

    @Test
    fun `scoreForCategory returns correct value`() {
        val valuesJson = """{"slider1":"9.5","slider2":"4.0"}"""
        assertEquals(9.5f, ScoreCalculator.scoreForCategory("slider1", valuesJson))
    }

    @Test
    fun `scoreForCategory returns null for missing key`() {
        val valuesJson = """{"slider1":"9.5"}"""
        assertNull(ScoreCalculator.scoreForCategory("slider2", valuesJson))
    }

    @Test
    fun `unparseable string value is excluded`() {
        val defs = listOf(sliderDef("a"), sliderDef("b"))
        val valuesJson = """{"a":"8.0","b":"not a number"}"""
        assertEquals(8.0f, ScoreCalculator.computeScore(defs, valuesJson))
    }
}
