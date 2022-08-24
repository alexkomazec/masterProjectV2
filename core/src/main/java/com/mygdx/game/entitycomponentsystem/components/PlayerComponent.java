package com.mygdx.game.entitycomponentsystem.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.mygdx.game.common.Direction;

public class PlayerComponent implements Component, Poolable{

	public int playerID;
	public OrthographicCamera cam = null;
	public boolean onPlatform = false;
	public boolean isDead = false;
	public boolean onSpring = false;
	public boolean alreadyFired;
	public PlayerConnectivity typeOfPlayer = PlayerConnectivity.NONE;

	/* !Warning! WorkAround that is a bridge between PlayerControlSystem, and CollectibleBasicManagerSystem*/
	public boolean fired = false;
	/* !Warning! WorkAround that is a bridge between PlayerControlSystem, and InputManagerTransmitingsystem*/
	public boolean readyToTransmitBullet = false;

	/* Temp data, this data should be sent over the network */
	public float bulletXvel =  0;
	public Direction bulletDirectionOnShoot = Direction.LEFT;
	public Vector2 bulletPosition = new Vector2();
	public boolean needTofire = false;


	public enum PlayerConnectivity {
		NONE,
		LOCAL,
		ONLINE
	}

	@Override
	public void reset() {
		playerID = 0;
		onSpring = false;
		alreadyFired = false;
		typeOfPlayer = PlayerConnectivity.NONE;
		fired = false;
		cam = null;
		onPlatform = false;
		isDead = false;
		bulletXvel = 0;
		bulletDirectionOnShoot = Direction.LEFT;
		bulletPosition = new Vector2();
		readyToTransmitBullet = false;
		needTofire = false;
	}	
}
