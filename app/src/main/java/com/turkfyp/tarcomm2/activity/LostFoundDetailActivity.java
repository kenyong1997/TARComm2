package com.turkfyp.tarcomm2.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.turkfyp.tarcomm2.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LostFoundDetailActivity extends AppCompatActivity {

    protected TextView tvDetailLostItemName, tvDetailLostItemDesc, tvDetailLostItemOwner,tvDetailOwnerContact,tvDetailLostDate;
    protected ImageView imageViewLostItem,ivEditLostItem,ivDeleteLostItem;
    protected String ownerContact, itemOwner, lostItemName, lostItemDesc, lostDate,email;
    protected Boolean checkYourUpload;
    protected Bitmap image;
    protected String imageURL,itemCategory;

    int lostID;
    String confirmation;
    RequestOptions options;

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
        ivDeleteLostItem = (ImageView) findViewById(R.id.ivDeleteLostItem);
        ivEditLostItem = (ImageView) findViewById(R.id.ivEditLostItem);


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
            ivEditLostItem.setVisibility(View.VISIBLE);
            ivDeleteLostItem.setVisibility(View.VISIBLE);
        }

        //(WILL BE ADDED IF NEEDED)
        //if the userFullName logged in is the seller of item
//        String checkItemSeller = itemSeller.toUpperCase();
//        String checkLoginUser = MainActivity.LOGGED_IN_USER.toUpperCase();

        //set the text and image by using extras value
        tvDetailLostItemName.setText(lostItemName);
        tvDetailLostItemDesc.setText(lostItemDesc);
        tvDetailLostDate.setText(lostDate);

        //set underline
        SpannableString content = new SpannableString(itemOwner);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tvDetailLostItemOwner.setText(content);
        SpannableString content2 = new SpannableString(ownerContact);
        content2.setSpan(new UnderlineSpan(), 0, content2.length(), 0);
        tvDetailOwnerContact.setText(content2);

        image = extras.getParcelable("LostImage");

        imageURL = extras.getString("LostImageURL");
        lostID = Integer.parseInt(imageURL.split("=")[1]);

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(this);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        //For Glide image
        options = new RequestOptions()
                .fitCenter()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(circularProgressDrawable)
                .error(R.drawable.background_white);

        Glide.with(getApplicationContext()).load(imageURL).apply(options).into(imageViewLostItem);
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
    public void onClickContactOwner(View view) {

        PackageManager packageManager = getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);

        try {
            //If want include message to whatsapp
            //String url = "https://api.whatsapp.com/send?phone=+6"+ ownerContact +"&text=" + URLEncoder.encode(message, "UTF-8");

            String url = "https://api.whatsapp.com/send?phone=+6"+ ownerContact;
            sendIntent.setPackage("com.whatsapp");
            sendIntent.setData(Uri.parse(url));
            if (sendIntent.resolveActivity(packageManager) != null) {
                startActivity(sendIntent);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
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

