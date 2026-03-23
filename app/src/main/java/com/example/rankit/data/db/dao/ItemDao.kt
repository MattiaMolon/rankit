package com.example.rankit.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rankit.data.db.entities.Item
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM items WHERE listId = :listId ORDER BY createdAt DESC")
    fun getForList(listId: String): Flow<List<Item>>

@Query("SELECT * FROM items WHERE id = :id")
    suspend fun getById(id: String): Item?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Item)

    @Delete
    suspend fun delete(item: Item)
}
