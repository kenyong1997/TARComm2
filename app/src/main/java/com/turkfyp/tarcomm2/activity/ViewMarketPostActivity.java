package com.turkfyp.tarcomm2.activity;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.View;

import com.turkfyp.tarcomm2.R;

public class ViewMarketPostActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_market_post);

        //replace default fragment
        replaceFragment(new FragmentTradingTab4());

    }
    public void onBackClicked(View view){
        finish();
    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.viewpost_container, fragment);

        transaction.commit();
    }

}


