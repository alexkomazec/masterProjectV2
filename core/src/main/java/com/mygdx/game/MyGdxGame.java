package com.mygdx.game;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.client.ClientHandler;
import com.mygdx.game.client.ConnectScreen;
import com.mygdx.game.client.data.GeneralInfoContainer;
import com.mygdx.game.common.Observer;
import com.mygdx.game.common.Publisher;
import com.mygdx.game.common.Topics;
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

public class MyGdxGame extends Game implements Publisher {

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

	private Stack<Screen> 			stackScreen = new Stack<>();

	HashMap<Integer, Screen>		screensMap;
	/*Class Members*/
	private SpriteBatch				batch;
	private AssetManagmentHandler 	assetManagmentHandler;
	private Viewport 				viewport;
	private TileMapHandler			tileMapHandler;
	private GameWorldCreator 		gameWorldCreator;
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

	/* List of different observers */
	private Array<Observer> onlineMatchInitObservers;
	private Array<Observer> updateRoomsStateObservers;
	private Array<Observer> playerLeaveRoomObservers;

	/*Class Methods*/

	private MyGdxGame()
	{
		screensMap = new HashMap<>();
		screensMap.put(MENU_SCREEN, null);
		screensMap.put(GAME_SCREEN, null);
		screensMap.put(OPTIONS_SCREEN, null);
		screensMap.put(DIFFICULTY_SCREEN, null);
		screensMap.put(LOADING_INTRO_SCREEN, null);
		screensMap.put(MODE_SELECTION_SCREEN, null);
		screensMap.put(CONNECT_SCREEN, null);
		screensMap.put(CONNECTION_TYPE_SCREEN, null);
		screensMap.put(ROOMS_SCREEN, null);

	}

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
		//this.viewport	= new StretchViewport(GameConfig.VIRTUAL_WIDTH ,GameConfig.VIRTUAL_HEIGHT);
		this.viewport	= new StretchViewport(566,566);
		this.batch = new SpriteBatch();
		this.assetManagmentHandler = new AssetManagmentHandler();

		this.pooledEngine = new PooledEngine();
		this.gameWorldCreator = GameWorldCreator.getInstance();
		this.gameWorldCreator.setPooledEngine(pooledEngine);
		this.gameWorldCreator.setAssetManagementHandler(this.assetManagmentHandler);
		this.gameWorldCreator.setUiSkin(this.uiSkin);
		this.generalInfoContainer = new GeneralInfoContainer();

		onlineMatchInitObservers = new Array<>();
		updateRoomsStateObservers = new Array<>();
		playerLeaveRoomObservers = new Array<>();

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
		Screen tempScreen;

