package com.mygdx.game;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.common.InputAdapterWrapper;
import com.mygdx.game.config.GameConfig;

public class KeyboardController extends InputAdapterWrapper {

	private final Vector2 mouseLocation = new Vector2(0,0);

	public KeyboardController()
	{
		abInputCommandList = new boolean[GameConfig.LIST_COMMANDS_MAX];
	}

	@Override
	public boolean keyDown(int keycode) {
		boolean keyProcessed = false;
		switch (keycode)
        {
	        case Keys.LEFT:
				abInputCommandList[GameConfig.LEFT] = true;
	            keyProcessed = true;
	            break;
	        case Keys.RIGHT:
				abInputCommandList[GameConfig.RIGHT] = true;
	            keyProcessed = true;
	            break;
	        case Keys.UP:
				abInputCommandList[GameConfig.UP] = true;
	            keyProcessed = true;
	            break;
	        case Keys.DOWN:
				abInputCommandList[GameConfig.DOWN] = true;
	            keyProcessed = true;
	            break;
			case Keys.SPACE:
				abInputCommandList[GameConfig.SPACE] = true;
				keyProcessed = true;
        }
		return keyProcessed;
	}
	@Override
	public boolean keyUp(int keycode) {
		boolean keyProcessed = false;
		switch (keycode) // switch code base on the variable keycode
        {
	        case Keys.LEFT:
				abInputCommandList[GameConfig.LEFT] = false;
	            keyProcessed = true;
	            break;
	        case Keys.RIGHT:
				abInputCommandList[GameConfig.RIGHT] = false;
	            keyProcessed = true;
	            break;
	        case Keys.UP:
				abInputCommandList[GameConfig.UP] = false;
	            keyProcessed = true;
	            break;
	        case Keys.DOWN:
				abInputCommandList[GameConfig.DOWN] = false;
	            keyProcessed = true;
	            break;
			case Keys.SPACE:
				abInputCommandList[GameConfig.SPACE] = false;
				keyProcessed = true;
        }
		return keyProcessed;
	}
	@Override
	public boolean keyTyped(char character) {
		return false;
	}
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		boolean isDragged = true;
		mouseLocation.x = screenX;
		mouseLocation.y = screenY;

		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		return false;
	}
}
