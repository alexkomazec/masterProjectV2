package com.akomazec.BaseSampleProject;

import com.akomazec.BaseSampleProject.Screens.ConnectToServerScreen;
import com.akomazec.BaseSampleProject.Sprites.Direction;
import com.akomazec.BaseSampleProject.Sprites.Player;
import com.akomazec.BaseSampleProject.Tools.B2WorldCreator;
import com.akomazec.BaseSampleProject.Tools.OrthogonalTiledMapRendererWithSprites;
import com.akomazec.BaseSampleProject.Tools.WorldContactListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
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
public class BaseSampleProject extends Game {
	
	private static BaseSampleProject instance = null;
	
	private final float UPDATE_TIME = 1/60f;
	float timer;

	/* Server stuff */
	public Socket socket;
	public IO.Options options;

	Emitter emitter = new Emitter();
	//public static String url = "http://localhost:8080";
	public static String url = "http://138.68.160.152:5000";
	//public static String url = "http://192.168.0.18:5000";

	public Player player;
	public HashMap<Integer, Player> otherPlayers;

	//Box2d variables
	public WorldSingleton world;
	public Box2DDebugRenderer b2dr;
	public B2WorldCreator creator;

	public TiledMap tiledMap;
	public OrthogonalTiledMapRendererWithSprites tiledMapRenderer;

	String id;

	private BaseSampleProject()
	{
		otherPlayers = new HashMap<Integer, Player>();
	}

	/*getInstance returns the new allocated space for object of the class or return the current
	* allocated spaced. Only one instance of the class can exist at the same time
	*/
	public static BaseSampleProject getInstance()
	{
		if (instance == null)
		{
			instance = new BaseSampleProject();
		}
		return instance;
	}

	public void connectSocket()
	{
		this.socket.connect();
	}

	@Override
	public void create() {

		createSocket();
		configSocketEvents();

		//create our Box2D world, setting no gravity in X, -100 gravity in Y, and allow bodies to sleep
		world = WorldSingleton.getInstance(new Vector2(0, -100), true);
		world.getWorld().setContactListener(new WorldContactListener());

		tiledMap = new TmxMapLoader().load("Tilesets/Dummy/dummy.tmx");
		tiledMapRenderer = new OrthogonalTiledMapRendererWithSprites(tiledMap);

		//allows for debug lines of our box2d world.
		b2dr = new Box2DDebugRenderer();
		creator = B2WorldCreator.getInstance(world, tiledMap);

		setScreen(new ConnectToServerScreen(this));
	}

	private void createSocket()
	{
		this.options = IO.Options.builder()
				.setReconnectionAttempts(3)
				.setReconnectionDelay(10)
				.setTimeout(50)
				.setAuth(new HashMap<String, String>())
				.setTimeout(10000)
				.build();

		try {
			this.socket = IO.socket(url, options);
			Gdx.app.log("SocketIO", "Socket set");
		} catch (URISyntaxException e) {
			Gdx.app.log("SocketIO", "Wrong URL");
			e.printStackTrace();
		}
	}

	public void configSocketEvents()
	{
		this.socket.io().on(Manager.EVENT_RECONNECT_FAILED, new Emitter.Listener() {

			@Override
			public void call(Object... args) {
				Gdx.app.log("SocketIo", "Server is offline");
			}
		});

		this.socket.on("socketID", new Emitter.Listener() {
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
		}).on("playerMoved", new Emitter.Listener() {
			@Override
			public void call(Object... args) {

				JSONArray objects = (JSONArray) args[0];
				Integer playerId = 0;
				String moveType  = "";
				playerId = objects.optInt(0);
				moveType = objects.optString(1);

				System.out.println("playerId " + playerId);
				System.out.println("moveType " + moveType);


				//System.out.println("Player " + playerId);
				//otherPlayers.get(playerId).b2body.setTransform(convertToFloat(pos_x), convertToFloat(pos_y),convertToFloat(0));
				//otherPlayers.get(playerId).b2body.setTransform(convertToFloat(pos_x), convertToFloat(pos_y),convertToFloat(0));

				if(moveType.equals("LEFT"))
				{
					otherPlayers.get(playerId).turnLeft();
					otherPlayers.get(playerId).direction = Direction.LEFT;
				}
				else if(moveType.equals("RIGHT"))
				{
					otherPlayers.get(playerId).turnRight();
					otherPlayers.get(playerId).direction = Direction.RIGHT;
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

		this.socket.on("getUpdatedPosition", new Emitter.Listener() {
			@Override
			public void call(Object... args)
			{
				System.out.println("updated position:" + "x: " + player.b2body.getPosition().x + "y: " + player.b2body.getPosition().y);
				socket.emit("refreshPlayersPosition", player.b2body.getPosition().x, player.b2body.getPosition().y, player.clientID);
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

		this.socket.on("assignID2Player", args -> {
			player.clientID = (int)args[0];
			System.out.println("assignID2Player:" + player.clientID);
			socket.emit("addPlayer", player.bdef.position.x, player.bdef.position.y, player.clientID);
		});

		this.socket.on("playerFiredMagic", args -> {
			int clientID = (int)args[0];
			otherPlayers.get(clientID).fireMagicBall();
		});
	}

	public void updateServer(float dt)
	{
		timer+= dt;
		if(timer >= (UPDATE_TIME) && player != null && player.hasMoved())
		{
			long startTime = TimeUtils.nanoTime();
			//socket.emit("LEFT");
			timer = 0;
			long elapsedTime = TimeUtils.timeSinceNanos(startTime);
			//System.out.println("elapsedTime: " + elapsedTime + " ns");
			//System.out.println("Moved!");
		}
	}
}