package com.mygdx.game.entitycomponentsystem.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

/*
 * Stores the type of entity this is
 */
public class TypeComponent implements Component, Poolable {
	public static final int PLAYER = 0;
	public static final int ENEMY = 1;
	public static final int SCENERY = 3;
	public static final int OTHER = 4;
	public static final int SPRING = 5;
	public static final int BULLET = 6;
	public static final int BASIC_COLLECTIBLE = 7;
	public static final int POTIONS = 8;
	public static final int ENEMY_SENSOR = 9;
	public static final int VIEW_AREA_SENSOR = 10;
	public static final int PORTALS = 11;
	public static final int HURTABLE_OBJECT = 12;
	
	public int type = OTHER;

	@Override
	public void reset() {
		type = OTHER;		
	}
}
