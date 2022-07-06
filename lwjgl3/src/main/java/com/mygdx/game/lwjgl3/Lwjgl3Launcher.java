package com.mygdx.game.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.config.GameConfig;
import com.mygdx.game.utils.ScreenOrientation;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
	public static void main(String[] args) {
		createApplication();
	}

	private static Lwjgl3Application createApplication() {

		/* Hint: Temp ScreenOrientation is just a placeholder, because somwhere in the code
		* is expected that ScreenOrientation is not null
		* */
		MyGdxGame myGdxGame = MyGdxGame.getInstance();
		myGdxGame.setScreenOrientation(new ScreenOrientation() {
			@Override
			public void setScreenToPortrait() {

			}

			@Override
			public void setScreenToLandscape() {

			}
		});
		return new Lwjgl3Application(MyGdxGame.getInstance(), getDefaultConfiguration());
	}

	private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
		Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
		configuration.setTitle("MyGdxGame");
		configuration.useVsync(true);
		//// Limits FPS to the refresh rate of the currently active monitor.
		configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate);
		//// If you remove the above line and set Vsync to false, you can get unlimited FPS, which can be
		//// useful for testing performance, but can also be very stressful to some hardware.
		//// You may also need to configure GPU drivers to fully disable Vsync; this can cause screen tearing.
		/*Set Physical measures of the desktop application*/
		configuration.setWindowedMode((int) GameConfig.PHYSICAL_WIDTH,(int) GameConfig.PHYSICAL_HEIGHT);
		configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
		return configuration;
	}

}