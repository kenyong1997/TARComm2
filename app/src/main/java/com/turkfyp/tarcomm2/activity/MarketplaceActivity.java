package com.turkfyp.tarcomm2.activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.turkfyp.tarcomm2.R;
import com.turkfyp.tarcomm2.guillotine.animation.GuillotineAnimation;

import java.util.ArrayList;
import java.util.List;

public class MarketplaceActivity extends AppCompatActivity  {

    private static final long RIPPLE_DURATION = 250;
//    private ViewPager viewPager;
    private TabLayout tabLayout;

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


//        viewPager = (ViewPager) findViewById(R.id.vp_trading);
//        setupViewPager(viewPager);
//        tabLayout = (TabLayout) findViewById(R.id.tab_marketplace);
//        tabLayout.setupWithViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tab_marketplace);

        //create tabs title
        tabLayout.addTab(tabLayout.newTab().setText("Want To Buy"));
        tabLayout.addTab(tabLayout.newTab().setText("Want To Sell"));
        tabLayout.addTab(tabLayout.newTab().setText("Want To Trade"));
        tabLayout.addTab(tabLayout.newTab().setText("Your Uploads"));

        //replace default fragment
        replaceFragment(new FragmentTradingTab1());


        //handling tab click event
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    replaceFragment(new FragmentTradingTab1());
                } else if (tab.getPosition() == 1) {
                    replaceFragment(new FragmentTradingTab2());
                } else if (tab.getPosition() == 2) {
                    replaceFragment(new FragmentTradingTab3());
                }else{
                    replaceFragment(new FragmentTradingTab4());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.marketplace_container, fragment);

        transaction.commit();
    }


    //Side Menu Navigation
    public void highlight_event_onclick(View view){
        Intent i = new Intent (this,MainActivity.class);
        startActivity(i);
    }
    public void event_onclick(View view){
        Intent i = new Intent (this,EventActivity.class);
        startActivity(i);
    }
    public void market_onclick(View view){
        Intent i = new Intent (this,MarketplaceActivity.class);
        startActivity(i);
    }
    public void lost_and_found_onclick(View view){
        Intent i = new Intent (this,LostAndFoundActivity.class);
        startActivity(i);
    }
    public void map_onclick(View view){
        Intent i = new Intent (this,MapActivity2.class);
        startActivity(i);
    }

/*
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new FragmentTradingTab1(), "Textbooks");
        adapter.addFragment(new FragmentTradingTab2(), "Others");
        adapter.addFragment(new FragmentTradingTab3(), "Your Uploads");
        viewPager.setAdapter(adapter);
    }

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
    }*/
}

