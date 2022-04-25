package com.akomazec.BaseSampleProject.Screens;

import com.akomazec.BaseSampleProject.BaseSampleProject;
import com.akomazec.BaseSampleProject.Sprites.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.form.SimpleFormValidator;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;
import com.kotcrab.vis.ui.widget.VisWindow;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class LoginScreenForm extends VisWindow
{
	private BaseSampleProject game;
	private Stage stage;
	public boolean readyToChangeScreen = false;

	public LoginScreenForm (BaseSampleProject game) {
		super("Enter your credentials");
		this.game = game;
		configSocketEvents();
		VisTextButton cancelButton = new VisTextButton("Cancel");
		VisTextButton loginButton = new VisTextButton("Login");
		VisTextButton registerButton = new VisTextButton("Register");

		VisValidatableTextField username = new VisValidatableTextField();
		VisValidatableTextField password = new VisValidatableTextField();

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

				game.options.auth.put("needToLogin", "true");
				game.options.auth.put("username", username.getText());
				game.options.auth.put("password",password.getText());
				game.connectSocket();
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
				stage.addActor(new RegisterScreenForm(game));
			}
		});

		pack();
		setSize(getWidth() + 60, getHeight());
		centerWindow();
		addCloseButton();
	}

	public void configSocketEvents()
	{
		this.game.socket.on(Socket.EVENT_CONNECT_ERROR, args -> {

			JSONObject message = (JSONObject) args[0];
			if(stage == null)
			{
				stage = getStage();
			}

			try {
				Dialogs.showErrorDialog(stage,message.getString("message"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		});

		this.game.socket.on(Socket.EVENT_CONNECT, args -> {
			Gdx.app.log("SocketIO", "Connected");

			game.player = new Player();
			game.creator.createEntity(game.player,-1.0f,-1.0f);
			readyToChangeScreen = true;
		});

	}
}
