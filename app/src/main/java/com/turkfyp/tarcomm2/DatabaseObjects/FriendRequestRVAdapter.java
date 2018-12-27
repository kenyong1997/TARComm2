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
import com.turkfyp.tarcomm2.activity.FriendListActivity;
import com.turkfyp.tarcomm2.activity.LostAndFoundActivity;
import com.turkfyp.tarcomm2.activity.ViewOtherProfileActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class FriendRequestRVAdapter extends RecyclerView.Adapter<FriendRequestRVAdapter.MyViewHolder> {

    RequestOptions options;
    private Context mContext ;
    private List<Friend> friendList;

    Bitmap bitmap;

    ProgressDialog progressDialog;

    public FriendRequestRVAdapter(Context mContext, List friendList) {
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
        view = mInflater.inflate(R.layout.friend_request_records, parent, false);

        //OnClick Listener for RecyclerView
        final FriendRequestRVAdapter.MyViewHolder viewHolder = new FriendRequestRVAdapter.MyViewHolder(view);
        viewHolder.friendRequestRecords_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent i = new Intent(mContext,ViewOtherProfileActivity.class);
                i.putExtra("email",friendList.get(viewHolder.getAdapterPosition()).getFriendEmail());
                mContext.startActivity(i);
            }
        });

        progressDialog = new ProgressDialog(mContext);

        viewHolder.btnAcceptFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    updateFriendRequest(mContext,"https://tarcomm.000webhostapp.com/acceptFriendRequest.php",friendList.get(viewHolder.getAdapterPosition()));
                    Intent intent = new Intent(mContext,FriendListActivity.class);
                    intent.putExtra("tabnumber",0);
                    mContext.startActivity(intent);
                    
                } catch (Exception e){
                    Toast.makeText(mContext , "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        viewHolder.btnRejectFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFriendRequest(mContext,"https://tarcomm.000webhostapp.com/deleteFriendRequest.php",friendList.get(viewHolder.getAdapterPosition()));
                Intent intent = new Intent(mContext,FriendListActivity.class);
                intent.putExtra("tabnumber",0);
                mContext.startActivity(intent);
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tvFriendRequestName.setText(friendList.get(position).getFriendName());

        // load image using Glide
        Glide.with(mContext).load(friendList.get(position).getProfilePicURL()).apply(options).into(holder.imageViewFriendRequest);
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout friendRequestRecords_container;
        ImageView imageViewFriendRequest;
        TextView tvFriendRequestName;
        Button btnAcceptFriend, btnRejectFriend;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvFriendRequestName = (TextView) itemView.findViewById(R.id.tvFriendRequestName);
            imageViewFriendRequest = (ImageView) itemView.findViewById(R.id.imageViewFriendRequest);
            friendRequestRecords_container = (LinearLayout)itemView.findViewById(R.id.friendRequestRecords_container);
            btnAcceptFriend = (Button) itemView.findViewById(R.id.btnAcceptFriend);
            btnRejectFriend = (Button) itemView.findViewById(R.id.btnRejectFriend);
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
