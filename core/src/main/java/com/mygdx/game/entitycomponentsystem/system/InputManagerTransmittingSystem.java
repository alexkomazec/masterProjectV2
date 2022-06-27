package com.mygdx.game.entitycomponentsystem.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.client.ClientHandler;
import com.mygdx.game.entitycomponentsystem.components.ControlledInputComponent;
import com.mygdx.game.entitycomponentsystem.components.LocalInputComponent;
import com.mygdx.game.entitycomponentsystem.components.PlayerComponent;

/** This system is reliable for uploading player input schema state
 *  on each change (Each time player press/unpress any buttons from
 *  input schema)
 *  */
public class InputManagerTransmittingSystem extends IteratingSystem {

    private ClientHandler clientHandler;

    public InputManagerTransmittingSystem(ClientHandler clientHandler) {
        super(Family.all(PlayerComponent.class, LocalInputComponent.class).get());
        this.clientHandler = clientHandler;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ControlledInputComponent cntrlInComp    = entity.getComponent(ControlledInputComponent.class);

        if(cntrlInComp.newInputHappend)
        {
            PlayerComponent playerComponent = entity.getComponent(PlayerComponent.class);
            Array<Boolean> aBInputCommandList = new Array<>();
            cntrlInComp.newInputHappend = false;

            for (int index = 0; index < cntrlInComp.abInputCommandList.length; index++)
            {
                aBInputCommandList.add(cntrlInComp.abInputCommandList[index]);
            }

            this.clientHandler.getSocket().emit("updatePlayerInputCmd",
                    playerComponent.playerID,
                    aBInputCommandList.toArray());
        }
    }
}
