package com.mygdx.game.entitycomponentsystem.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.KeyboardController;
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
	KeyboardController keyboardController;
	GameWorldCreator gameWorldCreator;
	PooledEngine pooledEngine;
	World world;
	
	
	@SuppressWarnings("unchecked")
	public PlayerControlSystem(KeyboardController keyboardController,
							   GameWorldCreator gameWorldCreator,
							   PooledEngine pooledEngine,
							   World world) {
		super(Family.all(PlayerComponent.class).get());
		this.keyboardController = keyboardController;
		this.gameWorldCreator = gameWorldCreator;
		this.pooledEngine = pooledEngine;
		this.world = world;

		pm = ComponentMapper.getFor(PlayerComponent.class);
		bodm = ComponentMapper.getFor(B2dBodyComponent.class);
		sm = ComponentMapper.getFor(StateComponent.class);
	}
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		B2dBodyComponent b2body = bodm.get(entity);
		StateComponent state = sm.get(entity);
		PlayerComponent player = pm.get(entity);

		player.cam.position.x = b2body.body.getPosition().x * GameConfig.MULTIPLY_BY_PPM;
		player.cam.position.y = b2body.body.getPosition().y * GameConfig.MULTIPLY_BY_PPM;

		ViewPortConfiguration.calculateViewport(10, 10);

		if (b2body.body.getLinearVelocity().y > 0 && state.get() != StateComponent.STATE_FALLING) {
			state.set(StateComponent.STATE_FALLING);
			System.out.println("setting to Falling");
		}

		if (b2body.body.getLinearVelocity().y == 0) {
			if (state.get() == StateComponent.STATE_FALLING) {
				state.set(StateComponent.STATE_NORMAL);
				System.out.println("setting to normal");
			}
			if (b2body.body.getLinearVelocity().x != 0 && state.get() != StateComponent.STATE_MOVING) {
				state.set(StateComponent.STATE_MOVING);
				System.out.println("setting to moving");
			}
		}

		if (this.keyboardController.left)
		{
			b2body.body.setLinearVelocity(MathUtils.lerp(b2body.body.getLinearVelocity().x, -7f, 0.2f), b2body.body.getLinearVelocity().y);
			player.direction = Direction.LEFT;
		}

		if (this.keyboardController.right)
		{
			b2body.body.setLinearVelocity(MathUtils.lerp(b2body.body.getLinearVelocity().x, 7f, 0.2f), b2body.body.getLinearVelocity().y);
			player.direction = Direction.RIGHT;
		}

		if (!this.keyboardController.left && !keyboardController.right)
		{
			b2body.body.setLinearVelocity(MathUtils.lerp(b2body.body.getLinearVelocity().x, 0, 0.1f), b2body.body.getLinearVelocity().y);

		}

		if (this.keyboardController.up &&
				(state.get() == StateComponent.STATE_NORMAL || state.get() == StateComponent.STATE_MOVING)) {
			b2body.body.applyLinearImpulse(0, 10f * b2body.body.getMass(), b2body.body.getWorldCenter().x, b2body.body.getWorldCenter().y, true);
			state.set(StateComponent.STATE_JUMPING);
			System.out.println("setting to jumping");
			player.onPlatform = false;
		}

		if (this.keyboardController.down)
		{
			b2body.body.applyLinearImpulse(0, -5f, b2body.body.getWorldCenter().x, b2body.body.getWorldCenter().y, true);
		}

		if (this.keyboardController.space && (!this.keyboardController.isMagicFired))
		{
			float startBulletPositionX;
			float startBulletPositionY;
			float xVel;

			if(player.direction == Direction.LEFT)
			{
				startBulletPositionX = (b2body.body.getPosition().x* GameConfig.MULTIPLY_BY_PPM) - 16f*3;
				xVel = -7;
			}
			else
			{
				startBulletPositionX = (b2body.body.getPosition().x* GameConfig.MULTIPLY_BY_PPM) + 16f;
				xVel = 7;
			}

			startBulletPositionY = b2body.body.getPosition().y * GameConfig.MULTIPLY_BY_PPM;

			this.gameWorldCreator.createBullet(startBulletPositionX, startBulletPositionY,
											xVel,0,
											BulletComponent.Owner.PLAYER, this.pooledEngine,
											world);
			this.keyboardController.isMagicFired = true;
		}

	}
}
