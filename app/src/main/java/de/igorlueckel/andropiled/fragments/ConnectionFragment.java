package de.igorlueckel.andropiled.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.skyfishjy.library.RippleBackground;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import de.igorlueckel.andropiled.R;
import de.igorlueckel.andropiled.events.ColorChangedEvent;

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
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_connection, container, false);
        ButterKnife.inject(this, root);
        rippleBackground.startRippleAnimation();
        return root;
    }

    public void onEvent(ColorChangedEvent event) {
        // We can not change the ripple background :/
    }
}
