package com.mygdx.game.entitycomponentsystem.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.mygdx.game.config.GameConfig;
import com.mygdx.game.entitycomponentsystem.components.CollisionEffectComponent;

public class CollisionEffectsSystem extends IteratingSystem {

    private float lifeDuration = 0;

    public CollisionEffectsSystem(float sizeOfFrames) {
        super(Family.all(CollisionEffectComponent.class).get());
        this.lifeDuration = sizeOfFrames * GameConfig.FRAME_DURATION;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        CollisionEffectComponent collisionComponent = entity.getComponent(CollisionEffectComponent.class);
        if(collisionComponent.aliveTime >= this.lifeDuration)
        {
            getEngine().removeEntity(entity);
        }
        collisionComponent.aliveTime+= deltaTime;
    }
}
