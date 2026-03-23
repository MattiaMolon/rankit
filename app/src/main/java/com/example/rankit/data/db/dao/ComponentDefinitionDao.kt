package com.example.rankit.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rankit.data.db.entities.ComponentDefinition
import kotlinx.coroutines.flow.Flow

@Dao
interface ComponentDefinitionDao {
    @Query("SELECT * FROM component_definitions WHERE listId = :listId ORDER BY orderIndex ASC")
    fun getForList(listId: String): Flow<List<ComponentDefinition>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(componentDefinition: ComponentDefinition)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(componentDefinitions: List<ComponentDefinition>)

    @Query("DELETE FROM component_definitions WHERE listId = :listId")
    suspend fun deleteForList(listId: String)
}
