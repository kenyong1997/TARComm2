package com.turkfyp.tarcomm2.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.turkfyp.tarcomm2.R;

public class EventDetailsActivity extends AppCompatActivity {
    protected TextView tvDetailEventName, tvDetailEventDate, tvDetailEventDesc, tvDetailEventVenue;
    protected ImageView imageViewDetailEvent;
    protected String eventName, eventDate, eventDesc, eventVenue;
    public static String EVENT_VENUE_LOCATION="" ;
    public static boolean DIRECT_TO_EVENT=false;


    int eventID;

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //No Activity Title
        getSupportActionBar().setTitle(null);

        //Back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        //Link code to UI
        tvDetailEventName = (TextView) findViewById(R.id.tvDetailEventName);
        tvDetailEventDate = (TextView) findViewById(R.id.tvDetailEventDate);
        tvDetailEventDesc = (TextView) findViewById(R.id.tvDetailEventDesc);
        tvDetailEventVenue = (TextView) findViewById(R.id.tvDetailEventVenue);


        imageViewDetailEvent = (ImageView) findViewById(R.id.imageViewDetailEvent);

        pDialog = new ProgressDialog(this);

        //get the extras and values
        Bundle extras = getIntent().getExtras();
        eventName = extras.getString("eventName");
        eventDate = extras.getString("eventDateTime");
        eventDesc = extras.getString("eventDesc");
        eventVenue = extras.getString("eventVenue");


        //set the text and image by using extras value
        tvDetailEventName.setText(eventName);
        tvDetailEventDate.setText("Event Date: " + eventDate);
        tvDetailEventDesc.setText("Event Description: " + eventDesc);


        //tvDetailEventVenue.setText(eventVenue);
        SpannableString content = new SpannableString(eventVenue);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tvDetailEventVenue.setText(content);


        Bitmap image = extras.getParcelable("Image");
        imageViewDetailEvent.setImageBitmap(image);

        String imageURL = extras.getString("ImageURL");
        eventID = Integer.parseInt(imageURL.split("=")[1]);

        tvDetailEventVenue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EVENT_VENUE_LOCATION = tvDetailEventVenue.getText().toString();

                Intent mainActivity = new Intent(EventDetailsActivity.this, MainActivity.class);

                DIRECT_TO_EVENT = true;
                startActivity(mainActivity);


            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
