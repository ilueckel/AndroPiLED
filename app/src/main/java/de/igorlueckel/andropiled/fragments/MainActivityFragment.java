package de.igorlueckel.andropiled.fragments;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.igorlueckel.andropiled.MainActivity;
import de.igorlueckel.andropiled.R;
import de.igorlueckel.andropiled.animation.AutomaticColorWheel;
import de.igorlueckel.andropiled.animation.FadeToBlackAnimation;
import de.igorlueckel.andropiled.events.ColorChangedEvent;
import de.igorlueckel.andropiled.models.LedDevice;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    @InjectView(R.id.tab_layout)
    TabLayout mTabLayout;

    @InjectView(R.id.view_pager)
    ViewPager mViewPager;

    @InjectView(R.id.toolbar_tabbar)
    Toolbar mToolbar;

    @InjectView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;

    @InjectView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    @InjectView(R.id.buttonFadeToBlack)
    FloatingActionButton buttonFadeToBlack;

    @InjectView(R.id.buttonAddDevice)
    FloatingActionButton buttonAddDevice;

    MainActivity mainActivity;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.inject(this, view);
        setupTabTextColor();
        setupViewPager();
        setupToolbar();
        mainActivity = ((MainActivity) getActivity());
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void setupTabTextColor() {
        int tabTextColor = getResources().getColor(R.color.titleTextColor);
        mTabLayout.setTabTextColors(tabTextColor, tabTextColor);
    }

    private void setupViewPager() {
        //You could use the normal supportFragmentManger if you like
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0: return ConnectionFragment.newInstance();
                    case 1: return ColorWheelFragment.newInstance();
                    case 2: return EffectsFragment.newInstance();
                }
                return TabFragment.newInstance();
            }

            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                String title = "";
                switch (position) {
                    case 0:
                        title = getString(R.string.connection);
                        break;
                    case 1:
                        title = getString(R.string.color_wheel);
                        break;
                    case 2:
                        title = getString(R.string.color_changer);
                        break;
//                    case 3:
//                        title = getString(R.string.equalizer);
//                        break;
                }
                return title;
            }
        };
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mainActivity.checkForConnectedDevice(coordinatorLayout, position);
                if (position == 0) {
                    buttonFadeToBlack.setVisibility(View.GONE);
                    buttonAddDevice.setVisibility(View.VISIBLE);
                } else {
                    buttonFadeToBlack.setVisibility(View.VISIBLE);
                    buttonAddDevice.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mTabLayout.setupWithViewPager(mViewPager);//this is the new nice thing ;D
    }

    private void setupToolbar() {
        mToolbar.setTitle("AndroPiLed");
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mViewPager.getLayoutParams();
        layoutParams.bottomMargin = -getStatusBarHeight();
        mViewPager.setLayoutParams(layoutParams);
        buttonFadeToBlack.setVisibility(View.GONE);
        buttonAddDevice.setVisibility(View.VISIBLE);
    }

    public void onEvent(ColorChangedEvent event) {
        if (mToolbar != null)
            mToolbar.setBackgroundColor(event.getColor());
        if (mTabLayout != null)
            mTabLayout.setBackgroundColor(event.getColor());
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @OnClick(R.id.buttonFadeToBlack)
    void fadeToBlack() {
        if (mainActivity.getNetworkService() != null && mainActivity.getNetworkService().getCurrentAnimation() != null) {
            int[] lastColors = mainActivity.getNetworkService().getCurrentAnimation().getLastColor();
            int[] targetColors = new int[mainActivity.getNetworkService().getSelectedDevice().getNumLeds()];
            for (int i = 0; i < targetColors.length; i++) {
                targetColors[i] = Color.BLACK;
            }
            try {
                FadeToBlackAnimation simpleColorAnimation = new FadeToBlackAnimation(lastColors, 500, TimeUnit.MILLISECONDS);
                mainActivity.getNetworkService().setCurrentAnimation(simpleColorAnimation);
            } catch (Exception e) {
                e.printStackTrace();
            }
//            mainActivity.getNetworkService().setCurrentAnimation(new AutomaticColorWheel());
        }
    }

    @OnClick(R.id.buttonAddDevice)
    void addDevice() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_adddevice, null);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        EditText textBox = (EditText) dialogView.findViewById(R.id.host);
                        LedDevice ledDevice = new LedDevice();
                        try {
                            ledDevice.setAddress(InetAddress.getByName(textBox.getText().toString()));
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }
                        mainActivity.getNetworkService().addDevice(ledDevice);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        builder.create().show();
    }
}
