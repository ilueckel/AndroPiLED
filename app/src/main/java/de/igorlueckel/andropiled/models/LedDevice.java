package de.igorlueckel.andropiled.models;

import java.net.InetAddress;
import java.util.List;

/**
 * Created by Igor on 29.07.2015.
 */
public class LedDevice {
    String name;
    InetAddress address;
    boolean selected = false;
    int numLeds = 50;

    /**
     * Matrix of the position of the LEDs
     * First index is row, second is the column
     */
    Integer[][] ledPositionMatrix;

    public LedDevice() {
        setName("Test");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getNumLeds() {
        return numLeds;
    }

    public void setNumLeds(int numLeds) {
        this.numLeds = numLeds;
    }

    public Integer[][] getLedPositionMatrix() {
        return ledPositionMatrix;
    }

    public void setLedPositionMatrix(Integer[][] ledPositionMatrix) {
        this.ledPositionMatrix = ledPositionMatrix;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LedDevice device = (LedDevice) o;

        return !(address != null ? !address.equals(device.address) : device.address != null);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (address != null ? address.hashCode() : 0);
        return result;
    }
}
