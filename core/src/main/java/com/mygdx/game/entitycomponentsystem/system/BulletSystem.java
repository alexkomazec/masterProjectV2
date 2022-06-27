package com.mygdx.game.entitycomponentsystem.system;

import static com.mygdx.game.entitycomponentsystem.components.BulletComponent.MAX_LIVING_TIME;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.mygdx.game.config.GameConfig;
import com.mygdx.game.entitycomponentsystem.components.B2dBodyComponent;
import com.mygdx.game.entitycomponentsystem.components.BulletComponent;
import com.mygdx.game.entitycomponentsystem.components.Mapper;
import com.mygdx.game.gameworld.GameWorld;

public class BulletSystem extends IteratingSystem{
	private GameWorld gameWorld;
	
	@SuppressWarnings("unchecked")
	public BulletSystem(GameWorld gameWorld){
		super(Family.all(BulletComponent.class).get());
		this.gameWorld = gameWorld;
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		//get box 2d body and bullet components
		B2dBodyComponent b2body = Mapper.b2dCom.get(entity);
		BulletComponent bullet = Mapper.bulletCom.get(entity);
		bullet.livingTime +=deltaTime;

		float xVel = bullet.xVel * GameConfig.MULTIPLY_BY_PPM;
		float yVel = bullet.yVel * GameConfig.MULTIPLY_BY_PPM;

		// apply bullet velocity to bullet body
		b2body.body.setLinearVelocity(xVel, yVel);

		if(bullet.livingTime >= MAX_LIVING_TIME)
		{
			bullet.isDead = true;
		}
		
		//check if bullet is dead
		if(bullet.isDead){
			System.out.println("Bullet died");
			b2body.isDead = true;
		}
	}
}
