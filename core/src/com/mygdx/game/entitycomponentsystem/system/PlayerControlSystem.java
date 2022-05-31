package com.mygdx.game.entitycomponentsystem.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.common.Direction;
import com.mygdx.game.common.ViewPortConfiguration;
import com.mygdx.game.config.GameConfig;
import com.mygdx.game.entitycomponentsystem.components.B2dBodyComponent;
import com.mygdx.game.entitycomponentsystem.components.BulletComponent;
import com.mygdx.game.entitycomponentsystem.components.PlayerComponent;
import com.mygdx.game.entitycomponentsystem.components.StateComponent;
import com.mygdx.game.gameworld.GameWorldCreator;


public class PlayerControlSystem extends IteratingSystem{

	ComponentMapper<PlayerComponent> pm;
	ComponentMapper<B2dBodyComponent> bodm;
	ComponentMapper<StateComponent> sm;
	GameWorldCreator gameWorldCreator;
	PooledEngine pooledEngine;
	World world;
	boolean alreadyFired;
	
	
	@SuppressWarnings("unchecked")
	public PlayerControlSystem(GameWorldCreator gameWorldCreator,
							   PooledEngine pooledEngine,
							   World world) {
		super(Family.all(PlayerComponent.class).get());
		this.gameWorldCreator = gameWorldCreator;
		this.pooledEngine = pooledEngine;
		this.world = world;

		pm = ComponentMapper.getFor(PlayerComponent.class);
		bodm = ComponentMapper.getFor(B2dBodyComponent.class);
		sm = ComponentMapper.getFor(StateComponent.class);
	}
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		B2dBodyComponent b2dbodyComponent = bodm.get(entity);
		StateComponent stateComponent = sm.get(entity);
		PlayerComponent playerComponent = pm.get(entity);

		playerComponent.cam.position.x = b2dbodyComponent.body.getPosition().x * GameConfig.MULTIPLY_BY_PPM;
		playerComponent.cam.position.y = b2dbodyComponent.body.getPosition().y * GameConfig.MULTIPLY_BY_PPM;

		ViewPortConfiguration.calculateViewport(10, 10);

		if (b2dbodyComponent.body.getLinearVelocity().y > 0 && stateComponent.get() != StateComponent.STATE_FALLING) {
			stateComponent.set(StateComponent.STATE_FALLING);
			System.out.println("setting to Falling");
		}

		if (b2dbodyComponent.body.getLinearVelocity().y == 0) {
			if (stateComponent.get() == StateComponent.STATE_FALLING) {
				stateComponent.set(StateComponent.STATE_NORMAL);
				System.out.println("setting to normal");
			}
			if (b2dbodyComponent.body.getLinearVelocity().x != 0 && stateComponent.get() != StateComponent.STATE_MOVING) {
				stateComponent.set(StateComponent.STATE_MOVING);
				System.out.println("setting to moving");
			}
		}

		if (isInputCommandTrue(GameConfig.LEFT, playerComponent))
		{
			b2dbodyComponent.body.setLinearVelocity(MathUtils.lerp(b2dbodyComponent.body.getLinearVelocity().x, -7f, 0.2f), b2dbodyComponent.body.getLinearVelocity().y);
			playerComponent.direction = Direction.LEFT;
		}

		if (isInputCommandTrue(GameConfig.RIGHT, playerComponent))
		{
			b2dbodyComponent.body.setLinearVelocity(MathUtils.lerp(b2dbodyComponent.body.getLinearVelocity().x, 7f, 0.2f), b2dbodyComponent.body.getLinearVelocity().y);
			playerComponent.direction = Direction.RIGHT;
		}

		/*
		Clarification: If left, and right are not in state "pressed" linear velocity to x axis
		should be 0. Wanted to avoid "sliding on ice" effect
		*/
		if (!isInputCommandTrue(GameConfig.LEFT, playerComponent) && !isInputCommandTrue(GameConfig.RIGHT, playerComponent))
		{
			b2dbodyComponent.body.setLinearVelocity(MathUtils.lerp(b2dbodyComponent.body.getLinearVelocity().x, 0, 0.1f), b2dbodyComponent.body.getLinearVelocity().y);
		}

		if (isInputCommandTrue(GameConfig.UP, playerComponent) &&
				(stateComponent.get() == StateComponent.STATE_NORMAL || stateComponent.get() == StateComponent.STATE_MOVING)) {
			b2dbodyComponent.body.applyLinearImpulse(0, 10f * b2dbodyComponent.body.getMass(), b2dbodyComponent.body.getWorldCenter().x, b2dbodyComponent.body.getWorldCenter().y, true);
			stateComponent.set(StateComponent.STATE_JUMPING);
			System.out.println("setting to jumping");
			playerComponent.onPlatform = false;
		}

		if (isInputCommandTrue(GameConfig.DOWN, playerComponent))
		{
			b2dbodyComponent.body.applyLinearImpulse(0, -5f, b2dbodyComponent.body.getWorldCenter().x, b2dbodyComponent.body.getWorldCenter().y, true);
		}

		if (isInputCommandTrue(GameConfig.SPACE, playerComponent) && (!alreadyFired))
		{
			float startBulletPositionX;
			float startBulletPositionY;
			float xVel;

			if(playerComponent.direction == Direction.LEFT)
			{
				startBulletPositionX = (b2dbodyComponent.body.getPosition().x* GameConfig.MULTIPLY_BY_PPM) - 16f*3;
				xVel = -7;
			}
			else
			{
				startBulletPositionX = (b2dbodyComponent.body.getPosition().x* GameConfig.MULTIPLY_BY_PPM) + 16f;
				xVel = 7;
			}

			startBulletPositionY = b2dbodyComponent.body.getPosition().y * GameConfig.MULTIPLY_BY_PPM;

			this.gameWorldCreator.createBullet(startBulletPositionX, startBulletPositionY,
											xVel,0,
											BulletComponent.Owner.PLAYER, this.pooledEngine,
											world);
			alreadyFired = true;
		}

		/* Space has been unpressed, and magic has been already fired, need reset*/
		if(!isInputCommandTrue(GameConfig.SPACE, playerComponent) && alreadyFired)
		{
			alreadyFired = false;
		}
	}

	private boolean isInputCommandTrue(int inputCommandID, PlayerComponent playerComponent)
	{
		return playerComponent.abInputCommandList[inputCommandID];
	}
}
