package com.dog.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2

class PositionComponent(var x: Float = 0f, var y: Float = 0f) : Component {
    constructor(vec: Vector2) : this() {
        this.x = vec.x
        this.y = vec.y
    }
}