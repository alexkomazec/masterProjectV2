package com.akomazec.BaseSampleProject.Sprites;

import com.akomazec.BaseSampleProject.WorldSingleton;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import java.util.ArrayList;

public class Player {

        public int entityID;
        public int clientID;

        public Body b2body;
        public BodyDef bdef;
        public FixtureDef fdef;
        public PolygonShape shape;

        public Direction direction;
        public final FiredBy firedBy = FiredBy.BY_PLAYER;

        public int magicBallWidth;
        public int magicBallHeight;

        public Boolean shouldBeDestroyed;

        public ArrayList<MagicBall> magicBalls;
        WorldSingleton world;

        private Vector2 previousPosition;

        public Player()
        {
            this.entityID = 0;
            this.clientID = 0;
            this.bdef = new BodyDef();
            this.fdef = new FixtureDef();
            this.shape = new PolygonShape();
            this.direction = Direction.RIGHT;
            this.magicBallWidth = 32;
            this.magicBallHeight = 32;
            shouldBeDestroyed = false;
            magicBalls = new ArrayList<>();
            world = WorldSingleton.getInstance(new Vector2(0, -100), true);
            previousPosition = new Vector2(0,0);
        }

        public boolean hasMoved()
        {
            if(
                    (previousPosition.x != this.b2body.getPosition().x) ||
                            (previousPosition.y != this.b2body.getPosition().y ))
            {
                previousPosition.x = this.b2body.getPosition().x;
                previousPosition.y = this.b2body.getPosition().y;
                return true;
            }
            return false;
        }

        public void jump()
        {
            b2body.applyLinearImpulse(new Vector2(0, 100f), b2body.getWorldCenter(), true);
        }

        public void turnRight()
        {
            b2body.applyLinearImpulse(new Vector2(100f, 0),
                    b2body.getWorldCenter(),true);
            direction = Direction.RIGHT;
        }

        public void turnLeft()
        {
            b2body.applyLinearImpulse(new Vector2(-100f, 0),
                    b2body.getWorldCenter(),
                    true);
            direction = Direction.LEFT;
        }

        public void fireMagicBall()
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
                    direction,
                    firedBy);

            magicBalls.add(magicBall);
        }

        public void powerUp()
        {
            this.magicBallWidth *= 2;
            this.magicBallHeight *= 2;
        }

        public void updateMagicBalls()
        {
            for (int i = 0; i < magicBalls.size(); i++)
            {
                MagicBall magicBall = magicBalls.get(i);

                if(!magicBall.shouldBeDestroyed)
                {
                    magicBall.update();
                }
                else
                {
                    //Remove the magic ball
                    world.getWorld().destroyBody(magicBall.b2body);
                    magicBalls.remove(magicBall);

                    //Handle indexing
                    if(i + 1 ==  magicBalls.size())
                    {
                    /* Case:	Last element in the array has been deleted
                                Just go out of the loop
                    */
                    }
                    else
                    {
                    /* Case:	Some mid element has been deleted
                                Just go out of the loop
                    */
                        i--;
                    }

                }
            }
        }

}
