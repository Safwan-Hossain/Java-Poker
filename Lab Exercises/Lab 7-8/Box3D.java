/* 3D Box class
 * Author: Mustafa Haddara
 *
 * This class represents a rectangle as (x,y,z) coordinate representing the 
 * front-facing top-left corner, and a (w,h,d) tuple representing the width,
 * height, depth
 */
public class Box3D {

    // Faces enums
    enum Face {
        FRONT,
        TOP,
        SIDE
    }

    private int[] frontTopLeftCorner;
    private int[] dimensions;

    /* Constructor accepts 2 parameters
     * corner: (x,y,z) tuple for the top left corner
     * size: (w,h,d) tuple containing the width and height
     */
    public Box3D (int[] corner, int[] size) {
        if(corner.length != 3 || size.length != 3) {
            throw new RuntimeException("Invalid argument size");
        }
        this.frontTopLeftCorner = corner;
        this.dimensions = size;
    }

    public int[] getFrontTopLeftCorner() {
        return this.frontTopLeftCorner;
    }

    public void setFrontTopLeftCorner(int[] newCorner) {
        this.frontTopLeftCorner = newCorner;
    }

    public int[] getDimensions() {
        return this.dimensions;
    }

    public void setDimensions(int[] newSize) {
        this.dimensions = newSize;
    }

    public int[] getDimensionsOfFace(Face face) {
        int[] dim = new int[3];
        if (face == Face.FRONT) {
            dim = new int[] { this.dimensions[0], this.dimensions[1] };
        } else if (face == Face.TOP) {
            dim = new int[] { this.dimensions[0], this.dimensions[2] };
        } else if (face == Face.SIDE) {
            dim = new int[] { this.dimensions[1], this.dimensions[2] };
        }

        return dim;
    }

    public int getAreaOfFace(Face face) {
        int[] dim = this.getDimensionsOfFace(face);
        return dim[0] * dim[1];
    }

    public int getPerimeterOfFace(Face face) {
        int[] dim = this.getDimensionsOfFace(face);
        return 2 * (dim[0] + dim[1]);
    }

    public int getVolume() {
        int width  = this.dimensions[0];
        int height = this.dimensions[1];
        int depth  = this.dimensions[2];
        return width * height * depth;
    }

    public int getSurfaceArea() {
        int surfaceArea = 0;
        for (Face face: Face.values()) {
           surfaceArea += (2* this.getAreaOfFace(face));
        }
        return surfaceArea;
    }

    // Example usage
    public static void main(String[] args) {
        Box3D b = new Box3D( new int[] {1,1,1}, new int[] {2,2,2});
        System.out.println(b.getVolume());
        System.out.println(b.getSurfaceArea());
    }
}