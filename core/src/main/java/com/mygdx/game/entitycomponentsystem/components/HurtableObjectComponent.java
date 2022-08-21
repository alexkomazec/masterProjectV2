package com.mygdx.game.entitycomponentsystem.components;

import com.mygdx.game.gameworld.GameWorld;

public class HurtableObjectComponent extends TiledMapComponent {

    HurtableObjectComponent()
    {
        this.belongsToLayer = GameWorld.TM_LAYER_HURTABLE_OBJECTS_LAYER;
    }
}