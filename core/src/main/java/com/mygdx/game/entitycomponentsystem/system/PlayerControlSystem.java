package com.mygdx.game.entitycomponentsystem.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Logger;
import com.mygdx.game.client.ClientHandler;
import com.mygdx.game.client.Message;
import com.mygdx.game.client.data.PlayerDataContainer;
import com.mygdx.game.common.Direction;
import com.mygdx.game.common.ViewPortConfiguration;
import com.mygdx.game.config.GameConfig;
import com.mygdx.game.entitycomponentsystem.components.B2dBodyComponent;
import com.mygdx.game.entitycomponentsystem.components.BulletComponent;
import com.mygdx.game.entitycomponentsystem.components.ControlledInputComponent;
import com.mygdx.game.entitycomponentsystem.components.CoolDownComponent;
import com.mygdx.game.entitycomponentsystem.components.DirectionComponent;
import com.mygdx.game.entitycomponentsystem.components.LocalInputComponent;
import com.mygdx.game.entitycomponentsystem.components.PlayerComponent;
import com.mygdx.game.entitycomponentsystem.components.StateComponent;
import com.mygdx.game.gameworld.GameWorldCreator;
import com.mygdx.game.utils.GdxUtils;

import org.json.JSONArray;


public class PlayerControlSystem extends IteratingSystem{

	private static final Logger logger = new Logger(PlayerControlSystem.class.getSimpleName(), Logger.INFO);
	ComponentMapper<PlayerComponent> pm;
	ComponentMapper<B2dBodyComponent> bodm;
	ComponentMapper<StateComponent> sm;
	ComponentMapper<ControlledInputComponent> cp;

	GameWorldCreator gameWorldCreator;
	PooledEngine pooledEngine;
	World world;
	Sound shootSound;
	ClientHandler clientHandler;

	@SuppressWarnings("unchecked")
	public PlayerControlSystem(GameWorldCreator gameWorldCreator,
							   PooledEngine pooledEngine,
							   World world,
							   Sound sound)
	{
		super(Family.all(PlayerComponent.class).get());
		this.gameWorldCreator = gameWorldCreator;
		this.pooledEngine = pooledEngine;
		this.world = world;

		pm = ComponentMapper.getFor(PlayerComponent.class);
		bodm = ComponentMapper.getFor(B2dBodyComponent.class);
		sm = ComponentMapper.getFor(StateComponent.class);
		cp = ComponentMapper.getFor(ControlledInputComponent.class);
		this.shootSound = sound;
	}

