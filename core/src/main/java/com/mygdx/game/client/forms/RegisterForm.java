package com.mygdx.game.client.forms;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.form.SimpleFormValidator;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.mygdx.game.MyGdxGame;

public class RegisterForm extends VisWindow {
    public RegisterForm(final MyGdxGame game)
    {
        super("REGISTRATION");

        VisTextButton cancelButton = new VisTextButton("CANCEL");
        VisTextButton registerButton = new VisTextButton("REGISTER");

        final VisValidatableTextField username = new VisValidatableTextField();
        final VisValidatableTextField password = new VisValidatableTextField();
        final VisValidatableTextField confirmPassword = new VisValidatableTextField();

        VisLabel errorLabel = new VisLabel();
        errorLabel.setColor(Color.RED);

        VisTable buttonTable = new VisTable(true);
        buttonTable.add(errorLabel).expand().fill();
        buttonTable.add(cancelButton);
        buttonTable.add(registerButton);

        add(new VisLabel("E-MAIL"));
        add(username).expand().fill();
        row();
        add(new VisLabel("PASSWORD"));
        add(password).expand().fill();
        row();
        add(new VisLabel("PASSWORD"));
        add(confirmPassword).expand().fill();
        row();
        add(buttonTable).fill().expand().colspan(2).padBottom(10);
        buttonTable.center();

        SimpleFormValidator validator; //for GWT compatibility
        validator = new SimpleFormValidator(registerButton, errorLabel, "smooth");
        validator.setSuccessMessage("READY!");
        validator.notEmpty(username, "E-MAIL EMPTY");
        validator.notEmpty(password, "PASSWORD EMPTY");
        validator.notEmpty(confirmPassword, "PASSWORD EMPTY");

        registerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                String password1 = password.getText();
                String password2 = confirmPassword.getText();

                if(password1.equals(password2))
                {
                    game.getClientHandler().getOptions().auth.put("needToLogin", "false");
                    game.getClientHandler().getOptions().auth.put("username", username.getText());
                    game.getClientHandler().getOptions().auth.put("password",password.getText());
                    game.getClientHandler().connectSocket();
                }
                else
                {
                    Dialogs.showErrorDialog(getStage(), "Passwords are not the same");
                }
            }
        });

        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                fadeOut();
            }
        });

        pack();
        setSize(getWidth() + 60, getHeight());
        centerWindow();
        addCloseButton();
        
    }
}
