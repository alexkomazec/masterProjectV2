package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.config.GameConfig;

public class DesktopLauncher {

	public static void main (String[] arg) {

		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

		/*Set Physical measures of the desktop application*/
		config.setWindowedMode((int) GameConfig.PHYSICAL_WIDTH,(int) GameConfig.PHYSICAL_HEIGHT);

		Lwjgl3Application app = new Lwjgl3Application(MyGdxGame.getInstance(), config){};

	}
}
