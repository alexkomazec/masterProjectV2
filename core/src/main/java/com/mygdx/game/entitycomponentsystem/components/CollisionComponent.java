package com.mygdx.game.entitycomponentsystem.components;

import static com.mygdx.game.config.GameConfig.MAX_SIZE;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.mygdx.game.config.GameConfig;

/*
 *  Stores collision data such as entity that this entity has collided with
 */
public class CollisionComponent implements Component, Poolable {
	public Entity collisionEntity;
	public boolean[] healthAction = new boolean[GameConfig.MAX_SIZE];

	@Override
	public void reset() {
		collisionEntity = null;
		healthAction  = new boolean[MAX_SIZE];
	}
}
