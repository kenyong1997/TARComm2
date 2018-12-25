package com.turkfyp.tarcomm2.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.concurrent.ExecutionException;

public class ViewOtherLostFoundPostActivity extends AppCompatActivity {
    public static boolean allowRefresh;
    protected String email;
    private static final String TAG = "ViewOtherLostFoundPostActivity";

    private static String GET_URL = "https://tarcomm.000webhostapp.com/getLostFoundUserPost.php";
    SwipeRefreshLayout swipeRefreshLostFound;
    List<LostFound> lostFoundList;

    ExpandableListView elvLostFoundUpload;
    LostFoundUploadAdapter lostFoundUploadAdapter;
    List<String> listDataHeader;
    HashMap<String, List<LostFound>> listDataChild;

    RequestQueue queue;
    Bitmap bitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Bundle extras = getIntent().getExtras();
        email = extras.getString("email");
        elvLostFoundUpload = (ExpandableListView) findViewById(R.id.elvLostItemUpload);
        swipeRefreshLostFound = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLostFound);


        try {
            //initialize lostFoundList
            lostFoundList = new ArrayList<>();

            downloadLostFoundRecords(getApplicationContext(), GET_URL);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        elvLostFoundUpload.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                LostFound selectedItem;
                selectedItem = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);

                Intent lostFoundDetail = new Intent(getApplicationContext(),LostFoundDetailActivity.class);
                lostFoundDetail.putExtra("lostCategory",selectedItem.getCategory());
                lostFoundDetail.putExtra("lostItemName",selectedItem.getLostItemName());
                lostFoundDetail.putExtra("email",selectedItem.getEmail());
                lostFoundDetail.putExtra("lostItemDesc",selectedItem.getLostItemDesc());
                lostFoundDetail.putExtra("lostDate", selectedItem.getLostDate());
                lostFoundDetail.putExtra("lostItemContactName",selectedItem.getContactName());
                lostFoundDetail.putExtra("lostItemContactNo",selectedItem.getContactNo());
                lostFoundDetail.putExtra("checkYourUpload",true);

                convertImage(selectedItem.getLostItemURL());
                lostFoundDetail.putExtra("LostImage", bitmap);
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

    @Override
    public void onResume() {
        super.onResume();
        if (allowRefresh) {
            allowRefresh = false;
            if (lostFoundList == null) {
                lostFoundList = new ArrayList<>();
                downloadLostFoundRecords(getApplicationContext(), GET_URL);
            } else {
                lostFoundUploadAdapter = new LostFoundUploadAdapter(getApplicationContext(),listDataHeader, listDataChild);
                elvLostFoundUpload.setAdapter(lostFoundUploadAdapter);
            }
        }
    }



    //Convert Glide Image to bitmap
    private void convertImage(String imageURL){
        class ConvertImage extends AsyncTask<String, Void, Bitmap> {

            @Override
            protected Bitmap doInBackground(String... strings) {
                String imageURL = strings[0];

                try {
                    bitmap = Glide.with(getApplicationContext())
                            .asBitmap()
                            .load(imageURL)
                            .submit(200,200)
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
