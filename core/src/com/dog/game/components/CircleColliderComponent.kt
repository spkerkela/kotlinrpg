package com.dog.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef

class CircleColliderComponent(var radius: Float = 0f, var categoryMask: Short = 0, var collidesWith: Short = 0, var type: BodyDef.BodyType = BodyDef.BodyType.DynamicBody) : Component {
    var body: Body? = null
}