package com.dog.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2

class TransformComponent(var direction: Vector2 = Vector2(0f, 1f)) : Component