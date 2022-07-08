package com.mygdx.game.entitycomponentsystem.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Logger;
import com.mygdx.game.entitycomponentsystem.components.B2dBodyComponent;
import com.mygdx.game.entitycomponentsystem.components.BrickComponent;
import com.mygdx.game.entitycomponentsystem.components.BulletComponent;
import com.mygdx.game.entitycomponentsystem.components.CollectibleBasicArrayComponent;
import com.mygdx.game.entitycomponentsystem.components.CollectibleBasicComponent;
import com.mygdx.game.entitycomponentsystem.components.CollisionComponent;
import com.mygdx.game.entitycomponentsystem.components.EnemyComponent;
import com.mygdx.game.entitycomponentsystem.components.Mapper;
import com.mygdx.game.entitycomponentsystem.components.PlayerComponent;
import com.mygdx.game.entitycomponentsystem.components.StateComponent;
import com.mygdx.game.entitycomponentsystem.components.TypeComponent;

public class CollisionSystem extends IteratingSystem {

	protected static final Logger logger = new Logger(CollisionSystem.class.getSimpleName(), Logger.INFO);
	ComponentMapper<CollisionComponent> cm;
	ComponentMapper<PlayerComponent> pm;
	ComponentMapper<StateComponent> sm;

	@SuppressWarnings("unchecked")
	public CollisionSystem() {
		super(Family.all(CollisionComponent.class).get());
		
		 cm = ComponentMapper.getFor(CollisionComponent.class);
		 pm = ComponentMapper.getFor(PlayerComponent.class);
		 sm = ComponentMapper.getFor(StateComponent.class);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		// get collision for this entity
		CollisionComponent cc = cm.get(entity);
		//get collided entity
		Entity collidedEntity = cc.collisionEntity;
		
		TypeComponent thisType = entity.getComponent(TypeComponent.class);
		
		// Do Player Collisions
		if(thisType.type == TypeComponent.PLAYER){
			if(collidedEntity != null){
				TypeComponent type = collidedEntity.getComponent(TypeComponent.class);
				if(type != null){
					switch(type.type){
					case TypeComponent.ENEMY:
						logger.debug("player hit enemy");
						cc.isHit = true;
						break;
					case TypeComponent.SCENERY:
						pm.get(entity).onPlatform = true;
						logger.debug("player hit scenery");
						break;
					case TypeComponent.SPRING:
						pm.get(entity).onSpring = true;
						logger.debug("player hit spring: bounce up");
						break;
					case TypeComponent.OTHER:
						//do player hit other thing
						logger.debug("player hit other");
						break; 
					case TypeComponent.BULLET:
						BulletComponent bullet = Mapper.bulletCom.get(collidedEntity);
						if(bullet.owner != BulletComponent.Owner.PLAYER){ // can't shoot own team
							cc.isHit = true;
						}
						logger.debug("Player just shot. bullet in player atm");
						break;

					case TypeComponent.BASIC_COLLECTIBLE:
						CollectibleBasicArrayComponent cbac = entity.getComponent(CollectibleBasicArrayComponent.class);
						CollectibleBasicComponent cbc = collidedEntity.getComponent(CollectibleBasicComponent.class);
						B2dBodyComponent b2bcColided = collidedEntity.getComponent(B2dBodyComponent.class);
						if(cbac == null)
						{
							/* This is player's first time to collect some basic collectibles
							*  Prepare array that will store the existence of basic collectibles
							*/
							cbac = new CollectibleBasicArrayComponent();
							entity.add(cbac);
						}

						/* Store Basic Collectible to a player's array */
						int collectibleBasicType = cbc.type;
						cbac.collectibleBasicArray[collectibleBasicType] = true;

						/* Delete already picked up basic collectible*/
						b2bcColided.isDead = true;
						cbc.isDead = true;

						break;
					default:
						logger.error("No matching type found");
					}
					cc.collisionEntity = null; // collision handled reset component
				}else{
					logger.error("Player: collidedEntity.type == null");
				}
			}
		}else if(thisType.type == TypeComponent.ENEMY)
		{  	// Do enemy collisions
			if(collidedEntity != null){
				TypeComponent type = collidedEntity.getComponent(TypeComponent.class);
				CollisionComponent ccColided = collidedEntity.getComponent(CollisionComponent.class);
				if(type != null){
					switch(type.type){
					case TypeComponent.PLAYER:
						ccColided.isHit = true;
						logger.debug("enemy hit player");
						break;
					case TypeComponent.ENEMY:
						logger.debug("enemy hit enemy");
						break;
					case TypeComponent.SCENERY:
						logger.debug("enemy hit scenery");
						break;
					case TypeComponent.SPRING:
						logger.debug("enemy hit spring");
						break;	
					case TypeComponent.OTHER:
						logger.debug("enemy hit other");
						break; 
					case TypeComponent.BULLET:
						BulletComponent bullet = Mapper.bulletCom.get(collidedEntity);
						if(bullet.owner != BulletComponent.Owner.ENEMY){ // can't shoot own team
							bullet.isDead = true;
							cc.isHit = true;
							logger.debug("enemy got shot");
						}
						break;
					default:
						logger.error("No matching type found");
					}
					cc.collisionEntity = null; // collision handled reset component
				}else{
					logger.error("Enemy: collidedEntity.type == null");
				}
			}
		}
		else if(thisType.type == TypeComponent.BULLET)
		{
			if(collidedEntity != null) {
				TypeComponent type = collidedEntity.getComponent(TypeComponent.class);
				CollisionComponent ccColided = collidedEntity.getComponent(CollisionComponent.class);

				if(type != null)
				{
					BulletComponent bullet =  Mapper.bulletCom.get(entity);
					switch(type.type)
					{
						case TypeComponent.PLAYER:
							logger.debug("bullet hit player");
							if(bullet.owner != BulletComponent.Owner.PLAYER) { // It is not a friendly bullet
								bullet.isDead = true;
								ccColided.isHit = true;
								logger.debug("player hit by the enemy's hit");
							}
							break;
						case TypeComponent.ENEMY:
							logger.debug("bullet hit enemy");
							EnemyComponent enemy = Mapper.enemyCom.get(collidedEntity);
							if(bullet.owner != BulletComponent.Owner.ENEMY) { // It is not a friendly bullet
								bullet.isDead = true;

								if(enemy != null){
									ccColided.isHit = true;
								}
								else
								{
									logger.debug("enemy is null");
								}

								logger.debug("enemy got shot");
							}
							break;
						case TypeComponent.SCENERY:
							logger.debug("bullet hit scenery");
							B2dBodyComponent bc = Mapper.b2dCom.get(collidedEntity);
							BrickComponent brc	= Mapper.brickCom.get(collidedEntity);
							if(bullet.owner != BulletComponent.Owner.ENEMY)
							{
								bullet.isDead = true;
								bc.isDead = true;
								brc.isDead = true;
							}
							break;
						case TypeComponent.SPRING:
							logger.debug("bullet hit spring");
							break;
						case TypeComponent.OTHER:
							logger.debug("bullet hit other");
							break;
						default:
							logger.error("No matching type found");
					}
					cc.collisionEntity = null; // collision handled reset component
				}
				else
				{
					logger.error("Bullet: collidedEntity.type == null");
				}
			}
		}
		else{
			cc.collisionEntity = null;
		}
	}
}
