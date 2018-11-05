package com.turkfyp.tarcomm2.DatabaseObjects;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.turkfyp.tarcomm2.R;

import java.util.List;

public class RvAdapter extends RecyclerView.Adapter<RvAdapter.MyViewHolder> {

    RequestOptions options ;
    private Context mContext ;
    private List<Item> mData ;

    public RvAdapter(Context mContext, List lst) {


        this.mContext = mContext;
        this.mData = lst;
        options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.background_white)
                .error(R.drawable.background_white);

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.tvItemName.setText(mData.get(position).getItemName());
        holder.tvItemSeller.setText(mData.get(position).getSellerName());
        holder.tvItemPrice.setText(mData.get(position).getItemPrice());

        // load image from the internet using Glide
        Glide.with(mContext).load(mData.get(position).getImageURL()).apply(options).into(holder.ivTradingImage);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.item_records, parent, false);
        // click listener here
        return new MyViewHolder(view);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvItemName, tvItemPrice,tvItemSeller;
        ImageView ivTradingImage,ivPrice;


        public MyViewHolder(View itemView) {
            super(itemView);
            tvItemName = (TextView) itemView.findViewById(R.id.tvItemName);
            tvItemPrice = (TextView) itemView.findViewById(R.id.tvItemPrice);
            tvItemSeller= (TextView) itemView.findViewById(R.id.tvItemSeller);
            ivTradingImage = (ImageView) itemView.findViewById(R.id.ivItemImage);
        }
    }
}
