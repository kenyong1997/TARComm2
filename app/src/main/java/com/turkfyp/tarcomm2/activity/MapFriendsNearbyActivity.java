package com.turkfyp.tarcomm2.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.turkfyp.tarcomm2.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MapFriendsNearbyActivity extends AppCompatActivity {

    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_friends_nearby);


        SharedPreferences preferences = getSharedPreferences("tarcommUser", MODE_PRIVATE);
        email = preferences.getString("email","");


        //Set up the main page
        MapFriendsNearbyFragment m = new MapFriendsNearbyFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.mapFriendFrame, m).commit();
    }
    public void onBackClicked(View view){
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
