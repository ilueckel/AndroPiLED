package de.igorlueckel.andropiled;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import de.igorlueckel.andropiled.animation.AbstractAnimation;
import de.igorlueckel.andropiled.services.NetworkService;


public class MainActivity extends AppCompatActivity {

    NetworkService networkService;
    boolean networkServiceBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CustomActivityOnCrash.install(this);

        Intent networkServiceIntent = new Intent(getApplicationContext(), NetworkService.class);
        networkServiceIntent.putExtra("action", "start Discovery");
        bindService(networkServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
        startService(networkServiceIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent networkServiceIntent2 = new Intent(getApplicationContext(), NetworkService.class);
        networkServiceIntent2.putExtra("action", "hide notification");
        startService(networkServiceIntent2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onStop() {
        super.onStop();
        Intent networkServiceIntent = new Intent(getApplicationContext(), NetworkService.class);
        networkServiceIntent.putExtra("action", "show notification");
        startService(networkServiceIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void forwardAnimation(AbstractAnimation abstractAnimation) {
        if (networkServiceBound)
            networkService.setCurrentAnimation(abstractAnimation);
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            NetworkService.NetworkBinder binder = (NetworkService.NetworkBinder) service;
            networkService = binder.getService();
            networkServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            networkServiceBound = false;
        }
    };
}
