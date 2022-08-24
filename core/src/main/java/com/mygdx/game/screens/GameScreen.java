package com.mygdx.game.screens;


import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.common.B2dContactListener;
import com.mygdx.game.common.FixturePair;
import com.mygdx.game.common.Observer;
import com.mygdx.game.common.Topics;
import com.mygdx.game.common.ViewPortConfiguration;
import com.mygdx.game.common.assets.AssetDescriptors;
import com.mygdx.game.config.GameConfig;
import com.mygdx.game.entitycomponentsystem.system.AnimationSystem;
import com.mygdx.game.entitycomponentsystem.system.B2dContactSystem;
import com.mygdx.game.entitycomponentsystem.system.BulletSystem;
import com.mygdx.game.entitycomponentsystem.system.CharacterStatsSystem;
import com.mygdx.game.entitycomponentsystem.system.CollectibleBasicManagerSystem;
import com.mygdx.game.entitycomponentsystem.system.CollisionSystem;
import com.mygdx.game.entitycomponentsystem.system.DataReceivingSystem;
import com.mygdx.game.entitycomponentsystem.system.DataTransmittingSystem;
import com.mygdx.game.entitycomponentsystem.system.EnemySystem;
import com.mygdx.game.entitycomponentsystem.system.HealthManagerSystem;
import com.mygdx.game.entitycomponentsystem.system.InputManagerAndroidSystem;
import com.mygdx.game.entitycomponentsystem.system.InputManagerSystem;
import com.mygdx.game.entitycomponentsystem.system.InputManagerTransmittingSystem;
import com.mygdx.game.entitycomponentsystem.system.PhysicsDebugSystem;
import com.mygdx.game.entitycomponentsystem.system.PhysicsSystem;
import com.mygdx.game.entitycomponentsystem.system.PlayerControlSystem;
import com.mygdx.game.entitycomponentsystem.system.RenderAndroidControllerSystem;
import com.mygdx.game.entitycomponentsystem.system.RenderCharacterHudSystem;
import com.mygdx.game.entitycomponentsystem.system.CollisionEffectsSystem;
import com.mygdx.game.entitycomponentsystem.system.RenderGameHud;
import com.mygdx.game.entitycomponentsystem.system.RenderTiledMapSystem;
import com.mygdx.game.entitycomponentsystem.system.RenderingSystem;
import com.mygdx.game.entitycomponentsystem.system.SteeringSystem;
import com.mygdx.game.entitycomponentsystem.system.SensorSystem;
import com.mygdx.game.gameworld.GameWorld;
import com.mygdx.game.gameworld.GameWorldCreator;
import com.mygdx.game.gameworld.TileMapHandler;
import com.mygdx.game.screens.menuScreens.MenuScreen;
import com.mygdx.game.ui.EndMatchPanel;
import com.mygdx.game.ui.PausePanel;
import com.mygdx.game.utils.ScreenOrientation;

import java.util.ArrayList;
import java.util.HashSet;

public class GameScreen implements Screen, Observer {

    private static final String CLASS_NAME  = MenuScreen.class.getSimpleName();
    private static final Logger logger         = new Logger(CLASS_NAME, Logger.INFO);
    private boolean readyToChangeScreen        = false;

    private final MyGdxGame game;
    private final OrthographicCamera camera;
    private GameWorld gameWorld;
    private Viewport viewport;
    private Viewport hudViewport;

    private Button pauseButton;
    private PausePanel pausePanel;
    private EndMatchPanel gameOverPanel;

    /* This buffer is used to synhronize aync B2dContactListener and synh CollisionSystem
    *  B2dContactListener put fresh contacts into this buffer,
    *  B2dContactSystem then reads it (one per cycle)
    *  CollisionSystem then resolve certain collision
    * */
    public static ArrayList<FixturePair> bufferOfFixtures = new ArrayList<>();

    Stage stageHUD;
    Stage characterHUD;

