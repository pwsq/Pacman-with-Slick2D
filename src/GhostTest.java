import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GhostTest {
    Ghost ghostTest = new Ghost(212, 145, 22, false, 0);

    @Test
    void getGhostColorFromIndex() {
       assertEquals(ghostTest.getGhostColorFromIndex(0), GhostColors.GREEN);
    }

    @Test
    void setGhostStartDelay() {
        ghostTest.setGhostStartDelay(2);
        assertEquals(ghostTest.getGhostStartDelay(), 4);
    }

    @Test
    void getGhostSpriteFolderLink() {
        assertEquals(ghostTest.getGhostSpriteFolderLink(GhostColors.GREEN, Directions.LEFT), "images/ghosts/green/green_left.png");
    }

    @Test
    void replaceGhostToPathCenter() {
        ghostTest.replaceGhostToPathCenter();
        assertEquals(ghostTest.getX(), 0);
    }

    @Test
    void getReverseDirection() {
        assertEquals(ghostTest.getReverseDirection(Directions.RIGHT), Directions.LEFT);
    }
}