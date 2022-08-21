package com.mygdx.game.entitycomponentsystem.components;

import com.mygdx.game.gameworld.GameWorld;

public class PotionComponent extends TiledMapComponent
{
    PotionComponent()
    {
        this.belongsToLayer = GameWorld.TM_LAYER_POTIONS;
    }

    @Override
    public void reset() {
        super.reset();
        this.belongsToLayer = GameWorld.TM_LAYER_POTIONS;
    }
}
