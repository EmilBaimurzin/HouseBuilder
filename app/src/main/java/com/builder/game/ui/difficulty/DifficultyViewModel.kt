package com.builder.game.ui.difficulty

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.builder.game.domain.Difficulty

class DifficultyViewModel: ViewModel() {
    private val _difficulty = MutableLiveData(Difficulty.EASY)
    val difficulty: LiveData<Difficulty> = _difficulty

    fun setDifficulty(difficulty: Difficulty) {
        _difficulty.postValue(difficulty)
    }
}