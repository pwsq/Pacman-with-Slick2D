import java.io.Console;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;

/**
 * Map is in charge of the following functionalities: 
 * 1. Initialize the map in game play based on saved map data by drawing the walls and dots
 * 2. Maintain and update the displayed map with number of dots left.
 * 3. Outputs the count of total number of dots, and the number of dots left
 * TODO: Instead of saving the map data in this class, instead create a folder that holds multiple map files representing different maps to be used.
 * 
 */
public class Map {
	private boolean isDebug;
	private boolean isFirstRender = true;

	private int mapDataRowCount;
	private int mapDataColCount;
	
	private float elementPixelUnit;
	private float mapOriginX;
	private float mapOriginY;
	
	// Details of the map to render
	private MapData mapData;
	private char[][] mapArray;

	/**
	 * Getter for wallShape
	 */
	public ArrayList<Shape> getWallShapes() {
		return wallShapes;
	}

	private ArrayList<Shape> wallShapes = new ArrayList<>();

	private int currentDotCount;
	
	private Image wallElementImage;
	private Image fruitImage;
	private Image dotImage;
	
	/**
	 * Constructor
	 */
	public Map(MapData mapData, float elementPixelUnit, float mapOriginX, float mapOriginY, boolean isDebug) {
		this.isDebug = isDebug;

		this.mapData = mapData;
		
		this.elementPixelUnit = elementPixelUnit;
		this.mapOriginX = mapOriginX;
		this.mapOriginY = mapOriginY;

		this.cloneMapArray(this.mapData.mapArray);
		this.mapDataRowCount = this.mapArray.length;
		this.mapDataColCount = this.mapArray[0].length;
	}
	
	/**
	 * init method here gets called in the init method in MainGameState class.
	 * It initiates the display of the map and dots.
	 */
	public void init() {
		try {
			this.wallElementImage = new Image("images/wallElement.jpg");
			this.fruitImage = new Image("images/cherry.png");
			this.dotImage = new Image("images/dot.png");
		} catch (SlickException e) {
			System.out.println("WallElement image cannot be found.");
		}
		this.createWallShapes();
		this.currentDotCount = this.countMapDots();
	}

	/**
	 * update method here gets called in the update method in MainGameState class.
	 * It updates the positions of the dots on the map as well as the count of the remaining dots.
	 */
	public int update(float pacmanX, float pacmanY) {
		int rowNum = -1;
		int colNum = -1;
		for (int i = 0; i < this.mapDataRowCount; i++) {
			float y = this.getYFromRowNumber(i);
			if (Math.abs(y - pacmanY) < 5) {
				rowNum = i;
			}
		}
		for (int j = 0; j < this.mapDataColCount; j++) {
			float x = this.getXFromColNumber(j);
			if (Math.abs(x - pacmanX) < 5) {
				colNum = j;
			}
		}
		if (rowNum == -1 || colNum == -1) {
			return 0;
		}
		if (this.mapArray[rowNum][colNum] == '.') {
			this.mapArray[rowNum][colNum] = ' ';
			return 10;
		}
		if (this.mapArray[rowNum][colNum] == '*') {
			this.mapArray[rowNum][colNum] = ' ';
			return 100;
		}

		this.currentDotCount = this.countMapDots();

		return 0;
	}
	
	/**
	 * render method here gets called in the render method in MainGameState class, which gets
	 * executed after update method in every frame.
	 * It renders the updated map based on the updated data (mainly updated location of dots).
	 */
	public void render(Graphics g) {
		this.drawWalls();
		this.drawWallElementRectangulars(g);
		this.drawDotsAndFruits();

		this.isFirstRender = false;
	}
	
	/**
	 * The method will return the x-coordinate for a given column number.
	 */
	public float getXFromColNumber(int columnNumber) {
		return this.elementPixelUnit * columnNumber + this.mapOriginX;
	}
	
	/**
	 * The method will return the y-coordinate for a given row number.
	 */
	public float getYFromRowNumber(int rowNumber) {
		return this.elementPixelUnit * rowNumber + this.mapOriginY;
	}

	/**
	 * The method can find the closet walls for a given position.
	 */
	public ArrayList<Shape> getCloseByWallShapes(float x, float y) {
		// Get wallShapes that are within 1.5 * elementPixelUnit for both x an dy because collision could only happen
		// with shapes nearby
		ArrayList<Shape> closeByWallShapes = new ArrayList<>(this.wallShapes);
		closeByWallShapes.removeIf(
				s ->
						(Math.abs(s.getX() - x) > 1.5 * this.elementPixelUnit) ||
								(Math.abs(s.getY() - y) > 1.5 * this.elementPixelUnit)
		);

		return closeByWallShapes;
	}

