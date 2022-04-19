package com.akomazec.BaseSampleProject.Tools;

import com.akomazec.BaseSampleProject.BaseSampleProject;
import com.akomazec.BaseSampleProject.Screens.MainScreen;
import com.akomazec.BaseSampleProject.Sprites.Bricks.Bricks;
import com.akomazec.BaseSampleProject.Sprites.Collects.Collectibles;
import com.akomazec.BaseSampleProject.Sprites.Enemy;
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
import com.badlogic.gdx.utils.compression.lzma.Base;


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
            fdef.filter.categoryBits = MainScreen.GROUND_BIT;
            fdef.filter.maskBits = MainScreen.MAGIC_BIT
                    | MainScreen.PLAYER_BIT
                    | MainScreen.ENEMY_BIT;

            /* Add fixture to body, also set object reference to the fixture */
            body.createFixture(fdef).setUserData(bricks.arrayOfBricks.get(brickIndex));
        }
    }

    public void createCollectibles(Collectibles collectibles)
    {
        //create body and fixture variables
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        //create ground bodies/fixtures
        for(TextureMapObject object : map.getLayers().
                get(2).
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

            collectibles.addCollectible(body, object, false);
            int collectibleIndex = collectibles.arrayOfCollectibles.size - 1;

            shape.setAsBox(rect.getWidth() / 2, rect.getHeight() / 2);
            fdef.shape = shape;
            fdef.filter.categoryBits = MainScreen.COLLECTIBLE_BIT;
            fdef.filter.maskBits =
                    MainScreen.GROUND_BIT
                            | MainScreen.PLAYER_BIT
                            | MainScreen.ENEMY_BIT
                            | MainScreen.MAGIC_BIT;

            /* Add fixture to body, also set object reference to the fixture */
            body.createFixture(fdef).
                    setUserData(collectibles.arrayOfCollectibles.get(collectibleIndex));
        }
    }

    public void createEntity(Player player, float x_pos, float y_pos)
    {

        for(RectangleMapObject object : map.getLayers().
                get(1).
                getObjects().
                getByType(RectangleMapObject.class))
        {

            Rectangle rect = object.getRectangle();

            //Set body definition
            player.bdef.type = BodyDef.BodyType.DynamicBody;

            if(x_pos > 0 && y_pos > 0)
            {
                player.bdef.position.set(x_pos, y_pos);
            }
            else
            {
                player.bdef.position.set((rect.getX() + rect.getWidth() / 2) ,
                        (rect.getY() + rect.getHeight() / 2));
            }

            //Create body in the world
            player.b2body = world.getWorld().createBody(player.bdef);
            player.b2body.setUserData(player);

            //Set Fixture def
            player.shape.setAsBox(rect.getWidth() / 2, rect.getHeight() / 2);
            player.fdef.shape = player.shape;
            player.fdef.filter.categoryBits = MainScreen.PLAYER_BIT;
            player.fdef.filter.maskBits = MainScreen.GROUND_BIT
                    | MainScreen.MAGIC_BIT
                    | MainScreen.COLLECTIBLE_BIT;

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
        magicBall.fdef.filter.categoryBits = MainScreen.MAGIC_BIT;
        magicBall.fdef.filter.maskBits = MainScreen.GROUND_BIT
                | MainScreen.PLAYER_BIT
                | MainScreen.ENEMY_BIT;

        //Create Fixture using set Fixture definition, and set user data
        magicBall.b2body.createFixture(magicBall.fdef).setUserData(magicBall);
    }

    public void createEntity(Enemy enemy) {
        for (RectangleMapObject object : map.getLayers().
                get(3).
                getObjects().
                getByType(RectangleMapObject.class)) {

            Rectangle rect = object.getRectangle();

            //Set body definition
            enemy.bdef.type = BodyDef.BodyType.DynamicBody;
            enemy.bdef.position.set((rect.getX() + rect.getWidth() / 2),
                    (rect.getY() + rect.getHeight() / 2));

            //Create body in the world
            enemy.b2body = world.getWorld().createBody(enemy.bdef);
            enemy.b2body.setUserData(enemy);

            //Set Fixture def
            enemy.shape.setAsBox(rect.getWidth() / 2, rect.getHeight() / 2);
            enemy.fdef.shape = enemy.shape;
            enemy.fdef.filter.categoryBits = MainScreen.ENEMY_BIT;
            enemy.fdef.filter.maskBits =
                    MainScreen.GROUND_BIT
                            |   MainScreen.MAGIC_BIT
                            |   MainScreen.PLAYER_BIT
                            |   MainScreen.COLLECTIBLE_BIT;

            //Create Fixture using set Fixture definition, and set user data
            enemy.b2body.createFixture(enemy.fdef).setUserData(enemy);

        }
    }

}
