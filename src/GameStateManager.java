import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * GameStateManager contains the main method of this project and manages between different states of the game such as
 * game state, game over state, etc.
 */
public class GameStateManager extends StateBasedGame {
	public static final int gameWindowWidth = 800;
	public static final int gameWindowHeight = 600;

	public static final int mainGameStateId = 0;
	public static final int gameOverStateId = 1;
	public static final int historyHighScoreStateId = 2;

	public GameStateManager(String title) {
		super(title);
	}

	/**
	 * Overridden from StateBasedGame to initialize all the states in this game into this class.
	 * @param gameContainer required by original method but not actually used in this overridden version
	 * @throws SlickException
	 */
	@Override
	public void initStatesList(GameContainer gameContainer) throws SlickException {
		// gameInfo object will be shared between MainGateState and GameOverState
		GameInfo gameInfo = new GameInfo();

		this.addState(new MainGameState(gameWindowWidth, gameWindowHeight, gameInfo, false));
		this.addState(new GameOverState(gameInfo));
		this.addState(new HistoryHighScoreState());
	}

	/**
	 * Overridden from StateBaseGame to pass on the key press into mainGameState
	 * @param key the int representation of the key pressed
	 * @param c the character of the key pressed
	 */
	@Override
	public void keyPressed(int key, char c) {
		this.getState(mainGameStateId).keyPressed(key, c);
	}

	/**
	 * The main method of the whole game that creates the appGameComtainer object, initializes it, and start it.
	 * @param args
	 * @throws SlickException
	 */
	public static void main(String[] args) throws SlickException {
		AppGameContainer appGameContainer = new AppGameContainer(new GameStateManager("Pacman"));

		appGameContainer.setDisplayMode(gameWindowWidth, gameWindowHeight, false);
		appGameContainer.setShowFPS(false);
		appGameContainer.setAlwaysRender(true);
		appGameContainer.setMinimumLogicUpdateInterval(1000 / 60);
		appGameContainer.start();
	}
}
