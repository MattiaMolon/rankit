package com.example.rankit.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ranking_lists")
data class RankingList(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val createdAt: Long
)
