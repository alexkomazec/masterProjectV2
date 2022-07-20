package com.mygdx.game.screens;


import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
import com.mygdx.game.common.ViewPortConfiguration;
import com.mygdx.game.config.GameConfig;
import com.mygdx.game.entitycomponentsystem.system.AnimationSystem;
import com.mygdx.game.entitycomponentsystem.system.BulletSystem;
import com.mygdx.game.entitycomponentsystem.system.CharacterStatsSystem;
import com.mygdx.game.entitycomponentsystem.system.CollectibleBasicManagerSystem;
import com.mygdx.game.entitycomponentsystem.system.CollisionSystem;
import com.mygdx.game.entitycomponentsystem.system.EnemySystem;
import com.mygdx.game.entitycomponentsystem.system.HealthManagerSystem;
import com.mygdx.game.entitycomponentsystem.system.InputManagerAndroidSystem;
import com.mygdx.game.entitycomponentsystem.system.InputManagerSystem;
import com.mygdx.game.entitycomponentsystem.system.PhysicsDebugSystem;
import com.mygdx.game.entitycomponentsystem.system.PhysicsSystem;
import com.mygdx.game.entitycomponentsystem.system.PlayerControlSystem;
import com.mygdx.game.entitycomponentsystem.system.RenderAndroidControllerSystem;
import com.mygdx.game.entitycomponentsystem.system.RenderTiledMapSystem;
import com.mygdx.game.entitycomponentsystem.system.RenderingSystem;
import com.mygdx.game.entitycomponentsystem.system.SteeringSystem;
import com.mygdx.game.screens.menuScreens.MenuScreen;
import com.mygdx.game.ui.PauseMenu;
import com.mygdx.game.utils.ScreenOrientation;

public class GameScreen implements Screen {

    private static final String CLASS_NAME  = MenuScreen.class.getSimpleName();
    private static final Logger logger         = new Logger(CLASS_NAME, Logger.DEBUG);

    private final MyGdxGame game;
    private final OrthographicCamera camera;
    private Viewport viewport;
    private Viewport hudViewport;

    private Button pauseButton;
    private PauseMenu pauseMenu;

    Stage stageHUD;
    Stage characterHUD;

    public GameScreen()
    {
        this.game = MyGdxGame.getInstance();

        this.pauseMenu = new PauseMenu(new PauseMenu.Listener()
        {
            @Override
            public void exit() {
                Gdx.app.exit();
            }

            @Override
            public void quit() {
                game.changeScreen(MyGdxGame.MENU_SCREEN);
            }

            @Override
            public void resume() {
                pauseMenu.hide();
            }
        }, game.getUiInGameSkin());

        pauseMenu.setPosition(0,0);
        pauseMenu.setFillParent(true);
        pauseMenu.setVisible(false);

        ScreenOrientation screenOrientation = this.game.getScreenOrientation();
        if(screenOrientation != null)
        {
            this.game.getScreenOrientation().setScreenToLandscape();
        }
        RenderingSystem renderingSystem = new RenderingSystem(this.game.getBatch());
        this.camera = renderingSystem.getCamera();
        this.game.getGameWorldCreator().setOrthographicCamera(this.camera);

        setRenderSystems(renderingSystem);
        this.game.getPooledEngine().addSystem(new AnimationSystem());
        viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera);
        hudViewport = new StretchViewport(700,700);
        this.game.getPooledEngine().addSystem(new HealthManagerSystem());
        this.game.getWorldCreator().setPooledEngine(this.game.getPooledEngine());
        this.game.getPooledEngine().addSystem(
                new PlayerControlSystem(
                        game.getWorldCreator(),
                        game.getPooledEngine(),
                        game.getGameWorld().getWorldSingleton().getWorld())
        );
        this.game.getPooledEngine().addSystem(
                new CollectibleBasicManagerSystem(game.getWorldCreator(),
                                                  game.getGameWorld().getWorldSingleton().getWorld(),
                                                  game.getPooledEngine())
        );
        this.game.getPooledEngine().addSystem(
                new EnemySystem(game.getWorldCreator(), game.getGameWorld(), game.getPooledEngine())
        );
        this.game.getPooledEngine().addSystem(
                new PhysicsSystem(game.getGameWorld().getWorldSingleton().getWorld()));
        this.game.getPooledEngine().addSystem(
                new CollisionSystem());
        this.game.getPooledEngine().addSystem(
                new BulletSystem(game.getGameWorld()));
        this.game.getPooledEngine().addSystem(
                new SteeringSystem());
        this.game.getPooledEngine().addSystem(
                new CharacterStatsSystem()
        );

