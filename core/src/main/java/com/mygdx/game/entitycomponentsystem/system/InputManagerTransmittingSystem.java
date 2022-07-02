package com.mygdx.game.entitycomponentsystem.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.client.ClientHandler;
import com.mygdx.game.common.Direction;
import com.mygdx.game.config.GameConfig;
import com.mygdx.game.entitycomponentsystem.components.ControlledInputComponent;
import com.mygdx.game.entitycomponentsystem.components.ControlledInputRemoteComponent;
import com.mygdx.game.entitycomponentsystem.components.LocalInputComponent;
import com.mygdx.game.entitycomponentsystem.components.PlayerComponent;
import com.mygdx.game.entitycomponentsystem.components.TransformComponent;

import org.json.JSONArray;

/** This system is reliable for uploading player input schema state
 *  on each change (Each time player press/unpress any buttons from
 *  input schema)
 *  */
public class InputManagerTransmittingSystem extends IteratingSystem {

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

        if(!lastStoredPosition.equals(transformComponent.position))
        {
            this.clientHandler.getSocket().emit("updatePlayerInputPosition",
                    transformComponent.position.x,
                    transformComponent.position.y,
                    playerComponent.playerID);
            lastStoredPosition.set(transformComponent.position);
        }

        if(currentDirection!= playerComponent.direction)
        {
            this.clientHandler.getSocket().emit("playerChangedDirReq", playerComponent.playerID);
            currentDirection = playerComponent.direction;
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
                this.clientHandler.getSocket().emit("magicFired",
                        playerComponent.playerID,
                        jAInputCommandList
                        );
                this.isMagicFired = cntrlInComp.abInputCommandList[GameConfig.SPACE];
            }
        }
    }
}
