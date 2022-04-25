package com.akomazec.BaseSampleProject.Screens;

import com.akomazec.BaseSampleProject.Sprites.Bricks.Brick;
import com.akomazec.BaseSampleProject.Sprites.Bricks.Bricks;
import com.akomazec.BaseSampleProject.Sprites.Collects.Collectible;
import com.akomazec.BaseSampleProject.Sprites.Collects.Collectibles;
import com.akomazec.BaseSampleProject.BaseSampleProject;
import com.akomazec.BaseSampleProject.Controller;
import com.akomazec.BaseSampleProject.WorldSingleton;
import com.akomazec.BaseSampleProject.Sprites.Direction;
import com.akomazec.BaseSampleProject.Sprites.Player;
import com.akomazec.BaseSampleProject.Tools.B2WorldCreator;
import com.akomazec.BaseSampleProject.Tools.OrthogonalTiledMapRendererWithSprites;
import com.akomazec.BaseSampleProject.Tools.WorldContactListener;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.TimeUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;

import io.socket.client.IO;
import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
public class MainScreen extends ScreenAdapter{

	public static SpriteBatch batch;
	private Texture image;

	public OrthographicCamera camera;
	Controller controller;

	Bricks bricks;
	Collectibles collectibles;

	//Box2D Collision Bits
	public static final short GROUND_BIT = 1;
	public static final short PLAYER_BIT = 2;
	public static final short MAGIC_BIT = 4;
	public static final short COLLECTIBLE_BIT = 8;
	public static final short ENEMY_BIT = 16;

	BaseSampleProject game;

	String isAnyKeyPressed = "NONE";

    // == constructors ==
    public MainScreen(BaseSampleProject game) 
    {
		this.game = game;
    }

    @Override
    public void show() 
    {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
		batch = new SpriteBatch();
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		camera = new OrthographicCamera();
		camera.setToOrtho(false,w,h);
		camera.update();

		controller = new Controller(camera, game.socket, BaseSampleProject.url, batch);


		this.bricks = new Bricks(this.game.world, this.game.tiledMap);
		this.collectibles = new Collectibles(this.game.world,this.game.tiledMap);

		this.game.creator.createBricks(this.bricks);
		this.game.creator.createCollectibles(this.collectibles);

    }

	@Override
	public void render(float delta) {

		update();
		Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();
		this.game.tiledMapRenderer.setView(camera);
		this.game.tiledMapRenderer.render();
		this.game.b2dr.render(this.game.world.getWorld(), camera.combined);

		if(Gdx.app.getType() == Application.ApplicationType.Android)
		{
			controller.draw();
		}
	}

    public void update()
	{
		updatePlayer();
		//updateEnemy();
		handleInput();
		this.game.updateServer(Gdx.graphics.getDeltaTime());
		updateMagicBalls();
		updateBricks();
		updateCollectibles();

		//takes 1 step in the physics simulation(60 times per second)
		this.game.world.getWorld().step(1 / 60f, 6, 2);
	}

	public void handleInput(){

		if(this.game.player != null)
		{
			//control our player using immediate impulses
			if (Gdx.input.isKeyJustPressed(Input.Keys.UP))
			{
				this.game.player.jump();
				isAnyKeyPressed = "JUMP";
			}
			if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && this.game.player.b2body.getLinearVelocity().x <= 2)
			{
				this.game.player.turnRight();
				isAnyKeyPressed = "RIGHT";
			}
			if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && this.game.player.b2body.getLinearVelocity().x >= -2)
			{
				this.game.player.turnLeft();
				isAnyKeyPressed = "LEFT";
			}

			if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
			{
				this.game.player.fireMagicBall();
				this.game.socket.emit("magicFired", this.game.player.clientID);
			}
		}


		if(controller.isRightPressed())
		{
			this.game.player.turnRight();
			isAnyKeyPressed = "RIGHT";
		}
		else if (controller.isLeftPressed())
		{
			this.game.player.turnLeft();
			isAnyKeyPressed = "LEFT";
		}
		else if (controller.isUpPressed())
		{
			this.game.player.jump();
			isAnyKeyPressed = "JUMP";
		}
		else if (controller.isFirePressed() && (!controller.cooldownFlag))
		{
			this.game.player.fireMagicBall();
			controller.cooldownFlag = true;
			this.game.socket.emit("magicFired", this.game.player.clientID);
		}
		else
		{

		}

		if(isAnyKeyPressed != "NONE")
		{
			this.game.socket.emit("updatePlayerPosition", this.game.player.clientID, isAnyKeyPressed);

			//socket.emit("oooooooooo");
			isAnyKeyPressed = "NONE";

			System.out.println("Moved!");
		}
		/*if(enemy != null)
		{
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
		}*/
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
		if(this.game.player != null)
		{
			if(this.game.player.shouldBeDestroyed)
			{
				/* Set the reference to point to null*/
				this.game.world.getWorld().destroyBody(this.game.player.b2body);
				this.game.player = null;
			}
		}
	}

	private void updateMagicBalls()
	{
		if(this.game.player != null)
		{
			this.game.player.updateMagicBalls();
		}

		/* Check if there are any players */
		if(!this.game.otherPlayers.isEmpty())
		{
			/* There are some players */
			for (Player player : this.game.otherPlayers.values()) {

				/*Check if any of these player casted some magic balls*/
				if(!player.magicBalls.isEmpty())
				{
					player.updateMagicBalls();
				}
			}

		}
	}

	public static Float convertToFloat(double doubleValue) {
		return (float) doubleValue;
	}
}
