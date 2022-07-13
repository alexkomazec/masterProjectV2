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

	/* !Warning! WorkAround that is a bridge between PlayerControlSystem, and CollectibleBasicManagerSystem*/
	public boolean fired = false;

	@Override
	public void reset() {
		cam = null;
		onPlatform = false;
		isDead = false;
	}	
}
