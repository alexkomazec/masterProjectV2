package com.mygdx.game.entitycomponentsystem.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;
import com.mygdx.game.entitycomponentsystem.components.B2dBodyComponent;
import com.mygdx.game.entitycomponentsystem.components.EnemyComponent;
import com.mygdx.game.entitycomponentsystem.components.SteeringComponent;
import com.mygdx.game.entitycomponentsystem.components.SensorComponent;

public class SensorSystem extends IteratingSystem
{
    PooledEngine pooledEngine;
    private static final Logger logger = new Logger(SensorSystem.class.getSimpleName(), Logger.DEBUG);

    public SensorSystem(PooledEngine pooledEngine) {
        super(Family.all(SensorComponent.class).get());
        this.pooledEngine = pooledEngine;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        SensorComponent sensorComponent = entity.getComponent(SensorComponent.class);
        B2dBodyComponent bodyComponent = entity.getComponent(B2dBodyComponent.class);

        if(!sensorComponent.owner.getComponent(B2dBodyComponent.class).isDead)
        {
            float posX = sensorComponent.owner.
                    getComponent(B2dBodyComponent.class).body.getPosition().x;
            float posY = sensorComponent.owner.
                    getComponent(B2dBodyComponent.class).body.getPosition().y;

            bodyComponent.body.setTransform(new Vector2(posX, posY), 0);

            if (sensorComponent.collisionHappened)
            {
                EnemyComponent enemyComponent = sensorComponent.owner.getComponent(EnemyComponent.class);
                sensorComponent.collisionHappened = false;

                if (enemyComponent.enemyType == EnemyComponent.Type.CLOUD)
                {
                    enemyComponent.target = sensorComponent.bodyThatColidedViewArea;
                    SteeringComponent steeringComponent = sensorComponent.owner.getComponent(SteeringComponent.class);
                    steeringComponent.currentMode = SteeringComponent.SteeringState.WANDER;
                }
                else if(enemyComponent.enemyType == EnemyComponent.Type.BASIC_ENEMY)
                {
                    enemyComponent.target = sensorComponent.bodyThatColidedViewArea;
                    enemyComponent.playerTouchedViewArea = true;
                }
                else
                {
                    logger.error("Wrong enemyType ");
                }
            }
        }
        else
        {
            //Owner is dead, so destroy viewArea
            this.pooledEngine.removeEntity(entity);
        }
    }
}
