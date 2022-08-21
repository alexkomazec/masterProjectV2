package com.mygdx.game.entitycomponentsystem.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.utils.Pool;

public class TiledMapComponent implements Component, Pool.Poolable {

    public TextureMapObject textureMapObject;
    public boolean isDead = false;
    public String belongsToLayer;

    @Override
    public void reset() {
        textureMapObject = null;
        isDead = false;
    }
}
