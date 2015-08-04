package de.igorlueckel.andropiled.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import de.igorlueckel.andropiled.MainActivity;
import de.igorlueckel.andropiled.R;
import de.igorlueckel.andropiled.animation.AbstractAnimation;
import de.igorlueckel.andropiled.events.DeviceSelectedEvent;
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

    static int notificationId = 6802;

    private final IBinder mBinder = new NetworkBinder();

    UdpMessenger udpMessenger;
    List<LedDevice> discoveredRaspberryAddresses;
    Thread discoveredRaspberryThread;
    ExecutorService executorService = Executors.newCachedThreadPool();
    boolean stopped = false;
    AbstractAnimation currentAnimation;

    // Active device
    LedDevice selectedDevice;

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
                        EventBus.getDefault().post(new DevicesResponseEvent(getDiscoveredRaspberryAddresses()));
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
                    for (int i = 0; i < 10; i++)
                        checkForIp();

                    while (discoveredRaspberryAddresses != null && !stopped) {
                        if (discoveredRaspberryAddresses.isEmpty())
                            TimeUnit.SECONDS.sleep(5);
                        else
                            TimeUnit.SECONDS.sleep(10);
                        checkForIp();
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
        if (dataString != null && dataString.equals("start Discovery")) {
            initalizeDiscovery();
        }
        if (dataString != null && dataString.equals("stop")) {
            stopService();
        }
        if (dataString != null && dataString.equals("show notification")) {
            showNotification();
        }
        if (dataString != null && dataString.equals("hide notification")) {
            hideNotification();
        }
    }

    void stopService() {
        EventBus.getDefault().unregister(this);
        udpMessenger.stopMessageReceiver();
        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        stopped = true;
        hideNotification();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (currentAnimation == null || !currentAnimation.isAlive())
            return;
        stopService();
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

    public void onEvent(DeviceSelectedEvent deviceSelectedEvent) {
        selectedDevice = deviceSelectedEvent.getDevice();
        int location = getLocation(discoveredRaspberryAddresses, deviceSelectedEvent.getDevice());
        for (int i = 0; i < discoveredRaspberryAddresses.size(); i++)
            if (i != location)
                discoveredRaspberryAddresses.get(i).setSelected(false);
        EventBus.getDefault().post(new DevicesResponseEvent(getDiscoveredRaspberryAddresses()));
    }

    private int getLocation(List<LedDevice> data, LedDevice entity) {
        for (int j = 0; j < data.size(); ++j) {
            LedDevice newEntity = data.get(j);
            if (entity.equals(newEntity)) {
                return j;
            }
        }
        return -1;
    }

    void showNotification() {
        if (currentAnimation == null || !currentAnimation.isAlive())
            return;

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent stopIntent = new Intent(this, NetworkService.class);
        stopIntent.putExtra("action", "stop");
        PendingIntent pendingIntentStop = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // build notification
        // the addAction re-use the same intent to keep the example short
        Notification notification = new Notification.Builder(this)
                .setContentTitle("AndroPiLed")
                .setContentText("Netzwerkservice ist aktiv und steuert die LEDs")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pIntent)
                .setOngoing(true)
                .addAction(R.drawable.ic_stop, "Stop", pendingIntentStop)
                .addAction(R.drawable.ic_wb_incandescent, "Turn off", pIntent)
                .build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(notificationId, notification);
    }

    void hideNotification() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
    }

    public void setCurrentAnimation(AbstractAnimation abstractAnimation) {
        this.currentAnimation = abstractAnimation;
        this.currentAnimation.setNetworkService(this);
        this.currentAnimation.start();
    }

    public void sendMessage(String message) {
        if (selectedDevice != null) {
            udpMessenger.sendData(message, selectedDevice.getAddress(), 6803);
        }
    }

    public LedDevice getSelectedDevice() {
        return selectedDevice;
    }
}
