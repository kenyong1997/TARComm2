package com.turkfyp.tarcomm2.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
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
    RequestOptions options;

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
        tvDetailItemDesc.setText(itemDesc);


        //set underline
        SpannableString content = new SpannableString(itemSeller);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tvDetailItemSeller.setText(content);
        SpannableString content2 = new SpannableString(sellerContact);
        content2.setSpan(new UnderlineSpan(), 0, content2.length(), 0);
        tvDetailSellerContact.setText(content2);

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

        imageURL = extras.getString("ImageURL");
        itemID = Integer.parseInt(imageURL.split("=")[1]);


        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(this);
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

        Glide.with(getApplicationContext()).load(imageURL).apply(options).into(imageViewDetailItem);
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
    public void onClickSellerContact(View view) {

//        PackageManager pm = getPackageManager();
//
//        try {
//            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
//
//            if (pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES) != null) {
//                Uri uri = Uri.parse("smsto:" + "+6" + sellerContact);
//                Intent sendIntent = new Intent(Intent.ACTION_SENDTO, uri);
//                //sendIntent.putExtra(Intent.EXTRA_TEXT, "Hi,I'm interested in the item you posted on TARoute [ " + itemName +" ]" );
//                sendIntent.setPackage("com.whatsapp");
//                startActivity(Intent.createChooser(sendIntent, ""));
//            }
//
//        } catch (PackageManager.NameNotFoundException e) {
//
//            Toast.makeText(this, "WhatsApp not found", Toast.LENGTH_SHORT)
//                    .show();
//
//            /*
//             //DIRECT USER TO MARKET AND DOWNLOAD WHATSAPPP (FUTURE USE IF NEEDED)
//
//             Uri uri = Uri.parse("market://details?id=com.whatsapp");
//            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
//            startActivity(goToMarket);
//            */
//
//        }

        PackageManager packageManager = getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);

        try {
            //If want include message to whatsapp
            //String url = "https://api.whatsapp.com/send?phone=+6"+ sellerContact +"&text=" + URLEncoder.encode(message, "UTF-8");

            String url = "https://api.whatsapp.com/send?phone=+6"+ sellerContact;
            sendIntent.setPackage("com.whatsapp");
            sendIntent.setData(Uri.parse(url));
            if (sendIntent.resolveActivity(packageManager) != null) {
                startActivity(sendIntent);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

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

        if(checkYourUpload){
            Intent sellerIntent = new Intent(this,ViewProfileActivity.class);
            startActivity(sellerIntent);
        }
        else{
            Intent sellerIntent = new Intent(this,ViewOtherProfileActivity.class);
            sellerIntent.putExtra("email",email);
            startActivity(sellerIntent);
        }
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
