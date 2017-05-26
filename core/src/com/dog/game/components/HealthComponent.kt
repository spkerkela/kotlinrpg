package com.dog.game.components

import com.badlogic.ashley.core.Component

/**
 * Created by spkerkela on 27/05/2017.
 */
class HealthComponent(maxHealth: Int) : Component {
    var maxHealth = 0
    var curHealth = maxHealth

}