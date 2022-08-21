package com.mygdx.game.entitycomponentsystem.system;

import static com.mygdx.game.entitycomponentsystem.components.EnemyComponent.Type.BASIC_ENEMY;
import static com.mygdx.game.entitycomponentsystem.components.EnemyComponent.Type.CLOUD;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.utils.Logger;
import com.mygdx.game.config.GameConfig;
import com.mygdx.game.entitycomponentsystem.components.B2dBodyComponent;
import com.mygdx.game.entitycomponentsystem.components.CollisionComponent;
import com.mygdx.game.entitycomponentsystem.components.EnemyComponent;
import com.mygdx.game.entitycomponentsystem.components.HealthComponent;
import com.mygdx.game.entitycomponentsystem.components.PlayerComponent;

public class HealthManagerSystem extends IteratingSystem {

    protected static final Logger logger = new Logger(HealthManagerSystem.class.getSimpleName(), Logger.INFO);
    HealthComponent healthComponent;
    CollisionComponent collisionComponent;
    B2dBodyComponent b2dBodyComponent;

    public HealthManagerSystem() {
        super(Family.all(HealthComponent.class, CollisionComponent.class, B2dBodyComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        this.healthComponent = entity.getComponent(HealthComponent.class);
        this.collisionComponent = entity.getComponent(CollisionComponent.class);
        this.b2dBodyComponent = entity.getComponent(B2dBodyComponent.class);

        if(this.collisionComponent.healthAction[GameConfig.DECREASE_HP])
        {
            this.collisionComponent.healthAction[GameConfig.DECREASE_HP] = false;
            int remainNoOfLives;
            remainNoOfLives = removeLife();

            if(isGameObjectDead(remainNoOfLives))
            {
                entity.remove(this.healthComponent.getClass());
                isEntityRemoved(this.healthComponent);
                this.b2dBodyComponent.isDead = true;
            }
        }

        if(this.collisionComponent.healthAction[GameConfig.INCREASE_HP])
        {
            collisionComponent.healthAction[GameConfig.INCREASE_HP] = false;
            addLife();
        }

        if(this.collisionComponent.healthAction[GameConfig.KILL_OBJECT])
        {
            this.collisionComponent.healthAction[GameConfig.KILL_OBJECT] = false;
            int remainNoOfLives;
            remainNoOfLives = removeAllLives();

            if(isGameObjectDead(remainNoOfLives))
            {
                entity.remove(this.healthComponent.getClass());
                isEntityRemoved(this.healthComponent);
                this.b2dBodyComponent.isDead = true;
            }
        }
    }

    public void initializeHealth(Entity entity)
    {
        logger.debug("initializeHealth");
        HealthComponent healthComponent = new HealthComponent();
        healthComponent.hpPoints = this.getInitHealthCap(entity);
        entity.add(healthComponent);
    }

    /* Get how many lives should entity have during initialization */
    private int getInitHealthCap(Entity entity)
    {
        PlayerComponent playerComponent = entity.getComponent(PlayerComponent.class);

        if(playerComponent != null)
        {
            logger.debug("Init Cap for player");
            return GameConfig.MAX_PLAYER_LIVES;
        }
        else
        {
            EnemyComponent.Type enemyType = entity.getComponent(EnemyComponent.class).enemyType;
            if(enemyType == BASIC_ENEMY)
            {
                logger.debug("Init Cap for Basic Enemies");
                return GameConfig.MAX_BASIC_ENEMY_LIVES;
            }
            else if(enemyType == CLOUD)
            {
                logger.debug("Init Cap for Cloud");
                return GameConfig.MAX_CLOUD;
            }
            else
            {
                logger.error("Init Cap Error");
                return GameConfig.ERROR;
            }
        }
    }

    private int removeLife()
    {
        logger.debug("Remove life");
        return --this.healthComponent.hpPoints;
    }

    private int removeAllLives()
    {
        logger.debug("Remove all lives");
        this.healthComponent.hpPoints = 0;
        return this.healthComponent.hpPoints;
    }

    private void addLife()
    {
        logger.debug("Add life");
        this.healthComponent.hpPoints++;
    }

    private boolean isGameObjectDead(int remainNoOfLives)
    {
        return remainNoOfLives == 0;
    }

    private void isEntityRemoved(Component component)
    {
        //Check if action is successful
        if(component == null)
        {
            logger.error(" Error: Health Component did not deleted");
        }
    }

}
