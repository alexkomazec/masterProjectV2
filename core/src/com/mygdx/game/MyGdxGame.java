package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.common.assets.AssetManagmentHandler;
import com.mygdx.game.screens.loadingScreens.LoadingIntroScreen;

public class MyGdxGame extends Game {

	/*Class Members*/
	private SpriteBatch				batch;
	private AssetManagmentHandler 	assetManagmentHandler;
	private static MyGdxGame 		instance = null;

	/*Class Methods*/

	private MyGdxGame()
	{}

	/*getInstance returns the new allocated space for object of the class or return the current
	* allocated spaced. Only one instance of the class can exist at the same time
	*/
	public static MyGdxGame getInstance()
	{
		if (instance == null)
		{
			instance = new MyGdxGame();
		}
		return instance;
	}

	@Override
	public void create() {

		Gdx.app.setLogLevel(Gdx.app.LOG_DEBUG);

		//Initilazing world
		//box2dWorld = Box2dWorld.getInstance();
		//box2dWorld.getWorld().setContactListener(new WorldContactListener());

		batch = new SpriteBatch();
		//ViewPortConfiguration.setupPhysicalSize();
		assetManagmentHandler = new AssetManagmentHandler();

		setScreen(new LoadingIntroScreen(this));
	}

	public AssetManagmentHandler getAssetManagmentHandler()
	{
		return assetManagmentHandler;
	}

	@Override
	public void dispose()
	{
		assetManagmentHandler.getAssetManager().dispose();
		batch.dispose();
	}

	//public Box2dWorld getBox2dWorld()
	// {
	//	return box2dWorld;
	//}

	public SpriteBatch getBatch()
	{
		return batch;
	}

	/*Note: Private constructor, and getInstance provide Singleton pattern*/
}
