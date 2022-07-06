package com.mygdx.game.entitycomponentsystem.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.mygdx.game.ai.SteeringPresets;
import com.mygdx.game.config.GameConfig;
import com.mygdx.game.entitycomponentsystem.components.B2dBodyComponent;
import com.mygdx.game.entitycomponentsystem.components.BulletComponent;
import com.mygdx.game.entitycomponentsystem.components.EnemyComponent;
import com.mygdx.game.entitycomponentsystem.components.Mapper;
import com.mygdx.game.entitycomponentsystem.components.SteeringComponent;
import com.mygdx.game.gameworld.GameWorld;
import com.mygdx.game.gameworld.GameWorldCreator;
import com.mygdx.game.utils.GdxUtils;

public class EnemySystem extends IteratingSystem{

	protected static final Logger logger = new Logger(EnemySystem.class.getSimpleName(), Logger.INFO);
	private ComponentMapper<EnemyComponent> em;
	private ComponentMapper<B2dBodyComponent> bodm;
	private GameWorldCreator gameWorldCreator;
	private GameWorld gameWorld;
	private PooledEngine pooledEngine;

	
	@SuppressWarnings("unchecked")
	public EnemySystem(GameWorldCreator gameWorldCreator, GameWorld gameWorld, PooledEngine pooledEngine){
		super(Family.all(EnemyComponent.class).get());
		this.em = ComponentMapper.getFor(EnemyComponent.class);
		this.bodm = ComponentMapper.getFor(B2dBodyComponent.class);
		this.gameWorldCreator = gameWorldCreator;
		this.gameWorld = gameWorld;
		this.pooledEngine = pooledEngine;
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		EnemyComponent enemyCom = em.get(entity);		// get EnemyComponent
		B2dBodyComponent bodyCom = bodm.get(entity);	// get B2dBodyComponent

		Array<Fixture> fixtureArray = bodyCom.body.getFixtureList();

		float halfRadius = fixtureArray.get(0).getShape().getRadius()*GameConfig.MULTIPLY_BY_PPM;
		if(enemyCom.enemyType == EnemyComponent.Type.BASIC_ENEMY){
			// get distance of enemy from its original start position (pad center)
			float currentXpos = bodyCom.body.getPosition().x * GameConfig.MULTIPLY_BY_PPM - halfRadius;
			float distFromOrig = Math.abs(enemyCom.xPosCenter - currentXpos);

			if(distFromOrig > enemyCom.noOfSteps)
			{
				if(enemyCom.velocity == EnemyComponent.LEFT_SPEED)
				{
					enemyCom.velocity = EnemyComponent.RIGHT_SPEED;
				}
				else if(enemyCom.velocity == EnemyComponent.RIGHT_SPEED)
				{
					enemyCom.velocity = EnemyComponent.LEFT_SPEED;
				}
				else
				{
					logger.error("Impossible combination");
				}
			}
			
			// apply speed to body
			bodyCom.body.setTransform(bodyCom.body.getPosition().x + enemyCom.velocity,
					bodyCom.body.getPosition().y,
					bodyCom.body.getAngle());	
		}else if(enemyCom.enemyType == EnemyComponent.Type.CLOUD){
			B2dBodyComponent b2Player = Mapper.b2dCom.get(this.gameWorld.getPlayer(0));
			B2dBodyComponent b2Enemy = Mapper.b2dCom.get(entity);
			
			float distance = b2Player.body.getPosition().dst(b2Enemy.body.getPosition());
			SteeringComponent scom = Mapper.sCom.get(entity);
			if(distance < 3 && scom.currentMode != SteeringComponent.SteeringState.FLEE){
				scom.steeringBehavior = SteeringPresets.getFlee(Mapper.sCom.get(entity),Mapper.sCom.get(this.gameWorld.getPlayer(0)));
				scom.currentMode = SteeringComponent.SteeringState.FLEE;
			}else if(distance > 3 && distance < 10 && scom.currentMode != SteeringComponent.SteeringState.ARRIVE){
				scom.steeringBehavior = SteeringPresets.getArrive(Mapper.sCom.get(entity),Mapper.sCom.get(this.gameWorld.getPlayer(0)));
				scom.currentMode = SteeringComponent.SteeringState.ARRIVE;
			}else if(distance > 15 && scom.currentMode != SteeringComponent.SteeringState.WANDER){
				scom.steeringBehavior  = SteeringPresets.getWander(Mapper.sCom.get(entity));
				scom.currentMode = SteeringComponent.SteeringState.WANDER;
			}
			
			// should enemy shoot
			if(scom.currentMode == SteeringComponent.SteeringState.ARRIVE){
				// enemy is following
				if(enemyCom.timeSinceLastShot >= enemyCom.shootDelay){
					//do shoot
					Vector2 aim = GdxUtils.aimTo(bodyCom.body.getPosition(), b2Player.body.getPosition());
					aim.scl(10);
					this.gameWorldCreator.createBullet(
							bodyCom.body.getPosition().x * GameConfig.MULTIPLY_BY_PPM,
							bodyCom.body.getPosition().y * GameConfig.MULTIPLY_BY_PPM,
							aim.x, 
							aim.y,
							BulletComponent.Owner.ENEMY,
							this.pooledEngine,
							this.gameWorld.getWorldSingleton().getWorld()
							);
					//reset timer
					enemyCom.timeSinceLastShot = 0;
				}
			}
		}
		
		// do shoot timer
		enemyCom.timeSinceLastShot += deltaTime;
		
		// check for dead enemies
		if(enemyCom.isDead){
			bodyCom.isDead =true;
		}
	}
}
