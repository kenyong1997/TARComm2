package com.turkfyp.tarcomm2.DatabaseObjects;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.turkfyp.tarcomm2.activity.MarketplaceDetailActivity;

import java.util.List;

public class MainItemRVAdapter extends RecyclerView.Adapter<MainItemRVAdapter.MyViewHolder> {

    RequestOptions options;
    private Context mContext ;
    private List<Item> itemList;

    public MainItemRVAdapter(Context mContext, List lst) {
        this.mContext = mContext;
        this.itemList = lst;

        //For Glide image
        options = new RequestOptions()
                .centerCrop()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.background_white)
                .error(R.drawable.background_white);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.tvMainItemName.setText(itemList.get(position).getItemName());
        holder.tvMainItemPrice.setText(itemList.get(position).getItemPrice());


        // load image using Glide
        Glide.with(mContext).load(itemList.get(position).getImageURL()).apply(options).into(holder.ivMarketItem);

        if(!itemList.get(position).getItemCategory().equals("WTT")){
            holder.tvMainItemPrice.setText(String.format("RM %.2f", Double.parseDouble(itemList.get(position).getItemPrice())));
            if(itemList.get(position).getItemCategory().equals("WTS")){
                holder.tvMainItemCategory.setText("Selling");
            }else if(itemList.get(position).getItemCategory().equals("WTB")){
                holder.tvMainItemCategory.setText("Buying");
            }
        }else{
            holder.tvMainItemPrice.setText(null);
            holder.tvMainItemCategory.setText("Trading");
        }

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.main_marketplace_records, parent, false);

        //OnClick Listener for RecyclerView
        final MyViewHolder viewHolder = new MyViewHolder(view);
        viewHolder.main_item_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent itemDetailIntent = new Intent(mContext,MarketplaceDetailActivity.class);

                itemDetailIntent.putExtra("itemName", itemList.get(viewHolder.getAdapterPosition()).getItemName());
                itemDetailIntent.putExtra("itemPrice",itemList.get(viewHolder.getAdapterPosition()).getItemPrice());
                itemDetailIntent.putExtra("itemDesc",itemList.get(viewHolder.getAdapterPosition()).getItemDescription());
                itemDetailIntent.putExtra("itemSeller",itemList.get(viewHolder.getAdapterPosition()).getSellerName());
                itemDetailIntent.putExtra("sellerContact",itemList.get(viewHolder.getAdapterPosition()).getSellerContact());
                itemDetailIntent.putExtra("email",itemList.get(viewHolder.getAdapterPosition()).getEmail());

                if(itemList.get(viewHolder.getAdapterPosition()).getItemCategory().equals("WTT"))
                    itemDetailIntent.putExtra("checkWTT",true);
                else
                    itemDetailIntent.putExtra("checkYourUpload",false);

                itemDetailIntent.putExtra("ImageURL", itemList.get(viewHolder.getAdapterPosition()).getImageURL());

                mContext.startActivity(itemDetailIntent);
            }
        });

        return viewHolder;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvMainItemName, tvMainItemPrice,tvMainItemCategory;
        ImageView ivMarketItem;
        LinearLayout main_item_container;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvMainItemName = (TextView) itemView.findViewById(R.id.tvMainItemName);
            tvMainItemPrice = (TextView) itemView.findViewById(R.id.tvMainItemPrice);
            tvMainItemCategory = (TextView) itemView.findViewById(R.id.tvMainItemCategory);
            ivMarketItem = (ImageView) itemView.findViewById(R.id.ivMarketItem);
            main_item_container = (LinearLayout)itemView.findViewById(R.id.main_item_container);
        }
    }
}
