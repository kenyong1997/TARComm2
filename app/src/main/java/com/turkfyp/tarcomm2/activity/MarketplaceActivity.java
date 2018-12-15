package com.turkfyp.tarcomm2.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.turkfyp.tarcomm2.R;
import com.turkfyp.tarcomm2.guillotine.animation.GuillotineAnimation;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class MarketplaceActivity extends AppCompatActivity  {

    private static final long RIPPLE_DURATION = 250;
//    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketplace);


        tabLayout = (TabLayout) findViewById(R.id.tab_marketplace);

        //create tabs title
        tabLayout.addTab(tabLayout.newTab().setText("Want To Sell"));
        tabLayout.addTab(tabLayout.newTab().setText("Want To Buy"));
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
    public void onBackClicked(View view){
        finish();
    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.marketplace_container, fragment);

        transaction.commit();
    }


}

