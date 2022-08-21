package com.mygdx.game.entitycomponentsystem.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Logger;
import com.mygdx.game.entitycomponentsystem.components.CharacterStatsComponent;
import com.mygdx.game.entitycomponentsystem.components.HealthComponent;
import com.mygdx.game.entitycomponentsystem.components.TransformComponent;

public class CharacterStatsSystem extends IteratingSystem {

    private final Logger logger = new Logger(CharacterStatsSystem.class.getSimpleName(), Logger.INFO);

    public CharacterStatsSystem() {
        super(Family.all(CharacterStatsComponent.class, HealthComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        TransformComponent transformComponent = entity.getComponent(TransformComponent.class);
        CharacterStatsComponent characterStatsComponent = entity.getComponent(CharacterStatsComponent.class);
        HealthComponent healthComponent = entity.getComponent(HealthComponent.class);

        characterStatsComponent.refreshPosition(transformComponent.position.x, transformComponent.position.y);
        characterStatsComponent.refreshLives(healthComponent.hpPoints);
    }
}
