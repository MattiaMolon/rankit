package com.example.rankit.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.rankit.data.db.dao.ComponentDefinitionDao
import com.example.rankit.data.db.dao.ItemDao
import com.example.rankit.data.db.dao.RankingListDao
import com.example.rankit.data.db.entities.ComponentDefinition
import com.example.rankit.data.db.entities.Item
import com.example.rankit.data.db.entities.RankingList
import com.example.rankit.data.db.entities.RankItTypeConverters

@Database(
    entities = [RankingList::class, ComponentDefinition::class, Item::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(RankItTypeConverters::class)
abstract class RankItDatabase : RoomDatabase() {

    abstract fun rankingListDao(): RankingListDao
    abstract fun componentDefinitionDao(): ComponentDefinitionDao
    abstract fun itemDao(): ItemDao

    companion object {
        @Volatile
        private var INSTANCE: RankItDatabase? = null

        fun getInstance(context: Context): RankItDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    RankItDatabase::class.java,
                    "rankit.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
