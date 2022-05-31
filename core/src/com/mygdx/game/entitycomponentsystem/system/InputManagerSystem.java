package com.mygdx.game.entitycomponentsystem.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.mygdx.game.KeyboardController;
import com.mygdx.game.common.InputAdapterWrapper;
import com.mygdx.game.entitycomponentsystem.components.LocalInputComponent;
import com.mygdx.game.entitycomponentsystem.components.PlayerComponent;

import java.util.HashMap;

public class InputManagerSystem extends IteratingSystem {

    /*There will be stored all needed input processors like keyboard, controller...*/
    private final HashMap<InputAdapterWrapper, Integer> hmpInputprocessor;

    private static final int UNASSIGNED_INPUT_PROCESSOR = 999;
    public InputManagerSystem() {
        super(Family.all(PlayerComponent.class, LocalInputComponent.class).get());
        hmpInputprocessor = new HashMap<>();

        hmpInputprocessor.put(new KeyboardController(),UNASSIGNED_INPUT_PROCESSOR);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PlayerComponent playerComponent = entity.getComponent(PlayerComponent.class);
        int playerID                    = playerComponent.playerID;
        HashMap.Entry<InputAdapterWrapper, Integer> localEntry = null;

        /* Iterate through HashMap to find a specified Input processor that player */
        for (HashMap.Entry<InputAdapterWrapper, Integer> tempEntry : hmpInputprocessor.entrySet())
        {
            Integer value = tempEntry.getValue();

            if(value.intValue() == playerID)
            {
                localEntry = tempEntry;
                break;
            }
        }

        /* if there is non-null localEntry it means , that playerID has been found in hash Map*/
        if(localEntry != null)
        {
            InputAdapterWrapper key = localEntry.getKey();
            /* Iterate through all input commands */
            for (int index = 0; index < playerComponent.abInputCommandList.length; index++) {

                boolean plInputCommand = playerComponent.abInputCommandList[index];
                boolean inputProcInputCommand = key.getInputCommand(index);
                if(plInputCommand != inputProcInputCommand)
                {
                    playerComponent.abInputCommandList[index] = inputProcInputCommand;
                }
            }
        }
    }

    /* TODO: Take a note that there is a potential problem with input processor when a few input
    *   processors come (Not only the keyboard)
    * */
    public void assignPlayerToInputProcessor(int playerID)
    {
        boolean isPlayerAssigend = false;
        for (HashMap.Entry<InputAdapterWrapper, Integer> entry : hmpInputprocessor.entrySet()) {
            Integer value = entry.getValue();
            if(value == UNASSIGNED_INPUT_PROCESSOR)
            {
                entry.setValue(playerID);
                InputProcessor key = entry.getKey();
                Gdx.input.setInputProcessor(key);
                isPlayerAssigend = true;
            }
        }

        if(!isPlayerAssigend)
        {
            System.out.println("Error: Player is not assigned");
        }
    }
}
