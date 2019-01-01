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


    String URL_UPDATE_STATUS = "https://tarcomm.000webhostapp.com/updateStatus.php";
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



}
