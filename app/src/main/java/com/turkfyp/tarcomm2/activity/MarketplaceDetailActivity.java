package com.turkfyp.tarcomm2.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class MarketplaceDetailActivity extends AppCompatActivity {

    protected TextView tvDetailItemName, tvDetailItemPrice, tvDetailItemDesc, tvDetailItemSeller,tvDetailSellerContact;
    protected ImageView imageViewDetailItem,ivEditItem,ivDeleteItem,ivItemPrice;
    protected LinearLayout llItemPrice;
    protected String sellerContact, itemSeller, itemName, itemDesc, itemPrice, imageURL, itemCategory,email;
    protected Boolean checkYourUpload,checkWTT;
    protected Bitmap image;

    int itemID;
    String confirmation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketplace_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //No Activity Title
        getSupportActionBar().setTitle(null);


        //Link code to UI
        tvDetailItemName = (TextView) findViewById(R.id.tvDetailItemName);
        tvDetailItemPrice = (TextView) findViewById(R.id.tvDetailItemPrice);
        tvDetailItemDesc = (TextView) findViewById(R.id.tvDetailItemDesc);
        tvDetailItemSeller = (TextView) findViewById(R.id.tvDetailItemSeller);
        tvDetailSellerContact = (TextView)findViewById(R.id.tvDetailSellerContact);
        imageViewDetailItem = (ImageView) findViewById(R.id.imageViewDetailItem);
        ivDeleteItem = (ImageView)findViewById(R.id.ivDeleteItem);
        ivEditItem = (ImageView)findViewById(R.id.ivEditItem);
        ivItemPrice = (ImageView) findViewById(R.id.ivItemPrice);
        llItemPrice = (LinearLayout) findViewById(R.id.llItemPrice);



        //get the extras and values
        Bundle extras = getIntent().getExtras();
        itemSeller = extras.getString("itemSeller");
        sellerContact = extras.getString("sellerContact");
        itemName = extras.getString("itemName");
        itemDesc = extras.getString("itemDesc");
        itemPrice = extras.getString("itemPrice");
        checkYourUpload = extras.getBoolean("checkYourUpload");
        checkWTT = extras.getBoolean("checkWTT");
        itemCategory = extras.getString("itemCategory");
        email = extras.getString("email");



        //(WILL BE ADDED IF NEEDED)
        //if the userFullName logged in is the seller of item
//        String checkItemSeller = itemSeller.toUpperCase();
//        String checkLoginUser = MainActivity.LOGGED_IN_USER.toUpperCase();

        //set the text and image by using extras value
        tvDetailItemName.setText(itemName);
        tvDetailItemPrice.setText(String.format("RM %.2f", Double.parseDouble(itemPrice)));
        tvDetailItemSeller.setText(itemSeller);
        tvDetailItemDesc.setText(itemDesc);
        tvDetailSellerContact.setText(sellerContact);

        if(checkYourUpload){
            ivEditItem.setVisibility(View.VISIBLE);
            ivDeleteItem.setVisibility(View.VISIBLE);
        }
        if(checkWTT){
            tvDetailItemPrice.setVisibility(View.GONE);
            ivItemPrice.setVisibility(View.GONE);
            llItemPrice.setVisibility(View.GONE);
        }
        image = extras.getParcelable("Image");
        imageViewDetailItem.setImageBitmap(image);

        imageURL = extras.getString("ImageURL");
        itemID = Integer.parseInt(imageURL.split("=")[1]);


    }

    public void onDeleteItemClicked(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Confirm to delete the item?");
        builder.setCancelable(true);

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();

                confirmation = "true";
                deleteItemRecords(getApplicationContext(),"https://tarcomm.000webhostapp.com/deleteItem.php");

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

    public void onEditItemClicked(View view){

        Intent itemDetailIntent = new Intent(this,EditUploadItemActivity.class);
        itemDetailIntent.putExtra("itemCategory",itemCategory);
        itemDetailIntent.putExtra("itemName",itemName);
        itemDetailIntent.putExtra("itemPrice",itemPrice);
        itemDetailIntent.putExtra("itemDesc",itemDesc);
        itemDetailIntent.putExtra("itemCategory",itemCategory);
        itemDetailIntent.putExtra("Image", image);
        itemDetailIntent.putExtra("ImageURL", imageURL);

        startActivity(itemDetailIntent);
    }

    public void onSellerClicked(View view){

        Intent sellerIntent = new Intent(this,ViewOtherProfileActivity.class);
        sellerIntent.putExtra("email",email);

        startActivity(sellerIntent);
    }
    public void onBackClicked(View view){
        finish();
    }

    public void deleteItemRecords(Context context, String url) {
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
                                    startActivity(new Intent(MarketplaceDetailActivity.this,MarketplaceActivity.class));
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
                    params.put("itemID", String.valueOf(itemID));

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
