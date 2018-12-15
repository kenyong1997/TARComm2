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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.turkfyp.tarcomm2.DatabaseObjects.Friend;
import com.turkfyp.tarcomm2.DatabaseObjects.FriendRVAdapter;
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

public class FragmentFriendTab1 extends Fragment {
    public static boolean allowRefresh;

    private static final String TAG = "FragmentFriendTab1";

    private static String GET_URL = "https://tarcomm.000webhostapp.com/getFriendList.php";

    SwipeRefreshLayout swipeRefreshFriends;
    List<Friend> friendList;
    protected String currentDate;

    RequestQueue queue;
    RecyclerView rvFriend;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_friend_tab1, container, false);

        //get current Date
        Date cal = Calendar.getInstance().getTime();
        java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd");
        currentDate = df.format(cal);

        swipeRefreshFriends = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshFriends);
        rvFriend = (RecyclerView) v.findViewById(R.id.rvFriend);

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

        return v;
    }

    public void downloadFriendRecords(Context context, String url){
        SharedPreferences preferences = getActivity().getSharedPreferences("tarcommUser", Context.MODE_PRIVATE);
        final String userEmail = preferences.getString("email", "");

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
                    params.put("email", userEmail);
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
        FriendRVAdapter myAdapter = new FriendRVAdapter(getActivity(),friendList) ;
        rvFriend.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvFriend.setAdapter(myAdapter);
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
}
