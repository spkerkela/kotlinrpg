package com.dog.game.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.dog.game.components.LimitedDurationComponent

/**
 * Created by spkerkela on 26/05/2017.
 */
class LimitedDurationSystem : IteratingSystem(Family.all(LimitedDurationComponent::class.java).get()) {
    val ldc: ComponentMapper<LimitedDurationComponent> = ComponentMapper.getFor(LimitedDurationComponent::class.java)
    override fun processEntity(entity: Entity?, deltaTime: Float) {
        if (entity != null) {
            val limitedDuration = ldc.get(entity)
            limitedDuration.lifeTimeDuration -= deltaTime
            if (limitedDuration.lifeTimeDuration < 0) {
                engine.removeEntity(entity)
            }
        }
    }
}