package com.turkfyp.tarcomm2.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewProfileActivity extends AppCompatActivity {

    TextView tvProfileName, tvProfileFaculty, tvProfileCourse, tvProfileEmail, tvProfilePhone,tvProfileBioData,tvProfileFriendCount,tvProfileMarketPost,tvProfileLostFoundPost;
    Button btnEditProfile;
    RequestQueue queue;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        SharedPreferences preferences = getSharedPreferences("tarcommUser", MODE_PRIVATE);


        //------------------------Activity Codes
        tvProfileName = (TextView) findViewById(R.id.tvProfileName);
        tvProfileFaculty = (TextView) findViewById(R.id.tvProfileFaculty);
        tvProfileCourse = (TextView) findViewById(R.id.tvProfileCourse);
        tvProfileEmail = (TextView) findViewById(R.id.tvProfileEmail);
        tvProfilePhone = (TextView) findViewById(R.id.tvProfilePhone);
        tvProfileBioData = (TextView) findViewById(R.id.tvProfileBioData);
        btnEditProfile = (Button) findViewById(R.id.btnEditProfile);
        CircleImageView imgViewProfilePic = (CircleImageView) findViewById(R.id.imgViewProfilePic);

        tvProfileFriendCount = (TextView) findViewById(R.id.tvProfileFriendCount);
        tvProfileMarketPost = (TextView) findViewById(R.id.tvProfileMarketPost);
        tvProfileLostFoundPost = (TextView) findViewById(R.id.tvProfileLostFoundPost);


        tvProfileName.setText(preferences.getString("loggedInUser",""));
        tvProfileEmail.setText(preferences.getString("email", ""));
        tvProfilePhone.setText(preferences.getString("contactNo", ""));
        tvProfileFaculty.setText(preferences.getString("faculty",""));
        tvProfileCourse.setText(preferences.getString("course",""));
        tvProfileBioData.setText(preferences.getString("biodata",""));

        //glide
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.background_white)
                .error(R.drawable.background_white);

        Glide.with(getApplicationContext()).load(preferences.getString("profilePicURL","")).apply(options).into(imgViewProfilePic);
        countPost(this,"https://tarcomm.000webhostapp.com/getTotalPost.php");
    }
    public void onBackClicked(View view){
        finish();
    }
    public void onEditProfileClicked(View view){
        Intent i = new Intent (this,EditProfileActivity.class);

        i.putExtra("image", bitmap);
        this.startActivity(i);
        finish();
    }
    public void countPost(Context context, String url) {
        // Instantiate the RequestQueue
        queue = Volley.newRequestQueue(context);

        try{
            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONArray j = new JSONArray(response);
                                try {

                                    JSONObject postCountResponse = (JSONObject) j.get(0);
                                    String lostFoundPostCount = postCountResponse.getString("totalPost");
                                    tvProfileLostFoundPost.setText(lostFoundPostCount);

                                    postCountResponse = (JSONObject) j.get(1);
                                    String marketPostCount = postCountResponse.getString("totalPost");
                                    tvProfileMarketPost.setText(marketPostCount);

                                    postCountResponse = (JSONObject) j.get(2);
                                    String friendCount = postCountResponse.getString("totalPost");
                                    tvProfileFriendCount.setText(friendCount);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Toast.makeText(getApplicationContext(), "Error: " + volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }){
                @Override
                protected Map<String, String> getParams() {
                    SharedPreferences preferences = getSharedPreferences("tarcommUser", MODE_PRIVATE);
                    String email = preferences.getString("email", "");
                    Map<String, String> params = new HashMap<>();
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
    public void onViewMarketPostClicked(View view){
        Intent i = new Intent (this,ViewMarketPostActivity.class);

        startActivity(i);
    }
    public void onViewLostFoundPostClicked(View view){
        Intent i = new Intent (this,ViewLostFoundPostActivity.class);
        startActivity(i);
    }
    public void onFriendPostClicked(View view){
        Intent i = new Intent (this,FriendListActivity.class);
        startActivity(i);
    }
}
