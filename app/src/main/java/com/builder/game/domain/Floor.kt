package com.builder.game.domain

import com.builder.game.core.library.XY

data class Floor(
    override var x: Float,
    override var y: Float,
    val floor: Int,
    val img: Int,
    var rotatingLeft: Boolean = false,
    var rotatingRight: Boolean = false,
): XY