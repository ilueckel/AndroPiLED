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
import de.greenrobot.event.EventBus;
import de.igorlueckel.andropiled.MainActivity;
import de.igorlueckel.andropiled.R;
import de.igorlueckel.andropiled.animation.SimpleColor;
import de.igorlueckel.andropiled.events.ColorChangedEvent;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ColorWheelFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ColorWheelFragment extends Fragment {

    @InjectView(R.id.color_picker)
    ColorPicker colorPicker;
    @InjectView(R.id.svbar)
    SVBar svBar;

    MainActivity mainActivity;

    ColorPicker.OnColorChangedListener onColorChangedListener = new ColorPicker.OnColorChangedListener() {
        @Override
        public void onColorChanged(int color) {
            colorPicker.setOldCenterColor(color);
            EventBus.getDefault().post(new ColorChangedEvent(color));

            SimpleColor simpleColor = new SimpleColor(color);
            mainActivity.forwardAnimation(simpleColor);
        }
    };

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ColorWheelFragment.
     */
    public static ColorWheelFragment newInstance() {
        ColorWheelFragment fragment = new ColorWheelFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ColorWheelFragment() {
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
        View root = inflater.inflate(R.layout.fragment_color_wheel, container, false);
        ButterKnife.inject(this, root);
        colorPicker.addSVBar(svBar);
        colorPicker.setOnColorChangedListener(onColorChangedListener);
        mainActivity = (MainActivity) getActivity();
        return root;
    }


}
