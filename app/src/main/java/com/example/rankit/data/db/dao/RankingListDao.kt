package com.example.rankit.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rankit.data.db.entities.RankingList
import kotlinx.coroutines.flow.Flow

@Dao
interface RankingListDao {
    @Query("SELECT * FROM ranking_lists ORDER BY createdAt DESC")
    fun getAll(): Flow<List<RankingList>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rankingList: RankingList)

    @Delete
    suspend fun delete(rankingList: RankingList)

    @Query("DELETE FROM ranking_lists WHERE id = :id")
    suspend fun deleteById(id: String)
}
