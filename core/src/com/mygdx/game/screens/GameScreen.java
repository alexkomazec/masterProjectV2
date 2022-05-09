package com.mygdx.game.screens;


import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Logger;
import com.mygdx.game.screens.menuScreens.MenuScreen;

public class GameScreen implements Screen {

    private static final String CLASS_NAME  = MenuScreen.class.getSimpleName();
    private static final Logger log         = new Logger(CLASS_NAME, Logger.DEBUG);

    @Override
    public void show() {
        log.debug("I am in game screen!");
    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
