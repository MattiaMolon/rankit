package com.example.rankit.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "items",
    foreignKeys = [
        ForeignKey(
            entity = RankingList::class,
            parentColumns = ["id"],
            childColumns = ["listId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("listId")]
)
data class Item(
    @PrimaryKey val id: String,
    val listId: String,
    // JSON blob: {"componentDefId1": "value", "componentDefId2": 8.5, ...}
    val valuesJson: String,
    // False when any component value is null (schema evolved after item creation).
    val isComplete: Boolean,
    val createdAt: Long
)
