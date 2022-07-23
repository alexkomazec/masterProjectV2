package com.mygdx.game.entitycomponentsystem.components;

import static com.mygdx.game.entitycomponentsystem.system.RenderingSystem.PPM;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

public class CharacterStatsComponent implements Component
{
    public int remainingLives = 3;
    public Label hpValLabel;
    public Table table;

    public void init(TextureAtlas textureAtlas, Skin skin, Stage stage)
    {
        Image lifeImage = new Image(textureAtlas.findRegion("heart"));
        lifeImage.setSize(PPM/2,PPM/2);
        this.hpValLabel = new Label(String.valueOf(remainingLives), skin);

        table = new Table();
        table.addActor(lifeImage);
        table.bottom();
        table.padLeft(PPM/2).add(hpValLabel);

        stage.addActor(table);
    }

    public void refreshPosition(float x, float y)
    {
        table.setPosition(x-PPM/2,y+PPM/2);
    }

    public void refreshLives(int remainingLives)
    {
        this.remainingLives = remainingLives;
        hpValLabel.setText(this.remainingLives);
    }

    public void removeTable()
    {
        this.table.setVisible(false);
    }
}
