package com.builder.game.domain

import com.builder.game.data.data_base.Database
import com.builder.game.data.data_base.Game
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class BuilderRepository {
    fun saveGame(floors: List<Floor>, difficulty: Difficulty) {
        CoroutineScope(Dispatchers.Default).launch {
            Database.instance.dao().insertGame(Game(0, difficulty, floors))
        }
    }

    fun removeGame() {
        CoroutineScope(Dispatchers.Default).launch {
            Database.instance.dao().removeGame()
        }
    }

    suspend fun getGame(): Game? {
        return suspendCoroutine { continuation ->
            CoroutineScope(Dispatchers.Default).launch {
                val list = Database.instance.dao().getAllGames()
                if (list.isNotEmpty()) {
                    continuation.resume(list[0])
                } else {
                    continuation.resume(null)
                }
            }
        }
    }
}