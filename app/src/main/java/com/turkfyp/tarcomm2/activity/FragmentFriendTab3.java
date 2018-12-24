package com.turkfyp.tarcomm2.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class FragmentFriendTab3 extends Fragment {
    public static boolean allowRefresh;

    private static final String TAG = "FragmentFriendTab3";

    private static String GET_URL = "https://tarcomm.000webhostapp.com/getAllUser.php";
    private static String GET_SEARCH_URL = "https://tarcomm.000webhostapp.com/getSearchUser.php";

    SwipeRefreshLayout swipeRefreshFriends;
    List<Friend> friendList;
    protected String currentDate;

    RequestQueue queue;
    RecyclerView rvFriendSearch;

    EditText etSearchFriendName;
    ImageView buttonSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_friend_tab3, container, false);

        //get current Date
        Date cal = Calendar.getInstance().getTime();
        java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd");
        currentDate = df.format(cal);

        swipeRefreshFriends = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshFriends);
        rvFriendSearch = (RecyclerView) v.findViewById(R.id.rvFriendSearch);

        buttonSearch = (ImageView) v.findViewById(R.id.buttonSearch);
        etSearchFriendName = (EditText) v.findViewById(R.id.etSearchFriendName);

        try {
            friendList = new ArrayList<>();

            downloadFriendRecords(getActivity().getApplicationContext(), GET_URL);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        swipeRefreshFriends.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                swipeRefreshFriends.setRefreshing(true);
                try {
                    downloadFriendRecords(getActivity().getApplicationContext(), GET_URL);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                swipeRefreshFriends.setRefreshing(false);
            }
        });

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search = etSearchFriendName.getText().toString();

                findUser(getActivity().getApplicationContext(), GET_SEARCH_URL, search);
            }
        });

        return v;
    }

    public void downloadFriendRecords(Context context, String url){
        SharedPreferences preferences = getActivity().getSharedPreferences("tarcommUser", Context.MODE_PRIVATE);
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
                                        JSONObject eventResponse = (JSONObject) j.get(i);
                                        String userEmail = eventResponse.getString("userEmail");
                                        String friendEmail = eventResponse.getString("friendEmail");
                                        String friendType = eventResponse.getString("type");
                                        String friendName = eventResponse.getString("friendName");
                                        String profilePicURL = eventResponse.getString("profilePicURL");

                                        Friend friend = new Friend(userEmail, friendEmail, friendType, friendName, profilePicURL);
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
                            Toast.makeText(getContext(), "Error. " + error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
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

    private void setRVAdapter(List<Friend> friendList){
        FriendSearchRVAdapter myAdapter = new FriendSearchRVAdapter(getActivity(),friendList) ;
        rvFriendSearch.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvFriendSearch.setAdapter(myAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (allowRefresh) {
            allowRefresh = false;
            if (friendList == null) {
                friendList = new ArrayList<>();
                downloadFriendRecords(getActivity().getApplicationContext(), GET_URL);
            } else {
                setRVAdapter(friendList);
            }
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (queue != null) {
            queue.cancelAll(TAG);
        }
    }

    public void findUser(final Context context, String url, final String searchName) {
        SharedPreferences preferences = getActivity().getSharedPreferences("tarcommUser", Context.MODE_PRIVATE);
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

                                        Friend friend = new Friend(userEmail, friendEmail, friendType, friendName, profilePicURL);
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
                    params.put("email", email);
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
