package com.mygdx.game;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.client.ClientHandler;
import com.mygdx.game.client.ConnectScreen;
import com.mygdx.game.client.data.GeneralInfoContainer;
import com.mygdx.game.common.assets.AssetDescriptors;
import com.mygdx.game.common.assets.AssetManagmentHandler;
import com.mygdx.game.config.GameConfig;
import com.mygdx.game.entitycomponentsystem.system.RenderingSystem;
import com.mygdx.game.gameworld.GameWorld;
import com.mygdx.game.gameworld.GameWorldCreator;
import com.mygdx.game.gameworld.TileMapHandler;
import com.mygdx.game.screens.ConnectionTypeScreen;
import com.mygdx.game.screens.GameScreen;
import com.mygdx.game.screens.loadingScreens.LoadingIntroScreen;
import com.mygdx.game.screens.menuScreens.DifficultyScreen;
import com.mygdx.game.screens.menuScreens.MenuScreen;
import com.mygdx.game.screens.menuScreens.ModeSelectionScreen;
import com.mygdx.game.screens.menuScreens.OptionsScreen;
import com.mygdx.game.screens.menuScreens.RoomsScreen;
import com.mygdx.game.utils.ScreenOrientation;

public class MyGdxGame extends Game {

	private static final Logger logger = new Logger(MyGdxGame.class.getName(), Logger.INFO);

	public final static int MENU_SCREEN 			= 0;
	public final static int GAME_SCREEN 			= 1;
	public final static int OPTIONS_SCREEN 			= 2;
	public final static int DIFFICULTY_SCREEN 		= 3;
	public final static int LOADING_INTRO_SCREEN 	= 4;
	public final static int MODE_SELECTION_SCREEN 	= 5;
	public final static int CONNECT_SCREEN			= 6;
	public final static int CONNECTION_TYPE_SCREEN	= 7;
	public final static int ROOMS_SCREEN			= 8;

	/* List of screen references */
	private MenuScreen 				menuScreen;
	private GameScreen 				gameScreen;
	private OptionsScreen 			optionsScreen;
	private DifficultyScreen 		difficultyScreen;
	private LoadingIntroScreen 		loadingIntroScreen;
	private ModeSelectionScreen 	modeSelectionScreen;
	private ConnectScreen			connectScreen;
	private ConnectionTypeScreen	connectionTypeScreen;
	private RoomsScreen 			roomsScreen;

	/*Class Members*/
	private SpriteBatch				batch;
	private AssetManagmentHandler 	assetManagmentHandler;
	private Viewport 				viewport;
	private TileMapHandler			tileMapHandler;
	private GameWorldCreator 		gameWorldCreator;
	private GameWorld				gameWorld;
	private InputMultiplexer 		InputMultiplexer;
	private PooledEngine 			pooledEngine;
	private static MyGdxGame 		instance;

	private Skin					uiSkin;
	private TextureAtlas			uiAtlas;
	private Skin					uiInGameSkin;

	private ClientHandler clientHandler = null;
	private boolean connectionType;
	private String gameMode;
	private ScreenOrientation screenOrientation;

	private GeneralInfoContainer generalInfoContainer;
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

		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		this.InputMultiplexer = new InputMultiplexer();
		this.tileMapHandler = TileMapHandler.getInstance(GameConfig.LEVEL1);
		this.gameWorld  = new GameWorld(tileMapHandler.getTiledMap());
		//this.viewport	= new StretchViewport(GameConfig.VIRTUAL_WIDTH ,GameConfig.VIRTUAL_HEIGHT);
		this.viewport	= new StretchViewport(566,566);
		this.batch = new SpriteBatch();
		this.assetManagmentHandler = new AssetManagmentHandler();

		this.pooledEngine = new PooledEngine();
		this.gameWorldCreator = GameWorldCreator.getInstance();
		this.gameWorldCreator.setAssetManagementHandler(this.assetManagmentHandler);
		this.gameWorldCreator.setGameWorld(this.gameWorld);
		this.gameWorldCreator.setUiSkin(this.uiSkin);
		this.generalInfoContainer = new GeneralInfoContainer();

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
			case MODE_SELECTION_SCREEN:
				if(modeSelectionScreen == null) modeSelectionScreen = new ModeSelectionScreen(this);
				this.setScreen(modeSelectionScreen);
				break;
			case CONNECT_SCREEN:
				if(connectScreen == null) connectScreen = new ConnectScreen(this);
				this.setScreen(connectScreen);
				break;
			case CONNECTION_TYPE_SCREEN:
				if(connectionTypeScreen == null) connectionTypeScreen = new ConnectionTypeScreen(this);
				this.setScreen(connectionTypeScreen);
				break;
			case ROOMS_SCREEN:
				if(roomsScreen == null) roomsScreen = new RoomsScreen(this);
				this.setScreen(roomsScreen);
				break;
		}
	}

	public TileMapHandler getTileMapHandler() {
		return tileMapHandler;
	}

	public GameWorldCreator getWorldCreator() {
		return gameWorldCreator;
	}

	public GameWorld getGameWorld() {
		return gameWorld;
	}

	public PooledEngine getPooledEngine() {
		return pooledEngine;
	}

	public ClientHandler getClientHandler() {
		return clientHandler;
	}

	public void setClientHandler()
	{
		this.clientHandler = ClientHandler.getInstance(pooledEngine, this);
	}

	public void setConnectionType(boolean connectionType) {
		this.connectionType = connectionType;
	}

	public boolean getConnectionType() {
		return connectionType;
	}

	public GameWorldCreator getGameWorldCreator() {
		return gameWorldCreator;
	}

	public Viewport getViewport() {
		return viewport;
	}

	public ScreenOrientation getScreenOrientation() {
		return screenOrientation;
	}

	public void setScreenOrientation(ScreenOrientation screenOrientation) {
		this.screenOrientation = screenOrientation;
	}

	@Override
	public void dispose()
	{
		assetManagmentHandler.getAssetManager().dispose();
		batch.dispose();
	}

	public Skin getUiSkin() {
		return uiSkin;
	}

	public void setUiSkin(Skin uiSkin) {
		this.uiSkin = uiSkin;
		this.gameWorldCreator.setUiSkin(this.uiSkin);
	}

	public com.badlogic.gdx.InputMultiplexer getInputMultiplexer() {
		return InputMultiplexer;
	}

	public Skin getUiInGameSkin() {
		return uiInGameSkin;
	}

	public void setUIAtlas(TextureAtlas uiAtlas) {
		this.uiAtlas = uiAtlas;
	}

	public TextureAtlas getUIAtlas() {
		return this.uiAtlas;
	}

	public void setUiCharacterAtlas()
	{
		this.gameWorldCreator.setUiCharacterStatsAtlas(this.assetManagmentHandler.getResources(AssetDescriptors.UI_CHARACTER_STATS));
	}

	public String getGameMode() {
		return gameMode;
	}

	public void setGameMode(String gameMode) {
		this.gameMode = gameMode;
	}

	public GeneralInfoContainer getGeneralInfoContainer() {
		return generalInfoContainer;
	}

	public void setGeneralInfoContainer(GeneralInfoContainer generalInfoContainer) {
		this.generalInfoContainer = generalInfoContainer;
	}
}
