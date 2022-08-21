package com.mygdx.game.gameworld;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.mygdx.game.entitycomponentsystem.system.DataReceivingSystem;

public class GameWorld {

    private static final Logger logger = new Logger(GameWorld.class.getSimpleName(), Logger.DEBUG);

    /* box2d world represents all bodies in the game world as 2d body with regular physics*/
    private WorldSingleton box2dWorldSingleton;
    private Array<Entity> aplayers;

    /* tiledMap is a game world composed of layers, and each layer is composed of tiled objects*/
    TiledMap tiledMap;
    public static final String TM_LAYER_PLATFORM = "Platform";
    public static final String TM_LAYER_PLAYERS_SPAWN_SPOTS = "PlayersSpawnSpots";
    public static final String TM_LAYER_PORTALS = "Portals";

    public static final String TM_LAYER_BASIC_ENEMIES = "BasicEnemies";
    public static final String TM_LAYER_CLOUD_ENEMIES = "CloudEnemies";

    public static final String TM_LAYER_BASIC_COLLECTIBLES = "PowerUpsBasic";
    public static final String TM_LAYER_POTIONS = "Potions";

    public GameWorld(TiledMap tiledMap)
    {
        WorldSingleton.instance = null;
        this.box2dWorldSingleton = WorldSingleton.getInstance(new Vector2(0, -15), true);
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

    public Entity getPlayerByReference(Entity entity)
    {
        Entity entityTemp = null;

        for (int index = 0; index < aplayers.size; index++) {

            if(this.aplayers.get(index) == entity)
            {
                entityTemp = this.aplayers.get(index);
            }
        }

        if(entityTemp == null)
        {
            logger.error("entityTemp is null");
        }

        return entityTemp;
    }

    public void setPlayer(Entity player) {
        this.aplayers.add(player);
    }

    public WorldSingleton getBox2dWorldSingleton() {
        return box2dWorldSingleton;
    }

    public void reset()
    {
        this.box2dWorldSingleton = null;
        aplayers = null;
    }
}
