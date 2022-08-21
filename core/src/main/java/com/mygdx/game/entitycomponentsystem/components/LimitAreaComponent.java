package com.mygdx.game.entitycomponentsystem.components;

import com.mygdx.game.gameworld.GameWorld;

public class LimitAreaComponent extends TiledMapComponent {

    LimitAreaComponent()
    {
        this.belongsToLayer = GameWorld.TM_LAYER_LIMIT_LAYER;
    }
}