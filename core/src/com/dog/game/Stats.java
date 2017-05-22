package com.dog.game;

/**
 * Created by spkerkela on 07/05/2017.
 */
public class Stats {
    public int hitPoints;
    public int mana;
    public int strength;

    @Override
    public String toString() {
        return String.format("HP: %s, MANA: %s, STR: %s", hitPoints, mana, strength);
    }
}
