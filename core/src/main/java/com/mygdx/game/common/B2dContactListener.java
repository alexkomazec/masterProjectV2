package com.mygdx.game.common;

import static com.mygdx.game.config.GameConfig.PLAYER_BIT;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Logger;
import com.mygdx.game.config.GameConfig;
import com.mygdx.game.entitycomponentsystem.components.CollisionComponent;
import com.mygdx.game.screens.GameScreen;


public class B2dContactListener implements ContactListener {

	protected static final Logger logger = new Logger(B2dContactListener.class.getSimpleName(), Logger.INFO);

	@Override
	public void beginContact(Contact contact)
	{
		Fixture fa = contact.getFixtureA();
		Fixture fb = contact.getFixtureB();

		GameScreen.bufferOfFixtures.add(new FixturePair(fa, fb));
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
