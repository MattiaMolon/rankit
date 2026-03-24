package com.example.rankit.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class RankingListWithCount(
    @Embedded val list: RankingList,
    @ColumnInfo(name = "itemCount") val itemCount: Int
)
