package com.dog.game

import com.badlogic.gdx.math.Vector2
import com.dog.game.components.AttackComponent

/**
 * Created by Simo on 22.5.2017.
 */
class GameData {
    val playerSpeed = 0.0f
    val playerStartPosition: Vector2 = Vector2.Zero
    val attack: AttackComponent = AttackComponent()
    val playerHealth = 100
    val world: World = World(0,0)
}