package com.akomazec.BaseSampleProject.Sprites.Collects;

import com.akomazec.BaseSampleProject.WorldSingleton;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;

public class Collectibles {

    public Array<Collectible> arrayOfCollectibles;

    public WorldSingleton world;
    public TiledMap map;

    public Collectibles(WorldSingleton world, TiledMap map)
    {
        this.arrayOfCollectibles = new Array<>();
        this.world = world;
        this.map = map;
    }

    public void addCollectible(Body body, MapObject mapObject, Boolean arrayShouldBeDestroyed)
    {
        arrayOfCollectibles.add(new Collectible(body, mapObject, arrayShouldBeDestroyed));
    }

    public void removeCollectible(int index)
    {
        /*Release the memory, by discarding reference for the particular MapObject*/
        map.getLayers().get(2).getObjects().remove(index);

        /*Release the memory, by discarding reference for the particular Body in the world*/
        Body bodyForDeletion = arrayOfCollectibles.get(index).body;
        this.world.getWorld().destroyBody(bodyForDeletion);

        /*Remove the collectible from the array*/
        this.arrayOfCollectibles.removeIndex(index);

    }
}
