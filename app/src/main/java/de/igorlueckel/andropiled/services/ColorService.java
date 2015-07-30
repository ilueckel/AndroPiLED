package de.igorlueckel.andropiled.services;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by Igor on 12.07.2015.
 */
public class ColorService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ColorService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
