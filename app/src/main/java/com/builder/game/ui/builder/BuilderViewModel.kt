package com.builder.game.ui.builder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.builder.game.core.library.XYImpl
import com.builder.game.core.library.random
import com.builder.game.domain.BuilderRepository
import com.builder.game.domain.Difficulty
import com.builder.game.domain.Floor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BuilderViewModel : ViewModel() {
    private val repository = BuilderRepository()

    private val _currentFloors = MutableLiveData<List<Floor>>(emptyList())
    val currentFloors: LiveData<List<Floor>> = _currentFloors

    private val _flyingFloor = MutableLiveData(Floor(0f, 0f, 1, 1))
    val flyingFloor: LiveData<Floor> = _flyingFloor

    private val _craneXY = MutableLiveData(XYImpl(0f, 0f))
    val craneXY: LiveData<XYImpl> = _craneXY

    private val _piggies = MutableLiveData(XYImpl(0f, 0f))
    val piggies: LiveData<XYImpl> = _piggies

    private val _timer = MutableLiveData(143)
    val timer: LiveData<Int> = _timer

    private val _isWolf = MutableLiveData(false)
    val isWolf: LiveData<Boolean> = _isWolf

    var isInit = false

    private var craneScopeRight = CoroutineScope(Dispatchers.Default)
    private var craneScopeLeft = CoroutineScope(Dispatchers.Default)
    private var isCraneMovingRight = true

    private var gameScope = CoroutineScope(Dispatchers.Default)

    var gameState = true
    var isFallingDown = false
    var isTimeUp = false
    var isProtected = false
    var craneSpeed = 10

    var endCallback: (() -> Unit)? = null

    fun initFloor(x: Float) {
        val list = listOf(
            Floor(x, 0f, 1, 1),
        )
        _currentFloors.postValue(list)
    }

    fun save() {
        repository.saveGame(_currentFloors.value!!, when (craneSpeed) {
            10 -> Difficulty.EASY
            20 -> Difficulty.INTERMEDIATE
            else -> Difficulty.HARD
        })
    }

    fun deleteGame() {
        repository.removeGame()
    }

    fun initFromDB() {
        viewModelScope.launch {
            val game = repository.getGame()
            craneSpeed = when (game!!.gameType) {
                Difficulty.EASY -> 10
                Difficulty.INTERMEDIATE -> 20
                Difficulty.HARD -> 30
            }
            _currentFloors.postValue(game.floors)
        }
    }

    fun start(
        containerWidth: Int,
        maxX: Float,
        minX: Float,
        maxY: Float,
        floorWidth: Int,
        floorHeight: Int
    ) {
        gameScope = CoroutineScope(Dispatchers.IO)
        craneScopeLeft = CoroutineScope(Dispatchers.IO)
        craneScopeRight = CoroutineScope(Dispatchers.IO)
        if (isCraneMovingRight) {
            moveCraneRight(containerWidth, maxX, minX)
        } else {
            moveCraneLeft(containerWidth, maxX, minX)
        }

        letFloorFall(maxY, floorWidth, floorHeight)
        startTimer(floorHeight, maxY.toInt())
    }

    fun stop() {
        gameScope.cancel()
        craneScopeLeft.cancel()
        craneScopeRight.cancel()
    }

    private fun startTimer(height: Int, maxY: Int) {
        gameScope.launch {
            while (true) {
                delay(150)
                if (_timer.value == 1) {
                    spawnWolf(height, maxY)
                }
                if (_timer.value!! - 1 >= 0) {
                    _timer.postValue(_timer.value!! - 1)
                }
            }
        }
    }

    private fun spawnWolf(floorHeight: Int, maxY: Int) {
        viewModelScope.launch {
            _isWolf.postValue(true)
            isTimeUp = true
            val x = _currentFloors.value!!.last().x
            val y = when (_currentFloors.value!!.size) {
                1 -> maxY - floorHeight.toFloat()
                2 -> maxY - floorHeight.toFloat() * 2
                3 -> maxY - floorHeight.toFloat() * 3
                else -> maxY - floorHeight.toFloat() * 4
            }
            _piggies.postValue(XYImpl(x, y - floorHeight))
            delay(1000)
            if (!isProtected) {
                val currentList = _currentFloors.value!!.toMutableList()
                val newList = mutableListOf<Floor>()
                if (currentList.size > 4) {
                    repeat(currentList.size) {
                        if (it + 1 <= currentList.size - 4) {
                            newList.add(currentList[it])
                        }
                    }
                }
                _currentFloors.postValue(newList)
            }
            _isWolf.postValue(false)
            isTimeUp = false
            _timer.postValue(143)
            isProtected = false
        }
    }

    private fun letFloorFall(maxY: Float, floorWidth: Int, floorHeight: Int) {
        gameScope.launch {
            while (true) {
                delay(16)
                if (isFallingDown && !isTimeUp) {
                    val floor = _flyingFloor.value!!
                    floor.y = floor.y + 15
                    if (floor.y >= maxY) {
                        repeat(
                            Floor(
                                floor.x,
                                0f,
                                floor.floor + 1,
                                1 random 3
                            )
                        )
                        endCallback?.invoke()
                    } else {
                        val topFloor = _currentFloors.value!!.last()
                        topFloor.y = when (_currentFloors.value!!.size) {
                            1 -> maxY - floorHeight.toFloat()
                            2 -> maxY - floorHeight.toFloat() * 2
                            3 -> maxY - floorHeight.toFloat() * 3
                            else -> maxY - floorHeight.toFloat() * 4
                        }
                        val floorX = floor.x.toInt()..(floor.x.toInt() + floorWidth)
                        val floorY = floor.y.toInt()..(floor.y.toInt() + floorHeight)

                        val topFloorX = topFloor.x.toInt()..(topFloor.x.toInt() + floorWidth)
                        val topFloorY = topFloor.y.toInt()..(topFloor.y.toInt() + floorHeight)
                        if (floorX.any { it in topFloorX } && floorY.any { it in topFloorY }) {
                            if (floor.x > (topFloor.x + floorWidth / 2)) {
                                floor.rotatingRight = true
                                _flyingFloor.postValue(floor)
                            } else if (floor.x < (topFloor.x - floorWidth / 2)) {
                                floor.rotatingLeft = true
                                _flyingFloor.postValue(floor)
                            } else {
                                val currentList = _currentFloors.value!!.toMutableList()
                                currentList.add(floor)
                                _currentFloors.postValue(currentList)
                                repeat(
                                    Floor(
                                        floor.x,
                                        0f,
                                        floor.floor + 1,
                                        1 random 3
                                    )
                                )
                            }
                        } else {
                            _flyingFloor.postValue(floor)
                        }
                    }
                }
            }
        }
    }

    private fun repeat(floor: Floor) {
        isFallingDown = false
        _flyingFloor.postValue(floor)
    }

    private fun moveCraneRight(containerWidth: Int, maxX: Float, minX: Float) {
        craneScopeRight = CoroutineScope(Dispatchers.Default)
        craneScopeRight.launch {
            while (true) {
                delay(16)
                if (!isFallingDown && !isTimeUp) {
                    if (_craneXY.value!!.x + craneSpeed + containerWidth > maxX) {
                        moveCraneLeft(containerWidth, maxX, minX)
                        isCraneMovingRight = false
                        craneScopeRight.cancel()
                    } else {
                        _craneXY.postValue(
                            XYImpl(
                                _craneXY.value!!.x + craneSpeed,
                                _craneXY.value!!.y
                            )
                        )
                    }
                }
            }
        }
    }

    private fun moveCraneLeft(containerWidth: Int, maxX: Float, minX: Float) {
        craneScopeLeft = CoroutineScope(Dispatchers.Default)
        craneScopeLeft.launch {
            while (true) {
                delay(16)
                if (!isFallingDown && !isTimeUp) {
                    if (_craneXY.value!!.x - craneSpeed < minX) {
                        moveCraneRight(containerWidth, maxX, minX)
                        isCraneMovingRight = true
                        craneScopeLeft.cancel()
                    } else {
                        _craneXY.postValue(
                            XYImpl(
                                _craneXY.value!!.x - craneSpeed,
                                _craneXY.value!!.y
                            )
                        )
                    }
                }
            }
        }
    }

    fun launchFloor(x: Float, y: Float) {
        isFallingDown = true
        _flyingFloor.postValue(
            Floor(
                x,
                y,
                1,
                _flyingFloor.value!!.img
            )
        )
    }

    override fun onCleared() {
        super.onCleared()
        stop()
    }
}