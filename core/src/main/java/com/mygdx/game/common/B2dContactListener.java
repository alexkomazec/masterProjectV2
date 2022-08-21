package com.mygdx.game.common;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Logger;
import com.mygdx.game.config.GameConfig;
import com.mygdx.game.entitycomponentsystem.components.CollisionComponent;


public class B2dContactListener implements ContactListener {

	protected static final Logger logger = new Logger(B2dContactListener.class.getSimpleName(), Logger.INFO);

	@Override
	public void beginContact(Contact contact) {
		Fixture fa = contact.getFixtureA();
		Fixture fb = contact.getFixtureB();

		int cDef = fa.getFilterData().categoryBits | fb.getFilterData().categoryBits;

		//if(cDef != GameConfig.PLAYER_ENEMY_COLLISION && cDef != GameConfig.PLAYER_CLOUD_COLLISION)
		//{
			if(fa.getBody().getUserData() instanceof Entity){
				Entity ent = (Entity) fa.getBody().getUserData();
				logger.debug("fa.getBody() = entity");
				entityCollision(ent,fb);
			}else if(fb.getBody().getUserData() instanceof Entity){
				Entity ent = (Entity) fb.getBody().getUserData();
				logger.debug("fb.getBody() = entity");
				entityCollision(ent,fa);
			}
		//}
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
