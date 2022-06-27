package com.mygdx.game.entitycomponentsystem.system;

import com.badlogic.ashley.core.EntitySystem;
import com.mygdx.game.AndroidController;

/** This system is reliable for rendering android specific stuff*/
public class RenderAndroidSystem extends EntitySystem {

    AndroidController androidController;

    public RenderAndroidSystem(AndroidController androidController)
    {
        this.androidController = androidController;
    }

    public void renderAndroidStuff()
    {
        this.androidController.draw();
    }

    @Override
    public void update(float deltaTime) {
        renderAndroidStuff();
    }
}
