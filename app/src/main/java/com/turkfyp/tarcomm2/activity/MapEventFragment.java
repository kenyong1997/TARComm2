package com.turkfyp.tarcomm2.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import com.turkfyp.tarcomm2.DatabaseObjects.Event;
import com.turkfyp.tarcomm2.DatabaseObjects.MapEventRVAdapter;
import com.turkfyp.tarcomm2.MapObjects.GPSTracker;
import com.turkfyp.tarcomm2.R;


import static android.content.Context.LOCATION_SERVICE;


public class MapEventFragment extends Fragment implements OnMapReadyCallback {

    private String GOOGLE_MAP_API = "AIzaSyDRSuYdn9yoCACUGxd4qGkgl59276mWvcs";

    //DECLARE THE VARIABLES
    protected GoogleMap mMap;

    private TextView tvNotInServiceArea;
    private ProgressDialog pDialog;
    protected RequestQueue queue;
    protected List<Marker> eventMarkersList = new ArrayList<>();

    //LOCATION LISTENER
    private GPSTracker gpsTracker;
    private Location mLastKnownLocation;
    private double lastKnownLatitude, lastKnownLongitude;


    //EVENT DOWNLOADING
    private static String GET_CURRENT_EVENT_URL = "https://tarcomm.000webhostapp.com/getCurrentEvent.php";
    private static String GET_UPCOMING_EVENT_URL = "https://tarcomm.000webhostapp.com/getUpcomingEvent.php";
    protected List<Event> eventList;
    protected String currentDate;
    private List<MarkerOptions> eventMarkers = new ArrayList<>();

    public static final String STATUS_ON = "ON";

    String email;

