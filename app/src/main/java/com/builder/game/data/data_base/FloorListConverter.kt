package com.builder.game.data.data_base

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.builder.game.domain.Floor
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FloorListConverter {
    @TypeConverter
    fun fromList(value: List<Floor>): String {
        val listType = object : TypeToken<List<Floor>>() {}.type
        return Gson().toJson(value, listType)
    }

    @TypeConverter
    fun toList(value: String): List<Floor> {
        val listType = object : TypeToken<List<Floor>>() {}.type
        return Gson().fromJson(value, listType)
    }
}