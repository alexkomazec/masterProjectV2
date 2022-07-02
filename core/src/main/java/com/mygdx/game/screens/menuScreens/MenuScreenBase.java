package com.mygdx.game.screens.menuScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.common.GameManager;
import com.mygdx.game.common.ViewPortConfiguration;
import com.mygdx.game.common.assets.AssetDescriptors;
import com.mygdx.game.common.assets.AssetManagmentHandler;
import com.mygdx.game.utils.GdxUtils;

public abstract class MenuScreenBase extends ScreenAdapter {

    // == members == //

    //Instance of a game
    protected final MyGdxGame game;

    //An asset manager
    protected final AssetManagmentHandler assetManager;

    //Viewport
    private Viewport viewport;

    //Stage
    private Stage stage;

    protected Music music;
    protected Sound sound;

    // == constructor == //
    public MenuScreenBase(MyGdxGame game) {
        this.game = game;
        this.assetManager = game.getAssetManagmentHandler();
        this.music = assetManager.getResource(AssetDescriptors.BACKGROUND_MUSIC);
        this.sound = assetManager.getResource(AssetDescriptors.CLICK_SOUND);
    }

    // == public methods == //
    @Override
    public void show() {
        this.viewport = game.getViewport();
        stage = new Stage(viewport, game.getBatch());
        //stage.setDebugAll(true);
        Gdx.input.setInputProcessor(stage);

        stage.addActor(createUi());

        playPauseBckMusic();
    }

    private void playPauseBckMusic()
    {
        if(GameManager.INSTANCE.isGameMusic()){
            music.play();
        }
        else{
            music.pause();
        }
    }

    protected void CheckAndPlayMenuSound(){
        if(GameManager.INSTANCE.isGameSound()){
            sound.play();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width,height,true);
    }

    @Override
    public void render(float delta) {
        GdxUtils.clearScreen();

        stage.act();
        stage.draw();
    }

    @Override
    public void hide() {
       dispose();
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    // == protected methods == //
    protected abstract Actor createUi();

    protected void back() {
        game.changeScreen(MyGdxGame.MENU_SCREEN);
    }

}
