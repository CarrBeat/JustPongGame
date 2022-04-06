package pong.game;

import com.badlogic.gdx.Game;
import Screens.FirstScreen;

public class PongGame extends Game {
	public static final int WORLD_WIDTH  = 70;
	public static final int WORLD_HEIGHT = 160;
	
	@Override
	public void create () {
		setScreen(new FirstScreen(this));
	}

	@Override
	public void dispose () {

	}
}
