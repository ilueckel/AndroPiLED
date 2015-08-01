package de.igorlueckel.andropiled.fragments;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import de.igorlueckel.andropiled.R;
import de.igorlueckel.andropiled.events.ColorChangedEvent;


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
                }
                return TabFragment.newInstance();
            }

            @Override
            public int getCount() {
                return 4;
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
                    case 3:
                        title = getString(R.string.equalizer);
                        break;
                }
                return title;
            }
        };
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);//this is the new nice thing ;D
    }

    private void setupToolbar() {
        mToolbar.setTitle("AndroPiLed");
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mViewPager.getLayoutParams();
        layoutParams.bottomMargin = -getStatusBarHeight();
        mViewPager.setLayoutParams(layoutParams);
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
}