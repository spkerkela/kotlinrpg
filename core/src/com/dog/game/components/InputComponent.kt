package com.dog.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2

class InputComponent(var movementEnabled: Boolean = true, var lastClick: Vector2 = Vector2.Zero, var attackPressed: Boolean = false) : Component