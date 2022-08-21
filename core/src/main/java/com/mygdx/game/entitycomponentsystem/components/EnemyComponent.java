package com.mygdx.game.entitycomponentsystem.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.mygdx.game.common.Direction;

public class EnemyComponent implements Component, Poolable{
	
	public enum Type {BASIC_ENEMY, CLOUD };
	public static float RIGHT_SPEED = 0.01f;
	public static float LEFT_SPEED = -0.01f;
	public boolean playerTouchedViewArea;

	public boolean isDead = false;
	public float xPosCenter = -1;
	public float shootDelay = 2f;
	public float timeSinceLastShot = 0f;
	public Type enemyType = Type.BASIC_ENEMY;

	public float velocity = LEFT_SPEED;
	public int noOfSteps = 0;
	public Entity target;

	@Override
	public void reset() {
		playerTouchedViewArea = false;
		target = null;
		shootDelay = 2f;
		timeSinceLastShot = 0f;
		enemyType = Type.BASIC_ENEMY;
		isDead = false;
		xPosCenter = -1;

		velocity = LEFT_SPEED;
		noOfSteps = 0;
	}

}