    public GameScreen()
    {
        bufferOfFixtures.clear();
        this.game = MyGdxGame.getInstance();

        initGameWorld();

        this.game.registerObserver(Topics.PLAYER_LEAVE_ROOM, this);
        this.pausePanel = new PausePanel(new PausePanel.Listener()
        {
            @Override
            public void exit() {
                Gdx.app.exit();
                System.exit(-1);
            }

            @Override
            public void quit() {
                if(game.getClientHandler() == null)
                {
                    game.backOneScreen();
                }
                else
                {
                    game.getClientHandler().goOutFromTheRoom();
                }
            }

            @Override
            public void resume() {
                pausePanel.hide();
            }
        }, game.getUiSkin());

        this.gameOverPanel = new EndMatchPanel(new EndMatchPanel.Listener()
        {
            @Override
            public void exit() {
                Gdx.app.exit();
                System.exit(-1);
            }

            @Override
            public void quit() {
                if(game.getClientHandler() == null)
                {
                    game.backOneScreen();
                }
                else
                {
                    game.getClientHandler().goOutFromTheRoom();
                }
            }

        }, game.getUiSkin());


        pausePanel.setPosition(0,0);
        pausePanel.setFillParent(true);
        pausePanel.setVisible(false);

        gameOverPanel.setPosition(0,0);
        gameOverPanel.setFillParent(true);
        gameOverPanel.setVisible(false);
        this.game.getMatchTracker().setGameOverPanel(gameOverPanel);

        ScreenOrientation screenOrientation = this.game.getScreenOrientation();
        if(screenOrientation != null)
        {
            this.game.getScreenOrientation().setScreenToLandscape();
        }
        RenderingSystem renderingSystem = new RenderingSystem(this.game.getBatch());
        this.camera = renderingSystem.getCamera();
        this.game.getGameWorldCreator().setOrthographicCamera(this.camera);

        this.viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera);
        this.hudViewport = new StretchViewport(700,700);
        this.stageHUD = new Stage(hudViewport, this.game.getBatch());
        this.characterHUD = new Stage(viewport, this.game.getBatch());

        InputManagerAndroidSystem inManAndroidSys = null;

        if(Gdx.app.getType() == Application.ApplicationType.Android)
        {
            /* Running application is on android device*/
            inManAndroidSys = new InputManagerAndroidSystem(this.game.getBatch(), hudViewport, this.game.getInputMultiplexer());

            this.game.getPooledEngine().addSystem(inManAndroidSys);
        }
        else
        {
            /* Running application is on desktop device*/
            this.game.getPooledEngine().addSystem(
                    new InputManagerSystem(this.game.getConnectionType(), this.game.getInputMultiplexer())
            );
        }

        this.game.getPooledEngine().addSystem(
                new PlayerControlSystem(
                        game.getWorldCreator(),
                        game.getPooledEngine(),
                        this.gameWorld.getWorldSingleton().getWorld(),
                        game.getAssetManagmentHandler().getResources(AssetDescriptors.SHOOT_SOUND)
                )
        );
        if(this.game.getClientHandler() != null)
        {
            this.game.getClientHandler().loadInputManagerTransmittingSystem();
        }

        this.game.getPooledEngine().addSystem(
                new CollectibleBasicManagerSystem(game.getWorldCreator(),
                        this.gameWorld.getWorldSingleton().getWorld(),
                        game.getPooledEngine())
        );

        this.game.getPooledEngine().addSystem(
                new B2dContactSystem());

        this.game.getPooledEngine().addSystem(
                new CollisionSystem(this.game.getMatchTracker()));

        this.game.getPooledEngine().addSystem(
                new RenderTiledMapSystem(game.getTileMapHandler().getOrthogonalTiledMapRenderer(),
                        this.camera,
                        this.gameWorld.getTiledMap()));
        
        HealthManagerSystem healthManagerSystem = new HealthManagerSystem();
        this.game.getPooledEngine().addSystem(healthManagerSystem);
        this.game.getWorldCreator().setHealthManagerSystem(healthManagerSystem);

        this.game.getPooledEngine().addSystem(new CharacterStatsSystem());

        this.game.getPooledEngine().addSystem(
                new BulletSystem(game.getWorldCreator()));

        this.game.getPooledEngine().addSystem(
                new CollisionEffectsSystem(this.game.getGameWorldCreator().getCollisionEffectFrames().size)
        );

        this.game.getPooledEngine().addSystem(
                new EnemySystem(game.getWorldCreator(), this.gameWorld, game.getPooledEngine())
        );

        this.game.getPooledEngine().addSystem(new SensorSystem(this.game.getPooledEngine())
        );

        this.game.getPooledEngine().addSystem(new SteeringSystem());

        this.game.getPooledEngine().addSystem(
                new PhysicsSystem(this.gameWorld.getWorldSingleton().getWorld(), this.game.getMatchTracker()));

        if(game.getClientHandler() != null)
        {
            this.game.getClientHandler().loadDataReceivingSystem();
            this.game.getClientHandler().loadDataTransmittingSystem();
        }

        setRenderSystems(renderingSystem, this.characterHUD, this.stageHUD, inManAndroidSys);

