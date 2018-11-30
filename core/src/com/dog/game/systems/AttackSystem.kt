package com.dog.game.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.dog.game.components.AttackComponent

class AttackSystem : IteratingSystem(Family.all(AttackComponent::class.java).get()) {
    val am: ComponentMapper<AttackComponent> = ComponentMapper.getFor(AttackComponent::class.java)
    private val longTime = 1000000f
    override fun processEntity(entity: Entity?, deltaTime: Float) {
        if (entity != null) {
            val attack = am.get(entity)
            attack.sinceLastAttack += deltaTime
            if (attack.sinceLastAttack > longTime) {
                attack.sinceLastAttack = longTime
            }
        }
    }

}