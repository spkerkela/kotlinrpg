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
    internal var lastClick: Vector2 = Vector2.Zero
    internal var playerPosition: Vector2 = Vector2.Zero
    internal var playerFacing: Vector2 = Vector2.Y
    internal var playerSpeed = 30.0f
    internal var playerMoveTarget = Vector2.Zero
    internal var gameData: GameData? = null
    internal var movementEnabled = true

    override fun create() {
        lastClick = Vector2(0f, 0f)
        batch = SpriteBatch()
        font = BitmapFont()
        shapeRenderer = ShapeRenderer()
        img = Texture("badlogic.jpg")
        val monsterDataFile = Gdx.files.internal("monsters.json")
        val gameDataFile = Gdx.files.internal("game-data.json")
        loadMonsterData(monsterDataFile)
        loadGameData(gameDataFile)
    }

    private fun loadGameData(gameDataFile: FileHandle) {
        val json = Json()
        json.setTypeName(null)
        json.setUsePrototypes(false)
        json.setIgnoreDeprecated(true)
        val loadedData = json.fromJson(GameData::class.java, gameDataFile)
        playerPosition = loadedData.playerStartPosition
        playerMoveTarget = loadedData.playerStartPosition
        playerSpeed = loadedData.playerSpeed
        gameData = loadedData

    }

    private fun loadMonsterData(monsterDataFile: FileHandle) {
        val base = JsonReader().parse(monsterDataFile)
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

    fun update(delta: Float) {
        val direction = Vector2(lastClick.x.minus(playerPosition.x), lastClick.y.minus(playerPosition.y)).nor()
        playerFacing = direction
        val distance = playerPosition.dst2(playerMoveTarget)
        if (distance > (playerSpeed * delta) + 100) {
            playerPosition.add(direction.x * playerSpeed * delta, direction.y * playerSpeed * delta)
        }
    }

    override fun render() {
        handleInput()
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        update(Gdx.graphics.deltaTime)
        batch?.begin()
        (0..monsterCount - 1)
                .map { monsters!![it] }
                .forEach { it ->
                    if (it != null && it.position != null) {
                        font!!.draw(batch, it.toString(), (it.position as Vector2).x, (it.position as Vector2).y)
                    }
                }
        batch?.end()
        shapeRenderer?.color = Color.GOLD
        shapeRenderer?.begin(ShapeType.Filled)
        shapeRenderer?.circle(lastClick.x, lastClick.y, 32f)
        shapeRenderer?.color = Color.RED
        shapeRenderer?.circle(playerPosition.x, playerPosition.y, 16f)
        shapeRenderer?.color = Color.WHITE
        shapeRenderer?.line(playerPosition,
                Vector2(playerPosition.x + playerFacing.x * 100, playerPosition.y + playerFacing.y * 100))
        shapeRenderer?.end()
    }

    private fun handleInput() {
        movementEnabled = !Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            lastClick.x = Gdx.input.x.toFloat()
            lastClick.y = (Gdx.graphics.height - Gdx.input.y).toFloat()
            if (movementEnabled) {
                playerMoveTarget = lastClick
            } else {
                playerMoveTarget = playerPosition
            }
        }
    }

    override fun dispose() {
        batch?.dispose()
        img?.dispose()
        shapeRenderer?.dispose()
    }
}
