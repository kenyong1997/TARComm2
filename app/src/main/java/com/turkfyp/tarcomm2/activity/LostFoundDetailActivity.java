package com.turkfyp.tarcomm2.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.turkfyp.tarcomm2.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LostFoundDetailActivity extends AppCompatActivity {

    protected TextView tvDetailLostItemName, tvDetailLostItemDesc, tvDetailLostItemOwner,tvDetailOwnerContact,tvDetailLostDate;
    protected ImageView imageViewLostItem,ivEditItem,ivDeleteItem;
    protected String ownerContact, itemOwner, lostItemName, lostItemDesc, lostDate,email;
    protected Boolean checkYourUpload;
    protected Bitmap image;
    protected String imageURL,itemCategory;

    int lostID;
    String confirmation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_found_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //No Activity Title
        getSupportActionBar().setTitle(null);


        //Link code to UI
        tvDetailLostItemName = (TextView) findViewById(R.id.tvDetailLostItemName);
        tvDetailLostItemDesc = (TextView) findViewById(R.id.tvDetailLostItemDesc);
        tvDetailLostItemOwner = (TextView) findViewById(R.id.tvDetailLostItemOwner);
        tvDetailOwnerContact = (TextView)findViewById(R.id.tvDetailOwnerContact);
        tvDetailLostDate = (TextView)findViewById(R.id.tvDetailLostDate);
        imageViewLostItem= (ImageView) findViewById(R.id.ivLostItemImage);
        ivDeleteItem = (ImageView) findViewById(R.id.ivDeleteItem);
        ivEditItem = (ImageView) findViewById(R.id.ivEditItem);


        //get the extras and values
        Bundle extras = getIntent().getExtras();
        ownerContact = extras.getString("lostItemContactNo");
        itemOwner=extras.getString("lostItemContactName");
        lostItemName=extras.getString("lostItemName");
        lostItemDesc=extras.getString("lostItemDesc");
        itemCategory=extras.getString("lostCategory");
        lostDate = extras.getString("lostDate");
        checkYourUpload=extras.getBoolean("checkYourUpload");
        email =extras.getString("email");

        if(checkYourUpload){
            ivEditItem.setVisibility(View.VISIBLE);
            ivDeleteItem.setVisibility(View.VISIBLE);
        }

        //(WILL BE ADDED IF NEEDED)
        //if the userFullName logged in is the seller of item
//        String checkItemSeller = itemSeller.toUpperCase();
//        String checkLoginUser = MainActivity.LOGGED_IN_USER.toUpperCase();

        //set the text and image by using extras value
        tvDetailLostItemName.setText(lostItemName);
        tvDetailLostItemOwner.setText(itemOwner);
        tvDetailLostItemDesc.setText(lostItemDesc);
        tvDetailOwnerContact.setText(ownerContact);
        tvDetailLostDate.setText(lostDate);

        image = extras.getParcelable("LostImage");
        imageViewLostItem.setImageBitmap(image);

        imageURL = extras.getString("LostImageURL");
        lostID = Integer.parseInt(imageURL.split("=")[1]);
    }

    public void onDeleteLostFoundItemClicked(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Confirm to delete the item?");
        builder.setCancelable(true);

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();

                confirmation = "true";
                deleteLostFoundRecords(getApplicationContext(),"https://tarcomm.000webhostapp.com/deleteLostFound.php");

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

    public void onBackClicked(View view){
        finish();
    }
    public void onSellerClicked(View view){

        Intent sellerIntent = new Intent(this,ViewOtherProfileActivity.class);
        sellerIntent.putExtra("email",email);

        startActivity(sellerIntent);
    }
    public void onEditLostItemClicked(View view){

        Intent lostFoundDetailIntent = new Intent(this,EditLostItemActivity.class);
        lostFoundDetailIntent.putExtra("lostItemContactNo",ownerContact);
        lostFoundDetailIntent.putExtra("lostItemContactName",itemOwner);
        lostFoundDetailIntent.putExtra("lostItemName",lostItemName);
        lostFoundDetailIntent.putExtra("lostItemDesc",lostItemDesc);
        lostFoundDetailIntent.putExtra("lostDate",lostDate);
        lostFoundDetailIntent.putExtra("itemCategory",itemCategory);
        lostFoundDetailIntent.putExtra("Image", image);
        lostFoundDetailIntent.putExtra("ImageURL", imageURL);

        startActivity(lostFoundDetailIntent);
    }

    public void deleteLostFoundRecords(Context context, String url) {
        RequestQueue queue = Volley.newRequestQueue(context);

        try{
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
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(LostFoundDetailActivity.this,LostAndFoundActivity.class));
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Error. " + error.toString(), Toast.LENGTH_LONG).show();
                }
            }){
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    // put the parameters with specific values
                    params.put("confirmation", confirmation);
                    params.put("lostID", String.valueOf(lostID));

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

