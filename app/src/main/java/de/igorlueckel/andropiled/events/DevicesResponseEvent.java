package de.igorlueckel.andropiled.events;

import java.util.List;

import de.igorlueckel.andropiled.models.LedDevice;

/**
 * Created by Igor on 02.08.2015.
 */
public class DevicesResponseEvent {
    List<LedDevice> ledDevices;

    public DevicesResponseEvent(List<LedDevice> ledDevices) {
        this.ledDevices = ledDevices;
    }

    public List<LedDevice> getLedDevices() {
        return ledDevices;
    }

    public void setLedDevices(List<LedDevice> ledDevices) {
        this.ledDevices = ledDevices;
    }
}
