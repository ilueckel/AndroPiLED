package de.igorlueckel.andropiled.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import de.igorlueckel.andropiled.handlers.IncomingPacketHandler;

/**
 * Created by Igor on 12.06.2015.
 */
public class UdpMessenger {
    protected static String DEBUG_TAG = "UDPMessenger";
    protected static final Integer BUFFER_SIZE = 4096;

    private boolean receiveMessages = false;

    protected Context context;
    private DatagramSocket socket;

    private Thread receiverThread;

    List<IncomingPacketHandler> incomingPacketHandlers;
    static UdpMessenger instance;

    /**
     * Class constructor
     * @param context the application's context
     */
    public UdpMessenger(Context context) throws IllegalArgumentException {
        this.context = context;
        incomingPacketHandlers = new ArrayList<>();
        instance = this;
    }

    public static UdpMessenger getInstance(Context context) {
        if (instance == null)
            new UdpMessenger(context);
        return instance;
    }

    /**
     * Sends a broadcast message (TAG EPOCH_TIME message). Opens a new socket in case it's closed.
     * @param message the message to send (multicast). It can't be null or 0-characters long.
     * @return Returns a boolean indicating if sending the message was successful.
     * @throws IllegalArgumentException
     */
    public boolean sendData(String message, InetAddress targetIP, int multicastPort) throws IllegalArgumentException {
        if(message == null || message.length() == 0 ||  multicastPort <= 1024 || multicastPort > 49151)
            throw new IllegalArgumentException();

        // Check for WiFi connectivity
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if(mWifi == null || !mWifi.isConnected()) {
            Log.d(DEBUG_TAG, "Sorry! You need to be in a WiFi network to control your LEDs.");
            return false;
        }

        // Create the send socket
        if(socket == null) {
            try {
                socket = new DatagramSocket();
            } catch (SocketException e) {
                Log.d(DEBUG_TAG, "There was a problem creating the sending socket. Aborting.");
                e.printStackTrace();
                return false;
            }
        }

        // Build the packet
        final DatagramPacket packet;
        byte data[] = UdpMessenger.hexStringToByteArray(message);

        try {
            packet = new DatagramPacket(data, data.length, targetIP, multicastPort);
        } catch (Exception e) {
            Log.d(DEBUG_TAG, e.getMessage());
            e.printStackTrace();
            return false;
        }

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    Log.d(DEBUG_TAG, "There was an error sending the UDP packet. Aborted.");
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        return true;
    }

    private InetAddress getBroadcastAddress() throws IOException {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        // handle null somehow

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) (broadcast >> (k * 8));
        return InetAddress.getByAddress(quads);
    }

    /**
     *
     * @param message
     * @param port Value was 6802
     * @return
     * @throws IllegalArgumentException
     */
    public boolean sendBroadcastMessage(String message, int port) throws IllegalArgumentException {
        if(message == null || message.length() == 0)
            throw new IllegalArgumentException();

        // Check for WiFi connectivity
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if(mWifi == null || !mWifi.isConnected())
        {
            Log.d(DEBUG_TAG, "Sorry! You need to be in a WiFi network in order to send UDP multicast packets. Aborting.");
            return false;
        }

        // Create the send socket
        if(socket == null) {
            try {
                socket = new DatagramSocket();
            } catch (SocketException e) {
                Log.d(DEBUG_TAG, "There was a problem creating the sending socket. Aborting.");
                e.printStackTrace();
                return false;
            }
        }

        // Build the packet
        final DatagramPacket packet;
        //Message msg = new Message(TAG, message);

        byte data[] = message.getBytes();

        try {
            packet = new DatagramPacket(data, data.length, getBroadcastAddress(), port); //ipToString(ip, true)
        } catch (Exception e) {
            Log.d(DEBUG_TAG, e.getMessage());
            e.printStackTrace();
            return false;
        }

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    Log.d(DEBUG_TAG, "There was an error sending the UDP packet. Aborted.");
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        return true;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public void startMessageReceiver() {
        Runnable receiver = new Runnable() {

            @Override
            public void run() {
                byte[] buffer = new byte[BUFFER_SIZE];
                DatagramPacket rPacket = new DatagramPacket(buffer, buffer.length);
                DatagramSocket rSocket;

                try {
                    rSocket = new DatagramSocket(6802);
                } catch (IOException e) {
                    Log.d(DEBUG_TAG, "Impossible to create a new MulticastSocket on port " + 6802);
                    e.printStackTrace();
                    return;
                }

                while(receiveMessages) {
                    try {
                        rSocket.receive(rPacket);
                    } catch (IOException e1) {
                        Log.d(DEBUG_TAG, "There was a problem receiving the incoming message.");
                        e1.printStackTrace();
                        continue;
                    }

                    if(!receiveMessages)
                        break;

                    byte data[] = rPacket.getData();
                    int i;
                    for(i = 0; i < data.length; i++) {
                        if(data[i] == '\0')
                            break;
                    }

                    for (IncomingPacketHandler incomingPacketHandler : incomingPacketHandlers)
                        incomingPacketHandler.onReceive(rPacket);

//                    String messageText;
//
//                    try {
//                        messageText = new String(data, 0, i, "UTF-8");
//                    } catch (UnsupportedEncodingException e) {
//                        Log.d(DEBUG_TAG, "UTF-8 encoding is not supported. Can't receive the incoming message.");
//                        e.printStackTrace();
//                        continue;
//                    }
//
//                    try {
//                        targetIP = rPacket.getAddress();
//                        //incomingMessage = new Message(messageText, rPacket.getAddress());
//                        //Toast.makeText(context, messageText + " " + targetIP.toString(), Toast.LENGTH_SHORT).show();
//                    } catch (IllegalArgumentException ex) {
//                        Log.d(DEBUG_TAG, "There was a problem processing the message: " + messageText);
//                        ex.printStackTrace();
//                        continue;
//                    }

                    //incomingMessageHandler.post(getIncomingMessageAnalyseRunnable());
                }
                rSocket.close();
            }

        };

        receiveMessages = true;
        if(receiverThread == null)
            receiverThread = new Thread(receiver);

        if(!receiverThread.isAlive())
            receiverThread.start();
    }

    public void stopMessageReceiver() {
        receiveMessages = false;
    }

    public static String ipToString(int ip, boolean broadcast) {
        String result = "";

        Integer[] address = new Integer[4];
        for(int i = 0; i < 4; i++)
            address[i] = (ip >> 8*i) & 0xFF;
        for(int i = 0; i < 4; i++) {
            if(i != 3)
                result = result.concat(address[i]+".");
            else result = result.concat("255.");
        }
        return result.substring(0, result.length() - 2);
    }

    public void addIncomingPacketHandler(IncomingPacketHandler incomingPacketHandler) {
        this.incomingPacketHandlers.add(incomingPacketHandler);
    }
}
