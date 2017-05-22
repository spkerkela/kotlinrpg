package com.dog.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.dog.game.DungeonOfGolrockMain;

public class DesktopLauncher {
  public static void main(String[] arg) {
    LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
    config.vSyncEnabled = false;
    config.foregroundFPS = 0;
    config.backgroundFPS = 0;
    new LwjglApplication(new DungeonOfGolrockMain(), config);
  }
}
