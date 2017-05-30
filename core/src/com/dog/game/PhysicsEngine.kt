package com.dog.game

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World

object PhysicsEngine {
    val world = World(Vector2(0f, 0f), true)
    internal var accum = 0f
    val timeStep = 1.0f / 300f
    fun doPhysicsStep(deltaTime: Float) {
        val frameTime = Math.min(deltaTime, 0.25f)
        accum += frameTime
        while (accum >= timeStep) {
            PhysicsEngine.world.step(timeStep, 6, 2)
            accum -= timeStep
        }
    }
}

