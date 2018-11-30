package com.dog.game

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World

object PhysicsEngine {
    val world = World(Vector2(0f, 0f), true)
    private var accumulator = 0f
    private const val timeStep = 1.0f / 300f
    fun doPhysicsStep(deltaTime: Float) {
        val frameTime = Math.min(deltaTime, 0.25f)
        accumulator += frameTime
        while (accumulator >= timeStep) {
            PhysicsEngine.world.step(timeStep, 6, 2)
            accumulator -= timeStep
        }
    }
}

