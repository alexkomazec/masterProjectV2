package com.mygdx.game.entitycomponentsystem.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Logger;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.client.ClientHandler;
import com.mygdx.game.common.FixturePair;
import com.mygdx.game.entitycomponentsystem.components.B2dBodyComponent;
import com.mygdx.game.entitycomponentsystem.components.CollisionComponent;
import com.mygdx.game.screens.GameScreen;

public class B2dContactSystem extends EntitySystem
{
    protected static final Logger logger = new Logger(B2dContactSystem.class.getSimpleName(), Logger.INFO);
    private ClientHandler clientHandler;

    public B2dContactSystem(ClientHandler clientHandler)
    {
        this.clientHandler = clientHandler;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if(!GameScreen.bufferOfFixtures.isEmpty())
        {
            /*There are elements in the buffer*/
            beginContactSync(GameScreen.bufferOfFixtures.remove(0));
        }
    }

    public void beginContactSync(FixturePair fixturePair)
    {
        Fixture fa = fixturePair.fa;
        Fixture fb = fixturePair.fb;

        if(fa != null)
        {
            if(fb != null)
            {
                if(clientHandler!= null)
                {
                    /* Check if this client is host or not*/
                    if(clientHandler.getGame().getClientIDInGame() == 0)
                    {
                        /* Client is host, so host needs to emit new contacts */
                        if(fa.getBody().getUserData() instanceof Entity
                                && fb.getBody().getUserData() instanceof Entity)
                        {
                            B2dBodyComponent b2dBodyComponent1 = ((Entity) (fa.getBody().getUserData())).getComponent(B2dBodyComponent.class);
                            B2dBodyComponent b2dBodyComponent2 = ((Entity) (fb.getBody().getUserData())).getComponent(B2dBodyComponent.class);

                            if(b2dBodyComponent1 != null && b2dBodyComponent2 != null)
                            {
                                int bodyID1         = b2dBodyComponent1.bodyID;
                                String bodyName1    = b2dBodyComponent1.bodyName;
                                int bodyID2         = b2dBodyComponent2.bodyID;
                                String bodyName2    = b2dBodyComponent2.bodyName;

                                this.clientHandler.emitCollisionEvent(bodyID1, bodyName1, bodyID2, bodyName2);
                            }
                        }
                    }
                }

                if(fa.getBody().getUserData() instanceof Entity){
                    Entity ent = (Entity) fa.getBody().getUserData();
                    logger.debug("fa.getBody() = entity");
                    entityCollision(ent,fb);
                }else if(fb.getBody().getUserData() instanceof Entity){
                    Entity ent = (Entity) fb.getBody().getUserData();
                    logger.debug("fb.getBody() = entity");
                    entityCollision(ent,fa);
                }
            }
            else
            {
                logger.error("fb is null");
            }
        }
        else
        {
            logger.error("fa is null");
        }

    }

    private void entityCollision(Entity ent, Fixture fb) {
        if(fb.getBody().getUserData() instanceof Entity){
            Entity colEnt = (Entity) fb.getBody().getUserData();

            CollisionComponent col = ent.getComponent(CollisionComponent.class);
            CollisionComponent colb = colEnt.getComponent(CollisionComponent.class);

            if(col != null){
                col.collisionEntity = colEnt;
            }else if(colb != null){
                colb.collisionEntity = ent;
            }
        }
    }
}
