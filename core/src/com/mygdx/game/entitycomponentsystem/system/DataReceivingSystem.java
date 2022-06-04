package com.mygdx.game.entitycomponentsystem.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.client.ClientHandler;
import com.mygdx.game.client.ReceivedMessage;
import com.mygdx.game.client.data.PlayerDataContainer;
import com.mygdx.game.entitycomponentsystem.components.ControlledInputComponent;
import com.mygdx.game.entitycomponentsystem.components.PlayerComponent;
import com.mygdx.game.entitycomponentsystem.components.RemoteInputComponent;
import com.mygdx.game.screens.GameScreen;
import com.mygdx.game.screens.loadingScreens.LoadingIntroScreen;
import com.mygdx.game.screens.menuScreens.DifficultyScreen;
import com.mygdx.game.screens.menuScreens.MenuScreen;
import com.mygdx.game.screens.menuScreens.ModeSelectionScreen;
import com.mygdx.game.screens.menuScreens.OptionsScreen;

public class DataReceivingSystem extends IteratingSystem {

    private ClientHandler clientHandler;

    public DataReceivingSystem(ClientHandler clientHandler) {
        super(Family.all(PlayerComponent.class,
                RemoteInputComponent.class,
                ControlledInputComponent.class).get());
        this.clientHandler = clientHandler;
    }

    @Override
    public void update (float deltaTime)
    {
        /* Optimization: Go through all entities only if there is some new data */
        if(this.clientHandler.isNewDataReceived())
        {
            super.update(deltaTime);
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        Array<ReceivedMessage> receivedMessage = clientHandler.getReceivedMessage();
        ControlledInputComponent cntrlInpComp = entity.getComponent(ControlledInputComponent.class);
        PlayerComponent playerComponent = entity.getComponent(PlayerComponent.class);

        if(receivedMessage.isEmpty())
        {
            processReceivedData(receivedMessage.removeIndex(0));
        }
    }

    private void processReceivedData(ReceivedMessage receivedMessage)
    {
       /* switch(receivedMessage.getActionType()){
            case MENU_SCREEN:
                if(menuScreen == null) menuScreen = new MenuScreen(this);
                this.setScreen(menuScreen);
                break;
            default:
        }

        for (int index = 0; index < receivedMessage.size; index++)
        {
            int playerID = receivedMessage.get(index).getPlayerDataContainer().getPlayerID();

            if( playerID == playerComponent.playerID)
            {
                *//*Set new command list for specified player, then remove it from client buffer *//*
                cntrlInpComp.abInputCommandList = receivedMessage.removeIndex(index).
                        getPlayerDataContainer().getAbInputCommandList();
            }
        }*/
    }

}
