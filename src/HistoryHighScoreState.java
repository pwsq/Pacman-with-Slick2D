import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * HistoryHighScoreState inherits from BasicGameState to serve as a state of this game. This state gets accessed when
 * the user clicks the high score button from the GameOverState rendered screen.
 */
public class HistoryHighScoreState extends BasicGameState {
    private Image highScoreImage;
    private Image backButtonImage;
    private static int currentScore = 0;

    private float backButtonWidth;
    private float backButtonHeight;
    private float backButtonX;
    private float backButtonY;

    /**
     * Overridden from BasicGameState class to return the ID of this state.
     * @return the ID of this sate of the game
     */
    @Override
    public int getID() {
        return GameStateManager.historyHighScoreStateId;
    }

    /**
     * Overridden from BasicGameState class to initialize the objects needed for this state.
     * @param gameContainer required by overridden method but not used
     * @param stateBasedGame required by overridden method but not used
     * @throws SlickException
     */
    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
    	this.highScoreImage = new Image("images/highScore.png");
    	this.backButtonImage = new Image("images/backButton.png");
    }

    /**
     * Overridden from BasicGameState class to render images and texts on the screen of this game state for every frame
     * @param gameContainer the container object of the whole game
     * @param stateBasedGame the StateBasedGame object that is not used in this method but required by overriding
     * @param graphics the Graphics object that provides drawing text on the screen in this method
     * @throws SlickException
     */
    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics)
            throws SlickException {
        float iconWidth = (float) (gameContainer.getWidth() * 0.2);
        float iconScale = iconWidth / this.highScoreImage.getWidth();
        float iconHeight = iconScale * this.highScoreImage.getHeight();
        float iconX = gameContainer.getWidth() / 2 - iconWidth / 2;
        float iconY = (float) (gameContainer.getHeight() * 0.15);
        this.highScoreImage.draw(iconX, iconY, iconWidth, iconHeight);

        Path file = Paths.get("high-scores.txt");
		List<String> lines = Collections.emptyList();
		try {
			// Files.write(file, lines, StandardCharsets.UTF_8);
			lines = Files.readAllLines(file, StandardCharsets.UTF_8); 
		} catch (Exception e) {
			// do nothing...
		}
		float scoreY = (float) (iconY + (gameContainer.getHeight() * 0.4));
		boolean currentScorePrinted = false;
		for (String s : lines) {
			if (Integer.parseInt(s) == currentScore && !currentScorePrinted) {
				currentScorePrinted = true;
				graphics.drawString("Current Score: ", gameContainer.getWidth() / 2 - 140, scoreY);
			}
			graphics.drawString(s, gameContainer.getWidth() / 2 - 10, scoreY);
            scoreY += 20;
		}

        this.backButtonX = iconX;
        this.backButtonY = scoreY;
        this.backButtonWidth = iconWidth;
        this.backButtonHeight = iconScale * this.backButtonImage.getHeight();
        this.backButtonImage.draw(this.backButtonX, this.backButtonY, this.backButtonWidth, this.backButtonHeight);
    }

    /**
     * Overridden from BasicGameState to update necessary objects in this class necessary for rendering or other class
     * dependencies every frame of the game.
     * @param gameContainer the GameContainer object that provides basic information of the game such as window size
     * @param stateBasedGame the StateBasedGame object that provides functionality of interacting with other game states
     * @param i int required by overriding the method but not used in this method
     * @throws SlickException
     */
    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int i) throws SlickException {
    	float posX = Mouse.getX();
    	float posY = gameContainer.getHeight() - Mouse.getY() - 16;
    	// Click history button
    	if ((posX > this.backButtonX && posX < this.backButtonX + this.backButtonWidth)
    			&& (posY > this.backButtonY && posY < this.backButtonY + this.backButtonHeight)
                && Mouse.isButtonDown(0)) {
            stateBasedGame.enterState(GameStateManager.gameOverStateId);
    	}

    }

    /**
     * Setter method for currentScore.
     * @param num input value to be set as currentScore
     */
    public static void setCurrentScore(int num) {
    	currentScore = num;
    }
}
