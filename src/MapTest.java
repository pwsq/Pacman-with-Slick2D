import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MapTest {
    MapData mapData = MapCollections.getMapData(1);
    Map map = new Map(mapData, 22, 190, 67, false);

    @Test
    void update() {
        assertEquals(map.update(212, 90), 100);
    }


    @Test
    void getXFromColNumber() {
        assertEquals(map.getXFromColNumber(1), 212);
    }

    @Test
    void getYFromRowNumber() {
        assertEquals(map.getYFromRowNumber(1), 89);
    }


    @Test
    void getClosestNonCollisionX() {
        assertEquals(map.getClosestNonCollisionX(212.1f), 212);
    }

    @Test
    void getClosestNonCollisionY() {
        assertEquals(map.getClosestNonCollisionX(85), 80);
    }



//	Map classUnderTest = new Map(5, 5);
//
//	@SuppressWarnings("deprecation")
//	@Test
//	void initMapTest() {
//		int rows = 5;
//		int cols = 5;
//		String[] outputMap = classUnderTest.initMap(rows, cols);
//		String[] expectedMap = {
//			"#####",
//			"#...#",
//			"#...#",
//			"#...#",
//			"#####"
//		};
//		assertEquals(outputMap, expectedMap);
//	}

}
