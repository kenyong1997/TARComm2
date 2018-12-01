package com.turkfyp.tarcomm2.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.turkfyp.tarcomm2.R;
import com.turkfyp.tarcomm2.guillotine.animation.GuillotineAnimation;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewProfileActivity extends AppCompatActivity {

    private static final long RIPPLE_DURATION = 250;

    TextView tvProfileName, tvProfileFaculty, tvProfileCourse, tvProfileEmail, tvProfilePhone,tvProfileBioData;
    Button btnEditProfile;

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

        tvProfileName.setText(preferences.getString("loggedInUser",""));
        tvProfileEmail.setText(preferences.getString("email", ""));
        tvProfilePhone.setText(preferences.getString("contactNo", ""));
        tvProfileFaculty.setText(preferences.getString("faculty",""));
        tvProfileCourse.setText(preferences.getString("course",""));
        tvProfileBioData.setText(preferences.getString("biodata",""));

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
    private Session session;
    public void logout_onclick(View view){
        session = new Session(view.getContext());
        session.setLoggedIn(false);
        finish();
        Intent i = new Intent (this,LoginActivity.class);
        startActivity(i);
    }
}
