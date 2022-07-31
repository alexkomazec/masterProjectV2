package com.mygdx.game.entitycomponentsystem.system;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class RenderCharacterHudSystem extends EntitySystem {

    Stage characterHudStage;

    public RenderCharacterHudSystem(Stage stage)
    {
        this.characterHudStage = stage;
    }
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        this.characterHudStage.draw();
    }
}
