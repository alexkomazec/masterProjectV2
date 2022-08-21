package com.mygdx.game.entitycomponentsystem.components;

import com.mygdx.game.gameworld.GameWorld;

public class PortalComponent extends TiledMapComponent{

    PortalComponent()
    {
        this.belongsToLayer = GameWorld.TM_LAYER_PORTALS;
    }
}
