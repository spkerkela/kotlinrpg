package com.dog.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;


public class DungeonOfGolrockMain extends ApplicationAdapter {
    SpriteBatch batch;
    ShapeRenderer shapeRenderer;
    Texture img;
    Monster[] monsters;
    int monsterCount;
    private BitmapFont font;
    Vector2 lastClick;

    @Override
    public void create() {
        lastClick = new Vector2(0, 0);
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapeRenderer = new ShapeRenderer();
        img = new Texture("badlogic.jpg");
        FileHandle jsonFile = Gdx.files.internal("monsters.json");
        loadGameData(jsonFile);

    }

    private void loadGameData(FileHandle jsonFile) {
        JsonValue base = new JsonReader().parse(jsonFile);
        Json json = new Json();
        json.setTypeName(null);
        json.setUsePrototypes(false);
        json.setIgnoreDeprecated(true);
        monsterCount = base.size;
        monsters = new Monster[monsterCount];
        for (int i = 0; i < monsterCount; i++) {
            Monster monster = json.fromJson(Monster.class, base.get(i).toString());
            monsters[i] = monster;
        }
    }

    @Override
    public void render() {
        handleInput();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        for (int i = 0; i < monsterCount; i++) {
            font.draw(batch, monsters[i].toString(), 3, 200 + 30 * i);
        }
        batch.end();
        shapeRenderer.setColor(Color.GOLD);
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.circle(lastClick.x, lastClick.y, 32);
        shapeRenderer.end();
    }

    private void handleInput() {
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            lastClick.x = Gdx.input.getX();
            lastClick.y = Gdx.graphics.getHeight() - Gdx.input.getY();
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        img.dispose();
        shapeRenderer.dispose();
    }
}
