package com.turkfyp.tarcomm2.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.turkfyp.tarcomm2.DatabaseObjects.Item;
import com.turkfyp.tarcomm2.DatabaseObjects.ItemUploadAdapter;
import com.turkfyp.tarcomm2.DatabaseObjects.LostFound;
import com.turkfyp.tarcomm2.DatabaseObjects.LostFoundUploadAdapter;
import com.turkfyp.tarcomm2.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewOtherLostFoundPostActivity extends AppCompatActivity {

    protected String email;
    protected ExpandableListView elvLostFoundUpload;
    protected SwipeRefreshLayout swipeRefreshLostFound;
    private static String GET_URL = "https://tarcomm.000webhostapp.com/getLostFoundUserPost.php";

    List<LostFound> lostFoundList;
    List<String> listDataHeader;
    HashMap<String, List<LostFound>> listDataChild;
    LostFoundUploadAdapter lostFoundUploadAdapter;
    RequestQueue queue;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_other_lost_found_post);

        Bundle extras = getIntent().getExtras();
        email = extras.getString("email");


        elvLostFoundUpload = (ExpandableListView) findViewById(R.id.elvOtherLostItemUpload);
        swipeRefreshLostFound = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLostFound);

        try {
            //initialize textBookList
            lostFoundList = new ArrayList<>();

            downloadLostFoundRecords(getApplicationContext(), GET_URL);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        elvLostFoundUpload.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                LostFound selectedItem;
                selectedItem = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);

                Intent lostFoundDetail = new Intent(ViewOtherLostFoundPostActivity.this,LostFoundDetailActivity.class);
                lostFoundDetail.putExtra("lostCategory",selectedItem.getCategory());
                lostFoundDetail.putExtra("lostItemName",selectedItem.getLostItemName());
                lostFoundDetail.putExtra("email",selectedItem.getEmail());
                lostFoundDetail.putExtra("lostItemDesc",selectedItem.getLostItemDesc());
                lostFoundDetail.putExtra("lostDate", selectedItem.getLostDate());
                lostFoundDetail.putExtra("lostItemContactName",selectedItem.getContactName());
                lostFoundDetail.putExtra("lostItemContactNo",selectedItem.getContactNo());
                lostFoundDetail.putExtra("checkYourUpload",false);

                ImageView ivImage = (ImageView) view.findViewById(R.id.imageViewLostItemImage);
                ivImage.buildDrawingCache();
                Bitmap image = ivImage.getDrawingCache();
                lostFoundDetail.putExtra("LostImage", image);
                lostFoundDetail.putExtra("LostImageURL", selectedItem.getLostItemURL());

                startActivity(lostFoundDetail);
                return false;
            }
        });

        swipeRefreshLostFound.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                swipeRefreshLostFound.setRefreshing(true);
                try {
                    downloadLostFoundRecords(getApplicationContext(), GET_URL);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                swipeRefreshLostFound.setRefreshing(false);
            }
        });
    }
    public void onBackClicked(View view){
        finish();
    }
    //retrieve the records from database

    //retrieve the records from database
    public void downloadLostFoundRecords(Context context, String url) {
        // Instantiate the RequestQueue
        queue = Volley.newRequestQueue(context);

        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<LostFound>>();

        listDataHeader.add("Lost Item");
        listDataHeader.add("Found Item");

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
                                    lostFoundList.clear();

                                    List<LostFound> lostList = new ArrayList<>();
                                    List<LostFound> foundList = new ArrayList<>();

                                    for (int i = 0; i < j.length(); i++) {
                                        JSONObject lostFoundResponse = (JSONObject) j.get(i);
                                        String category = lostFoundResponse.getString("category");
                                        String lostItemName = lostFoundResponse.getString("lostItemName");
                                        String lostItemDesc = lostFoundResponse.getString("lostItemDesc");
                                        String lostItemURL = lostFoundResponse.getString("url");
                                        String email = lostFoundResponse.getString("email");
                                        String contactName = lostFoundResponse.getString("fullname");
                                        String contactNo = lostFoundResponse.getString("contactno");
                                        String lostDate = lostFoundResponse.getString("lostDate");

                                        LostFound lostFound = new LostFound(category, lostItemName, lostItemDesc, lostItemURL, lostDate, email, contactName, contactNo);

                                        if(category.toUpperCase().equals("LOST"))
                                            lostList.add(lostFound);
                                        else
                                            foundList.add(lostFound);
                                    }

                                    listDataChild.put(listDataHeader.get(0), lostList);
                                    listDataChild.put(listDataHeader.get(1), foundList);

                                    lostFoundUploadAdapter = new LostFoundUploadAdapter(getApplicationContext(),listDataHeader, listDataChild);
                                    elvLostFoundUpload.setAdapter(lostFoundUploadAdapter);
                                    elvLostFoundUpload.expandGroup(0);
                                    elvLostFoundUpload.expandGroup(1);

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

    }








