package com.dog.game.systems

import com.badlogic.ashley.core.*
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.*
import com.dog.game.PhysicsEngine
import com.dog.game.components.*

class PhysicsSystem(priority: Int) : EntitySystem(priority), EntityListener, ContactListener {

    override fun beginContact(contact: Contact?) {
        if (contact != null) {
            val a = contact.fixtureA.body
            val b = contact.fixtureB.body
            val entityA = a.userData
            val entityB = b.userData
            if (entityA is Entity && entityB is Entity) {
                dealDamage(entityA, entityB)
                dealDamage(entityB, entityA)
            }
        }
    }

    private fun dealDamage(entityA: Entity, entityB: Entity) {
        val health = hm.get(entityA)
        val pos = cm.get(entityA)
        val damage = dm.get(entityB)
        if (health is HealthComponent && pos is CircleColliderComponent && damage is DamageComponent) {
            val baseDamage = MathUtils.random(damage.lowerBound, damage.lowerBound + damage.range)
            val textEntity = Entity()
            if (damage.isCritical) {
                val criticalDamage = (baseDamage.toFloat() * damage.criticalMultiplier).toInt()
                health.curHealth -= criticalDamage
                textEntity.add(TextComponent(String.format("*%s*", criticalDamage.toString()), Color.ORANGE, scale = 1.5f))
            } else {
                health.curHealth -= baseDamage
                textEntity.add(TextComponent(baseDamage.toString()))
            }
            textEntity.add(LimitedDurationComponent(2.0f))
            textEntity.add(PositionComponent(pos.body!!.position.x + MathUtils.random(-10f, 10f), pos.body!!.position.y))
            textEntity.add(VelocityComponent(0f, 50f + MathUtils.random(10.0f, 20.0f)))
            engine.addEntity(textEntity)
        }
    }

    override fun endContact(contact: Contact?) {
    }

    override fun preSolve(contact: Contact?, oldManifold: Manifold?) {
    }

    override fun postSolve(contact: Contact?, impulse: ContactImpulse?) {
    }

    val pm: ComponentMapper<PositionComponent> = ComponentMapper.getFor(PositionComponent::class.java)
    val vm: ComponentMapper<VelocityComponent> = ComponentMapper.getFor(VelocityComponent::class.java)
    val cm: ComponentMapper<CircleColliderComponent> = ComponentMapper.getFor(CircleColliderComponent::class.java)
    val hm: ComponentMapper<HealthComponent> = ComponentMapper.getFor(HealthComponent::class.java)
    val dm: ComponentMapper<DamageComponent> = ComponentMapper.getFor(DamageComponent::class.java)
    override fun entityAdded(entity: Entity?) {
        if (entity != null) {
            val collider = cm.get(entity)
            val position = pm.get(entity)
            val velocity = vm.get(entity)
            val bodyDef = BodyDef()
            bodyDef.type = collider.type
            bodyDef.position.set(position.x, position.y)
            val body = PhysicsEngine.world.createBody(bodyDef)
            val circleShape = CircleShape()
            val filter = Filter()
            filter.categoryBits = collider.categoryMask
            filter.maskBits = collider.collidesWith

            circleShape.radius = collider.radius
            body.createFixture(circleShape, 1f).filterData = filter
            body.setLinearVelocity(velocity.x, velocity.y)
            collider.body = body
            circleShape.dispose()
            body.userData = entity
        }
    }

    override fun entityRemoved(entity: Entity?) {
        if (entity != null) {
            val collider = cm.get(entity)
            if (collider.body != null) {
                PhysicsEngine.world.destroyBody(collider.body)
            }
        }
    }

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        Box2D.init()
        PhysicsEngine.world.setContactListener(this)
        Gdx.app.log("Systems", "Physics initialized")
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
    }
}