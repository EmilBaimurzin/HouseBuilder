package com.builder.game.data.data_base

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.builder.game.domain.Difficulty
import com.builder.game.domain.Floor

@Entity
data class Game(
    @PrimaryKey val id: Long = 0,
    @ColumnInfo(name = "game_type") val gameType: Difficulty,
    val floors: List<Floor>
)