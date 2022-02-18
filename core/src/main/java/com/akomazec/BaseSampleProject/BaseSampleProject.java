package com.akomazec.BaseSampleProject;

import com.akomazec.BaseSampleProject.Tools.B2WorldCreator;
import com.akomazec.BaseSampleProject.Tools.OrthogonalTiledMapRendererWithSprites;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class BaseSampleProject extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture image;

	//Box2d variables
	private World world;
	private Box2DDebugRenderer b2dr;
	private B2WorldCreator creator;

	TiledMap tiledMap;
	OrthographicCamera camera;
	OrthogonalTiledMapRendererWithSprites tiledMapRenderer;

	@Override
	public void create() {

		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		camera = new OrthographicCamera();
		camera.setToOrtho(false,w,h);
		camera.update();

		tiledMap = new TmxMapLoader().load("Tilesets/Dummy/dummy.tmx");
		tiledMapRenderer = new OrthogonalTiledMapRendererWithSprites(tiledMap);

		//create our Box2D world, setting no gravity in X, -10 gravity in Y, and allow bodies to sleep
		world = new World(new Vector2(0, -10), true);
		//allows for debug lines of our box2d world.
		b2dr = new Box2DDebugRenderer();
		creator = B2WorldCreator.getInstance(world, tiledMap);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		tiledMapRenderer.setView(camera);
		tiledMapRenderer.render();
		b2dr.render(world, camera.combined);
	}

	@Override
	public void dispose() {

	}
}