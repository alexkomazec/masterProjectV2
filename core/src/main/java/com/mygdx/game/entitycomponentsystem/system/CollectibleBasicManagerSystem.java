package com.mygdx.game.entitycomponentsystem.system;

import static com.mygdx.game.entitycomponentsystem.components.StateComponent.STATE_FALLING;
import static com.mygdx.game.entitycomponentsystem.components.StateComponent.STATE_JUMPING;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Logger;
import com.mygdx.game.common.Direction;
import com.mygdx.game.config.GameConfig;
import com.mygdx.game.entitycomponentsystem.components.B2dBodyComponent;
import com.mygdx.game.entitycomponentsystem.components.BulletComponent;
import com.mygdx.game.entitycomponentsystem.components.CollectibleBasicArrayComponent;
import com.mygdx.game.entitycomponentsystem.components.ControlledInputComponent;
import com.mygdx.game.entitycomponentsystem.components.CoolDownComponent;
import com.mygdx.game.entitycomponentsystem.components.DirectionComponent;
import com.mygdx.game.entitycomponentsystem.components.PlayerComponent;
import com.mygdx.game.entitycomponentsystem.components.StateComponent;
import com.mygdx.game.gameworld.BodyCreator;
import com.mygdx.game.gameworld.GameWorldCreator;
import com.mygdx.game.utils.GdxUtils;

import javax.swing.plaf.nimbus.State;

public class CollectibleBasicManagerSystem extends IteratingSystem {

    private static final Logger logger = new Logger(CollectibleBasicManagerSystem.class.getSimpleName(), Logger.DEBUG);
    public final int MAX_BUFF = 3;

    /* Mechanism for delayed double shoot in order to make some space between the first shoot,
    * and the second shoot. Timeout is 0.5f seconds because regular cooldown of shoot is 1 second.
    * */
    private final float TIMEOUT = 0.25f;
    private float elapsedTime = 0f;

    boolean doubleJumpUsed = false;

    boolean jumpButtonCurrentState = true;
    boolean jumpButtonLastState = true;
    boolean firstTimeJumpHappend = false;

    PooledEngine pooledEngine;
    World world;
    private GameWorldCreator gameWorldCreator;
    private CollectibleBasicArrayComponent collectibleBasicComponent;
    private PlayerComponent playerComponent;
    private DirectionComponent directionComponent;
    private ControlledInputComponent cntrlComponent;
    private B2dBodyComponent b2dbodyComponent;
    private StateComponent stateComponent;

    public CollectibleBasicManagerSystem(GameWorldCreator gameWorldCreator, World world, PooledEngine pooledEngine) {
        super(Family.all(PlayerComponent.class, CollectibleBasicArrayComponent.class).get());
        this.gameWorldCreator = gameWorldCreator;
        this.world = world;
        this.pooledEngine = pooledEngine;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        collectibleBasicComponent = entity.getComponent(CollectibleBasicArrayComponent.class);
        playerComponent = entity.getComponent(PlayerComponent.class);
        cntrlComponent = entity.getComponent(ControlledInputComponent.class);
        b2dbodyComponent = entity.getComponent(B2dBodyComponent.class);
        stateComponent = entity.getComponent(StateComponent.class);
        directionComponent = entity.getComponent(DirectionComponent.class);

        if(collectibleBasicComponent.collectibleBasicArray[GameConfig.DOUBLE_SHOOT])
        {
            doubleShoot(deltaTime);
        }

        if(collectibleBasicComponent.collectibleBasicArray[GameConfig.DOUBLE_JUMP])
        {
            doubleJump();
        }

        if(collectibleBasicComponent.collectibleBasicArray[GameConfig.STOMP])
        {
            stomp();
        }

    }

    private void doubleShoot(float deltaTime)
    {
        if(playerComponent.fired) {
            elapsedTime += deltaTime;
            if (elapsedTime >= TIMEOUT)
            {
                elapsedTime = 0;
                processDoubleShoot();
            }
        }
    }
    private void processDoubleShoot()
    {
        playerComponent.fired = false;
        float startBulletPositionX;
        float startBulletPositionY;
        float xVel;

        if (directionComponent.direction == Direction.LEFT) {
            startBulletPositionX = (b2dbodyComponent.body.getPosition().x * GameConfig.MULTIPLY_BY_PPM) - 16f * 3;
            xVel = -7;
        } else {
            startBulletPositionX = (b2dbodyComponent.body.getPosition().x * GameConfig.MULTIPLY_BY_PPM) + 16f;
            xVel = 7;
        }

        startBulletPositionY = b2dbodyComponent.body.getPosition().y * GameConfig.MULTIPLY_BY_PPM;

        this.gameWorldCreator.createBullet(startBulletPositionX, startBulletPositionY,
                xVel, 0,
                directionComponent.direction,
                BulletComponent.Owner.PLAYER, this.pooledEngine,
                world);
    }

    private void stomp()
    {
        if (GdxUtils.isInputCommandTrue(GameConfig.DOWN, cntrlComponent))
        {
            b2dbodyComponent.body.applyLinearImpulse(0, -5f, b2dbodyComponent.body.getWorldCenter().x, b2dbodyComponent.body.getWorldCenter().y, true);
        }
    }

    public void doubleJump()
    {
        this.jumpButtonCurrentState = GdxUtils.isInputCommandTrue(GameConfig.UP, cntrlComponent);

        if (!firstTimeJumpHappend)
        {
            if(this.jumpButtonCurrentState)
            {
                firstTimeJumpHappend = true;
            }
        }

        if (this.jumpButtonCurrentState) {
            if (!doubleJumpUsed) {
                if (!this.jumpButtonLastState) {
                    logger.debug("double jump");
                    b2dbodyComponent.body.applyLinearImpulse(0, 5f * b2dbodyComponent.body.getMass(), b2dbodyComponent.body.getWorldCenter().x, b2dbodyComponent.body.getWorldCenter().y, true);
                    doubleJumpUsed = true;
                }
            }
        }

        if (playerComponent.onPlatform) {
            if (doubleJumpUsed) {
                logger.debug("Reset Double Jump");
                doubleJumpUsed = false;
            }
        }
        this.jumpButtonLastState = this.jumpButtonCurrentState;
    }

}
