package com.dog.game

/**
 * Created by spkerkela on 07/05/2017.
 */
class Stats {
    var hitPoints: Int = 0
    var mana: Int = 0
    var strength: Int = 0

    override fun toString(): String {
        return String.format("HP: %s, MANA: %s, STR: %s", hitPoints, mana, strength)
    }
}
