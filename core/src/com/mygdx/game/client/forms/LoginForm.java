package com.mygdx.game.client.forms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.brashmonkey.spriter.Player;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.form.SimpleFormValidator;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.client.ConnectScreen;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.EngineIOException;

public class LoginForm extends VisWindow
{
	private MyGdxGame game;
	private Stage stage;
	private ConnectScreen connectScreen;

	public LoginForm(final MyGdxGame game, final ConnectScreen connectScreen) {
		super("Enter your credentials");
		this.game = game;
		this.connectScreen = connectScreen;
		configSocketEvents();
		VisTextButton cancelButton = new VisTextButton("Cancel");
		VisTextButton loginButton = new VisTextButton("Login");
		VisTextButton registerButton = new VisTextButton("Register");

		final VisValidatableTextField username = new VisValidatableTextField();
		final VisValidatableTextField password = new VisValidatableTextField();

		VisLabel errorLabel = new VisLabel();
		errorLabel.setColor(Color.RED);

		VisTable buttonTable = new VisTable(true);
		buttonTable.add(errorLabel).expand().fill();
		buttonTable.add(cancelButton);
		buttonTable.add(loginButton);
		buttonTable.add(registerButton);

		add(new VisLabel("e-mail: "));
		add(username).expand().fill();
		row();
		add(new VisLabel("password: "));
		add(password).expand().fill();
		row();
		add(buttonTable).fill().expand().colspan(2).padBottom(10);
		buttonTable.center();

		SimpleFormValidator validator; //for GWT compatibility
		validator = new SimpleFormValidator(loginButton, errorLabel, "smooth");
		validator.setSuccessMessage("all good!");
		validator.notEmpty(username, "e-mail can not be empty");
		validator.notEmpty(password, "password can not be empty");

		loginButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {

				if(stage == null)
				{
					stage = getStage();
				}

				game.getClientHandler().getOptions().auth.put("needToLogin", "true");
				game.getClientHandler().getOptions().auth.put("username", username.getText());
				game.getClientHandler().getOptions().auth.put("password",password.getText());
				game.getClientHandler().connectSocket();
			}
		});

		cancelButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {

				if(stage == null)
				{
					stage = getStage();
				}

				Dialogs.showOKDialog(stage, "message", "you can't escape this!");
			}
		});

		registerButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				Stage stage = getStage();
				connectScreen.setRegisterForm();
				stage.addActor(connectScreen.getRegisterForm());
			}
		});

		pack();
		setSize(getWidth() + 60, getHeight());
		centerWindow();
		addCloseButton();
	}

	public void configSocketEvents()
	{
		/* Triggers when the connection has been established */
		this.game.getClientHandler().getSocket().on(Socket.EVENT_CONNECT,new Emitter.Listener()
		{
			@Override
			public void call(Object... args) {
				connected(args);
			}
		});

		/* Connection Error, Try to reconnect again (ReconnectionAttempts times) */
		this.game.getClientHandler().getSocket().on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener()
		{
			@Override
			public void call(Object... args)
			{
				if(args[0] instanceof JSONObject) {
					JSONObject message = (JSONObject) args[0];
					connectionError(message);
				}
				else
				{
					System.out.println("xhr poll error, EVENT CONNECT ERROR");
				}
			}
		});

		/* Maximum ReconnectionAttempts has been reached, Server is offline*/
		this.game.getClientHandler().getSocket().io().on(Manager.EVENT_RECONNECT_FAILED, new Emitter.Listener() {

			@Override
			public void call(Object... args) {
				Gdx.app.log("SocketIo", "Server is offline");
				maxRecconectionAttemptsReached();
			}
		});
	}

	/* ================ Callbacks ================ */

	private void connected(Object... args)
	{
		connectScreen.setReadyToChangeScreen(true);
		Gdx.app.log("SocketIO", "Connected");
	}

	private void connectionError(JSONObject message)
	{
		if(stage == null)
		{
			stage = getStage();
		}

		try {
			Dialogs.showErrorDialog(stage,message.getString("message"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void maxRecconectionAttemptsReached()
	{
		if(stage == null)
		{
			stage = getStage();
		}
		Dialogs.showErrorDialog(stage,"Server is offline");
	}

}
