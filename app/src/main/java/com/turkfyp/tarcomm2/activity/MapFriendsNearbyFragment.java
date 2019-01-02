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
import com.turkfyp.tarcomm2.DatabaseObjects.Friend;
import com.turkfyp.tarcomm2.DatabaseObjects.MapEventRVAdapter;
import com.turkfyp.tarcomm2.DatabaseObjects.MapFriendRVAdapter;
import com.turkfyp.tarcomm2.MapObjects.GPSTracker;
import com.turkfyp.tarcomm2.R;


import static android.content.Context.LOCATION_SERVICE;


public class MapFriendsNearbyFragment extends Fragment implements OnMapReadyCallback {

    private String GOOGLE_MAP_API = "AIzaSyDRSuYdn9yoCACUGxd4qGkgl59276mWvcs";

    //DECLARE THE VARIABLES
    protected GoogleMap mMap;

    private TextView tvNotInServiceArea;
    private ProgressDialog pDialog;
    protected RequestQueue queue;
    protected List<Marker> eventMarkersList = new ArrayList<>();

    protected String currentDate;

    //LOCATION LISTENER
    private GPSTracker gpsTracker;
    private Location mLastKnownLocation;
    private double lastKnownLatitude, lastKnownLongitude;

    //FRIEND DOWNLOADING
    private static String GET_FRIEND_URL = "https://tarcomm.000webhostapp.com/getFriendList.php";
    protected List<Friend> friendList;
    protected List<Marker> friendMarkersList = new ArrayList<>();
    private List<MarkerOptions> friendMarkers = new ArrayList<>();

    String email;

    RecyclerView rvMapFriend;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_map_friends_nearby, container, false);

        rvMapFriend = (RecyclerView) v.findViewById(R.id.rvMapFriend);
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

    private void setRVAdapter(List<Friend> friendList){
        MapFriendRVAdapter myAdapter = new MapFriendRVAdapter(getActivity(),friendList,mMap) ;
        rvMapFriend.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvMapFriend.setAdapter(myAdapter);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in TARC and move the camera
        LatLng tarc = new LatLng(3.215049, 101.726534);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tarc, 19));

        try{
            friendList = new ArrayList<>();
            downloadFriendRecords(getActivity(),GET_FRIEND_URL);

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getContext(), "Download Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

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
                LocationManager locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);

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

                        return true;
                    }

                    // if the current location is in service area and gps is turned on, update the current location and status to database
                    else {
                        updateLatitude(getContext(), "https://tarcomm.000webhostapp.com/updateLatitude.php", lastKnownLatitude);
                        updateLongitude(getContext(), "https://tarcomm.000webhostapp.com/updateLongitude.php", lastKnownLongitude);

                        //move the camera to current location
                        LatLng lastKnownLocation = new LatLng(lastKnownLatitude, lastKnownLongitude);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLocation, 18));
                        return false;
                    }
                }

                return true;
            }
        });
//
//        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
//            @Override
//            public void onInfoWindowClick(Marker marker) {
//
//                Intent i = new Intent(getActivity(),EventActivity.class);
//                getActivity().finish();
//                getActivity().startActivity(i);
//            }
//        });
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResult) {
        if (requestCode == 0) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);

            }
        }

    }

    //add the friend markers
    public void addFriendMarkers(List<Friend> friends) {
        pDialog.dismiss();
        friendMarkers = new ArrayList<>();

        for (Friend friend : friends) {

            double latitude = Double.parseDouble(friend.getLatitude());
            double longitude = Double.parseDouble(friend.getLongitude());

            //make sure the userFullName is currently active
            if (latitude != 0.00 && longitude != 0.00) {

                LatLng friendLocation = new LatLng(latitude, longitude);
                friendMarkers.add(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_friends))
                        .title(friend.getFriendName())
                        .position(friendLocation));
            }
        }

        for (int i = 0; i < friendMarkers.size(); i++) {

            for (int j = 0; j < friendMarkers.size(); j++) {
                // if there is duplicated location for different events
                if (friendMarkers.get(i) != friendMarkers.get(j) && friendMarkers.get(i).getPosition().latitude == friendMarkers.get(j).getPosition().latitude && friendMarkers.get(i).getPosition().longitude == friendMarkers.get(j).getPosition().longitude)

                {
                    // make the location slightly different in order to have 2 or more markers on a same spot
                    LatLng duplicatedPlace = friendMarkers.get(j).getPosition();
                    double newLat = duplicatedPlace.latitude + (Math.random() / 25000);
                    double newLng = duplicatedPlace.longitude + (Math.random() / 25000);

                    LatLng finalLatLng = new LatLng(newLat, newLng);

                    friendMarkers.get(j).position(finalLatLng);
                }

                friendMarkersList.add(mMap.addMarker(friendMarkers.get(i)));
            }
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
                                        JSONObject friendResponse = (JSONObject) j.get(i);
                                        String friendEmail = friendResponse.getString("friendEmail");
                                        String friendType = friendResponse.getString("type");
                                        String friendName = friendResponse.getString("friendName");
                                        String profilePicURL = friendResponse.getString("profilePicURL");
                                        String friendLastModified = friendResponse.getString("friendLastModified");
                                        String friendLatitude = String.valueOf(friendResponse.getString("friendLatitude"));
                                        String friendLongitude = String.valueOf(friendResponse.getString("friendLongitude"));

                                        Friend friend = new Friend(userEmail, friendEmail, friendType, friendName, profilePicURL, friendLastModified, friendLatitude, friendLongitude);
                                        friendList.add(friend);

                                    }
                                    //Load friendList into RecyclerView Adapter
                                    setRVAdapter(friendList);

                                    //Add markers into the map
                                    addFriendMarkers(friendList);

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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}



