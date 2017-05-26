package com.dog.game

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array

/**
 * Created by spkerkela on 07/05/2017.
 */
class Monster {
    var name: String? = null
    var types: Array<String>? = null
    var stats: Stats? = null
    var position: Vector2 = Vector2.Zero

    override fun toString(): String {
        return String.format("%s, types=%s, stats:%s, pos:%s", name, types!!.toString(", "), stats!!.toString(), position.toString())
    }
}
