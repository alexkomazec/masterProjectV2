package com.akomazec.BaseSampleProject.Tools;

import com.akomazec.BaseSampleProject.BaseSampleProject;
import com.akomazec.BaseSampleProject.Sprites.Bricks.Bricks;
import com.akomazec.BaseSampleProject.Sprites.MagicBall;
import com.akomazec.BaseSampleProject.Sprites.Player;
import com.akomazec.BaseSampleProject.WorldSingleton;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;


public class B2WorldCreator {

    public WorldSingleton world;
    public TiledMap map;

    public static B2WorldCreator _instance;

    public static B2WorldCreator getInstance(WorldSingleton world, TiledMap map) {
        if (_instance == null) {
            _instance = new B2WorldCreator(world, map);
        }

        return _instance;
    }


    public B2WorldCreator(WorldSingleton world, TiledMap map)
    {
         this.world = world;
         this.map = map;
    }

    public void createBricks(Bricks bricks)
    {
        //create body and fixture variables
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        //create ground bodies/fixtures
        for(TextureMapObject object : map.getLayers().
                get(0).
                getObjects().
                getByType(TextureMapObject.class))
        {

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
            bdef.position.set((rect.getX() + rect.getWidth() / 2),
                    (rect.getY() + rect.getHeight() / 2));

            body = world.getWorld().createBody(bdef);

            bricks.addBrick(body, object, false);
            int brickIndex = bricks.arrayOfBricks.size - 1;

            shape.setAsBox(rect.getWidth() / 2, rect.getHeight() / 2);
            fdef.shape = shape;

            /* Add fixture to body, also set object reference to the fixture */
            body.createFixture(fdef).setUserData(bricks.arrayOfBricks.get(brickIndex));
        }

     }

    public void createEntity(Player player)
    {
            for(RectangleMapObject object : map.getLayers().
                    get(1).
                    getObjects().
                    getByType(RectangleMapObject.class))
            {

                Rectangle rect = object.getRectangle();

                //Set body definition
                player.bdef.type = BodyDef.BodyType.DynamicBody;
                player.bdef.position.set((rect.getX() + rect.getWidth() / 2) ,
                        (rect.getY() + rect.getHeight() / 2));

                //Create body in the world
                player.b2body = world.getWorld().createBody(player.bdef);
                player.b2body.setUserData(player);

                //Set Fixture def
                player.shape.setAsBox(rect.getWidth() / 2, rect.getHeight() / 2);
                player.fdef.shape = player.shape;
                player.fdef.filter.categoryBits = BaseSampleProject.PLAYER_BIT;
                player.fdef.filter.maskBits = BaseSampleProject.GROUND_BIT
                        | BaseSampleProject.MAGIC_BIT;

                //Create Fixture using set Fixture definition, and set user data
                player.b2body.createFixture(player.fdef).setUserData(player);

            }
    }

    public void createEntity(MagicBall magicBall)
    {

            //Set body definition
            magicBall.bdef.type = BodyDef.BodyType.DynamicBody;
            magicBall.bdef.position.set(magicBall.x, magicBall.y);

            //Create body in the world
            magicBall.b2body = world.getWorld().createBody(magicBall.bdef);
            magicBall.b2body.setUserData(magicBall);

            //Set Fixture def
            magicBall.shape.setAsBox(magicBall.width/2, magicBall.height/2);
            magicBall.fdef.shape = magicBall.shape;
            magicBall.fdef.filter.categoryBits = BaseSampleProject.MAGIC_BIT;
            magicBall.fdef.filter.maskBits = BaseSampleProject.GROUND_BIT
                    | BaseSampleProject.PLAYER_BIT;

            //Create Fixture using set Fixture definition, and set user data
            magicBall.b2body.createFixture(magicBall.fdef).setUserData(magicBall);
    }


}
