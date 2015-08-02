package de.igorlueckel.andropiled.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.skyfishjy.library.RippleBackground;
import com.wnafee.vector.compat.ResourcesCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import de.igorlueckel.andropiled.R;
import de.igorlueckel.andropiled.adapters.DeviceAdapter;
import de.igorlueckel.andropiled.controls.EmptyRecyclerView;
import de.igorlueckel.andropiled.events.ColorChangedEvent;
import de.igorlueckel.andropiled.events.DeviceDiscoveredEvent;
import de.igorlueckel.andropiled.events.DevicesRequestEvent;
import de.igorlueckel.andropiled.events.DevicesResponseEvent;
import de.igorlueckel.andropiled.models.LedDevice;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConnectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConnectionFragment extends Fragment {

    @InjectView(R.id.rippleDeviceDiscover)
    RippleBackground rippleBackground;

    @InjectView(R.id.connectionFragmentScrollView)
    ScrollView scrollView;

    @InjectView(R.id.devicesRecyclerView)
    EmptyRecyclerView recyclerView;

    @InjectView(R.id.imageDeviceSearch)
    ImageView scanningImageView;

    private DeviceAdapter devicesAdapter;

     /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ConnectionFragment.
     */
    public static ConnectionFragment newInstance() {
        ConnectionFragment fragment = new ConnectionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ConnectionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
//        if (getArguments() != null) {
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_connection, container, false);
        ButterKnife.inject(this, root);
        rippleBackground.startRippleAnimation();
        // Prevent the view hanging in the middle of nowhere at the beginning
        // Because we are using a RecyclerView in a ScrollView - yeah I know...
        recyclerView.setFocusable(false);
        scanningImageView.setImageDrawable(ResourcesCompat.getDrawable(root.getContext(), R.drawable.ic_settings_input_antenna));

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(root.getContext());
        recyclerView.setLayoutManager(layoutManager);
        devicesAdapter = new DeviceAdapter(root.getContext());
        recyclerView.setAdapter(devicesAdapter);
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });
        EventBus.getDefault().post(new DevicesRequestEvent());
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public void onEvent(ColorChangedEvent colorChangedEvent) {
        // We can not change the ripple background color :/
    }

    public void onEvent(DevicesResponseEvent devicesResponseEvent) {
        devicesAdapter.updateData(devicesResponseEvent.getLedDevices());
    }

    public void onEvent(DeviceDiscoveredEvent deviceDiscoveredEvent) {
        List<LedDevice> device = Collections.singletonList(deviceDiscoveredEvent.getDevice());
        devicesAdapter.updateData(device);
    }
}
