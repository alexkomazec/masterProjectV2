package com.mygdx.game.entitycomponentsystem.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.mygdx.game.client.ClientHandler;
import com.mygdx.game.client.Message;
import com.mygdx.game.client.data.PlayerDataContainer;
import com.mygdx.game.config.GameConfig;
import com.mygdx.game.entitycomponentsystem.components.LocalInputComponent;
import com.mygdx.game.entitycomponentsystem.components.PlayerComponent;
import com.mygdx.game.entitycomponentsystem.components.TransformComponent;

import io.socket.client.Socket;

public class DataTransmittingSystem extends IteratingSystem {

    private ClientHandler clientHandler;

    public DataTransmittingSystem(ClientHandler clientHandler) {
        super(Family.all(PlayerComponent.class, LocalInputComponent.class).get());

        this.clientHandler = clientHandler;
    }

    @Override
    public void update (float deltaTime)
    {
        /* Optimization: Go through all entities only if there is some new data */
        if(!this.clientHandler.isTransMessageArrayEmpty())
        {
            if(this.clientHandler.peekAtFirstTrans().doesActionDependsOnEntity())
            {
                super.update(deltaTime);
            }
            else {
                Message message = this.clientHandler.getTransmittingMessage();
                for (int index = 0; index < message.getPlayerDataContainerArray().size;)
                {
                    processData(message.getPlayerDataContainer(), message.getActionType());
                }
            }

        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        Message message = this.clientHandler.getTransmittingMessage();

        for (int index = 0; index < message.getPlayerDataContainerArray().size;) {
            processData(message.getPlayerDataContainer(), message.getActionType(), entity);
        }
    }

    private void processData(PlayerDataContainer playerDataContainer, int actionType, Entity entity)
    {
        Socket socket = this.clientHandler.getSocket();
        switch(actionType)
        {
            case ClientHandler.UPLOAD_CURRENT_PLAYER_POS_REQ:

                System.out.println("DataTransmittingSystem: emit refreshPlayersPosition");

                PlayerComponent playerComponent = entity.getComponent(PlayerComponent.class);
                TransformComponent transformComponent = entity.getComponent(TransformComponent.class);

                playerDataContainer.setPlayerID(playerComponent.playerID);
                playerDataContainer.setPosition(transformComponent.position);

                socket.emit("refreshPlayersPosition",
                        playerDataContainer.getPosition().x,
                        playerDataContainer.getPosition().y,
                        playerDataContainer.getPlayerID());
            break;

            default:
                System.out.println("Wrong action type" + actionType);
        }
    }

    private void processData(PlayerDataContainer playerDataContainer, int actionType)
    {
        Socket socket = this.clientHandler.getSocket();
        switch(actionType)
        {
            /* NOTE: Be careful, this case does not depend on entity*/
            case ClientHandler.SEND_PLAYER_TO_SERVER:

                System.out.println("DataTransmittingSystem: emit addPlayer");

                socket.emit("addPlayer",
                        playerDataContainer.getPosition().x,
                        playerDataContainer.getPosition().y,
                        playerDataContainer.getPlayerID());
                break;
        }
    }

}
