package de.igorlueckel.andropiled.events;

/**
 * Created by Igor on 20.06.2015.
 */
public class ColorChangedEvent {
    int color;

    public ColorChangedEvent(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
