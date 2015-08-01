package de.igorlueckel.andropiled.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.skyfishjy.library.RippleBackground;
import com.wnafee.vector.compat.ResourcesCompat;

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

    @InjectView(R.id.devicesRecyclerView)
    RecyclerView recyclerView;

    @InjectView(R.id.imageDeviceSearch)
    ImageView scanningImageView;

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
        // Prevent the view hanging in the middle of nowhere at the beginning
        // Because we are using a RecyclerView in a ScrollView - yeah I know...
        recyclerView.setFocusable(false);
        scanningImageView.setImageDrawable(ResourcesCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.ic_settings_input_antenna));
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public void onEvent(ColorChangedEvent event) {
        // We can not change the ripple background color :/
    }
}
