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

public class EventRVAdapter extends RecyclerView.Adapter<EventRVAdapter.MyViewHolder>{

    RequestOptions options;
    private Context mContext ;
    private List<Event> eventList;

    Bitmap bitmap;

    public EventRVAdapter(Context mContext, List lst) {
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
        holder.tvEventName.setText(eventList.get(position).getEventName());
        holder.tvEventDateTime.setText(eventList.get(position).getEventDateTime());
        holder.tvEventEndDateTime.setText(eventList.get(position).getEventEndDateTime());

        // load image using Glide
        Glide.with(mContext).load(eventList.get(position).getEventImageURL()).apply(options).into(holder.ivImageEvent);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.event_records, parent, false);

        //OnClick Listener for RecyclerView
        final EventRVAdapter.MyViewHolder viewHolder = new EventRVAdapter.MyViewHolder(view);
        viewHolder.eventRecords_container.setOnClickListener(new View.OnClickListener() {
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

        TextView tvEventName, tvEventDateTime, tvEventEndDateTime;
        ImageView ivImageEvent;
        LinearLayout eventRecords_container;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvEventName = (TextView) itemView.findViewById(R.id.tvEventName);
            tvEventDateTime = (TextView) itemView.findViewById(R.id.tvEventDateTime);
            tvEventEndDateTime = (TextView) itemView.findViewById(R.id.tvEventEndDateTime);
            ivImageEvent = (ImageView) itemView.findViewById(R.id.ivImageEvent);
            eventRecords_container = (LinearLayout)itemView.findViewById(R.id.eventRecords_container);
        }
    }

    //Convert Glide Image to bitmap
    private void convertImage(String imageURL){
        class ConvertImage extends AsyncTask<String, Void, Bitmap> {

            @Override
            protected Bitmap doInBackground(String... strings) {
                String imageURL = strings[0];

                try {
                    bitmap = Glide.with(mContext)
                            .asBitmap()
                            .load(imageURL)
                            .apply(options)
                            .submit(200,200)
                            .get();

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
            }
        }
        ConvertImage convertImage = new ConvertImage();
        convertImage.execute(imageURL);
    }
}