        buildUI();

        ViewPortConfiguration.setupPhysicalSize();
        this.game.getWorldCreator().setCharacterHUD(this.characterHUD);
        this.game.getWorldCreator().createPlatforms();
        this.game.getWorldCreator().createPortals();
        this.game.getWorldCreator().createLimitAreaObjects();
        this.game.getWorldCreator().createHurtableObjects();
        this.game.getWorldCreator().createPlayer(true, this.game.getConnectionType(),null);
        //this.game.getWorldCreator().setConnectionType(this.game.getConnectionType());
        this.game.getWorldCreator().createBasicCollectibles();
        this.game.getWorldCreator().createPotions();

        if(this.game.getConnectionType() == GameConfig.LOCAL_CONNECTION)
        {
            this.game.getWorldCreator().createEnemies();
            this.game.getWorldCreator().createClouds();
        }
    }

    private void buildUI()
    {
        //Getting skin for all the menus
        Skin skin = this.game.getUiSkin();

        pauseButton = new Button(skin,"pause");
        pauseButton.setWidth(70);
        pauseButton.setHeight(70);

        pauseButton.setPosition(
                0,
                700 - pauseButton.getHeight()
                //stageHUD.getHeight() - 30 - pauseButton.getHeight()
        );
        //pauseButton.addListener(UI_Utils.clickSound());
        pauseButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                //pause();
                pausePanel.show();
                logger.debug("Clicked!");
            }
        });

        this.stageHUD.addActor(gameOverPanel);
        this.stageHUD.addActor(pausePanel);
        this.stageHUD.addActor(pauseButton);
        this.game.getInputMultiplexer().addProcessor(this.stageHUD);
    }

    @Override
    public void show()
    {
        this.camera.setToOrtho(false,
                ViewPortConfiguration.physicalWidth,
                ViewPortConfiguration.physicalHeight);
        this.camera.update();
        Gdx.input.setInputProcessor(this.game.getInputMultiplexer());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        this.characterHUD.draw();
        game.getPooledEngine().update(delta);
        this.stageHUD.draw();
        camera.update();

        if(readyToChangeScreen)
        {
            pausePanel.hide();
            gameOverPanel.hide();
            this.game.backOneScreen();
            readyToChangeScreen = false;
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        this.game.getViewport().update(width,height,true);
        this.hudViewport.update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {

        game.getPooledEngine().getSystem(InputManagerSystem.class).removeInputProcessor();
        this.game.getInputMultiplexer().removeProcessor(this.stageHUD);

        game.getPooledEngine().removeAllEntities();
        game.getPooledEngine().removeAllSystems();

        if(this.game.getClientHandler() != null)
        {
            this.game.getClientHandler().clearSystems();
            this.game.getClientHandler().getReceivedMessageArray().clear();
            this.game.getClientHandler().getTransmitingMessageArray().clear();
        }

        this.gameWorld = null;
    }

    @Override
    public void dispose() {

    }

    /* Put all systems that are responsisble for rendering */
    private void setRenderSystems(RenderingSystem renderingSystem, Stage characterHudStage, Stage gameHudStage, InputManagerAndroidSystem inManAndroidSys)
    {
        this.game.getPooledEngine().addSystem(new AnimationSystem());
        this.game.getPooledEngine().addSystem(new RenderCharacterHudSystem(characterHudStage));
        this.game.getPooledEngine().addSystem(renderingSystem);
        this.game.getPooledEngine().addSystem
                (new PhysicsDebugSystem(this.gameWorld.getWorldSingleton().getWorld(),
                        this.camera));
        this.game.getPooledEngine().addSystem(
                new RenderGameHud(gameHudStage));

        if(Gdx.app.getType() == Application.ApplicationType.Android)
        {
            this.game.getPooledEngine().addSystem(
                    new RenderAndroidControllerSystem(inManAndroidSys.getAndroidController()));
        }
    }

    @Override
    public void update(Object... args)
    {
        readyToChangeScreen = true;
    }

    private void initGameWorld()
    {
        resetGame();
        TileMapHandler.instance = null;
        this.game.setTiledMapHandler(TileMapHandler.getInstance(GameConfig.LEVEL1));
        this.gameWorld = new GameWorld(this.game.getTileMapHandler().getTiledMap());
        this.game.setGameWorld(this.gameWorld);
        GameWorldCreator.currentAvailablePlayerID = 0;
    }

    private void resetGame()
    {
        this.game.getMatchTracker().reset();
        //this.gameWorld.reset();
    }
}
