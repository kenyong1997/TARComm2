package com.turkfyp.tarcomm2.activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.View;


import com.turkfyp.tarcomm2.R;

public class FriendListActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    int tabNumber =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        Bundle extras = getIntent().getExtras();
        tabNumber = extras.getInt("tabnumber");
        //Tab Fragment
        tabLayout = (TabLayout) findViewById(R.id.tab_friendlist);

        //create tabs title
        tabLayout.addTab(tabLayout.newTab().setText("Friends"));
        tabLayout.addTab(tabLayout.newTab().setText("Request"));
        tabLayout.addTab(tabLayout.newTab().setText("Search"));

        //replace default fragment


        if(tabNumber==0) {
            TabLayout.Tab tab = tabLayout.getTabAt(tabNumber);
            tab.select();
            replaceFragment(new FragmentFriendTab1());
        }
        else if(tabNumber==2){
            TabLayout.Tab tab = tabLayout.getTabAt(tabNumber);
            tab.select();
            replaceFragment(new FragmentFriendTab3());
        }




        //handling tab click event
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    replaceFragment(new FragmentFriendTab1());
                } else if(tab.getPosition()==1){
                    replaceFragment(new FragmentFriendTab2());
                }else{
                    replaceFragment(new FragmentFriendTab3());
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
        Intent i = new Intent (this,MainActivity.class);
        startActivity(i);
    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.friendlist_container, fragment);

        transaction.commit();
    }



}
