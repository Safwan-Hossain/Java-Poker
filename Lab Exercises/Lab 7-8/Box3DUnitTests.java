import static org.junit.Assert .*;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class Box3DUnitTests {
    private static Box3D box;

    @BeforeClass
    public void setup() {
        int[] location = {1, 2, 3};
        int[] dimensions = {2, 2, 2};
        box = new Box3D(location, dimensions);
    }

    @Test
    public void getDimensionsOfFaceTest() {
        int[] output = box.getDimensionsOfFace(Box3D.Face.FRONT);
        int[] expectedOutput = {2, 2};
        assertArrayEquals(output, expectedOutput);
    }

    @Test
    public void getAreaOfFaceTest() {
        int output = box.getAreaOfFace(Box3D.Face.FRONT);
        int expectedOutput = 4;
        assertEquals(output, expectedOutput);
    }

    @Test
    public void getPerimeterOfFaceTest() {
        int output = box.getPerimeterOfFace(Box3D.Face.FRONT);
        int expectedOutput = 8;
        assertEquals(output, expectedOutput);
    }

    @Test
    public void getVolumeTest() {
        int output = box.getVolume();
        int expectedOutput = 8;
        assertEquals(output, expectedOutput);
    }

    @Test
    public void getSurfaceAreaTest() {
        int output = box.getSurfaceArea();
        int expectedOutput = 24;
        assertEquals(output, expectedOutput);
    }
}