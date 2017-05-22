package com.dog.game

/**
 * Created by spkerkela on 07/05/2017.
 */
class Stats {
    val hitPoints = 0
    val mana = 0
    val strength = 0

    override fun toString(): String {
        return String.format("HP: %s, MANA: %s, STR: %s", hitPoints, mana, strength)
    }
}
