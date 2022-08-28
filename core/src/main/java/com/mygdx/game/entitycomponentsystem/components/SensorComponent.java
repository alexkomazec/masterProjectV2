package com.mygdx.game.entitycomponentsystem.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.common.SensorType;

public class SensorComponent implements Component{

    public Entity owner;
    public Entity bodyThatColidedViewArea;
    public boolean collisionHappened;
    public SensorType sensorType = SensorType.DEFAULT;

}
