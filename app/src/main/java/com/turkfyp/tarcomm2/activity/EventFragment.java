package com.turkfyp.tarcomm2.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import com.turkfyp.tarcomm2.DatabaseObjects.EventAdapter;
public class EventFragment extends android.support.v4.app.Fragment{

    public static boolean allowRefresh;

    private static final String TAG = "EventFragment";

    private static String GET_URL = "https://taroute.000webhostapp.com/getEvent.php";
    ListView lvEvents;
    SwipeRefreshLayout swipeRefreshEvents;
    List<Event> eventList;
    protected String currentDate;

    RequestQueue queue;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_event_tab1, container, false);

        //hide action bar
        //((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        //get current Date
        Date cal = Calendar.getInstance().getTime();


        java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd");
        currentDate = df.format(cal);


        //link with Floating button




        lvEvents = (ListView) v.findViewById(R.id.lvEvents);
       swipeRefreshEvents = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshEvents);

        try {

            //initialize textBookList
            eventList = new ArrayList<>();

            downloadEventRecords(getActivity().getApplicationContext(), GET_URL);

            //Log.d("HIIIIIIIIIIII","lullllllllllllllllllllllllllllllllllllllllllll");
            //Log.d("context", String.valueOf(getActivity().getApplicationContext()));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }


        //When user swipe to refresh
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


        //when a particular event was selected to view more details
        lvEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event selectedEvent = (Event) parent.getItemAtPosition(position);
                Intent eventDetailIntent = new Intent(getActivity(), EventDetailsActivity.class);
                eventDetailIntent.putExtra("eventName", selectedEvent.getEventName());
                eventDetailIntent.putExtra("eventDateTime", selectedEvent.getEventDateTime());
                eventDetailIntent.putExtra("eventDesc", selectedEvent.getEventDesc());
                eventDetailIntent.putExtra("eventVenue", selectedEvent.getEventVenue());


                ImageView imageEvent = (ImageView) view.findViewById(R.id.ivImageEvent);
                imageEvent.buildDrawingCache();
                Bitmap image = imageEvent.getDrawingCache();
                eventDetailIntent.putExtra("Image", image);
                eventDetailIntent.putExtra("ImageURL", selectedEvent.getEventImageURL());

                startActivity(eventDetailIntent);
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
                                        String eventCreator = eventResponse.getString("eventCreator");
                                        String eventDesc = eventResponse.getString("eventDesc");
                                        String eventImageURL = eventResponse.getString("url");
                                        String eventVenue = eventResponse.getString("eventVenue");

                                        Event event = new Event(eventName, eventCreator, eventDateTime, eventDesc, eventImageURL, eventVenue);
                                        eventList.add(event);
                                    }
                                    loadEvents();

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

    private void loadEvents() {
        final EventAdapter adapter = new EventAdapter(getActivity(), R.layout.fragment_event_tab1, eventList);
        lvEvents.setAdapter(adapter);

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
                loadEvents();
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

