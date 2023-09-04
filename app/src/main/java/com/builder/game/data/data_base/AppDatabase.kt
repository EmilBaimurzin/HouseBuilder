package com.builder.game.data.data_base

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Game::class], version = 1)
@TypeConverters(FloorListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): GameDao

    companion object {
        const val DATABASE_NAME = "app_database"
    }
}