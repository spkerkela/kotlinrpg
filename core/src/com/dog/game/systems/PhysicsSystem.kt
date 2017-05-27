package com.dog.game.systems

import com.badlogic.ashley.core.*
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.dog.game.components.CircleColliderComponent
import com.dog.game.components.PositionComponent
import com.dog.game.components.VelocityComponent

class PhysicsSystem(priority: Int) : EntitySystem(priority), EntityListener, ContactListener {
    override fun endContact(contact: Contact?) {
    }

    override fun beginContact(contact: Contact?) {
    }

    override fun preSolve(contact: Contact?, oldManifold: Manifold?) {
    }

    override fun postSolve(contact: Contact?, impulse: ContactImpulse?) {
    }

    val pm: ComponentMapper<PositionComponent> = ComponentMapper.getFor(PositionComponent::class.java)
    val vm: ComponentMapper<VelocityComponent> = ComponentMapper.getFor(VelocityComponent::class.java)
    val cm: ComponentMapper<CircleColliderComponent> = ComponentMapper.getFor(CircleColliderComponent::class.java)
    override fun entityAdded(entity: Entity?) {
        if (entity != null) {
            val collider = cm.get(entity)
            val position = pm.get(entity)
            val velocity = vm.get(entity)
            val bodyDef = BodyDef()
            bodyDef.type = collider.type
            bodyDef.position.set(position.x, position.y)
            val body = world.createBody(bodyDef)
            val circleShape = CircleShape()
            val filter = Filter()
            filter.categoryBits = collider.categoryMask
            filter.maskBits = collider.collidesWith

            circleShape.radius = collider.radius
            body.createFixture(circleShape, 1f).filterData = filter
            body.setLinearVelocity(velocity.x, velocity.y)
            collider.body = body
            circleShape.dispose()
        }
    }

    override fun entityRemoved(entity: Entity?) {
        if (entity != null) {
            val collider = cm.get(entity)
            if (collider.body != null) {
                world.destroyBody(collider.body)
            }
        }
    }

    internal var accum = 0f
    internal var world = World(Vector2(0.0f, 0.0f), true)
    val timeStep = 1.0f / 300f
    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        Box2D.init()
        world.setContactListener(this)
        Gdx.app.log("Systems", "Physics initialized")
    }

    fun doPhysicsStep(deltaTime: Float) {
        val frameTime = Math.min(deltaTime, 0.25f)
        accum += frameTime
        while (accum >= timeStep) {
            world.step(timeStep, 6, 2)
            accum -= timeStep
        }
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        doPhysicsStep(deltaTime)
    }
}