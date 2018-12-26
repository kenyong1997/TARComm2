package com.turkfyp.tarcomm2.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.turkfyp.tarcomm2.R;

public class AddMarketItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_market_item);

        FragmentAddMarketItem f = new FragmentAddMarketItem();
        getSupportFragmentManager().beginTransaction().replace(R.id.addMarketItem_frame, f).commit();

    }

    public void onBackClicked(View view){
        finish();
    }

}

