package com.dog.game.components

import com.badlogic.ashley.core.Component

class DamageComponent(var lowerBound: Int = 0, var range: Int = 0, val isCritical: Boolean = false, val criticalMultiplier: Double = 2.0) : Component
