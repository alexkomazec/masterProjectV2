package com.mygdx.game.gameworld;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class GameWorld {

    /* box2d world represents all bodies in the game world as 2d body with regular physics*/
    private final WorldSingleton box2dWorldSingleton;
    private Array<Entity> aplayers;

    /* tiledMap is a game world composed of layers, and each layer is composed of tiled objects*/
    TiledMap tiledMap;
    public static final String TM_LAYER_PLATFORM = "Platform";
    public static final String TM_LAYER_PLAYERS_SPAWN_SPOTS = "PlayersSpawnSpots";

    public static final String TM_LAYER_BASIC_ENEMIES = "BasicEnemies";
    public static final String TM_LAYER_CLOUD_ENEMIES = "CloudEnemies";

    public static final String TM_LAYER_BASIC_COLLECTIBLES = "PowerUpsBasic";
    public static final String TM_LAYER_POTIONS = "Potions";

    public GameWorld(TiledMap tiledMap)
    {
        this.box2dWorldSingleton = WorldSingleton.getInstance(new Vector2(0, -9), true);
        this.tiledMap = tiledMap;
        this.aplayers = new Array<>();
    }

    public WorldSingleton getWorldSingleton()
    {
        return this.box2dWorldSingleton;
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }

    public Entity getPlayer(int playerID) {
        return this.aplayers.get(playerID);
    }

    public void setPlayer(Entity player) {
        this.aplayers.add(player);
    }

    public WorldSingleton getBox2dWorldSingleton() {
        return box2dWorldSingleton;
    }
}
