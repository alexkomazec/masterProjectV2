package com.akomazec.BaseSampleProject.Screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.form.SimpleFormValidator;
import com.kotcrab.vis.ui.widget.*;

public class LoginScreenForm extends VisWindow {

	public LoginScreenForm () {
		super("Enter your credentials");

		VisTextButton cancelButton = new VisTextButton("Cancel");
		VisTextButton acceptButton = new VisTextButton("Login");
		VisTextButton registerButton = new VisTextButton("Register");

		VisValidatableTextField username = new VisValidatableTextField();
		VisValidatableTextField password = new VisValidatableTextField();

		VisLabel errorLabel = new VisLabel();
		errorLabel.setColor(Color.RED);

		VisTable buttonTable = new VisTable(true);
		buttonTable.add(errorLabel).expand().fill();
		buttonTable.add(cancelButton);
		buttonTable.add(acceptButton);
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
		validator = new SimpleFormValidator(acceptButton, errorLabel, "smooth");
		validator.setSuccessMessage("all good!");
		validator.notEmpty(username, "e-mail can not be empty");
		validator.notEmpty(password, "password can not be empty");

		acceptButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				Dialogs.showOKDialog(getStage(), "Credentials", 
				"Username: " + username.getText() +
				"\nPassword: " + password.getText());
				fadeOut();
			}
		});

		cancelButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				Dialogs.showOKDialog(getStage(), "message", "you can't escape this!");
			}
		});

		registerButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				Stage stage = getStage();
				stage.addActor(new RegisterScreenForm());
			}
		});

		pack();
		setSize(getWidth() + 60, getHeight());
		centerWindow();
		addCloseButton();
	}
}
