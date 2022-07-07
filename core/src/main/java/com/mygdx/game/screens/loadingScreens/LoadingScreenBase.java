package com.mygdx.game.screens.loadingScreens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.common.ViewPortConfiguration;
import com.mygdx.game.common.assets.AssetManagmentHandler;
import com.mygdx.game.config.GameConfig;
import com.mygdx.game.utils.GdxUtils;

import javax.swing.text.View;


public class LoadingScreenBase extends ScreenAdapter {


    // == constants ==
    protected static final Logger logger = new Logger(LoadingIntroScreen.class.getSimpleName(), Logger.INFO);

    protected static final float PROGRESS_BAR_WIDTH = ViewPortConfiguration.getPhysicalWidth() / 2f; // world units
    protected static final float PROGRESS_BAR_HEIGHT = 60; // world units

    // == attributes ==
    protected OrthographicCamera camera;
    protected Viewport viewport;
    protected ShapeRenderer renderer;

    protected float progress;
    protected float waitTime = 0.75f;
    protected boolean changeScreen;

    protected final MyGdxGame game;
    protected final AssetManagmentHandler assetManager;

    // == constructors ==
    public LoadingScreenBase(MyGdxGame game) {
        this.game = game;
        assetManager = game.getAssetManagmentHandler();
    }

    // == public methods ==
    @Override
    public void show() {

        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(
                false,
                ViewPortConfiguration.getPhysicalWidth(),
                ViewPortConfiguration.getPhysicalHeight()
        );

        this.viewport = new ScreenViewport(camera);
        this.renderer = new ShapeRenderer();

    }

    @Override
    public void render(float delta) {
        update(delta);

        GdxUtils.clearScreen();

        /*Apply viewport dimensions to camera*/
        this.viewport.apply();

        this.renderer.setProjectionMatrix(this.camera.combined);
        this.renderer.begin(ShapeRenderer.ShapeType.Filled);

        draw();

        this.renderer.end();
    }

    @Override
    public void resize(int width, int height) {
        this.viewport.update(width, height, true);
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        this.renderer.dispose();
        this.renderer = null;
    }

    // == private methods ==
    private void update(float delta) {
        // progress is between 0 and 1
        this.progress = this.assetManager.getProgress();

        // update returns true when all assets are loaded
        if(this.assetManager.updateAssetLoading()) {
            this.waitTime -= delta;

            if(this.waitTime <= 0) {
                this.changeScreen = true;
            }
        }
    }

    private void draw() {
        float progressBarX = (ViewPortConfiguration.getPhysicalWidth() - PROGRESS_BAR_WIDTH) / 2f;
        float progressBarY = (ViewPortConfiguration.getPhysicalWidth() - PROGRESS_BAR_HEIGHT) / 2f;

        this.renderer.rect(progressBarX, progressBarY,
                this.progress * PROGRESS_BAR_WIDTH, PROGRESS_BAR_HEIGHT
        );
    }

}
