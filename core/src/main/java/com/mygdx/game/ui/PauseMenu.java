package com.mygdx.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;


public class PauseMenu extends Panel {

    private TextButton resume;
    private TextButton settings;
    private TextButton exit;
    private Label label;
    public Listener listener;

    public interface Listener{
        void exit();
        void quit();
        void resume();
    }

    public  PauseMenu(final Listener listener1, Skin skin){
        super(skin);
        this.listener = listener1;

        label = new Label("pause",skin,"header");
        label.setColor(Color.BROWN);

        resume = new TextButton("resume",skin,"longBrown");
        //resume.addListener(UI_Utils.clickSound());
        resume.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                listener.resume();
            }
        });

        settings = new TextButton("quit",skin,"longBrown");
        //settings.addListener(UI_Utils.clickSound());
        settings.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                listener.quit();
            }
        });


        exit = new TextButton("exit",skin,"longNegative");
        //exit.addListener(UI_Utils.clickSound());
        exit.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                listener.exit();
            }
        });

        float padA = 20;
        float padB = 0;
        float padC = 30;
        float padD = 70;
        root.add(label).pad(padA).padBottom(padC).padTop(padD);
        root.row();
        root.add(resume).pad(padA);
        root.row();
        root.add(settings).pad(padA);
        root.row();
        root.add(exit).pad(padA).padBottom(padD);
        root.pad(padB);
        root.pack();
        root.setTransform(true);
        root.setOrigin(Align.center);

        float size=Math.max(root.getWidth(), root.getHeight());

        add(root).center().size(size);
    }
}