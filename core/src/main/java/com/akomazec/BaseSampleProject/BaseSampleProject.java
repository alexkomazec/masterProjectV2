package com.akomazec.BaseSampleProject;

import com.akomazec.BaseSampleProject.Sprites.Bricks.Brick;
import com.akomazec.BaseSampleProject.Sprites.Bricks.Bricks;
import com.akomazec.BaseSampleProject.Sprites.Collects.Collectible;
import com.akomazec.BaseSampleProject.Sprites.Collects.Collectibles;
import com.akomazec.BaseSampleProject.Sprites.Direction;
import com.akomazec.BaseSampleProject.Sprites.Enemy;
import com.akomazec.BaseSampleProject.Sprites.MagicBall;
import com.akomazec.BaseSampleProject.Sprites.Player;
import com.akomazec.BaseSampleProject.Tools.B2WorldCreator;
import com.akomazec.BaseSampleProject.Tools.OrthogonalTiledMapRendererWithSprites;
import com.akomazec.BaseSampleProject.Tools.WorldContactListener;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

import java.util.ArrayList;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class BaseSampleProject extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture image;

	Player player;
	Enemy enemy;

	//Box2d variables
	private WorldSingleton world;
	private Box2DDebugRenderer b2dr;
	private B2WorldCreator creator;

	TiledMap tiledMap;
	OrthographicCamera camera;
	OrthogonalTiledMapRendererWithSprites tiledMapRenderer;

	ArrayList<MagicBall> magicBalls;
	Bricks bricks;
	Collectibles collectibles;

	//Box2D Collision Bits
	public static final short GROUND_BIT = 1;
	public static final short PLAYER_BIT = 2;
	public static final short MAGIC_BIT = 4;
	public static final short COLLECTIBLE_BIT = 8;
	public static final short ENEMY_BIT = 16;

	@Override
	public void create() {

		magicBalls = new ArrayList<>();

		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		camera = new OrthographicCamera();
		camera.setToOrtho(false,w,h);
		camera.update();

		tiledMap = new TmxMapLoader().load("Tilesets/Dummy/dummy.tmx");
		tiledMapRenderer = new OrthogonalTiledMapRendererWithSprites(tiledMap);

		//create our Box2D world, setting no gravity in X, -100 gravity in Y, and allow bodies to sleep
		world = WorldSingleton.getInstance(new Vector2(0, -100), true);
		world.getWorld().setContactListener(new WorldContactListener());

		//allows for debug lines of our box2d world.
		b2dr = new Box2DDebugRenderer();
		creator = B2WorldCreator.getInstance(world, tiledMap);

		this.player = new Player();
		creator.createEntity(this.player);

		this.enemy = new Enemy();
		creator.createEntity(this.enemy);

		this.bricks = new Bricks(world, tiledMap);
		this.collectibles = new Collectibles(world,tiledMap);

		creator.createBricks(this.bricks);
		creator.createCollectibles(this.collectibles);
		//bricks.removeBrick(0);
		//bricks.removeBrick(0);
		//bricks.removeBrick(0);

	}

	@Override
	public void render() {
		update();
		Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();
		tiledMapRenderer.setView(camera);
		tiledMapRenderer.render();
		b2dr.render(world.getWorld(), camera.combined);
	}

	public void update()
	{
		updatePlayer();
		updateEnemy();
		handleInput();
		updateMagicBalls();
		updateBricks();
		updateCollectibles();

		//takes 1 step in the physics simulation(60 times per second)
		world.getWorld().step(1 / 60f, 6, 2);
	}

	public void handleInput(){

		if(player != null)
		{
			//control our player using immediate impulses
			if (Gdx.input.isKeyJustPressed(Input.Keys.UP))
				player.jump();
			if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= 2) {
				player.b2body.applyLinearImpulse(new Vector2(100f, 0),
						player.b2body.getWorldCenter(),
						true);
				player.direction = Direction.RIGHT;
			}
			if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x >= -2) {
				player.b2body.applyLinearImpulse(new Vector2(-100f, 0),
						player.b2body.getWorldCenter(),
						true);
				player.direction = Direction.LEFT;
			}

			if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
			{
				magicBalls.add(player.fireMagicBall());
			}

		}

		if(enemy != null)
		{
			/*Enemy controls*/
			if (Gdx.input.isKeyJustPressed(Input.Keys.W))
				enemy.jump();
			if (Gdx.input.isKeyPressed(Input.Keys.D) && enemy.b2body.getLinearVelocity().x <= 2)
			{
				enemy.b2body.applyLinearImpulse(new Vector2(100f, 0),
						enemy.b2body.getWorldCenter(),
						true);
				enemy.direction = Direction.RIGHT;
			}
			if (Gdx.input.isKeyPressed(Input.Keys.A) && enemy.b2body.getLinearVelocity().x >= -2)
			{
				enemy.b2body.applyLinearImpulse(new Vector2(-100f, 0),
						enemy.b2body.getWorldCenter(),
						true);
				enemy.direction = Direction.LEFT;
			}

			if (Gdx.input.isKeyJustPressed(Input.Keys.F))
			{
				magicBalls.add(enemy.fireMagicBall());
			}
		}

	}

	private void updateMagicBalls()
	{
		for (int i = 0; i < magicBalls.size(); i++)
		{
			MagicBall magicBall = magicBalls.get(i);

			if(!magicBall.shouldBeDestroyed)
			{
				magicBall.update();
			}
			else
			{
				//Remove the magic ball
				world.getWorld().destroyBody(magicBall.b2body);
				magicBalls.remove(magicBall);

				//Handle indexing
				if(i + 1 ==  magicBalls.size())
				{
					/* Case:	Last element in the array has been deleted
					 			Just go out of the loop
					*/
				}
				else
				{
					/* Case:	Some mid element has been deleted
					 			Just go out of the loop
					*/
					i--;
				}

			}
		}
	}

	void updateBricks()
	{
		for (int i = 0; i < bricks.arrayOfBricks.size; i++)
		{
			Brick brick = bricks.arrayOfBricks.get(i);

			if(brick.shouldBeDestroyed) {
				//Remove the brick
				bricks.removeBrick(i);

				//Handle indexing
				if (i + 1 == bricks.arrayOfBricks.size) {
					/* Case:	Last element in the array has been deleted
					 			Just go out of the loop
					*/
				} else {
					/* Case:	Some mid element has been deleted
					 			Just go out of the loop
					*/
					i--;
				}
			}
		}
	}

	void updateCollectibles()
	{
		for (int i = 0; i < collectibles.arrayOfCollectibles.size; i++)
		{
			Collectible collectible = collectibles.arrayOfCollectibles.get(i);

			if(collectible.shouldBeDestroyed) {
				//Remove the collectible
				collectibles.removeCollectible(i);

				//Handle indexing
				if (i + 1 == collectibles.arrayOfCollectibles.size) {
					/* Case:	Last element in the array has been deleted
					 			Just go out of the loop
					*/
				} else {
					/* Case:	Some mid element has been deleted
					 			Just go out of the loop
					*/
					i--;
				}
			}
		}
	}

	void updatePlayer()
	{
		if(player != null)
		{
			if(player.shouldBeDestroyed)
			{
				/* Set the reference to point to null*/
				world.getWorld().destroyBody(player.b2body);
				player = null;
			}
		}
	}

	void updateEnemy()
	{
		if(enemy != null)
		{
			if(enemy.shouldBeDestroyed)
			{
				/* Set the reference to point to null*/
				world.getWorld().destroyBody(enemy.b2body);
				enemy = null;
			}
		}
	}

	@Override
	public void dispose() {

	}
}