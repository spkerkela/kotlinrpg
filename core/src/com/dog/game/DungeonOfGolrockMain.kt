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
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonReader
import com.dog.game.components.*
import com.dog.game.systems.*

class DungeonOfGolrockMain : ApplicationAdapter() {
    internal var batch: SpriteBatch? = null
    internal var shapeRenderer: ShapeRenderer? = null
    internal var debugRenderer: Box2DDebugRenderer? = null
    internal var img: Texture? = null
    internal var monsters: Array<Monster?>? = null
    internal var monsterCount: Int = 0
    private var font: BitmapFont? = null
    internal var camera: OrthographicCamera? = null
    internal val engine = Engine()
    internal val positionMapper: ComponentMapper<PositionComponent> = ComponentMapper.getFor(PositionComponent::class.java)
    internal val colliderMapper: ComponentMapper<CircleColliderComponent> = ComponentMapper.getFor(CircleColliderComponent::class.java)
    internal val circleMapper: ComponentMapper<CircleComponent> = ComponentMapper.getFor(CircleComponent::class.java)
    internal val textMapper: ComponentMapper<TextComponent> = ComponentMapper.getFor(TextComponent::class.java)

    override fun create() {
        batch = SpriteBatch()
        font = BitmapFont()
        shapeRenderer = ShapeRenderer()
        debugRenderer = Box2DDebugRenderer()
        img = Texture("badlogic.jpg")
        val magic = 60f
        camera = OrthographicCamera(magic, magic / (Gdx.graphics.height / Gdx.graphics.width))
        camera!!.position.set(camera!!.viewportWidth / 2f, camera!!.viewportHeight / 2f, 30f)
        camera!!.zoom = 20f
        camera!!.update()
        val monsterDataFile = Gdx.files.internal("monsters.json")
        val gameDataFile = Gdx.files.internal("game-data.json")
        val gameData = loadGameData(gameDataFile)

        engine.addSystem(MovementSystem())
        engine.addSystem(InputSystem(camera!!))
        engine.addSystem(LimitedDurationSystem())
        engine.addSystem(HealthSystem())
        engine.addSystem(AttackSystem())
        val physicsSystem = PhysicsSystem(2)
        engine.addSystem(physicsSystem)
        engine.addEntityListener(Family.all(VelocityComponent::class.java, CircleColliderComponent::class.java, PositionComponent::class.java).get(), physicsSystem)
        loadMonsterData(monsterDataFile)
        for (x in 0..50) {
            for (y in 0..50) {
                engine.addEntity(createObject(Vector2(x * 100.0f, y * 100.0f)))
            }
        }
        val player = createPlayer(gameData)
        engine.addEntity(player)
        engine.addSystem(PlayerControllerSystem())
    }

    private fun createPlayer(gameData: GameData): Entity {
        val entity = Entity()
        val attack = gameData.attack
        entity.add(PositionComponent(x = gameData.playerStartPosition.x, y = gameData.playerStartPosition.y))
        entity.add(VelocityComponent())
        entity.add(TransformComponent())
        entity.add(InputComponent(movementEnabled = false))
        entity.add(PlayerComponent(speed = gameData.playerSpeed))
        entity.add(CircleColliderComponent(radius = 50f, categoryMask = 1, collidesWith = 2, type = BodyDef.BodyType.KinematicBody))
        entity.add(HealthComponent(gameData.playerHealth))
        entity.add(AttackComponent(radius = attack.radius, cooldown = attack.cooldown, lifetime = attack.lifetime, projectileSpeed = attack.projectileSpeed))
        return entity
    }

    private fun createObject(p: Vector2): Entity {
        val entity = Entity()
        entity.add(PositionComponent(p.x, p.y))
        entity.add(VelocityComponent())
        entity.add(TransformComponent())
        entity.add(CircleColliderComponent(radius = 10.0f, categoryMask = 2, collidesWith = 1))
        entity.add(DamageComponent(20, 5, isCritical = true))
        entity.add(HealthComponent(100))

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
            monsterEntity.add(VelocityComponent())
            monsterEntity.add(CircleColliderComponent(radius = 30f, collidesWith = 1, categoryMask = 2, type = BodyDef.BodyType.KinematicBody))
            monsterEntity.add(HealthComponent(monster.stats!!.hitPoints))
            engine.addEntity(monsterEntity)
        }
    }

    override fun resize(width: Int, height: Int) {
        camera!!.viewportWidth = 30f
        camera!!.viewportHeight = 30f * height / width
        camera!!.update()
    }

    override fun render() {

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        val players = engine.getEntitiesFor(Family.one(PlayerComponent::class.java).get())
        if (players.count() > 0) {
            val world = PhysicsEngine.world
            val p = players.first()
            val collider = colliderMapper.get(p)
            val pos = collider.body!!.position
            camera!!.position.set(pos.x, pos.y, 0f)
            camera!!.update()
            debugRenderer!!.render(world, camera!!.combined)
            engine.update(Gdx.graphics.deltaTime)
            val transform = p.getComponent(TransformComponent::class.java)

            batch?.projectionMatrix = (camera!!.combined)
            batch?.begin()
            for (textEntity in engine.getEntitiesFor(Family.all(PositionComponent::class.java, TextComponent::class.java).get())) {
                val textPos = positionMapper.get(textEntity)
                val textComponent = textMapper.get(textEntity)
                font!!.color = textComponent.color
                font!!.data.setScale(textComponent.scale)
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

            PhysicsEngine.doPhysicsStep(Gdx.graphics.deltaTime)
        }

    }

    override fun dispose() {
        batch?.dispose()
        img?.dispose()
        shapeRenderer?.dispose()
    }
}
