package com.akomazec.BaseSampleProject.Tools;

import com.akomazec.BaseSampleProject.BaseSampleProject;
import com.akomazec.BaseSampleProject.Screens.MainScreen;
import com.akomazec.BaseSampleProject.Sprites.Bricks.Brick;
import com.akomazec.BaseSampleProject.Sprites.Collects.Collectible;
import com.akomazec.BaseSampleProject.Sprites.Enemy;
import com.akomazec.BaseSampleProject.Sprites.FiredBy;
import com.akomazec.BaseSampleProject.Sprites.MagicBall;
import com.akomazec.BaseSampleProject.Sprites.Player;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;


public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (cDef){
            case MainScreen.MAGIC_BIT | MainScreen.GROUND_BIT:

                if(fixA.getFilterData().categoryBits == MainScreen.MAGIC_BIT)
                {
                    ((MagicBall)fixA.getUserData()).shouldBeDestroyed = true;
                    ((Brick)fixB.getUserData()).shouldBeDestroyed = true;

                }
                else
                {
                    ((MagicBall)fixB.getUserData()).shouldBeDestroyed = true;
                    ((Brick)fixA.getUserData()).shouldBeDestroyed = true;
                }
            break;

            case MainScreen.PLAYER_BIT | MainScreen.COLLECTIBLE_BIT:

                if(fixA.getFilterData().categoryBits == MainScreen.PLAYER_BIT)
                {
                    ((Player)fixA.getUserData()).powerUp();
                    ((Collectible)fixB.getUserData()).shouldBeDestroyed = true;

                }
                else
                {
                    ((Player)fixB.getUserData()).powerUp();
                    ((Collectible)fixA.getUserData()).shouldBeDestroyed = true;
                }
            break;

            case MainScreen.PLAYER_BIT | MainScreen.MAGIC_BIT:

                if(fixA.getFilterData().categoryBits == MainScreen.PLAYER_BIT)
                {
                    if( ( (MagicBall)fixB.getUserData()).firedBy == FiredBy.BY_ENEMY)
                    {
                        //Delete player
                        ((Player)fixA.getUserData()).shouldBeDestroyed = true;
                    }
                }
                else
                {
                    if( ( (MagicBall)fixA.getUserData()).firedBy == FiredBy.BY_ENEMY)
                    {
                        //Delete player
                        ((Player)fixB.getUserData()).shouldBeDestroyed = true;
                    }
                }
            break;

            case MainScreen.ENEMY_BIT | MainScreen.MAGIC_BIT:

                if(fixA.getFilterData().categoryBits == MainScreen.ENEMY_BIT)
                {
                    if( ( (MagicBall)fixB.getUserData()).firedBy == FiredBy.BY_PLAYER)
                    {
                        //Delete player
                        ((Enemy)fixA.getUserData()).shouldBeDestroyed = true;
                    }
                }
                else
                {
                    if( ( (MagicBall)fixA.getUserData()).firedBy == FiredBy.BY_PLAYER)
                    {
                        //Delete player
                        ((Enemy)fixB.getUserData()).shouldBeDestroyed = true;
                    }
                }
                break;

            case MainScreen.ENEMY_BIT | MainScreen.COLLECTIBLE_BIT:

                if(fixA.getFilterData().categoryBits == MainScreen.ENEMY_BIT)
                {
                    ((Enemy)fixA.getUserData()).powerUp();
                    ((Collectible)fixB.getUserData()).shouldBeDestroyed = true;

                }
                else
                {
                    ((Enemy)fixB.getUserData()).powerUp();
                    ((Collectible)fixA.getUserData()).shouldBeDestroyed = true;
                }
                break;
        }
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
