package com.mygdx.game.entitycomponentsystem.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;

/*
 *  Stores collision data such as entity that this entity has collided with
 */
public class CollisionComponent implements Component, Poolable {
	public Entity collisionEntity;
	public boolean isHit;

	@Override
	public void reset() {
		collisionEntity = null;
		isHit = false;
	}
}
