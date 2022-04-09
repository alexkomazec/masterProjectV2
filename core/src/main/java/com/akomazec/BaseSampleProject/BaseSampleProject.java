package com.akomazec.BaseSampleProject;

import com.akomazec.BaseSampleProject.Sprites.Bricks.Brick;
import com.akomazec.BaseSampleProject.Sprites.Bricks.Bricks;
import com.akomazec.BaseSampleProject.Sprites.Collects.Collectible;
import com.akomazec.BaseSampleProject.Sprites.Collects.Collectibles;
import com.akomazec.BaseSampleProject.Sprites.Player;
import com.akomazec.BaseSampleProject.Tools.B2WorldCreator;
import com.akomazec.BaseSampleProject.Tools.OrthogonalTiledMapRendererWithSprites;
import com.akomazec.BaseSampleProject.Tools.WorldContactListener;
import com.badlogic.gdx.Application;
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


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class BaseSampleProject extends ApplicationAdapter {
	private final float UPDATE_TIME = 1/60;
	float timer;

	public static SpriteBatch batch;
	private Texture image;

	Player player;
	HashMap<Integer, Player> otherPlayers;
	//Enemy enemy;

	//Box2d variables
	private WorldSingleton world;
	private Box2DDebugRenderer b2dr;
	private B2WorldCreator creator;

	TiledMap tiledMap;
	public OrthographicCamera camera;
	OrthogonalTiledMapRendererWithSprites tiledMapRenderer;
	Controller controller;

	Bricks bricks;
	Collectibles collectibles;

	//Box2D Collision Bits
	public static final short GROUND_BIT = 1;
	public static final short PLAYER_BIT = 2;
	public static final short MAGIC_BIT = 4;
	public static final short COLLECTIBLE_BIT = 8;
	public static final short ENEMY_BIT = 16;


	/* Server stuff */
	private Socket socket;
	Emitter emitter = new Emitter();

	String id;
	//private static String url = "http://localhost:8080";
	//private static String url = "http://138.68.160.152:5000";
	private static String url = "http://192.168.0.18:5000";

	String isAnyKeyPressed = "NONE";

	public void updateServer(float dt)
	{
		timer+= dt;
		if(timer >= (UPDATE_TIME) && player != null && player.hasMoved())
		{
			long startTime = TimeUtils.nanoTime();
			socket.emit("LEFT");
			timer = 0;
			long elapsedTime = TimeUtils.timeSinceNanos(startTime);
			//System.out.println("elapsedTime: " + elapsedTime + " ns");
			//System.out.println("Moved!");
		}
	}
	@Override
	public void create() {

		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		batch = new SpriteBatch();
		otherPlayers = new HashMap<Integer, Player>();
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		camera = new OrthographicCamera();
		camera.setToOrtho(false,w,h);
		camera.update();
		controller = new Controller(camera);

		tiledMap = new TmxMapLoader().load("Tilesets/Dummy/dummy.tmx");
		tiledMapRenderer = new OrthogonalTiledMapRendererWithSprites(tiledMap);

		//create our Box2D world, setting no gravity in X, -100 gravity in Y, and allow bodies to sleep
		world = WorldSingleton.getInstance(new Vector2(0, -100), true);
		world.getWorld().setContactListener(new WorldContactListener());

		//allows for debug lines of our box2d world.
		b2dr = new Box2DDebugRenderer();
		creator = B2WorldCreator.getInstance(world, tiledMap);

		//this.enemy = new Enemy();
		//creator.createEntity(this.enemy);

		this.bricks = new Bricks(world, tiledMap);
		this.collectibles = new Collectibles(world,tiledMap);

		creator.createBricks(this.bricks);
		creator.createCollectibles(this.collectibles);
		//bricks.removeBrick(0);
		//bricks.removeBrick(0);
		//bricks.removeBrick(0);

		createSocket();
		configSocketEvents();
		connectSocket();

		for(int index = 0; index < 1000; index++)
		{
			long startTime = TimeUtils.nanoTime();
			socket.emit("LEFT");
			timer = 0;
			long elapsedTime = TimeUtils.timeSinceNanos(startTime);
			//System.out.println("elapsedTime: " + elapsedTime + " ns");
		}

		player = new Player();
		creator.createEntity(player,-1.0f,-1.0f);

	}

	@Override
	public void render() {

		//System.out.println("render");
		/*for(int index = 0; index < 100; index++)
		{
			long startTime = TimeUtils.nanoTime();
			socket.emit("LEFT");
			timer = 0;
			long elapsedTime = TimeUtils.timeSinceNanos(startTime);
			System.out.println("elapsedTime: " + elapsedTime + " ns");
		}*/

		update();
		Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();
		tiledMapRenderer.setView(camera);
		tiledMapRenderer.render();
		b2dr.render(world.getWorld(), camera.combined);
		controller.draw();
	}

	public void update()
	{
		updatePlayer();
		//updateEnemy();
		handleInput();
		updateServer(Gdx.graphics.getDeltaTime());
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
			{
				player.jump();
				isAnyKeyPressed = "JUMP";
			}
			if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= 2)
			{
				player.turnRight();
				isAnyKeyPressed = "RIGHT";
			}
			if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x >= -2)
			{
				player.turnLeft();
				isAnyKeyPressed = "LEFT";
			}

			if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
			{
				player.fireMagicBall();
			}
		}


		if(controller.isRightPressed())
			player.turnRight();
		else if (controller.isLeftPressed())
			player.turnLeft();
		else if (controller.isUpPressed())
			player.jump();
		else
		{

		}

		/*if(isAnyKeyPressed != "NONE")
		{
			socket.emit("UpdatePlayerPosition", player.clientID, isAnyKeyPressed);

			//socket.emit("oooooooooo");
			isAnyKeyPressed = "NONE";

			System.out.println("Moved!");
		}*/
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

	/*	void updateEnemy()
        {
            if(enemy != null)
            {
                if(enemy.shouldBeDestroyed)
                {
                    world.getWorld().destroyBody(enemy.b2body);
                    enemy = null;
                }
            }
        }
    */
	@Override
	public void dispose() {

	}

	private void updateMagicBalls()
	{
		if(player != null)
		{
			player.updateMagicBalls();
		}
	}

	private void createSocket()
	{
		IO.Options options = new IO.Options();
		//options.transports = new String[]{"websocket"};
		// Number of failed retries
		options.reconnectionAttempts = 3;
		// Time interval for failed reconnection
		options.reconnectionDelay = 10;
		// Connection timeout (ms)
		options.timeout = 50;

		try {
			this.socket = IO.socket(url, options);
			Gdx.app.log("SocketIO", "Socket set");
		} catch (URISyntaxException e) {
			Gdx.app.log("SocketIO", "Wrong URL");
			e.printStackTrace();
		}
	}


	private void connectSocket()
	{

		this.socket.connect();
	}

	public void configSocketEvents()
	{
		this.socket.io().on(Manager.EVENT_RECONNECT_FAILED, new Emitter.Listener() {

			@Override
			public void call(Object... args) {
				Gdx.app.log("SocketIo", "Server is offline");
			}
		});

		this.socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
			@Override
			public void call(Object... args)
			{
				Gdx.app.log("SocketIO", "Connected");

				player = new Player();
				creator.createEntity(player,-1.0f,-1.0f);
			}
		}).on("socketID", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {

					id = data.getString("id");
					Gdx.app.log("SocketIO", "My ID: " + id);
				} catch (JSONException e) {
					Gdx.app.log("SocketIO", "Error getting ID");
				}
			}
		}).on("newPlayer", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				//JSONObject data = (JSONObject) args[0];

				System.out.println("Here");
				System.out.println(args[0]);
				//Gdx.app.log("SocketIO", "New Player Connect: " + id);
				//otherPlayers.put(id, new Player());
			}
		}).on("playerDisconnected", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];

				try {
					id = data.getString("id");
					Gdx.app.log("SocketIO", "Player with id" + id + " has been disconnected");
					//otherPlayers.remove(id);
				}catch(JSONException e){
					Gdx.app.log("SocketIO", "Error getting disconnected PlayerID");
				}
			}
		}).on("FromServer_PlayerMoved", new Emitter.Listener() {
			@Override
			public void call(Object... args) {

				int playerId = (int) args[0];
				String moveType = (String) args[1];

				System.out.println("Player " + playerId);
				//otherPlayers.get(playerId).b2body.setTransform(convertToFloat(pos_x), convertToFloat(pos_y),convertToFloat(0));
				//otherPlayers.get(playerId).b2body.setTransform(convertToFloat(pos_x), convertToFloat(pos_y),convertToFloat(0));

				if(moveType.equals("LEFT"))
				{
					otherPlayers.get(playerId).turnLeft();
				}
				else if(moveType.equals("RIGHT"))
				{
					otherPlayers.get(playerId).turnRight();
				}
				else if(moveType.equals("JUMP"))
				{
					otherPlayers.get(playerId).jump();
				}
				else
				{
					System.out.println("Wrong move type");
				}
			}
		});

		this.socket.on("GetUpdatedPosition", new Emitter.Listener() {
			@Override
			public void call(Object... args)
			{
				System.out.println("updated position:" + "x: " + player.b2body.getPosition().x + "y: " + player.b2body.getPosition().y);
				socket.emit("refreshPlayerPosition", player.b2body.getPosition().x, player.b2body.getPosition().y, player.clientID);
			}
		});

		this.socket.on("updatePlayerTable", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				System.out.println("Refresh Player Table");
				JSONArray arrPlayers = (JSONArray)args[0];

				for(int index = 0; index < arrPlayers.length(); index++)
				{
					JSONObject jsonObjectCurPlayer = null;

					try
					{
						jsonObjectCurPlayer = (JSONObject)arrPlayers.get(index);
						System.out.println(jsonObjectCurPlayer);
					}
					catch (JSONException e)
					{
						e.printStackTrace();
					}

					try {

						if(jsonObjectCurPlayer != null)
						{
							/* Check is it itself*/
							if(player.clientID != jsonObjectCurPlayer.getInt("playerID"))
							{
								int anotherPlayerID = jsonObjectCurPlayer.getInt("playerID");
								double x_pos		= jsonObjectCurPlayer.getDouble("x_pos");
								double y_pos		= jsonObjectCurPlayer.getDouble("y_pos");
								Player tempPlayer = new Player();
								creator.createEntity(tempPlayer, (float)x_pos, (float)y_pos);

								otherPlayers.put(anotherPlayerID, tempPlayer);
							}
							else
							{
								/* Do nothing, found itself*/
							}
						}
						else
						{
							System.out.println("jsonObjectCurPlayer = null");
						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}


				/*Number of players*/
				System.out.println("There are " + otherPlayers.size() + " players");
			}
		});

		this.socket.on("assignID2Player", new Emitter.Listener() {
			@Override
			public void call(Object... args)
			{
				player.clientID = (int)args[0];
				System.out.println("assignID2Player:" + player.clientID);
				socket.emit("addPlayer", player.bdef.position.x, player.bdef.position.y, player.clientID);
			}
		});
	}

	public static Float convertToFloat(double doubleValue) {
		return (float) doubleValue;
	}
}