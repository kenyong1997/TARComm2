package com.turkfyp.tarcomm2.DatabaseObjects;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.turkfyp.tarcomm2.R;
import com.turkfyp.tarcomm2.activity.EditLostItemActivity;
import com.turkfyp.tarcomm2.activity.LostAndFoundActivity;
import com.turkfyp.tarcomm2.activity.ViewOtherProfileActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class FriendSearchRVAdapter extends RecyclerView.Adapter<FriendSearchRVAdapter.MyViewHolder> {

    RequestOptions options;
    private Context mContext ;
    private List<Friend> friendList;

    Bitmap bitmap;

    ProgressDialog progressDialog;

    public FriendSearchRVAdapter(Context mContext, List friendList) {
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
        view = mInflater.inflate(R.layout.friend_search_records, parent, false);

        //OnClick Listener for RecyclerView
        final FriendSearchRVAdapter.MyViewHolder viewHolder = new FriendSearchRVAdapter.MyViewHolder(view);
        viewHolder.friendSearchRecords_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent i = new Intent(mContext,ViewOtherProfileActivity.class);
                i.putExtra("email",friendList.get(viewHolder.getAdapterPosition()).getFriendEmail());
                mContext.startActivity(i);
            }
        });

        progressDialog = new ProgressDialog(mContext);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tvFriendSearchName.setText(friendList.get(position).getFriendName());
        // if friend, then set tv to visible

        if (friendList.get(position).getType().equals("friend")) {
            holder.tvFriend.setVisibility(View.VISIBLE);
            holder.btnAddFriend.setVisibility(View.INVISIBLE);
        }
        // load image using Glide
        Glide.with(mContext).load(friendList.get(position).getProfilePicURL()).apply(options).into(holder.imageViewFriendSearch);
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout friendSearchRecords_container;
        ImageView imageViewFriendSearch;
        TextView tvFriendSearchName, tvFriend;
        Button btnAddFriend;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvFriendSearchName = (TextView) itemView.findViewById(R.id.tvFriendSearchName);
            tvFriend = (TextView) itemView.findViewById(R.id.tvFriend);
            imageViewFriendSearch = (ImageView) itemView.findViewById(R.id.imageViewFriendSearch);
            friendSearchRecords_container = (LinearLayout)itemView.findViewById(R.id.friendSearchRecords_container);
            btnAddFriend = (Button) itemView.findViewById(R.id.btnAddFriend);
        }
    }


    public void updateFriendRequest(final Context context, String url, final Friend friend) {
        RequestQueue queue = Volley.newRequestQueue(context);

        try{

            if(!progressDialog.isShowing()){
                progressDialog.setMessage("Processing, please wait awhile.");
                progressDialog.show();
            }

            StringRequest postRequest = new StringRequest(
                    Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONObject jsonObject;

                            try{
                                jsonObject = new JSONObject(response);
                                int success = jsonObject.getInt("success");
                                String message = jsonObject.getString("message");

                                if(success == 0){
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();

                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                                } else {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();

                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, "Error. " + error.toString(), Toast.LENGTH_LONG).show();
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                }
            }){
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    // put the parameters with specific values
                    params.put("userEmail", friend.getUserEmail());
                    params.put("friendEmail", friend.getFriendEmail());
                    params.put("confirmation", "true");

                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/x-www-form-urlencoded");
                    return params;
                }
            };

            queue.add(postRequest);

        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
