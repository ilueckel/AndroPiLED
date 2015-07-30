package de.igorlueckel.andropiled.handlers;

import java.net.DatagramPacket;

/**
 * Created by Igor on 13.06.2015.
 */
public interface IncomingPacketHandler {
    void onReceive(DatagramPacket datagramPacket);
}
