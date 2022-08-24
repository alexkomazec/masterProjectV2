package com.mygdx.game.entitycomponentsystem.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.mygdx.game.client.ClientHandler;
import com.mygdx.game.common.CoolDown;
import com.mygdx.game.common.Direction;
import com.mygdx.game.config.GameConfig;
import com.mygdx.game.entitycomponentsystem.components.ControlledInputComponent;
import com.mygdx.game.entitycomponentsystem.components.ControlledInputRemoteComponent;
import com.mygdx.game.entitycomponentsystem.components.CoolDownComponent;
import com.mygdx.game.entitycomponentsystem.components.DirectionComponent;
import com.mygdx.game.entitycomponentsystem.components.LocalInputComponent;
import com.mygdx.game.entitycomponentsystem.components.PlayerComponent;
import com.mygdx.game.entitycomponentsystem.components.TransformComponent;

import org.json.JSONArray;

/** This system is reliable for uploading player input schema state
 *  on each change (Each time player press/unpress any buttons from
 *  input schema)
 *  */
public class InputManagerTransmittingSystem extends IteratingSystem {

    protected static final Logger logger = new Logger(InputManagerTransmittingSystem.class.getSimpleName(), Logger.DEBUG);
    private ClientHandler clientHandler;
    private Vector2 lastStoredPosition;
    boolean isMagicFired = false;
    Direction currentDirection = Direction.RIGHT;
    private boolean isInit;

    public InputManagerTransmittingSystem(ClientHandler clientHandler) {
        super(Family.all(PlayerComponent.class, LocalInputComponent.class).get(), 1);
        logger.debug("InputManagerTransmittingSystem has been created");
        this.clientHandler = clientHandler;
        this.lastStoredPosition = new Vector2();
        this.isInit = false;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ControlledInputComponent cntrlInComp = entity.getComponent(ControlledInputComponent.class);
        TransformComponent transformComponent = entity.getComponent(TransformComponent.class);
        PlayerComponent playerComponent = entity.getComponent(PlayerComponent.class);
        DirectionComponent directionComponent = entity.getComponent(DirectionComponent.class);

        if(!isInit)
        {
            this.lastStoredPosition.x = transformComponent.position.x;
            this.lastStoredPosition.y = transformComponent.position.y;
            this.currentDirection = directionComponent.direction;
            isInit =  true;
        }

        boolean posTheSame = false;

        /* Hint: Cast to int because there is no reason to bother others with the different
        *        in fifth decimal of the float number. Comparing floats causes a big stream of
        *        emitting data.
        * */
        int x = (int)lastStoredPosition.x;
        int y = (int)lastStoredPosition.y;
        int x1 = (int)transformComponent.position.x;
        int y1 = (int)transformComponent.position.y;

        if(x == x1 && y == y1)
        {
            posTheSame = true;
        }
        if(!posTheSame)
        {
            //logger.debug("updatePlayerInputPosition: Player with ID " + playerComponent.playerID + " changed position");
            //logger.debug(" lastStoredPosition.x " + x +
            //        " lastStoredPosition.y " + y +
            //        " x1 " + x1 +
            //        " y1 " + y1);
            this.clientHandler.getSocket().emit("updatePlayerInputPosition",
                    transformComponent.position.x,
                    transformComponent.position.y,
                    this.clientHandler.getSocket().id());
            lastStoredPosition.set(transformComponent.position);
        }

        if(currentDirection!= directionComponent.direction)
        {
            logger.debug("playerChangedDirReq: Player with ID " + playerComponent.playerID + " changed direction");
            this.clientHandler.getSocket().emit("playerChangedDirReq", this.clientHandler.getSocket().id());
            currentDirection = directionComponent.direction;
        }

        if(cntrlInComp.newInputHappend)
        {
            JSONArray jAInputCommandList = new JSONArray();
            cntrlInComp.newInputHappend = false;

            /* Send event only if the player fired magic. Other player movements are updated
             * using updatePlayerInputPosition event*/
            if(cntrlInComp.abInputCommandList[GameConfig.SPACE] != this.isMagicFired)
            {

                if (playerComponent.readyToTransmitBullet)
                {
                    playerComponent.readyToTransmitBullet = false;
                    for (int index = 0; index < cntrlInComp.abInputCommandList.length - 1; index++) {
                        /* Add dummy boolean false*/
                        jAInputCommandList.put(false);
                    }
                    jAInputCommandList.put(cntrlInComp.abInputCommandList[GameConfig.SPACE]);
                    logger.debug("magicFired: Player with ID " + playerComponent.playerID + " fire Magic");
                    logger.debug(
                            "playerComponent.bulletPosition: " + playerComponent.bulletPosition +
                                    "playerComponent.bulletXvel: " + playerComponent.bulletXvel +
                                    "playerComponent.bulletDirectionOnShoot: " + playerComponent.bulletDirectionOnShoot);

                    this.clientHandler.getSocket().emit("magicFired",
                            this.clientHandler.getSocket().id(),
                            jAInputCommandList,
                            playerComponent.bulletPosition.x,
                            playerComponent.bulletPosition.y,
                            playerComponent.bulletXvel,
                            playerComponent.bulletDirectionOnShoot
                    );

                    //playerComponent.bulletPosition.x, //playerComponent.bulletPosition.x,
                    //        playerComponent.bulletPosition.y, //playerComponent.bulletPosition.y,
                    //        playerComponent.bulletXvel, //playerComponent.bulletXvel,
                    //        directionComponent.direction//playerComponent.bulletDirectionOnShoot
                    this.isMagicFired = cntrlInComp.abInputCommandList[GameConfig.SPACE];
                }
            }
        }
    }
}
