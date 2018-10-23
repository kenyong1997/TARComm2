package com.turkfyp.tarcomm2.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.turkfyp.tarcomm2.R;
import com.turkfyp.tarcomm2.guillotine.animation.GuillotineAnimation;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {
    private static final long RIPPLE_DURATION = 250;
    ImageView imgViewEditProfilePic;
    EditText etEditName,etEditContactNo,etEditCourse,etEditBioData;
    Spinner edit_faculty_spinner;
    DatePicker dpEditDOB;
    RadioGroup rgEditGender;
    RadioButton rbEditGender,rb_male,rb_female;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // For side menu
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        FrameLayout editprofile_layout = (FrameLayout) findViewById(R.id.editprofile_layout);
        View contentHamburger = (View) findViewById(R.id.content_hamburger);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(null);
        }

        View guillotineMenu = LayoutInflater.from(this).inflate(R.layout.guillotine, null);
        editprofile_layout.addView(guillotineMenu);

        TextView tvUserFullName = (TextView) findViewById(R.id.tvUserFullName);

        SharedPreferences preferences = getSharedPreferences("tarcommUser", MODE_PRIVATE);

        //Set User Name on Navigation Bar
        tvUserFullName.setText(preferences.getString("loggedInUser",""));

        //Set Profile Picture on Navigation Bar
        String imageURL = preferences.getString("profilePicURL","");
        convertImage(imageURL);

        new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.guillotine_hamburger), contentHamburger)
                .setStartDelay(RIPPLE_DURATION)
                .setActionBarViewForAnimation(toolbar)
                .setClosedOnStart(true)
                .build();

        //------------------------Activity Codes

        imgViewEditProfilePic = (ImageView)findViewById(R.id.imgViewEditProfilePic);
        etEditName = (EditText)findViewById(R.id.etEditName);
        etEditContactNo=(EditText)findViewById(R.id.etEditContactNo);
        etEditCourse=(EditText)findViewById(R.id.etEditCourse);
        etEditBioData= (EditText)findViewById(R.id.etEditBioData);
        edit_faculty_spinner = (Spinner) findViewById(R.id.edit_faculty_spinner);
        dpEditDOB = (DatePicker)findViewById(R.id.dpEditDOB);
        rgEditGender = (RadioGroup) findViewById(R.id.rgEditGender);
        rbEditGender = (RadioButton) findViewById(rgEditGender.getCheckedRadioButtonId());
        rb_male = (RadioButton)findViewById(R.id.rb_male);
        rb_female = (RadioButton) findViewById(R.id.rb_female);

        //set spinner and date
        String faculty = preferences.getString("faculty","");
        String dob = preferences.getString("dateofbirth","");
        String gender = preferences.getString("gender","");

        String stryear=dob.substring(0,4);
        String strmonth=dob.substring(5,7);
        String strday=dob.substring(8,10);


        int year=Integer.parseInt(stryear);
        int month=Integer.parseInt(strmonth);
        int day=Integer.parseInt(strday);

        if(gender.equals("Male")){
            rb_male.setChecked(true);
        }else if(gender.equals("Female")){
            rb_female.setChecked(true);
        }
        etEditName.setText(preferences.getString("loggedInUser", ""));
        etEditContactNo.setText(preferences.getString("contactNo", ""));
        edit_faculty_spinner.setSelection(getIndex(edit_faculty_spinner,faculty));
        dpEditDOB.updateDate(year,month-1,day);
        etEditCourse.setText(preferences.getString("course",""));
        etEditBioData.setText(preferences.getString("biodata",""));

    }
    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }

        return 0;
    }
    public void onEditProfileClicked(View view){
        Intent i = new Intent (this,EditProfileActivity.class);
        startActivity(i);
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

    //Get Profile Image for Navigation Menu
    private void convertImage(String imageURL){
        class ConvertImage extends AsyncTask<String, Void, Bitmap> {

            @Override
            protected Bitmap doInBackground(String... strings) {
                String imageURL = strings[0];

                try {
                    URL url = new URL(imageURL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(input);
                    return myBitmap;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                CircleImageView profile_image = (CircleImageView) findViewById(R.id.profile_image);
                profile_image.setImageBitmap(bitmap);

                CircleImageView imgViewProfilePic = (CircleImageView) findViewById(R.id.imgViewEditProfilePic);
                imgViewProfilePic.setImageBitmap(bitmap);
            }
        }
        ConvertImage convertImage = new ConvertImage();
        convertImage.execute(imageURL);
    }
}
