package com.mygdx.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class EndMatchPanel extends Panel{

    private TextButton quit;
    private TextButton exit;
    private Label label;
    public EndMatchPanel.Listener listener;

    public interface Listener{
        void exit();
        void quit();
    }

    public EndMatchPanel(final EndMatchPanel.Listener listener, Skin skin){
        super(skin);
        this.listener =  listener;

        label = new Label("GAME OVER",skin,"default");
        label.setColor(Color.BROWN);

        quit = new TextButton("QUIT",skin,"default");
        quit.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                listener.quit();
            }
        });

        exit = new TextButton("EXIT",skin,"default");
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
        root.add(quit).pad(padA);
        root.row();
        root.add(exit).pad(padA).padBottom(padD);
        root.pad(padB);
        root.pack();
        root.setTransform(true);
        root.setOrigin(Align.center);

        float size=Math.max(root.getWidth(), root.getHeight());

        add(root).center().size(size);
    }

    public void setLabelText(String labelString) {
        this.label.setText(labelString);
    }
}
