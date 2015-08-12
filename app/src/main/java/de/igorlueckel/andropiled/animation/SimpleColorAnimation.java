package de.igorlueckel.andropiled.animation;

import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import de.igorlueckel.andropiled.events.DeviceSelectedEvent;
import de.igorlueckel.andropiled.models.LedDevice;

/**
 * Created by Igor on 06.08.2015.
 */
public class SimpleColorAnimation extends AbstractAnimation {

    int startColor[];
    int endColor[];
    LedDevice ledDevice;
    int steps = 255;

    /**
     * Change a color to another with a smooth animation (255 steps)
     * @param startColor The starting color for the animation
     * @param endColor The color that you want to transit into
     * @param duration The duration
     * @param timeUnit The duration unit
     */
    public SimpleColorAnimation(int[] startColor, int[] endColor, long duration, TimeUnit timeUnit) throws Exception {
        setIsInfinite(false);
        // We want at least 30 fps
        steps = Math.max(45, (int) (timeUnit.toSeconds(duration) * 45));
        long waiting = timeUnit.toMillis(duration) / steps;
        setTickDuration(waiting);
        DeviceSelectedEvent deviceSelectedEvent = EventBus.getDefault().getStickyEvent(DeviceSelectedEvent.class);
        if (deviceSelectedEvent != null)
            ledDevice = deviceSelectedEvent.getDevice();

        if (ledDevice.getNumLeds() != startColor.length && ledDevice.getNumLeds() != endColor.length && endColor.length != startColor.length)
            throw new Exception("LED size is not equals");

        this.startColor = startColor;
        this.endColor = endColor;
    }

    @Override
    public void run() {
        if (ledDevice == null || getNetworkService() == null || isStopped())
            return;
        if (getAnimationEventHandler() != null)
            getAnimationEventHandler().onAnimationStarted();

        // 2-dimension array: LED-ID | HSV data
        final float[][] hsvStartColor = new float[ledDevice.getNumLeds()][3];
        final float[][] hsvEndColor = new float[ledDevice.getNumLeds()][3];
        final float[][] hsvSteps = new float[ledDevice.getNumLeds()][3];

        for (int i = 0; i < ledDevice.getNumLeds(); i++) {
            Color.colorToHSV(startColor[i], hsvStartColor[i]);
            Color.colorToHSV(endColor[i], hsvEndColor[i]);

            hsvSteps[i][0] = (hsvStartColor[i][0] - hsvEndColor[i][0]) / steps;
            hsvSteps[i][1] = (hsvStartColor[i][1] - hsvEndColor[i][1]) / steps;
            hsvSteps[i][2] = (hsvStartColor[i][2] - hsvEndColor[i][2]) / steps;
        }

        int currentSteps = 0;
        while (currentSteps < steps) {
            while (isStopped()) {
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException ignore) {  }
            }
            Log.i("", "Start calculation for step " + currentSteps);
            final int[] colorsToSend = new int[ledDevice.getNumLeds()];
            for (int i = 0; i < ledDevice.getNumLeds(); i++) {
                hsvStartColor[i][0] = hsvStartColor[i][0] - hsvSteps[i][0];
                hsvStartColor[i][1] = hsvStartColor[i][1] - hsvSteps[i][1];
                hsvStartColor[i][2] = hsvStartColor[i][2] - hsvSteps[i][2];

                int color = Color.HSVToColor(hsvStartColor[i]);
                colorsToSend[i] = color;
            }
            setLastColor(colorsToSend);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    getNetworkService().sendColor(colorsToSend);
                }
            });
            thread.start();

            try {
                TimeUnit.MILLISECONDS.sleep(getTickDuration());
            } catch (InterruptedException ignore) { }
            currentSteps += 1;
        }

        if (getAnimationEventHandler() != null)
            getAnimationEventHandler().onAnimationFinished();
    }
}
