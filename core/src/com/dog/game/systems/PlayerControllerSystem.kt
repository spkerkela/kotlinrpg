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
    val cm: ComponentMapper<CircleColliderComponent> = ComponentMapper.getFor(CircleColliderComponent::class.java)
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
            val collider = cm.get(player)
            val body = collider.body
            val input = im.get(player)
            val position = body!!.position
            val transform = tm.get(player)
            val playerData = plm.get(player)

            val direction = Vector2(input.lastLeftClick.x - position.x, input.lastLeftClick.y - position.y).nor()
            if (!direction.isZero) {
                // TODO rotation for box2d body
                transform.direction = direction
            }
            if (input.attackPressed) {
                val bullet = Entity()
                bullet.add(PositionComponent(position.x, position.y))
                bullet.add(VelocityComponent(transform.direction.x * 600 + MathUtils.random(0.0f, 50.0f), transform.direction.y * 600 + MathUtils.random(0.0f, 50.0f)))
                bullet.add(LimitedDurationComponent(5.0f))
                bullet.add(CircleColliderComponent(radius = 10.0f, categoryMask = 1, collidesWith = 2))
                engine.addEntity(bullet)
            }
            if (input.movementEnabled) {
                val distance = Vector2(position.x, position.y).dst2(playerData.movementTarget)
                if (distance > 1 * deltaTime * playerData.speed) {
                    body.setLinearVelocity(direction.x * playerData.speed, direction.y * playerData.speed)
                } else {
                    body.setLinearVelocity(0f, 0f)
                }
                playerData.movementTarget.x = input.lastLeftClick.x
                playerData.movementTarget.y = input.lastLeftClick.y
            } else {
                playerData.movementTarget.x = position.x
                playerData.movementTarget.y = position.y
                body.setLinearVelocity(0f, 0f)
            }

        }
    }
}