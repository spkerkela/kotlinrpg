package com.dog.game

import com.badlogic.gdx.utils.Array

/**
 * Created by spkerkela on 07/05/2017.
 */
class Monster {
    var name: String? = null
    var types: Array<String>? = null
    var stats: Stats? = null

    override fun toString(): String {
        return String.format("%s, types=%s, stats:%s", name, types!!.toString(", "), stats!!.toString())
    }
}
