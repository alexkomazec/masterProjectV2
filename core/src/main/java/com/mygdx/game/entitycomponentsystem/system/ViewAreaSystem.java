package com.mygdx.game.entitycomponentsystem.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.entitycomponentsystem.components.B2dBodyComponent;
import com.mygdx.game.entitycomponentsystem.components.EnemyComponent;
import com.mygdx.game.entitycomponentsystem.components.SteeringComponent;
import com.mygdx.game.entitycomponentsystem.components.ViewAreaComponent;

public class ViewAreaSystem extends IteratingSystem
{
    public ViewAreaSystem() {
        super(Family.all(ViewAreaComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        ViewAreaComponent viewAreaComponent = entity.getComponent(ViewAreaComponent.class);
        float posX = viewAreaComponent.owner.
                getComponent(B2dBodyComponent.class).body.getPosition().x;
        float posY = viewAreaComponent.owner.
                getComponent(B2dBodyComponent.class).body.getPosition().y;

        viewAreaComponent.viewAreaBody.setTransform(new Vector2(posX, posY), 0);

        if(viewAreaComponent.collisionHappened) {
            EnemyComponent enemyComponent = viewAreaComponent.owner.getComponent(EnemyComponent.class);
            enemyComponent.target = viewAreaComponent.bodyThatColidedViewArea;
            viewAreaComponent.collisionHappened = false;

            if (enemyComponent.enemyType == EnemyComponent.Type.CLOUD)
            {
                SteeringComponent steeringComponent = viewAreaComponent.owner.getComponent(SteeringComponent.class);
                steeringComponent.currentMode = SteeringComponent.SteeringState.WANDER;
            }
        }
    }
}
