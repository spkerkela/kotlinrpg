package com.dog.game

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonReader
import com.dog.game.components.*
import com.dog.game.systems.InputSystem
import com.dog.game.systems.LimitedDurationSystem
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
    internal val positionMapper: ComponentMapper<PositionComponent> = ComponentMapper.getFor(PositionComponent::class.java)
    internal val circleMapper: ComponentMapper<CircleComponent> = ComponentMapper.getFor(CircleComponent::class.java)
    internal val textMapper: ComponentMapper<TextComponent> = ComponentMapper.getFor(TextComponent::class.java)

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
        val gameData = loadGameData(gameDataFile)
        val player = createPlayer(gameData)
        for (x in 0..50) {
            for (y in 0..50) {
                engine.addEntity(createObject(Vector2(x * 30.0f, y * 40.0f), Color.TEAL))
            }
        }
        engine.addEntity(player)
        engine.addSystem(MovementSystem())
        engine.addSystem(PlayerControllerSystem())
        engine.addSystem(InputSystem(camera!!))
        engine.addSystem(LimitedDurationSystem())
        loadMonsterData(monsterDataFile)
    }

    private fun createPlayer(gameData: GameData): Entity {
        val entity = Entity()
        entity.add(PositionComponent(x = gameData.playerStartPosition.x, y = gameData.playerStartPosition.y))
        entity.add(VelocityComponent())
        entity.add(TransformComponent())
        entity.add(InputComponent())
        entity.add(PlayerComponent(speed = gameData.playerSpeed))
        entity.add(CircleComponent(radius = 50f, color = Color.GREEN))
        return entity
    }

    private fun createObject(p: Vector2, color: Color): Entity {
        val entity = Entity()
        entity.add(PositionComponent(p.x, p.y))
        entity.add(VelocityComponent())
        entity.add(TransformComponent())
        entity.add(InputComponent())
        entity.add(CircleComponent(radius = 10.0f, color = color))
        entity.add(LimitedDurationComponent(MathUtils.random(0.5f, 10.0f)))
        return entity
    }

    private fun loadGameData(gameDataFile: FileHandle): GameData {
        val json = Json()
        json.setTypeName(null)
        json.setUsePrototypes(false)
        json.setIgnoreDeprecated(true)
        val loadedData = json.fromJson(GameData::class.java, gameDataFile)
        val gameData = loadedData
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
            val monsterEntity = Entity()
            monsterEntity.add(TextComponent(monster.toString(), Color.RED))
            monsterEntity.add(PositionComponent(monster.position))

            engine.addEntity(monsterEntity)
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
        val pos = positionMapper.get(p)
        val transform = p.getComponent(TransformComponent::class.java)
        camera!!.position.set(pos.x, pos.y, 0f)
        camera!!.update()

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batch?.projectionMatrix = (camera!!.combined)
        batch?.begin()
        for (textEntity in engine.getEntitiesFor(Family.all(PositionComponent::class.java, TextComponent::class.java).get())) {
            val textPos = positionMapper.get(textEntity)
            val textComponent = textMapper.get(textEntity)
            font!!.color = textComponent.color
            font!!.draw(batch, textComponent.text, textPos.x, textPos.y)
        }
        batch?.end()
        shapeRenderer?.projectionMatrix = (camera!!.combined)
        shapeRenderer?.begin(ShapeType.Filled)
        val entities = engine.getEntitiesFor(Family.all(PositionComponent::class.java, CircleComponent::class.java).get())
        for (entity in entities) {
            val circle = circleMapper.get(entity)
            val entityPos = positionMapper.get(entity)
            shapeRenderer?.color = circle.color
            shapeRenderer?.circle(entityPos.x, entityPos.y, circle.radius)
        }
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
