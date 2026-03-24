package com.example.rankit.data

import com.example.rankit.data.db.dao.ComponentDefinitionDao
import com.example.rankit.data.db.dao.ItemDao
import com.example.rankit.data.db.dao.RankingListDao
import com.example.rankit.data.db.entities.ComponentDefinition
import com.example.rankit.data.db.entities.ComponentType
import com.example.rankit.data.db.entities.Item
import com.example.rankit.data.db.entities.RankingList
import com.example.rankit.domain.validateConfig
import kotlinx.coroutines.flow.Flow

class RankItRepository(
    private val rankingListDao: RankingListDao,
    private val componentDefinitionDao: ComponentDefinitionDao,
    private val itemDao: ItemDao
) {
    // ── Queries ──────────────────────────────────────────────────────────────

    fun getLists(): Flow<List<RankingList>> = rankingListDao.getAll()

    fun getComponentDefsForList(listId: String): Flow<List<ComponentDefinition>> =
        componentDefinitionDao.getForList(listId)

    fun getItemsForList(listId: String): Flow<List<Item>> = itemDao.getForList(listId)

    suspend fun getItemById(id: String): Item? = itemDao.getById(id)

    // ── List operations ───────────────────────────────────────────────────────

    suspend fun createList(list: RankingList, componentDefs: List<ComponentDefinition>) {
        componentDefs.forEach { it.validateConfig() }
        require(componentDefs.any { it.type == ComponentType.SLIDER }) {
            "At least one slider is required"
        }

        rankingListDao.insert(list)
        componentDefinitionDao.insertAll(componentDefs)
    }

    suspend fun deleteList(listId: String) = rankingListDao.deleteById(listId)

    // ── Item operations ───────────────────────────────────────────────────────

    suspend fun addItem(item: Item) = itemDao.insert(item)

    suspend fun deleteItem(item: Item) = itemDao.delete(item)
}
