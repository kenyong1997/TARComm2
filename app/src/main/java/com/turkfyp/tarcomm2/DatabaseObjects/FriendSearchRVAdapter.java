package com.turkfyp.tarcomm2.DatabaseObjects;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Image;
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

    private static String ADD_URL = "https://tarcomm.000webhostapp.com/sendFriendRequest.php";
    private static String UPDATE_URL = "https://tarcomm.000webhostapp.com/acceptFriendRequest.php";
    private static String DELETE_URL = "https://tarcomm.000webhostapp.com/deleteFriendRequest.php";
    private static String CANCEL_URL = "https://tarcomm.000webhostapp.com/cancelFriendRequest.php";

    RequestOptions options;
    private Context mContext ;
    private List<Friend> friendList;

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

        viewHolder.btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFriendRequest(mContext,ADD_URL,friendList.get(viewHolder.getAdapterPosition()));
            }
        });

        viewHolder.btnConfirmFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFriendRequest(mContext,UPDATE_URL,friendList.get(viewHolder.getAdapterPosition()));
            }
        });

        viewHolder.btnCancelRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Confirm to cancel friend request?");
                builder.setCancelable(true);

                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();

                        updateFriendRequest(mContext,CANCEL_URL,friendList.get(viewHolder.getAdapterPosition()));

                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        viewHolder.ivDeleteRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Confirm to delete friend?");
                builder.setCancelable(true);

                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();

                        updateFriendRequest(mContext,DELETE_URL,friendList.get(viewHolder.getAdapterPosition()));

                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        progressDialog = new ProgressDialog(mContext);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tvFriendSearchName.setText(friendList.get(position).getFriendName());
        // if friend, then set tv to visible
        if (friendList.get(position).getType().equals("a_pending_b")) {
            holder.tvFriend.setVisibility(View.INVISIBLE);
            holder.btnAddFriend.setVisibility(View.INVISIBLE);
            holder.btnCancelRequest.setVisibility(View.VISIBLE);
            holder.btnConfirmFriend.setVisibility(View.INVISIBLE);
            holder.ivDeleteRequest.setVisibility(View.INVISIBLE);
        }
        else if(friendList.get(position).getType().equals("b_pending_a")) {
            holder.tvFriend.setVisibility(View.INVISIBLE);
            holder.btnAddFriend.setVisibility(View.INVISIBLE);
            holder.btnCancelRequest.setVisibility(View.INVISIBLE);
            holder.btnConfirmFriend.setVisibility(View.VISIBLE);
            holder.ivDeleteRequest.setVisibility(View.VISIBLE);
        }
         else if (friendList.get(position).getType().equals("friend")) {
            holder.tvFriend.setVisibility(View.VISIBLE);
            holder.btnAddFriend.setVisibility(View.INVISIBLE);
            holder.btnCancelRequest.setVisibility(View.INVISIBLE);
            holder.btnConfirmFriend.setVisibility(View.INVISIBLE);
            holder.ivDeleteRequest.setVisibility(View.INVISIBLE);
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
        ImageView imageViewFriendSearch,ivDeleteRequest;
        TextView tvFriendSearchName, tvFriend;
        Button btnAddFriend, btnCancelRequest, btnConfirmFriend;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvFriendSearchName = (TextView) itemView.findViewById(R.id.tvFriendSearchName);
            tvFriend = (TextView) itemView.findViewById(R.id.tvFriend);
            imageViewFriendSearch = (ImageView) itemView.findViewById(R.id.imageViewFriendSearch);
            friendSearchRecords_container = (LinearLayout)itemView.findViewById(R.id.friendSearchRecords_container);
            btnAddFriend = (Button) itemView.findViewById(R.id.btnAddFriend);
            btnCancelRequest = (Button) itemView.findViewById(R.id.btnCancelRequest);
            btnConfirmFriend = (Button) itemView.findViewById(R.id.btnConfirmFriend);
            ivDeleteRequest = (ImageView) itemView.findViewById(R.id.ivDeleteRequest);

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

                    SharedPreferences preferences = mContext.getSharedPreferences("tarcommUser", Context.MODE_PRIVATE);
                    String userEmail = preferences.getString("email", "");

                    // put the parameters with specific values
                    params.put("userEmail", userEmail);
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
