package com.example.rankit.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rankit.data.db.entities.RankingList
import com.example.rankit.data.db.entities.RankingListWithCount
import kotlinx.coroutines.flow.Flow

@Dao
interface RankingListDao {
    @Query("SELECT * FROM ranking_lists ORDER BY createdAt DESC")
    fun getAll(): Flow<List<RankingList>>

    @Query("""
        SELECT ranking_lists.*, COUNT(items.id) as itemCount
        FROM ranking_lists
        LEFT JOIN items ON items.listId = ranking_lists.id
        GROUP BY ranking_lists.id
        ORDER BY ranking_lists.createdAt DESC
    """)
    fun getAllWithCount(): Flow<List<RankingListWithCount>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rankingList: RankingList)

    @Delete
    suspend fun delete(rankingList: RankingList)

    @Query("DELETE FROM ranking_lists WHERE id = :id")
    suspend fun deleteById(id: String)
}
