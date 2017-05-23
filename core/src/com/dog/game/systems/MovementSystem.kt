package com.dog.game.systems

import com.badlogic.ashley.core.*
import com.badlogic.ashley.systems.IteratingSystem
import com.dog.game.components.PositionComponent
import com.dog.game.components.VelocityComponent

class MovementSystem : IteratingSystem(Family.all(PositionComponent::class.java, VelocityComponent::class.java).get()) {
    internal val pm = ComponentMapper.getFor(PositionComponent::class.java)
    internal val vm = ComponentMapper.getFor(VelocityComponent::class.java)
    override fun addedToEngine(engine: Engine?) {
        println("Movement system initiated")
        super.addedToEngine(engine)
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val position = pm.get(entity)
        val velocity = vm.get(entity)
        position.x += velocity.x * deltaTime
        position.y += velocity.y * deltaTime
        if (position.x != 0f && position.y != 0f) {
            println(String.format("%s %s", position.x, position.y))
        }
    }
}