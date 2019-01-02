package com.turkfyp.tarcomm2.DatabaseObjects;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.widget.CircularProgressDrawable;
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
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.turkfyp.tarcomm2.R;
import com.turkfyp.tarcomm2.activity.EventDetailsActivity;
import com.turkfyp.tarcomm2.activity.ViewOtherProfileActivity;

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

public class MapFriendRVAdapter extends RecyclerView.Adapter<MapFriendRVAdapter.MyViewHolder>{

    RequestOptions options;
    private Context mContext ;
    private List<Friend> friendList;

    GoogleMap googleMap;


    public MapFriendRVAdapter(Context mContext, List lst, GoogleMap googleMap) {
        this.mContext = mContext;
        this.friendList = lst;
        this.googleMap = googleMap;

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.tvMapFriendName.setText(friendList.get(position).getFriendName());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.map_friend_records, parent, false);//here

        //OnClick Listener for RecyclerView
        final MapFriendRVAdapter.MyViewHolder viewHolder = new MapFriendRVAdapter.MyViewHolder(view);
        viewHolder.mapFriendRecords_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(mContext,ViewOtherProfileActivity.class);
                i.putExtra("email",friendList.get(viewHolder.getAdapterPosition()).getFriendEmail());
                mContext.startActivity(i);
            }
        });

        viewHolder.ivLocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double lat = Double.parseDouble(friendList.get(viewHolder.getAdapterPosition()).getLatitude());
                double lng = Double.parseDouble(friendList.get(viewHolder.getAdapterPosition()).getLongitude());

                LatLng coordinate = new LatLng(lat, lng);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 18));
            }
        });

        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvMapFriendName;
        LinearLayout mapFriendRecords_container;
        ImageView ivLocate;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvMapFriendName = (TextView) itemView.findViewById(R.id.tvMapFriendName);
            ivLocate = (ImageView) itemView.findViewById(R.id.ivLocate);
            mapFriendRecords_container = (LinearLayout)itemView.findViewById(R.id.mapFriendRecords_container);
        }
    }

}
