package com.mygdx.game.entitycomponentsystem.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.mygdx.game.common.Direction;

public class TransformComponent implements Component, Poolable {

	/* position from TransformComponent should be just read only,
	*  any position updates should be put in body component position
	* */
    public final Vector2 position = new Vector2();
	public Vector2 lastPosition = new Vector2();

    public final Vector2 scale = new Vector2(1.0f, 1.0f);
    public boolean isHidden = false;
	@Override
	public void reset() {
	    isHidden = false;
	}
}