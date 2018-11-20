package com.turkfyp.tarcomm2.DatabaseObjects;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.turkfyp.tarcomm2.R;
import com.turkfyp.tarcomm2.activity.EventDetailsActivity;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MapEventRVAdapter extends RecyclerView.Adapter<MapEventRVAdapter.MyViewHolder>{

    RequestOptions options;
    private Context mContext ;
    private List<Event> eventList;

    Bitmap bitmap;

    public MapEventRVAdapter(Context mContext, List lst) {
        this.mContext = mContext;
        this.eventList = lst;

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

        public MyViewHolder(View itemView) {
            super(itemView);
            tvMapEventName = (TextView) itemView.findViewById(R.id.tvMapEventName);
            tvMapEventDate = (TextView) itemView.findViewById(R.id.tvMapEventDate);
            tvMapEventVenue = (TextView) itemView.findViewById(R.id.tvMapEventVenue);
            mapEventRecords_container = (LinearLayout)itemView.findViewById(R.id.mapEventRecords_container);
        }
    }


}
