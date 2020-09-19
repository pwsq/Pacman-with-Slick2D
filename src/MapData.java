/**
 * MapData class defines the structure of each map saved in the game
 */
public class MapData {
	// Row and Column number (0-indexed) on a map where pacman start position is.
	// You can use getXFromColNumber and getYFromRowNumber in Map class to convert
	// row and column numbers to x, y coordinate on the rendered map.
	public RowColTuple pacmanRowColTuple;

	public RowColTuple[] ghostRowColTuples;

	public char[][] mapArray;
	
	public MapData(
			RowColTuple pacmanRowColTuple,
			RowColTuple[] ghostRowColTuples,
			char[][] mapArray
			) {
		this.pacmanRowColTuple = pacmanRowColTuple;
		this.ghostRowColTuples = ghostRowColTuples;
		this.mapArray = mapArray;
	}
}
