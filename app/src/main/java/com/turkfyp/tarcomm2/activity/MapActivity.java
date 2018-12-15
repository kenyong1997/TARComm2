package com.turkfyp.tarcomm2.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapActivity extends AppCompatActivity {

    private static final long RIPPLE_DURATION = 250;

    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_container);

        SharedPreferences preferences = getSharedPreferences("tarcommUser", MODE_PRIVATE);
        email = preferences.getString("email","");



        //Set up the main page
        MapFragment m = new MapFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame, m).commit();



    }

    @Override
    protected void onStop() {
        super.onStop();

        //make user inactive
        updateStatus(this, "https://tarcomm.000webhostapp.com/updateStatus.php", "OFF");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //make user inactive
        updateStatus(this, "https://tarcomm.000webhostapp.com/updateStatus.php", "OFF");
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
    public void onBackClicked(View view){
        finish();
    }

}
