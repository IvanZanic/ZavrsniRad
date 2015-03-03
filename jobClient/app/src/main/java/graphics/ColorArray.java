package graphics;

import android.graphics.Color;

/**
 * Created by ivan on 03.03.15..
 */
public class ColorArray {

    private final static int[] colorArray = new int[12];

    private final static void initColors () {
        colorArray[0] = Color.MAGENTA;
        colorArray[1] = Color.GREEN;
        colorArray[2] = Color.BLUE;
        colorArray[3] = Color.CYAN;
        colorArray[4] = Color.RED;
        colorArray[5] = Color.BLACK;
        colorArray[6] = Color.GRAY;
        colorArray[7] = Color.DKGRAY;
        colorArray[8] = Color.LTGRAY;
        colorArray[9] = Color.TRANSPARENT;
        colorArray[10] = Color.YELLOW;
    }

    public static int[] getColorArray() {
        initColors();
        return colorArray;
    }
}
