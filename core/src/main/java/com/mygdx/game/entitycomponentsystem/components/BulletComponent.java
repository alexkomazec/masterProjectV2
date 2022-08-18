package com.mygdx.game.entitycomponentsystem.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.mygdx.game.common.Direction;

public class BulletComponent implements Component, Poolable{
	public enum Owner { ENEMY,PLAYER,SCENERY,NONE }

	/* Maximum bullet living time, specified in seconds */
	public static final float MAX_LIVING_TIME = 5f;

	public float livingTime = 0;
	public float xVel = 0;
	public float yVel = 0;
	public boolean isDead = false;
	public Owner owner = Owner.NONE;
	public B2dBodyComponent ownerReference;
	public Direction direction = Direction.LEFT;
	
	@Override
	public void reset() {
		owner = Owner.NONE;
		xVel = 0;
		yVel = 0;
		isDead = false;
		livingTime = 0;
		ownerReference = null;
	}
}
