package com.dog.game

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
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
import com.dog.game.components.*
import com.dog.game.systems.InputSystem
import com.dog.game.systems.MovementSystem
import com.dog.game.systems.PlayerControllerSystem

class DungeonOfGolrockMain : ApplicationAdapter() {
    internal var batch: SpriteBatch? = null
    internal var shapeRenderer: ShapeRenderer? = null
    internal var img: Texture? = null
    internal var monsters: Array<Monster?>? = null
    internal var monsterCount: Int = 0
    private var font: BitmapFont? = null
    internal var camera: OrthographicCamera? = null
    internal val engine = Engine()

    override fun create() {
        batch = SpriteBatch()
        font = BitmapFont()
        shapeRenderer = ShapeRenderer()
        img = Texture("badlogic.jpg")
        val magic = 60f
        camera = OrthographicCamera(magic, magic / (Gdx.graphics.height / Gdx.graphics.width))
        camera!!.position.set(camera!!.viewportWidth / 2f, camera!!.viewportHeight / 2f, 30f)
        camera!!.zoom = 20f
        camera!!.update()
        val monsterDataFile = Gdx.files.internal("monsters.json")
        val gameDataFile = Gdx.files.internal("game-data.json")
        loadMonsterData(monsterDataFile)
        val gameData = loadGameData(gameDataFile)
        val entity = Entity()
        entity.add(PositionComponent(x = gameData.playerStartPosition.x, y = gameData.playerStartPosition.y))
        entity.add(VelocityComponent())
        entity.add(TransformComponent())
        entity.add(InputComponent())
        entity.add(PlayerComponent(speed = gameData.playerSpeed))
        engine.addEntity(entity)
        engine.addSystem(MovementSystem())
        engine.addSystem(PlayerControllerSystem())
        engine.addSystem(InputSystem(camera!!))
    }

    private fun loadGameData(gameDataFile: FileHandle): GameData {
        val json = Json()
        json.setTypeName(null)
        json.setUsePrototypes(false)
        json.setIgnoreDeprecated(true)
        val loadedData = json.fromJson(GameData::class.java, gameDataFile)
        val gameData = loadedData
        println(loadedData.world.width)
        println(loadedData.world.height)
        if (gameData != null) {
            return gameData
        } else {
            return GameData()
        }
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

    override fun render() {
        engine.update(Gdx.graphics.deltaTime)
        val p = engine.getEntitiesFor(Family.one(PlayerComponent::class.java).get()).first()
        val pos = p.getComponent(PositionComponent::class.java)
        val transform = p.getComponent(TransformComponent::class.java)
        camera!!.position.set(pos.x, pos.y, 0f)
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
        shapeRenderer?.color = Color.ORANGE
        shapeRenderer?.circle(pos.x, pos.y, 26f)
        shapeRenderer?.color = Color.RED
        shapeRenderer?.color = Color.WHITE
        shapeRenderer?.line(Vector2(pos.x, pos.y),
                Vector2(pos.x + (transform.direction.x * 100), pos.y + (transform.direction.y * 100)))
        shapeRenderer?.end()
    }

    override fun dispose() {
        batch?.dispose()
        img?.dispose()
        shapeRenderer?.dispose()
    }
}
