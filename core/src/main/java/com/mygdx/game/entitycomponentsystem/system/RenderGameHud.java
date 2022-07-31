package com.mygdx.game.entitycomponentsystem.system;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class RenderGameHud extends EntitySystem {

    Stage gameHudStage;

    public RenderGameHud(Stage stage)
    {
        this.gameHudStage = stage;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        this.gameHudStage.draw();
    }
}
