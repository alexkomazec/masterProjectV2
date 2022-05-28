package com.mygdx.game.gameworld;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;

public class GameWorld {

    /* box2d world represents all bodies in the game world as 2d body with regular physics*/
    private final WorldSingleton box2dWorldSingleton;
    private Entity player;

    /* tiledMap is a game world composed of layers, and each layer is composed of tiled objects*/
    TiledMap tiledMap;
    public static final String TM_LAYER_PLATFORM = "Platform";
    public static final String TM_LAYER_PLAYERS = "Players";

    public static final String TM_LAYER_BASIC_ENEMIES = "BasicEnemies";
    public static final String TM_LAYER_CLOUD_ENEMIES = "CloudEnemies";

    public GameWorld(TiledMap tiledMap)
    {
        this.box2dWorldSingleton = WorldSingleton.getInstance(new Vector2(0, -9), true);
        this.tiledMap = tiledMap;
    }

    public WorldSingleton getWorldSingleton()
    {
        return this.box2dWorldSingleton;
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }

    public Entity getPlayer() {
        return player;
    }

    public void setPlayer(Entity player) {
        this.player = player;
    }
}
