package com.turkfyp.tarcomm2.activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.turkfyp.tarcomm2.R;
import com.turkfyp.tarcomm2.guillotine.animation.GuillotineAnimation;

import java.util.ArrayList;
import java.util.List;

public class MarketplaceActivity extends AppCompatActivity  {

    private static final long RIPPLE_DURATION = 250;
    //private SectionsPagerAdapter mSectionsPagerAdapter;
    //private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketplace);

        // For side menu
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        FrameLayout trading_layout = (FrameLayout) findViewById(R.id.trading_layout);
        View contentHamburger = (View) findViewById(R.id.content_hamburger);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(null);
        }

        View guillotineMenu = LayoutInflater.from(this).inflate(R.layout.guillotine, null);
        trading_layout.addView(guillotineMenu);

        new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.guillotine_hamburger), contentHamburger)
                .setStartDelay(RIPPLE_DURATION)
                .setActionBarViewForAnimation(toolbar)
                .setClosedOnStart(true)
                .build();


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
//        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
//
//        // Set up the ViewPager with the sections adapter.
//        mViewPager = (ViewPager) findViewById(R.id.vp_trading);
//        mViewPager.setAdapter(mSectionsPagerAdapter);
//
//        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_trading);
//        tabLayout.setupWithViewPager(mViewPager);

        ViewPager viewPager = (ViewPager) findViewById(R.id.vp_trading);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_marketplace);

        // Add Fragments to adapter one by one
        adapter.addFragment(new FragmentTradingTab1(), "Textbooks");
        adapter.addFragment(new FragmentTradingTab2(), "Others");
        adapter.addFragment(new FragmentTradingTab3(), "Your Uploads");
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

    }
    public void map_onclick(View view){
        Intent i = new Intent (this,MapActivity2.class);
        startActivity(i);
    }
    public void highlight_event_onclick(View view){
        Intent i = new Intent (this,MainActivity.class);
        startActivity(i);
    }
    public void market_onclick(View view){
        Intent i = new Intent (this,MarketplaceActivity.class);
        startActivity(i);
    }
    // Adapter for the viewpager using FragmentPagerAdapter
//    public static class SectionsPagerAdapter extends FragmentStatePagerAdapter {
//        public SectionsPagerAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//        @Override
//        public int getCount() {
//            // Show 3 total pages.
//            return 3;
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            switch (position) {
//                case 0:
//                    return new FragmentTradingTab1();
//                case 1:
//                    return new FragmentTradingTab2();
//                case 2:
//                    return new FragmentTradingTab3();
//                default:
//                    return null;
//            }
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            switch (position) {
//                case 0:
//                    return "Textbooks";
//                case 1:
//                    return "Others";
//                case 2:
//                    return "Your Uploads";
//                default:
//                    return null;
//            }
//        }
//    }

    // Adapter for the viewpager using FragmentPagerAdapter
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}

