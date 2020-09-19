import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class PacmanTest {
    Pacman pacmanTest = new Pacman(212, 90, 22, false);
    MapData mapData = MapCollections.getMapData(1);
    Map map = new Map(mapData, 22, 190, 67, false);

    @Test
    void initDirMap() {
        pacmanTest.initDirMap();
        assertEquals(pacmanTest.getDirMapX().get(Directions.LEFT), -1);
    }

    @Test
    void getIsCollidingWithCircle() {
        pacmanTest.setWallShapesAroundPacman(map.getCloseByWallShapes(pacmanTest.getX(), pacmanTest.getY()));
        boolean res = pacmanTest.getIsCollidingWithCircle(pacmanTest.getPacmanCircle());
        assertFalse(res);
    }

    @Test
    void setIsAtIntersectionAndCollidingWithWall() {
        pacmanTest.setWallShapesAroundPacman(map.getCloseByWallShapes(pacmanTest.getX(), pacmanTest.getY()));
        pacmanTest.setIsAtIntersectionAndCollidingWithWall();
        assertFalse(pacmanTest.getIsAtIntersection());
    }

    @Test
    void updatePosition() {
        pacmanTest.updatePosition();
        assertEquals(pacmanTest.getX(), 212);
    }

    @Test
    void replacePacmanToPathCenter() {
        pacmanTest.replacePacmanToPathCenter();
        assertEquals(pacmanTest.getX(), pacmanTest.getCenterX());
    }
}