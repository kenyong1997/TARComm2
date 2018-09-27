package com.turkfyp.tarcomm2.activity;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


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
import com.turkfyp.tarcomm2.DatabaseObjects.Item;
import com.turkfyp.tarcomm2.DatabaseObjects.User;
import com.turkfyp.tarcomm2.MapObjects.DirectionFinder;
import com.turkfyp.tarcomm2.MapObjects.DirectionFinderListener;
import com.turkfyp.tarcomm2.MapObjects.GPSTracker;
import com.turkfyp.tarcomm2.MapObjects.Route;
import com.turkfyp.tarcomm2.R;


import static android.content.Context.LOCATION_SERVICE;


public class MapActivity extends android.support.v4.app.Fragment implements OnMapReadyCallback, DirectionFinderListener {


    //DECLARE THE VARIABLES
    protected GoogleMap mMap;
    private Button btnDirect;
    private Spinner spinnerFilter;
    private TextView tvFilterTitle;
    private AutoCompleteTextView etOrigin;
    private AutoCompleteTextView etDestination;

    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private TextView tvNotInServiceArea;
    private ProgressDialog pDialog;
    protected RequestQueue queue;
    protected List<Marker> eventMarkersList = new ArrayList<>();
    protected List<Marker> itemMarkersList = new ArrayList<>();
    protected List<Marker> userMarkersList = new ArrayList<>();

    //LOCATION LISTENER
    private GPSTracker gpsTracker;
    private Location mLastKnownLocation;
    private double lastKnownLatitude, lastKnownLongitude;


    //EVENT DOWNLOADING
    private static String GET_CURRENT_EVENT_URL = "https://taroute.000webhostapp.com/getCurrentEvent.php";
    private static String GET_UPCOMING_EVENT_URL = "https://taroute.000webhostapp.com/getUpcomingEvent.php";
    protected List<Event> eventList;
    protected String currentDate;
    private List<MarkerOptions> eventMarkers = new ArrayList<>();

    //ITEM DOWNLOADING
    private static String GET_ITEM_URL = "https://taroute.000webhostapp.com/getItem.php";
    protected List<Item> itemList;
    private List<MarkerOptions> itemMarkers = new ArrayList<>();

    public static final String STATUS_ON = "ON";

