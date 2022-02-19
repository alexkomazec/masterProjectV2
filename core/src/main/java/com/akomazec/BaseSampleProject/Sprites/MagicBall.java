package com.akomazec.BaseSampleProject.Sprites;

import com.akomazec.BaseSampleProject.Tools.B2WorldCreator;
import com.akomazec.BaseSampleProject.WorldSingleton;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class MagicBall {

    public int entityID;

    public Body b2body;
    public BodyDef bdef;
    public FixtureDef fdef;
    public PolygonShape shape;

    public Direction direction;

    public float x;
    public float y;
    public float width;
    public float height;

    public boolean shouldBeDestroyed;

    public MagicBall()
    {

    }

    public void throwMe(float x, float y, int width, int height, Direction direction)
    {
        this.entityID = 1;
        this.bdef = new BodyDef();
        this.fdef = new FixtureDef();
        this.shape = new PolygonShape();
        this.direction = direction;

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.shouldBeDestroyed = false;

        B2WorldCreator b2WorldCreator = B2WorldCreator.getInstance(null,null);
        b2WorldCreator.createEntity(this);

    }

    public void update()
    {
        if(direction == Direction.LEFT)
        {
            this.b2body.applyLinearImpulse(new Vector2(-100f, 0),
                    this.b2body.getWorldCenter(),
                    true);
        }
        else
        {
            this.b2body.applyLinearImpulse(new Vector2(+100f, 0),
                    this.b2body.getWorldCenter(),
                    true);
        }

    }

    public void destroyBody()
    {
        WorldSingleton worldSingleton = WorldSingleton.getInstance(null,false);
        worldSingleton.getWorld().destroyBody(this.b2body);
    }

}
