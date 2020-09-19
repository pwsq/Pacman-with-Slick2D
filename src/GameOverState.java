import org.lwjgl.input.Mouse;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * GameOverState sets up and renders the game over screen
 */
public class GameOverState extends BasicGameState {
    Image backgroundImage;
    Image replayButtonImage;
    Image historyButtonImage;
    
    float imageX;
    float imageY;
    float imageHeight;

    float replayButtonWidth;
    float replayButtonHeight;
    float replayButtonX;
    float replayButtonY;

    float historyButtonWidth;
    float historyButtonHeight;
    float historyButtonX;
    float historyButtonY;

    private GameInfo gameInfo;

    public GameOverState(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
    }

    /**
     * Overridden method from BasicGameState to get the state ID.
     */
    @Override
    public int getID() {
        return GameStateManager.gameOverStateId;
    }

    /**
     * Overridden method from BasicGameState to initialize images used in this state.
     */
    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        this.backgroundImage = new Image("images/gameOverWallpaper.jpg");
        this.replayButtonImage = new Image("images/replayButton.png");
        this.historyButtonImage = new Image("images/historyScore.png");
    }

    /**
     * Overridden method from BasicGameState to render all images and text on game over screen.
     */
    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) throws SlickException {
        float imageScale =  this.getFullWindowImageScale(
                gameContainer.getWidth(),
                gameContainer.getHeight(),
                this.backgroundImage);

        imageX = (gameContainer.getWidth() - imageScale * this.backgroundImage.getWidth()) / 2;
        imageY = (gameContainer.getHeight() - imageScale * this.backgroundImage.getHeight()) / 2;
        imageHeight = imageScale * this.backgroundImage.getHeight();

        this.backgroundImage.draw(imageX, imageY, imageScale);

        this.replayButtonWidth = (float) (gameContainer.getWidth() * 0.2);
        float buttonScale = replayButtonWidth / this.replayButtonImage.getWidth();
        this.replayButtonHeight = buttonScale * this.replayButtonImage.getHeight();
        this.replayButtonX = gameContainer.getWidth() / 2 - this.replayButtonWidth / 2;
        this.replayButtonY = (float) (gameContainer.getHeight() * 0.67);
        this.replayButtonImage.draw(this.replayButtonX, this.replayButtonY, this.replayButtonWidth, this.replayButtonHeight);
        
        this.historyButtonWidth = (float) (gameContainer.getWidth() * 0.2);
        float historyButtonScale = historyButtonWidth / this.replayButtonImage.getWidth();
        this.historyButtonHeight = historyButtonScale * this.replayButtonImage.getHeight();
        this.historyButtonX = gameContainer.getWidth() / 2 - this.historyButtonWidth / 2;
        this.historyButtonY = this.replayButtonY + 60;
        
        this.historyButtonImage.draw(this.historyButtonX, this.historyButtonY, this.historyButtonWidth, this.historyButtonHeight);

        float scoreAndLevelX = (float) gameContainer.getWidth() / 2 - 55;
        graphics.drawString(
                "Your score: " + this.gameInfo.getScore(),
                scoreAndLevelX,
                (float) (gameContainer.getHeight() * 0.3));
        graphics.drawString(
                "Your level: " + this.gameInfo.getLevel(),
                scoreAndLevelX,
                (float) (gameContainer.getHeight() * 0.35));
    }

    /**
     * Overridden method from BasicGameState to check if any button is clicked and perform the state switching related.
     */
    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int i)
            throws SlickException {
    	float posX = Mouse.getX();
    	float posY = this.imageHeight + 2 * this.imageY - Mouse.getY() - 16;
    	// Click history button
    	if ((posX > this.historyButtonX && posX < this.historyButtonX + this.historyButtonWidth)
    			&& (posY > this.historyButtonY && posY < this.historyButtonY + this.historyButtonHeight)
                && Mouse.isButtonDown(0)) {
            stateBasedGame.enterState(GameStateManager.historyHighScoreStateId);
    	}
    	// Click replay button
        if ((posX > this.replayButtonX && posX < this.replayButtonX + this.replayButtonWidth)
                && (posY > this.replayButtonY && posY < this.replayButtonY + this.replayButtonHeight)
                && Mouse.isButtonDown(0)) {
            stateBasedGame.enterState(GameStateManager.mainGameStateId);
        }
    }

    /**
     * Calculates a proper scale between the given image and the provided window sizes to fit the image into the window
     */
    private float getFullWindowImageScale(float windowW, float windowH, Image image) {
        float imageW = image.getWidth();
        float imageH = image.getHeight();

        float widthRatio = windowW / imageW;
        float heightRatio = windowH / imageH;

        return widthRatio < heightRatio ? widthRatio : heightRatio;
    }
}
