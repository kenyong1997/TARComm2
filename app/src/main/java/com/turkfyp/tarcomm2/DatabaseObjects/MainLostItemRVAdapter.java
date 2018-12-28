package com.turkfyp.tarcomm2.DatabaseObjects;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.widget.CircularProgressDrawable;
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
import com.turkfyp.tarcomm2.activity.LostFoundDetailActivity;

import java.util.List;

public class MainLostItemRVAdapter extends RecyclerView.Adapter<MainLostItemRVAdapter.MyViewHolder> {

    RequestOptions options;
    private Context mContext ;
    private List<LostFound> lostFoundItemList;

    public MainLostItemRVAdapter(Context mContext, List lst) {
        this.mContext = mContext;
        this.lostFoundItemList = lst;

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(mContext);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        //For Glide image
        options = new RequestOptions()
                .centerCrop()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(circularProgressDrawable)
                .error(R.drawable.background_white);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.tvMainLostItemName.setText( lostFoundItemList.get(position).getLostItemName());
        holder.tvLostCategory.setText( lostFoundItemList.get(position).getCategory());

        // load image using Glide
        Glide.with(mContext).load( lostFoundItemList.get(position).getLostItemURL()).apply(options).into(holder.ivLostFoundItem);


    }

    @Override
    public int getItemCount() {
        return  lostFoundItemList.size();
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.main_lostfound_records, parent, false);

        //OnClick Listener for RecyclerView
        final MyViewHolder viewHolder = new MyViewHolder(view);
        viewHolder.main_lostfound_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent lostItemDetailIntent = new Intent(mContext,LostFoundDetailActivity.class);

                lostItemDetailIntent.putExtra("lostItemName", lostFoundItemList.get(viewHolder.getAdapterPosition()).getLostItemName());
                lostItemDetailIntent.putExtra("lostItemDesc",lostFoundItemList.get(viewHolder.getAdapterPosition()).getLostItemDesc());
                lostItemDetailIntent.putExtra("lostCategory",lostFoundItemList.get(viewHolder.getAdapterPosition()).getCategory());
                lostItemDetailIntent.putExtra("lostDate",lostFoundItemList.get(viewHolder.getAdapterPosition()).getLostDate());
                lostItemDetailIntent.putExtra("lostItemContactNo",lostFoundItemList.get(viewHolder.getAdapterPosition()).getContactNo());
                lostItemDetailIntent.putExtra("lostItemContactName",lostFoundItemList.get(viewHolder.getAdapterPosition()).getContactName());
                lostItemDetailIntent.putExtra("email",lostFoundItemList.get(viewHolder.getAdapterPosition()).getEmail());
                lostItemDetailIntent.putExtra("checkYourUpload",false);
                lostItemDetailIntent.putExtra("LostImageURL", lostFoundItemList.get(viewHolder.getAdapterPosition()).getLostItemURL());

                mContext.startActivity(lostItemDetailIntent);
            }
        });

        return viewHolder;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvMainLostItemName, tvLostCategory;
        ImageView ivLostFoundItem;
        LinearLayout main_lostfound_container;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvMainLostItemName = (TextView) itemView.findViewById(R.id.tvMainLostItemName);
            tvLostCategory = (TextView) itemView.findViewById(R.id.tvLostCategory);
            ivLostFoundItem = (ImageView) itemView.findViewById(R.id.ivLostFoundItem);
            main_lostfound_container = (LinearLayout)itemView.findViewById(R.id.main_lostfound_container);
        }
    }
}
