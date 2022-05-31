package com.mygdx.game.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Logger;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.common.ViewPortConfiguration;
import com.mygdx.game.entitycomponentsystem.system.BulletSystem;
import com.mygdx.game.entitycomponentsystem.system.CollisionSystem;
import com.mygdx.game.entitycomponentsystem.system.EnemySystem;
import com.mygdx.game.entitycomponentsystem.system.InputManagerSystem;
import com.mygdx.game.entitycomponentsystem.system.PhysicsDebugSystem;
import com.mygdx.game.entitycomponentsystem.system.PhysicsSystem;
import com.mygdx.game.entitycomponentsystem.system.PlayerControlSystem;
import com.mygdx.game.entitycomponentsystem.system.RenderTiledMapSystem;
import com.mygdx.game.entitycomponentsystem.system.SteeringSystem;
import com.mygdx.game.screens.menuScreens.MenuScreen;

public class GameScreen implements Screen {

    private static final String CLASS_NAME  = MenuScreen.class.getSimpleName();
    private static final Logger log         = new Logger(CLASS_NAME, Logger.DEBUG);

    private final MyGdxGame game;
    private final OrthographicCamera camera;

    public GameScreen()
    {
        this.game = MyGdxGame.getInstance();
        this.camera = new OrthographicCamera();

        this.game.getPooledEngine().addSystem(
                new InputManagerSystem()
        );
        this.game.getPooledEngine().addSystem(
                new PlayerControlSystem(
                        game.getWorldCreator(),
                        game.getPooledEngine(),
                        game.getGameWorld().getWorldSingleton().getWorld())
        );
        this.game.getPooledEngine().addSystem(
                new EnemySystem(game.getWorldCreator(), game.getGameWorld(), game.getPooledEngine())
        );
        this.game.getPooledEngine().addSystem(
                new RenderTiledMapSystem(game.getTileMapHandler().getOrthogonalTiledMapRenderer(),
                        this.camera,
                        this.game.getGameWorld().getTiledMap()));
        this.game.getPooledEngine().addSystem(
                new PhysicsSystem(game.getGameWorld().getWorldSingleton().getWorld()));
        this.game.getPooledEngine().addSystem(
                new CollisionSystem());
        this.game.getPooledEngine().addSystem(
                new PhysicsDebugSystem(game.getGameWorld().getWorldSingleton().getWorld(),
                        this.camera));
        this.game.getPooledEngine().addSystem(
                new BulletSystem(game.getGameWorld()));
        this.game.getPooledEngine().addSystem(
                new SteeringSystem());
    }
    @Override
    public void show()
    {
        int testPlayerID = 5;

        ViewPortConfiguration.setupPhysicalSize();
        this.camera.setToOrtho(false,
                ViewPortConfiguration.physicalWidth,
                ViewPortConfiguration.physicalHeight);
        this.camera.update();

        this.game.getWorldCreator().createPlatforms(
                this.game.getGameWorld().getTiledMap(),
                this.game.getGameWorld().getWorldSingleton().getWorld(),
                game.getPooledEngine());

        InputManagerSystem inputManagerSystemTemp =
                this.game.getPooledEngine().getSystem(InputManagerSystem.class);

        inputManagerSystemTemp.assignPlayerToInputProcessor(testPlayerID);

        this.game.getWorldCreator().createPlayer(
                this.game.getGameWorld(),
                game.getPooledEngine(),
                this.camera,
                testPlayerID,
                true);

        this.game.getWorldCreator().createEnemies(
                this.game.getGameWorld(),
                game.getPooledEngine());

        this.game.getWorldCreator().createClouds(
                this.game.getGameWorld().getTiledMap(),
                this.game.getGameWorld(),
                game.getPooledEngine()
        );
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        game.getPooledEngine().update(delta);
        camera.update();
    }

    @Override
    public void resize(int width, int height) {
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
}
