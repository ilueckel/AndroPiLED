package de.igorlueckel.andropiled.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import de.igorlueckel.andropiled.events.ColorChangedEvent;
import de.igorlueckel.andropiled.events.DeviceDiscoveredEvent;
import de.igorlueckel.andropiled.events.DevicesRequestEvent;
import de.igorlueckel.andropiled.events.DevicesResponseEvent;
import de.igorlueckel.andropiled.handlers.IncomingPacketHandler;
import de.igorlueckel.andropiled.helpers.Preferences;
import de.igorlueckel.andropiled.helpers.UdpMessenger;
import de.igorlueckel.andropiled.models.LedDevice;

/**
 * Created by Igor on 12.07.2015.
 */
public class NetworkService extends IntentService {

    private final IBinder mBinder = new NetworkBinder();

    UdpMessenger udpMessenger;
    List<LedDevice> discoveredRaspberryAddresses;
    Thread discoveredRaspberryThread;
    ExecutorService executorService = Executors.newCachedThreadPool();

    public NetworkService() {
        super("NetworkService");

        // Handle incoming UDP answers from devices
        IncomingPacketHandler incomingPacketHandler = new IncomingPacketHandler() {
            @Override
            public void onReceive(DatagramPacket datagramPacket) {
                String response = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
                if (response.equals("Pi here")) {
                    LedDevice device = new LedDevice();
                    device.setAddress(datagramPacket.getAddress());
                    if (!discoveredRaspberryAddresses.contains(device)) {
                        discoveredRaspberryAddresses.add(device);
                        EventBus.getDefault().post(new DeviceDiscoveredEvent(device));
                    }
                }
            }
        };
        discoveredRaspberryAddresses = new ArrayList<>();
        udpMessenger = new UdpMessenger(this);
        udpMessenger.addIncomingPacketHandler(incomingPacketHandler);
        discoveredRaspberryThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (discoveredRaspberryAddresses != null) {
                        checkForIp();
                        if (discoveredRaspberryAddresses.isEmpty())
                            TimeUnit.SECONDS.sleep(5);
                        else
                            TimeUnit.SECONDS.sleep(30);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String dataString = intent.getStringExtra("action");
        if (dataString.equals("start Discovery")) {
            initalizeDiscovery();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public class NetworkBinder extends Binder {
        public NetworkService getService(){
            return NetworkService.this;
        }
    }

    private void initalizeDiscovery() {
        executorService.submit(discoveredRaspberryThread);
        udpMessenger.startMessageReceiver();
    }

    private void checkForIp() {
        Log.i("", "Checking for IP");
        Preferences preferences = new Preferences(this);
        String ip = preferences.getSharedPreferences().getString("connection.lastip", "");
        if (ip.isEmpty()) {
            Log.i("", "Sending broadcast");
            udpMessenger.sendBroadcastMessage("Hello PiLed", 6802);
        } else {
            try {
                Log.i("", "Trying to resolve saved ip.");
                InetAddress potentialAddress = InetAddress.getByName(ip);
                boolean reachable = potentialAddress.isReachable((int) TimeUnit.SECONDS.toMillis(5));
                if (reachable) {
                    Log.i("", "IP is reachable");
                    LedDevice device = new LedDevice();
                    device.setAddress(potentialAddress);
                    if (!discoveredRaspberryAddresses.contains(device))
                        discoveredRaspberryAddresses.add(device);
                } else {
                    Log.i("", "IP is not reachable. Sending broadcast.");
                    udpMessenger.sendBroadcastMessage("Hello PiLed", 6802);
                }
            } catch (Exception e) {
                Log.e("", "Error. Sending broadcast.", e);
                udpMessenger.sendBroadcastMessage("Hello PiLed", 6802);
            }
        }
    }

    public List<LedDevice> getDiscoveredRaspberryAddresses() {
        return discoveredRaspberryAddresses;
    }

    public void onEvent(DevicesRequestEvent event) {
        EventBus.getDefault().post(new DevicesResponseEvent(getDiscoveredRaspberryAddresses()));
    }
}
