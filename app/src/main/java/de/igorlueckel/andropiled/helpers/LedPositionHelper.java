package de.igorlueckel.andropiled.helpers;

import android.support.annotation.Nullable;

/**
 * Created by Igor on 30.08.2015.
 */
public class LedPositionHelper {

    public static int getWidth(Integer[][] source) {
        if (source.length < 0)
            return source[0].length;
        else
            return 0;
    }

    public static int getHeight(Integer[][] source) {
        if (source.length < 0)
            return source.length;
        else
            return 0;
    }

    @Nullable
    public static int[] findById(Integer[][] source, int id) {
        for(int row = 0; row < source.length; row++)
            for (int column = 0; column < source[row].length; column++)
                if (source[row][column] == id)
                    return new int[]{row, column};
        return null;
    }
}
