package com.turkfyp.tarcomm2.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.turkfyp.tarcomm2.R;
import com.turkfyp.tarcomm2.guillotine.animation.GuillotineAnimation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapEventActivity extends AppCompatActivity {

    private static final long RIPPLE_DURATION = 250;

    String URL_UPDATE_STATUS = "https://tarcomm.000webhostapp.com/updateStatus.php";
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_event);

        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        FrameLayout mapEventContainer = (FrameLayout) findViewById(R.id.mapEventContainer);
        View contentHamburger = (View) findViewById(R.id.content_hamburger);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(null);
        }

        View guillotineMenu = LayoutInflater.from(this).inflate(R.layout.guillotine, null);
        mapEventContainer.addView(guillotineMenu);

        TextView tvUserFullName = (TextView) findViewById(R.id.tvUserFullName);

        SharedPreferences preferences = getSharedPreferences("tarcommUser", MODE_PRIVATE);
        email = preferences.getString("email","");

        //Set User Name on Navigation Bar
        tvUserFullName.setText(preferences.getString("loggedInUser",""));

        //Set Profile Picture on Navigation Bar
        String imageURL = preferences.getString("profilePicURL","");

        CircleImageView profile_image = (CircleImageView) findViewById(R.id.profile_image);

        RequestOptions options = new RequestOptions()
                .circleCrop()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.background_white)
                .error(R.drawable.background_white);

        Glide.with(getApplicationContext()).load(imageURL).apply(options).into(profile_image);

        new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.guillotine_hamburger), contentHamburger)
                .setStartDelay(RIPPLE_DURATION)
                .setActionBarViewForAnimation(toolbar)
                .setClosedOnStart(true)
                .build();

        //Set up the main page
        MapEventFragment m = new MapEventFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.mapEventFrame, m).commit();
    }

    @Override
    protected void onStop() {
        super.onStop();

        //make user inactive
        updateStatus(this, URL_UPDATE_STATUS, "OFF");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //make user inactive
        updateStatus(this, URL_UPDATE_STATUS, "OFF");
    }

    //update the status of user to OFF when leave application
    public void updateStatus(Context context, String url, final String status) {
        RequestQueue queue = Volley.newRequestQueue(context);

        //Send data
        try {
            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    url,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            //response =string returned by server to the client
                            JSONObject jsonObject;
                            try {
                                jsonObject = new JSONObject(response);
                                int success = jsonObject.getInt("success");
                                String message = jsonObject.getString("message");
                                if (success == 0) {

                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                                } else {
                                    //SUCCESS
                                    //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Error. " + error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("status", status);
                    params.put("email", email);
                    return params;
                }


                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/x-www-form-urlencoded");
                    return params;
                }
            };


            queue.add(postRequest);
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    private Session session;
    public void logout_onclick(View view){
        session = new Session(view.getContext());

        session.setLoggedIn(false);
        finish();
        Intent i = new Intent (this,LoginActivity.class);
        startActivity(i);
    }


    //Side Menu Navigation - START
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
        Intent i = new Intent (this,MapActivity.class);
        startActivity(i);
    }
    public void view_profile_onclick(View view){
        Intent i = new Intent (this,ViewProfileActivity.class);
        startActivity(i);
    }
    public void map_event_onclick(View view){
        Intent i = new Intent(this,MapEventActivity.class);
        startActivity(i);
    }
    //Side Menu Navigation - END
}