package com.mygdx.game.android;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.utils.ScreenOrientation;

/** Launches the Android application. */
public class AndroidLauncher extends AndroidApplication implements ScreenOrientation {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
		MyGdxGame myGdxGame = MyGdxGame.getInstance();
		myGdxGame.setScreenOrientation(this);
		initialize(MyGdxGame.getInstance(), configuration);
	}

	@Override
	public void setScreenToPortrait() {
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	@Override
	public void setScreenToLandscape() {
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}
}