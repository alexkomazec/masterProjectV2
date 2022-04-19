package com.akomazec.BaseSampleProject;

import com.akomazec.BaseSampleProject.Screens.ConnectToServer;
import com.badlogic.gdx.Game;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class BaseSampleProject extends Game {
	
	private static BaseSampleProject 		instance = null;
	
	private BaseSampleProject()
	{}

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

	@Override
	public void create() {
		setScreen(new ConnectToServer(this));
	}
}