package com.dog.game

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World

object PhysicsEngine {
    var world = World(Vector2(0f, 0f), true)
}

