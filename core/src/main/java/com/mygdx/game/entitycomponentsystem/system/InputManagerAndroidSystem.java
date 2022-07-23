package com.mygdx.game.entitycomponentsystem.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.AndroidController;
import com.mygdx.game.entitycomponentsystem.components.ControlledInputComponent;
import com.mygdx.game.entitycomponentsystem.components.LocalInputComponent;
import com.mygdx.game.entitycomponentsystem.components.PlayerComponent;

public class InputManagerAndroidSystem extends IteratingSystem {

    AndroidController androidController;

    public InputManagerAndroidSystem(SpriteBatch batch, Viewport viewport, InputMultiplexer inputMultiplexer)
    {
        super(Family.all(PlayerComponent.class, LocalInputComponent.class).get());
        this.androidController = new AndroidController(batch, viewport, inputMultiplexer);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        ControlledInputComponent cntrlInComp    = entity.getComponent(ControlledInputComponent.class);

        for (int index = 0; index < cntrlInComp.abInputCommandList.length; index++) {

            boolean plInputCommand =  cntrlInComp.abInputCommandList[index];
            boolean inputProcInputCommand = this.androidController.getInputCommand(index);
            if(plInputCommand != inputProcInputCommand)
            {
                cntrlInComp.abInputCommandList[index] = inputProcInputCommand;
                cntrlInComp.newInputHappend = true;
            }
        }
    }

    public AndroidController getAndroidController() {
        return androidController;
    }
}
