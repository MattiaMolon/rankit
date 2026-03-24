package com.example.rankit

import android.app.Application
import com.example.rankit.data.RankItRepository
import com.example.rankit.data.db.RankItDatabase

// Application is created once when the app process starts.
// Holding the database and repository here gives every screen
// a single shared instance without needing a DI framework.
class RankItApplication : Application() {

    val database by lazy { RankItDatabase.getInstance(this) }

    val repository by lazy {
        RankItRepository(
            rankingListDao = database.rankingListDao(),
            componentDefinitionDao = database.componentDefinitionDao(),
            itemDao = database.itemDao()
        )
    }
}
