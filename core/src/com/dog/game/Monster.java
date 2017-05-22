package com.dog.game;

import com.badlogic.gdx.utils.Array;

/**
 * Created by spkerkela on 07/05/2017.
 */
public class Monster {
    public String name;
    public Array<String> types;
    public Stats stats;

    @Override
    public String toString() {
        return String.format("%s, types=%s, stats:%s", name, types.toString(", "), stats.toString());
    }
}
