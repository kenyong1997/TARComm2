package com.turkfyp.tarcomm2.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.turkfyp.tarcomm2.R;

public class EventActivity extends AppCompatActivity {

    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        //Tab Fragment
        tabLayout = (TabLayout) findViewById(R.id.tab_event);

        //create tabs title
        tabLayout.addTab(tabLayout.newTab().setText("Ongoing Events"));
        tabLayout.addTab(tabLayout.newTab().setText("Upcoming Events"));

        //replace default fragment
        replaceFragment(new FragmentEventTab1());


        //handling tab click event
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    replaceFragment(new FragmentEventTab1());
                } else {
                    replaceFragment(new FragmentEventTab2());
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
        transaction.replace(R.id.event_container, fragment);

        transaction.commit();
    }


}
