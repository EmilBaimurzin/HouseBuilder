package com.builder.game.core.library

interface XY {
    var x: Float
    var y: Float
}

data class XYImpl(
    override var x: Float,
    override var y: Float
) : XY