    RecyclerView rvMapEvent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_map_event, container, false);

        rvMapEvent = (RecyclerView) v.findViewById(R.id.rvMapEvent);
        //LINK CODES WITH UI

        pDialog = new ProgressDialog(v.getContext());


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //get current Date
        Date cal = Calendar.getInstance().getTime();
        java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd");
        currentDate = df.format(cal);

        //Get Email from SharedPreferences
        SharedPreferences preferences = getActivity().getSharedPreferences("tarcommUser", Context.MODE_PRIVATE);
        email = preferences.getString("email","");

        return v;
    }
    private void setRVAdapter(List<Event> eventList){
        MapEventRVAdapter myAdapter = new MapEventRVAdapter(getActivity(),eventList,mMap) ;
        rvMapEvent.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvMapEvent.setAdapter(myAdapter);
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the userFullName will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the userFullName has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        // Add a marker in TARC and move the camera

        LatLng tarc = new LatLng(3.215049, 101.726534);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tarc, 19));

        downloadCurrentEventDetails();
        //downloadUpcomingEventDetails();

        //check if someone select a event venue in event details
        /*
        if (!EventDetails.EVENT_VENUE_LOCATION.matches("")) {
            //LatLng eventLocation = getLocationFromAddress(getContext(), EventDetails.EVENT_VENUE_LOCATION);

            Geocoding geocoding = new Geocoding();
            geocoding.execute(EventDetails.EVENT_VENUE_LOCATION, "directToEvent");

        }
*/


        //Allow userFullName to set location

        //PERMISSION CHECK
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);

            return;
        } else {
            mMap.setMyLocationEnabled(true);

        }


        // when the Locate button on map is clicked
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {

                // declare location manager and boolean to check location provider availability
                LocationManager locationManager = (LocationManager)
                        getContext().getSystemService(LOCATION_SERVICE);

                boolean isGPSEnabled = locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);
                boolean isNetworkEnabled = locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER);


                //if location provider is off
                if (isGPSEnabled == false && isNetworkEnabled == false) {

                    promptForLocationService();
                }

                //create a new Gps tracker object to get the last known location of userFullName.
                gpsTracker = new GPSTracker(getView().getContext().getApplicationContext());
                mLastKnownLocation = gpsTracker.getLastKnownLocation();


                // IF got the last known location successfully
                if (mLastKnownLocation != null) {
                    lastKnownLatitude = mLastKnownLocation.getLatitude();
                    lastKnownLongitude = mLastKnownLocation.getLongitude();


                    //Boundaries of TARC, if not in service area will show the error message
                    if ((lastKnownLatitude > 3.2190 || lastKnownLatitude < 3.2120) || (lastKnownLongitude < 101.7230 || lastKnownLongitude > 101.73510)) {


                        final View view = (LayoutInflater.from(getView().getContext()).inflate(R.layout.service_area_dialog, null));

                        tvNotInServiceArea = (TextView) view.findViewById(R.id.tvNotInServiceArea);
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getView().getContext());
                        alertBuilder.setView(view);
                        alertBuilder.setTitle("Out of service area");
                        tvNotInServiceArea.setText("Your location is not in service area");

                        alertBuilder.setCancelable(true)
                                .
                                        setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {


                                            }
                                        });

                        AlertDialog dialog = alertBuilder.create();
                        dialog.show();


                    }

                    // if the current location is in service area and gps is turned on, update the current location and status to database
                    else {

                        updateStatus(getContext(), "https://tarcomm.000webhostapp.com/updateStatus.php", STATUS_ON);
                        updateLatitude(getContext(), "https://tarcomm.000webhostapp.com/updateLatitude.php", lastKnownLatitude);
                        updateLongitude(getContext(), "https://tarcomm.000webhostapp.com/updateLongitude.php", lastKnownLongitude);

                        //move the camera to current location
                        LatLng lastKnownLocation = new LatLng(lastKnownLatitude, lastKnownLongitude);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLocation, 18));
                    }
                }

                return false;
            }
        });


        //when info windows are clicked, direct to whatsapp
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                Intent i = new Intent(getActivity(),EventActivity.class);
                getActivity().finish();
                getActivity().startActivity(i);
            }
        });
    }


    public void promptForLocationService() {
        //build an alert dialog
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
        alertBuilder.setTitle("Location Service is off");
        alertBuilder.setMessage("Please turn on your location service to proceed");

        alertBuilder.setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //let userFullName to turn on location provider
                        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(settingsIntent);

                    }
                });

        Dialog dialog = alertBuilder.create();
        dialog.show();
    }

    public void downloadCurrentEventDetails() {
        //get the event list and mark it
        try {
            eventList = new ArrayList<>();
            downloadEventRecords(getActivity().getApplicationContext(), GET_CURRENT_EVENT_URL);


        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void downloadUpcomingEventDetails() {
        //get the event list and mark it
        try {
            eventList = new ArrayList<>();
            downloadEventRecords(getActivity().getApplicationContext(), GET_UPCOMING_EVENT_URL);


        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void removeOtherMarkers() {
        if (eventMarkersList != null) {
            for (Marker marker : eventMarkersList) {
                marker.remove();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResult) {
        if (requestCode == 0) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);

            }
        }

    }


    //download all the events
    public void downloadEventRecords(Context context, String url) {
        RequestQueue queue = Volley.newRequestQueue(context);

        if (!pDialog.isShowing()) {
            pDialog.setMessage("Adding Events...");
            pDialog.show();
        }

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
                                    addEventMarkers(eventList);
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

    //add the event markers
    public void addEventMarkers(List<Event> events) {
        pDialog.dismiss();
        eventMarkers = new ArrayList<>();

        for (Event event : events) {
            Geocoding geocoding = new Geocoding();
            geocoding.execute(event.getEventVenue(), "eventGeocoding", event.getEventName(), event.getEventDateTime());
        }

    }

    //get Location from Address(GEOCODING API)
    private class Geocoding extends AsyncTask<String, Void, String[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          /*  dialog.setMessage("Please wait...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();*/
        }

        String encodedAddress;
        String address;
        String eventName;
        String eventDate;

        String itemName;
        String itemDesc;
        String taskType;

        @Override
        protected String[] doInBackground(String... params) {

            address = params[0];
            taskType = params[1];

            if (taskType == "itemGeocoding") {

                itemName = params[2];
                itemDesc = params[3];

            } else if (taskType == "eventGeocoding") {
                eventName = params[2];
                eventDate = params[3];

            }


            //connection to get result from Geolocation API
            try {
                encodedAddress = URLEncoder.encode(address, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String response;
            try {
                response = getLatLongByURL("https://maps.google.com/maps/api/geocode/json?address=" + encodedAddress + "&key="+ GOOGLE_MAP_API);
                Log.d("response", "" + response);
                return new String[]{response};
            } catch (Exception e) {
                return new String[]{"error"};
            }
        }

        @Override
        protected void onPostExecute(String... result) {
            try {
                JSONObject jsonObject = new JSONObject(result[0]);

                double lng = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lng");

                double lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lat");

                //Toast.makeText(getContext(), "coordinates:" + lat + "," + lng, Toast.LENGTH_SHORT).show();

                LatLng coordinate = new LatLng(lat, lng);

                if (taskType == "eventGeocoding") {

                    if (coordinate != null) {
                        eventMarkers.add(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_hot_event))
                                .title(eventName)
                                .position(coordinate)
                                .snippet("Event Date: " + eventDate));
                    }

                    for (int i = 0; i < eventMarkers.size(); i++) {

                        for (int j = 0; j < eventMarkers.size(); j++) {
                            // if there is duplicated location for different events
                            if (eventMarkers.get(i) != eventMarkers.get(j) && eventMarkers.get(i).getPosition().latitude == eventMarkers.get(j).getPosition().latitude && eventMarkers.get(i).getPosition().longitude == eventMarkers.get(j).getPosition().longitude)

                            {
                                // make the location slightly different in order to have 2 or more markers on a same spot
                                LatLng duplicatedPlace = eventMarkers.get(j).getPosition();
                                double newLat = duplicatedPlace.latitude + (Math.random() / 25000);
                                double newLng = duplicatedPlace.longitude + (Math.random() / 25000);

                                LatLng finalLatLng = new LatLng(newLat, newLng);

                                eventMarkers.get(j).position(finalLatLng);
                            }

                            eventMarkersList.add(mMap.addMarker(eventMarkers.get(j)));
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public String getLatLongByURL(String requestURL) {
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    //TRY UPDATE RESULT
    public void updateStatus(Context context, String url, final String status) {
        RequestQueue queue = Volley.newRequestQueue(context);

        //Send data
        try {

            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    url,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            //response =string returned by server to the client
                            JSONObject jsonObject;
                            try {
                                jsonObject = new JSONObject(response);
                                int success = jsonObject.getInt("success");
                                String message = jsonObject.getString("message");
                                if (success == 0) {

                                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

                                } else {
                                    //SUCCESS
                                    // Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

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
                    params.put("status", status);
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

    public void updateLatitude(Context context, String url, final double latitude) {
        RequestQueue queue = Volley.newRequestQueue(context);

        //Send data
        try {

            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    url,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            //response =string returned by server to the client
                            JSONObject jsonObject;
                            try {
                                jsonObject = new JSONObject(response);
                                int success = jsonObject.getInt("success");
                                String message = jsonObject.getString("message");
                                if (success == 0) {

                                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

                                } else {
                                    //SUCCESS
                                    //Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();


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
                    params.put("latitude", Double.toString(latitude));
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

    public void updateLongitude(Context context, String url, final double longitude) {
        RequestQueue queue = Volley.newRequestQueue(context);

        //Send data
        try {

            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    url,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            //response =string returned by server to the client
                            JSONObject jsonObject;
                            try {
                                jsonObject = new JSONObject(response);
                                int success = jsonObject.getInt("success");
                                String message = jsonObject.getString("message");
                                if (success == 0) {

                                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

                                } else {
                                    //SUCCESS
                                    // Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();


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
                    params.put("longitude", Double.toString(longitude));
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
    public void onDestroy() {
        super.onDestroy();

        //make user status become inactive
        updateStatus(getContext(), "https://tarcomm.000webhostapp.com/updateStatus.php","OFF");
    }
}



