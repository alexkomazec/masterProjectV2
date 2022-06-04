package com.mygdx.game.entitycomponentsystem.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.mygdx.game.client.ClientHandler;
import com.mygdx.game.entitycomponentsystem.components.ControlledInputComponent;
import com.mygdx.game.entitycomponentsystem.components.LocalInputComponent;
import com.mygdx.game.entitycomponentsystem.components.PlayerComponent;

public class DataTransmittingSystem extends IteratingSystem {

    private ClientHandler clientHandler;

    public DataTransmittingSystem(ClientHandler clientHandler) {
        super(Family.all(PlayerComponent.class, LocalInputComponent.class).get());

        this.clientHandler = clientHandler;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        PlayerComponent playerComponent = entity.getComponent(PlayerComponent.class);
        ControlledInputComponent cntrlInpComp = entity.getComponent(ControlledInputComponent.class);

        if(cntrlInpComp.newInputHappend)
        {

            //clientHandler.sendData(cntrlInpComp.playerDataInputList);
            cntrlInpComp.newInputHappend = false;
        }
    }
}
