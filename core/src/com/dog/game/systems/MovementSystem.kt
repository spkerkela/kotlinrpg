package com.dog.game.systems

import com.badlogic.ashley.core.*
import com.badlogic.ashley.systems.IteratingSystem
import com.dog.game.components.CircleColliderComponent
import com.dog.game.components.PositionComponent
import com.dog.game.components.VelocityComponent

class MovementSystem : IteratingSystem(Family.all(PositionComponent::class.java, VelocityComponent::class.java).exclude(CircleColliderComponent::class.java).get()) {
    private val pm = ComponentMapper.getFor(PositionComponent::class.java)
    private val vm = ComponentMapper.getFor(VelocityComponent::class.java)
    override fun addedToEngine(engine: Engine?) {
        println("Movement system initiated")
        super.addedToEngine(engine)
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val position = pm.get(entity)
        val velocity = vm.get(entity)
        position.x += velocity.x * deltaTime
        position.y += velocity.y * deltaTime
    }
}