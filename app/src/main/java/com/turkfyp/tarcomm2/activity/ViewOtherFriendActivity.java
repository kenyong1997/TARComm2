package com.turkfyp.tarcomm2.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.turkfyp.tarcomm2.DatabaseObjects.Friend;
import com.turkfyp.tarcomm2.DatabaseObjects.FriendSearchRVAdapter;
import com.turkfyp.tarcomm2.DatabaseObjects.OtherFriendSearchRVAdapter;
import com.turkfyp.tarcomm2.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewOtherFriendActivity extends AppCompatActivity {
    public static boolean allowRefresh;

    private static final String TAG = "ViewOtherFriendActivity";

    private static String GET_URL = "https://tarcomm.000webhostapp.com/getOtherFriend.php";
    private static String GET_SEARCH_URL = "https://tarcomm.000webhostapp.com/getSearchOtherFriend.php";

    SwipeRefreshLayout swipeRefreshFriends;
    List<Friend> friendList;
    protected String currentDate;

    RequestQueue queue;
    RecyclerView rvFriendSearch;

    EditText etSearchFriendName;
    ImageView buttonSearch;
    String friendEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_other_friend);

        Bundle extras = getIntent().getExtras();
        friendEmail = extras.getString("email");

        //get current Date
        Date cal = Calendar.getInstance().getTime();
        java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd");
        currentDate = df.format(cal);

        swipeRefreshFriends = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshFriends);
        rvFriendSearch = (RecyclerView) findViewById(R.id.rvFriendSearch);

        buttonSearch = (ImageView) findViewById(R.id.buttonSearch);
        etSearchFriendName = (EditText) findViewById(R.id.etSearchFriendName);

        try {
            friendList = new ArrayList<>();

            downloadFriendRecords(getApplicationContext(), GET_URL);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        swipeRefreshFriends.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                swipeRefreshFriends.setRefreshing(true);
                try {
                    downloadFriendRecords(getApplicationContext(), GET_URL);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                swipeRefreshFriends.setRefreshing(false);
            }
        });

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search = etSearchFriendName.getText().toString();

                findUser(getApplicationContext(), GET_SEARCH_URL, search);
            }
        });

    }

    public void downloadFriendRecords(Context context, String url){
        SharedPreferences preferences = getSharedPreferences("tarcommUser", Context.MODE_PRIVATE);
        final String email = preferences.getString("email", "");


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
                                    friendList.clear();
                                    for (int i = 0; i < j.length(); i++) {
                                        JSONObject friendResponse = (JSONObject) j.get(i);
                                        String userEmail = friendResponse.getString("userEmail");
                                        String friendEmail = friendResponse.getString("friendEmail");
                                        String friendType = friendResponse.getString("type");
                                        String friendName = friendResponse.getString("friendName");
                                        String profilePicURL = friendResponse.getString("profilePicURL");
                                        String friendLastModified = friendResponse.getString("friendLastModified");

                                        if(friendType == "null"){
                                            if(friendEmail.equals(email))
                                                friendType = "ownself";
                                            else
                                                friendType = "self";
                                        }
                                        Friend friend = new Friend(userEmail, friendEmail, friendType, friendName, profilePicURL, friendLastModified);
                                        friendList.add(friend);

                                    }
                                    //Load friendList into RecyclerView Adapter
                                    setRVAdapter(friendList);

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
                    params.put("userEmail", email);
                    params.put("friendEmail",friendEmail);
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

    private void setRVAdapter(List<Friend> friendList){
        OtherFriendSearchRVAdapter myAdapter = new OtherFriendSearchRVAdapter(ViewOtherFriendActivity.this,friendList,friendEmail) ;
        rvFriendSearch.setLayoutManager(new LinearLayoutManager(ViewOtherFriendActivity.this));
        rvFriendSearch.setAdapter(myAdapter);
    }
    public void onBackClicked(View view){
        finish();
    }

    public void findUser(final Context context, String url, final String searchName) {
        SharedPreferences preferences = getSharedPreferences("tarcommUser", Context.MODE_PRIVATE);
        final String email = preferences.getString("email", "");

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
                                    friendList.clear();
                                    for (int i = 0; i < j.length(); i++) {
                                        JSONObject searchResponse = (JSONObject) j.get(i);

                                        String userEmail = searchResponse.getString("userEmail");
                                        String friendEmail = searchResponse.getString("friendEmail");
                                        String friendType = searchResponse.getString("type");
                                        String friendName = searchResponse.getString("friendName");
                                        String profilePicURL = searchResponse.getString("profilePicURL");
                                        String friendLastModified = searchResponse.getString("friendLastModified");

                                        if(friendType == "null"){
                                            if(friendEmail.equals(email))
                                                friendType = "ownself";
                                            else
                                                friendType = "self";
                                        }
                                        Friend friend = new Friend(userEmail, friendEmail, friendType, friendName, profilePicURL, friendLastModified);
                                        friendList.add(friend);

                                    }
                                    //Load friendList into RecyclerView Adapter
                                    setRVAdapter(friendList);

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
                            Toast.makeText(context, "Error. " + error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("userEmail", email);
                    params.put("friendEmail",friendEmail);
                    params.put("search", searchName);

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
