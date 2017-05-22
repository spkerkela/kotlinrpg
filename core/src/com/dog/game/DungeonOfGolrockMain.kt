package com.dog.game

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonReader


class DungeonOfGolrockMain : ApplicationAdapter() {
    internal var batch: SpriteBatch? = null
    internal var shapeRenderer: ShapeRenderer? = null
    internal var img: Texture? = null
    internal var monsters: Array<Monster?>? = null
    internal var monsterCount: Int = 0
    private var font: BitmapFont? = null
    internal var lastClick: Vector2? = null

    override fun create() {
        lastClick = Vector2(0f, 0f)
        batch = SpriteBatch()
        font = BitmapFont()
        shapeRenderer = ShapeRenderer()
        img = Texture("badlogic.jpg")
        val jsonFile = Gdx.files.internal("monsters.json")
        loadGameData(jsonFile)

    }

    private fun loadGameData(jsonFile: FileHandle) {
        val base = JsonReader().parse(jsonFile)
        val json = Json()
        json.setTypeName(null)
        json.setUsePrototypes(false)
        json.setIgnoreDeprecated(true)
        monsterCount = base.size
        monsters = arrayOfNulls<Monster>(size = monsterCount)
        for (i in 0..monsterCount - 1) {
            val monster = json.fromJson(Monster::class.java, base.get(i).toString())
            monsters!![i] = monster
        }
    }

    override fun render() {
        handleInput()
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batch?.begin()
        for (i in 0..monsterCount - 1) {
            font!!.draw(batch, monsters!![i].toString(), 3f, (200 + 30 * i).toFloat())
        }
        batch?.end()
        shapeRenderer?.color = Color.GOLD
        shapeRenderer?.begin(ShapeType.Filled)
        shapeRenderer?.circle(lastClick!!.x, lastClick?.y!!, 32f)
        shapeRenderer?.end()
    }

    private fun handleInput() {
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            lastClick?.x = Gdx.input.x.toFloat()
            lastClick?.y = (Gdx.graphics.height - Gdx.input.y).toFloat()
        }
    }

    override fun dispose() {
        batch?.dispose()
        img?.dispose()
        shapeRenderer?.dispose()
    }
}
