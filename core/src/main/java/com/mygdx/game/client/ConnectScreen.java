package com.mygdx.game.client;

import static com.mygdx.game.MyGdxGame.GAME_SCREEN;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.kotcrab.vis.ui.VisUI;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.client.forms.LoginForm;
import com.mygdx.game.client.forms.RegisterForm;
import com.mygdx.game.utils.ScreenOrientation;

public class ConnectScreen extends ScreenAdapter
{
    private Stage stage;
    private final MyGdxGame game;
    private LoginForm loginScreenForm;
    private RegisterForm registerForm;
    private boolean readyToChangeScreen = false;

    public ConnectScreen(MyGdxGame game)
    {
        this.game = game;
        ScreenOrientation screenOrientation = this.game.getScreenOrientation();
        if(screenOrientation != null)
        {
            this.game.getScreenOrientation().setScreenToLandscape();
            VisUI.load(VisUI.SkinScale.X1);
        }
    }
    
    @Override
    public void show() 
    {
        this.stage = new Stage(new StretchViewport(450,200));
        loginScreenForm = new LoginForm(this.game, this);
        stage.addActor(loginScreenForm);
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
            game.changeScreen(GAME_SCREEN);
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
