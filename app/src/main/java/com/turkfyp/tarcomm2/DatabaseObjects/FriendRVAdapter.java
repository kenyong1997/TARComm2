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

import java.util.List;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.turkfyp.tarcomm2.R;
import com.turkfyp.tarcomm2.activity.ViewOtherProfileActivity;

public class FriendRVAdapter extends RecyclerView.Adapter<FriendRVAdapter.MyViewHolder> {

    RequestOptions options;
    private Context mContext ;
    private List<Friend> friendList;

    Bitmap bitmap;


    public FriendRVAdapter(Context mContext, List friendList) {
        this.mContext = mContext;
        this.friendList = friendList;

        //For Glide image
        options = new RequestOptions()
                .fitCenter()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.background_white)
                .error(R.drawable.background_white);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.friend_list_records, parent, false);

        //OnClick Listener for RecyclerView
        final FriendRVAdapter.MyViewHolder viewHolder = new FriendRVAdapter.MyViewHolder(view);
        viewHolder.friendListRecords_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Intent i = new Intent(mContext,ViewOtherProfileActivity.class);
                    i.putExtra("email",friendList.get(viewHolder.getAdapterPosition()).getFriendEmail());
                    mContext.startActivity(i);

            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tvFriendName.setText(friendList.get(position).getFriendName());

        // load image using Glide
        Glide.with(mContext).load(friendList.get(position).getProfilePicURL()).apply(options).into(holder.imageViewFriendList);
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout friendListRecords_container;
        ImageView imageViewFriendList;
        TextView tvFriendName;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvFriendName = (TextView) itemView.findViewById(R.id.tvFriendName);
            imageViewFriendList = (ImageView) itemView.findViewById(R.id.imageViewFriendList);
            friendListRecords_container = (LinearLayout)itemView.findViewById(R.id.friendListRecords_container);
        }
    }
}