        if(Gdx.app.getType() == Application.ApplicationType.Android)
        {
            /* Running application is on android device*/
            InputManagerAndroidSystem inManAndroidSys =
                    new InputManagerAndroidSystem(this.game.getBatch(), hudViewport);
            RenderAndroidControllerSystem renderASys =
                    new RenderAndroidControllerSystem(inManAndroidSys.getAndroidController());

            this.game.getPooledEngine().addSystem(inManAndroidSys);
            this.game.getPooledEngine().addSystem(renderASys);
        }
        else
        {
            /* Running application is on desktop device*/
            this.game.getPooledEngine().addSystem(
                    new InputManagerSystem(this.game.getConnectionType(), this.game.getInputMultiplexer())
            );
        }

        this.stageHUD = new Stage(hudViewport, this.game.getBatch());
        this.characterHUD = new Stage(viewport, this.game.getBatch());
        buildUI();
    }

    private void buildUI()
    {
        //Getting skin for all the menus
        Skin skin = this.game.getUiSkin();

        pauseButton = new Button(skin,"pause");
        pauseButton.setPosition(
                0,
                Gdx.graphics.getHeight()
                //stageHUD.getHeight() - 30 - pauseButton.getHeight()
        );
        //pauseButton.addListener(UI_Utils.clickSound());
        pauseButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                //pause();
                pauseMenu.show();
                logger.debug("Clicked!");
            }
        });

        this.stageHUD.addActor(pauseMenu);
        this.stageHUD.addActor(pauseButton);
        this.game.getInputMultiplexer().addProcessor(this.stageHUD);
        Gdx.input.setInputProcessor(this.game.getInputMultiplexer());
    }

    @Override
    public void show()
    {
        ViewPortConfiguration.setupPhysicalSize();
        this.camera.setToOrtho(false,
                ViewPortConfiguration.physicalWidth,
                ViewPortConfiguration.physicalHeight);
        this.camera.update();

        this.game.getWorldCreator().setCharacterHUD(this.characterHUD);
        this.game.getWorldCreator().createPlatforms();
        this.game.getWorldCreator().setConnectionType(this.game.getConnectionType());
        this.game.getWorldCreator().createPlayer(true,null);
        this.game.getWorldCreator().createBasicCollectibles();

        this.game.getWorldCreator().createEnemies();
        this.game.getWorldCreator().createClouds();
        this.game.getWorldCreator().createPotions();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        game.getPooledEngine().update(delta);
        this.stageHUD.draw();
        this.characterHUD.draw();
        camera.update();
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
        game.getPooledEngine().removeAllEntities();
        game.getPooledEngine().removeAllSystems();
    }

    @Override
    public void dispose() {

    }

    /* Put all systems that are responsisble for rendering */
    void setRenderSystems(RenderingSystem renderingSystem)
    {
        this.game.getPooledEngine().addSystem(renderingSystem);
        this.game.getPooledEngine().addSystem(
                new RenderTiledMapSystem(game.getTileMapHandler().getOrthogonalTiledMapRenderer(),
                        this.camera,
                        this.game.getGameWorld().getTiledMap()));
        this.game.getPooledEngine().addSystem(
                new PhysicsDebugSystem(game.getGameWorld().getWorldSingleton().getWorld(),
                        this.camera));
    }
}
