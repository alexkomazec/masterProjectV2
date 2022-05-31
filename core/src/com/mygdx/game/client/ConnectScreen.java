package com.mygdx.game.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.mygdx.game.MyGdxGame;

public class ConnectScreen extends ScreenAdapter
{
    /*private Stage stage;
    private final MyGdxGame game;
    private LoginScreenForm loginScreenForm;

    public ConnectScreen(BaseSampleProject game)
    {
        this.game = game;
    }
    
    @Override
    public void show() 
    {
        this.stage = new Stage(new ScreenViewport());
        VisUI.load(VisUI.SkinScale.X1);
        Gdx.input.setInputProcessor(stage);
        loginScreenForm = new LoginScreenForm(this.game);
        stage.addActor(loginScreenForm);
    }

    @Override
	public void render(float delta)
    {
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		stage.draw();

		if(loginScreenForm.readyToChangeScreen)
		{
            game.setScreen(new MainScreen(game));
        }
    }*/

}
