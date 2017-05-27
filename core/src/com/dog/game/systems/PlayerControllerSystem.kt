package com.dog.game.systems

import com.badlogic.ashley.core.*
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.dog.game.components.*

class PlayerControllerSystem : EntitySystem(1) {
    var player: Entity? = null
    val im: ComponentMapper<InputComponent> = ComponentMapper.getFor(InputComponent::class.java)
    val pm: ComponentMapper<PositionComponent> = ComponentMapper.getFor(PositionComponent::class.java)
    val vm: ComponentMapper<VelocityComponent> = ComponentMapper.getFor(VelocityComponent::class.java)
    val tm: ComponentMapper<TransformComponent> = ComponentMapper.getFor(TransformComponent::class.java)
    val plm: ComponentMapper<PlayerComponent> = ComponentMapper.getFor(PlayerComponent::class.java)
    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        if (engine != null) {
            player = engine.getEntitiesFor(Family.all(
                    PlayerComponent::class.java,
                    InputComponent::class.java,
                    PositionComponent::class.java,
                    VelocityComponent::class.java,
                    TransformComponent::class.java).get()).first()
        }
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        if (player != null) {
            val input = im.get(player)
            val position = pm.get(player)
            val transform = tm.get(player)
            val velocity = vm.get(player)
            val playerData = plm.get(player)
            val direction = Vector2(input.lastLeftClick.x - position.x, input.lastLeftClick.y - position.y).nor()
            if (!direction.isZero) {
                transform.direction = direction
            }
            if (input.attackPressed) {
                val bullet = Entity()
                bullet.add(PositionComponent(position.x, position.y))
                bullet.add(CircleComponent(10.0f, Color.CHARTREUSE))
                bullet.add(VelocityComponent(transform.direction.x * 600 + MathUtils.random(0.0f, 50.0f), transform.direction.y * 600 + MathUtils.random(0.0f, 50.0f)))
                bullet.add(LimitedDurationComponent(5.0f))
                engine.addEntity(bullet)
            }
            if (input.movementEnabled) {
                val distance = Vector2(position.x, position.y).dst2(playerData.movementTarget)
                if (distance > 1 * deltaTime * playerData.speed) {
                    velocity.x = direction.x * playerData.speed
                    velocity.y = direction.y * playerData.speed
                } else {
                    velocity.x = 0f
                    velocity.y = 0f
                }
                playerData.movementTarget.x = input.lastLeftClick.x
                playerData.movementTarget.y = input.lastLeftClick.y
            } else {
                playerData.movementTarget.x = position.x
                playerData.movementTarget.y = position.y
                velocity.x = 0f
                velocity.y = 0f
            }
        }
    }
}