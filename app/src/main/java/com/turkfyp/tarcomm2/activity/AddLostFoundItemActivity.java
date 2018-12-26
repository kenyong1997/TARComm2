package com.turkfyp.tarcomm2.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.turkfyp.tarcomm2.R;

public class AddLostFoundItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lost_found_item);

        FragmentAddLostFound f = new FragmentAddLostFound();
        getSupportFragmentManager().beginTransaction().replace(R.id.addLostItem_frame, f).commit();

    }
    public void onBackClicked(View view){
        finish();
    }

}
