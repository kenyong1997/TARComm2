package com.turkfyp.tarcomm2.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.turkfyp.tarcomm2.DatabaseObjects.Event;
import com.turkfyp.tarcomm2.R;
import com.turkfyp.tarcomm2.guillotine.animation.GuillotineAnimation;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final long RIPPLE_DURATION = 250;
    private ViewPager mViewpager;
    private ViewPagerAdapter mAdapter;
    private ArrayList<ViewPagerModel> mContents;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.root)
    FrameLayout root;
    @BindView(R.id.content_hamburger)
    View contentHamburger;

    private static String GET_URL = "https://tarcomm.000webhostapp.com/getHighlightedEvent.php";
    String currentDate;
    List<Event> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
        ButterKnife.bind(this);

        //Navigation Menu - START
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(null);
        }

        View guillotineMenu = LayoutInflater.from(this).inflate(R.layout.guillotine, null);
        root.addView(guillotineMenu);

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
        //Navigation Menu - END

        //Get Current date
        Date cal = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = dateFormat.format(cal);

        try{
            //Initialize Event List
            eventList = new ArrayList<>();

            downloadHighlightEvent(getApplicationContext(), GET_URL);

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private class ViewPagerStack implements ViewPager.PageTransformer{

        @Override
        public void transformPage(View page, float position) {
            if(position >= 0){
                page.setScaleX(0.7f - 0.05f * position);
                page.setScaleY(0.7f);
                page.setTranslationX(-page.getWidth() * position);

                page.setTranslationY(-30*position);
            }
        }
    }

    //retrieve the records from database
    public void downloadHighlightEvent(Context context, String url) {
        RequestQueue queue = Volley.newRequestQueue(context);

        //Send data
        try {
            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONArray j = new JSONArray(response);
                                try {
                                    for (int i = 0; i < j.length(); i++) {
                                        JSONObject eventResponse = (JSONObject) j.get(i);
                                        String eventName = eventResponse.getString("eventName");
                                        String eventDateTime = eventResponse.getString("eventDateTime");
                                        String eventDesc = eventResponse.getString("eventDesc");
                                        String eventImageURL = eventResponse.getString("url");
                                        String eventVenue = eventResponse.getString("eventVenue");
                                        String eventVenueName = eventResponse.getString("eventVenueName");
                                        String eventHighlight = eventResponse.getString("eventHighlight");
                                        String eventEndDateTime = eventResponse.getString("eventEndDateTime");

                                        Event event = new Event(eventName, eventDateTime, eventDesc, eventImageURL, eventVenue, eventVenueName, eventHighlight,eventEndDateTime);
                                        eventList.add(event);
                                    }

                                    mViewpager = (ViewPager) findViewById(R.id.viewpager);
                                    mContents = new ArrayList<>();
//                                    int images[] =  {R.drawable.one,R.drawable.two,R.drawable.three,R.drawable.four,R.drawable.five,R.drawable.six};
//                                    String names[] = {"One","Two","Three","Four","Five","Six"};
//                                    String desc[] = {"Event","Event","Event","Event","Event","Event"};
//                                    String location[] = {"Main Campus","Main Campus","Main Campus","Main Campus","Main Campus","Main Campus"};

                                    for(int i =0;i<eventList.size();i++)
                                    {
                                        ViewPagerModel viewPagerModel = new ViewPagerModel();

                                        viewPagerModel.image = eventList.get(i).getEventImageURL();
                                        viewPagerModel.name = eventList.get(i).getEventName();
                                        viewPagerModel.desc = eventList.get(i).getEventDesc();
                                        viewPagerModel.location = eventList.get(i).getEventVenueName();

                                        mContents.add(viewPagerModel);
                                    }
                                    mAdapter = new ViewPagerAdapter(mContents,getApplicationContext());
                                    mViewpager.setPageTransformer(true, new ViewPagerStack());
                                    mViewpager.setOffscreenPageLimit(6);
                                    mViewpager.setAdapter(mAdapter);
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
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Error. " + error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("date", currentDate);
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
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    //Navigation Settings

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
            }
        }
        ConvertImage convertImage = new ConvertImage();
        convertImage.execute(imageURL);
    }
}
