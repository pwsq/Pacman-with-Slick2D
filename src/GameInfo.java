import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import org.newdawn.slick.Graphics;

/**
 * GameInfo is the class that initializes the score in the game, keeps track of it, and display it on the game window.
 */
public class GameInfo {
	private static final int initialScore = 0;
	private static final int initialLives = 3;
	private static final int initialLevel = 1;

	private int score;
	private int lives;
	private int level;

	public GameInfo() {
		this.reset();
	}

	/**
	 * Adds score based on different increment amount
	 */
	public void addScore(int scoreAdded) {
		this.score += scoreAdded;
	}

	/**
	 * Renders game info printed on the game screen
	 */
	public void render(Graphics g) {
		g.drawString("Press 'P' to see real power", 10, 0);

		g.drawString("Level:" + this.level, 10, 50);
		g.drawString("Score: " + this.score, 10, 70);
		g.drawString("Remaining lives: " + this.lives, 10, 90);
	}

	/**
	 * Getter for lives
	 */
	public int getLives() {
		return this.lives;
	}

	/**
	 * Setter for lives
	 */
	public void setLives(int newLives) {
		this.lives = newLives;
	}

	/**
	 * Getter for score
	 */
	public int getScore() {
		return score;
	}

	/**
	 * Setter for level
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * Getter for legel
	 */
	public int getLevel() {
		return this.level;
	}

	/**
	 * Reset score, lives, level back to initial values
	 */
	public void reset() {
		this.score = initialScore;
		this.lives = initialLives;
		this.level = initialLevel;
	}

	/**
	 * Update the high history scores
	 * @return
	 */
	public boolean updateHighScore() {
		// List<String> lines = Arrays.asList("0", "0", "0", "0", "0", "0", "0", "0", "0", "0");
		Path file = Paths.get("high-scores.txt");
		List<String> lines = Collections.emptyList();
		try {
			// Files.write(file, lines, StandardCharsets.UTF_8);
			lines = Files.readAllLines(file, StandardCharsets.UTF_8); 
		} catch (Exception e) {
			// do nothing...
			return false;
		}
		if (lines.size() != 10) {
			return false;
		}
		List<Integer> intList = lines.stream()
				.map(s -> Integer.parseInt(s))
				.collect(Collectors.toList());
		if (this.getScore() > intList.get(9)) {
			intList.set(9, this.getScore());
		}
		Collections.sort(intList, Collections.reverseOrder()); 
		List<String> newLines = intList.stream()
				.map(s -> Integer.toString(s))
				.collect(Collectors.toList());
		try {
			Files.write(file, newLines, StandardCharsets.UTF_8);
		} catch (Exception e) {
			// do nothing...
			return false;
		}
		return true;
	}
}
