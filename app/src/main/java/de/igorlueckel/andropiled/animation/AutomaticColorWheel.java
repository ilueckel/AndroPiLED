package de.igorlueckel.andropiled.animation;

import android.graphics.Color;

import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import de.igorlueckel.andropiled.events.DeviceSelectedEvent;
import de.igorlueckel.andropiled.models.LedDevice;

/**
 * Created by Igor on 18.08.2015.
 */
public class AutomaticColorWheel extends AbstractAnimation {

    /**
     * Colors to construct the color wheel using {@link android.graphics.SweepGradient}.
     */
    private static final int[] COLORS = new int[] { 0xFFFF0000, 0xFFFF00FF,
            0xFF0000FF, 0xFF00FFFF, 0xFF00FF00, 0xFFFFFF00, 0xFFFF0000 };

    LedDevice ledDevice;
    float angle = 0;

    /**
     * Default constructor
     */
    public AutomaticColorWheel() {
        setIsInfinite(true);
        setTickDuration(1000/6);
        DeviceSelectedEvent deviceSelectedEvent = EventBus.getDefault().getStickyEvent(DeviceSelectedEvent.class);
        if (deviceSelectedEvent != null)
            ledDevice = deviceSelectedEvent.getDevice();
    }

    @Override
    public void run() {
        if (ledDevice == null || getNetworkService() == null || isStopped())
            return;
        if (getAnimationEventHandler() != null)
            getAnimationEventHandler().onAnimationStarted();

        while (true) {
            if (isStopped())
                return;

            int color = calculateColor((float) Math.toRadians(angle));

            int[] colorsToSend = new int[ledDevice.getNumLeds()];
            for (int i = 0; i < ledDevice.getNumLeds(); i++)
                colorsToSend[i] = color;
            setLastColor(colorsToSend);
            getNetworkService().sendColor(colorsToSend);

            angle = (angle + 1) % 360;
            try {
                TimeUnit.MILLISECONDS.sleep(getTickDuration());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Calculate the color using the supplied angle.
     *
     * @param angle The selected color's position expressed as angle (in rad).
     *
     * @return The ARGB value of the color on the color wheel at the specified
     *         angle.
     */
    private int calculateColor(float angle) {
        float unit = (float) (angle / (2 * Math.PI));
        if (unit < 0) {
            unit += 1;
        }

        if (unit <= 0) {
            return COLORS[0];
        }
        if (unit >= 1) {
            return COLORS[COLORS.length - 1];
        }

        float p = unit * (COLORS.length - 1);
        int i = (int) p;
        p -= i;

        int c0 = COLORS[i];
        int c1 = COLORS[i + 1];
        int a = ave(Color.alpha(c0), Color.alpha(c1), p);
        int r = ave(Color.red(c0), Color.red(c1), p);
        int g = ave(Color.green(c0), Color.green(c1), p);
        int b = ave(Color.blue(c0), Color.blue(c1), p);

        return Color.argb(a, r, g, b);
    }

    private int ave(int s, int d, float p) {
        return s + Math.round(p * (d - s));
    }
}
