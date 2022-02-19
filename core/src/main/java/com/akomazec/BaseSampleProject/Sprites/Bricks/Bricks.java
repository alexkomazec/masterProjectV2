package com.akomazec.BaseSampleProject.Sprites.Bricks;

import com.akomazec.BaseSampleProject.WorldSingleton;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;

public class Bricks {

    public Array<Brick> arrayOfBricks;

    public WorldSingleton world;
    public TiledMap map;

    public Bricks(WorldSingleton world, TiledMap map)
    {
        this.arrayOfBricks = new Array<>();
        this.world = world;
        this.map = map;
    }

    public void addBrick(Body body, MapObject mapObject, Boolean arrayShouldBeDestroyed)
    {
        arrayOfBricks.add(new Brick(body, mapObject, arrayShouldBeDestroyed));
    }

    public void removeBrick(int index)
    {
        /*Release the memory, by discarding reference for the particular MapObject*/
        map.getLayers().get(0).getObjects().remove(index);

        /*Release the memory, by discarding reference for the particular Body in the world*/
        Body bodyForDeletion = arrayOfBricks.get(index).body;
        this.world.getWorld().destroyBody(bodyForDeletion);

        /*Remove the brick from the array*/
        arrayOfBricks.removeIndex(index);

    }
}
