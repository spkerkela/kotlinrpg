package com.dog.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Color

class TextComponent(var text: String = "", var color: Color? = Color.WHITE, var scale: Float = 1.0f) : Component