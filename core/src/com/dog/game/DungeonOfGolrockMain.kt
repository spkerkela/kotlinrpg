package com.dog.game

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3


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
    internal var camera: OrthographicCamera? = null
    internal val rotateSpeed = 0.02f

    override fun create() {
        lastClick = Vector2(0f, 0f)
        batch = SpriteBatch()
        font = BitmapFont()
        shapeRenderer = ShapeRenderer()
        img = Texture("badlogic.jpg")
        val magic = 60f
        camera = OrthographicCamera(magic, magic / (Gdx.graphics.height / Gdx.graphics.width))
        camera!!.position.set(camera!!.viewportWidth / 2f, camera!!.viewportHeight / 2f, 30f)
        camera!!.update()
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
        println(loadedData.world.width)
        println(loadedData.world.height)
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

    override fun resize(width: Int, height: Int) {
        camera!!.viewportWidth = 30f
        camera!!.viewportHeight = 30f * height / width
        camera!!.update()
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
        update(Gdx.graphics.deltaTime)
        camera!!.position.set(playerPosition.x, playerPosition.y, 0f)
        camera!!.update()

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batch?.projectionMatrix = (camera!!.combined)
        batch?.begin()
        (0..monsterCount - 1)
                .map { monsters!![it] }
                .forEach { it ->
                    if (it != null && it.position != null) {
                        font!!.draw(batch, it.toString(), (it.position as Vector2).x, (it.position as Vector2).y)
                    }
                }
        batch?.end()
        shapeRenderer?.projectionMatrix = (camera!!.combined)
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
            lastClick.y = Gdx.input.y.toFloat()
            val projection = camera!!.unproject(Vector3(lastClick.x, lastClick.y, 0f))
            lastClick.set(projection.x, projection.y)
            if (movementEnabled) {
                playerMoveTarget = lastClick
            } else {
                playerMoveTarget = playerPosition
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            camera!!.zoom += 0.02f
            //If the A Key is pressed, add 0.02 to the Camera's Zoom
        }
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            camera!!.zoom -= 0.02f
            //If the Q Key is pressed, subtract 0.02 from the Camera's Zoom
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera!!.translate(-3f, 0f, 0f)
            //If the LEFT Key is pressed, translate the camera -3 units in the X-Axis
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera!!.translate(3f, 0f, 0f)
            //If the RIGHT Key is pressed, translate the camera 3 units in the X-Axis
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera!!.translate(0f, -3f, 0f)
            //If the DOWN Key is pressed, translate the camera -3 units in the Y-Axis
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera!!.translate(0f, 3f, 0f)
            //If the UP Key is pressed, translate the camera 3 units in the Y-Axis
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            camera!!.rotate(-rotateSpeed, 0f, 0f, 1f)
            //If the W Key is pressed, rotate the camera by -rotationSpeed around the Z-Axis
        }
        if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            camera!!.rotate(rotateSpeed, 0f, 0f, 1f)
            //If the E Key is pressed, rotate the camera by rotationSpeed around the Z-Axis
        }

        camera!!.zoom = MathUtils.clamp(camera!!.zoom, 20f, 1000 / camera!!.viewportWidth)

        val effectiveViewportWidth = camera!!.viewportWidth * camera!!.zoom
        val effectiveViewportHeight = camera!!.viewportHeight * camera!!.zoom

        camera!!.position.x = MathUtils.clamp(camera!!.position.x, effectiveViewportWidth / 2f, 100 - effectiveViewportWidth / 2f)
        camera!!.position.y = MathUtils.clamp(camera!!.position.y, effectiveViewportHeight / 2f, 100 - effectiveViewportHeight / 2f)
    }

    override fun dispose() {
        batch?.dispose()
        img?.dispose()
        shapeRenderer?.dispose()
    }
}
