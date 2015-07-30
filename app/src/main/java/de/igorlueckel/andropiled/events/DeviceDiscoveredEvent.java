package de.igorlueckel.andropiled.events;

import de.igorlueckel.andropiled.models.LedDevice;

/**
 * Created by Igor on 14.06.2015.
 */
public class DeviceDiscoveredEvent {
    LedDevice device;

    public DeviceDiscoveredEvent(LedDevice ledDevice) {
        this.device = ledDevice;
    }

    public LedDevice getDevice() {
        return device;
    }

    public void setDevice(LedDevice device) {
        this.device = device;
    }
}
