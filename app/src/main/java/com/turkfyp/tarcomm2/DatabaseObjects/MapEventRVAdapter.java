package com.turkfyp.tarcomm2.DatabaseObjects;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.turkfyp.tarcomm2.R;
import com.turkfyp.tarcomm2.activity.EventDetailsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

public class MapEventRVAdapter extends RecyclerView.Adapter<MapEventRVAdapter.MyViewHolder>{

    RequestOptions options;
    private Context mContext ;
    private List<Event> eventList;

    private String GOOGLE_MAP_API = "AIzaSyDRSuYdn9yoCACUGxd4qGkgl59276mWvcs";
    GoogleMap googleMap;

    Bitmap bitmap;

    public MapEventRVAdapter(Context mContext, List lst, GoogleMap googleMap) {
        this.mContext = mContext;
        this.eventList = lst;
        this.googleMap = googleMap;

        //For Glide image
        options = new RequestOptions()
                .fitCenter()
//                .skipMemoryCache(true)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.background_white)
                .error(R.drawable.background_white);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.tvMapEventName.setText(eventList.get(position).getEventName());
        holder.tvMapEventDate.setText(eventList.get(position).getEventDateTime()+" to " + eventList.get(position).getEventEndDateTime());
        holder.tvMapEventVenue.setText(eventList.get(position).getEventVenueName());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.map_event_records, parent, false);

        //OnClick Listener for RecyclerView
        final MapEventRVAdapter.MyViewHolder viewHolder = new MapEventRVAdapter.MyViewHolder(view);
        viewHolder.mapEventRecords_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent eventDetailIntent = new Intent(mContext, EventDetailsActivity.class);

                eventDetailIntent.putExtra("eventName", eventList.get(viewHolder.getAdapterPosition()).getEventName());
                eventDetailIntent.putExtra("eventDateTime", eventList.get(viewHolder.getAdapterPosition()).getEventDateTime());
                eventDetailIntent.putExtra("eventDesc", eventList.get(viewHolder.getAdapterPosition()).getEventDesc());
                eventDetailIntent.putExtra("eventVenue", eventList.get(viewHolder.getAdapterPosition()).getEventVenue());
                eventDetailIntent.putExtra("eventEndDatetime",eventList.get(viewHolder.getAdapterPosition()).getEventEndDateTime());

                //convertImage(eventList.get(viewHolder.getAdapterPosition()).getEventImageURL());
                //eventDetailIntent.putExtra("Image", bitmap);
                eventDetailIntent.putExtra("ImageURL", eventList.get(viewHolder.getAdapterPosition()).getEventImageURL());

                mContext.startActivity(eventDetailIntent);

//                Geocoding geocoding = new Geocoding();
//                geocoding.execute(eventList.get(viewHolder.getAdapterPosition()).getEventVenue());
            }
        });

        viewHolder.ivLocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Geocoding geocoding = new Geocoding();
                geocoding.execute(eventList.get(viewHolder.getAdapterPosition()).getEventVenue());
            }
        });

        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvMapEventName, tvMapEventDate, tvMapEventVenue;
        LinearLayout mapEventRecords_container;
        ImageView ivLocate;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvMapEventName = (TextView) itemView.findViewById(R.id.tvMapEventName);
            tvMapEventDate = (TextView) itemView.findViewById(R.id.tvMapEventDate);
            tvMapEventVenue = (TextView) itemView.findViewById(R.id.tvMapEventVenue);
            ivLocate = (ImageView) itemView.findViewById(R.id.ivLocate);
            mapEventRecords_container = (LinearLayout)itemView.findViewById(R.id.mapEventRecords_container);
        }
    }

    //get Location from Address(GEOCODING API)
    private class Geocoding extends AsyncTask<String, Void, String[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        String encodedAddress;

        String address;

        @Override
        protected String[] doInBackground(String... params) {

            address = params[0];

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

                LatLng coordinate = new LatLng(lat, lng);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 18));


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

}
