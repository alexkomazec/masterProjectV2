package com.mygdx.game.entitycomponentsystem.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Logger;
import com.mygdx.game.common.FixturePair;
import com.mygdx.game.entitycomponentsystem.components.CollisionComponent;
import com.mygdx.game.screens.GameScreen;

public class B2dContactSystem extends EntitySystem
{
    protected static final Logger logger = new Logger(B2dContactSystem.class.getSimpleName(), Logger.INFO);

    public B2dContactSystem() {}

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if(!GameScreen.bufferOfFixtures.isEmpty())
        {
            /*There are elements in the buffer*/
            beginContactSync(GameScreen.bufferOfFixtures.remove(0));
        }
    }

    public void beginContactSync(FixturePair fixturePair) {
        Fixture fa = fixturePair.fa;
        Fixture fb = fixturePair.fb;

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