	public PlayerControlSystem(GameWorldCreator gameWorldCreator,
							   PooledEngine pooledEngine,
							   World world,
							   Sound sound,
							   ClientHandler clientHandler)
	{
		super(Family.all(PlayerComponent.class).get());
		this.gameWorldCreator = gameWorldCreator;
		this.pooledEngine = pooledEngine;
		this.world = world;

		pm = ComponentMapper.getFor(PlayerComponent.class);
		bodm = ComponentMapper.getFor(B2dBodyComponent.class);
		sm = ComponentMapper.getFor(StateComponent.class);
		cp = ComponentMapper.getFor(ControlledInputComponent.class);
		this.shootSound = sound;
		this.clientHandler = clientHandler;
	}
	@Override
	protected void processEntity(Entity entity, float deltaTime) {

		B2dBodyComponent b2dbodyComponent = bodm.get(entity);
		StateComponent stateComponent = sm.get(entity);
		PlayerComponent playerComponent = pm.get(entity);
		DirectionComponent directionComponent = entity.getComponent(DirectionComponent.class);
		ControlledInputComponent cntrlComponent = cp.get(entity);
		LocalInputComponent localInputComponent = null;
		CoolDownComponent coolDownComponent = entity.getComponent(CoolDownComponent.class);
		coolDownComponent.coolDown.elapsedTimeInSeconds += deltaTime;
		boolean isMoving = false;

		localInputComponent = entity.getComponent(LocalInputComponent.class);

		/* Move Camera only for the player that has localInputComponent (aka local player) */
		if(localInputComponent!= null)
		{
			playerComponent.cam.position.x = b2dbodyComponent.body.getPosition().x * GameConfig.MULTIPLY_BY_PPM;
			playerComponent.cam.position.y = b2dbodyComponent.body.getPosition().y * GameConfig.MULTIPLY_BY_PPM;
		}

		ViewPortConfiguration.calculateViewport(10, 10);

		if (!playerComponent.onPlatform) {
			if(stateComponent.get() != StateComponent.STATE_FALLING)
			{
				stateComponent.set(StateComponent.STATE_FALLING);
				logger.debug("setting to Falling");
			}
		}

		if (b2dbodyComponent.body.getLinearVelocity().y == 0) {
			if (stateComponent.get() == StateComponent.STATE_FALLING || b2dbodyComponent.body.getLinearVelocity().x == 0) {
				if(stateComponent.get() != StateComponent.STATE_NORMAL)
				{
					stateComponent.set(StateComponent.STATE_NORMAL);
					logger.debug("setting to Normal");
				}
			}
			if (b2dbodyComponent.body.getLinearVelocity().x != 0 && stateComponent.get() != StateComponent.STATE_MOVING) {
				stateComponent.set(StateComponent.STATE_MOVING);
				logger.debug("setting to Moving");
			}
		}

		if (GdxUtils.isInputCommandTrue(GameConfig.LEFT, cntrlComponent))
		{
			logger.debug("GdxUtils.isInputCommandTrue(GameConfig.LEFT, cntrlComponent)");
 			b2dbodyComponent.body.setLinearVelocity(MathUtils.lerp(b2dbodyComponent.body.getLinearVelocity().x, -7f, 0.2f), b2dbodyComponent.body.getLinearVelocity().y);
			directionComponent.direction = Direction.LEFT;
			isMoving = true;
		}

		if (GdxUtils.isInputCommandTrue(GameConfig.RIGHT, cntrlComponent))
		{
			//logger.debug("GdxUtils.isInputCommandTrue(GameConfig.RIGHT, cntrlComponent)");
			b2dbodyComponent.body.setLinearVelocity(MathUtils.lerp(b2dbodyComponent.body.getLinearVelocity().x, 7f, 0.2f), b2dbodyComponent.body.getLinearVelocity().y);
			directionComponent.direction = Direction.RIGHT;
			isMoving = true;
		}

		/*
		Clarification: If left, and right are not in state "pressed" linear velocity to x axis
		should be 0. Wanted to avoid "sliding on ice" effect
		*/
		if (!GdxUtils.isInputCommandTrue(GameConfig.LEFT, cntrlComponent) && !GdxUtils.isInputCommandTrue(GameConfig.RIGHT, cntrlComponent))
		{
			b2dbodyComponent.body.setLinearVelocity(MathUtils.lerp(b2dbodyComponent.body.getLinearVelocity().x, 0, 0.1f), b2dbodyComponent.body.getLinearVelocity().y);
		}

		if (GdxUtils.isInputCommandTrue(GameConfig.UP, cntrlComponent) &&
				(stateComponent.get() == StateComponent.STATE_NORMAL || stateComponent.get() == StateComponent.STATE_MOVING)) {
			b2dbodyComponent.body.applyLinearImpulse(0, 10f * b2dbodyComponent.body.getMass(), b2dbodyComponent.body.getWorldCenter().x, b2dbodyComponent.body.getWorldCenter().y, true);
			stateComponent.set(StateComponent.STATE_JUMPING);
			logger.debug("setting to Jumping");
			playerComponent.onPlatform = false;
		}

		if(playerComponent.typeOfPlayer == PlayerComponent.PlayerConnectivity.ONLINE
				&& playerComponent.needTofire)
		{
			float startBulletPositionX = 0;
			float startBulletPositionY = 0;
			float xVel = 0;
			Direction direction = null;


			logger.info("playerComponent.typeOfPlayer is ONLINE");
			playerComponent.needTofire = false;

			direction = playerComponent.bulletDirectionOnShoot;
			startBulletPositionX = playerComponent.bulletPosition.x;
			startBulletPositionY = playerComponent.bulletPosition.y;
			xVel = playerComponent.bulletXvel;

			coolDownComponent.coolDown.elapsedTimeInSeconds = 0;
			playerComponent.alreadyFired = true;
			playerComponent.fired = true;

			this.shootSound.play();
			this.gameWorldCreator.createBullet(startBulletPositionX, startBulletPositionY,
					xVel, 0,
					direction,
					null,
					BulletComponent.Owner.PLAYER,
					b2dbodyComponent,
					this.pooledEngine,
					world);

			logger.info("======= SHOOTING BULLET ============");
			logger.info("playerComponent.playerID: " + playerComponent.playerID);
			logger.info("playerComponent.typeOfPlayer: " + playerComponent.typeOfPlayer);
			logger.info("startBulletPositionX: " + startBulletPositionX);
			logger.info("startBulletPositionY: " + startBulletPositionY);
			logger.info("xVel: " + xVel);
			logger.info("direction: " + direction);
			logger.info("======= END SHOOTING BULLET ============");
		}

		boolean condition = GdxUtils.isInputCommandTrue(GameConfig.SPACE, cntrlComponent) && !playerComponent.alreadyFired;
		if (condition)
		{
			if(playerComponent.typeOfPlayer == PlayerComponent.PlayerConnectivity.LOCAL)
			{
				if(coolDownComponent.coolDown.elapsedTimeInSeconds >= coolDownComponent.coolDown.cooldown)
				{
					logger.info("playerComponent.typeOfPlayer: " +  playerComponent.typeOfPlayer);
					logger.info("directionComponent.direction:" + directionComponent.direction);

					handleLocalFiring(directionComponent,b2dbodyComponent,playerComponent, coolDownComponent, cntrlComponent);
				}
			}
			else
			{
				logger.error("Error, wrong player connectivity ");
			}
		}

		/* Space has been unpressed, and magic has been already fired, need reset*/
		if(!GdxUtils.isInputCommandTrue(GameConfig.SPACE, cntrlComponent) && playerComponent.alreadyFired)
		{
			playerComponent.alreadyFired = false;
		}
	}

