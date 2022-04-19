package com.akomazec.BaseSampleProject.Screens;

import com.akomazec.BaseSampleProject.BaseSampleProject;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;

public class ConnectToServer extends ScreenAdapter
{
    private Stage stage;
    public ConnectToServer(BaseSampleProject game)
    {
        stage = new Stage(new ScreenViewport());
    }
    
    @Override
    public void show() 
    {
        VisUI.load(VisUI.SkinScale.X1);
        Gdx.input.setInputProcessor(stage);
        stage.addActor(new LoginScreenForm());
        System.out.println("Show screen, LoginScreen");
    }

    @Override
	public void render(float delta)
    {
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		stage.draw();
    }

}
