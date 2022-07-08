package com.mygdx.game.entitycomponentsystem.components;


import static com.mygdx.game.config.GameConfig.DOUBLE_JUMP;

import com.badlogic.ashley.core.Component;
import com.mygdx.game.config.GameConfig;
import com.mygdx.game.gameworld.GameWorld;


public class CollectibleBasicComponent extends TiledMapComponent
{
    public int type;
    CollectibleBasicComponent()
    {
       this.type = GameConfig.DEFAULT_TYPE;
       this.belongsToLayer = GameWorld.TM_LAYER_BASIC_COLLECTIBLES;
    }
}
