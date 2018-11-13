package com.turkfyp.tarcomm2.activity;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
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
import com.turkfyp.tarcomm2.DatabaseObjects.EventRVAdapter;
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

import com.turkfyp.tarcomm2.DatabaseObjects.Event;

public class FragmentEventTab1 extends android.support.v4.app.Fragment{

    public static boolean allowRefresh;

    private static final String TAG = "FragmentEventTab1";

    private static String GET_URL = "https://tarcomm.000webhostapp.com/getCurrentEvent.php";
    SwipeRefreshLayout swipeRefreshEvents;
    List<Event> eventList;
    protected String currentDate;

    RequestQueue queue;
    RecyclerView rvEvent;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_event_tab1, container, false);

        //get current Date
        Date cal = Calendar.getInstance().getTime();
        java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd");
        currentDate = df.format(cal);

        swipeRefreshEvents = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshEvents);
        rvEvent = (RecyclerView) v.findViewById(R.id.rvEvent);

        try {
            //initialize eventList
            eventList = new ArrayList<>();

            downloadEventRecords(getActivity().getApplicationContext(), GET_URL);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        swipeRefreshEvents.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                swipeRefreshEvents.setRefreshing(true);
                try {
                    downloadEventRecords(getActivity().getApplicationContext(), GET_URL);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                swipeRefreshEvents.setRefreshing(false);
            }
        });

        return v;
    }

    //retrieve the records from database
    public void downloadEventRecords(Context context, String url) {
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
                                    eventList.clear();
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
                                    //Load eventList into RecyclerView Adapter
                                    setRVAdapter(eventList);

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

    private void setRVAdapter(List<Event> eventList){
        EventRVAdapter myAdapter = new EventRVAdapter(getActivity(),eventList) ;
        rvEvent.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvEvent.setAdapter(myAdapter);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (allowRefresh) {
            allowRefresh = false;
            if (eventList == null) {
                eventList = new ArrayList<>();
                downloadEventRecords(getActivity().getApplicationContext(), GET_URL);
            } else {
                setRVAdapter(eventList);
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

