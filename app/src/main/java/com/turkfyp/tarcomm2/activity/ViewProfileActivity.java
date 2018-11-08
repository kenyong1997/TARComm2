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

        // For side menu
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        FrameLayout viewprofile_layout = (FrameLayout) findViewById(R.id.viewprofile_layout);
        View contentHamburger = (View) findViewById(R.id.content_hamburger);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(null);
        }

        View guillotineMenu = LayoutInflater.from(this).inflate(R.layout.guillotine, null);
        viewprofile_layout.addView(guillotineMenu);

        TextView tvUserFullName = (TextView) findViewById(R.id.tvUserFullName);

        SharedPreferences preferences = getSharedPreferences("tarcommUser", MODE_PRIVATE);

        //Set User Name on Navigation Bar
        tvUserFullName.setText(preferences.getString("loggedInUser",""));

        //Set Profile Picture on Navigation Bar
        String imageURL = preferences.getString("profilePicURL","");

        //For Glide image
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.background_white)
                .error(R.drawable.background_white);

        CircleImageView profile_image = (CircleImageView) findViewById(R.id.profile_image);
        CircleImageView imgViewProfilePic = (CircleImageView) findViewById(R.id.imgViewProfilePic);

        //Navigation Image
        Glide.with(getApplicationContext()).load(imageURL).apply(options).into(profile_image);

        //User Profile Image
        Glide.with(getApplicationContext()).load(imageURL).apply(options).into(imgViewProfilePic);

        convertImage(imageURL);




        new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.guillotine_hamburger), contentHamburger)
                .setStartDelay(RIPPLE_DURATION)
                .setActionBarViewForAnimation(toolbar)
                .setClosedOnStart(true)
                .build();

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

    //Side Menu Navigation
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
    //End Side Menu Navigation
    public void onViewPostClicked(View view){
        Intent i = new Intent (this,ViewPostActivity.class);
        startActivity(i);
    }
    //Get Profile Image for Navigation Menu
    private void convertImage(String imageURL){
        class ConvertImage extends AsyncTask<String, Void, Bitmap> {

            @Override
            protected Bitmap doInBackground(String... strings) {
                String imageURL = strings[0];

                try {
                    bitmap = Glide.with(getApplicationContext())
                            .asBitmap()
                            .load(imageURL)
                            .submit(150,150)
                            .get();

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
            }
        }
        ConvertImage convertImage = new ConvertImage();
        convertImage.execute(imageURL);
    }
}
