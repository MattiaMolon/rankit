package com.example.rankit.data

import com.example.rankit.data.db.dao.ComponentDefinitionDao
import com.example.rankit.data.db.dao.ItemDao
import com.example.rankit.data.db.dao.RankingListDao
import com.example.rankit.data.db.entities.ComponentDefinition
import com.example.rankit.data.db.entities.ComponentType
import com.example.rankit.data.db.entities.Item
import com.example.rankit.data.db.entities.RankingList
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class RankItRepository(
    private val rankingListDao: RankingListDao,
    private val componentDefinitionDao: ComponentDefinitionDao,
    private val itemDao: ItemDao
) {
    private val json = Json { ignoreUnknownKeys = true }

    // ── Queries ──────────────────────────────────────────────────────────────

    fun getLists(): Flow<List<RankingList>> = rankingListDao.getAll()

    fun getComponentDefsForList(listId: String): Flow<List<ComponentDefinition>> =
        componentDefinitionDao.getForList(listId)

    fun getItemsForList(listId: String): Flow<List<Item>> = itemDao.getForList(listId)

    suspend fun getItemById(id: String): Item? = itemDao.getById(id)

    // ── List operations ───────────────────────────────────────────────────────

    suspend fun createList(list: RankingList, componentDefs: List<ComponentDefinition>) {
        validateHasScoringSlider(componentDefs)
        rankingListDao.insert(list)
        componentDefinitionDao.insertAll(componentDefs)
    }

    suspend fun deleteList(listId: String) = rankingListDao.deleteById(listId)

    // ── Item operations ───────────────────────────────────────────────────────

    suspend fun addItem(item: Item) = itemDao.insert(item)

    suspend fun deleteItem(item: Item) = itemDao.delete(item)

    // ── Validation ────────────────────────────────────────────────────────────

    private fun validateHasScoringSlider(defs: List<ComponentDefinition>) {
        val hasScoringSlider = defs.any { def ->
            def.type == ComponentType.SLIDER && isScoringSlider(def.configJson)
        }
        require(hasScoringSlider) { "At least one scoring slider is required" }
    }

    private fun isScoringSlider(configJson: String): Boolean {
        return try {
            json.parseToJsonElement(configJson)
                .jsonObject["isScoring"]
                ?.jsonPrimitive
                ?.boolean == true
        } catch (e: Exception) {
            false
        }
    }
}
