package com.dog.game.components

import com.badlogic.ashley.core.Component

class AttackComponent(var radius: Float = 0.0f, var lifetime: Float = 0.0f, var projectileSpeed: Float = 0.0f, var randomSpread: Float = 0.0f, var cooldown: Float = 0.0f, var sinceLastAttack: Float = 0.0f) : Component