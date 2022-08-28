package com.mygdx.game.entitycomponentsystem.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Pool.Poolable;

public class B2dBodyComponent implements Component, Poolable{

	public int bodyID = 0;
	public String bodyName = "";
	public Body body;
	public boolean isDead = false;

	@Override
	public void reset()
	{
		body = null;
		isDead = false;
		bodyName = "";
	}
}
