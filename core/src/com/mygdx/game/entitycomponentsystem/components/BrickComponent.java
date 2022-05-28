package com.mygdx.game.entitycomponentsystem.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.maps.objects.TextureMapObject;

public class BrickComponent implements Component {
    public TextureMapObject textureMapObject;
    public boolean isDead = false;
}
