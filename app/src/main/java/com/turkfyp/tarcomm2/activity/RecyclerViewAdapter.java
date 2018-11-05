package com.turkfyp.tarcomm2.activity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

import com.bumptech.glide.Glide;
import com.turkfyp.tarcomm2.R;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<String> images = new ArrayList<>();
    private ArrayList<String> prices = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter(Context mContext,ArrayList<String> titles, ArrayList<String> images, ArrayList<String> prices ) {
        this.titles = titles;
        this.images = images;
        this.prices = prices;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        Log.d(TAG, "onCreateViewHolder: called.");

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerlayout_listitem,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Log.d(TAG,"onBindViewHolder: called.");

        Glide.with(mContext)
                .asBitmap()
                .load(images.get(position))
                .into(viewHolder.recyclerImage);

        viewHolder.rvTitle.setText(titles.get(position));
        viewHolder.rvPrice.setText(prices.get(position));

        viewHolder.recyclerImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.d(TAG,"onClick: clicked on an image: ");
            }
        });
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView recyclerImage;
        TextView rvTitle,rvPrice;

        public ViewHolder(View itemView){
            super(itemView);
            recyclerImage = itemView.findViewById(R.id.recyclerImage);
            rvTitle = itemView.findViewById(R.id.rvTitle);
            rvPrice = itemView.findViewById(R.id.rvPrice);


        }
    }

}
