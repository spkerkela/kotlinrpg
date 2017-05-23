package com.dog.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2

class PlayerComponent(var movementTarget: Vector2 = Vector2.Zero, var speed: Float = 100f) : Component