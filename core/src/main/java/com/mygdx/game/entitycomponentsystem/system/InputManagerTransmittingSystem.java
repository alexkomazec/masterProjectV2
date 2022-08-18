package com.mygdx.game.entitycomponentsystem.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.mygdx.game.client.ClientHandler;
import com.mygdx.game.common.Direction;
import com.mygdx.game.config.GameConfig;
import com.mygdx.game.entitycomponentsystem.components.ControlledInputComponent;
import com.mygdx.game.entitycomponentsystem.components.ControlledInputRemoteComponent;
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

    protected static final Logger logger = new Logger(InputManagerTransmittingSystem.class.getSimpleName(), Logger.INFO);
    private ClientHandler clientHandler;
    private Vector2 lastStoredPosition;
    boolean isMagicFired = false;
    Direction currentDirection = Direction.RIGHT;

    public InputManagerTransmittingSystem(ClientHandler clientHandler) {
        super(Family.all(PlayerComponent.class, LocalInputComponent.class).get());
        this.clientHandler = clientHandler;
        this.lastStoredPosition = new Vector2();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ControlledInputComponent cntrlInComp    = entity.getComponent(ControlledInputComponent.class);
        TransformComponent transformComponent = entity.getComponent(TransformComponent.class);
        PlayerComponent playerComponent = entity.getComponent(PlayerComponent.class);
        DirectionComponent directionComponent = entity.getComponent(DirectionComponent.class);

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
            logger.debug("updatePlayerInputPosition: Player with ID " + playerComponent.playerID + " changed position");
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
                for (int index = 0; index < cntrlInComp.abInputCommandList.length-1; index++)
                {
                    /* Add dummy boolean false*/
                    jAInputCommandList.put(false);
                }
                jAInputCommandList.put(cntrlInComp.abInputCommandList[GameConfig.SPACE]);
                logger.debug("magicFired: Player with ID " + playerComponent.playerID + " fire Magic");
                this.clientHandler.getSocket().emit("magicFired",
                        this.clientHandler.getSocket().id(),
                        jAInputCommandList
                        );
                this.isMagicFired = cntrlInComp.abInputCommandList[GameConfig.SPACE];
            }
        }
    }
}
