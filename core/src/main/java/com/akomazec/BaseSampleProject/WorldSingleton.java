package com.akomazec.BaseSampleProject;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class WorldSingleton {

    public static WorldSingleton _instance;
    private World world;

    public static WorldSingleton getInstance(Vector2 vector, boolean doSleep) {
        if (_instance == null) {
            _instance = new WorldSingleton(vector, doSleep);
        }
        return _instance;
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
