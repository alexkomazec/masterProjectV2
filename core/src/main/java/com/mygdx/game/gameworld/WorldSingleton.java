package com.mygdx.game.gameworld;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.common.B2dContactListener;

public class WorldSingleton {

    public static WorldSingleton instance;
    private World world;

    public static WorldSingleton getInstance(Vector2 vector, boolean doSleep) {
        if (instance == null) {
            instance = new WorldSingleton(vector, doSleep);
        }
        return instance;
    }

    private WorldSingleton(Vector2 vector, boolean doSleep)
    {
        world = new World(vector, doSleep);
    }

    public World getWorld()
    {
        return this.world;
    }


}
