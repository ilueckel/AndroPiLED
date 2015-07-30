package de.igorlueckel.andropiled.events;

/**
 * Created by Igor on 16.06.2015.
 */
public class LedStatusEvent {
    boolean isOff = false;

    public LedStatusEvent(boolean isOff) {
        this.isOff = isOff;
    }

    public boolean isOff() {
        return isOff;
    }

    public void setIsOff(boolean isOff) {
        this.isOff = isOff;
    }
}
