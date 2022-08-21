package com.mygdx.game.entitycomponentsystem.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
	}	
}