	/**
	 * This method provides x value for repositioning a character to its closest 
	 * non-collision position on the map.
	 */
	public float getClosestNonCollisionX(float currentX) {
		int closestColNumber = this.getClosestCol(currentX);

		return this.getXFromColNumber(closestColNumber);
	}

	/**
	 * This method provides y value for repositioning a character to its closest 
	 * non-collision position on the map.
	 */
	public float getClosestNonCollisionY(float currentY) {
		int closestRowNumber = this.getClosestRow(currentY);

		return this.getYFromRowNumber(closestRowNumber);
	}

	/**
	 * Getter for currentDotCount
	 */
	public int getCurrentDotCount() {
		return this.currentDotCount;
	}

	/**
	 * Counts the number of dots on the map to be used for calculating score.
	 */
	private int countMapDots() {
		int dotCount = 0;
		for (int r = 0; r < this.mapDataRowCount; r++) {
			for (int c = 0; c < this.mapDataColCount; c++) {
				if (this.mapArray[r][c] == '.') {
					dotCount++;
				}
			}
		}

		return dotCount;
	}

	/**
	 * Method to draw wall rectangulars in the graph.
	 */
	private void drawWallElementRectangulars(Graphics g) {
		for (int r = 0; r < this.mapDataRowCount; r++) {
			for (int c = 0; c < this.mapDataColCount; c++) {
				float x = this.getXFromColNumber(c);
				float y = this.getYFromRowNumber(r);
				if (this.isDebug) {
					Rectangle currentWallElementRec = new Rectangle(x, y, this.elementPixelUnit, this.elementPixelUnit);
					g.draw(currentWallElementRec);
				}
			}
		}

	}

	/**
	 * Method to draw walls in the graph.
	 */
	private void drawWalls() {
		for (int r = 0; r < this.mapDataRowCount; r++) {
			for (int c = 0; c < this.mapDataColCount; c++) {
				char elementSymbol = this.mapArray[r][c];
				float x = this.getXFromColNumber(c);
				float y = this.getYFromRowNumber(r);
				if (elementSymbol == '#') { // wall
					Rectangle currentWallElementRec = new Rectangle(x, y, this.elementPixelUnit, this.elementPixelUnit);
					if (!this.isDebug) {
						this.wallElementImage.draw(x, y, this.elementPixelUnit, this.elementPixelUnit);
					}
				}
			}
		}
	}

	/**
	 * Method to create walls in the graph.
	 */
	private void createWallShapes() {
		for (int r = 0; r < this.mapDataRowCount; r++) {
			for (int c = 0; c < this.mapDataColCount; c++) {
				char elementSymbol = this.mapArray[r][c];
				float x = this.getXFromColNumber(c);
				float y = this.getYFromRowNumber(r);
				if (elementSymbol == '#') { // wall
					Rectangle currentWallElementRec = new Rectangle(x, y, this.elementPixelUnit, this.elementPixelUnit);
					this.wallShapes.add(currentWallElementRec);
				}
			}
		}
	}

	/**
	 * Draw dots and fruits in the graph.
	 */
	private void drawDotsAndFruits() {
		for (int r = 0; r < this.mapDataRowCount; r++) {
			for (int c = 0; c < this.mapDataColCount; c++) {
				char elementSymbol = this.mapArray[r][c];
				float x = this.getXFromColNumber(c);
				float y = this.getYFromRowNumber(r);
				if (elementSymbol == '.') { // dots
					this.dotImage.draw(x, y, this.elementPixelUnit, this.elementPixelUnit);
				}
				if (elementSymbol == '*') { // bonus fruit
					this.fruitImage.draw(x, y, this.elementPixelUnit, this.elementPixelUnit);
				}
			}
		}
	}

	/**
	 * Return the closet column number for a given x-coordinate
	 */
	private int getClosestCol(float currentX) {
		return Math.round((currentX - this.mapOriginX) / this.elementPixelUnit);
	}

	/**
	 * Return the closet row number for a given y-coordinate
	 */
	private int getClosestRow(float currentY) {
		return Math.round((currentY - this.mapOriginY) / this.elementPixelUnit);
	}

	/**
	 * Make a clone for the map array
	 */
	private void cloneMapArray(char[][] mapArray) {
		this.mapArray = new char[mapArray.length][];
		for (int i = 0; i < mapArray.length; i++) {
			this.mapArray[i] = mapArray[i].clone();
		}
	}
}
