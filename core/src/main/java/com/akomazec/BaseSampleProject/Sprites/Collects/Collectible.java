package com.akomazec.BaseSampleProject.Sprites.Collects;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Body;

public class Collectible {

    public Body body;
    public MapObject mapObject;
    public Boolean shouldBeDestroyed;

    public Collectible(Body body, MapObject mapObject, Boolean shouldBeDestroyed)
    {
        this.body = body;
        this.mapObject = mapObject;
        this.shouldBeDestroyed = shouldBeDestroyed;

    }
}