		switch(screen){
			case MENU_SCREEN:
				tempScreen = screensMap.get(MENU_SCREEN);
				if(tempScreen == null)
				{
					screensMap.remove(MENU_SCREEN);
					screensMap.put(MENU_SCREEN, new MenuScreen(this));
					tempScreen = screensMap.get(MENU_SCREEN);
				}
				stackScreen.push(tempScreen);
				this.setScreen(tempScreen);
				break;
			case GAME_SCREEN:
				screensMap.remove(GAME_SCREEN);
				screensMap.put(GAME_SCREEN, new GameScreen());
				tempScreen = screensMap.get(GAME_SCREEN);
				stackScreen.push(tempScreen);
				this.setScreen(tempScreen);
				break;
			case OPTIONS_SCREEN:
				tempScreen = screensMap.get(OPTIONS_SCREEN);
				if(tempScreen == null)
				{
					screensMap.remove(OPTIONS_SCREEN);
					screensMap.put(OPTIONS_SCREEN, new OptionsScreen(this));
					tempScreen = screensMap.get(OPTIONS_SCREEN);
				}
				stackScreen.push(tempScreen);
				this.setScreen(tempScreen);
				break;
			case DIFFICULTY_SCREEN:
				tempScreen = screensMap.get(DIFFICULTY_SCREEN);
				if(tempScreen == null)
				{
					screensMap.remove(DIFFICULTY_SCREEN);
					screensMap.put(DIFFICULTY_SCREEN, new DifficultyScreen(this));
					tempScreen = screensMap.get(DIFFICULTY_SCREEN);
				}
				stackScreen.push(tempScreen);
				this.setScreen(tempScreen);
				break;
			case LOADING_INTRO_SCREEN:
				tempScreen = screensMap.get(LOADING_INTRO_SCREEN);
				if(tempScreen == null)
				{
					screensMap.remove(LOADING_INTRO_SCREEN);
					screensMap.put(LOADING_INTRO_SCREEN, new LoadingIntroScreen(this));
					tempScreen = screensMap.get(LOADING_INTRO_SCREEN);
				}
				stackScreen.push(tempScreen);
				this.setScreen(tempScreen);
				break;
			case MODE_SELECTION_SCREEN:
				tempScreen = screensMap.get(MODE_SELECTION_SCREEN);
				if(tempScreen == null)
				{
					screensMap.remove(MODE_SELECTION_SCREEN);
					screensMap.put(MODE_SELECTION_SCREEN, new ModeSelectionScreen(this));
					tempScreen = screensMap.get(MODE_SELECTION_SCREEN);
				}
				stackScreen.push(tempScreen);
				this.setScreen(tempScreen);
				break;
			case CONNECT_SCREEN:
				tempScreen = screensMap.get(CONNECT_SCREEN);
				if(tempScreen == null)
				{
					screensMap.remove(CONNECT_SCREEN);
					screensMap.put(CONNECT_SCREEN, new ConnectScreen(this));
					tempScreen = screensMap.get(CONNECT_SCREEN);
				}
				stackScreen.push(tempScreen);
				this.setScreen(tempScreen);
				break;
			case CONNECTION_TYPE_SCREEN:
				tempScreen = screensMap.get(CONNECTION_TYPE_SCREEN);
				if(tempScreen == null)
				{
					screensMap.remove(CONNECTION_TYPE_SCREEN);
					screensMap.put(CONNECTION_TYPE_SCREEN, new ConnectionTypeScreen(this));
					tempScreen = screensMap.get(CONNECTION_TYPE_SCREEN);
				}
				stackScreen.push(tempScreen);
				this.setScreen(tempScreen);
				break;
			case ROOMS_SCREEN:
				tempScreen = screensMap.get(ROOMS_SCREEN);
				if(tempScreen == null)
				{
					screensMap.remove(ROOMS_SCREEN);
					screensMap.put(ROOMS_SCREEN, new RoomsScreen(this));
					tempScreen = screensMap.get(ROOMS_SCREEN);
				}
				stackScreen.push(tempScreen);
				this.setScreen(tempScreen);
				break;
		}
	}

	public TileMapHandler getTileMapHandler() {
		return tileMapHandler;
	}

	public GameWorldCreator getWorldCreator() {
		return gameWorldCreator;
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

	@Override
	public void registerObserver(Topics topic, Observer obj)
	{
		if (topic == Topics.ONLINE_MATCH_INIT_STARTED) {
			this.onlineMatchInitObservers.add(obj);
		}
		else if(topic == Topics.UPDATE_ROOMS_STATE)
		{
			this.updateRoomsStateObservers.add(obj);
		}
		else if(topic == Topics.PLAYER_LEAVE_ROOM)
		{
			this.playerLeaveRoomObservers.add(obj);
		}
		else
		{
			logger.error("Register: Wrong topic: " + topic);
		}

	}

	@Override
	public void unregisterObserver(Topics topic, Observer obj)
	{
		if (topic == Topics.ONLINE_MATCH_INIT_STARTED) {
			onlineMatchInitObservers.removeValue(obj,true);
		}
		else if(topic == Topics.UPDATE_ROOMS_STATE)
		{
			this.updateRoomsStateObservers.add(obj);
		}
		else
		{
			logger.error("UnRegister: Wrong topic: " + topic);
		}
	}

	@Override
	public void notifyObservers(Topics topic, Object... args)
	{
		if (topic == Topics.ONLINE_MATCH_INIT_STARTED) {
			for (Observer observer:onlineMatchInitObservers)
			{
				observer.update(topic);
			}
		}
		else if( topic == Topics.UPDATE_ROOMS_STATE)
		{
			for (Observer observer:updateRoomsStateObservers)
			{
				observer.update(topic);
			}
		}
		else if( topic == Topics.PLAYER_LEAVE_ROOM)
		{
			for (Observer observer:playerLeaveRoomObservers)
			{
				observer.update(topic);
			}
		}
		else
		{
			logger.error("UnRegister: Wrong topic: " + topic);
		}
	}

	public void backOneScreen()
	{
		/* Delete the top element from the stack, it is not needed
		*  So the top element in the stack will become the previous seen screen
		* */
		stackScreen.pop();
		Screen tempScreen = stackScreen.pop();
		this.changeScreen(getKeyByValue(tempScreen));
	}

	private int getKeyByValue(Screen screen)
	{
		int tempInt = -555;
		// iterate each entry of hashmap
		for(Map.Entry<Integer, Screen> entry: this.screensMap.entrySet())
		{

			// if give value is equal to value from entry
			// print the corresponding key
			if(entry.getValue() == screen) {
				tempInt = entry.getKey();
				break;
			}
		}
		return tempInt;
	}

	public void setGameWorld(GameWorld gameWorld)
	{
		/* Send to all who need it*/
		this.gameWorldCreator.setGameWorld(gameWorld);
	}
}
