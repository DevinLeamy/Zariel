package engine.graphics;

public class Lighting {
    /**
     * @param sideOne block (x, y + 1, z - 1) active ? 1 : 0
     * @param sideTwo block (x + 1, y + 1, z) active ? 1 : 0
     * @param corner block (x + 1, y + 1, z - 1) active ? 1 : 0
     * @return ambient occlusion factor
     */
    public static int calculateAO(int sideOne, int sideTwo, int corner) {
        if (sideOne == 1 && sideTwo == 1) {
            return 0;
        }

        return 3 - (sideOne + sideTwo + corner);
    }
}
