package de.igorlueckel.andropiled.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SVBar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.igorlueckel.andropiled.MainActivity;
import de.igorlueckel.andropiled.R;
import de.igorlueckel.andropiled.animation.AutomaticColorWheel;
import de.igorlueckel.andropiled.animation.SimpleColor;
import de.igorlueckel.andropiled.events.ColorChangedEvent;
import de.igorlueckel.andropiled.helpers.VideoAnalyzer;
import de.igorlueckel.andropiled.models.LedDevice;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EffectsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EffectsFragment extends Fragment {

    MainActivity mainActivity;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ColorWheelFragment.
     */
    public static EffectsFragment newInstance() {
        EffectsFragment fragment = new EffectsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public EffectsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_effects, container, false);
        ButterKnife.inject(this, root);
        mainActivity = (MainActivity) getActivity();
        return root;
    }

    @OnClick(R.id.buttonAutomaticColorWheel)
    void onAutomaticColorWheelClick() {
        mainActivity.forwardAnimation(new AutomaticColorWheel());
    }

    //        VideoAnalyzer videoAnalyzer = new VideoAnalyzer(R.raw.fire_1, getActivity().getApplicationContext());
//        videoAnalyzer.analyze(new LedDevice());
}
