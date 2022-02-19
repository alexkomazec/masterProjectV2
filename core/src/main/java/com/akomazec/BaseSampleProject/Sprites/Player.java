package com.akomazec.BaseSampleProject.Sprites;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class Player {

    public int entityID;

    public Body b2body;
    public BodyDef bdef;
    public FixtureDef fdef;
    public PolygonShape shape;

    public Direction direction;

    public int magicBallWidth;
    public int magicBallHeight;

    public Player()
    {
        this.entityID = 0;
        this.bdef = new BodyDef();
        this.fdef = new FixtureDef();
        this.shape = new PolygonShape();
        this.direction = Direction.RIGHT;
        this.magicBallWidth = 32;
        this.magicBallHeight = 32;
    }

    public void jump()
    {
        b2body.applyLinearImpulse(new Vector2(0, 100f), b2body.getWorldCenter(), true);
    }

    public MagicBall fireMagicBall()
    {
        MagicBall magicBall = new MagicBall();

        int width = this.magicBallWidth;
        int height = this.magicBallHeight;
        float magicBallX;
        float magicBallY;

        magicBallY = this.b2body.getPosition().y + height/2 ;

        if(this.direction == Direction.LEFT)
        {
            magicBallX = this.b2body.getPosition().x - width/2;
        }
        else
        {
            magicBallX = this.b2body.getPosition().x + width/2;
        }

        Direction direction = this.direction;

         magicBall.throwMe(
                magicBallX,
                magicBallY,
                width,
                height,
                direction);

         return magicBall;
    }

    public void powerUp()
    {
        this.magicBallWidth *= 2;
        this.magicBallHeight *= 2;
    }

}
