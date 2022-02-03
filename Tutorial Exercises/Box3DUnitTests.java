import static org.junit.Assert .*;
import org.junit.BeforeClass;
import org.junit.Test;

public class Box3DUnitTests {
    private static Box3D box;

    @Test
    public void getDimensionsOfFaceTest() {
        int[] location = {1, 2, 3};
        int[] dimensions = {2, 2, 2};
        box = new Box3D(new int[]{1, 2, 3}, new int[]{2, 2, 2});
        assertArrayEquals(box.getDimensionsOfFace(Box3D.Face.FRONT), new int[] {2, 2});
    }
}