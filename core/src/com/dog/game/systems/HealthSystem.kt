package com.dog.game.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.dog.game.components.HealthComponent

class HealthSystem : IteratingSystem(Family.all(HealthComponent::class.java).get()) {
    val hm: ComponentMapper<HealthComponent> = ComponentMapper.getFor(HealthComponent::class.java)
    override fun processEntity(entity: Entity?, deltaTime: Float) {
        if (entity is Entity) {
            val health = hm.get(entity)
            if (health.curHealth <= 0) {
                engine.removeEntity(entity)
            }
        }
    }

}