    private static String GET_USER_URL = "https://taroute.000webhostapp.com/getAllUsers.php";
    protected List<User> userList;
    private List<MarkerOptions> userMarkers = new ArrayList<>();

    Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_map_content, container, false);


        //hide action bar
        //((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        //LINK CODES WITH UI
        btnDirect = (Button) v.findViewById(R.id.btnDirect);
        //spinnerFilter = (Spinner) v.findViewById(R.id.spinnerFilter);

        pDialog = new ProgressDialog(v.getContext());


        //set up spinner
        //spinnerFilter.setSelection(0);
        //tvFilterTitle = (TextView) v.findViewById(R.id.tvFilterTitle);
        //tvFilterTitle.setText("All \u25BD");

        if (mMap != null) {
            //download all the marker details
            //downloadCurrentEventDetails();
            //downloadUpcomingEventDetails();
            //downloadItemDetails();
        }


        //set up place autocomplete adapter

        etOrigin = (AutoCompleteTextView) v.findViewById(R.id.etOrigin);
        etOrigin.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), R.layout.autocomplete_list_item));


        etDestination = (AutoCompleteTextView) v.findViewById(R.id.etDestination);
        etDestination.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), R.layout.autocomplete_list_item));


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //get current Date
        Date cal = Calendar.getInstance().getTime();
        java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd");
        currentDate = df.format(cal);


        //if the DIRECT button is clicked
        btnDirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();

            }
        });

        return v;
    }


    //send the request based on the origin and destination address
    private void sendRequest() {

        // set the origin and destination
        String origin = etOrigin.getText().toString();
        String destination = etDestination.getText().toString();


        if (origin.isEmpty()) {
            Toast.makeText(getView().getContext(), "Please enter origin address", Toast.LENGTH_SHORT).show();
            return;

        } else {
            if (origin.equals("Current Location")) {
                origin = lastKnownLatitude + "," + lastKnownLongitude;
            }
        }

        if (destination.isEmpty()) {
            Toast.makeText(getView().getContext(), "Please enter destination address", Toast.LENGTH_SHORT).show();
            return;

        }


        try {
            new DirectionFinder((DirectionFinderListener) this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        // Add a marker in TARC and move the camera

        LatLng tarc = new LatLng(3.215049, 101.726534);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tarc, 19));

       /* mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        originMarkers.add(mMap.addMarker(new MarkerOptions()
                .title("TARUC")
                .position(tarc)));*/

        //when the spinner was clicked to change
        /*
        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    tvFilterTitle.setText("All \u25BD");

                    //download all the marker details
                    //downloadCurrentEventDetails();
                    //downloadUpcomingEventDetails();
                    //downloadItemDetails();
                    // downloadUsers();

                    //execute the periodical task to update users every 10 seconds
                    handler.post(tryDownloadUser);

                } else if (position == 1) {
                    tvFilterTitle.setText("Current Events \u25BD");

                    //remove other markers and download only relevant marker details
                    removeOtherMarkers();
                    downloadCurrentEventDetails();

                    //stop the handler
                    handler.removeCallbacks(tryDownloadUser);

                } else if (position == 2) {
                    tvFilterTitle.setText("Upcoming Events \u25BD");

                    removeOtherMarkers();
                    downloadUpcomingEventDetails();
                } else if (position == 3) {
                    tvFilterTitle.setText("Market Items \u25BD");

                    //stop the handler
                    handler.removeCallbacks(tryDownloadUser);

                    removeOtherMarkers();
                    //downloadItemDetails();
                } else if (position == 4) {
                    tvFilterTitle.setText("Friends \u25BD");

                    removeOtherMarkers();
                    //downloadUsers();

                    //execute the periodical task to update users every 10 seconds
                    handler.post(tryDownloadUser);
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
*/

        //check if someone select a event venue in event details
        /*
        if (!EventDetails.EVENT_VENUE_LOCATION.matches("")) {
            //LatLng eventLocation = getLocationFromAddress(getContext(), EventDetails.EVENT_VENUE_LOCATION);

            Geocoding geocoding = new Geocoding();
            geocoding.execute(EventDetails.EVENT_VENUE_LOCATION, "directToEvent");

        }
*/
/*
        //check if someone select the desired meeting point in item details
        if (!ItemDetails.DESIRED_MEETING_POINT.matches("")) {
            //LatLng itemLocation = getLocationFromAddress(getContext(), ItemDetails.DESIRED_MEETING_POINT);

            Geocoding geocoding = new Geocoding();
            geocoding.execute(ItemDetails.DESIRED_MEETING_POINT, "directToItem");
        }
*/
       /*INITIAL PART -- CREATE POLYLINES MANUALLY

           mMap.addMarker(new MarkerOptions()
                .position(tarc)
                .title("TARUC")
                //add custom icon
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pinpoint))
        );

        LatLng citcBusStop = new LatLng(3.214704, 101.727169);

        //DRAW POLYLINE
        mMap.addPolyline(new PolylineOptions().add(

                tarc,
                new LatLng(3.214788, 101.726608),
                new LatLng(3.214217, 101.726670),
                citcBusStop)
                .width(10)
                .color(Color.MAGENTA)
        );*/


        //Allow user to set location

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


                //clear the previous markers and polyline if needed
                //THIS PART OF CODE IS SUBJECT TO REMOVE
                if (originMarkers != null) {
                    for (Marker marker : originMarkers) {
                        marker.remove();
                    }
                }

                if (destinationMarkers != null) {
                    for (Marker marker : destinationMarkers) {
                        marker.remove();
                    }
                }

                if (polylinePaths != null) {
                    for (Polyline polyline : polylinePaths) {
                        polyline.remove();
                    }
                }


                // declare location manager and boolean to check location provider availability
                LocationManager locationManager = (LocationManager)
                        getContext().getSystemService(LOCATION_SERVICE);

                boolean isGPSEnabled = locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);
                boolean isNetworkEnabled = locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER);


                //if location provider is off
                if (isGPSEnabled == false && isNetworkEnabled == false) {

                    promptForLocationService();
                }

                //create a new Gps tracker object to get the last known location of user.
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

                        Dialog dialog = alertBuilder.create();
                        dialog.show();


                    }
                    // if the current location is in service area and gps is turned on, update the current location and status to database
                    else {

                        updateStatus(getContext(), "https://taroute.000webhostapp.com/updateStatus.php", STATUS_ON);
                        updateLatitude(getContext(), "https://taroute.000webhostapp.com/updateLatitude.php", lastKnownLatitude);
                        updateLongitude(getContext(), "https://taroute.000webhostapp.com/updateLongitude.php", lastKnownLongitude);


                        // set the current location in the coordinates form
                        etOrigin.setText("Current Location");
                        etOrigin.setTextColor(Color.RED);

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
                for (int i = 0; i < userList.size(); i++) {
                    if (marker.getTitle().toUpperCase().equals(userList.get(i).getUserName().toUpperCase())) {
                        contactUserByWhatsapp(userList.get(i).getContactNumber());
                    }
                }
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

                        //let user to turn on location provider
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
/*
    public void downloadItemDetails() {
        try {
            itemList = new ArrayList<>();
            downloadItemRecords(getActivity().getApplicationContext(), GET_ITEM_URL);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
*/

    //normal Download Users
    public void downloadUsers() {

        try {
            userList = new ArrayList<>();
            downloadUserRecords(getActivity().getApplicationContext(), GET_USER_URL);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }


    //this is to Download Users periodically. In this case, update every 5 seconds
    private final Runnable tryDownloadUser = new Runnable() {
        public void run() {
            try {

                removeFriendMarkers();

                try {
                    userList = new ArrayList<>();
                    downloadUserRecords(getActivity().getApplicationContext(), GET_USER_URL);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                handler.postDelayed(this, 10000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    public void removeFriendMarkers() {
        if (userMarkersList != null) {
            for (Marker marker : userMarkersList) {
                marker.remove();
            }
        }
    }

    public void removeOtherMarkers() {
        if (eventMarkersList != null) {
            for (Marker marker : eventMarkersList) {
                marker.remove();
            }
        }
        if (itemMarkersList != null) {
            for (Marker marker : itemMarkersList) {
                marker.remove();
            }
        }
        if (userMarkersList != null) {
            for (Marker marker : userMarkersList) {
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

    //Start finding direction, remove all the polylines and markers
    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(getView().getContext(), "Please wait.",
                "Finding direction...", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
    }

    //Found the direction, place markers and calculate distance & duration
    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 18));
            ((TextView) getView().findViewById(R.id.tvDuration)).setText(route.duration.text);
            ((TextView) getView().findViewById(R.id.tvDistance)).setText(route.distance.text);
            //((TextView) getView().findViewById(R.id.tvDistance)).setText((route.distance.value)*1000);


            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_origin))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.MAGENTA).
                    width(18);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
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
                                        String eventCreator = eventResponse.getString("eventCreator");
                                        String eventDesc = eventResponse.getString("eventDesc");
                                        String eventImageURL = eventResponse.getString("url");
                                        String eventVenue = eventResponse.getString("eventVenue");

                                        Event event = new Event(eventName, eventCreator, eventDateTime, eventDesc, eventImageURL, eventVenue);
                                        eventList.add(event);

                                    }
                                    //addEventMarkers(eventList);

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

    //download all the items
    /*
    public void downloadItemRecords(Context context, String url) {

        if (!pDialog.isShowing()) {
            pDialog.setMessage("Adding Items...");
            pDialog.show();
        }
        queue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            itemList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject itemResponse = (JSONObject) response.get(i);
                                String itemSeller = itemResponse.getString("itemSeller");
                                String itemName = itemResponse.getString("itemName");
                                double itemPrice = Double.parseDouble(itemResponse.getString("itemPrice"));
                                String itemDescription = itemResponse.getString("itemDescription");
                                String itemCategory = itemResponse.getString("itemCategory");
                                String dateAdded = itemResponse.getString("dateAdded");
                                String imageURL = itemResponse.getString("url");
                                String sellerContact = itemResponse.getString("contactNumber");
                                String desiredLocation = itemResponse.getString("desiredLocation");

                                Item item = new Item(itemSeller, itemName, itemPrice, itemDescription, itemCategory, dateAdded, imageURL, sellerContact, desiredLocation);
                                itemList.add(item);
                            }

                            addItemMarkers(itemList);

                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getActivity(), "Error" + volleyError.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });


        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }
*/
    //download all the users

     public void downloadUserRecords(Context context, String url) {

      //  if (!pDialog.isShowing()) {
       //     pDialog.setMessage("Adding Friends...");
        //    pDialog.show();}
        queue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            itemList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject userResponse = (JSONObject) response.get(i);
                                String userName = userResponse.getString("userName");
                                String password = userResponse.getString("password");
                                String contactNumber = userResponse.getString("contactNumber");
                                String userEmail = userResponse.getString("userEmail");
                                String latitude = userResponse.getString("latitude");
                                String longitude = userResponse.getString("longitude");
                                String status = userResponse.getString("status");

                                User friend = new User(userName, password, contactNumber, userEmail, latitude, longitude, status);
                                userList.add(friend);
                            }

                            //addFriendMarkers(userList);

                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getActivity(), "Error" + volleyError.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });


        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }
/*
    //add the event markers
    public void addEventMarkers(List<Event> events) {
        pDialog.dismiss();
        eventMarkers = new ArrayList<>();


        for (Event event : events) {
            Geocoding geocoding = new Geocoding();
            geocoding.execute(event.getEventVenue(), "eventGeocoding", event.getEventName(), event.getEventDateTime());
        }

    }
   */
/*
    //add the item markers
    public void addItemMarkers(List<Item> items) {
        pDialog.dismiss();
        itemMarkers = new ArrayList<>();

        for (Item item : items) {

            Geocoding geocoding = new Geocoding();
            geocoding.execute(item.getDesiredLocation(), "itemGeocoding", item.getItemName(), item.getItemDescription());

        }
    }

    //add the friend markers
    public void addFriendMarkers(List<User> friends) {
        pDialog.dismiss();
        userMarkers = new ArrayList<>();

        for (User friend : friends) {

            double latitude = Double.parseDouble(friend.getLatitude());
            double longitude = Double.parseDouble(friend.getLongitude());


            //make sure it is not the logged in user
            if (!friend.getUserName().toUpperCase().equals(MainActivity.LOGGED_IN_USER.toUpperCase())) {

                //make sure the user is currently active
                if (latitude != 0.00 && longitude != 0.00 && friend.getStatus().equals(STATUS_ON)) {

                    LatLng friendLocation = new LatLng(latitude, longitude);
                    userMarkers.add(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_friends))
                            .title(friend.getUserName())
                            .position(friendLocation));
                }
            }
        }

        for (int i = 0; i < userMarkers.size(); i++) {

            for (int j = 0; j < userMarkers.size(); j++) {
                // if there is duplicated location for different events
                if (userMarkers.get(i) != userMarkers.get(j) && userMarkers.get(i).getPosition().latitude == userMarkers.get(j).getPosition().latitude && userMarkers.get(i).getPosition().longitude == userMarkers.get(j).getPosition().longitude)

                {
                    // make the location slightly different in order to have 2 or more markers on a same spot
                    LatLng duplicatedPlace = userMarkers.get(j).getPosition();
                    double newLat = duplicatedPlace.latitude + (Math.random() / 25000);
                    double newLng = duplicatedPlace.longitude + (Math.random() / 25000);

                    LatLng finalLatLng = new LatLng(newLat, newLng);

                    userMarkers.get(j).position(finalLatLng);
                }

                userMarkersList.add(mMap.addMarker(userMarkers.get(i)));
            }
        }

    }
*/

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
                response = getLatLongByURL("https://maps.google.com/maps/api/geocode/json?address=" + encodedAddress);
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

                // Toast.makeText(getContext(), "coordinates:" + lat + "," + lng, Toast.LENGTH_SHORT).show();

                LatLng coordinate = new LatLng(lat, lng);

/*
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
                } else if (taskType == "itemGeocoding") {

                    if (coordinate != null) {
                        itemMarkers.add(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_selling_item))
                                .title(itemName)
                                .position(coordinate)
                                .snippet(itemDesc));
                    }


                    for (int i = 0; i < itemMarkers.size(); i++) {

                        for (int j = 0; j < itemMarkers.size(); j++) {


                            // if there is duplicated location with other items or events
                            if (itemMarkers.get(i) != itemMarkers.get(j) && itemMarkers.get(i).getPosition().latitude == itemMarkers.get(j).getPosition().latitude && itemMarkers.get(i).getPosition().longitude == itemMarkers.get(j).getPosition().longitude)

                            {
                                // make the location slightly different in order to have 2 or more markers on a same spot
                                LatLng duplicatedPlace = itemMarkers.get(i).getPosition();
                                double newLat = duplicatedPlace.latitude + (Math.random() / 25000);
                                double newLng = duplicatedPlace.longitude + (Math.random() / 25000);

                                LatLng finalLatLng = new LatLng(newLat, newLng);

                                itemMarkers.get(i).position(finalLatLng);
                            }

                        }

                        for (int k = 0; k < eventMarkers.size(); k++) {
                            if (itemMarkers.get(i).getPosition().latitude == eventMarkers.get(k).getPosition().latitude && itemMarkers.get(i).getPosition().longitude == eventMarkers.get(k).getPosition().longitude) {
                                // make the location slightly different in order to have 2 or more markers on a same spot
                                LatLng duplicatedPlace = itemMarkers.get(i).getPosition();
                                double newLat = duplicatedPlace.latitude + (Math.random() / 25000);
                                double newLng = duplicatedPlace.longitude + (Math.random() / 25000);

                                LatLng finalLatLng = new LatLng(newLat, newLng);

                                itemMarkers.get(i).position(finalLatLng);

                            }
                        }

                        itemMarkersList.add(mMap.addMarker(itemMarkers.get(i)));
                    }

                } else if (taskType == "directToEvent") {
                    if (coordinate != null)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 18));
                    EventDetails.EVENT_VENUE_LOCATION = "";
                } else if (taskType == "directToItem") {
                    if (coordinate != null)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 18));
                    ItemDetails.DESIRED_MEETING_POINT = "";
                }

*/
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
                    //params.put("userName", MainActivity.LOGGED_IN_USER);
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
                    //params.put("userName", MainActivity.LOGGED_IN_USER);
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
                    //params.put("userName", MainActivity.LOGGED_IN_USER);
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


    public void contactUserByWhatsapp(String userContact) {
        PackageManager pm = getActivity().getPackageManager();

        try {
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);

            if (pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES) != null) {
                Uri uri = Uri.parse("smsto:" + "+6" + userContact);
                Intent sendIntent = new Intent(Intent.ACTION_SENDTO, uri);

                //sendIntent.putExtra(Intent.EXTRA_TEXT, "Hi, nice to meet you!" );
                sendIntent.setPackage("com.whatsapp");
                startActivity(Intent.createChooser(sendIntent, ""));
            }

        } catch (PackageManager.NameNotFoundException e) {

            Toast.makeText(getContext(), "WhatsApp not found", Toast.LENGTH_SHORT)
                    .show();

            /*
             //DIRECT USER TO MARKET AND DOWNLOAD WHATSAPPP (FUTURE USE IF NEEDED)

             Uri uri = Uri.parse("market://details?id=com.whatsapp");
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(goToMarket);
            */

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //stop refreshing the friend markers
        handler.removeCallbacks(tryDownloadUser);

        //make user status become inactive
        //updateStatus(getContext(), "https://taroute.000webhostapp.com/updateStatus.php","OFF");
    }
}



