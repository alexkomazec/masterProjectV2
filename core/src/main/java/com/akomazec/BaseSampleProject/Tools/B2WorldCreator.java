package com.akomazec.BaseSampleProject.Tools;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;


public class B2WorldCreator {

    World world;
    TiledMap map;

    public static B2WorldCreator _instance;

    public static B2WorldCreator getInstance(World world, TiledMap map) {
        if (_instance == null) {
            _instance = new B2WorldCreator(world, map);
        }

        return _instance;
    }


    public B2WorldCreator(World world, TiledMap map){
         this.world = world;
         this.map = map;
        //create body and fixture variables
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        //create ground bodies/fixtures
        for(TextureMapObject object : map.getLayers().get(0).getObjects().getByType(TextureMapObject.class)) {

            float width = 1;
            float height = 1;

            String mapTilewidth = object.getProperties().get("width").toString();
            String mapTileheight = object.getProperties().get("height").toString();

            width = Float.parseFloat(mapTilewidth);
            height = Float.parseFloat(mapTileheight);

            float xPos = object.getX();
            float yPos = object.getY();

            Rectangle rect = new Rectangle(xPos, yPos, width, height);

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2), (rect.getY() + rect.getHeight() / 2));

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2, rect.getHeight() / 2);
            fdef.shape = shape;
            body.createFixture(fdef);
        }


    }

}
