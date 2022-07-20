package com.mygdx.game.entitycomponentsystem.components;

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
    public Image lifeImage;
    public Label hpValLabel;
    public Table table;

    public void init(TextureAtlas textureAtlas, Skin skin, float x, float y, Stage stage)
    {
        this.lifeImage = new Image(textureAtlas.findRegion("heart"));
        this.lifeImage.setSize(35,35);
        this.hpValLabel = new Label(String.valueOf(remainingLives), skin);

        table = new Table();
        table.addActor(this.lifeImage);
        table.bottom();
        table.padLeft(33).add(hpValLabel);

        stage.addActor(table);
    }

    public void refreshPosition(float x, float y)
    {
        table.setPosition(x-20,y+20);
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