	public void handleLocalFiring(DirectionComponent directionComponent,
								  B2dBodyComponent b2dbodyComponent,
								  PlayerComponent playerComponent,
								  CoolDownComponent coolDownComponent,
								  ControlledInputComponent cntrlInComp)
	{
		logger.info("playerComponent.typeOfPlayer is LOCAL");
		Direction direction;
		float startBulletPositionX;
		float xVel;

		coolDownComponent.coolDown.elapsedTimeInSeconds = 0;

		if (directionComponent.direction == Direction.LEFT) {
			direction = Direction.LEFT;
			startBulletPositionX = (b2dbodyComponent.body.getPosition().x * GameConfig.MULTIPLY_BY_PPM) - 16f * 3;
			xVel = -7;
		} else {
			direction = Direction.RIGHT;
			startBulletPositionX = (b2dbodyComponent.body.getPosition().x * GameConfig.MULTIPLY_BY_PPM) + 16f;
			xVel = 7;
		}

		float startBulletPositionY = b2dbodyComponent.body.getPosition().y * GameConfig.MULTIPLY_BY_PPM;

		/* Store temp data for the bullet */
		playerComponent.bulletDirectionOnShoot = directionComponent.direction;
		playerComponent.bulletXvel = xVel;
		playerComponent.bulletPosition = new Vector2(startBulletPositionX, startBulletPositionY);

		this.shootSound.play();
		this.gameWorldCreator.createBullet(startBulletPositionX, startBulletPositionY,
				xVel, 0,
				direction,
				null,
				BulletComponent.Owner.PLAYER,
				b2dbodyComponent,
				this.pooledEngine,
				world);

		playerComponent.alreadyFired = true;
		playerComponent.fired = true;
		playerComponent.readyToTransmitBullet = true;

		logger.info("======= SHOOTING BULLET ============");
		logger.info("playerComponent.playerID: " + playerComponent.playerID);
		logger.info("playerComponent.typeOfPlayer: " + playerComponent.typeOfPlayer);
		logger.info("startBulletPositionX: " + startBulletPositionX);
		logger.info("startBulletPositionY: " + startBulletPositionY);
		logger.info("xVel: " + xVel);
		logger.info("direction: " + direction);
		logger.info("======= END SHOOTING BULLET ============");


		if(this.clientHandler != null)
		{
			if(cntrlInComp.abInputCommandList[GameConfig.SPACE])
			{
				boolean[] inputCommandList = new boolean[GameConfig.LIST_COMMANDS_MAX];

				if (playerComponent.readyToTransmitBullet)
				{
					playerComponent.readyToTransmitBullet = false;
					inputCommandList[GameConfig.SPACE] = cntrlInComp.abInputCommandList[GameConfig.SPACE];

					logger.debug("magicFired: Player with ID " + playerComponent.playerID + " fire Magic");
					logger.debug(
							"playerComponent.bulletPosition: " + playerComponent.bulletPosition +
									"playerComponent.bulletXvel: " + playerComponent.bulletXvel +
									"playerComponent.bulletDirectionOnShoot: " + playerComponent.bulletDirectionOnShoot);

					/* Fill message */
					PlayerDataContainer playerDataContainerTmp = new PlayerDataContainer();
					playerDataContainerTmp.setPlayerID(playerComponent.playerID);
					playerDataContainerTmp.setAbInputCommandList(inputCommandList);
					playerDataContainerTmp.setPosition(new Vector2(playerComponent.bulletPosition));
					playerDataContainerTmp.setBulletXvelocity(playerComponent.bulletXvel);
					playerDataContainerTmp.setBulletDirection(playerComponent.bulletDirectionOnShoot);

					Message message = new Message(ClientHandler.PLAYER_FIRED_SEND, true);
					message.addPlayerDataContainer(playerDataContainerTmp);
					this.clientHandler.getTransmitingMessageArray().add(message);
				}
			}
		}
	}

}
