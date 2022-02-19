package com.akomazec.BaseSampleProject.Sprites.Bricks;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Body;

public class Brick
{
    public Body body;
    public MapObject mapObject;
    public Boolean shouldBeDestroyed;

    public Brick(Body body, MapObject mapObject, Boolean shouldBeDestroyed)
    {
        this.body = body;
        this.mapObject = mapObject;
        this.shouldBeDestroyed = shouldBeDestroyed;

    }

}
