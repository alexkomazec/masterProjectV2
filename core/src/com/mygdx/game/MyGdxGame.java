package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Logger;
import com.mygdx.game.common.assets.AssetManagmentHandler;
import com.mygdx.game.screens.GameScreen;
import com.mygdx.game.screens.loadingScreens.LoadingIntroScreen;
import com.mygdx.game.screens.menuScreens.DifficultyScreen;
import com.mygdx.game.screens.menuScreens.MenuScreen;
import com.mygdx.game.screens.menuScreens.OptionsScreen;

public class MyGdxGame extends Game {

	private static final Logger log = new Logger(MyGdxGame.class.getName(), Logger.DEBUG);

	public final static int MENU_SCREEN 			= 0;
	public final static int GAME_SCREEN 			= 1;
	public final static int OPTIONS_SCREEN 			= 2;
	public final static int DIFFICULTY_SCREEN 		= 3;
	public final static int LOADING_INTRO_SCREEN 	= 4;

	/* List of screen references */
	private MenuScreen 			menuScreen;
	private GameScreen 			gameScreen;
	private OptionsScreen 		optionsScreen;
	private DifficultyScreen 	difficultyScreen;
	private LoadingIntroScreen 	loadingIntroScreen;


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

		changeScreen(MyGdxGame.LOADING_INTRO_SCREEN);
	}

	public AssetManagmentHandler getAssetManagmentHandler()
	{
		return assetManagmentHandler;
	}

	public SpriteBatch getBatch()
	{
		return batch;
	}

	public void changeScreen(int screen){
		switch(screen){
			case MENU_SCREEN:
				if(menuScreen == null) menuScreen = new MenuScreen(this);
				this.setScreen(menuScreen);
				break;
			case GAME_SCREEN:
				if(gameScreen == null) gameScreen = new GameScreen();
				this.setScreen(gameScreen);
				break;
			case OPTIONS_SCREEN:
				if(optionsScreen == null) optionsScreen = new OptionsScreen(this);
				this.setScreen(optionsScreen);
				break;
			case DIFFICULTY_SCREEN:
				if(difficultyScreen == null) difficultyScreen = new DifficultyScreen(this);
				this.setScreen(difficultyScreen);
				break;
			case LOADING_INTRO_SCREEN:
				if(loadingIntroScreen == null) loadingIntroScreen = new LoadingIntroScreen(this);
				this.setScreen(loadingIntroScreen);
				break;
		}
	}

	@Override
	public void dispose()
	{
		assetManagmentHandler.getAssetManager().dispose();
		batch.dispose();
	}
}
