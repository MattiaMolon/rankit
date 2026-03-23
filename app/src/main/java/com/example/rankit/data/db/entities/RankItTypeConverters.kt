package com.example.rankit.data.db.entities

import androidx.room.TypeConverter

class RankItTypeConverters {
    @TypeConverter
    fun fromComponentType(type: ComponentType): String = type.name

    @TypeConverter
    fun toComponentType(value: String): ComponentType = ComponentType.valueOf(value)
}
