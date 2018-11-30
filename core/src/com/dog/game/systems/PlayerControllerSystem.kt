package com.dog.game.systems

import com.badlogic.ashley.core.*
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.dog.game.components.*

class PlayerControllerSystem : EntitySystem(1) {
    var player: Entity? = null
    private val im: ComponentMapper<InputComponent> = ComponentMapper.getFor(InputComponent::class.java)
    private val pm: ComponentMapper<PositionComponent> = ComponentMapper.getFor(PositionComponent::class.java)
    private val vm: ComponentMapper<VelocityComponent> = ComponentMapper.getFor(VelocityComponent::class.java)
    private val am: ComponentMapper<AttackComponent> = ComponentMapper.getFor(AttackComponent::class.java)
    private val tm: ComponentMapper<TransformComponent> = ComponentMapper.getFor(TransformComponent::class.java)
    private val plm: ComponentMapper<PlayerComponent> = ComponentMapper.getFor(PlayerComponent::class.java)
    private val cm: ComponentMapper<CircleColliderComponent> = ComponentMapper.getFor(CircleColliderComponent::class.java)
    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        if (engine != null) {
            player = engine.getEntitiesFor(Family.all(
                    PlayerComponent::class.java,
                    InputComponent::class.java,
                    PositionComponent::class.java,
                    VelocityComponent::class.java,
                    TransformComponent::class.java,
                    AttackComponent::class.java).get()).first()
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
            val attack = am.get(player)

            val direction = Vector2(input.lastLeftClick.x - position.x, input.lastLeftClick.y - position.y).nor()
            if (!direction.isZero) {
                // TODO rotation for box2d body
                transform.direction = direction
            }
            if (input.attackPressed && attack.sinceLastAttack >= attack.cooldown) {
                val bullet = Entity()
                val criticalRoll = MathUtils.random(1.0f) < 0.20f
                bullet.add(PositionComponent(position.x + transform.direction.x * collider.radius, position.y + transform.direction.y * collider.radius))
                bullet.add(VelocityComponent(transform.direction.x * attack.projectileSpeed + MathUtils.random(0.0f, attack.randomSpread), transform.direction.y * attack.projectileSpeed + MathUtils.random(0.0f, attack.randomSpread)))
                bullet.add(LimitedDurationComponent(attack.lifetime))
                bullet.add(CircleColliderComponent(radius = attack.radius, categoryMask = 1, collidesWith = 2))
                bullet.add(HealthComponent(1))
                bullet.add(DamageComponent(100, 20, isCritical = criticalRoll))
                engine.addEntity(bullet)
                attack.sinceLastAttack = 0.0f
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