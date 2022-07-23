package com.mygdx.game.ui;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.I18NBundle;

public class Panel extends Table {

    protected Table root;
    protected Skin skin;

    public Panel(Skin skin){
        this.skin = skin;
        this.setFillParent(true);
        //this.setBackground(skin.getDrawable("box1"));
        //this.setTouchable(Touchable.enabled);
        //this.setVisible(false);

        root = new Table();
        root.setBackground(skin.getDrawable("box1"));
        root.setTransform(true);
    }

    @Override
    public void setScale(float scaleX, float scaleY) {
        super.setScale(scaleX, scaleY);
        root.setScale(scaleX, scaleY);
    }

    public void show(){
        this.setVisible(true);
    }
    public void hide(){
        this.setVisible(false);
    }
}
