package com.mygdx.game.entitycomponentsystem.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Logger;
import com.mygdx.game.KeyboardController;
import com.mygdx.game.common.InputAdapterWrapper;
import com.mygdx.game.entitycomponentsystem.components.Controllable;
import com.mygdx.game.entitycomponentsystem.components.ControlledInputComponent;
import com.mygdx.game.entitycomponentsystem.components.LocalInputComponent;
import com.mygdx.game.entitycomponentsystem.components.PlayerComponent;

import java.util.HashMap;

/** This system is reliable for tracking any changes made on active Input Processor
 * (Keyboard, mouse, joypad, ...) and forwards these changes into ControlledInputComponent
 * of the current player
 *  */
public class InputManagerSystem extends IteratingSystem {

    protected static final Logger logger = new Logger(InputManagerSystem.class.getSimpleName(), Logger.INFO);

    /*There will be stored all needed input processors like keyboard, controller...*/
    private final HashMap<InputAdapterWrapper, Integer> hmpInputprocessor;
    private boolean isOnlineConnection = false;
    private static final int UNASSIGNED_INPUT_PROCESSOR = 999;

    public InputManagerSystem(boolean isOnlineConnection) {
        super(Family.all(PlayerComponent.class, LocalInputComponent.class, Controllable.class).get());
        hmpInputprocessor = new HashMap<>();
        this.isOnlineConnection = isOnlineConnection;
        hmpInputprocessor.put(new KeyboardController(),UNASSIGNED_INPUT_PROCESSOR);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        PlayerComponent playerComponent         = entity.getComponent(PlayerComponent.class);
        ControlledInputComponent cntrlInComp    = entity.getComponent(ControlledInputComponent.class);
        int playerID                            = playerComponent.playerID;
        HashMap.Entry<InputAdapterWrapper, Integer> localEntry  = null;

        /* Iterate through HashMap to find a specified Input processor that player */
        for (HashMap.Entry<InputAdapterWrapper, Integer> tempEntry : hmpInputprocessor.entrySet())
        {
            Integer value = tempEntry.getValue();
            if(value == playerID)
            {
                localEntry = tempEntry;
                break;
            }
        }

        /* if there is non-null localEntry it means that playerID has been found in hash Map*/
        if(localEntry != null)
        {
            InputAdapterWrapper key = localEntry.getKey();
            int abInputCommandListLength = cntrlInComp.abInputCommandList.length;
            /* Iterate through all input commands */
            for (int index = 0; index < abInputCommandListLength; index++) {

                boolean plInputCommand =  cntrlInComp.abInputCommandList[index];
                boolean inputProcInputCommand = key.getInputCommand(index);
                if(plInputCommand != inputProcInputCommand)
                {
                    cntrlInComp.abInputCommandList[index] = inputProcInputCommand;
                    cntrlInComp.newInputHappend = true;
                }
            }
        }
    }

    /* TODO: Take a note that there is a potential problem with input processor when a few input
    *   processors come (Not only the keyboard)
    * */
    public void assignPlayerToInputProcessor(int playerID, boolean isLocalPlayer)
    {
        /* This sh...ty code will reside here for a while
        *  Problem: Iteration through all HashMap elements, and assiging the same id to all of them
        *           will cause Overwritting value field of all Hashmap elements.
        *           So far, this is just a keyboard, the only one element in a hashmap
        *  TODO:    In the future, there will be more input processors (Keyboard, Joypad, ...)
        *           so the method should be expanded with input parameter that represents the input
        *           processor.
        * */
        boolean isPlayerAssigend = false;

        if(isLocalPlayer)
        {
            for (HashMap.Entry<InputAdapterWrapper, Integer> entry : hmpInputprocessor.entrySet())
            {
                Integer value = entry.getValue();
                entry.setValue(playerID);
                InputAdapterWrapper key = entry.getKey();

                if(!key.isInputProcesorSet())
                {
                    Gdx.input.setInputProcessor(key);
                    key.setInputProcesorSet(true);
                }
                isPlayerAssigend = true;
            }
        }

        if(!isPlayerAssigend)
        {
            logger.error("Player is not assigned");
        }
    }
}
