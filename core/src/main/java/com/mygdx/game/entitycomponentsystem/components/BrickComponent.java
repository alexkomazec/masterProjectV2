package com.mygdx.game.entitycomponentsystem.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.mygdx.game.gameworld.GameWorld;

public class BrickComponent extends TiledMapComponent {

    BrickComponent()
     {
         this.belongsToLayer = GameWorld.TM_LAYER_PLATFORM;
     }
}
