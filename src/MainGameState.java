import java.util.ArrayList;
import java.util.HashMap;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * MainGameState is the class that combines all elements (map, ghost, pacman) to start and maintain the game using methods overridden from
 * the Java game library Slick2D. In addition, MainGameState class handles connections between key pressed and game controls during game play.
 */
public class MainGameState extends BasicGameState {
	private boolean isDebug;
	private boolean shouldFullReset = false;

	private int gameWindowHeight;
	private int gameWindowWidth;
	
	// YQ: Based on the number of rows/columns on the provided map data
	// and the input window size, how many pixel (xy coordinate unit length)
	// is one row/column equal to.
	private float elementPixelUnit;
	
	// YQ: The ratio for how much width and height for map to be displayed on given window
	private float mapWindowRatio = (float) 0.7;

	private GameInfo gameInfo;
	private HashMap<Integer, Directions> keyMap = new HashMap<>();
	
	private MapData mapData;
	
	private Map map;
	private Pacman pacman;

	private ArrayList<Ghost> ghosts;

	public MainGameState(int gameWindowWidth, int gameWindowHeight, GameInfo gameInfo,boolean isDebug) {
		this.gameInfo = gameInfo;
		this.isDebug = isDebug;

		initKeyMap();
		this.gameWindowWidth = gameWindowWidth;
		this.gameWindowHeight = gameWindowHeight;
		
		this.setupMapGhostsPacmanObjects(false);
	}

