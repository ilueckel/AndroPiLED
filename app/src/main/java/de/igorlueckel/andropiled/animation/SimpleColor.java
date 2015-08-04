package de.igorlueckel.andropiled.animation;

import de.greenrobot.event.EventBus;
import de.igorlueckel.andropiled.events.DeviceSelectedEvent;
import de.igorlueckel.andropiled.models.LedDevice;

/**
 * Created by Igor on 04.08.2015.
 */
public class SimpleColor extends AbstractAnimation {

    int color;
    LedDevice ledDevice;

    public SimpleColor(int color) {
        setIsInfinite(false);
        setTickDuration(0);
        DeviceSelectedEvent deviceSelectedEvent = EventBus.getDefault().getStickyEvent(DeviceSelectedEvent.class);
        if (deviceSelectedEvent != null)
            ledDevice = deviceSelectedEvent.getDevice();
        this.color = color;
    }

    @Override
    public void run() {
        if (ledDevice == null || getNetworkService() == null || isStopped())
            return;

        String colorCode = intColorToHex(color);
        String output = "";
        for (int i = 0; i < ledDevice.getNumLeds(); i++) {
            output = output + colorCode;
        }
        setLastColor(color);
        getNetworkService().sendMessage(output);
    }
}
