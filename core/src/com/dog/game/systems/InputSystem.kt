package com.dog.game.systems

import com.badlogic.ashley.core.*
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector3
import com.dog.game.components.InputComponent

class InputSystem(private val camera: OrthographicCamera) : IteratingSystem(Family.all(InputComponent::class.java).get()) {
    internal val im = ComponentMapper.getFor(InputComponent::class.java)
    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val input = im.get(entity)
        input.movementEnabled = !Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
        input.attackPressed = Gdx.input.isButtonPressed(Input.Buttons.RIGHT)
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) || Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            val projection = camera.unproject(Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))
            input.lastLeftClick.set(projection.x, projection.y)
        }

        if(input.attackPressed) {
            val projection = camera.unproject(Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))
            input.lastRightClick.set(projection.x, projection.y)
            input.movementEnabled = false
        }
    }
}