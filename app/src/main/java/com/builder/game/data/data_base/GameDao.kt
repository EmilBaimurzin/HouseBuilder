package com.builder.game.data.data_base

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GameDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGame(game: Game)

    @Query("SELECT * FROM Game")
    fun getAllGames(): List<Game>

    @Query("DELETE FROM Game")
    fun removeGame()
}