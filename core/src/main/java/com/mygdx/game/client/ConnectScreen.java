package com.mygdx.game.client;

import static com.mygdx.game.MyGdxGame.GAME_SCREEN;
import static com.mygdx.game.MyGdxGame.MODE_SELECTION_SCREEN;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.kotcrab.vis.ui.VisUI;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.client.forms.LoginForm;
import com.mygdx.game.client.forms.RegisterForm;
import com.mygdx.game.common.assets.AssetDescriptors;
import com.mygdx.game.config.GameConfig;
import com.mygdx.game.utils.ScreenOrientation;

public class ConnectScreen extends ScreenAdapter
{
    private Stage stage;
    private final MyGdxGame game;
    private LoginForm loginScreenForm;
    private RegisterForm registerForm;
    private boolean readyToChangeScreen = false;
    Table table;
    public ConnectScreen(MyGdxGame game)
    {
        this.game = game;
        this.table = new Table();
        TextureAtlas background = game.getAssetManagmentHandler().getResources(AssetDescriptors.BACK_GROUND);
        TextureRegion textureRegion = background.findRegion(GameConfig.BACKGROUND);

        this.table.setBackground(new TextureRegionDrawable(textureRegion));
        ScreenOrientation screenOrientation = this.game.getScreenOrientation();
        if(screenOrientation != null)
        {
            this.game.getScreenOrientation().setScreenToLandscape();
            VisUI.load(Gdx.files.internal("ui/VisUI/uiskin.json"));
        }

        this.stage = new Stage(new StretchViewport(800,400));
        loginScreenForm = new LoginForm(this.game, this);
        //stage.addActor(loginScreenForm);
        table.add(loginScreenForm);
        table.center();
        table.setFillParent(true);
        table.pack();

        stage.addActor(this.table);
    }

    @Override
    public void show() 
    {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
	public void render(float delta)
    {
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		stage.draw();

		if(this.readyToChangeScreen)
		{
            this.readyToChangeScreen = false;
            game.changeScreen(MODE_SELECTION_SCREEN);
        }
    }

    @Override
    public void resize (int width, int height) {
        this.stage.getViewport().update(width,height,true);
    }

    public RegisterForm getRegisterForm() {
        return registerForm;
    }

    public void setRegisterForm() {
        this.registerForm = new RegisterForm(this.game);
    }

    public void setReadyToChangeScreen(boolean readyToChangeScreen) {
        this.readyToChangeScreen = readyToChangeScreen;
    }

}