	/**
	 * init method is overridden from BasicGame class, it initiates the display of the map, pacman, and ghosts
	 */
	@Override
	public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) {
		this.initMapGhostsPacman();
	}
	
	/**
	 * update method is overridden from BasicGame class, it updates the positions (x, y) and directions (dir) of the pacman and ghosts, 
	 * as well as the dots on the map. Method update gets run every frame of the game.
	 */
	@Override
	public void update(GameContainer container, StateBasedGame stateBasedGame, int delta) {
		if (this.shouldFullReset) {
			this.fullGameReset();
			this.shouldFullReset = false;
		}

		if (this.gameInfo.getLives() > 0) {
			 stateBasedGame.enterState(GameStateManager.mainGameStateId);
		}
		// game over
		else {
			this.gameInfo.updateHighScore();
			HistoryHighScoreState.setCurrentScore(this.gameInfo.getScore());
			stateBasedGame.enterState(GameStateManager.gameOverStateId);
			// we don't want to perform full game reset right after game ends because GameOverState needs the gameInfo
			// for displaying score and level. We perform full game reset next time this update is run (game state
			// is turned active again).
			this.shouldFullReset = true;
			return;
		}

		this.manageGhostPacmanCollision();

		// For some reason the overridden keyPressed method stopped being triggered. Therefore explicitly passing
		// pressed keys here.
		this.keyPressed(container);

		// update for pacman
		ArrayList<Shape> pacmanCloseByWallShapes = this.map.getCloseByWallShapes(this.pacman.getX(), this.pacman.getY());
		float pacmanClosestNonCollisionX = this.map.getClosestNonCollisionX(this.pacman.getX());
		float pacmanClosestNonCollisionY = this.map.getClosestNonCollisionY(this.pacman.getY());
		this.pacman.update(delta, pacmanCloseByWallShapes,pacmanClosestNonCollisionX, pacmanClosestNonCollisionY);

		// update for ghosts
		this.ghosts.forEach(g -> {
			ArrayList<Shape> closeByWallShapes = this.map.getCloseByWallShapes(g.getX(), g.getY());
			float ghostClosestNonCollisionX = this.map.getClosestNonCollisionX(g.getX());
			float ghostClosestNonCollisionY = this.map.getClosestNonCollisionY(g.getY());
			g.update(
					delta,
					closeByWallShapes,
					ghostClosestNonCollisionX,
					ghostClosestNonCollisionY,
					this.pacman
			);
		});
		
		// update for map
		int scoreAdded = this.map.update(this.pacman.getX(), this.pacman.getY());
		this.gameInfo.addScore(scoreAdded);

		if (this.map.getCurrentDotCount() <= 0) {
			this.levelUp();
		}
	}

	/**
	 * render method is overridden from BasicGame class, it gets executed after update method in every frame.
	 * It renders the updated map, ghosts, and pacman, based on the updated data.
	 */
	@Override
	public void render(GameContainer container, StateBasedGame stateBasedGame, Graphics g) {
		this.map.render(g);

		this.pacman.render(g);
		this.ghosts.forEach(ghost -> {
			ghost.render(g);
		});
		this.gameInfo.render(g);
	}

	@Override
	public int getID() {
		return GameStateManager.mainGameStateId;
	}


	/**
	 * keyPressed gets called when a key is pressed on the keyboard.
	 */
	public void keyPressed(GameContainer container) {
		Input input = container.getInput();

		// toggle between pacman and Arvind animations as easter egg
		if (input.isKeyPressed(Input.KEY_P)) {
			this.pacman.toggleShouldShowArvind();
		}

		for (Integer key : keyMap.keySet()) {
			if (input.isKeyPressed(key)) {
				this.pacman.setNextDirection(keyMap.get(key));
			}
		}
	}

	/**
	 * Initiate key map.
	 */
	private void initKeyMap() {
		this.keyMap.put(Input.KEY_LEFT, Directions.LEFT);
		this.keyMap.put(Input.KEY_RIGHT, Directions.RIGHT);
		this.keyMap.put(Input.KEY_UP, Directions.UP);
		this.keyMap.put(Input.KEY_DOWN, Directions.DOWN);
	}

	/**
	 * Load map data from existing map collections.
	 */
	private void pickMapDataFromCollection() {
		int currentLevel = this.gameInfo.getLevel();

		int availableMapCount = MapCollections.getAvailableMapCount();

		if (currentLevel <= availableMapCount) {
			this.mapData = MapCollections.getMapData(currentLevel - 1);
		}
		else {
			this.mapData = MapCollections.getMapData(availableMapCount - 1);
		}
	}

	/**
	 * Fit the map fully to the window by returning the smaller convertionRatio between width and height
	 */
	private float getElementPixelUnit(int mapRowCount, int mapColumnCount, float gameWindowWidth,
									  float gameWindowHeight) {
		float widthConversionRatio = (float) (gameWindowWidth * 1.00 / mapColumnCount);
		float heightConversionRatio = (float) (gameWindowHeight * 1.00 / mapRowCount);
		// when width restricts the size of the map on the game window
		return widthConversionRatio < heightConversionRatio
				? widthConversionRatio * this.mapWindowRatio
				: heightConversionRatio * this.mapWindowRatio;
	}

	/**
	 * Find the x for origin of the map for it to be displayed at the center of the screen using 70% of the width
	 */
	private float getMapOriginX() {
		return (this.gameWindowWidth - mapData.mapArray[0].length * this.elementPixelUnit) / 2;
	}

	/**
	 * Find the y for origin of the map for it to be displayed at the center of the screen using 70% of the height
	 */
	private float getMapOriginY() {
		return (this.gameWindowHeight - mapData.mapArray.length * this.elementPixelUnit) / 2;
	}

	/**
	 * Manage the scenario where ghost run into the pacman
	 */
	private void manageGhostPacmanCollision() {
		boolean isPacmanKilled = false;
		for (Ghost ghost : this.ghosts) {
			if (ghost.getIsCollidingWithPacman()) {
				isPacmanKilled = true;
				break;
			}
		}

		if (isPacmanKilled) {
			this.gameInfo.setLives(this.gameInfo.getLives() - 1);
			this.pacman.reset();

			for (Ghost ghost: this.ghosts) {
				ghost.rest();
			}
		}
	}

	/**
	 * Go to the next level
	 */
	private void levelUp() {
		this.gameInfo.setLevel(this.gameInfo.getLevel() + 1);
		this.setupMapGhostsPacmanObjects(true);
	}

	/**
	 * Initiate objects for map, ghosts and pacman
	 */
	private void setupMapGhostsPacmanObjects(boolean isLevelUpOrFullReset) {
		this.pickMapDataFromCollection();
		this.elementPixelUnit = this.getElementPixelUnit(
				mapData.mapArray[0].length,
				mapData.mapArray.length,
				gameWindowWidth,
				gameWindowHeight);

		this.map = new Map(mapData, elementPixelUnit, this.getMapOriginX(), this.getMapOriginY(), isDebug);

		RowColTuple[] ghostsOnMap = this.mapData.ghostRowColTuples;
		this.ghosts = new ArrayList<>();
		for (int i = 0; i < ghostsOnMap.length; i++) {
			this.ghosts.add(
					new Ghost(
							this.map.getXFromColNumber(ghostsOnMap[i].col),
							this.map.getYFromRowNumber(ghostsOnMap[i].row),
							this.elementPixelUnit,
							isDebug,
							i
					)
			);
		}

		this.pacman = new Pacman(
				this.map.getXFromColNumber(this.mapData.pacmanRowColTuple.col),
				this.map.getYFromRowNumber(this.mapData.pacmanRowColTuple.row),
				elementPixelUnit,
				isDebug);

		if (isLevelUpOrFullReset) {
			this.initMapGhostsPacman();
		}
	}

	/**
	 * Initiate map, ghosts and pacman
	 */
	private void initMapGhostsPacman() {
		this.map.init();
		this.pacman.init();
		this.ghosts.forEach(ghost -> ghost.init());
	}

	/**
	 * Reset the entire game
	 */
	private void fullGameReset() {
		this.gameInfo.reset();
		this.setupMapGhostsPacmanObjects(true);
	}
}
