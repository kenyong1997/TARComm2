package com.turkfyp.tarcomm2.DatabaseObjects;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.turkfyp.tarcomm2.R;
import com.turkfyp.tarcomm2.activity.FriendListActivity;
import com.turkfyp.tarcomm2.activity.ViewOtherProfileActivity;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;

public class FriendRVAdapter extends RecyclerView.Adapter<FriendRVAdapter.MyViewHolder> {

    RequestOptions options;
    private Context mContext ;
    private List<Friend> friendList;
    private static String DELETE_URL = "https://tarcomm.000webhostapp.com/deleteFriend.php";
    ProgressDialog progressDialog;

    public FriendRVAdapter(Context mContext, List friendList) {
        this.mContext = mContext;
        this.friendList = friendList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.friend_list_records, parent, false);

        progressDialog = new ProgressDialog(mContext);

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
        viewHolder.ivDeleteFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Confirm to delete friend?");
                builder.setCancelable(true);

                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();

                        updateFriendRequest(mContext, DELETE_URL,friendList.get(viewHolder.getAdapterPosition()));

                        try {
                            Thread.sleep(500);

                            if(progressDialog.isShowing())
                                progressDialog.dismiss();

                            Intent intent = new Intent(mContext,FriendListActivity.class);
                            intent.putExtra("tabnumber",0);
                            mContext.startActivity(intent);
                            ((Activity)mContext).finish();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

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
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tvFriendName.setText(friendList.get(position).getFriendName());

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(mContext);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        //For Glide image
        options = new RequestOptions()
                .centerCrop()
                .signature(new ObjectKey(friendList.get(position).getFriendLastModified()))
                .placeholder(circularProgressDrawable)
                .error(R.drawable.background_white);

        // load image using Glide
        Glide.with(mContext).load(friendList.get(position).getProfilePicURL()).apply(options).into(holder.imageViewFriendList);
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout friendListRecords_container;
        ImageView imageViewFriendList,ivDeleteFriend;
        TextView tvFriendName;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvFriendName = (TextView) itemView.findViewById(R.id.tvFriendName);
            imageViewFriendList = (ImageView) itemView.findViewById(R.id.imageViewFriendList);
            friendListRecords_container = (LinearLayout)itemView.findViewById(R.id.friendListRecords_container);
            ivDeleteFriend = (ImageView) itemView.findViewById(R.id.ivDeleteFriend);
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

                    SharedPreferences preferences = mContext.getSharedPreferences("tarcommUser", MODE_PRIVATE);
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
