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

        }else{
            holder.tvMainItemPrice.setText(null);
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

                //Adapter position starts from -1
                itemDetailIntent.putExtra("itemName", itemList.get(viewHolder.getAdapterPosition()+1).getItemName());
                itemDetailIntent.putExtra("itemPrice",itemList.get(viewHolder.getAdapterPosition()+1).getItemPrice());
                itemDetailIntent.putExtra("itemDesc",itemList.get(viewHolder.getAdapterPosition()+1).getItemDescription());
                itemDetailIntent.putExtra("itemSeller",itemList.get(viewHolder.getAdapterPosition()+1).getSellerName());
                itemDetailIntent.putExtra("sellerContact",itemList.get(viewHolder.getAdapterPosition()+1).getSellerContact());
                itemDetailIntent.putExtra("email",itemList.get(viewHolder.getAdapterPosition()+1).getEmail());

                if(itemList.get(viewHolder.getAdapterPosition()+1).getItemCategory().equals("WTT"))
                    itemDetailIntent.putExtra("checkWTT",true);
                else
                    itemDetailIntent.putExtra("checkYourUpload",false);

                ImageView ivMarketItem = (ImageView) view.findViewById(R.id.ivMarketItem);
                ivMarketItem.buildDrawingCache();
                Bitmap image = ivMarketItem.getDrawingCache();
                itemDetailIntent.putExtra("Image", image);
                itemDetailIntent.putExtra("ImageURL", itemList.get(viewHolder.getAdapterPosition()+1).getImageURL());

                mContext.startActivity(itemDetailIntent);
            }
        });

        return new MyViewHolder(view);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvMainItemName, tvMainItemPrice;
        ImageView ivMarketItem;
        LinearLayout main_item_container;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvMainItemName = (TextView) itemView.findViewById(R.id.tvMainItemName);
            tvMainItemPrice = (TextView) itemView.findViewById(R.id.tvMainItemPrice);
            ivMarketItem = (ImageView) itemView.findViewById(R.id.ivMarketItem);
            main_item_container = (LinearLayout)itemView.findViewById(R.id.main_item_container);
        }
    }
}
