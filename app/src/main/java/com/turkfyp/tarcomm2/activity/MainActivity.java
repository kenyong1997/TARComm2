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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.turkfyp.tarcomm2.DatabaseObjects.Event;
import com.turkfyp.tarcomm2.DatabaseObjects.Item;
import com.turkfyp.tarcomm2.DatabaseObjects.ItemRVAdapter;
import com.turkfyp.tarcomm2.DatabaseObjects.LostFound;
import com.turkfyp.tarcomm2.DatabaseObjects.MainItemRVAdapter;
import com.turkfyp.tarcomm2.DatabaseObjects.MainLostItemRVAdapter;
import com.turkfyp.tarcomm2.DatabaseObjects.ViewPagerAdapter;
import com.turkfyp.tarcomm2.DatabaseObjects.ViewPagerModel;
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
    private static final String TAG = "Main Activity";
    private static final long RIPPLE_DURATION = 250;
    private ViewPager mViewpager;
    private ViewPagerAdapter mAdapter;
    private ArrayList<ViewPagerModel> mContents;
    private RecyclerView rvMainMarket, rvMainLost;
    public static boolean allowRefresh;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.root)
    FrameLayout root;
    @BindView(R.id.content_hamburger)
    View contentHamburger;

    private static String GET_URL = "https://tarcomm.000webhostapp.com/getHighlightedEvent.php";
    private static String GET_URL_ITEM = "https://tarcomm.000webhostapp.com/getRandWTS.php";
    private static String GET_URL_LOST_ITEM = "https://tarcomm.000webhostapp.com/getRandLostFound.php";
    String currentDate;
    List<Event> eventList;
    RequestQueue queue;
    List<Item> itemList;
    List<LostFound> lostFoundList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
        ButterKnife.bind(this);

        rvMainMarket = (RecyclerView) findViewById(R.id.rvMainMarket);
        rvMainLost = (RecyclerView) findViewById(R.id.rvMainLost);

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

        CircleImageView profile_image = (CircleImageView) findViewById(R.id.profile_image);

        RequestOptions options = new RequestOptions()
                .circleCrop()
                .placeholder(R.drawable.background_white)
                .error(R.drawable.background_white);

        Glide.with(getApplicationContext()).load(imageURL).apply(options).into(profile_image);

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
            //Initialize List
            eventList = new ArrayList<>();
            lostFoundList = new ArrayList<>();
            itemList = new ArrayList<>();

            downloadHighlightEvent(getApplicationContext(), GET_URL);
            downloadTradingRecords(getApplicationContext(), GET_URL_ITEM);
            downloadLostFoundRecords(getApplicationContext(),GET_URL_LOST_ITEM);

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
    //retrieve the records from database
    public void downloadLostFoundRecords(Context context, String url) {
        // Instantiate the RequestQueue
        queue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            lostFoundList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject lostFoundResponse = (JSONObject) response.get(i);
                                String category = lostFoundResponse.getString("category");
                                String lostItemName = lostFoundResponse.getString("lostItemName");
                                String lostItemDesc = lostFoundResponse.getString("lostItemDesc");
                                String lostItemURL = lostFoundResponse.getString("url");
                                String email = lostFoundResponse.getString("email");
                                String contactName = lostFoundResponse.getString("fullname");
                                String contactNo = lostFoundResponse.getString("contactno");
                                String lostDate = lostFoundResponse.getString("lostDate");

                                LostFound lostFound = new LostFound(category, lostItemName, lostItemDesc, lostItemURL, lostDate, email, contactName, contactNo);
                                lostFoundList.add(lostFound);
                            }
                            //load the item into adapter
                            setLostRVAdapter(lostFoundList);

                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getApplicationContext(), "Error: " + volleyError.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });

        // Set the tag on the request.
        jsonObjectRequest.setTag(TAG);

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);


    }
    //retrieve the records from database
    public void downloadTradingRecords(Context context, String url) {
        // Instantiate the RequestQueue
        queue = Volley.newRequestQueue(context);

        StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONArray jsonArray = new JSONArray(response);
                    try{
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject itemResponse = (JSONObject) jsonArray.get(i);

                            String itemCategory = itemResponse.getString("itemCategory");
                            String itemName = itemResponse.getString("itemName");
                            String itemDescription = itemResponse.getString("itemDesc");
                            String imageURL = itemResponse.getString("url");
                            String itemPrice = itemResponse.getString("itemPrice");
                            String email = itemResponse.getString("email");
                            String sellerName = itemResponse.getString("fullname");
                            String sellerContact = itemResponse.getString("contactno");

                            Item item = new Item(itemCategory, itemName, itemDescription, imageURL, itemPrice, email, sellerName, sellerContact);
                            itemList.add(item);
                        }
                        //Load item into RecyclerView Adapter
                        setRVAdapter(itemList);

                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Error1: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error2: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        // Set the tag on the request.
        postRequest.setTag(TAG);

        // Add the request to the RequestQueue.
        queue.add(postRequest);
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

    private void setRVAdapter(List<Item> itemList){
        MainItemRVAdapter myAdapter = new MainItemRVAdapter(this,itemList) ;

        //For horizontal layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        rvMainMarket.setLayoutManager(layoutManager);
        rvMainMarket.setAdapter(myAdapter);
    }
    private void setLostRVAdapter(List<LostFound> lostFoundList){
        MainLostItemRVAdapter myAdapter = new MainLostItemRVAdapter(this,lostFoundList) ;

        //For horizontal layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        rvMainLost.setLayoutManager(layoutManager);
        rvMainLost.setAdapter(myAdapter);
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
}
