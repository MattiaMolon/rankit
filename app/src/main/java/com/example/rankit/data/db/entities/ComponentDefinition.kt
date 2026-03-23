package com.example.rankit.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "component_definitions",
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
data class ComponentDefinition(
    @PrimaryKey val id: String,
    val listId: String,
    val name: String,
    val type: ComponentType,
    // JSON string with type-specific config.
    // SLIDER example: {"min": 0, "max": 10, "isScoring": true}
    // TEXT / IMAGE / DESCRIPTION / LOCATION: {}
    val configJson: String,
    val orderIndex: Int
)
