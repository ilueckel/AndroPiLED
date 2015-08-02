package de.igorlueckel.andropiled.events;

import de.igorlueckel.andropiled.models.LedDevice;

/**
 * Created by Igor on 02.08.2015.
 */
public class DeviceSelectedEvent {
    LedDevice device;

    public DeviceSelectedEvent(LedDevice device) {
        this.device = device;
    }

    public LedDevice getDevice() {
        return device;
    }

    public void setDevice(LedDevice device) {
        this.device = device;
    }
}
