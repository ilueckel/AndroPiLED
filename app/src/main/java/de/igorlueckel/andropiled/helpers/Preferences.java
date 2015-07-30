package de.igorlueckel.andropiled.helpers;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Igor on 13.06.2015.
 */
public class Preferences {
    SharedPreferences sharedPreferences;

    public Preferences(Context context) {
        sharedPreferences = context.getSharedPreferences("de.igorlueckel.andropiled", Context.MODE_PRIVATE);
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public SharedPreferences.Editor getEditor() {
        return sharedPreferences.edit();
    }
}
