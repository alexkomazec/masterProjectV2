package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.config.GameConfig;


public class AndroidController {

    private Stage stage;

    protected boolean[] abInputCommandList;
    protected boolean inputProcesorSet;

    public AndroidController(SpriteBatch batch, Viewport viewport)
    {
        this.abInputCommandList = new boolean[GameConfig.LIST_COMMANDS_MAX];
        inputProcesorSet = false;

        stage = new Stage(viewport, batch);
        Gdx.input.setInputProcessor(stage);
        Table table = new Table();
        table.left().bottom();

        Table table1 = new Table();
        table1.left();


        Image upImg = new Image(new Texture("flatDark25.png"));
        upImg.setSize(100, 100);
        upImg.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
            {
                abInputCommandList[GameConfig.UP] = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
            {
                abInputCommandList[GameConfig.UP] = false;
            }
        });

        Image downImg = new Image(new Texture("flatDark26.png"));
        downImg.setSize(100, 100);
        downImg.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                abInputCommandList[GameConfig.DOWN] = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                abInputCommandList[GameConfig.DOWN] = false;
            }
        });

        Image rightImg = new Image(new Texture("flatDark24.png"));
        rightImg.setSize(100, 100);
        rightImg.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                abInputCommandList[GameConfig.RIGHT] = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                abInputCommandList[GameConfig.RIGHT] = false;
            }
        });

        Image leftImg = new Image(new Texture("flatDark23.png"));
        leftImg.setSize(100, 100);
        leftImg.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                abInputCommandList[GameConfig.LEFT] = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                abInputCommandList[GameConfig.LEFT] = false;
            }
        });

        Image fireImg = new Image(new Texture("flatDark27.png"));
        fireImg.setSize(100, 100);
        fireImg.addListener(new InputListener()
        {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                abInputCommandList[GameConfig.SPACE] = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                abInputCommandList[GameConfig.SPACE] = false;
            }
        });

        table.add(leftImg).size(leftImg.getWidth(), leftImg.getHeight());
        table.add(rightImg).size(rightImg.getWidth(), rightImg.getHeight());
        table.row();
        table.add().size(upImg.getWidth(), upImg.getHeight());

        table1.row().padBottom(110);
        table1.add().size(upImg.getWidth(), upImg.getHeight());
        table1.add().size(upImg.getWidth(), upImg.getHeight());
        table1.add().size(upImg.getWidth(), upImg.getHeight());
        table1.add().size(upImg.getWidth(), upImg.getHeight());
        table1.add(upImg).size(upImg.getWidth(), upImg.getHeight());
        table1.add(fireImg).size(fireImg.getWidth(), fireImg.getHeight());


        stage.addActor(table1);
        stage.addActor(table);
    }

    public boolean getInputCommand(int inputCommandID) {
        return abInputCommandList[inputCommandID];
    }

    public void draw(){
        stage.draw();
    }

}
